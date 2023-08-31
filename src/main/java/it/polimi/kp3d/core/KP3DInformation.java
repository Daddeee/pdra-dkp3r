package it.polimi.kp3d.core;

import it.polimi.algorithm.domain.Information;
import it.polimi.kp3d.domain.Item;

import java.util.Set;

public class KP3DInformation implements Information {
    private Set<Item> newItems;

    public KP3DInformation(Set<Item> newItems) {
        this.newItems = newItems;
    }

    public Set<Item> getNewItems() {
        return newItems;
    }
}
