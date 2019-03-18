package app.server;

import com.google.gson.Gson;
import app.model.Purpose;
import app.model.Message;
import app.model.Response;
import app.model.Status;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

public class ServerThread implements Runnable {

    private Socket socket;
    private BufferedWriter out;
    private BufferedReader in;
    private Gson gson;
    private Resources resources;

    public ServerThread(Socket socket, Resources resources) throws IOException {
        this.socket = socket;
        this.out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.gson = new Gson();
        this.resources = resources;
    }

    public void run() {
        try {
            String requestStr = in.readLine();
            Message request = gson.fromJson(requestStr, Message.class);

            Response response = new Response();
            response.setStatus(Status.DENIED);

            if (Purpose.REQUEST_CHAIR.equals(request.getPurpose())) {
                boolean seated = resources.giveSeat(new User(request.getId()));
                System.out.println("Stigao zahtev za stolicu od korisnika " + request.getId());

                if (seated) {
                    response.setStatus(Status.OK);
                }
            }

            out.write(gson.toJson(response));
            out.newLine();
            out.flush();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
