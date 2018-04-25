package Display;

import Tool.Solution;
import com.wolfram.jlink.KernelLink;

import java.util.List;

public class Graphics2D extends Graphics {
    public Graphics2D(List<Solution> solutions, KernelLink kl, int[][] range){
        super(kl, range);
        WINDOW_WIDTH = 800;
        init(solutions);
    }

    protected String constructPrefix() {
        return "Plot3D[Piecewise[{";
    }

    protected String constructPostfix(int[][] range){
        if(range.length != 2 || range[0].length != 2)
            return null;

        StringBuilder sb = new StringBuilder("}], {X1, ");
        sb.append(range[0][0]);
        sb.append(", ");
        sb.append(range[0][1]);
        sb.append("}, {X2, ");
        sb.append(range[1][0]);
        sb.append(", ");
        sb.append(range[1][1]);
        sb.append("}, ImageSize -> Large, PlotRange -> All, ColorFunction -> \"BlueGreenYellow\"]");
        return sb.toString();
    }

    protected void preprocess(List<Solution> solutions){
        int objective_number = solutions.get(0).getObjectives().length;
        formula_infix = new String[objective_number];
        objectives = new String[objective_number];
        for(int i = 0; i < objective_number; ++i){
            StringBuilder s = new StringBuilder();
            for (Solution solution: solutions) {
                s.append("{" + solution.getObjectives()[i] + ", " + solution.getConstraint() + "},");
            }
            formula_infix[i] = s.substring(0, s.length() - 1);
            objectives[i] = "输出" + (i + 1);
        }
    }
}