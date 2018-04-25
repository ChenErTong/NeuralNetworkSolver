package Logics;

import Display.Graphics2D;
import Display.GraphicsForTest;
import Tool.FileProcesser;
import Tool.Solution;
import Tool.Utility;
import com.wolfram.jlink.KernelLink;
import com.wolfram.jlink.MLFunction;
import com.wolfram.jlink.MathLinkException;
import com.wolfram.jlink.MathLinkFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class InequalitiesSolver {
//    public static InequalitiesSolver instance = new InequalitiesSolver();
    private static KernelLink kernelLink;
    private Stack<Function> stack = new Stack<Function>();

    public InequalitiesSolver(){
        if (kernelLink == null){
            try {
                kernelLink = MathLinkFactory.createKernelLink("-linkmode launch -linkname '" + Utility.MATHEMATICA_PATH + "'");
                kernelLink.discardAnswer();
            } catch (MathLinkException e) {
                System.out.println("Link could not be created: " + e.getMessage());
            }
        }
    }

    public void showForTest(){
        new GraphicsForTest(kernelLink);
    }

    public List<Solution> solveInequalities(List<Solution> solutions){
        List<Solution> listWithSolution = new ArrayList<Solution>();
        String formula;

        for (Solution solution: solutions){
            if(!(formula = solveInequality(solution.getInput_number(), solution.getConstraint())).equals("Impossible")){
                solution.setSolutionSet(formula);
                listWithSolution.add(solution);
                FileProcesser.recordSolution(solution, listWithSolution.size());
            }
        }

        return listWithSolution;
    }

    public void showGraphics(List<Solution> solutions){
        if(Utility.SHOW_GRAPH && solutions.size() > 0){
            if(solutions.get(0).getInput_number() == 2){
                System.out.println("Display 2-dimension graphics.");
                new Graphics2D(solutions, kernelLink, Utility.RANGE2D);
            }else{
                System.out.println("The dimension cannot be displayed.");
            }
        }
    }

    public String solveInequality(int input_number, String constraint){
        return solveInequality(convertToReduceFormula(input_number, constraint));
    }

    /**
     * 求解线性不等式方程组
     * @param formula 已经包装成Wolfram语言形式的约束不等式方程组
     * @return 方程组的解
     */
    public String solveInequality(String formula){
        FileProcesser.writeToFile("\nFormula: " + formula + "\n");
        //stack为缓存Mathematica返回值的堆栈
        stack.clear();
        StringBuilder sb = new StringBuilder();
        try {
            //向Mathematica传递公式
            kernelLink.evaluate(formula);
            kernelLink.waitForAnswer();
            //当stack再次为空时，说明返回值已经全部解析完成
            do {
                int type = kernelLink.getType();
                if(type == 70){
                    //返回值类型为函数
                    sb.append(processFunction(kernelLink.getFunction()));
                }else if(type == 35){
                    //返回值类型为符号
                    sb.append(processSymbol(kernelLink.getSymbol()));
                }else if(type == 43){
                    //返回值类型为整型
                    sb.append(processNumerical(kernelLink.getInteger()));
                }else if(type == 42){
                    //返回值类型为浮点数
                    sb.append(processNumerical(kernelLink.getDouble()));
                }else{
                    //返回值类型错误
                    kernelLink.discardAnswer();
                    stack.clear();
                    System.out.println("Unexpected Type: " + type);
                }
            }while (!stack.empty());
        } catch (MathLinkException e) {
            System.out.println("MathLinkException occurred: " + e.getMessage());
        }

        //当sb为空时说明该方程组无解
        return sb.length() == 0 ? "Impossible" : sb.toString();
    }

    private String processFunction(MLFunction function){
        Function func = new Function(function.name, function.argCount);
        boolean higherPriority = higherPriority(func);
        stack.push(func);
        return higherPriority ? "" : "(";
    }

    private String processSymbol(String symbol){
        if(symbol.equals("False")) return "";
        if(Utility.SYMBOLS.containsKey(symbol))
            return process(Utility.SYMBOLS.get(symbol));
        else
            return process(symbol);
    }

    private String processNumerical(int numerical){
        return process(Integer.toString(numerical));
    }

    private String processNumerical(double numerical){
        return process(Utility.CONVERT_DOUBLE(numerical));
    }

    /**
     * 处理基础类型返回值
     * @param name 基础类型
     * @return 解析出来的字符串
     */
    private String process(String name){
        StringBuilder sb = new StringBuilder(name);
        while(!stack.empty()){
            if (--stack.peek().remain_operator_num > 0){
                //当该数据不是Function最后一个操作数时
                sb.append(stack.peek().name);
                break;
            }else {
                //当该数据是Function最后一个操作数时
                Function function = stack.pop();
                //判断操作符优先级以完成括号添加
                if (!higherPriority(function))
                    sb.append(")");
            }
        }

        return sb.toString();
    }

    private boolean higherPriority(Function function){
        return stack.empty() || Utility.Priority.get(function.name) >= Utility.Priority.get(stack.peek().name);
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

    public void close(){
        if(kernelLink != null){
            kernelLink.close();
            kernelLink = null;
        }
    }

    class Function {
        String name;
        int remain_operator_num;

        Function(String n, int ac){
            name = Utility.SYMBOLS.get(n);
            remain_operator_num = ac;
        }
    }
}