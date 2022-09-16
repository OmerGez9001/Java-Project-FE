public class Main extends Thread {
    public static void main(String[] args) {
        Main obj  = new Main();
        Thread thread = new Thread(obj);
        thread.start();
    }
    public void run() {
        LoginPage.instance();
    }

}
