import com.wolfram.jlink.*;

import java.util.Stack;

public class InequalitiesSolver {
    public static InequalitiesSolver instance = new InequalitiesSolver();
    private static KernelLink kernelLink;

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

    public void solveInequalities(int input_number, String constraint){
        solveInequalities(convertToReduceFormula(input_number, constraint));
    }

    public void solveInequalities(String formula){

        try {
            kernelLink.evaluate(formula);
            kernelLink.waitForAnswer();
            while(true){
                int type = kernelLink.getType();
                if(type == 70){
                    MLFunction function = kernelLink.getFunction();
                    System.out.println(type + ": " + function.name + ", " + function.argCount);
                }else if(type == 35){
                    System.out.println(type + ": " + kernelLink.getSymbol());
                }else if(type == 43){
                    System.out.println(type + ": " + kernelLink.getInteger());
                }else if(type == 42){
                    System.out.println(type + ": " + kernelLink.getDouble());
                }else{
                    System.out.println("New type " + type);
                }
            }
        } catch (MathLinkException e) {
            System.out.println("MathLinkException occurred: " + e.getMessage());
        } finally {
            kernelLink.close();
        }
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