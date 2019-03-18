package app.server;

import java.io.IOException;

public class ServerMain {

    public static void main(String[] args) throws IOException {
        Server server = new Server();
        Thread thread = new Thread(server);
        thread.start();
    }
}
