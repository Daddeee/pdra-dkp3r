package it.polimi.algorithm.core;

import it.polimi.algorithm.domain.Information;
import it.polimi.algorithm.domain.State;

public interface InformationSpace {
    Information getInformation(State state);
}
