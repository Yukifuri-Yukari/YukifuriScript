public class A {
    public static int aStaticField = 12345;
    public int aInstanceField = 54321;

    public static void main(String[] args) {
        System.out.println("Hello World!");
    }

    public static void a() {
        add(1, 2);
    }

    public static int add(int a, int b) {
        return a + b;
    }

    public int mul(int a, int b) {
        get(114514L);
        return a * b;
    }

    public long insertAFieldBetweenMethods = 1234567890L;

    public double get(long a) {
        return (double) a;
    }

    public void types(
            byte a,
            short b,
            int c,
            long d,
            float e,
            double f,
            char g,
            boolean h,
            String i,
            byte[] j,
            String[] k
    ) { }

    public static synchronized short awa() {
        return 1;
    }

    public static void comment(String... cmts) {}

    public static void loops() {
        int a = 0;
        comment("for loop");
        for (int i = 0; i < 10; i++) { }
        comment("while loop");
        while (a != 0) { }
        comment("do while loop");
        do { } while (a != 0);
        comment("switch");
        switch (a) {
            default:
                break;
        }
        comment("for-without-any");
        for (;;) { break; }
        comment("for-without-updater");
        for (int i = 0; i < 10;) { i++; }
        comment("for-without-condition");
        for (int i = 0;; i++) { if (i == -1) break; }
    }
}
