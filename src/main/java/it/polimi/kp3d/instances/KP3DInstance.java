package it.polimi.kp3d.instances;

import it.polimi.kp3d.domain.Item;

import java.util.LinkedList;
import java.util.Objects;
import java.util.Set;

public class KP3DInstance {
    private final int id;
    private final int W;
    private final int D;
    private final int H;
    private final LinkedList<Set<Item>> items;

    public KP3DInstance(int id, int W, int D, int H, LinkedList<Set<Item>> items) {
        this.id = id;
        this.W = W;
        this.D = D;
        this.H = H;
        this.items = items;
    }

    public int getId() {
        return id;
    }

    public int getW() {
        return W;
    }

    public int getD() {
        return D;
    }

    public int getH() {
        return H;
    }

    public LinkedList<Set<Item>> getItems() {
        return items;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        KP3DInstance that = (KP3DInstance) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
