public class Demo3_12_1 {
    public static void main(String[] args) {
        int result = test();
        System.out.println(result);
    }

    private static int test() {
        try {
            int i = 1 / 0;
            return 10;
        } finally {
            // finally内return会吞异常
            return 20;
        }
    }
}
