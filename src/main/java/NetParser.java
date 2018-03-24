import sun.nio.ch.Net;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class NetParser {

    /**
     * 范围集
     */
    private List<Solution> solutions = new ArrayList<Solution>();

    /**
     * 解析神经网络参数，获得各种情况的对应范围集
     * @param path_weight 权重文件参数
     * @param path_bias 偏差文件参数
     * @return 范围集
     */
    public List<Solution> parse(String path_weight, String path_bias){
        solutions.clear();

        List<double[][]> weights = readParameter(path_weight);
        List<double[][]> biases = readParameter(path_bias);

        for (double[][] weight: weights) outputLayer(weight, "weight");
        for (double[][] bias: biases) outputLayer(bias, "bias");

        long startTime = System.currentTimeMillis();
        calculateNet(2, weights, biases);
        long endTime = System.currentTimeMillis();
        System.out.println("Total Duration: " + (endTime - startTime) / 1000.0 + " sec");

        return solutions;
    }

    private List<double[][]> readParameter(String path){
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
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        weights.add(weight.toArray(new double[weight.size()][]));
        return weights;
    }

    private void calculateNet(int input_number, List<double[][]> weights, List<double[][]> biases){
        if (!checkValidity(input_number, weights)){
            System.out.println("weights参数不符合");
            return;
        }

        if (!checkValidity(input_number, biases)){
            System.out.println("biases参数不符合");
            return;
        }
        System.out.println("成功检验所有参数");

        double[][] input_layer = new double[input_number + 1][input_number + 1];
        input_layer[0][input_number] = 1;
        for (int i = 1; i <= input_number; ++i)
            input_layer[i][i - 1] = 1;

        calculateNet(input_layer, weights, biases, 0, weights.size(), new ArrayList<double[]>());
    }

    private void calculateNet(double[][] input_layer, List<double[][]> weights, List<double[][]> biases, int layer_index, int layer_nubmer, List<double[]> condition){
        //如果已经计算到最后一层，则将结果记录后返回
        if(layer_index == layer_nubmer) {
            outputPredictionLayer(input_layer, condition);
            return;
        }

        //获取当前层的所有权重
        double[][] weight = weights.get(layer_index);
        double[][] bias = biases.get(layer_index);
        double[][][][] cache = attainNodeCache(input_layer, weight, bias);

        if(layer_index++ == 0){
            calculateNet(calculateLayer(cache,0), weights, biases, layer_index, layer_nubmer, condition);
        }else {
            int relu_tag = (int) Math.pow(2, input_layer.length - 1) - 1;
            //获得当前层所有节点大于零小于零的排列组合，递归调用计算下一层
            for (int i = relu_tag; i >= 0; --i){
                List<double[]> tmp_condition = new ArrayList<double[]>(condition);
                tmp_condition.addAll(attainCondition(input_layer, i));
                calculateNet(calculateLayer(cache, i), weights, biases, layer_index, layer_nubmer, tmp_condition);
            }
        }
    }

    private double[][] calculateLayer(double[][][][] cache, int relu_tag){
        double[][] output_layer = new double[cache.length + 1][cache[0][0][0].length];
        output_layer[0][output_layer[0].length - 1] = 1;
        for (int i = 1;i < output_layer.length; i++)
            output_layer[i] = calculateNode(cache, i - 1, relu_tag);

        return output_layer;
    }

    //输入层已经加入了1
    private double[] calculateNode(double[][][][] cache, int output_node_index, int relu_tag){
        int length = cache[0].length;

        //考虑多个输入的情况，node元素个数应该为每组输入数据元素个数加1
        double[] node = new double[cache[0][0][0].length];

        for (int i = length - 1; i >= 0; --i){
            boolean isZero = (relu_tag % 2 == 1);
            relu_tag /= 2;
            if(isZero){
                node = addDoubleArray(node, cache[output_node_index][i][0]);
            }else{
                node = addDoubleArray(node, cache[output_node_index][i][1]);
            }
        }

        return node;
    }

    private boolean checkValidity(int input_number, List<double[][]> parameters){
        if(parameters.size() == 0) return false;
        if((input_number + 1) != parameters.get(0)[0].length) return false;

        for (int i = 0; i < parameters.size() - 1; ++i)
            if ((parameters.get(i).length + 1) != parameters.get(i + 1)[0].length) return false;

        return true;
    }

    private List<double[]> attainCondition(double[][] input_layer, int relu_tag){
        List<double[]> result = new ArrayList<double[]>();
        for (int i = input_layer.length - 1; i > 0; --i){
            boolean isZero = (relu_tag % 2 == 1);
            relu_tag /= 2;
            if(isZero){
                double[] cache = new double[input_layer[i].length];
                for (int j = 0; j < cache.length; ++j){
                    cache[j] = -input_layer[i][j];
                }
                result.add(cache);
            }else{
                result.add(input_layer[i]);
            }
        }

        return result;
    }

    private double[][][][] attainNodeCache(double[][] input_layer, double[][] weight, double[][] bias){
        double[][][][] cache = new double[weight.length][input_layer.length][2][input_layer[0].length];
        int node_last_index = input_layer[0].length - 1;
        for (int i = 0; i < weight.length; ++i){
            for (int j = 0; j < input_layer.length; ++j){
                cache[i][j][0][node_last_index] = cache[i][j][1][node_last_index] = bias[i][j];
                for (int k = 0; k <= node_last_index; ++k){
                    cache[i][j][1][k] += input_layer[j][k] * weight[i][j];
                }
            }
        }
        return cache;
    }

    private double[] addDoubleArray(double[] a, double[] b){
        double[] result = new double[a.length];
        for (int i = 0; i < a.length; ++i)
            result[i] = a[i] + b[i];
        return result;
    }

    private void outputPredictionLayer(double[][] prediction_layer, List<double[]> cache){
        Solution solution = new Solution(prediction_layer[1], cache);
        System.out.print("Prediction: " + solution.getObjective());
        System.out.print("\nConstraints: " + solution.getConstraint());
        System.out.println("\n");
        solutions.add(solution);
    }

    private void outputLayer(double[][] layer, String comment){
        System.out.print(comment + ": ");
        for (double[] node: layer) {
            for (double parameter: node) {
                System.out.print(String.format("%.2f", parameter) + " ");
            }
            System.out.print("; ");
        }
        System.out.print("\n");
    }
}