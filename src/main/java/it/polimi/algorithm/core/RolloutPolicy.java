package it.polimi.algorithm.core;

import it.polimi.algorithm.domain.Action;
import it.polimi.algorithm.domain.Information;
import it.polimi.algorithm.domain.State;

import java.util.List;

public abstract class RolloutPolicy implements Policy {

    protected InformationSpaceFactory informationSpaceFactory;
    protected Policy basePolicy;
    protected TransitionFunction transitionFunction;
    protected RewardFunction rewardFunction;
    protected int m;

    public RolloutPolicy(InformationSpaceFactory informationSpaceFactory,
                         Policy basePolicy,
                         TransitionFunction transitionFunction,
                         RewardFunction rewardFunction,
                         int m) {
        this.informationSpaceFactory = informationSpaceFactory;
        this.basePolicy = basePolicy;
        this.transitionFunction = transitionFunction;
        this.rewardFunction = rewardFunction;
        this.m = m;
    }

    @Override
    public Action chooseAction(State state) {
        List<Action> possibleActions = getPossibleActions(state);

        if (possibleActions.isEmpty())
            return emptyAction(state);

        if (possibleActions.size() == 1)
            return possibleActions.get(0);

        // System.out.println("Applying rollout with " + possibleActions.size() + " actions");

        Action best = null;
        double bestReward = Double.MIN_VALUE;
        for (Action action : possibleActions) {
            State init = state.clone();
            init = transitionFunction.preToPost(init, action);
            double v = 0;
            for (int i=0; i<m; i++) {
                State s = init.clone();
                InformationSpace path = informationSpaceFactory.build(s);
                while (!s.isAbsorbing()) {
                    // observe realization + post-to-pre transition
                    Information realization = path.getInformation(s);
                    if (realization == null)
                        break;
                    s = transitionFunction.postToPre(s, realization);
                    // select action according to policy
                    Action act = basePolicy.chooseAction(s);
                    if (act == null)
                        break;
                    // compute reward
                    double immediateReward = rewardFunction.getReward(s, act);
                    v += immediateReward / Math.max(m, 1);
                    // transition to post-decision state
                    s = transitionFunction.preToPost(s, act);
                }
            }
            double imm = rewardFunction.getReward(state, action);
            double r = imm + v;
            // System.out.println("Expanded action: " + action + ". Imm. Reward: " + imm + ". Exp. Avg. Reward: " + v);
            if (r > bestReward) {
                bestReward = r;
                best = action;
            }
        }

        return best;
    }

    @Override
    public String getName() {
        return "rollout";
    }

    protected abstract List<Action> getPossibleActions(State state);
    protected abstract Action emptyAction(State state);
}
