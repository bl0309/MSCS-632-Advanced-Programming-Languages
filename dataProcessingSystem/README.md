# Data Processing System

A multi-threaded data processing system implemented in both **Java** and **Go**, demonstrating parallel task processing with shared queues, concurrency-safe result storage, and structured error handling.

---

## Project Structure

```
dataProcessingSystem/
├── Java/
│   ├── DataProcessingSystem.java
│   ├── DataProcessingSystem.class
│   ├── DataProcessingSystem$SharedQueue.class
│   ├── DataProcessingSystem$SharedResults.class
│   ├── DataProcessingSystem$Task.class
│   ├── DataProcessingSystem$Worker.class
│   ├── processing.log
│   └── results.txt
├── Go/
│   ├── main.go
│   ├── processing.log
│   └── results.txt
└── README-4.md
```

---

## Java

### Requirements
- Java 17+

### Run
```bash
cd dataProcessingSystem/Java
javac DataProcessingSystem.java
java DataProcessingSystem
```

### Output
- Console: worker logs and sample results
- `Java/results.txt`: all processed task results
- `Java/processing.log`: full execution log

---

## Go

### Requirements
- Go 1.18+

### Run
```bash
cd dataProcessingSystem/Go
go run main.go
```

### Output
- Console: worker logs and sample results
- `Go/results.txt`: all processed task results
- `Go/processing.log`: full execution log

---

## Features

- **6 concurrent workers** processing 40 tasks in parallel
- **Thread-safe queue** — `LinkedBlockingQueue` (Java) / buffered channel (Go)
- **Shared results store** — `ReentrantLock` (Java) / `sync.Mutex` (Go)
- **Error handling** — ~10% simulated task failures; workers survive and continue
- **Graceful shutdown** — `ExecutorService` (Java) / `WaitGroup` + channel close (Go)
- **Logging** — INFO / WARN / ERROR to console and `processing.log`
