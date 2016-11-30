package project;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
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
    private ServerSocket serverSocket;
    private int globalMapSeed;
    private Map<Integer, PlayerState> allPlayerState;

    public Server() {
        currentNumClients = 0;
        try {
            serverSocket = new ServerSocket(9000);
        } catch (IOException e) {
            e.printStackTrace();
        }
        globalMapSeed = new Random().nextInt();
        allPlayerState = new ConcurrentHashMap<>();
    }

    public Map<Integer, PlayerState> getAllPlayerState() {
        return allPlayerState;
    }


    private void acceptConnections() {
        int clientNumber = 0;
        while (true) {
            Socket clientSocket = null;
            try {
                clientSocket = serverSocket.accept();
            } catch (IOException e) {
                e.printStackTrace();
            }
            //delegate to new thread
            if (clientSocket != null) {
                new Thread(
                        new ConnectionHandler(clientSocket, allPlayerState, clientNumber++, globalMapSeed)
                ).start();
                System.out.println("client connected");
            }
        }
    }

    public static void main(String[] args) {
        Server server = new Server();
        server.acceptConnections();
    }


}
