package project;

import java.io.Serializable;

/**
 * Created by brianzhao on 11/23/16.
 */
public class PlayerState implements Serializable{
    private static final long serialVersionUID = 1123789127L;

    private final int id;
    private final float x, y, z;
    private long time;

    public PlayerState(float x, float y, float z, int id, long time) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.id = id;
        this.time = time;
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

    public int getId() {
        return id;
    }

    public long getTime() {
        return time;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PlayerState that = (PlayerState) o;

        if (id != that.id) return false;
        if (Float.compare(that.x, x) != 0) return false;
        if (Float.compare(that.y, y) != 0) return false;
        return Float.compare(that.z, z) == 0;
    }

    @Override
    public int hashCode() {
        return id;
    }

    @Override
    public String toString() {
        return "PlayerState{" +
                "id=" + id +
                ", x=" + x +
                ", y=" + y +
                ", z=" + z +
                ", time=" + time +
                '}';
    }
}
