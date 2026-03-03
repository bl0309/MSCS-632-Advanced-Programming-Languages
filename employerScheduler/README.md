# Employee Schedule Manager

Two-language implementation of an employee scheduling system for a company operating 7 days/week with morning, afternoon, and evening shifts.

## Languages
| Language | File |
|----------|------|
| Python 3 | `scheduler.py` |
| Java | `EmployeeScheduler.java` |

---

## Features

| Requirement | Implemented |
|---|---|
| Collect employee names + shift preferences | ✅ (interactive + demo mode) |
| Store in appropriate data structure | ✅ (dict/Map keyed by name → day → preferences) |
| Max 1 shift per employee per day | ✅ |
| Max 5 days per employee per week | ✅ |
| Min 2 employees per shift per day | ✅ (random fill in pass 2) |
| Conflict detection & resolution | ✅ (fallback shift assignment) |
| Readable weekly schedule output | ✅ |
| **Bonus:** Priority-ranked shift preferences | ✅ (e.g. `morning afternoon evening`) |

---

## How to Run

### Python
```bash
python3 scheduler.py
# Choose 'd' for demo mode or 'i' for interactive input
```

### Java
```bash
javac EmployeeScheduler.java
java EmployeeScheduler
# Choose 'd' for demo mode or 'i' for interactive input
```

### Interactive Input Format
When prompted for a day, enter shifts in priority order:
```
Monday: morning afternoon evening   ← works morning, fallback afternoon, then evening
Tuesday:                            ← (press Enter) = unavailable that day
```

---

## Design & Control Structures Used

### Python
- **`if/elif/else`** — input validation, unavailability checks, conflict resolution
- **`for` loops** — iterating days, shifts, employees in two scheduling passes
- **`while True`** — input retry loops
- **`defaultdict`, `dict`, `list`** — data storage
- **`random.choice`** — random understaffing fill
- **Functions** — modular decomposition (`collect_employees`, `assign_shifts`, `print_schedule`)

### Java
- **`if/else`** — validation, availability checks
- **Enhanced `for`** — iteration over days/shifts/employees
- **`while` loops** — input retry and minimum-staffing fill loop
- **`LinkedHashMap`, `HashMap`, `ArrayList`, `HashSet`** — ordered and hashed collections
- **`Random`** — random fill selection
- **Static methods** — modular decomposition matching Python structure
- **`Scanner`** — console input

---

## Scheduling Algorithm

The scheduler runs in **two passes**:

**Pass 1 — Preference Assignment**
Iterates every employee for every day. If the employee is available (non-empty preference list) and hasn't hit their 5-day limit, assigns them to their **highest-priority preferred shift**. If all preferred shifts fail (conflict), falls back to any available shift on the same day.

**Pass 2 — Minimum Staffing**
For every (day, shift) pair that has fewer than 2 employees, randomly selects an eligible employee (not yet working that day, under 5-day limit) until the minimum is met or no eligible employees remain. Understaffed shifts are flagged `⚠ UNDERSTAFFED` in output.

---

## Sample Output (Python — Demo Mode)

```
Employee Scheduler — Python
────────────────────────────────────────
Run in (d)emo mode or (i)nteractive mode? [d/i]: d

[DEMO MODE — using sample employee data]

╔══════════════════════════════════════════════════════════════╗
║               FINAL WEEKLY SCHEDULE                         ║
╚══════════════════════════════════════════════════════════════╝

Day         Morning               Afternoon             Evening
──────────────────────────────────────────────────────────────────────────────
Monday      Alice, David          Carol                 Bob, Eve
Tuesday     Alice, Eve            Carol, Frank          Bob
Wednesday   Bob, Eve              Alice, Carol          David, Frank
Thursday    Alice, David, Frank   Eve                   Bob
Friday      Alice, Eve            Carol, Frank          David
Saturday    Bob, David            Carol                 Frank
Sunday      ⚠ UNDERSTAFFED        ⚠ UNDERSTAFFED        ⚠ UNDERSTAFFED

────────────────────────────────────────
Days worked per employee:
  Alice                5 day(s)
  Bob                  5 day(s)
  Carol                5 day(s)
  David                5 day(s)
  Eve                  5 day(s)
  Frank                5 day(s)
```

> **Note on Sunday:** All 6 demo employees hit their 5-day maximum by Saturday,
> leaving Sunday understaffed. In a real deployment you would add more employees
> or relax the 5-day cap for weekend coverage.

---

## Sample Output (Java — Demo Mode)

```
Employee Scheduler — Java
────────────────────────────────────────
Run in (d)emo mode or (i)nteractive mode? [d/i]: d

[DEMO MODE — using sample employee data]

╔══════════════════════════════════════════════════════════════╗
║               FINAL WEEKLY SCHEDULE                         ║
╚══════════════════════════════════════════════════════════════╝

Day         Morning                 Afternoon               Evening
────────────────────────────────────────────────────────────────────────────────
Monday      Alice, David            Carol                   Bob, Eve
Tuesday     Alice, Eve              Carol, Frank            Bob
Wednesday   Bob, Eve                Alice, Carol            David, Frank
Thursday    Alice, David, Frank     Eve                     Bob
Friday      Alice, Eve              Carol, Frank            David
Saturday    Bob, David              Carol                   Frank
Sunday      ⚠ UNDERSTAFFED          ⚠ UNDERSTAFFED          ⚠ UNDERSTAFFED

────────────────────────────────────────
Days worked per employee:
  Alice                5 day(s)
  Bob                  5 day(s)
  Carol                5 day(s)
  David                5 day(s)
  Eve                  5 day(s)
  Frank                5 day(s)
```

---

## Language Contrast

| Aspect | Python | Java |
|---|---|---|
| Typing | Dynamic | Static |
| Data structures | `dict`, `list`, `set` (built-in) | `LinkedHashMap`, `ArrayList`, `HashSet` |
| Loops | `for x in iterable` | `for (String x : array)` |
| Input | `input()` | `Scanner.nextLine()` |
| Randomness | `random.choice()` | `Random.nextInt()` |
| Entry point | `if __name__ == "__main__"` | `public static void main(String[] args)` |
| Lines of code | ~140 | ~220 |
