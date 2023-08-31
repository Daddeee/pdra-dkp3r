package it.polimi.algorithm.core;

import it.polimi.algorithm.domain.Action;
import it.polimi.algorithm.domain.State;

public interface Policy {
    Action chooseAction(State state);
    String getName();
}
