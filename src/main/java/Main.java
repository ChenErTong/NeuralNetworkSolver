import java.util.List;

public class Main {
    public static void main(String[] args){
//        NetParser parser = new NetParser();
//        List<Solution> solutions = parser.parse(Utility.WEIGHT_PATH, Utility.BIAS_PATH);

        InequalitiesSolver solver = InequalitiesSolver.instance;
        solver.solveInequalities("Reduce[x*2. - 7. y*2. > 1. && x + y < 100., {x, y}, Reals]");
    }
}
