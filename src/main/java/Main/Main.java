package Main;

import Logics.Calculator;
import Logics.InequalitiesSolver;
import Logics.NetParser;
import Logics.NeuralNet;
import Tool.FileProcesser;
import Tool.Solution;
import Tool.Utility;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Main {
    public static void main(String[] args){
        runCalculator(Utility.INPUT_PATH, runSolver(Utility.WEIGHT_PATH));
    }

    private static List<Solution> runSolver(String weight_path){
        List<double[][]> weights = FileProcesser.readParameter(weight_path);

        for (double[][] weight: weights) FileProcesser.recordLayer(weight, "weight");
        FileProcesser.writeToFile("\n");

        long startTime, endTime;

        FileProcesser.writeToFile("Begin parsing the network.");
        NetParser parser = new NetParser();
        startTime = System.currentTimeMillis();
        List<Solution> solutions = parser.parse(weights);
        endTime = System.currentTimeMillis();
        FileProcesser.writeToFile("Succeed in parsing the network in " + (endTime - startTime) / 1000.0 + "sec.\n");

        FileProcesser.writeToFile("Begin solving the inequalities.");
        InequalitiesSolver solver = new InequalitiesSolver();
        startTime = System.currentTimeMillis();
        solutions = solver.solveInequalities(solutions);
        endTime = System.currentTimeMillis();
        FileProcesser.writeToFile("===========================================\n" +
                "Succeed in solving the inequalities in " + (endTime - startTime) / 1000.0 + "sec.\n");

        startTime = System.currentTimeMillis();
        solver.showGraphics(solutions);
        endTime = System.currentTimeMillis();
        FileProcesser.writeToFile("\n===========================================\n" +
                "Succeed in showing the graphics in " + (endTime - startTime) / 1000.0 + "sec.\n");

        solver.close();

        return solutions;
    }

    private static void runCalculator(String input_path, List<Solution> solutions){
        List<double[]> inputs = FileProcesser.readInput(input_path);

        long startTime, endTime;

        Calculator calculator = new Calculator();
        FileProcesser.writeToFile("Begin calculating new inputs:\n");
        startTime = System.currentTimeMillis();
        for (double[] input: inputs) {
            FileProcesser.writeToFile(Arrays.toString(input) + ": ");

            Solution solution = calculator.selectSolution(solutions, input);
            if(solution == null){
                FileProcesser.writeToFile("No Solution Set Satisfied.\n");
            }else{
                FileProcesser.writeToFile(solution.getConstraint() + ": " + Arrays.toString(calculator.calculateOutout(solution.getObjectives(), input)) + "\n");
            }
        }
        endTime = System.currentTimeMillis();
        FileProcesser.writeToFile("Succeed in calculating new inputs in " + (endTime - startTime) / 1000.0 + "sec.\n");
    }

    private static void runNet(String input_path){
        List<double[]> inputs = FileProcesser.readInput(input_path);

        NeuralNet net = new NeuralNet();

        double[][] outputs = net.calculate(inputs.toArray(new double[0][0]));
        for (int i = 0; i < outputs.length; i++) {
            System.out.println(Arrays.toString(inputs.get(i)) + ": " + Arrays.toString(outputs[i]));
        }
    }

    private static List<double[][]> constructWeights(String layer_number){
        String[] ls = layer_number.split(" ");
        int[] ns = new int[ls.length + 1];
        ns[0] = 2;
        for (int i = 0; i < ls.length; ++i){
            ns[i + 1] = Integer.parseInt(ls[i]);
        }
        List<double[][]> weights = new ArrayList<>();

        for (int i = 1; i < ns.length; ++i){
            double[][] weight = new double[ns[i]][ns[i - 1] + 1];
            for (int j = 0; j < weight.length; ++j)
                for (int k = 0; k < weight[0].length; ++k)
                    weight[j][k] = 1 - Math.random();

            weights.add(weight);
        }

        return weights;
    }
}