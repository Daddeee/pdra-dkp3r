package it.polimi.kp3d.core;

import it.polimi.algorithm.domain.State;
import it.polimi.kp3d.domain.Cube;
import it.polimi.kp3d.domain.Item;
import it.polimi.kp3d.domain.Placement;

import java.util.HashSet;
import java.util.Set;

public class KP3DState implements State {
    private final int W;
    private final int D;
    private final int H;
    private final Set<Item> packedItems;
    private final Set<Cube> emptyMaximalSpaces;
    private long packedVolume;
    private final Set<Item> itemsToPack;
    private double reward;
    private int numBatches;

    public KP3DState(int w, int d, int h) {
        W = w;
        D = d;
        H = h;
        packedItems = new HashSet<>();
        emptyMaximalSpaces = new HashSet<>();
        emptyMaximalSpaces.add(new Cube(w, d, h, 0, 0, 0));
        packedVolume = 0;
        reward = 0;
        numBatches = 0;
        itemsToPack = new HashSet<>();
    }

    public KP3DState(int w, int d, int h, Set<Item> packedItems, Set<Cube> emptyMaximalSpaces, long packedVolume,
                     double reward, int numBatches, Set<Item> itemsToPack) {
        W = w;
        D = d;
        H = h;
        this.packedItems = packedItems;
        this.emptyMaximalSpaces = emptyMaximalSpaces;
        this.packedVolume = packedVolume;
        this.reward = reward;
        this.numBatches = numBatches;
        this.itemsToPack = itemsToPack;
    }

    public void place(Placement placement) {
        updateEmptyMaximalSpaces(placement, emptyMaximalSpaces);
        packedItems.add(placement.getItem());
        itemsToPack.remove(placement.getItem());
        packedVolume += placement.getCube().getVolume();
        reward += placement.getItem().getReward();
    }

    public static void updateEmptyMaximalSpaces(Placement placement, Set<Cube> emptyMaximalSpaces) {
        Cube placementCube = placement.getCube();
        Cube placementSpace = placement.getSpace();
        if (placementSpace.getX() != placementCube.getX() 
                || placementSpace.getY() != placementCube.getY() 
                || placementSpace.getZ() != placementCube.getZ())
            throw new IllegalArgumentException("Illegal position for splitting");
        emptyMaximalSpaces.remove(placementSpace);
        int top = placementSpace.getHeight() - placementCube.getHeight();
        if (top > 1) {
            Cube topSpace = new Cube(placementSpace.getWidth(), placementSpace.getDepth(),
                    top, placementSpace.getX(), placementSpace.getY(),
                    placementSpace.getZ() + placementCube.getHeight());
            emptyMaximalSpaces.add(topSpace);
        }
        int front = placementSpace.getDepth() - placementCube.getDepth();
        if (front > 1) {
            Cube frontSpace = new Cube(placementSpace.getWidth(), front,
                    placementCube.getHeight(), placementSpace.getX(), placementSpace.getY() + placementCube.getDepth(),
                    placementSpace.getZ());
            emptyMaximalSpaces.add(frontSpace);
        }
        int right = placementSpace.getWidth() - placementCube.getWidth();
        if (right > 1) {
            Cube rightSpace = new Cube(right, placementCube.getDepth(),
                    placementCube.getHeight(), placementSpace.getX() + placementCube.getWidth(), placementSpace.getY(),
                    placementSpace.getZ());
            emptyMaximalSpaces.add(rightSpace);
        }
        Set<Cube> toRemove = new HashSet<>();
        for (Cube space : emptyMaximalSpaces) {
            for (Cube otherSpace : emptyMaximalSpaces) {
                if (!space.equals(otherSpace) && space.contains(otherSpace)) {
                    toRemove.add(otherSpace);
                }
            }
        }
        emptyMaximalSpaces.removeAll(toRemove);
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

    public Set<Item> getPackedItems() {
        return packedItems;
    }

    public Set<Cube> getEmptyMaximalSpaces() {
        return emptyMaximalSpaces;
    }

    public long getPackedVolume() {
        return packedVolume;
    }

    public double getReward() {
        return reward;
    }

    public Set<Item> getItemsToPack() {
        return itemsToPack;
    }

    public int getNumBatches() {
        return numBatches;
    }

    public void setNumBatches(int numBatches) {
        this.numBatches = numBatches;
    }

    @Override
    public boolean isAbsorbing() {
        return false;
    }

    @Override
    public State clone() {
        return new KP3DState(W, D, H, new HashSet<>(packedItems), new HashSet<>(emptyMaximalSpaces), packedVolume,
                reward, numBatches, new HashSet<>(itemsToPack));
    }
}
