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
import org.neuroph.core.learning.LearningRule;
import org.neuroph.nnet.MultiLayerPerceptron;
import org.neuroph.nnet.learning.BackPropagation;
import org.neuroph.util.TransferFunctionType;

import java.util.*;

public class NeuralNet implements LearningEventListener{
    private NeuralNetwork network;

    public static void main(String[] args) {
        double[][] inputs = new double[1000][];
        double[][] outputs = new double[inputs.length][];
        int[] layers = new int[]{2, 4, 1};
        Random rd = new Random();

        //人工模拟1000条训练数据 ，分界线为x2=x1+0.5
        for(int i = 0; i < inputs.length; i++){
            double x1 = rd.nextDouble();//随机产生一个分量
            double x2 = rd.nextDouble();//随机产生另一个分量
            inputs[i] = new double[]{x1, x2};
            outputs[i] = new double[]{x1 > x2 + 0.5 ? 1.0 : 0.0};
        }
        NeuralNet net = new NeuralNet();
//        net.train(inputs, outputs, layers);

        inputs = FileProcesser.readInput(Utility.INPUT_PATH).toArray(new double[1][1]);

//        [0.5, 0.5]: X1<=0.79&&0.01*(-44.00+66.00*X1)<X2<0.01*(137.00+-145.00*X1)||0.79<X1<0.80&&0.01*(-44.00+66.00*X1)<X2<0.33*(49.00+-61.00*X1): [-0.06499999999999995]
//[0.0, 0.5]: X1<=0.79&&0.01*(-44.00+66.00*X1)<X2<0.01*(137.00+-145.00*X1)||0.79<X1<0.80&&0.01*(-44.00+66.00*X1)<X2<0.33*(49.00+-61.00*X1): [0.795]
//[0.5, 0.1]: X1<=0.79&&0.01*(-44.00+66.00*X1)<X2<0.01*(137.00+-145.00*X1)||0.79<X1<0.80&&0.01*(-44.00+66.00*X1)<X2<0.33*(49.00+-61.00*X1): [0.12300000000000004]
//[0.1, 0.5]: X1<=0.79&&0.01*(-44.00+66.00*X1)<X2<0.01*(137.00+-145.00*X1)||0.79<X1<0.80&&0.01*(-44.00+66.00*X1)<X2<0.33*(49.00+-61.00*X1): [0.623]

        outputs = net.calculate(inputs);
        for (int i = 0; i < inputs.length; i++) {
            System.out.println(Arrays.toString(inputs[i]) + ", " + Arrays.toString(outputs[i]));
        }
//        net.outputWeights();
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

        DataSet set = new DataSet(inputs[0].length, outputs[0].length);
        for (int i = 0; i < inputs.length; ++i){
            set.addRow(new DataSetRow(inputs[i], outputs[i]));
        }

        MultiLayerPerceptron perceptron = new MultiLayerPerceptron(TransferFunctionType.RectifiedLinear, layers_config);
        perceptron.setLearningRule(new BackPropagation());

        LearningRule learningRule = network.getLearningRule();
        learningRule.addListener(this);

        perceptron.learn(set);

        perceptron.save(Utility.NETWORK_PATH);
        network = NeuralNetwork.createFromFile(Utility.NETWORK_PATH);
        return true;
    }

    public void outputWeights(){
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

        FileProcesser.recordParameter(weights, Utility.WEIGHT_PATH);
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
        if (event.getEventType() == LearningEvent.Type.LEARNING_STOPPED)
            System.out.println(bp.getCurrentIteration() + ". iteration : "+ bp.getTotalNetworkError());
    }
}
