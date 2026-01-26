# Syntax, Semantics, and Memory Management Across Programming Languages

## Course
Advanced Programming Languages (MSCS-632-M20)

## Author
Bikram Lamsal

---

## Overview
This repository contains source code and analysis artifacts for an assignment focused on understanding **syntax**, **semantics**, and **memory management** across multiple programming languages. The goal of this assignment is to compare how different languages handle errors, typing, scope, closures, and memory at both compile time and runtime.

The languages used in this assignment include:
- Python
- JavaScript
- C++
- Rust
- Java

Each section of the repository corresponds directly to a part of the assignment instructions.

---

## Repository Structure
```
Syntax-Semantics-Memory-Management/
├── part1/
│ ├── python/
│ │ ├── python_syntax_error.py
│ │ ├── python_working.py
│ │ └── python_semantics.py
│ ├── javascript/
│ │ ├── javascript_syntax_error.js
│ │ ├── javascript_working.js
│ │ └── javascript_semantics.js
│ └── cpp/
│ ├── cpp_syntax_error.cpp
│ ├── cpp_working.cpp
│ └── cpp_semantics.cpp
├── part2/
│ ├── rust_memory.rs
│ ├── JavaMemory.java
│ └── cpp_memory.cpp 
└── README.md
```


---

## Part 1: Syntax and Semantics

### Section 1: Syntax Errors
Each language folder in `part1` contains:
- A **syntax error version** of a program
- A **correct working version**
- Error output used for analysis and screenshots

These examples demonstrate how interpreters and compilers detect, report, and halt execution due to syntax errors.

### Section 2: Semantics
Semantic examples focus on:
- Type systems (dynamic vs static)
- Scope and closures
- Function behavior and variable binding

Each program highlights how semantic rules differ across languages and how these differences affect program behavior and safety.

---

## Part 2: Memory Management

### Rust
- Demonstrates ownership and borrowing
- Memory safety enforced at compile time
- No garbage collector

### Java
- Demonstrates automatic garbage collection
- Memory freed by the JVM
- Explicit deallocation not required

### C++
- Demonstrates manual memory allocation and deallocation
- Uses `new` and `delete`
- Highlights risks such as memory leaks if memory is not freed

Memory profiling tools such as **Valgrind** (C++) and JVM garbage collection logs (Java) were used to observe memory behavior.

---

## How to Run the Programs

### Python
```bash
python3 filename.py
```

### JavaScript
```bash
node filename.js
```

### C++
```bash
g++ filename.cpp -o program
./program
```

### Java
```bash
javac JavaMemory.java
java JavaMemory
```

###  Rust
```bash
rustc rust_memory.rs
./rust_memory
```
