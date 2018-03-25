package Logics;

import Display.Graphics2D;
import Display.Graphics3D;
import Tool.Solution;
import Tool.Utility;
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

    public List<Solution> solveInequalities(List<Solution> solutions, boolean showGraph){
        List<Solution> listWithSolution = new ArrayList<Solution>();
        Map<String, String> mapWithSolution = new HashMap<String, String>();
        String formula;

        for (Solution solution: solutions){
            if(!(formula = solveInequality(solution.getInput_number(), solution.getConstraint())).equals("Impossible")){
                solution.setSolutionSet(formula);
                listWithSolution.add(solution);
                mapWithSolution.put(solution.getObjective(), formula);
            }
        }

        if(showGraph && listWithSolution.size() > 0){
            showGraphics(listWithSolution.get(0).getInput_number(), mapWithSolution);
        }

        return listWithSolution;
    }

    public void showGraphics(int input_number, Map<String, String> solutions){
        if(input_number == 2){
            System.out.println("Display 2-dimension graphics.");
            new Graphics2D(solutions, kernelLink, Utility.RANGE2D);
        }else if(input_number == 3){
            System.out.println("Display 3-dimension graphics.");
            new Graphics3D(solutions, kernelLink, Utility.RANGE3D);
        }else{
            System.out.println("The dimension cannot be displayed.");
        }
    }

    public String solveInequality(int input_number, String constraint){
        return solveInequality(convertToReduceFormula(input_number, constraint));
    }

    public String solveInequality(String formula){
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

    private String processFunction(MLFunction function){
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

    class MLTK{
        String name;
        int remain_operator_num;

        MLTK(String n, int ac){
            name = Utility.SYMBOLS.get(n);
            remain_operator_num = ac;
        }
    }
}