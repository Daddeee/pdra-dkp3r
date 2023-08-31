package it.polimi.kp3d.core;

import it.polimi.algorithm.core.InformationSpace;
import it.polimi.algorithm.domain.Information;
import it.polimi.algorithm.domain.State;
import it.polimi.kp3d.domain.Item;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

public class KP3DInformationSpace implements InformationSpace {
    private final LinkedList<Set<Item>> itemBatches;

    public KP3DInformationSpace(LinkedList<Set<Item>> itemBatches) {
        this.itemBatches = new LinkedList<>();
        for (Set<Item> batch : itemBatches)
            this.itemBatches.add(new HashSet<>(batch));
    }

    @Override
    public Information getInformation(State state) {
        if (itemBatches.isEmpty()) return null;
        return new KP3DInformation(itemBatches.poll());
    }
}
