package app.server;

public class ServerMain {

    public static void main(String[] args) {
        Server server = new Server();
        Thread thread = new Thread(server);
        thread.start();
    }
}
