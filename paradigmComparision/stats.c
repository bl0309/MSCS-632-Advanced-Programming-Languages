#include <stdio.h>
#include <stdlib.h>
#include <string.h>

/* ── Comparison function for qsort ── */
int compare_ints(const void *a, const void *b) {
    return (*(int *)a - *(int *)b);
}

/* ── Mean ── */
double calculate_mean(int *arr, int n) {
    long long sum = 0;
    for (int i = 0; i < n; i++) sum += arr[i];
    return (double)sum / n;
}

/* ── Median ── */
double calculate_median(int *arr, int n) {
    /* Work on a sorted copy so we don't disturb the original */
    int *sorted = malloc(n * sizeof(int));
    memcpy(sorted, arr, n * sizeof(int));
    qsort(sorted, n, sizeof(int), compare_ints);

    double median;
    if (n % 2 == 0)
        median = (sorted[n/2 - 1] + sorted[n/2]) / 2.0;
    else
        median = sorted[n/2];

    free(sorted);
    return median;
}

/* ── Mode ── */
/* Returns the mode(s) via a dynamically allocated array.
   *mode_count is set to the number of modes found.          */
int *calculate_mode(int *arr, int n, int *mode_count) {
    int *sorted = malloc(n * sizeof(int));
    memcpy(sorted, arr, n * sizeof(int));
    qsort(sorted, n, sizeof(int), compare_ints);

    int max_freq = 1, cur_freq = 1;
    for (int i = 1; i < n; i++) {
        cur_freq = (sorted[i] == sorted[i-1]) ? cur_freq + 1 : 1;
        if (cur_freq > max_freq) max_freq = cur_freq;
    }

    int *modes = malloc(n * sizeof(int));
    *mode_count = 0;
    cur_freq = 1;

    for (int i = 1; i <= n; i++) {
        if (i < n && sorted[i] == sorted[i-1]) {
            cur_freq++;
        } else {
            if (cur_freq == max_freq)
                modes[(*mode_count)++] = sorted[i-1];
            cur_freq = 1;
        }
    }

    free(sorted);
    return modes;
}

/* ── Pretty-print helpers ── */
void print_array(int *arr, int n) {
    printf("[");
    for (int i = 0; i < n; i++) {
        printf("%d", arr[i]);
        if (i < n-1) printf(", ");
    }
    printf("]\n");
}

/* ── Main ── */
int main(void) {
    int data[] = {4, 1, 2, 2, 3, 5, 3, 3, 7, 1};
    int n = sizeof(data) / sizeof(data[0]);

    printf("=== C Statistics Calculator (Procedural) ===\n");
    printf("Data:   ");
    print_array(data, n);

    printf("Mean:   %.2f\n", calculate_mean(data, n));
    printf("Median: %.2f\n", calculate_median(data, n));

    int mode_count;
    int *modes = calculate_mode(data, n, &mode_count);
    printf("Mode:   ");
    print_array(modes, mode_count);
    free(modes);

    /* ── Second example: even-length list ── */
    printf("\n--- Second example ---\n");
    int data2[] = {10, 20, 20, 30, 40, 40, 40, 50};
    int n2 = sizeof(data2) / sizeof(data2[0]);
    printf("Data:   ");
    print_array(data2, n2);
    printf("Mean:   %.2f\n", calculate_mean(data2, n2));
    printf("Median: %.2f\n", calculate_median(data2, n2));

    int mc2;
    int *modes2 = calculate_mode(data2, n2, &mc2);
    printf("Mode:   ");
    print_array(modes2, mc2);
    free(modes2);

    return 0;
}
