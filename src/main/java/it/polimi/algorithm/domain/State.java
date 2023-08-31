package it.polimi.algorithm.domain;

public interface State {
    boolean isAbsorbing();

    State clone();
}
