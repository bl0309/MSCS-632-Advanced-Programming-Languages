# Statistics Calculator (C, Python, OCaml)

This project shows the same basic statistics computations implemented in three paradigms:
- **C (`stats.c`)**: Procedural
- **Python (`stats.py`)**: Object-Oriented
- **OCaml (`stats.ml`)**: Functional

Each program computes **mean**, **median**, and **mode** for a few example datasets.

## Files
- `stats.c` – C implementation (procedural)
- `stats.py` – Python implementation (OOP)
- `stats.ml` – OCaml implementation (functional)

## Requirements
- C compiler (e.g., `gcc`, `clang`, or `cc`)
- Python 3.10+
- OCaml toolchain (`ocamlc`)

## How to Run

### C
```bash
cc -O2 -Wall -Wextra stats.c -o stats_c
./stats_c
```

### Python
```bash
python3 stats.py
```

### OCaml
```bash
ocamlc stats.ml -o stats_ml
./stats_ml
```

## Notes
- All programs use built-in example datasets defined in the source files.
- The outputs are comparable across languages.
