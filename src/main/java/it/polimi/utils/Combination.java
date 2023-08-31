package it.polimi.utils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Combination {

    public static <T> List<Set<T>> generateCombinations(Set<T> set, int r) {
        List<T> inputList = new ArrayList<>(set);
        List<Set<T>> result = new ArrayList<>();
        generateCombinations(inputList, r, 0, new HashSet<>(), result);
        return result;
    }

    private static <T> void generateCombinations(List<T> inputList, int r, int index, Set<T> current, List<Set<T>> result) {
        if (current.size() == r) {
            result.add(new HashSet<>(current));
            return;
        }

        if (index == inputList.size()) {
            return;
        }

        T element = inputList.get(index);
        current.add(element);
        generateCombinations(inputList, r, index + 1, current, result);
        current.remove(element);
        generateCombinations(inputList, r, index + 1, current, result);
    }

}
