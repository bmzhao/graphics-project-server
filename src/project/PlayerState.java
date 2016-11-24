package project;

import java.io.Serializable;

/**
 * Created by brianzhao on 11/23/16.
 */
public class PlayerState implements Serializable{
    private static final long serialVersionUID = 1123789127L;


    private float x, y, z;

    public PlayerState(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getZ() {
        return z;
    }

    @Override
    public String toString() {
        return "project.PlayerState{" +
                "x=" + x +
                ", y=" + y +
                ", z=" + z +
                '}';
    }
}
