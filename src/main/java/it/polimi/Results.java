package it.polimi;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class Results {
    private final Map<Integer, Map<String, Double>> resultsPerInstance;
    private final Set<String> policies;

    public Results() {
        this.resultsPerInstance = new HashMap<>();
        this.policies = new HashSet<>();
    }

    public void addResult(int instance, String policy, double result) {
        if (!resultsPerInstance.containsKey(instance)) {
            resultsPerInstance.put(instance, new HashMap<>());
        }
        resultsPerInstance.get(instance).put(policy, result);
        policies.add(policy);
    }

    public String toCsv() {
        List<String> sortedPolicies = new ArrayList<>(this.policies);
        sortedPolicies.sort(String::compareTo);

        StringBuilder sb = new StringBuilder();
        sb.append("instance");
        for (String policy : sortedPolicies) {
            sb.append(",").append(policy);
        }
        sb.append("\n");
        for (int instance : resultsPerInstance.keySet()) {
            sb.append(instance);
            for (String policy : sortedPolicies) {
                sb.append(",").append(String.format("%.2f", resultsPerInstance.get(instance).get(policy)));
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    public void printCsv(String filepath) {
        try {
            File solutionFile = new File(filepath);
            solutionFile.getParentFile().mkdirs();
            solutionFile.createNewFile();
            BufferedWriter writer = new BufferedWriter(new FileWriter(solutionFile));
            writer.write(toCsv());
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
