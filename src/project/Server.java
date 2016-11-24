package project;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
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
    private static final int NUM_CLIENTS = 2;
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

    public void acceptConnections() {
        while (currentNumClients < NUM_CLIENTS) {
            Socket clientSocket = null;
            try {
                clientSocket = serverSocket.accept();
                System.out.println("client connected");
            } catch (IOException e) {
                e.printStackTrace();
            }
            //delegate to new thread
            ConnectionHandler connectionHandler =
                    new ConnectionHandler(clientSocket, this, currentNumClients, globalMapSeed);
            clients.add(connectionHandler);

            new Thread(connectionHandler).start();
            currentNumClients++;
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
                List<PlayerStateWithID> allPlayerState = new ArrayList<>();
                for (int i = 0; i < currentNumClients; i++) {
                    PlayerStateWithID playerStateWithID = (PlayerStateWithID) messages.take();
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
