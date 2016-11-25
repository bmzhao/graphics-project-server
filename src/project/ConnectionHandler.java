package project;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * Created by brianzhao on 11/23/16.
 */
public class ConnectionHandler implements Runnable, Messagable {
    private Socket clientSocket;
    private BlockingQueue<Object> messages;
    private Messagable mainServer;
    private ObjectOutputStream clientOutputStream;
    private ObjectInputStream clientInputStream;
    private int id;
    private int globalMapSeed;
    private int totalNumPlayers;
    private PlayerState currentPlayerState;

    //first thing to send to client = client id (for distinguishing person objects)
    // seed for map and total number of players
    public ConnectionHandler(Socket clientSocket, Messagable sender, int id,
                             int globalMapSeed, int totalNumPlayers) {
        this.clientSocket = clientSocket;
        this.mainServer = sender;
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
    public void sendMessage(Object object) {
        messages.add(object);
    }

    @Override
    public void run() {
        while (true) {
            try {
                Object message = messages.take();
                if (message instanceof Message.GatherState) {
                    //read state of player from socket
                    currentPlayerState = (PlayerState) clientInputStream.readObject();
                    //forward state to server
                    mainServer.sendMessage(currentPlayerState);
                } else if (message instanceof Message.SendState) {
                    //mainserver tells us to forward state of all clients to our socket
                    Set<PlayerState> allState = ((Message.SendState) message).getAllPlayers();
                    clientOutputStream.writeObject(allState);
                }
            } catch (InterruptedException | IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }
}
