package app.client;

import app.model.Message;
import app.model.Purpose;
import app.model.Response;
import app.model.Status;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.UUID;

public class Client implements Runnable {

    private Socket socket;
    private BufferedWriter out;
    private BufferedReader in;
    private Gson gson;

    private String id;
    private Boolean seated;

    public Client() throws IOException {
        this.socket = new Socket("localhost", 9393);
        this.out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.gson = new Gson();
        this.id = UUID.randomUUID().toString();
        this.seated = false;
    }

    public void run() {
        try {
            if (!this.seated) {
                Message message = new Message();
                message.setId(this.id);
                message.setPurpose(Purpose.REQUEST_CHAIR);
                String convertedMessage = gson.toJson(message);

                out.write(convertedMessage);
                out.newLine();
                out.flush();

                String responseStr = in.readLine();
                Response response = gson.fromJson(responseStr, Response.class);

                if (response.getStatus().equals(Status.OK)) {
                    this.seated = true;
                    System.out.println("Klijent je seo za sto");
                } else {
                    System.out.println("Klijent nije uspeo da sedne za sto");
                }

                Thread.sleep(1000);
                socket.close();
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
