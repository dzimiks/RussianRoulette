package app.server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientInfo {

    private boolean freeSpot;
    private BufferedReader in;
    private PrintWriter out;

    public ClientInfo() {
        freeSpot = true;
    }

    public void setSocket(Socket socket) throws IOException {
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(new BufferedWriter(
                new OutputStreamWriter(socket.getOutputStream())), 
                true);
    }

    public boolean isFreeSpot() {
        return freeSpot;
    }

    public void setFreeSpot(boolean freeSpot) {
        this.freeSpot = freeSpot;
    }

    public BufferedReader getIn() {
        return in;
    }

    public PrintWriter getOut() {
        return out;
    }
}
