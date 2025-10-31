public class Demo1_8 extends ClassLoader{
    public static void main(String[] args) {
        int j = 0;
        Demo1_8 test = new Demo1_8();
        for (int i = 0; i < 10000; i++) {
            System.out.println("i = " + i);
        }
    }
}
