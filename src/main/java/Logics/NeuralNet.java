package Logics;

import org.neuroph.core.Layer;
import org.neuroph.core.NeuralNetwork;
import org.neuroph.core.Neuron;
import org.neuroph.core.data.DataSet;
import org.neuroph.core.data.DataSetRow;
import org.neuroph.core.events.LearningEvent;
import org.neuroph.core.events.LearningEventListener;
import org.neuroph.nnet.MultiLayerPerceptron;
import org.neuroph.nnet.learning.BackPropagation;
import org.neuroph.util.TransferFunctionType;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class NeuralNet implements LearningEventListener{
    public static void main(String[] args) {
        new NeuralNet().run();
    }

    /**
     * Runs this sample
     */
    public void run() {
        //人工模拟1000条训练数据 ，分界线为x2=x1+0.5
        DataSet trainingSet = new DataSet(2, 1);
        for(int i = 0; i < 1000; i++){
            Random rd = new Random();
            double x1 = rd.nextDouble();//随机产生一个分量
            double x2 = rd.nextDouble();//随机产生另一个分量
            trainingSet.addRow(new DataSetRow(new double[]{x1, x2}, new double[]{x2 > x1 + 0.5 ? 1 : 0}));
        }

        // create multi layer perceptron
        MultiLayerPerceptron myMlPerceptron = new MultiLayerPerceptron(TransferFunctionType.RectifiedLinear, 2, 3, 1);
        myMlPerceptron.setLearningRule(new BackPropagation());

        // learn the training set
        System.out.println("Training neural network...");
        myMlPerceptron.learn(trainingSet);

        // test perceptron
        System.out.println("Testing trained neural network");
        testNeuralNetwork(myMlPerceptron, 0.5, 0.5);

        // save trained neural network
        myMlPerceptron.save("myMlPerceptron.nnet");

        NeuralNetwork loadedMlPerceptron = NeuralNetwork.createFromFile("myMlPerceptron.nnet");

        List<Layer> layers = loadedMlPerceptron.getLayers();
        for (Layer layer: layers) {
            for (Neuron neuron: layer.getNeurons()) {
                    System.out.println(Arrays.toString(neuron.getWeights()));
            }
            System.out.println();
        }

        Double[] weights = loadedMlPerceptron.getWeights();
        for (Double weight: weights) {
            System.out.print(weight.doubleValue() + "; ");
        }

        System.out.println();
    }

    /**
     * Prints network output for the each element from the specified training set.
     * @param neuralNet neural network
     */
    public static void testNeuralNetwork(org.neuroph.core.NeuralNetwork neuralNet, double... inputVector) {
        neuralNet.setInput(inputVector);
        neuralNet.calculate();
        double[] networkOutput = neuralNet.getOutput();

        System.out.print("Input: " + Arrays.toString( inputVector) );
        System.out.println(" Output: " + Arrays.toString( networkOutput) );
    }

    public void handleLearningEvent(LearningEvent event) {
        BackPropagation bp = (BackPropagation)event.getSource();
//        if (event.getEventType() == LearningEvent.Type.LEARNING_STOPPED)
//            System.out.println(bp.getCurrentIteration() + ". iteration : "+ bp.getTotalNetworkError());
    }
}
