package Display;

import Tool.Solution;
import Tool.Utility;
import com.wolfram.jlink.KernelLink;

import java.util.List;

public class GraphicsForTest extends Graphics {
    public GraphicsForTest(KernelLink kl){
        super(kl, Utility.RANGE2D);
        WINDOW_WIDTH = 800;
        init(null);
    }

    protected String constructPrefix() {
        return "Plot3D[Piecewise[{";
    }

    protected String constructPostfix(int[][] range){
        return "}], {X1, -100, 100}, {X2, -100, 100}, ImageSize -> Large, \n" +
                " PlotRange -> All, ColorFunction -> \"BlueGreenYellow\"]";
    }

    protected void preprocess(List<Solution> solutions){
        formula_infix = new String[1];
        objectives = new String[1];
        formula_infix[0] = "{X1 - X2, X1 + X2 < -100},\n" +
                "       {2 (X1 - X2), -100 <= X1 + X2 < 0},\n" +
                "{3 (X1 - X2), 0 <= X1 + X2 < 100},\n" +
                "{4 (X1 - X2), X1 + X2 >= 100}";
        objectives[0] = "输出1";
    }
}