package it.polimi.kp3d.core;

import it.polimi.algorithm.core.RewardFunction;
import it.polimi.algorithm.domain.Action;
import it.polimi.algorithm.domain.State;

public class KP3DRewardFunction implements RewardFunction {
    @Override
    public double getReward(State state, Action action) {
        return ((KP3DAction) action).getReward();
    }
}
