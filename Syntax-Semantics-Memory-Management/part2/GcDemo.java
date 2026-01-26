public class GcDemo {
    static class BigObject {
        private byte[] data = new byte[5_000_000]; // ~5 MB
    }

    public static void main(String[] args) throws Exception {
        for (int i = 0; i < 50; i++) {
            BigObject obj = new BigObject();
            // obj becomes unreachable at the end of the loop iteration
            Thread.sleep(50);
        }
        System.out.println("Done allocating; GC will reclaim unreachable objects.");
    }
}
