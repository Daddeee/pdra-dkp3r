package it.polimi.algorithm.core;

import it.polimi.algorithm.domain.Action;
import it.polimi.algorithm.domain.State;

public interface RewardFunction {
    double getReward(State state, Action action);
}
