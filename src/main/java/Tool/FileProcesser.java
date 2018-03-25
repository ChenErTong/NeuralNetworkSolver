package Tool;

import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class FileProcesser {
    private static FileWriter FILE_RECORDER;

    public static List<double[][]> readParameter(String path){
        File file = new File(path);

        String line;
        double[] cache;
        List<double[]> weight = null;
        List<double[][]> weights = new ArrayList<double[][]>();
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
            while((line = br.readLine()) != null){
                if(line.length() > 0){
                    String[] numbers = line.split(" ");
                    if(weight == null)  weight = new ArrayList<double[]>();

                    cache = new double[numbers.length];
                    for (int i = 0; i < numbers.length; ++i)
                        cache[i] = Double.parseDouble(numbers[i]);

                    weight.add(cache);
                }else{
                    weights.add(weight.toArray(new double[weight.size()][]));
                    weight = null;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        weights.add(weight.toArray(new double[weight.size()][]));
        return weights;
    }

    public static void recordSolution(List<Solution> solutions){
        for (Solution solution: solutions) {
            writeToFile(solution.toString() + "\n\n");
        }
    }

    public static void recordLayer(double[][] layer, String comment){
        writeToFile(comment + ": ");
        for (double[] node: layer) {
            for (double parameter: node) {
                writeToFile(String.format("%.2f", parameter) + " ");
            }
            writeToFile("; ");
        }
        writeToFile("\n");
    }

    public static void writeToFile(String content){
        if(FILE_RECORDER == null){
            File file = new File(Utility.RECORD_PATH + getRandomFileName() + ".txt");
            try {
                file.createNewFile();
                FILE_RECORDER = new FileWriter(file, true);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            FILE_RECORDER.append(content);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.print(content);
    }

    public static void closeFile(){
        if(FILE_RECORDER != null){
            try {
                FILE_RECORDER.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            FILE_RECORDER = null;
            System.out.print("Complete in recording the result.");
        }
    }

    private static String getRandomFileName() {
        DateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddhhmmss");;
        String date = simpleDateFormat.format(new Date());
        Random random = new Random();
        int num = (int) (random.nextDouble() * (9999 - 1000 + 1)) + 1000;
        return date + num;
    }
}