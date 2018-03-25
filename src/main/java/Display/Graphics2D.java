package Display;

import com.wolfram.jlink.KernelLink;

import java.util.Map;

public class Graphics2D extends Graphics {
    public Graphics2D(Map<String, String> solutions, KernelLink kl, int[][] range){
        super(kl, range);
        WINDOW_WIDTH = 1000;
        init(solutions);
    }

    protected String constructPrefix() {
        return "RegionPlot[";
    }

    protected String constructPostfix(int[][] range){
        if(range.length != 2 || range[0].length != 2)
            return null;

        StringBuilder sb = new StringBuilder(", {X1, ");
        sb.append(range[0][0]);
        sb.append(", ");
        sb.append(range[0][1]);
        sb.append("}, {X2, ");
        sb.append(range[1][0]);
        sb.append(", ");
        sb.append(range[1][1]);
        sb.append("}]");
        return sb.toString();
    }
}
