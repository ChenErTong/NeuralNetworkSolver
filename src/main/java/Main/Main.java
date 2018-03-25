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
        List<double[][]> weights = FileProcesser.readParameter(Utility.WEIGHT_PATH);
        List<double[][]> biases = FileProcesser.readParameter(Utility.BIAS_PATH);
        List<double[]> inputs = FileProcesser.readInput(Utility.INPUT_PATH);
        FileProcesser.writeToFile("Succeed in reading the parameters\n");

        for (double[][] weight: weights) FileProcesser.recordLayer(weight, "weight");
        for (double[][] bias: biases) FileProcesser.recordLayer(bias, "bias");
        FileProcesser.writeToFile("\n");

        NetParser parser = new NetParser();
        List<Solution> solutions = parser.parse(weights, biases);
        FileProcesser.writeToFile("Succeed in parsing the network:\n");
        FileProcesser.recordSolution(solutions);

        InequalitiesSolver solver = InequalitiesSolver.instance;
        solutions = solver.solveInequalities(solutions, false);
        FileProcesser.writeToFile("===========================================\n" +
                "Succeed in solving the inequalities:\n");
        FileProcesser.recordSolution(solutions);


        Calculator calculator = new Calculator();
        FileProcesser.writeToFile("Begin calculating new inputs:\n");
        for (double[] input: inputs) {
            FileProcesser.writeToFile(Arrays.toString(input) + ": ");

            Solution solution = calculator.calculateResult(solutions, input);
            if(solution == null){
                FileProcesser.writeToFile("No Solution Set Satisfied.\n");
            }else{
                FileProcesser.writeToFile(solution.getSolutionSet() + ": " + Double.toString(calculator.calculateInput(solution.getObjective(), input)) + "\n");
            }
        }

        FileProcesser.closeFile();
        System.out.print("\nFinish processing the program.");
    }
}