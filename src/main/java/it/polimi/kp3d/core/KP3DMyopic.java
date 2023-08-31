package it.polimi.kp3d.core;

import it.polimi.algorithm.core.Policy;
import it.polimi.algorithm.domain.Action;
import it.polimi.algorithm.domain.State;
import it.polimi.kp3d.domain.Item;
import it.polimi.kp3d.domain.Placement;
import it.polimi.utils.Combination;

import java.util.List;
import java.util.Set;

public class KP3DMyopic implements Policy {

    @Override
    public Action chooseAction(State state) {
        KP3DState kp3DState = (KP3DState) state;
        Set<Item> toPack = kp3DState.getItemsToPack();
        // System.out.println("Evaluating " + toPack.size() + " items");
        int size = toPack.size();
        double bestReward = Double.NEGATIVE_INFINITY;
        Action bestAction = null;
        for (int i=size; i>=0; i--) {
            List<Set<Item>> subsets = Combination.generateCombinations(toPack, size);
            for (Set<Item> subset : subsets) {
                List<Placement> placements = EMSHeuristic.getPlacements(kp3DState, subset);
                if (placements.size() == subset.size()) {
                    KP3DAction action = new KP3DAction(placements);
                    double reward = action.getReward();
                    if (reward > bestReward) {
                        bestReward = reward;
                        bestAction = action;
                    }
                }
            }
        }
        return bestAction;
    }

    @Override
    public String getName() {
        return "myopic";
    }
}
