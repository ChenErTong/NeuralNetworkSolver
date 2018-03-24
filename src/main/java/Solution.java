import java.util.List;

public class Solution {
    private int input_number;
    private String objective;
    private String[] constraints;
    private String constraint;

    public Solution(int in, String o, String c){
        input_number = in;
        objective = o;
        constraint = c;
    }

    public Solution(double[] ob, List<double[]> cons){
        input_number = ob.length - 1;
        objective = convertObjective(ob);
        constraints = new String[cons.size()];
        for (int i = 0; i < constraints.length; ++i)
            constraints[i] = convertConstraints(cons.get(i));

        StringBuilder sb = new StringBuilder();
        for (String constraint: constraints)
            sb.append(constraint + " && ");

        constraint = sb.length() > 0 ? sb.substring(0, sb.length() - 4) : "";
    }

    public int getInput_number(){
        return input_number;
    }

    public String getObjective(){
        return objective;
    }

    public String[] getConstraints(){
        return constraints;
    }

    public String getConstraint(){
        return constraint;
    }

    private String convertObjective(double[] ob){
        StringBuilder sb = new StringBuilder();
        int i = 0;
        for (; i < ob.length - 1; ++i)
            sb.append(String.format("%.2f", ob[i]) + " * X" + (i + 1) + " + ");
        sb.append(String.format("%.2f", ob[i]));
        return sb.toString();
    }

    private String convertConstraints(double[] constraints){
        return convertObjective(constraints) + " > 0";
    }
}
