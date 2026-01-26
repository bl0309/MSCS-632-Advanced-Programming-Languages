fn main() {
    let v = vec![1, 2, 3, 4, 5];          // heap allocation inside Vec
    let sum = calculate_sum(&v);          // borrow immutably
    println!("Sum in Rust: {}", sum);

    let s1 = String::from("hello");
    let s2 = s1;                          // move ownership
    // println!("{}", s1);                // compile-time error: use of moved value
    println!("{}", s2);
}

fn calculate_sum(arr: &Vec<i32>) -> i32 {
    arr.iter().sum()
}
