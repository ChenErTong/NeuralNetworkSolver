package Main;

import Logics.Calculator;
import Logics.InequalitiesSolver;
import Logics.NetParser;
import Logics.NeuralNet;
import Tool.Solution;
import Tool.FileProcesser;
import Tool.Utility;

import java.util.Arrays;
import java.util.List;

public class Main {
    public static void main(String[] args){
//        test();
//        System.exit(0);

        long openTime, closeTime;
        openTime = System.currentTimeMillis();
        List<double[][]> weights = FileProcesser.readParameter(Utility.WEIGHT_PATH);
        List<double[]> inputs = FileProcesser.readInput(Utility.INPUT_PATH);

        for (double[][] weight: weights) FileProcesser.recordLayer(weight, "weight");
        FileProcesser.writeToFile("\n");

//        runNet(inputs);
        runSolver(weights, inputs);

        closeTime = System.currentTimeMillis();
        FileProcesser.writeToFile("\nFinish processing the program. Total Duratoin: " + (closeTime - openTime) / 1000.0 + "sec.");
        FileProcesser.closeFile();
    }

    private static void test(){
        String constraint = "-0.10328118855979174 * X1 + -0.05012250887588361 * X2 + 0.3111099132609237 * X3 + -0.03294619970281937 > 0 && " +
                "3.045782833549556 * X1 + 2.98287477081037 * X2 + 2.9530057302209523 * X3 + -4.523606517610982 > 0 && " +
                "-0.5326376419620387 * X1 + -0.6929153260164957 * X2 + -0.5450808952511463 * X3 + 0.7680458780188641 > 0 && " +
                "0.6527082065569729 * X1 + 0.2539050232858495 * X2 + -0.1942019675675709 * X3 + 0.10899790676739256 > 0";
        InequalitiesSolver solver = InequalitiesSolver.instance;
        System.out.println(solver.solveInequality(3, constraint));
    }

    private static void runSolver(List<double[][]> weights, List<double[]> inputs){
        long startTime, endTime;

        FileProcesser.writeToFile("Begin parsing the network.");
        NetParser parser = new NetParser();
        startTime = System.currentTimeMillis();
        List<Solution> solutions = parser.parse(weights);
        endTime = System.currentTimeMillis();
        FileProcesser.writeToFile("Succeed in parsing the network in " + (endTime - startTime) / 1000.0 + "sec.\n");
        FileProcesser.recordSolution(solutions);

        FileProcesser.writeToFile("Begin solving the inequalities.");
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

    private static void runNet(List<double[]> inputs){
        NeuralNet net = new NeuralNet();

        double[][] outputs = net.calculate(inputs.toArray(new double[0][0]));
        for (int i = 0; i < outputs.length; i++) {
            System.out.println(Arrays.toString(inputs.get(i)) + ": " + Arrays.toString(outputs[i]));
        }
    }
}