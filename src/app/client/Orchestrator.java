package app.client;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;

public class Orchestrator implements Runnable {

    private int N;
    private ExecutorService executorService;

    public Orchestrator(int N) {
        this.N = N;
        this.executorService = Executors.newCachedThreadPool();
    }

    public void run() {
        try {
            for (int i = 0; i < N; i++) {
                Thread.sleep(ThreadLocalRandom.current().nextLong(1, 1001));
                this.executorService.submit(new Client());
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        this.executorService.shutdown();
    }
}
