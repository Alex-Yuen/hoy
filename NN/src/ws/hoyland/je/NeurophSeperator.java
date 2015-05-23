package ws.hoyland.je;

import java.util.Vector;

import org.neuroph.core.learning.SupervisedTrainingElement;
import org.neuroph.core.learning.TrainingElement;
import org.neuroph.core.learning.TrainingSet;
import org.neuroph.nnet.MultiLayerPerceptron;
import org.neuroph.nnet.learning.DynamicBackPropagation;
import org.neuroph.util.TransferFunctionType;


public class NeurophSeperator {
    /**
     * Runs this sample
     */
    public static void main(String[] args) {
        
        // create training set (logical XOR function)
        TrainingSet trainingSet = new TrainingSet(2, 1);
        trainingSet.addElement(new SupervisedTrainingElement(new double[]{0, 0}, new double[]{0}));
        trainingSet.addElement(new SupervisedTrainingElement(new double[]{0, 1}, new double[]{1}));
        trainingSet.addElement(new SupervisedTrainingElement(new double[]{1, 0}, new double[]{1}));
        trainingSet.addElement(new SupervisedTrainingElement(new double[]{1, 1}, new double[]{0}));

        // create multi layer perceptron
        MultiLayerPerceptron network = new MultiLayerPerceptron(TransferFunctionType.TANH, 2, 3, 1);
        
        DynamicBackPropagation train = new DynamicBackPropagation();
        train.setNeuralNetwork(network);
        network.setLearningRule(train);
        
        int epoch = 1;
        do
        {
        	train.doOneLearningIteration(trainingSet);
        	System.out.println("Epoch " + epoch + ", error=" + train.getTotalNetworkError());
        	epoch++;
        	
        } while(train.getTotalNetworkError()>0.01);
        
        System.out.println("Neural Network Results:");
        
        
        for(TrainingElement element : trainingSet.trainingElements()) {
        	network.setInput(element.getInput());
            network.calculate();
            Vector<Double> output = network.getOutput();
            SupervisedTrainingElement ste = (SupervisedTrainingElement)element;
            
			System.out.println(element.getInput().get(0) + "," + element.getInput().get(1)
					+ ", actual=" + output.get(0) + ", ideal=" + ste.getDesiredOutput().get(0));
		}
    }
}
