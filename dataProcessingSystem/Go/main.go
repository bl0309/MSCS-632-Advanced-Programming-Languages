// Data Processing System — Go implementation
//
// Demonstrates:
//   - Goroutines as worker processes
//   - Buffered channels as a concurrency-safe task queue
//   - sync.Mutex protecting the shared results slice
//   - sync.WaitGroup for worker lifecycle management
//   - defer for guaranteed resource cleanup
//   - Idiomatic Go error returns throughout

package main

import (
	"fmt"
	"log"
	"math/rand"
	"os"
	"strings"
	"sync"
	"sync/atomic"
	"time"
)

// -----------------------------------------------------------------------------
// Logging helpers
// -----------------------------------------------------------------------------

var (
	infoLog  = log.New(os.Stdout, "INFO  ", log.Ltime|log.Lmicroseconds)
	warnLog  = log.New(os.Stdout, "WARN  ", log.Ltime|log.Lmicroseconds)
	errorLog = log.New(os.Stderr, "ERROR ", log.Ltime|log.Lmicroseconds)
)

// setupFileLogger adds a file sink to all loggers (returns closer).
func setupFileLogger(path string) (func(), error) {
	f, err := os.OpenFile(path, os.O_CREATE|os.O_WRONLY|os.O_APPEND, 0644)
	if err != nil {
		return nil, fmt.Errorf("open log file %q: %w", path, err)
	}

	// Multi-writer: stdout + file
	mw := io_MultiWriter(os.Stdout, f)
	infoLog.SetOutput(mw)
	warnLog.SetOutput(mw)

	mwErr := io_MultiWriter(os.Stderr, f)
	errorLog.SetOutput(mwErr)

	closer := func() {
		if err := f.Close(); err != nil {
			fmt.Fprintf(os.Stderr, "log file close error: %v\n", err)
		}
	}
	return closer, nil
}

// io_MultiWriter is a local re-export so we don't import "io" just for this.
func io_MultiWriter(writers ...interface{ Write([]byte) (int, error) }) *multiWriter {
	return &multiWriter{writers: writers}
}

type multiWriter struct {
	writers []interface{ Write([]byte) (int, error) }
}

func (mw *multiWriter) Write(p []byte) (int, error) {
	for _, w := range mw.writers {
		if _, err := w.Write(p); err != nil {
			return 0, err
		}
	}
	return len(p), nil
}

// -----------------------------------------------------------------------------
// Task
// -----------------------------------------------------------------------------

// Task represents a unit of work.
type Task struct {
	ID      int
	Payload string
}

func (t Task) String() string {
	return fmt.Sprintf("Task-%d[%s]", t.ID, t.Payload)
}

// -----------------------------------------------------------------------------
// SharedResults — mutex-protected in-memory store + file flusher
// -----------------------------------------------------------------------------

// SharedResults stores processed results with safe concurrent access.
type SharedResults struct {
	mu      sync.Mutex
	entries []string
}

// Add safely appends a result entry.
func (sr *SharedResults) Add(entry string) {
	sr.mu.Lock()
	defer sr.mu.Unlock()
	sr.entries = append(sr.entries, entry)
}

// Len returns the current count (safe).
func (sr *SharedResults) Len() int {
	sr.mu.Lock()
	defer sr.mu.Unlock()
	return len(sr.entries)
}

// Snapshot returns a copy of all entries.
func (sr *SharedResults) Snapshot() []string {
	sr.mu.Lock()
	defer sr.mu.Unlock()
	cp := make([]string, len(sr.entries))
	copy(cp, sr.entries)
	return cp
}

// FlushToFile writes every result entry to path (overwrites).
func (sr *SharedResults) FlushToFile(path string) error {
	sr.mu.Lock()
	defer sr.mu.Unlock()

	f, err := os.Create(path)
	if err != nil {
		return fmt.Errorf("create results file %q: %w", path, err)
	}
	defer func() {
		if cerr := f.Close(); cerr != nil {
			errorLog.Printf("close results file: %v", cerr)
		}
	}()

	if _, err = fmt.Fprintln(f, "=== Data Processing Results ==="); err != nil {
		return fmt.Errorf("write header: %w", err)
	}
	for _, e := range sr.entries {
		if _, err = fmt.Fprintln(f, e); err != nil {
			return fmt.Errorf("write entry: %w", err)
		}
	}
	_, err = fmt.Fprintf(f, "=== Total results: %d ===\n", len(sr.entries))
	return err
}

// -----------------------------------------------------------------------------
// processTask — pure function; returns (result, error)
// -----------------------------------------------------------------------------

// processTask simulates computational work and returns a result string.
func processTask(t Task, workerID int) (string, error) {
	// Simulate variable work (50–250 ms)
	delay := time.Duration(50+rand.Intn(200)) * time.Millisecond
	time.Sleep(delay)

	// ~10% chance of a simulated failure
	if rand.Float64() < 0.10 {
		return "", fmt.Errorf("simulated failure for %s", t)
	}

	transformed := strings.ToUpper(strings.ReplaceAll(t.Payload, " ", "_")) + "_PROCESSED"
	result := fmt.Sprintf("[Worker-%d] %s -> %s (took %dms)",
		workerID, t, transformed, delay.Milliseconds())
	return result, nil
}

// -----------------------------------------------------------------------------
// worker — goroutine body
// -----------------------------------------------------------------------------

// worker reads from taskCh until closed, processes each Task, and stores results.
func worker(id int, taskCh <-chan Task, results *SharedResults, completed *int64, wg *sync.WaitGroup) {
	defer wg.Done()
	infoLog.Printf("Worker-%d started", id)

	for task := range taskCh {
		infoLog.Printf("Worker-%d picked up %s", id, task)

		result, err := processTask(task, id)
		if err != nil {
			errorLog.Printf("Worker-%d error processing %s: %v", id, task, err)
			continue // skip storing a failed result; worker stays alive
		}

		results.Add(result)
		atomic.AddInt64(completed, 1)
	}

	infoLog.Printf("Worker-%d finished", id)
}

// -----------------------------------------------------------------------------
// generateTasks — produces a variety of test tasks
// -----------------------------------------------------------------------------

func generateTasks(count int) []Task {
	categories := []string{"alpha", "beta", "gamma", "delta", "epsilon"}
	rng := rand.New(rand.NewSource(42))
	tasks := make([]Task, count)
	for i := range tasks {
		cat := categories[rng.Intn(len(categories))]
		tasks[i] = Task{
			ID:      i + 1,
			Payload: fmt.Sprintf("%s data packet %d", cat, i+1),
		}
	}
	return tasks
}

// -----------------------------------------------------------------------------
// main
// -----------------------------------------------------------------------------

func main() {
	// --- Setup logging to file + stdout ------------------------------------
	logClose, err := setupFileLogger("processing.log")
	if err != nil {
		fmt.Fprintf(os.Stderr, "WARNING: file logger unavailable: %v\n", err)
	} else {
		defer logClose()
	}

	infoLog.Println("=== Data Processing System Starting ===")

	const (
		taskCount   = 40
		workerCount = 6
		outputFile  = "results.txt"
	)

	// --- Create buffered channel as the task queue -------------------------
	// Buffered to taskCount so producers never block during enqueue.
	taskCh := make(chan Task, taskCount)

	var (
		results   SharedResults
		completed int64
		wg        sync.WaitGroup
	)

	// --- Launch workers (goroutines) before filling the queue ---------------
	for i := 1; i <= workerCount; i++ {
		wg.Add(1)
		go worker(i, taskCh, &results, &completed, &wg)
	}

	// --- Enqueue tasks -------------------------------------------------------
	tasks := generateTasks(taskCount)
	infoLog.Printf("Generated %d tasks", taskCount)
	for _, t := range tasks {
		taskCh <- t // non-blocking because buffer >= taskCount
		infoLog.Printf("Enqueued %s", t)
	}
	close(taskCh) // signal workers: no more tasks coming
	infoLog.Printf("All %d tasks enqueued; channel closed", taskCount)

	// --- Wait for all workers to finish -------------------------------------
	wg.Wait()

	// --- Flush results to file ----------------------------------------------
	if err := results.FlushToFile(outputFile); err != nil {
		errorLog.Printf("Failed to write results: %v", err)
	} else {
		infoLog.Printf("Results written to %s", outputFile)
	}

	// --- Summary ------------------------------------------------------------
	infoLog.Printf("=== Processing complete. Tasks submitted: %d | Results recorded: %d ===",
		taskCount, atomic.LoadInt64(&completed))

	fmt.Println("\n--- Sample Results (first 10) ---")
	for i, r := range results.Snapshot() {
		if i >= 10 {
			break
		}
		fmt.Println(r)
	}
	fmt.Printf("(Full results written to %s)\n", outputFile)
}
