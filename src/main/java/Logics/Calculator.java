package Logics;

import Tool.Solution;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Calculator {
    private Map<String, Boolean> checkCache;

    public Calculator(){
        checkCache = new HashMap<>();
    }

    public double[] calculateOutout(String[] objective, double[] input){
        double[] output = new double[objective.length];
        for (int i = 0; i < output.length; ++i){
            output[i] = calculateInput(objective[i], input);
        }
        return output;
    }

    public Solution selectSolution(List<Solution> solutions, double[] input){
        checkCache.clear();

        for (Solution solution: solutions) {
            if(meetConstraints(solution.getInput_number(), solution.getConstraints(), input)){
                return solution;
            }
        }

        return null;
    }

    private boolean meetConstraints(int input_number, String[] constraints, double[] input){
        if(input_number != input.length){
            return false;
        }

        for (String constraint: constraints) {
            if(!meetConstraints(constraint, input)){
                return false;
            }
        }

        return true;
    }

    private boolean meetConstraints(String constraint, double[] input){
        if(!checkCache.containsKey(constraint)){
            checkCache.put(constraint, calculateInput(constraint.substring(0, constraint.length() - 4), input) >= 0);
        }
        return checkCache.get(constraint);
    }

    private double calculateInput(String objective, double[] input){
        String[] paras = objective.split(" \\+ ");
        double left = Double.parseDouble(paras[input.length]);
        for (int i = 0; i < input.length; ++i){
            left += (Double.parseDouble(paras[i].split(" \\* ")[0]) * input[i]);
        }
        return left;
    }
}
