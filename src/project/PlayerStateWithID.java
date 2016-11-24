package project;

/**
 * Created by brianzhao on 11/24/16.
 */
public class PlayerStateWithID {
    private PlayerState playerState;
    private int id;

    public PlayerStateWithID(PlayerState playerState, int id) {
        this.playerState = playerState;
        this.id = id;
    }

    public PlayerState getPlayerState() {
        return playerState;
    }

    public int getId() {
        return id;
    }

    @Override
    public String toString() {
        return "project.PlayerStateWithID{" +
                "playerState=" + playerState +
                ", id=" + id +
                '}';
    }
}
