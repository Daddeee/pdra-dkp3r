package it.polimi.kp3d.core;

import it.polimi.algorithm.domain.Action;
import it.polimi.kp3d.domain.Placement;

import java.util.List;

public class KP3DAction implements Action {
    private final List<Placement> placements;
    public double reward;

    public KP3DAction(List<Placement> placements) {
        this.placements = placements;
        this.reward = 0;
        for (Placement placement : placements)
            this.reward += placement.getItem().getReward();
    }

    public List<Placement> getPlacements() {
        return placements;
    }

    public double getReward() {
        return reward;
    }
}
