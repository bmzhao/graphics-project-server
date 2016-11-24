package project;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * Created by brianzhao on 11/23/16.
 */
public class ConnectionHandler implements Runnable,Messagable {
    private Socket clientSocket;
    private BlockingQueue<Object> messages;
    private Messagable mainServer;
    private ObjectOutputStream clientOutputStream;
    private ObjectInputStream clientInputStream;
    private int id;
    private int globalMapSeed;

    public ConnectionHandler(Socket clientSocket, Messagable sender, int id, int globalMapSeed) {
        this.clientSocket = clientSocket;
        this.mainServer = sender;
        this.id = id;
        this.globalMapSeed = globalMapSeed;
        this.messages = new ArrayBlockingQueue<>(100);
        try {
            clientOutputStream = new ObjectOutputStream(clientSocket.getOutputStream());
            clientInputStream = new ObjectInputStream(clientSocket.getInputStream());
            clientOutputStream.writeObject(globalMapSeed);
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
                    PlayerState playerState = (PlayerState) clientInputStream.readObject();
                    //forward state to server
                    mainServer.sendMessage(new PlayerStateWithID(playerState,id));
                } else if (message instanceof Message.SendState) {
                    List<PlayerStateWithID> allState = ((Message.SendState) message).getAllPlayers();
                    List<PlayerState> stateOfAllOtherPlayers = new ArrayList<>();
                    for (PlayerStateWithID playerStateWithID : allState) {
                        if (playerStateWithID.getId() != this.id) {
                            stateOfAllOtherPlayers.add(playerStateWithID.getPlayerState());
                        }
                    }
                    clientOutputStream.writeObject(stateOfAllOtherPlayers);
                }
            } catch (InterruptedException | IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }


}
