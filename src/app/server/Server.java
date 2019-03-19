package app.server;

import app.Constants;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server implements Runnable {

    private ServerMiddleware serverMiddleware;

    public Server() {
        this.serverMiddleware = new ServerMiddleware();
        Thread thread = new Thread(serverMiddleware);
        thread.start();
    }

    public void run() {
        try {
            ServerSocket serverSocket = new ServerSocket(Constants.PORT);
            System.out.println("Server is running on port: " + Constants.PORT);

            while (serverMiddleware.getNumberOfPlayedGames() < ServerMiddleware.GAMES_NUM) {
                Socket player = serverSocket.accept();
                serverMiddleware.addClient(player);
            }

            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
