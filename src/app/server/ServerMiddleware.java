package app.server;

import java.io.IOException;
import java.net.Socket;
import java.util.*;

import app.Constants;
import com.google.gson.Gson;

import app.model.ClientMessage;
import app.model.ServerPurpose;
import app.model.ServerMessage;

public class ServerMiddleware implements Runnable {

    public static int GAMES_NUM;
    private List<ClientInfo> clients;
    private Map<String, Integer> scores;
    private int clientsAtTable;
    private GamesInfo gamesInfo;
    private Gson gson;
    private static Random random = new Random();

    public ServerMiddleware() {
        Scanner in = new Scanner(System.in);
        System.out.print("Enter number of games: ");
        GAMES_NUM = in.nextInt();
        in.close();
        this.clients = new ArrayList<>();
        this.scores = new HashMap<>();
        this.clientsAtTable = 0;
        this.gamesInfo = new GamesInfo();
        this.gson = new Gson();

        for (int i = 0; i < Constants.CLIENTS_NUMBER; i++) {
            clients.add(new ClientInfo());
        }
    }

    public synchronized void addClient(Socket clientSocket) {
        try {
            ClientInfo clientInfo = new ClientInfo();
            clientInfo.setSocket(clientSocket);
            clientInfo.setFreeSpot(false);

            if (clientsAtTable == Constants.CLIENTS_NUMBER) {
                notifyClientToLeave(clientInfo, "No space!");
                clientInfo.getIn().close();
                clientInfo.getOut().close();
                System.out.println("Client left the game, because there's no space!\n");
            } else {
                for (int i = 0; i < Constants.CLIENTS_NUMBER; i++) {
                    if (clients.get(i).isFreeSpot()) {
                        clients.set(i, clientInfo);
                        clientsAtTable++;
                        System.out.println("Client entered the game!");
                        break;
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("[IOException]: ServerMiddleware: addClient method!");
        }
    }

    public void run() {
        try {
            while (gamesInfo.getNumberOfPlayedGames() < GAMES_NUM) {
                if (clientsAtTable == Constants.CLIENTS_NUMBER) {
                    playTheGame();
                }

                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    System.out.println("[InterruptedException]: ServerMiddleware: run method!");
                }
            }

            int maxScore = -1;
            String winner = null;

            for (int i = 0; i < Constants.CLIENTS_NUMBER; i++) {
                if (!clients.get(i).isFreeSpot()) {
                    ServerMessage serverMessage = new ServerMessage(ServerPurpose.CLIENT_SCORE, null);
                    String json = gson.toJson(serverMessage);
                    clients.get(i).getOut().println(json);
                    String clientJSON = clients.get(i).getIn().readLine();
                    ClientMessage clientMessage = gson.fromJson(clientJSON, ClientMessage.class);

                    int score = Integer.parseInt(clientMessage.getMessage());

                    serverMessage = new ServerMessage(ServerPurpose.CLIENT_UUID, null);
                    json = gson.toJson(serverMessage);
                    clients.get(i).getOut().println(json);

                    clientJSON = clients.get(i).getIn().readLine();
                    clientMessage = gson.fromJson(clientJSON, ClientMessage.class);
                    scores.put(clientMessage.getMessage(), score);

                    if (score > maxScore) {
                        maxScore = score;
                        winner = clientMessage.getMessage();
                    }
                }
            }

            scores = sortByValue(scores);
            System.out.println("\n=====================");
            System.out.println("SCORES:");

            for (Map.Entry<String, Integer> entry : scores.entrySet()) {
                System.out.println(entry.getKey() + ": " + entry.getValue());
            }

            System.out.println("\nGAME NUMBER: " + GAMES_NUM);
            System.out.println("Winner's UUID: " + winner);
            System.out.println("Winner's score: " + maxScore);
            System.out.println("=====================");

            for (int i = 0; i < Constants.CLIENTS_NUMBER; i++) {
                if (!clients.get(i).isFreeSpot()) {
                    notifyClientToLeave(clients.get(i), "Game is over!");
                }
            }
        } catch (IOException e) {
            System.out.println("[IOException]: ServerMiddleware: run method!");
        }
    }

    private void playTheGame() throws IOException {
        System.out.println("\nGAME NUMBER: " + gamesInfo.getNumberOfPlayedGames());
        int smallStickIndex = random.nextInt(gamesInfo.getNumberOfSticksInHand());

        int choice = -1;
        Boolean predictions[] = new Boolean[Constants.CLIENTS_NUMBER];

        for (int i = 0; i < Constants.CLIENTS_NUMBER; i++) {
            ServerMessage serverMessage;

            if (i == gamesInfo.getNextClientNumber()) {
                serverMessage = new ServerMessage(ServerPurpose.DRAW, Integer.toString(gamesInfo.getNumberOfSticksInHand()));
            } else {
                serverMessage = new ServerMessage(ServerPurpose.PREDICT, null);
            }

            String json = gson.toJson(serverMessage);
            clients.get(i).getOut().println(json);
        }

        for (int i = 0; i < Constants.CLIENTS_NUMBER; i++) {
            String clientJSON = clients.get(i).getIn().readLine();
            ClientMessage clientMessage = gson.fromJson(clientJSON, ClientMessage.class);

            if (i == gamesInfo.getNextClientNumber()) {
                if (clientMessage.getMessage() != null) {
                    choice = Integer.parseInt(clientMessage.getMessage());
                } else {
                    choice = 0;
                }
            } else {
                if (clientMessage.getMessage() != null) {
                    predictions[i] = Boolean.parseBoolean(clientMessage.getMessage());
                } else {
                    predictions[i] = false;
                }
            }
        }

        for (int i = 0; i < Constants.CLIENTS_NUMBER; i++) {
            if (i == gamesInfo.getNextClientNumber()) {
                if (smallStickIndex == choice) {
                    notifyClientToLeave(clients.get(i), "Small stick is chosen!");
                    clients.get(i).setFreeSpot(true);
                    clientsAtTable--;
                }
            } else {
                ServerMessage serverMessage;

                if ((smallStickIndex == choice) == !predictions[i]) {
                    serverMessage = new ServerMessage(ServerPurpose.GOOD_PREDICTION, null);
                } else {
                    serverMessage = new ServerMessage(ServerPurpose.BAD_PREDICTION, null);
                }

                String json = gson.toJson(serverMessage);
                clients.get(i).getOut().println(json);
            }
        }

        if (smallStickIndex == choice) {
            gamesInfo.resetNextClientNumber();
            gamesInfo.resetSticks();
        } else {
            gamesInfo.incrementNextClientNumber();
            gamesInfo.decrementSticks();
        }

        gamesInfo.incrementNumberOfPlayedGames();
        System.out.println("Game has ended! There are " + clientsAtTable + " clients at the table!");
    }

    private void notifyClientToLeave(ClientInfo info, String reason) {
        ServerMessage serverMessage = new ServerMessage(ServerPurpose.LEAVE, reason);
        String json = gson.toJson(serverMessage);
        info.getOut().println(json);
    }

    public int getNumberOfPlayedGames() {
        return gamesInfo.getNumberOfPlayedGames();
    }

    private <K, V extends Comparable<? super V>> Map<K, V> sortByValue(Map<K, V> map) {
        List<Map.Entry<K, V>> list = new ArrayList<>(map.entrySet());
        list.sort(Collections.reverseOrder(Map.Entry.comparingByValue()));
        Map<K, V> result = new LinkedHashMap<>();

        for (Map.Entry<K, V> entry : list) {
            result.put(entry.getKey(), entry.getValue());
        }

        return result;
    }
}
