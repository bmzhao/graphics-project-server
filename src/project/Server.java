package project;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by brianzhao on 11/23/16.
 * Static number of clients, wait until accept # of clients first
 * <p>
 * Each client on separate socket, wait until we have all clients state,
 * and then broadcast all state to all clients
 */
public class Server {
    private static final long MILLISECONDS_TO_WAIT_FOR_CLIENTS = 20000;
    private int currentNumClients;
    private List<ConnectionHandler> clients;
    private ServerSocket serverSocket;
    private BlockingQueue<Object> messages;
    private int globalMapSeed;
    private Map<Integer, PlayerState> allPlayerState;
    private List<Thread> clientThreads;
    private static final float initialX = 50;
    private static final float initialY = 80;
    private static final float initialZ = 30;

    public Server() {
        currentNumClients = 0;
        clients = new ArrayList<>();
        clientThreads = new ArrayList<>();
        messages = new ArrayBlockingQueue<>(100);
        try {
            serverSocket = new ServerSocket(9000);
        } catch (IOException e) {
            e.printStackTrace();
        }
        globalMapSeed = new Random().nextInt();
    }

    //accept clients until 20 second timer runs out
    public void acceptConnections() {
        long currentTime = System.currentTimeMillis();
        List<Socket> clientSockets = new ArrayList<>();
        new Thread(new SocketCloser((int) (MILLISECONDS_TO_WAIT_FOR_CLIENTS / 1000), serverSocket)).start();
        while (System.currentTimeMillis() < currentTime + MILLISECONDS_TO_WAIT_FOR_CLIENTS) {
            Socket clientSocket = null;
            try {
                clientSocket = serverSocket.accept();
                System.out.println("client connected");
            } catch (IOException e) {
                e.printStackTrace();
            }
            //delegate to new thread
            if (clientSocket != null) {
                clientSockets.add(clientSocket);
                currentNumClients++;
            }
        }

        allPlayerState = new ConcurrentHashMap<>(currentNumClients);
        for (int i = 0; i < currentNumClients; i++) {
            allPlayerState.put(i, new PlayerState(initialX, initialY, initialZ, i));
        }

        for (int i = 0; i < currentNumClients; i++) {
            ConnectionHandler connectionHandler =
                    new ConnectionHandler(clientSockets.get(i), getAllPlayerState(),
                            i, globalMapSeed, currentNumClients);
            clients.add(connectionHandler);
            clientThreads.add(new Thread(connectionHandler));
            clientThreads.get(i).start();
            System.out.println("Starting client connection...");
        }
    }

    public Map<Integer, PlayerState> getAllPlayerState() {
        return allPlayerState;
    }

    public void gameLoop() {
        for (Thread t : clientThreads) {
            try {
                t.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        Server server = new Server();
        server.acceptConnections();
        server.gameLoop();
    }
}
