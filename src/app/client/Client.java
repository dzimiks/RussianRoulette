package app.client;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Random;
import java.util.UUID;

import app.Constants;
import com.google.gson.Gson;

import app.model.*;

public class Client implements Runnable {

	private static Random random = new Random();

	private UUID uuid;
	private int score;

	private Socket socket;
	private BufferedReader in;
	private PrintWriter out;
	private boolean seated;

	private Gson gson;

	public Client() {
		uuid = UUID.randomUUID();
		score = 0;
		gson = new Gson();
		seated = true;

		try {
			socket = new Socket(Constants.HOST, Constants.PORT);
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
		} catch (IOException e) {
			System.out.println("[IOException]: Client constructor!");
		}
	}

	@Override
	public void run() {
		try {
			System.out.println("Client with UUID: " + uuid + " is created!\n");

			while (seated) {
				String message = in.readLine();
				ServerMessage json = gson.fromJson(message, ServerMessage.class);
				play(json);
			}

			socket.close();
		} catch (IOException e) {
			System.out.println("[IOException]: Client run method!");
		}
	}

	public void play(ServerMessage message) {
		ServerPurpose command = message.getServerPurpose();

		if (command.equals(ServerPurpose.LEAVE)) {
			System.out.println("[Server]: LEAVE command!");
			System.out.println("[Server Message]: " + message.getMessage());
			System.out.println();
			seated = false;
		} else if (command.equals(ServerPurpose.DRAW)) {
			int numberOfSticks = Integer.parseInt(message.getMessage());
			int clientChoice = random.nextInt(numberOfSticks);

			System.out.println("[Server]: DRAW command!");
			System.out.println("[Server]: Number of sticks: " + numberOfSticks);
			System.out.println("[Client]: Choice: " + clientChoice);
			System.out.println();

			ClientMessage clientMessage = new ClientMessage(ClientPurpose.CHOICE, Integer.toString(clientChoice));
			String json = gson.toJson(clientMessage);
			out.println(json);
		} else if (command.equals(ServerPurpose.PREDICT)) {
			boolean prediction = random.nextBoolean();

			System.out.println("[Server]: PREDICT command!");
			System.out.println("[Client]: Prediction: " + (prediction ? "WIN" : "LOOSE"));
			System.out.println();

			ClientMessage clientMessage = new ClientMessage(ClientPurpose.CHOICE, Boolean.toString(prediction));
			String json = gson.toJson(clientMessage);
			out.println(json);
		} else if (command.equals(ServerPurpose.GOOD_PREDICTION)) {
			System.out.println("[Server]: GOOD_PREDICTION command!");
			System.out.println();
			score++;
		} else if (command.equals(ServerPurpose.BAD_PREDICTION)) {
			System.out.println("[Server]: BAD_PREDICTION command!");
			System.out.println();
		} else if (command.equals(ServerPurpose.CLIENT_UUID)) {
			System.out.println("[Server]: CLIENT_UUID command!");
			System.out.println("[Client]: UUID: " + uuid.toString());
			System.out.println();

			ClientMessage clientMessage = new ClientMessage(ClientPurpose.UUID, uuid.toString());
			String json = gson.toJson(clientMessage);
			out.println(json);
		} else if (command.equals(ServerPurpose.CLIENT_SCORE)) {
			System.out.println("[Server]: CLIENT_SCORE command!");
			System.out.println("[Client]: Score: " + score);
			System.out.println();

			ClientMessage clientMessage = new ClientMessage(ClientPurpose.SCORE, Integer.toString(score));
			String json = gson.toJson(clientMessage);
			out.println(json);
		}
	}
}
