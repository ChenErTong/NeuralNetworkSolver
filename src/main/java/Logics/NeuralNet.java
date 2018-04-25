package Logics;

import Tool.FileProcesser;
import Tool.Utility;
import org.neuroph.core.Layer;
import org.neuroph.core.NeuralNetwork;
import org.neuroph.core.Neuron;
import org.neuroph.core.data.DataSet;
import org.neuroph.core.data.DataSetRow;
import org.neuroph.core.events.LearningEvent;
import org.neuroph.core.events.LearningEventListener;
import org.neuroph.core.exceptions.NeurophException;
import org.neuroph.nnet.MultiLayerPerceptron;
import org.neuroph.nnet.learning.BackPropagation;
import org.neuroph.util.TransferFunctionType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class NeuralNet implements LearningEventListener{
    private NeuralNetwork network;

    public static void main(String[] args) {
        int[] layers = new int[]{2, 3, 1};

        Random rd = new Random();
        double[][] inputs = new double[100][];
        double[][] outputs = new double[inputs.length][];
        //人工模拟5000条训练数据 ，分界线为x1+x2+x3=1.54
        for(int i = 0; i < inputs.length; i++){
            double x1 = rd.nextDouble();//随机产生一个分量
            double x2 = rd.nextDouble();//随机产生一个分量
            inputs[i] = new double[]{x1, x2};
            outputs[i] = new double[]{x1 + x2 >= 1 ? 1 : 0};
        }

        NeuralNet net = new NeuralNet();
//        net.train(inputs, outputs, layers);
        FileProcesser.recordParameter(net.outputWeights(), Utility.WEIGHT_PATH);

//        inputs = FileProcesser.readInput(Utility.INPUT_PATH).toArray(new double[1][1]);

        outputs = net.calculate(inputs);
        for (int i = 0; i < inputs.length; i++) {
            System.out.println(Arrays.toString(inputs[i]) + ", " + Arrays.toString(outputs[i]));
        }
    }

    public NeuralNet(){
        try{
            network = NeuralNetwork.createFromFile(Utility.NETWORK_PATH);
        }catch (NeurophException e){
            System.out.println(e.getMessage());
        }
    }

    public boolean train(double[][] inputs, double[][] outputs, int[] layers_config) {
        if(inputs == null || inputs.length == 0 || outputs == null || outputs.length == 0 || inputs.length != outputs.length){
            return false;
        }

        //构造神经网络，并将ReLU函数作为其激活函数
        MultiLayerPerceptron perceptron = new MultiLayerPerceptron(TransferFunctionType.RectifiedLinear, layers_config);
        //将其学习规则设置为反向传播算法，配置BP神经网络
        BackPropagation learningRule = new BackPropagation();
        perceptron.setLearningRule(learningRule);

        learningRule.addListener(this);

        //设置每组数据集输入数据和输出数据的维数
        DataSet set = new DataSet(inputs[0].length, outputs[0].length);
        for (int i = 0; i < inputs.length; ++i){
            set.addRow(new DataSetRow(inputs[i], outputs[i]));
        }
        //对神经网络进行训练
        perceptron.learn(set);

        perceptron.save(Utility.NETWORK_PATH);
        network = NeuralNetwork.createFromFile(Utility.NETWORK_PATH);
        return true;
    }

    public List<String> outputWeights(){
        List<String> weights = new ArrayList<>();
        List<Layer> layers = network.getLayers();
        for(int i = 1; i < layers.size() - 1; ++i){
            List<Neuron> neurons = layers.get(i).getNeurons();
            for (int j = 0; j < neurons.size() - 1; ++j){
                String weight = Arrays.toString(neurons.get(j).getWeights());
                weights.add(weight.substring(1, weight.length() - 1) + "\n");
            }
            weights.add("\n");
        }

        for (Neuron neuron: layers.get(layers.size() - 1).getNeurons()) {
            String weight = Arrays.toString(neuron.getWeights());
            weights.add(weight.substring(1, weight.length() - 1) + "\n");
        }

        return weights;
    }

    public double[][] calculate(double[][] inputs){
        double[][] outputs = new double[inputs.length][];
        double[] output;
        for (int i = 0; i < inputs.length; ++i){
            network.setInput(inputs[i]);
            network.calculate();
            output = network.getOutput();
            outputs[i] = Arrays.copyOf(output, output.length);
        }
        return outputs;
    }

    public void handleLearningEvent(LearningEvent event) {
        BackPropagation bp = (BackPropagation)event.getSource();
        if (event.getEventType() != LearningEvent.Type.LEARNING_STOPPED)
            System.out.println(bp.getCurrentIteration() + ". iteration : "+ bp.getTotalNetworkError());
    }
}
