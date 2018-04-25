package Tool;

import java.util.HashMap;
import java.util.Map;

public class Utility {
    public static final String WEIGHT_PATH = "weights.txt";
    public static final String INPUT_PATH = "input.txt";
    public static final String RECORD_PATH = "solutions";
    public static final String NETWORK_PATH = "myMlPerceptron.nnet";
    public static final String MATHEMATICA_PATH = "E:\\Wolfram Research\\Mathematica\\11.2\\mathkernel.exe";
    /**
     * 两两一对，每对第一个为选项名称，第二个为选项内容
     * linkmode是连接模式，launch意味着启动Wolfram系统内核
     * linkname为连接路径，其内容为可执行文件mathkernel.exe的路径
     */
    public static final int[][] RANGE2D = new int[][]{{0, 1}, {0, 1}};
    public static final int[][] RANGE3D = new int[][]{{-100, 100}, {-100, 100}, {-100, 100}};

    public static final Map<String, String> SYMBOLS;
    public static final Map<String, Integer> Priority;

    public static final boolean RECORD_TO_FILE = true;
    public static final boolean SHOW_GRAPH = true;
    public static final int DOUBLE_PRECISION = -1;

    static {
        SYMBOLS = new HashMap<>();
        SYMBOLS.put("And", "&&");
        SYMBOLS.put("Plus", "+");
        SYMBOLS.put("Times", "*");
        SYMBOLS.put("Or", "||");
        SYMBOLS.put("Less", "<");
        SYMBOLS.put("Greater", ">");
        SYMBOLS.put("Equal", "=");
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
        Priority.put("=", 3);
        Priority.put("", 2);
        Priority.put("||", 1);
        Priority.put("&&", 0);
    }

    public static String CONVERT_DOUBLE(double d){
        if(DOUBLE_PRECISION == -1){
            return Double.toString(d);
        }else{
            return String.format("%." + DOUBLE_PRECISION + "f", d);
        }
    }
}