package Display;

import Tool.Solution;
import com.wolfram.jlink.KernelLink;

import java.util.List;

public class Graphics3D extends Graphics {
    public Graphics3D(List<Solution> solutions, KernelLink kl, int[][] range){
        super(kl, range);
        WINDOW_WIDTH = 1200;
        init(solutions);
    }

    protected String constructPrefix() {
        return "RegionPlot3D[";
    }

    protected String constructPostfix(int[][] range){
        if(range.length != 3 || range[0].length != 2)
            return null;

        StringBuilder sb = new StringBuilder(", {X1, ");
        sb.append(range[0][0]);
        sb.append(", ");
        sb.append(range[0][1]);
        sb.append("}, {X2, ");
        sb.append(range[1][0]);
        sb.append(", ");
        sb.append(range[1][1]);
        sb.append("}, {X3, ");
        sb.append(range[2][0]);
        sb.append(", ");
        sb.append(range[2][1]);
        sb.append("}]");
        return sb.toString();
    }

    protected void preprocess(List<Solution> solutions){
    }
}
