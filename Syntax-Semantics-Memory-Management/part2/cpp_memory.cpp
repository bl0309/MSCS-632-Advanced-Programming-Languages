// Manual allocation + deallocation (plus a deliberate leak for profiling)
#include <iostream>

int main() {
    int* data = new int[1'000'000];   // allocate
    for (int i = 0; i < 1'000'000; i++) data[i] = i;

    std::cout << "Allocated array; first element = " << data[0] << std::endl;

    // delete[] data; // uncomment to fix leak
    return 0;
}
