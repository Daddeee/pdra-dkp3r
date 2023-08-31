package it.polimi.kp3d.core;

import it.polimi.algorithm.core.InformationSpace;
import it.polimi.algorithm.core.InformationSpaceFactory;
import it.polimi.algorithm.domain.State;
import it.polimi.kp3d.instances.KP3DGenerator;
import it.polimi.kp3d.instances.KP3DInstance;

public class KP3DRandomSpaceFactory implements InformationSpaceFactory {
    private final KP3DGenerator generator;

    public KP3DRandomSpaceFactory(KP3DGenerator generator) {
        this.generator = generator;
    }

    @Override
    public InformationSpace build(State s) {
        KP3DState state = (KP3DState) s;
        int maxNumBatches;
        if (generator.getMaxNumBatches() > state.getNumBatches())
            maxNumBatches = generator.sampleUniform(state.getNumBatches(),
                    generator.getMaxNumBatches() + (generator.getMaxNumBatches() - state.getNumBatches()));
        else
            maxNumBatches = generator.getMaxNumBatches();
        int numBatches = maxNumBatches - state.getNumBatches();
        KP3DInstance instance = generator.generate(-1, numBatches);
        return new KP3DInformationSpace(instance.getItems());
    }
}
