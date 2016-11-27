package project;

import java.io.*;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by brianzhao on 11/23/16.
 */
public class ConnectionHandler implements Runnable {
    public static final int SERVER_DELTA_TRIAL_COUNT = 5;
    private Socket clientSocket;
    private Map<Integer, PlayerState> stateMap;
    private DataOutputStream clientOutputStream;
    private DataInputStream clientInputStream;
    private int id;
    private int globalMapSeed;
    private int totalNumPlayers;
    private PlayerState currentPlayerState;

    //first thing to send to client = client id (for distinguishing person objects)
    // seed for map and total number of players
    public ConnectionHandler(Socket clientSocket, Map<Integer, PlayerState> stateMap, int id,
                             int globalMapSeed, int totalNumPlayers) {
        this.clientSocket = clientSocket;
        this.stateMap = stateMap;
        this.id = id;
        this.globalMapSeed = globalMapSeed;
        this.totalNumPlayers = totalNumPlayers;

        try {
            clientOutputStream = new DataOutputStream(new BufferedOutputStream(clientSocket.getOutputStream()));
            clientInputStream = new DataInputStream(new BufferedInputStream(clientSocket.getInputStream()));
            timeDeltaExchange();
            //send seed
            clientOutputStream.writeInt(globalMapSeed);
            //send client id
            clientOutputStream.writeInt(id);
            //send total number of players
            clientOutputStream.writeInt(totalNumPlayers);
            clientOutputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void timeDeltaExchange(){
        try {
            for (int i = 0; i < SERVER_DELTA_TRIAL_COUNT; i++) {
                clientInputStream.read();
                clientOutputStream.writeLong(System.currentTimeMillis());
                clientOutputStream.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        while (true) {
            try {
                float x = clientInputStream.readFloat();
                float y = clientInputStream.readFloat();
                float z = clientInputStream.readFloat();
                long time = clientInputStream.readLong();
                currentPlayerState = new PlayerState(x, y, z, id, time);
                stateMap.put(id, currentPlayerState);

                Map<Integer, PlayerState> toSend = new HashMap<>(stateMap);
                for (Integer id : toSend.keySet()) {
                    clientOutputStream.writeInt(id);
                    PlayerState playerState = toSend.get(id);
                    clientOutputStream.writeFloat(playerState.getX());
                    clientOutputStream.writeFloat(playerState.getY());
                    clientOutputStream.writeFloat(playerState.getZ());
                    clientOutputStream.writeLong(playerState.getTime());
                }
                clientOutputStream.flush();
                System.out.println(toSend);
            } catch (IOException e) {
                e.printStackTrace();
                System.exit(0);
            }
        }
    }
}
