package app.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server implements Runnable {

    private final int PORT = 9393;
    private ServerSocket serverSocket;
    private Resources resources;

    public Server() throws IOException {
        System.out.println("Connecting to port " + PORT);
        this.serverSocket = new ServerSocket(PORT);
        resources = new Resources();
    }

    public void run() {
        while (true) {
            try {
                Socket socket = serverSocket.accept();
                ServerThread serverThread = new ServerThread(socket, resources);
                new Thread(serverThread).start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
