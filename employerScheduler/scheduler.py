"""
Employee Schedule Manager - Python Implementation
Manages employee shifts across 7 days with morning/afternoon/evening options.
Supports priority-ranked preferences (Bonus feature).
"""

import random
from collections import defaultdict

# ─────────────────────────────────────────────
#  Constants
# ─────────────────────────────────────────────
DAYS   = ["Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"]
SHIFTS = ["morning", "afternoon", "evening"]
MAX_DAYS_PER_WEEK  = 5
MIN_EMPLOYEES_PER_SHIFT = 2

# ─────────────────────────────────────────────
#  Data Structures
# ─────────────────────────────────────────────
# employees: { name: { day: [shift_pref1, shift_pref2, shift_pref3] } }
# schedule:  { day:  { shift: [name, ...] } }

def build_empty_schedule():
    return {day: {shift: [] for shift in SHIFTS} for day in DAYS}

# ─────────────────────────────────────────────
#  Input Collection
# ─────────────────────────────────────────────
def collect_employees():
    """
    Collect employee names and their priority-ranked shift preferences for each day.
    Returns a dict: { name: { day: [pref1, pref2, pref3] } }
    """
    employees = {}
    print("\n╔══════════════════════════════════════╗")
    print("║    Employee Schedule Input System    ║")
    print("╚══════════════════════════════════════╝")

    while True:
        name = input("\nEnter employee name (or 'done' to finish): ").strip()
        if name.lower() == "done":
            if len(employees) < 2:
                print("  ⚠  Please enter at least 2 employees.")
                continue
            break
        if not name:
            continue
        if name in employees:
            print(f"  ⚠  {name} already added.")
            continue

        employees[name] = {}
        print(f"\n  Scheduling preferences for {name}:")
        print(f"  Shifts available: morning, afternoon, evening")
        print(f"  Enter preferences as a ranked list, e.g.:  morning afternoon evening")
        print(f"  (Press ENTER to mark employee as unavailable for that day)\n")

        for day in DAYS:
            while True:
                raw = input(f"    {day}: ").strip().lower()
                if raw == "":
                    # Unavailable this day
                    employees[name][day] = []
                    break
                prefs = raw.split()
                valid = all(p in SHIFTS for p in prefs)
                unique = len(prefs) == len(set(prefs))
                if valid and unique and 1 <= len(prefs) <= 3:
                    # Fill in any missing shifts at the end (lowest priority)
                    remaining = [s for s in SHIFTS if s not in prefs]
                    random.shuffle(remaining)
                    employees[name][day] = prefs + remaining
                    break
                print(f"      ⚠  Enter 1-3 unique shifts from: morning, afternoon, evening")

    return employees


# ─────────────────────────────────────────────
#  Scheduling Logic
# ─────────────────────────────────────────────
def assign_shifts(employees):
    """
    Build the weekly schedule respecting:
      - max 5 days/week per employee
      - max 1 shift/day per employee
      - honor priority-ranked preferences
      - detect and resolve conflicts
      - ensure at least 2 employees per shift per day
    """
    schedule   = build_empty_schedule()
    days_worked = defaultdict(int)   # name -> count
    assigned    = defaultdict(set)   # name -> set of days already assigned

    # ── Pass 1: Assign based on priority preferences ──
    for day in DAYS:
        for name, prefs_by_day in employees.items():
            prefs = prefs_by_day.get(day, [])
            if not prefs:
                continue  # employee unavailable
            if days_worked[name] >= MAX_DAYS_PER_WEEK:
                continue  # hit weekly limit
            if day in assigned[name]:
                continue  # already placed today (shouldn't happen, safety check)

            placed = False
            for shift in prefs:
                # No hard cap per shift from preferences — just assign
                schedule[day][shift].append(name)
                assigned[name].add(day)
                days_worked[name] += 1
                placed = True
                break

            if not placed:
                # All preferred shifts tried — try any shift (conflict resolution)
                for shift in SHIFTS:
                    if day not in assigned[name] and days_worked[name] < MAX_DAYS_PER_WEEK:
                        schedule[day][shift].append(name)
                        assigned[name].add(day)
                        days_worked[name] += 1
                        break

    # ── Pass 2: Ensure minimum 2 employees per shift per day ──
    for day in DAYS:
        for shift in SHIFTS:
            while len(schedule[day][shift]) < MIN_EMPLOYEES_PER_SHIFT:
                # Find eligible employees: not yet working today, under 5-day limit
                eligible = [
                    name for name in employees
                    if day not in assigned[name] and days_worked[name] < MAX_DAYS_PER_WEEK
                ]
                if not eligible:
                    break  # No one left to assign — note in output
                pick = random.choice(eligible)
                schedule[day][shift].append(pick)
                assigned[pick].add(day)
                days_worked[pick] += 1

    return schedule, days_worked


# ─────────────────────────────────────────────
#  Output
# ─────────────────────────────────────────────
def print_schedule(schedule, days_worked):
    print("\n\n╔══════════════════════════════════════════════════════════════╗")
    print("║               FINAL WEEKLY SCHEDULE                         ║")
    print("╚══════════════════════════════════════════════════════════════╝\n")

    col_w = 22
    header = f"{'Day':<12}" + "".join(f"{s.capitalize():<{col_w}}" for s in SHIFTS)
    print(header)
    print("─" * (12 + col_w * 3))

    for day in DAYS:
        row = f"{day:<12}"
        for shift in SHIFTS:
            names = schedule[day][shift]
            if names:
                cell = ", ".join(names)
            else:
                cell = "⚠ UNDERSTAFFED"
            row += f"{cell:<{col_w}}"
        print(row)

    print("\n" + "─" * 40)
    print("Days worked per employee:")
    for name, count in sorted(days_worked.items()):
        print(f"  {name:<20} {count} day(s)")
    print()


# ─────────────────────────────────────────────
#  Demo Mode (no interactive input)
# ─────────────────────────────────────────────
def demo_mode():
    """Run with pre-loaded sample data for quick demonstration."""
    print("\n[DEMO MODE — using sample employee data]\n")

    sample_employees = {
        "Alice": {
            "Monday":    ["morning", "afternoon", "evening"],
            "Tuesday":   ["morning", "afternoon", "evening"],
            "Wednesday": ["afternoon", "morning", "evening"],
            "Thursday":  ["morning", "afternoon", "evening"],
            "Friday":    ["morning", "afternoon", "evening"],
            "Saturday":  [],
            "Sunday":    [],
        },
        "Bob": {
            "Monday":    ["evening", "afternoon", "morning"],
            "Tuesday":   ["evening", "morning", "afternoon"],
            "Wednesday": ["morning", "evening", "afternoon"],
            "Thursday":  ["evening", "afternoon", "morning"],
            "Friday":    [],
            "Saturday":  ["morning", "afternoon", "evening"],
            "Sunday":    ["morning", "afternoon", "evening"],
        },
        "Carol": {
            "Monday":    ["afternoon", "morning", "evening"],
            "Tuesday":   ["afternoon", "evening", "morning"],
            "Wednesday": ["afternoon", "morning", "evening"],
            "Thursday":  [],
            "Friday":    ["afternoon", "morning", "evening"],
            "Saturday":  ["afternoon", "morning", "evening"],
            "Sunday":    ["evening", "morning", "afternoon"],
        },
        "David": {
            "Monday":    ["morning", "evening", "afternoon"],
            "Tuesday":   [],
            "Wednesday": ["evening", "morning", "afternoon"],
            "Thursday":  ["morning", "afternoon", "evening"],
            "Friday":    ["evening", "morning", "afternoon"],
            "Saturday":  ["morning", "evening", "afternoon"],
            "Sunday":    ["morning", "afternoon", "evening"],
        },
        "Eve": {
            "Monday":    ["evening", "morning", "afternoon"],
            "Tuesday":   ["morning", "evening", "afternoon"],
            "Wednesday": ["morning", "afternoon", "evening"],
            "Thursday":  ["afternoon", "morning", "evening"],
            "Friday":    ["morning", "evening", "afternoon"],
            "Saturday":  [],
            "Sunday":    ["afternoon", "evening", "morning"],
        },
        "Frank": {
            "Monday":    [],
            "Tuesday":   ["afternoon", "morning", "evening"],
            "Wednesday": ["evening", "afternoon", "morning"],
            "Thursday":  ["morning", "evening", "afternoon"],
            "Friday":    ["afternoon", "evening", "morning"],
            "Saturday":  ["evening", "morning", "afternoon"],
            "Sunday":    ["morning", "evening", "afternoon"],
        },
    }
    return sample_employees


# ─────────────────────────────────────────────
#  Main
# ─────────────────────────────────────────────
def main():
    print("Employee Scheduler — Python")
    print("─" * 40)
    mode = input("Run in (d)emo mode or (i)nteractive mode? [d/i]: ").strip().lower()

    if mode == "i":
        employees = collect_employees()
    else:
        employees = demo_mode()

    schedule, days_worked = assign_shifts(employees)
    print_schedule(schedule, days_worked)


if __name__ == "__main__":
    main()
