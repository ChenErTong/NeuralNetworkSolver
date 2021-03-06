package Tool;

import java.util.List;

public class Solution {
    private int input_number;
    private String[] objectives;
    private String[] constraints;
    private String constraint;
    private String solutionSet;

    /**
     * 构造器
     * @param ob 存储线性表达式组的数组
     * @param cons 存储约束的线性不等式方程组的数组
     */
    public Solution(double[][] ob, List<double[]> cons){
        input_number = ob[0].length - 1;
        objectives = new String[ob.length - 1];
        for (int i = 0; i < ob.length - 1; ++i){
            objectives[i] = convertObjective(ob[i]);
        }
        constraints = new String[cons.size()];
        for (int i = 0; i < constraints.length; ++i){
            constraints[i] = convertConstraints(cons.get(i));
        }

        StringBuilder sb = new StringBuilder();
        for (String constraint: constraints){
            sb.append(constraint);
            sb.append(" && ");
        }

        constraint = sb.length() > 0 ? sb.substring(0, sb.length() - 4) : "";
    }

    public void setSolutionSet(String ss){
        solutionSet = ss;
    }

    public int getInput_number(){
        return input_number;
    }

    public String[] getObjectives(){
        return objectives;
    }

    public String getConstraint(){
        return constraint;
    }

    public String[] getConstraints(){
        return constraints;
    }

    public String toString(){
        StringBuilder sb = new StringBuilder("Objective:");
        for (String objective: objectives) {
            sb.append("\n");
            sb.append(objective);
        }
        sb.append("\nConstraints:");
        for (String c: constraints) {
            sb.append("\n");
            sb.append(c);
        }
        if(solutionSet != null){
            sb.append("\nSolution Set:\n");
            sb.append(solutionSet);
        }
        return sb.toString();
    }

    private String convertObjective(double[] ob){
        StringBuilder sb = new StringBuilder();
        int i = 0;
        for (; i < ob.length - 1; ++i){
            if(ob[i] != 0){
                sb.append(Utility.CONVERT_DOUBLE(ob[i]));
                sb.append(" * X");
                sb.append(i + 1);
                sb.append(" + ");
            }
        }
        sb.append(Utility.CONVERT_DOUBLE(ob[i]));
        return sb.toString();
    }

    private String convertConstraints(double[] constraints){
        return convertObjective(constraints) + " > 0";
    }
}
