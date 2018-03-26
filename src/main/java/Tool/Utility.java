package Tool;

import java.util.HashMap;
import java.util.Map;

public class Utility {
    public static final String WEIGHT_PATH = "weights.txt";
    public static final String INPUT_PATH = "input.txt";
    public static final String RECORD_PATH = "solutions";
    public static final String NETWORK_PATH = "myMlPerceptron.nnet";
    public static final String MATHEMATICA_PATH = "E:\\Wolfram Research\\Mathematica\\11.2\\mathkernel.exe";

    public static final int[][] RANGE2D = new int[][]{{-100, 100}, {-100, 100}};
    public static final int[][] RANGE3D = new int[][]{{-100, 100}, {-100, 100}, {-100, 100}};

    public static final Map<String, String> SYMBOLS;
    public static final Map<String, Integer> Priority;

    public static boolean RECORD_TO_FILE = false;
    public static boolean SHOW_GRAPH = false;

    static {
        SYMBOLS = new HashMap<>();
        SYMBOLS.put("And", "&&");
        SYMBOLS.put("Plus", "+");
        SYMBOLS.put("Times", "*");
        SYMBOLS.put("Or", "||");
        SYMBOLS.put("Less", "<");
        SYMBOLS.put("Greater", ">");
        SYMBOLS.put("LessEqual", "<=");
        SYMBOLS.put("GreaterEqual", ">=");
        SYMBOLS.put("Inequality", "");

        Priority = new HashMap<>();
        Priority.put("*", 5);
        Priority.put("+", 4);
        Priority.put(">", 3);
        Priority.put(">=", 3);
        Priority.put("<", 3);
        Priority.put("<=", 3);
        Priority.put("", 2);
        Priority.put("&&", 1);
        Priority.put("||", 0);
    }
}