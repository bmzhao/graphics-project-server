package project;

import java.util.List;
import java.util.Set;

/**
 * Created by brianzhao on 11/23/16.
 */
public class Message {
    public static class GatherState{}

    public static class SendState {
        private Set<PlayerState> allPlayers;

        public SendState(Set<PlayerState> allPlayers) {
            this.allPlayers = allPlayers;
        }

        public Set<PlayerState> getAllPlayers() {
            return allPlayers;
        }
    }

}
