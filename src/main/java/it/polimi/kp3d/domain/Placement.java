package it.polimi.kp3d.domain;

import java.util.Objects;

public class Placement {
    private final Item item;
    private final Cube cube;
    private final Cube space;

    public Placement(Item item, Cube cube, Cube space) {
        this.item = item;
        this.cube = cube;
        this.space = space;
    }

    public Item getItem() {
        return item;
    }

    public Cube getCube() {
        return cube;
    }

    public Cube getSpace() {
        return space;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Placement placement = (Placement) o;
        return Objects.equals(item, placement.item) && Objects.equals(cube, placement.cube);
    }

    @Override
    public int hashCode() {
        return Objects.hash(item, cube);
    }

    @Override
    public String toString() {
        return "Placement{" +
                "item=" + item +
                ", cube=" + cube +
                '}';
    }
}
