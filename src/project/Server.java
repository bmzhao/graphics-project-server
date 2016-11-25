package project;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * Created by brianzhao on 11/23/16.
 * Static number of clients, wait until accept # of clients first
 * <p>
 * Each client on separate socket, wait until we have all clients state,
 * and then broadcast all state to all clients
 */
public class Server implements Messagable {
    private static final long MILLISECONDS_TO_WAIT_FOR_CLIENTS= 20000;
    private int currentNumClients;
    private List<ConnectionHandler> clients;
    private ServerSocket serverSocket;
    private BlockingQueue<Object> messages;
    private int globalMapSeed;

    public Server() {
        currentNumClients = 0;
        clients = new ArrayList<>();
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
        while (System.currentTimeMillis() < currentTime + MILLISECONDS_TO_WAIT_FOR_CLIENTS) {
            Socket clientSocket = null;
            try {
                clientSocket = serverSocket.accept();
                System.out.println("client connected");
            } catch (IOException e) {
                e.printStackTrace();
            }
            //delegate to new thread
            clientSockets.add(clientSocket);
            currentNumClients++;
        }

        for (int i = 0; i < currentNumClients; i++) {
            ConnectionHandler connectionHandler =
                    new ConnectionHandler(clientSockets.get(i), this,
                            currentNumClients, globalMapSeed, currentNumClients);
            clients.add(connectionHandler);
            new Thread(connectionHandler).start();
            System.out.println("Starting client connection...");
        }
    }

    public void gameLoop() {
        while (true) {
            try {
                //gather all state
                System.out.println("Gathering state...");
                for (ConnectionHandler client : clients) {
                    client.sendMessage(new Message.GatherState());
                }
                Set<PlayerState> allPlayerState = new HashSet<>();
                for (int i = 0; i < currentNumClients; i++) {
                    PlayerState playerStateWithID = (PlayerState) messages.take();
                    allPlayerState.add(playerStateWithID);
                }
                System.out.println("All state: \n" + allPlayerState.toString());
                Message.SendState resultState = new Message.SendState(allPlayerState);
                //gather all state
                for (ConnectionHandler client : clients) {
                    client.sendMessage(resultState);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }

    @Override
    public void sendMessage(Object object) {
        messages.add(object);
    }

    public static void main(String[] args) {
        Server server = new Server();
        server.acceptConnections();
        server.gameLoop();
    }
}
