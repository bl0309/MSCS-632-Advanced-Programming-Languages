"use strict";

// JavaScript: closure and implicit coercion
function makeCounter(start = 0) {
  let count = start;
  return function inc(step = 1) {
    count += step;
    return count;
  };
}

const c = makeCounter(10);
console.log(c());      // 11
console.log(c(5));     // 16
console.log(c("2"));   // "162" (string concatenation)
