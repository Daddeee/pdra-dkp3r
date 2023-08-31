package it.polimi.kp3d;

import com.google.gson.Gson;
import it.polimi.Results;
import it.polimi.algorithm.Simulator;
import it.polimi.algorithm.core.InformationSpace;
import it.polimi.algorithm.core.Policy;
import it.polimi.algorithm.core.RewardFunction;
import it.polimi.algorithm.core.TransitionFunction;
import it.polimi.algorithm.domain.State;
import it.polimi.kp3d.core.*;
import it.polimi.kp3d.instances.KP3DGenerator;
import it.polimi.kp3d.instances.KP3DInstance;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class Simulate {
    public static void main(String[] args) throws IOException {
        String instancesPath = "instances/kp3d/";
        Path instancesDir = Path.of(instancesPath);
        Results results = new Results();
        List<String> filenames = Files.walk(instancesDir)
                .map(p -> p.getFileName().toString())
                .filter(f -> f.endsWith(".json"))
                .sorted().collect(Collectors.toList());
        List<KP3DInstance> instances = new ArrayList<>();
        Gson gson = new Gson();
        for (String filename : filenames) {
            String filepath = instancesPath + filename;
            BufferedReader reader = new BufferedReader(new FileReader(filepath));
            KP3DInstance instance = gson.fromJson(reader, KP3DInstance.class);
            instances.add(instance);
            reader.close();
        }
        instances.sort(Comparator.comparingInt(KP3DInstance::getId));
        double avgMyopicReward = 0;
        double avgRolloutReward = 0;
        int numInstances = 0;
        for (KP3DInstance instance : instances) {
            System.out.println(instance.getItems().size());
            if (System.nanoTime() > 0) continue;
            numInstances++;
            RewardFunction rewardFunction = new KP3DRewardFunction();
            TransitionFunction transitionFunction = new KP3dTransitionFunction();
            avgMyopicReward = simulateMyopic(results, avgMyopicReward, instance, rewardFunction, transitionFunction);
            avgRolloutReward = simulateRolloutMyopic(results, avgRolloutReward, instance, rewardFunction, transitionFunction);
        }
        System.out.printf("Average myopic reward: %f%n", avgMyopicReward / numInstances);
        System.out.printf("Average rollout reward: %f%n", avgRolloutReward / numInstances);

        results.printCsv("results/kp3d/policies.csv");
    }

    private static double simulateMyopic(Results results, double avgMyopicReward, KP3DInstance instance,
                                         RewardFunction rewardFunction, TransitionFunction transitionFunction) {
        InformationSpace informationSpace = new KP3DInformationSpace(instance.getItems());
        Policy policy = new KP3DMyopic();
        Simulator simulator = new Simulator(policy, rewardFunction, informationSpace, transitionFunction);
        State initial = new KP3DState(instance.getW(), instance.getD(), instance.getH());
        double start = System.nanoTime();
        double reward = simulator.simulate(initial, instance.getId());
        double end = System.nanoTime();
        double seconds = (end - start) / 1e9;
        KP3DState state = (KP3DState) simulator.getBestState();
        long binVol = (long) instance.getW() * instance.getD() * instance.getH();
        long usedVol = state.getPackedVolume();
        double volRatio = (double) usedVol / binVol;
        avgMyopicReward += reward;
        results.addResult(instance.getId(), policy.getName(), reward);
        results.addResult(instance.getId(), policy.getName() + "-seconds", seconds);
        results.addResult(instance.getId(), policy.getName() + "-volratio", volRatio);
        System.out.printf("[Myopic %d] Total reward: %f%n", instance.getId(), reward);
        return avgMyopicReward;
    }

    private static double simulateRolloutMyopic(Results results, double avgRolloutMyopicReward, KP3DInstance instance,
                                                RewardFunction rewardFunction, TransitionFunction transitionFunction) {
        InformationSpace informationSpace = new KP3DInformationSpace(instance.getItems());
        Policy policy = new KP3DRolloutMyopic(
                new KP3DRandomSpaceFactory(new KP3DGenerator(new Random(1338))),
                new KP3DMyopic(),
                transitionFunction,
                rewardFunction,
                5
        );
        Simulator simulator = new Simulator(policy, rewardFunction, informationSpace, transitionFunction);
        State initial = new KP3DState(instance.getW(), instance.getD(), instance.getH());
        double start = System.nanoTime();
        double reward = simulator.simulate(initial, instance.getId());
        double end = System.nanoTime();
        double seconds = (end - start) / 1e9;
        KP3DState state = (KP3DState) simulator.getBestState();
        long binVol = (long) instance.getW() * instance.getD() * instance.getH();
        long usedVol = state.getPackedVolume();
        double volRatio = (double) usedVol / binVol;
        avgRolloutMyopicReward += reward;
        results.addResult(instance.getId(), policy.getName(), reward);
        results.addResult(instance.getId(), policy.getName() + "-seconds", seconds);
        results.addResult(instance.getId(), policy.getName() + "-volratio", volRatio);
        System.out.printf("[Rollout %d] Total reward: %f%n", instance.getId(), reward);
        return avgRolloutMyopicReward;
    }
}
