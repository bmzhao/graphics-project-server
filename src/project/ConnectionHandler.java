package project;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * Created by brianzhao on 11/23/16.
 */
public class ConnectionHandler implements Runnable {
    private Socket clientSocket;
    private BlockingQueue<Object> messages;
    private Map<Integer,PlayerState> stateMap;
    private ObjectOutputStream clientOutputStream;
    private ObjectInputStream clientInputStream;
    private int id;
    private int globalMapSeed;
    private int totalNumPlayers;
    private PlayerState currentPlayerState;

    //first thing to send to client = client id (for distinguishing person objects)
    // seed for map and total number of players
    public ConnectionHandler(Socket clientSocket, Map<Integer,PlayerState> stateMap, int id,
                             int globalMapSeed, int totalNumPlayers) {
        this.clientSocket = clientSocket;
        this.stateMap = stateMap;
        this.id = id;
        this.globalMapSeed = globalMapSeed;
        this.totalNumPlayers = totalNumPlayers;
        this.messages = new ArrayBlockingQueue<>(100);

        try {
            clientOutputStream = new ObjectOutputStream(clientSocket.getOutputStream());
            clientInputStream = new ObjectInputStream(clientSocket.getInputStream());
            //send seed
            clientOutputStream.writeObject(globalMapSeed);
            //send client id
            clientOutputStream.writeObject(id);
            //send total number of players
            clientOutputStream.writeObject(totalNumPlayers);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void run() {
        while (true) {
            try {
                currentPlayerState = (PlayerState) clientInputStream.readObject();
                stateMap.put(currentPlayerState.getId(), currentPlayerState);
                clientOutputStream.writeObject(stateMap);
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }
}
