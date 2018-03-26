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

    public static void recordParameter(List<String> parameters, String path){
        File file = new File(path);

        try {
            if(!file.exists()){
                file.createNewFile();
            }
            FileWriter writer = new FileWriter(file, false);
            for (String parameter : parameters) {
                writer.append(parameter);
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<double[]> readInput(String path){
        File file = new File(path);
        List<double[]> inputs = new ArrayList<>();

        String line;
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
            while((line = br.readLine()) != null) {
                String[] numbers = line.split(" ");
                double[] input = new double[numbers.length];
                for (int i = 0; i < input.length; ++i){
                    input[i] = Double.parseDouble(numbers[i]);
                }
                inputs.add(input);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return inputs;
    }

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
                    String[] numbers = line.split(", ");
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
        for (int i = 0; i < solutions.size(); ++i){
            writeToFile("Solution " + (i + 1) + ": \n" + solutions.get(i).toString() + "\n\n");
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
        if(Utility.RECORD_TO_FILE){
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
        DateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddhhmmss_");;
        String date = simpleDateFormat.format(new Date());
        Random random = new Random();
        int num = (int) (random.nextDouble() * (9999 - 1000 + 1)) + 1000;
        return date + num;
    }
}