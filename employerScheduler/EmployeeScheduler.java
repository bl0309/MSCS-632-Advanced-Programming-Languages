import java.util.*;

/**
 * EmployeeScheduler.java
 * Employee Schedule Manager — Java Implementation
 *
 * Manages employee shifts across 7 days (morning / afternoon / evening).
 * Supports priority-ranked preferences (Bonus feature).
 *
 * Compile:  javac EmployeeScheduler.java
 * Run:      java EmployeeScheduler
 */
public class EmployeeScheduler {

    // ─────────────────────────────────────────
    //  Constants
    // ─────────────────────────────────────────
    static final String[] DAYS   = {"Monday","Tuesday","Wednesday","Thursday","Friday","Saturday","Sunday"};
    static final String[] SHIFTS = {"morning","afternoon","evening"};
    static final int MAX_DAYS          = 5;
    static final int MIN_PER_SHIFT     = 2;

    // ─────────────────────────────────────────
    //  Data model
    // ─────────────────────────────────────────
    /**
     * employees : name → (day → ordered list of preferred shifts)
     * schedule  : day  → (shift → list of assigned employees)
     * daysWorked: name → count
     * assigned  : name → set of days already assigned
     */
    static Map<String, Map<String, List<String>>> employees  = new LinkedHashMap<>();
    static Map<String, Map<String, List<String>>> schedule   = new LinkedHashMap<>();
    static Map<String, Integer>                   daysWorked = new HashMap<>();
    static Map<String, Set<String>>               assigned   = new HashMap<>();

    static Random rng = new Random();

    // ─────────────────────────────────────────
    //  Initialise empty schedule
    // ─────────────────────────────────────────
    static void initSchedule() {
        for (String day : DAYS) {
            Map<String, List<String>> dayMap = new LinkedHashMap<>();
            for (String shift : SHIFTS) {
                dayMap.put(shift, new ArrayList<>());
            }
            schedule.put(day, dayMap);
        }
    }

    // ─────────────────────────────────────────
    //  Demo data
    // ─────────────────────────────────────────
    static void loadDemoData() {
        System.out.println("\n[DEMO MODE — using sample employee data]\n");

        // helper lambda stored as a Runnable isn't ideal — use a private method style
        addEmployee("Alice",
            new String[]{"morning","afternoon","evening"},   // Mon
            new String[]{"morning","afternoon","evening"},   // Tue
            new String[]{"afternoon","morning","evening"},   // Wed
            new String[]{"morning","afternoon","evening"},   // Thu
            new String[]{"morning","afternoon","evening"},   // Fri
            new String[]{},                                  // Sat
            new String[]{}                                   // Sun
        );
        addEmployee("Bob",
            new String[]{"evening","afternoon","morning"},
            new String[]{"evening","morning","afternoon"},
            new String[]{"morning","evening","afternoon"},
            new String[]{"evening","afternoon","morning"},
            new String[]{},
            new String[]{"morning","afternoon","evening"},
            new String[]{"morning","afternoon","evening"}
        );
        addEmployee("Carol",
            new String[]{"afternoon","morning","evening"},
            new String[]{"afternoon","evening","morning"},
            new String[]{"afternoon","morning","evening"},
            new String[]{},
            new String[]{"afternoon","morning","evening"},
            new String[]{"afternoon","morning","evening"},
            new String[]{"evening","morning","afternoon"}
        );
        addEmployee("David",
            new String[]{"morning","evening","afternoon"},
            new String[]{},
            new String[]{"evening","morning","afternoon"},
            new String[]{"morning","afternoon","evening"},
            new String[]{"evening","morning","afternoon"},
            new String[]{"morning","evening","afternoon"},
            new String[]{"morning","afternoon","evening"}
        );
        addEmployee("Eve",
            new String[]{"evening","morning","afternoon"},
            new String[]{"morning","evening","afternoon"},
            new String[]{"morning","afternoon","evening"},
            new String[]{"afternoon","morning","evening"},
            new String[]{"morning","evening","afternoon"},
            new String[]{},
            new String[]{"afternoon","evening","morning"}
        );
        addEmployee("Frank",
            new String[]{},
            new String[]{"afternoon","morning","evening"},
            new String[]{"evening","afternoon","morning"},
            new String[]{"morning","evening","afternoon"},
            new String[]{"afternoon","evening","morning"},
            new String[]{"evening","morning","afternoon"},
            new String[]{"morning","evening","afternoon"}
        );
    }

    /** Convenience helper: register one employee's preferences for all 7 days. */
    static void addEmployee(String name, String[]... dayPrefs) {
        Map<String, List<String>> prefMap = new LinkedHashMap<>();
        for (int i = 0; i < DAYS.length; i++) {
            List<String> prefs = new ArrayList<>(Arrays.asList(dayPrefs[i]));
            // Fill in missing shifts at lowest priority
            for (String s : SHIFTS) {
                if (!prefs.contains(s)) prefs.add(s);
            }
            // If originally empty, keep empty (employee unavailable)
            if (dayPrefs[i].length == 0) prefs.clear();
            prefMap.put(DAYS[i], prefs);
        }
        employees.put(name, prefMap);
        daysWorked.put(name, 0);
        assigned.put(name, new HashSet<>());
    }

    // ─────────────────────────────────────────
    //  Interactive input
    // ─────────────────────────────────────────
    static void collectInteractive() {
        Scanner sc = new Scanner(System.in);
        Set<String> shiftSet = new HashSet<>(Arrays.asList(SHIFTS));

        System.out.println("\n╔══════════════════════════════════════╗");
        System.out.println("║    Employee Schedule Input System    ║");
        System.out.println("╚══════════════════════════════════════╝");

        while (true) {
            System.out.print("\nEnter employee name (or 'done' to finish): ");
            String name = sc.nextLine().trim();
            if (name.equalsIgnoreCase("done")) {
                if (employees.size() < 2) { System.out.println("  ⚠  Please enter at least 2 employees."); continue; }
                break;
            }
            if (name.isEmpty() || employees.containsKey(name)) { System.out.println("  ⚠  Invalid or duplicate name."); continue; }

            Map<String, List<String>> prefMap = new LinkedHashMap<>();
            System.out.println("  Shifts: morning  afternoon  evening");
            System.out.println("  Enter ranked preferences per day (e.g. 'morning afternoon evening')");
            System.out.println("  Press ENTER to mark as unavailable.\n");

            for (String day : DAYS) {
                while (true) {
                    System.out.printf("    %s: ", day);
                    String line = sc.nextLine().trim().toLowerCase();
                    if (line.isEmpty()) { prefMap.put(day, new ArrayList<>()); break; }
                    String[] parts = line.split("\\s+");
                    List<String> prefs = new ArrayList<>();
                    boolean valid = true;
                    Set<String> seen = new HashSet<>();
                    for (String p : parts) {
                        if (!shiftSet.contains(p) || seen.contains(p)) { valid = false; break; }
                        prefs.add(p); seen.add(p);
                    }
                    if (!valid || prefs.isEmpty()) { System.out.println("      ⚠  Use: morning afternoon evening"); continue; }
                    // Append missing shifts at end
                    for (String s : SHIFTS) if (!prefs.contains(s)) prefs.add(s);
                    prefMap.put(day, prefs);
                    break;
                }
            }
            employees.put(name, prefMap);
            daysWorked.put(name, 0);
            assigned.put(name, new HashSet<>());
        }
    }

    // ─────────────────────────────────────────
    //  Scheduling
    // ─────────────────────────────────────────
    static void assignShifts() {
        // Pass 1 — honour priority preferences
        for (String day : DAYS) {
            for (String name : employees.keySet()) {
                if (daysWorked.get(name) >= MAX_DAYS) continue;
                if (assigned.get(name).contains(day))  continue;

                List<String> prefs = employees.get(name).get(day);
                if (prefs == null || prefs.isEmpty()) continue;  // unavailable

                boolean placed = false;
                for (String shift : prefs) {
                    schedule.get(day).get(shift).add(name);
                    assigned.get(name).add(day);
                    daysWorked.put(name, daysWorked.get(name) + 1);
                    placed = true;
                    break;  // first preference wins
                }

                // Conflict fallback: if somehow not placed, try any shift
                if (!placed) {
                    for (String shift : SHIFTS) {
                        if (!assigned.get(name).contains(day) && daysWorked.get(name) < MAX_DAYS) {
                            schedule.get(day).get(shift).add(name);
                            assigned.get(name).add(day);
                            daysWorked.put(name, daysWorked.get(name) + 1);
                            break;
                        }
                    }
                }
            }
        }

        // Pass 2 — ensure minimum 2 per shift per day
        List<String> nameList = new ArrayList<>(employees.keySet());
        for (String day : DAYS) {
            for (String shift : SHIFTS) {
                while (schedule.get(day).get(shift).size() < MIN_PER_SHIFT) {
                    List<String> eligible = new ArrayList<>();
                    for (String n : nameList) {
                        if (!assigned.get(n).contains(day) && daysWorked.get(n) < MAX_DAYS) {
                            eligible.add(n);
                        }
                    }
                    if (eligible.isEmpty()) break;  // understaffed — noted in output
                    String pick = eligible.get(rng.nextInt(eligible.size()));
                    schedule.get(day).get(shift).add(pick);
                    assigned.get(pick).add(day);
                    daysWorked.put(pick, daysWorked.get(pick) + 1);
                }
            }
        }
    }

    // ─────────────────────────────────────────
    //  Output
    // ─────────────────────────────────────────
    static void printSchedule() {
        System.out.println("\n\n╔══════════════════════════════════════════════════════════════╗");
        System.out.println("║               FINAL WEEKLY SCHEDULE                         ║");
        System.out.println("╚══════════════════════════════════════════════════════════════╝\n");

        int colW = 24;
        System.out.printf("%-12s", "Day");
        for (String s : SHIFTS) System.out.printf("%-" + colW + "s", capitalize(s));
        System.out.println();
        System.out.println("─".repeat(12 + colW * 3));

        for (String day : DAYS) {
            System.out.printf("%-12s", day);
            for (String shift : SHIFTS) {
                List<String> names = schedule.get(day).get(shift);
                String cell = names.isEmpty() ? "⚠ UNDERSTAFFED" : String.join(", ", names);
                System.out.printf("%-" + colW + "s", cell);
            }
            System.out.println();
        }

        System.out.println("\n" + "─".repeat(40));
        System.out.println("Days worked per employee:");
        for (Map.Entry<String, Integer> e : daysWorked.entrySet()) {
            System.out.printf("  %-20s %d day(s)%n", e.getKey(), e.getValue());
        }
        System.out.println();
    }

    static String capitalize(String s) {
        return s.isEmpty() ? s : Character.toUpperCase(s.charAt(0)) + s.substring(1);
    }

    // ─────────────────────────────────────────
    //  Main
    // ─────────────────────────────────────────
    public static void main(String[] args) {
        System.out.println("Employee Scheduler — Java");
        System.out.println("─".repeat(40));
        Scanner sc = new Scanner(System.in);
        System.out.print("Run in (d)emo mode or (i)nteractive mode? [d/i]: ");
        String mode = sc.nextLine().trim().toLowerCase();

        initSchedule();

        if (mode.equals("i")) {
            collectInteractive();
        } else {
            loadDemoData();
        }

        assignShifts();
        printSchedule();
    }
}
