import java.util.List;

public class Main {
    public static void main(String[] args){
        NetParser parser = new NetParser();
        List<Solution> solutions = parser.parse(Utility.WEIGHT_PATH, Utility.BIAS_PATH);
        Solution solution = solutions.get(7);
        InequalitiesSolver solver = InequalitiesSolver.instance;
        String result = solver.solveInequalities(solution.getInput_number(), solution.getConstraint());
        System.out.println(result);
        solver.close();
    }
}
