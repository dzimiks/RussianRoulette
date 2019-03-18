package app.client;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Orchestrator implements Runnable {

    private ExecutorService executorService;

    public Orchestrator() {
        this.executorService = Executors.newCachedThreadPool();
    }

    public void run() {
        try {
            for (int i = 0; i < 10; i++) {
                Thread.sleep(100);
                this.executorService.submit(new Client());
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

        this.executorService.shutdown();
    }
}
