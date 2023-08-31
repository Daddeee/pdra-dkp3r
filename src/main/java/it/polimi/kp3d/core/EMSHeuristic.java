package it.polimi.kp3d.core;

import it.polimi.kp3d.domain.Cube;
import it.polimi.kp3d.domain.Item;
import it.polimi.kp3d.domain.Placement;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class EMSHeuristic {

    public static List<Placement> getPlacements(KP3DState state, Set<Item> candidates) {
        Set<Item> items = new HashSet<>(candidates);
        Set<Cube> emptyMaximalSpaces = new HashSet<>(state.getEmptyMaximalSpaces());

        List<Placement> placements = new ArrayList<>();
        while (!items.isEmpty()) {
            Placement bestPlacement = null;
            for (Item item : items) {
                Placement placement = getPlacement(item, emptyMaximalSpaces);
                if (placement != null) {
                    if (bestPlacement ==  null || bestPlacement.getItem().getReward() < placement.getItem().getReward())
                        bestPlacement = placement;
                }
            }
            if (bestPlacement == null)
                break;
            placements.add(bestPlacement);
            items.remove(bestPlacement.getItem());
            KP3DState.updateEmptyMaximalSpaces(bestPlacement, emptyMaximalSpaces);
        }
        return placements;
    }

    public static Placement getPlacement(Item item, Set<Cube> emptyMaximalSpaces) {
        long bestResidualVolume = Long.MAX_VALUE;
        Cube bestSpace = null, bestCube = null;
        for (Cube space : emptyMaximalSpaces) {
            Cube itemCube = new Cube(item.getW(), item.getD(), item.getH(), space.getX(), space.getY(), space.getZ());
            if (space.contains(itemCube)) {
                long residualVolume = space.getVolume() - item.getVolume();
                if (residualVolume < bestResidualVolume) {
                    bestResidualVolume = residualVolume;
                    bestSpace = space;
                    bestCube = itemCube;
                }
            }
            Cube rotatedCube = new Cube(item.getD(), item.getW(), item.getH(), space.getX(), space.getY(), space.getZ());
            if (space.contains(rotatedCube)) {
                long residualVolume = space.getVolume() - rotatedCube.getVolume();
                if (residualVolume < bestResidualVolume) {
                    bestResidualVolume = residualVolume;
                    bestSpace = space;
                    bestCube = rotatedCube;
                }
            }
        }
        if (bestSpace == null)
            return null;
        return new Placement(item, bestCube, bestSpace);
    }

}
