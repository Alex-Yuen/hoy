package ws.hoyland.captcha.synapesnet;

import org.joone.engine.*;
import org.joone.engine.learning.TeachingSynapse;
import org.joone.io.MemoryInputSynapse;
import org.joone.io.MemoryOutputSynapse;
import org.joone.net.NeuralNet;

//字符识别
/**
 * atuhor shentuxuhui
 */
public class Charaterrecognize {

	private LinearLayer input;
	private SigmoidLayer hidden;
	private SigmoidLayer output;
	private FullSynapse synapse_IH;
	private FullSynapse synapse_HO;
	private Monitor monitor;
	private MemoryInputSynapse inputSynapse; // 输入
	private MemoryInputSynapse desiredOutputSynapse; // 设计好的输出
	private NeuralNet nnet;
	private double[][] inputArray = new double[][] {
			{ 0.0, 0.0, 0.0, 1.0, 1.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0,
					0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 1.0, 0.0,
					0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0,
					1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 0.0, 1.0, 0.0, 0.0,
					0.0, 0.0, 0.0, 1.0, 0.0, 1.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0,
					1.0, 1.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 1.0,
					0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 1.0 },
			{ 0.0, 0.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 0.0, 0.0, 1.0, 1.0, 0.0,
					0.0, 0.0, 0.0, 1.0, 1.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0,
					1.0, 1.0, 0.0, 1.0, 0.0, 0.0, 1.0, 1.0, 1.0, 1.0, 0.0, 1.0,
					1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 0.0, 0.0, 0.0,
					0.0, 0.0, 0.0, 0.0, 1.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,
					0.0, 1.0, 1.0, 0.0, 0.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0,
					1.0, 1.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0 },
			{ 0.0, 0.0, 0.0, 1.0, 1.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0,
					0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 1.0, 0.0,
					0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, 0.0,
					1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 0.0, 1.0, 0.0, 0.0,
					0.0, 0.0, 0.0, 1.0, 0.0, 1.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0,
					1.0, 1.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 1.0,
					0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0 },
			{ 0.0, 0.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 0.0, 0.0, 1.0, 1.0, 0.0,
					0.0, 0.0, 0.0, 1.0, 1.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0,
					1.0, 1.0, 0.0, 1.0, 0.0, 0.0, 1.0, 1.0, 1.0, 1.0, 0.0, 1.0,
					1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 0.0, 0.0, 0.0,
					0.0, 0.0, 0.0, 0.0, 1.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,
					0.0, 1.0, 1.0, 0.0, 0.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0,
					1.0, 1.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0 } };

	private double[][] in = new double[][] {

	{ 0.0, 0.0, 0.0, 1.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 1.0,
			0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 1.0, 1.0, 0.0, 0.0, 0.0,
			0.0, 0.0, 1.0, 1.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 1.0,
			0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0,
			0.0, 0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 1.0, 1.0, 1.0, 0.0,
			0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 1.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0,
			0.0, 1.0 },

	};
	private double[][] desiredOutputArray = new double[][] { { 0.0 }, { 1.0 },
			{ 0.0 }, { 1.0 } };

	TeachingSynapse trainer;//

	public void setIn(double[][] in) {
		this.in = in;
	}

	public void setDersiredIn(double[][] in) {

		this.inputArray = in;

	}

	public void setDeriredOut(double[][] out) {

		this.desiredOutputArray = out;
	}

	public Charaterrecognize() {
		input = new LinearLayer();
		hidden = new SigmoidLayer();
		output = new SigmoidLayer();
		input.setLayerName("input");
		hidden.setLayerName("hidden");
		output.setLayerName("output");
		input.setRows(81);
		hidden.setRows(4);
		output.setRows(1);
		synapse_IH = new FullSynapse();
		synapse_HO = new FullSynapse();
		synapse_IH.setName("IH");
		synapse_HO.setName("HO");
		input.addOutputSynapse(synapse_IH);
		hidden.addInputSynapse(synapse_IH);
		// Connect the hidden layer with the output layer
		hidden.addOutputSynapse(synapse_HO);
		output.addInputSynapse(synapse_HO);
		// Create the Monitor object and set the learning parameters
		inputSynapse = new MemoryInputSynapse();
		input.addInputSynapse(inputSynapse);
		desiredOutputSynapse = new MemoryInputSynapse();

		trainer = new TeachingSynapse();
		trainer.setDesired(this.desiredOutputSynapse);

		nnet = new NeuralNet();

		nnet.addLayer(input, NeuralNet.INPUT_LAYER);
		nnet.addLayer(hidden, NeuralNet.HIDDEN_LAYER);
		nnet.addLayer(output, NeuralNet.OUTPUT_LAYER);
		nnet.setTeacher(trainer);
		output.addOutputSynapse(trainer);

		// trainer.start();

	}

	// 训练神经网络
	public void trainning() {

		inputSynapse.setInputArray(inputArray);

		inputSynapse.setAdvancedColumnSelector(" 1-81 ");
		desiredOutputSynapse.setInputArray(desiredOutputArray);
		desiredOutputSynapse.setAdvancedColumnSelector(" 1 ");
		monitor = this.nnet.getMonitor();
		monitor.setLearningRate(0.8);
		monitor.setMomentum(0.005);
		monitor.setTrainingPatterns(inputArray.length);
		monitor.setTotCicles(5000);
		monitor.setLearning(true);

		nnet.go(true);
	}

	public static void main(String args[]) {
		Charaterrecognize rec = new Charaterrecognize();
		rec.trainning();
		rec.recognize();

	}

	// 识别
	public double recognize() {

		this.inputSynapse.setInputArray(in);
		this.inputSynapse.setAdvancedColumnSelector("1-81");
		Monitor monitor = nnet.getMonitor();
		monitor.setTrainingPatterns(4);
		monitor.setTotCicles(1);
		monitor.setLearning(false);
		MemoryOutputSynapse memOut = new MemoryOutputSynapse();
		// set the output synapse to write the output of the net
		double revalue = 0.0;
		if (nnet != null) {
			nnet.addOutputSynapse(memOut);
			System.out.println(nnet.check());
			boolean singleThreadMode = false;
			nnet.getMonitor().setSingleThreadMode(singleThreadMode);
			nnet.go();

			//for (int i = 0; i < 1; i++) { //i<1
				double[] pattern = memOut.getNextPattern();
				System.out.println(" Output pattern # " + (0 + 1) + " = " //i+1
						+ pattern[0]);
				revalue = pattern[0];
			//	break;

			//}

			System.out.println(" Interrogating Finished ");
		}

		return revalue;

	}

}
