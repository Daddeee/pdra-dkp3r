package it.polimi.kp3d.instances;

import it.polimi.kp3d.domain.Item;
import it.polimi.utils.JSONWriter;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Random;
import java.util.Set;

public class KP3DGenerator {

    private final Random random;
    private int minNumBatches = 10;
    private int maxNumBatches = 20;
    private int minBatchSize = 1;
    private int maxBatchSize = 8;
    private int minEdge = 120;
    private int maxEdge = 400;
    private double rewardExpected = 100;
    private double rewardDeviation = 20;

    public KP3DGenerator(Random random, int minNumBatches, int maxNumBatches, int minBatchSize, int maxBatchSize,
                         int minEdge, int maxEdge, double rewardExpected, double rewardDeviation) {
        this.random = random;
        this.minNumBatches = minNumBatches;
        this.maxNumBatches = maxNumBatches;
        this.minBatchSize = minBatchSize;
        this.maxBatchSize = maxBatchSize;
        this.minEdge = minEdge;
        this.maxEdge = maxEdge;
        this.rewardExpected = rewardExpected;
        this.rewardDeviation = rewardDeviation;
    }

    public KP3DGenerator(Random random) {
        this.random = random;
    }

    public int getMaxBatchSize() {
        return maxBatchSize;
    }

    public int getMinBatchSize() {
        return minBatchSize;
    }

    public int getMaxNumBatches() {
        return maxNumBatches;
    }

    public int getMinNumBatches() {
        return minNumBatches;
    }

    public KP3DInstance generate(int id) {
        int numBatches = sampleUniform(minNumBatches, maxNumBatches);
        return generate(id, numBatches);
    }

    public KP3DInstance generate(int id, int numBatches) {
        int w = 1200, d = 800, h = 2000;
        LinkedList<Set<Item>> items = new LinkedList<>();
        int idItem = 0;
        for (int i = 0; i< numBatches; i++) {
            int batchSize = sampleUniform(minBatchSize, maxBatchSize);
            Set<Item> batch = new HashSet<>(batchSize);
            for (int j=0; j<batchSize; j++) {
                int wItem = sampleUniform(minEdge, maxEdge);
                int dItem = sampleUniform(minEdge, maxEdge);
                int hItem = sampleUniform(minEdge, maxEdge);
                double reward = sampleNormal(rewardExpected, rewardDeviation);
                batch.add(new Item(idItem++, reward, wItem, dItem, hItem));
            }
            items.add(batch);
        }
        return new KP3DInstance(id, w, d, h, items);
    }

    public int sampleUniform(int min, int max) {
        return random.nextInt(max - min + 1) + min;
    }

    private double sampleNormal(double expected, double deviation) {
        return random.nextGaussian() * deviation + expected;
    }

    public static void main(String[] args) {
        int seed = 1337;
        int numInstances = 50;
        String basePath = "instances/kp3d/";
        KP3DGenerator generator = new KP3DGenerator(new Random(seed),
                50, 100,
                1, 8,
                120, 500,
                100, 20);
        for (int i=0; i<numInstances; i++) {
            KP3DInstance instance = generator.generate(i);
            JSONWriter.write(instance, basePath + instance.getId() + ".json");
        }
    }
}
