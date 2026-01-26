// C++: lambda closure with static typing
#include <iostream>
#include <functional>

std::function<int(int)> makeCounter(int start = 0) {
    int count = start;
    return [count](int step) mutable {
        count += step;
        return count;
    };
}

int main() {
    auto c = makeCounter(10);
    std::cout << c(1) << std::endl;  // 11
    std::cout << c(5) << std::endl;  // 16
    // c("2"); // Compile-time error: cannot convert const char* to int
}
