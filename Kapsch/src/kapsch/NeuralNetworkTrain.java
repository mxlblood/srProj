package kapsch;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import org.neuroph.core.NeuralNetwork;
import org.neuroph.core.data.DataSet;
import org.neuroph.core.data.DataSetRow;
import org.neuroph.core.learning.SupervisedLearning;
import org.neuroph.nnet.MultiLayerPerceptron;
import org.neuroph.nnet.learning.MomentumBackpropagation;
import org.neuroph.util.TrainingSetImport;
import org.neuroph.util.TransferFunctionType;
import org.neuroph.core.learning.SupervisedLearning;

public class NeuralNetworkTrain {

    public static void main(String[] args) {
        //configurable layers
        int inputNeurons = 6; 
        int hiddenNeurons = 1;
        int outputNeurons = 1;
        
        String trainingSetFileName = "normalizedData.txt";
        int inputsCount = 6;
        int outputsCount = 1;

        System.out.println("Running Sample");
        System.out.println("Using training set " + trainingSetFileName);

        // create training set
        DataSet trainingSet = null;
        try {
            trainingSet = TrainingSetImport.importFromFile(trainingSetFileName, inputsCount, outputsCount, ",");
        } catch (FileNotFoundException ex) {
            System.out.println("File not found!");
        } catch (IOException | NumberFormatException ex) {
            System.out.println("Error reading file or bad number format!");
        }

        // create multi layer perceptron
        System.out.println("Creating neural network");
        MultiLayerPerceptron neuralNet = new MultiLayerPerceptron(TransferFunctionType.SIGMOID, inputNeurons, hiddenNeurons, outputNeurons);

        // set learning parameters
        MomentumBackpropagation learningRule = (MomentumBackpropagation) neuralNet.getLearningRule();
        learningRule.setLearningRate(0.2);
        learningRule.setMomentum(0.7);

        // learn the training set
        System.out.println("Training neural network...");
        neuralNet.learn(trainingSet);
        System.out.println("Done!");

        // test perceptron
        System.out.println("Testing trained neural network");
        testPrediction(neuralNet, trainingSet);

        //Outputs with error
        testOutputs(neuralNet, trainingSet);
        System.out.println("Total Network Error: " + learningRule.getTotalNetworkError());
       
    }

    public static void testPrediction(NeuralNetwork nnet, DataSet dset) {
        for (DataSetRow trainingElement : dset.getRows()) {
            nnet.setInput(trainingElement.getInput());
            nnet.calculate();
            double[] networkOutput = nnet.getOutput();
            System.out.print("Input: " + Arrays.toString(trainingElement.getInput()));
            System.out.println(" Output: " + Arrays.toString(networkOutput));
        }

    }
    
    public static void testOutputs(NeuralNetwork nnet, DataSet dset) {
        for (DataSetRow trainingElement : dset.getRows()) {
            nnet.setInput(trainingElement.getInput());
            nnet.calculate();
            double[] networkOutput = nnet.getOutput();
            double[] desiredOutput = trainingElement.getDesiredOutput();
            System.out.print("Actual Output: " + Arrays.toString(desiredOutput));
            System.out.print(" Predicted Output: " + Arrays.toString(networkOutput));
            double error = desiredOutput[0] - networkOutput[0];
            System.out.println(" Error: " + error);
            
            //calculateOutputError(desiredOutput, networkOutput);
        }

    }
    
}