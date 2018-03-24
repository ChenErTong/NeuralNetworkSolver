import com.wolfram.jlink.*;

import java.util.Stack;

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

    public String solveInequalities(int input_number, String constraint){
        return solveInequalities(convertToReduceFormula(input_number, constraint));
    }

    public String solveInequalities(String formula){
        System.out.println("Reduce Formula: " + formula);

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
            }while (true);
        } catch (MathLinkException e) {
            System.out.println("MathLinkException occurred: " + e.getMessage());
        }

        return sb.length() == 0 ? "Impossible" : sb.toString();
    }

    public void close(){
        kernelLink.close();
    }

    private String processFunction(MLFunction function){
        System.out.println("Function: " + function.name + ", " + function.argCount);
        return "";
    }

    private String processSymbol(String symbol){
        if(symbol.equals("False"))
            return "";

        System.out.println("Symbol: " + symbol);
        return "";
    }

    private String processNumerical(int numerical){
        System.out.println("Numerical: " + numerical);
        return "";
    }

    private String processNumerical(double numerical){
        System.out.println("Numerical: " + numerical);
        return "";
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

    class MLTK{
        String symbol;
        int remain_operator_num;
    }
}