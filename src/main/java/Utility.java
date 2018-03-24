import java.util.HashMap;
import java.util.Map;

public class Utility {
    public static final String WEIGHT_PATH = "weights.txt";
    public static final String BIAS_PATH = "biases.txt";
    public static final String MATHEMATICA_PATH = "E:\\Wolfram Research\\Mathematica\\11.2\\mathkernel.exe";
    public static final Map<String, String> SYMBOLS;

    static {
        SYMBOLS = new HashMap<String, String>();
        SYMBOLS.put("And", "&&");
        SYMBOLS.put("Plus", "+");
        SYMBOLS.put("Times", "*");
        SYMBOLS.put("Or", "||");
        SYMBOLS.put("Less", "<");
        SYMBOLS.put("Greater", ">");
    }
}
