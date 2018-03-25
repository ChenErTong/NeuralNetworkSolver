package Main;

import Logics.InequalitiesSolver;
import Logics.NetParser;
import Tool.Solution;
import Tool.FileProcesser;
import Tool.Utility;

import java.util.List;

public class Main {
    public static void main(String[] args){
        List<double[][]> weights = FileProcesser.readParameter(Utility.WEIGHT_PATH);
        List<double[][]> biases = FileProcesser.readParameter(Utility.BIAS_PATH);
        for (double[][] weight: weights) FileProcesser.recordLayer(weight, "weight");
        for (double[][] bias: biases) FileProcesser.recordLayer(bias, "bias");

        NetParser parser = new NetParser();
        List<Solution> solutions = parser.parse(weights, biases);
        FileProcesser.writeToFile("Succeed in parsing the network:\n");
        FileProcesser.recordSolution(solutions);

        InequalitiesSolver solver = InequalitiesSolver.instance;
        solutions = solver.solveInequalities(solutions);
        FileProcesser.writeToFile("Succeed in solving the inequalities:\n");
        FileProcesser.recordSolution(solutions);

        FileProcesser.closeFile();
        System.out.print("Finish processing the program.");
    }
}