package it.polimi.algorithm.core;

import it.polimi.algorithm.domain.State;

public interface InformationSpaceFactory {
    InformationSpace build(State s);
}
