package Main;

import Display.Graphics2D;
import Logics.Calculator;
import Logics.InequalitiesSolver;
import Logics.NetParser;
import Tool.Solution;
import Tool.FileProcesser;
import Tool.Utility;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main {
    public static void main(String[] args){
        long openTime, closeTime, startTime, endTime;
        openTime = startTime = System.currentTimeMillis();
        List<double[][]> weights = FileProcesser.readParameter(Utility.WEIGHT_PATH);
        List<double[][]> biases = FileProcesser.readParameter(Utility.BIAS_PATH);
        List<double[]> inputs = FileProcesser.readInput(Utility.INPUT_PATH);
        endTime = System.currentTimeMillis();
        FileProcesser.writeToFile("Succeed in reading the parameters in " + (endTime - startTime) / 1000.0 + "sec.\n");

        for (double[][] weight: weights) FileProcesser.recordLayer(weight, "weight");
        for (double[][] bias: biases) FileProcesser.recordLayer(bias, "bias");
        FileProcesser.writeToFile("\n");

        NetParser parser = new NetParser();
        startTime = System.currentTimeMillis();
        List<Solution> solutions = parser.parse(weights, biases);
        endTime = System.currentTimeMillis();
        FileProcesser.writeToFile("Succeed in parsing the network in " + (endTime - startTime) / 1000.0 + "sec.\n");
        FileProcesser.recordSolution(solutions);

        InequalitiesSolver solver = InequalitiesSolver.instance;
        startTime = System.currentTimeMillis();
        solutions = solver.solveInequalities(solutions, Utility.SHOW_GRAPH);
        endTime = System.currentTimeMillis();
        FileProcesser.writeToFile("===========================================\n" +
                "Succeed in solving the inequalities in " + (endTime - startTime) / 1000.0 + "sec.\n");
        FileProcesser.recordSolution(solutions);

        Calculator calculator = new Calculator();
        FileProcesser.writeToFile("Begin calculating new inputs:\n");
        startTime = System.currentTimeMillis();
        for (double[] input: inputs) {
            FileProcesser.writeToFile(Arrays.toString(input) + ": ");

            Solution solution = calculator.calculateResult(solutions, input);
            if(solution == null){
                FileProcesser.writeToFile("No Solution Set Satisfied.\n");
            }else{
                FileProcesser.writeToFile(solution.getSolutionSet() + ": " + Double.toString(calculator.calculateInput(solution.getObjective(), input)) + "\n");
            }
        }
        closeTime = endTime = System.currentTimeMillis();
        FileProcesser.writeToFile("Succeed in calculating new inputs in " + (endTime - startTime) / 1000.0 + "sec.\n");

        FileProcesser.closeFile();
        FileProcesser.writeToFile("\nFinish processing the program. Total Duratoin: " + (closeTime - openTime) / 1000.0 + "sec.");
    }
}