package it.polimi.kp3d.domain;

import java.util.Objects;

public class Item {
    private final int id;
    private final double reward;
    private final int w;
    private final int d;
    private final int h;

    public Item(int id, double reward, int w, int d, int h) {
        this.id = id;
        this.reward = reward;
        this.w = w;
        this.d = d;
        this.h = h;
    }

    public int getId() {
        return id;
    }

    public double getReward() {
        return reward;
    }

    public int getW() {
        return w;
    }

    public int getD() {
        return d;
    }

    public int getH() {
        return h;
    }

    public long getVolume() {
        return (long) w * d * h;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Item item = (Item) o;
        return id == item.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
