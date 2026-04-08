"""stats.py – Basic statistics in Python (object-oriented paradigm)."""


class StatisticsCalculator:
    """Encapsulates a list of integers and exposes mean / median / mode."""

    def __init__(self, data: list[int]):
        if not data:
            raise ValueError("Data list must not be empty.")
        self._data: list[int] = list(data)   # defensive copy

    # ── Properties ──────────────────────────────────────────────────────────

    @property
    def data(self) -> list[int]:
        return list(self._data)

    # ── Mean ─────────────────────────────────────────────────────────────────

    def mean(self) -> float:
        """Return the arithmetic mean."""
        return sum(self._data) / len(self._data)

    # ── Median ───────────────────────────────────────────────────────────────

    def median(self) -> float:
        """Return the median value."""
        sorted_data = sorted(self._data)
        n = len(sorted_data)
        mid = n // 2
        if n % 2 == 1:
            return float(sorted_data[mid])
        return (sorted_data[mid - 1] + sorted_data[mid]) / 2.0

    # ── Mode ─────────────────────────────────────────────────────────────────

    def mode(self) -> list[int]:
        """Return a sorted list of the most frequently occurring value(s)."""
        freq: dict[int, int] = {}
        for value in self._data:
            freq[value] = freq.get(value, 0) + 1

        max_freq = max(freq.values())
        return sorted(k for k, v in freq.items() if v == max_freq)

    # ── Summary ──────────────────────────────────────────────────────────────

    def summary(self) -> str:
        """Return a formatted summary of all statistics."""
        return (
            f"Data:   {self._data}\n"
            f"Mean:   {self.mean():.2f}\n"
            f"Median: {self.median():.2f}\n"
            f"Mode:   {self.mode()}"
        )

    def __repr__(self) -> str:
        return f"StatisticsCalculator(data={self._data!r})"


# ── Entry point ──────────────────────────────────────────────────────────────

if __name__ == "__main__":
    print("=== Python Statistics Calculator (Object-Oriented) ===\n")

    examples = [
        ("Example 1", [4, 1, 2, 2, 3, 5, 3, 3, 7, 1]),
        ("Example 2", [10, 20, 20, 30, 40, 40, 40, 50]),
        ("Example 3 – multi-modal", [1, 1, 2, 2, 3]),
    ]

    for label, data in examples:
        print(f"--- {label} ---")
        calc = StatisticsCalculator(data)
        print(calc.summary())
        print()
