package project;

import java.io.IOException;
import java.net.ServerSocket;

/**
 * Created by brianzhao on 11/24/16.
 */
public class SocketCloser implements Runnable {
    private int secondsToWait;
    private ServerSocket serverSocket;

    public SocketCloser(int secondsToWait, ServerSocket serverSocket) {
        this.secondsToWait = secondsToWait;
        this.serverSocket = serverSocket;
    }

    @Override
    public void run() {
        try {
            for (int i = 0; i < secondsToWait; i++) {
                Thread.sleep(1000);
                System.out.println(i+1 + " seconds passed...");
            }
            serverSocket.close();
        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
        }
    }
}
