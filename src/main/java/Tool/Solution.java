package Tool;

import java.util.List;

public class Solution {
    private int input_number;
    private String objective;
    private String[] constraints;
    private String constraint;
    private String solutionSet;

    public Solution(double[] ob, List<double[]> cons){
        input_number = ob.length - 1;
        objective = convertObjective(ob);
        constraints = new String[cons.size()];
        for (int i = 0; i < constraints.length; ++i)
            constraints[i] = convertConstraints(cons.get(i));

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

    public String getObjective(){
        return objective;
    }

    public String getConstraint(){
        return constraint;
    }

    public String getSolutionSet(){
        return solutionSet;
    }

    public String[] getConstraints(){
        return constraints;
    }

    public String toString(){
        StringBuilder sb = new StringBuilder("Objective:\n");
        sb.append(objective);
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
            sb.append(String.format("%.2f", ob[i]));
            sb.append(" * X");
            sb.append(i + 1);
            sb.append(" + ");
        }
        sb.append(String.format("%.2f", ob[i]));
        return sb.toString();
    }

    private String convertConstraints(double[] constraints){
        return convertObjective(constraints) + " > 0";
    }
}
