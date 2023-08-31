package it.polimi.kp3d.core;

import it.polimi.algorithm.core.TransitionFunction;
import it.polimi.algorithm.domain.Action;
import it.polimi.algorithm.domain.Information;
import it.polimi.algorithm.domain.State;
import it.polimi.kp3d.domain.Item;
import it.polimi.kp3d.domain.Placement;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;

public class KP3dTransitionFunction implements TransitionFunction {
    @Override
    public State preToPost(State state, Action action) {
        KP3DAction kp3DAction = (KP3DAction) action;
        KP3DState kp3DState = (KP3DState) state;
        Set<Item> placed = new HashSet<>();
        for (Placement placement : kp3DAction.getPlacements()) {
            kp3DState.place(placement);
            placed.add(placement.getItem());
        }
        kp3DState.getItemsToPack().removeIf(Predicate.not(placed::contains));
        // System.out.println("Placed " + placed.size() + " items");
        return kp3DState;
    }

    @Override
    public State postToPre(State state, Information information) {
        KP3DState kp3DState = (KP3DState) state;
        KP3DInformation kp3DInformation = (KP3DInformation) information;
        for (Item item : kp3DInformation.getNewItems())
            kp3DState.getItemsToPack().add(item);
        // System.out.println("Observed " + kp3DInformation.getNewItems().size() + " items");
        kp3DState.setNumBatches(kp3DState.getNumBatches() + 1);
        return kp3DState;
    }
}
