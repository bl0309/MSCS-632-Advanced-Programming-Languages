# Ride Sharing System — OOP in Smalltalk & C++

A class-based Ride Sharing System implemented in both **C++** and **Smalltalk**, demonstrating three core object-oriented programming principles: **encapsulation**, **inheritance**, and **polymorphism**.

Built as a course assignment comparing how OOP concepts are expressed across two languages with very different paradigms.

---

## Table of Contents

- [Project Structure](#project-structure)
- [Class Design](#class-design)
- [OOP Principles Demonstrated](#oop-principles-demonstrated)
- [Getting Started](#getting-started)
  - [C++](#c)
  - [Smalltalk](#smalltalk)
- [Fare Calculation Reference](#fare-calculation-reference)
- [Language Comparison](#language-comparison)

---

## Project Structure

```
ride-sharing-oop/
├── ride_sharing.cpp        # C++ implementation
├── ride_sharing.st         # Smalltalk (GNU Smalltalk) implementation
└── README.md
```

---

## Class Design

```
Ride  (abstract base)
├── StandardRide    — $1.50 base + $1.10/mile
├── PremiumRide     — $5.00 base + $2.75/mile
└── SharedRide      — $1.00 base + $0.75/mile, split among passengers

Driver              — holds a private list of completed rides
Rider               — holds a private list of requested rides
```

---

## OOP Principles Demonstrated

### Encapsulation
Both `Driver` and `Rider` keep their ride collections (`assignedRides` / `requestedRides`) private. External code must use the defined public interface — `addRide()`, `requestRide()`, `viewRides()` — and cannot modify the lists directly.

- **C++**: enforced via `private:` access specifier
- **Smalltalk**: enforced by default — instance variables are always private to their object

### Inheritance
`StandardRide`, `PremiumRide`, and `SharedRide` all inherit from the base `Ride` class, reusing its core attributes (`rideID`, `pickupLocation`, `dropoffLocation`, `distance`) and overriding fare calculation and display behavior.

- **C++**: `class StandardRide : public Ride { ... }`
- **Smalltalk**: `Ride subclass: #StandardRide ...`

### Polymorphism
All three ride types are stored in a single collection. Calling `calculateFare()` / `rideDetails()` on each element dispatches to the correct subclass implementation at runtime — no conditional logic needed.

- **C++**: achieved via `virtual` functions and base-class pointer dispatch
- **Smalltalk**: all methods are dynamically dispatched by default; no special syntax required

---

## Getting Started

### C++

**Requirements:** A C++17-compatible compiler (GCC 7+, Clang 5+, MSVC 2017+)

```bash
# Compile
g++ -std=c++17 -o ride_sharing ride_sharing.cpp

# Run
./ride_sharing
```

On Windows with MSVC:
```cmd
cl /std:c++17 ride_sharing.cpp /Fe:ride_sharing.exe
ride_sharing.exe
```

No external dependencies — uses the standard library only.

### Smalltalk

**Requirements:** [GNU Smalltalk](https://www.gnu.org/software/smalltalk/) (`gst`)

```bash
# Run
gst ride_sharing.st
```

Alternatively, the `.st` file can be loaded into a Pharo or Squeak image with minor syntax adjustments to class definition format.

---

## Fare Calculation Reference

| Ride Type    | Base Fare | Per Mile | Example (10 mi) |
|--------------|-----------|----------|-----------------|
| Standard     | $1.50     | $1.10    | $12.50          |
| Premium      | $5.00     | $2.75    | $32.50          |
| Shared (×2)  | $1.00     | $0.75    | $4.25 each      |
| Shared (×3)  | $1.00     | $0.75    | $2.83 each      |

---

## Language Comparison

| Feature            | C++                                  | Smalltalk                            |
|--------------------|--------------------------------------|--------------------------------------|
| Encapsulation      | `private:` keyword required          | Instance vars private by default     |
| Inheritance        | `class Child : public Parent`        | `Parent subclass: #Child`            |
| Polymorphism       | `virtual` + override through pointer | All methods virtual; message-passing |
| Type checking      | Static (compile-time)                | Dynamic (runtime)                    |
| Verbosity          | Higher (type annotations, specifiers)| Lower (no type declarations)         |

---
