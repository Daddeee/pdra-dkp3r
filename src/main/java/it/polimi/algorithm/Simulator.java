package it.polimi.algorithm;

import it.polimi.algorithm.core.InformationSpace;
import it.polimi.algorithm.core.Policy;
import it.polimi.algorithm.core.RewardFunction;
import it.polimi.algorithm.core.TransitionFunction;
import it.polimi.algorithm.domain.Action;
import it.polimi.algorithm.domain.Information;
import it.polimi.algorithm.domain.State;

public class Simulator {
    private final Policy policy;
    private final RewardFunction rewardFunction;
    private final InformationSpace informationSpace;
    private final TransitionFunction transitionFunction;

    public Simulator(Policy policy, RewardFunction rewardFunction, InformationSpace informationSpace,
                     TransitionFunction transitionFunction) {
        this.policy = policy;
        this.rewardFunction = rewardFunction;
        this.informationSpace = informationSpace;
        this.transitionFunction = transitionFunction;
    }

    private State bestState;

    public State getBestState() {
        return bestState;
    }

    public double simulate(State initial, int instanceId) {
        State state = initial;
        int epoch = 0;
        double totalReward = 0;
        double start = System.nanoTime();
        while (!state.isAbsorbing()) {
            epoch += 1;

            // select action according to policy
            Action action = policy.chooseAction(state);

            // no action available
            if (action == null)
                break;

            // compute reward
            double immediateReward = rewardFunction.getReward(state, action);
            totalReward += immediateReward;

            // transition to post-decision state
            state = transitionFunction.preToPost(state, action);

            // observe realization
            Information realization = informationSpace.getInformation(state);

            // no more realizations available
            if (realization == null)
                break;

            // transition to next pre-decision state
            state = transitionFunction.postToPre(state, realization);

            int time = (int) Math.round((System.nanoTime() - start) / 1e9);
            System.out.printf("[Instance %d][Epoch %d][Secs %s] Total reward: %f%n", instanceId, epoch, time,
                    totalReward);
        }
        this.bestState = state;
        return totalReward;
    }
}
