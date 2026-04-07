import java.io.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.*;

/**
 * Data Processing System - Multi-threaded implementation in Java
 *
 * Demonstrates:
 *  - Shared thread-safe task queue (BlockingQueue)
 *  - Worker threads via ExecutorService
 *  - ReentrantLock for results list access
 *  - Comprehensive exception handling
 *  - Java logging (java.util.logging)
 */
public class DataProcessingSystem {

    // -------------------------------------------------------------------------
    // Logger
    // -------------------------------------------------------------------------
    private static final Logger LOGGER = Logger.getLogger(DataProcessingSystem.class.getName());

    static {
        // Configure a simple console + file handler
        try {
            LogManager.getLogManager().reset();
            ConsoleHandler ch = new ConsoleHandler();
            ch.setFormatter(new SimpleFormatter());
            ch.setLevel(Level.ALL);
            LOGGER.addHandler(ch);

            FileHandler fh = new FileHandler("processing.log", true);
            fh.setFormatter(new SimpleFormatter());
            LOGGER.addHandler(fh);
            LOGGER.setLevel(Level.ALL);
        } catch (IOException e) {
            System.err.println("Logger setup failed: " + e.getMessage());
        }
    }

    // -------------------------------------------------------------------------
    // Task definition
    // -------------------------------------------------------------------------
    record Task(int id, String payload) {
        @Override public String toString() { return "Task-" + id + "[" + payload + "]"; }
    }

    // -------------------------------------------------------------------------
    // SharedQueue — wraps BlockingQueue with named add/get semantics
    // -------------------------------------------------------------------------
    static class SharedQueue {
        private final BlockingQueue<Task> queue;

        SharedQueue(int capacity) {
            this.queue = new LinkedBlockingQueue<>(capacity);
        }

        /** Add a task; blocks if queue is full. */
        void addTask(Task task) throws InterruptedException {
            queue.put(task);
            LOGGER.fine("Enqueued " + task);
        }

        /**
         * Retrieve next task; waits up to {@code timeoutMs} ms.
         * Returns {@code null} on timeout (signals "no more work").
         */
        Task getTask(long timeoutMs) throws InterruptedException {
            return queue.poll(timeoutMs, TimeUnit.MILLISECONDS);
        }

        int size() { return queue.size(); }
        boolean isEmpty() { return queue.isEmpty(); }
    }

    // -------------------------------------------------------------------------
    // SharedResults — thread-safe results list + file writer
    // -------------------------------------------------------------------------
    static class SharedResults {
        private final List<String> results = new ArrayList<>();
        private final java.util.concurrent.locks.ReentrantLock lock =
                new java.util.concurrent.locks.ReentrantLock();
        private final String outputFile;

        SharedResults(String outputFile) {
            this.outputFile = outputFile;
        }

        void add(String result) {
            lock.lock();
            try {
                results.add(result);
                LOGGER.fine("Result saved: " + result);
            } finally {
                lock.unlock();
            }
        }

        /** Flush all in-memory results to a file. */
        void flushToFile() throws IOException {
            lock.lock();
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(outputFile))) {
                bw.write("=== Data Processing Results ===");
                bw.newLine();
                for (String r : results) {
                    bw.write(r);
                    bw.newLine();
                }
                bw.write("=== Total results: " + results.size() + " ===");
                bw.newLine();
                LOGGER.info("Flushed " + results.size() + " results to " + outputFile);
            } finally {
                lock.unlock();
            }
        }

        List<String> snapshot() {
            lock.lock();
            try {
                return new ArrayList<>(results);
            } finally {
                lock.unlock();
            }
        }
    }

    // -------------------------------------------------------------------------
    // Worker — Runnable that pulls tasks from the queue and processes them
    // -------------------------------------------------------------------------
    static class Worker implements Runnable {
        private final int workerId;
        private final SharedQueue queue;
        private final SharedResults results;
        private final AtomicInteger completedCount;
        private static final long POLL_TIMEOUT_MS = 500;

        Worker(int id, SharedQueue q, SharedResults r, AtomicInteger counter) {
            this.workerId = id;
            this.queue = q;
            this.results = r;
            this.completedCount = counter;
        }

        @Override
        public void run() {
            String workerName = "Worker-" + workerId;
            LOGGER.info(workerName + " started.");

            while (true) {
                Task task = null;
                try {
                    // Attempt to get a task within the timeout window
                    task = queue.getTask(POLL_TIMEOUT_MS);

                    if (task == null) {
                        // Timeout means queue is empty and likely done
                        LOGGER.fine(workerName + " found no task; exiting.");
                        break;
                    }

                    LOGGER.info(workerName + " picked up " + task);
                    String result = process(task, workerName);
                    results.add(result);
                    completedCount.incrementAndGet();

                } catch (InterruptedException e) {
                    LOGGER.warning(workerName + " was interrupted. Stopping.");
                    Thread.currentThread().interrupt();
                    break;
                } catch (RuntimeException e) {
                    // Catch processing errors so the worker survives and moves on
                    String failed = (task != null) ? task.toString() : "unknown";
                    LOGGER.severe(workerName + " error processing " + failed + ": " + e.getMessage());
                }
            }

            LOGGER.info(workerName + " finished.");
        }

        /** Simulate computation: transform payload and sleep to mimic real work. */
        private String process(Task task, String workerName) throws InterruptedException {
            // Simulate variable processing time (50–250 ms)
            long delay = 50 + (long)(Math.random() * 200);
            Thread.sleep(delay);

            // Produce a richer result string
            String transformed = task.payload().toUpperCase()
                    .replace(" ", "_")
                    + "_PROCESSED";

            // Occasionally inject a simulated processing error (~10% chance)
            if (Math.random() < 0.10) {
                throw new RuntimeException("Simulated processing failure for " + task);
            }

            return String.format("[%s] %s -> %s (took %dms)",
                    workerName, task, transformed, delay);
        }
    }

    // -------------------------------------------------------------------------
    // TaskGenerator — produces a richer variety of tasks
    // -------------------------------------------------------------------------
    static List<Task> generateTasks(int count) {
        String[] categories = {"alpha", "beta", "gamma", "delta", "epsilon"};
        List<Task> tasks = new ArrayList<>();
        Random rng = new Random(42);
        for (int i = 1; i <= count; i++) {
            String cat = categories[rng.nextInt(categories.length)];
            tasks.add(new Task(i, cat + " data packet " + i));
        }
        return tasks;
    }

    // -------------------------------------------------------------------------
    // Main
    // -------------------------------------------------------------------------
    public static void main(String[] args) {
        LOGGER.info("=== Data Processing System Starting ===");

        final int TASK_COUNT   = 40;
        final int WORKER_COUNT = 6;
        final String OUTPUT    = "results.txt";

        SharedQueue   queue   = new SharedQueue(TASK_COUNT);
        SharedResults results = new SharedResults(OUTPUT);
        AtomicInteger done    = new AtomicInteger(0);

        // -- Enqueue tasks ---------------------------------------------------
        List<Task> tasks = generateTasks(TASK_COUNT);
        LOGGER.info("Generated " + tasks.size() + " tasks.");
        for (Task t : tasks) {
            try {
                queue.addTask(t);
            } catch (InterruptedException e) {
                LOGGER.warning("Interrupted while enqueuing: " + e.getMessage());
                Thread.currentThread().interrupt();
            }
        }
        LOGGER.info("All tasks enqueued. Queue size: " + queue.size());

        // -- Launch workers via ExecutorService ------------------------------
        ExecutorService pool = Executors.newFixedThreadPool(WORKER_COUNT);
        for (int i = 1; i <= WORKER_COUNT; i++) {
            pool.submit(new Worker(i, queue, results, done));
        }

        // -- Shut down the pool and wait for completion ----------------------
        pool.shutdown();
        try {
            boolean finished = pool.awaitTermination(60, TimeUnit.SECONDS);
            if (!finished) {
                LOGGER.warning("Timeout reached; forcing shutdown.");
                pool.shutdownNow();
            }
        } catch (InterruptedException e) {
            LOGGER.severe("Main thread interrupted: " + e.getMessage());
            pool.shutdownNow();
            Thread.currentThread().interrupt();
        }

        // -- Flush results to file -------------------------------------------
        try {
            results.flushToFile();
        } catch (IOException e) {
            LOGGER.severe("Failed to write results file: " + e.getMessage());
        }

        // -- Summary ---------------------------------------------------------
        LOGGER.info(String.format(
            "=== Processing complete. Tasks submitted: %d | Results recorded: %d ===",
            TASK_COUNT, done.get()
        ));

        System.out.println("\n--- Sample Results (first 10) ---");
        results.snapshot().stream().limit(10).forEach(System.out::println);
        System.out.println("(Full results written to " + OUTPUT + ")");
    }
}
