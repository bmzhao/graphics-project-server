package project;

import java.util.List;

/**
 * Created by brianzhao on 11/23/16.
 */
public class Message {
    public static class GatherState{}

    public static class SendState {
        private List<PlayerStateWithID> allPlayers;

        public SendState(List<PlayerStateWithID> allPlayers) {
            this.allPlayers = allPlayers;
        }

        public List<PlayerStateWithID> getAllPlayers() {
            return allPlayers;
        }
    }

}
