public class Load9 {
    public static void main(String[] args) {
        Singleton.test();
    }
}

class Singleton{
    private Singleton(){}

    public static void test(){
        System.out.println("test");
    }

    private static class LazyHolder{
        private static final Singleton SINGLETON = new Singleton();
        static{
            System.out.println("init");
        }
    }

    public Singleton getInstance(){
        return LazyHolder.SINGLETON;
    }
}
