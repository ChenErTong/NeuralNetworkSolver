import com.wolfram.jlink.*;

import java.util.*;

public class InequalitiesSolver {
    public static InequalitiesSolver instance = new InequalitiesSolver();
    private static KernelLink kernelLink;
    private Stack<MLTK> stack = new Stack<MLTK>();

    private InequalitiesSolver(){
        if (kernelLink == null){
            try {
                kernelLink = MathLinkFactory.createKernelLink("-linkmode launch -linkname '" + Utility.MATHEMATICA_PATH + "'");
                kernelLink.discardAnswer();
            } catch (MathLinkException e) {
                System.out.println("Link could not be created: " + e.getMessage());
            }
        }
    }

    public List<Solution> solveInequalities(List<Solution> solutions){
        List<Solution> result = new ArrayList<Solution>();
        String formula;

        for (Solution solution: solutions){
            if(!(formula = solveInequalities(solution.getInput_number(), solution.getConstraint())).equals("Impossible")){
                result.add(new Solution(solution.getInput_number(), solution.getObjective(), formula));
            }
        }

        if(result.size() > 0){
            showResult(result);
        }

        return result;
    }

    public void showResult(List<Solution> solutions){

    }

    public void showResultIn2D(String formula){
        GraphicsApp graphicsApp = new GraphicsApp(kernelLink);
        graphicsApp.setMathCommand(formula);
    }

    public void showResultIn3D(String formula){
        GraphicsApp graphicsApp = new GraphicsApp(kernelLink);
        graphicsApp.setMathCommand(formula);
    }

    public String solveInequalities(int input_number, String constraint){
        return solveInequalities(convertToReduceFormula(input_number, constraint));
    }

    public String solveInequalities(String formula){
//        System.out.println("Reduce Formula: " + formula);

        stack.clear();
        StringBuilder sb = new StringBuilder();
        try {
            kernelLink.evaluate(formula);
            kernelLink.waitForAnswer();
            do {
                int type = kernelLink.getType();
                if(type == 70){
                    sb.append(processFunction(kernelLink.getFunction()));
                }else if(type == 35){
                    sb.append(processSymbol(kernelLink.getSymbol()));
                }else if(type == 43){
                    sb.append(processNumerical(kernelLink.getInteger()));
                }else if(type == 42){
                    sb.append(processNumerical(kernelLink.getDouble()));
                }else{
                    kernelLink.discardAnswer();
                    stack.clear();
                    System.out.println("Unexpected Type: " + type);
                }
            }while (!stack.empty());
        } catch (MathLinkException e) {
            System.out.println("MathLinkException occurred: " + e.getMessage());
        }

        return sb.length() == 0 ? "Impossible" : sb.toString();
    }

    public void close(){
        kernelLink.close();
    }

    private String processFunction(MLFunction function){
//        System.out.println("Function: " + function.name + ", " + function.argCount);
        MLTK mltk = new MLTK(function.name, function.argCount);
        boolean higherPriority = higherPriority(mltk);
        stack.push(mltk);
        return higherPriority ? "" : "(";
    }

    private String processSymbol(String symbol){
        if(symbol.equals("False")) return "";

//        System.out.println("Symbol: " + symbol);
        if(Utility.SYMBOLS.containsKey(symbol))
            return process(Utility.SYMBOLS.get(symbol));
        else
            return process(symbol);
    }

    private String processNumerical(int numerical){
//        System.out.println("Numerical: " + numerical);
        return process(Integer.toString(numerical));
    }

    private String processNumerical(double numerical){
//        System.out.println("Numerical: " + numerical);
        return process(String.format("%.2f", numerical));
    }

    private String process(String name){
        StringBuilder sb = new StringBuilder(name);
        while(!stack.empty()){
            if (--stack.peek().remain_operator_num > 0){
                sb.append(stack.peek().name);
                break;
            }else {
                MLTK mltk = stack.pop();
                if (!higherPriority(mltk))
                    sb.append(")");
            }
        }

        return sb.toString();
    }

    private boolean higherPriority(MLTK mltk){
        return stack.empty() || Utility.Priority.get(mltk.name) >= Utility.Priority.get(stack.peek().name);
    }

    private String convertToReduceFormula(int input_number, String constraint){
        StringBuilder sb = new StringBuilder("Reduce[");
        sb.append(constraint);
        sb.append(", {");
        for (int i = 1; i <= input_number; ++i)
            sb.append("X" + i + ",");
        sb.append("}, Reals]");
        return sb.toString();
    }

    private String convertToRegionPlotFormulaIn2D(String formula, int[][] range){
        if(range.length != 2 || range[0].length != 2)
            return null;

        StringBuilder sb = new StringBuilder("RegionPlot[");
        sb.append(formula);
        sb.append(", {X1, ");
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

    private String convertToRegionPlotFormulaIn3D(String formula, int[][] range){
        if(range.length != 3 || range[0].length != 2)
            return null;

        StringBuilder sb = new StringBuilder("RegionPlot3D[");
        sb.append(formula);
        sb.append(", {X1, ");
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

    class MLTK{
        String name;
        int remain_operator_num;

        MLTK(String n, int ac){
            name = Utility.SYMBOLS.get(n);
            remain_operator_num = ac;
        }
    }
}