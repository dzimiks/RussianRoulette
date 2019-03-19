package app.client;

import java.util.Scanner;

public class ClientMain {

    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        System.out.print("Enter number of clients: ");
        int N = in.nextInt();
        in.close();
        Orchestrator orchestrator = new Orchestrator(N);
        Thread thread = new Thread(orchestrator);
        thread.start();
    }
}
