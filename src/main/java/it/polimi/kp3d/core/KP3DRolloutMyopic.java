package it.polimi.kp3d.core;

import it.polimi.algorithm.core.*;
import it.polimi.algorithm.domain.Action;
import it.polimi.algorithm.domain.State;
import it.polimi.kp3d.domain.Item;
import it.polimi.kp3d.domain.Placement;
import it.polimi.utils.Combination;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class KP3DRolloutMyopic extends RolloutPolicy {

    public KP3DRolloutMyopic(InformationSpaceFactory informationSpaceFactory,
                             Policy basePolicy,
                             TransitionFunction transitionFunction,
                             RewardFunction rewardFunction,
                             int m) {
        super(informationSpaceFactory, basePolicy, transitionFunction, rewardFunction, m);
    }

    @Override
    protected List<Action> getPossibleActions(State state) {
        KP3DState kp3DState = (KP3DState) state;
        Set<Item> toPack = kp3DState.getItemsToPack();
        // System.out.println("Evaluating " + toPack.size() + " items");
        int size = toPack.size();
        List<Action> actions = new ArrayList<>();
        for (int i=size; i>=0; i--) {
            List<Set<Item>> subsets = Combination.generateCombinations(toPack, i);
            for (Set<Item> subset : subsets) {
                List<Placement> placements = EMSHeuristic.getPlacements(kp3DState, subset);
                if (placements.size() == subset.size()) {
                    KP3DAction action = new KP3DAction(placements);
                    actions.add(action);
                }
            }
        }
        actions.add(new KP3DAction(new ArrayList<>()));
        return actions;
    }

    @Override
    protected Action emptyAction(State state) {
        return null;
    }
}
