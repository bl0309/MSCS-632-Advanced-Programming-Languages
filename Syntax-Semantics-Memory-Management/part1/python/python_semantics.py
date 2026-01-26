# Python: closure and dynamic typing
def make_counter(start=0):
    count = start
    def inc(step=1):
        nonlocal count
        count += step
        return count
    return inc

c = make_counter(10)
print(c())        # 11
print(c(5))       # 16
print(c("2"))     # TypeError at runtime
