package it.polimi.algorithm.core;

import it.polimi.algorithm.domain.Action;
import it.polimi.algorithm.domain.Information;
import it.polimi.algorithm.domain.State;

public interface TransitionFunction {
    State preToPost(State state, Action action);
    State postToPre(State state, Information information);
}
