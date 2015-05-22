package ws.hoyland.nn.bp;

import java.util.Random;

/**
 * BP Neural Network
 * 
 * @author Liangchenhao
 * 
 */
public class BP {
	/**
	 * 输入层
	 */
	private final double[] input;
	/**
	 * 隐藏层
	 */
	private final double[] hidden;
	/**
	 * 输出层
	 */
	private final double[] output;
	/**
	 * 真实结果
	 */
	private final double[] target;
	/*
	 * 隐藏层阈值
	 */
	private final double[] hidThresholds;
	/*
	 * 输出层阈值
	 */
	private final double[] outThresholds;
	/*
	 * 更新前的隐藏层阈值
	 */
	private final double[] hidPreThresholds;
	/*
	 * 更新前的输出层阈值
	 */
	private final double[] outPreThresholds;
	/**
	 * 隐藏层训练误差
	 */
	private final double[] hidError;
	/**
	 * 输出层训练误差
	 */
	private final double[] outError;
	/**
	 * 学习速率
	 */
	private final double rate;
	/**
	 * 常数 决定过去权重的变化对目前权值变化的影响程度
	 */
	private final double constant;
	/**
	 * 输入层到隐藏层的权值
	 */
	private final double[][] inHidWeights;
	/**
	 * 隐藏层到输出层的权值
	 */
	private final double[][] hidOutWeights;
	/**
	 * 更新前的输入层到隐藏层的权值
	 */
	private final double[][] inHidPreWeights;
	/**
	 * 更新前的隐藏层到输出层的权值
	 */
	private final double[][] hidOutPreWeights;
	/*
	 * 输出层误差和
	 */
	public double outErrSum = 0d;
	/*
	 * 隐藏层误差和
	 */
	public double hidErrSum = 0d;

	Random random = new Random();

	/*
	 * 构造方法
	 */
	public BP(int inputSize, int hiddenSize, int outputSize, double rate,
			double constant) {
		input = new double[inputSize];
		hidden = new double[hiddenSize];
		output = new double[outputSize];
		target = new double[outputSize];

		hidThresholds = new double[hiddenSize];
		outThresholds = new double[outputSize];
		hidPreThresholds = new double[hiddenSize];
		outPreThresholds = new double[outputSize];

		hidError = new double[hiddenSize];
		outError = new double[outputSize];
		this.rate = rate;
		this.constant = constant;

		inHidWeights = new double[hiddenSize][inputSize];
		hidOutWeights = new double[outputSize][hiddenSize];
		inHidPreWeights = new double[hiddenSize][inputSize];
		hidOutPreWeights = new double[outputSize][hiddenSize];

		InitThreshold(hidThresholds);
		InitThreshold(outThresholds);
		InitWeight(inHidWeights);
		InitWeight(hidOutWeights);
	}

	/*
	 * 构造方法 rate = 0.25,constant = 0.9
	 */
	public BP(int inputSize, int hiddenSize, int outputSize) {
		this(inputSize, hiddenSize, outputSize, 0.25, 0.9);
	}

	/*
	 * 初始化权值(随机,-1~1)
	 */
	private void InitWeight(double[][] weights) {
		for (int i = 0, len1 = weights.length; i < len1; i++) {
			for (int j = 0, len2 = weights[i].length; j < len2; j++) {
				double val = random.nextDouble();
				weights[i][j] = random.nextDouble() > 0.5 ? val : -val;
			}
		}
	}

	/*
	 * 初始化阈值(随机,-1~1)
	 */
	private void InitThreshold(double[] thresholds) {
		for (int i = 0, len1 = thresholds.length; i < len1; i++) {
			double val = random.nextDouble();
			thresholds[i] = random.nextDouble() > 0.5 ? val : -val;
		}
	}

	/*
	 * 训练神经网络
	 */
	public void Train(double[] inputData, double[] targetData) {
		LoadInput(inputData);
		LoadTarget(targetData);
		Forward();
		CalError();
		AdjustWeight();
		AdjustThreshold();
	}

	/*
	 * 测试神经网络
	 */
	public void Test(double[] inputData, double[] result) {
		LoadInput(inputData);
		Forward();
		System.arraycopy(output, 0, result, 0, output.length);
	}

	/*
	 * 输入数据赋值
	 */
	private void LoadInput(double[] arg) {
		if (input.length != arg.length)
			throw new IllegalArgumentException("输入数据格式不匹配");
		System.arraycopy(arg, 0, input, 0, arg.length);
	}

	/*
	 * 结果数据赋值
	 */
	private void LoadTarget(double[] arg) {
		if (target.length != arg.length)
			throw new IllegalArgumentException("结果数据格式不匹配");
		System.arraycopy(arg, 0, target, 0, arg.length);
	}

	/*
	 * 正向传播
	 */
	private void Forward(double[] layer1, double[] layer2, double[][] weight) {
		for (int i = 0, len1 = layer2.length; i < len1; i++) {
			double val = 0;
			for (int j = 0, len2 = layer1.length; j < len2; j++)
				val += layer1[j] * weight[i][j];
			layer2[i] = Sigmoid(val + layer2[i]);
		}
	}

	/*
	 * 先计算隐藏层
	 */
	private void Forward() {
		Forward(input, hidden, inHidWeights);
		Forward(hidden, output, hidOutWeights);
	}

	/*
	 * 计算输出层误差
	 */
	private void CalOutError() {
		double errSum = 0;
		for (int i = 0, len = outError.length; i < len; i++) {
			double out = output[i];
			outError[i] = out * (1 - out) * (target[i] - out);
			errSum += Math.abs(outError[i]);
		}
		outErrSum = errSum;
		System.out.println("输出层误差 = " + outErrSum);
	}

	/*
	 * 计算隐藏层误差
	 */
	private void CalHidError() {
		double errSum = 0;
		for (int i = 0, len = hidError.length; i < len; i++) {
			double hid = hidden[i];
			double sum = 0;
			for (int j = 0, len2 = outError.length; j < len2; j++)
				sum += outError[j] * hidOutWeights[j][i];
			hidError[i] = hid * (1 - hid) * sum;
			errSum += Math.abs(hidError[i]);
		}
		hidErrSum = errSum;
		System.out.println("隐藏层误差 = " + outErrSum);
	}

	/*
	 * 计算误差 反向传播,先计算输出层误差
	 */
	private void CalError() {
		CalOutError();
		CalHidError();
	}

	/*
	 * 调整权值
	 */
	private void AdjustWeight(double[] error, double[] layer,
			double[][] weights, double[][] prevUptWeights) {
		for (int i = 0, len1 = error.length; i < len1; i++) {
			for (int j = 0, len2 = layer.length; j < len2; j++) {
				double newVal = constant * prevUptWeights[i][j] + rate
						* error[i] * layer[j];
				weights[i][j] += newVal;
				prevUptWeights[i][j] = newVal;
			}
		}
	}

	private void AdjustWeight() {
		AdjustWeight(outError, hidden, hidOutWeights, hidOutPreWeights);
		AdjustWeight(hidError, input, inHidWeights, inHidPreWeights);
	}

	/*
	 * 调整阈值
	 */
	private void AdjustThreshold(double[] error, double[] thresholds,
			double[] preThresholds) {
		for (int i = 0, len = thresholds.length; i < len; i++) {
			double newVal = constant * preThresholds[i] + rate * error[i];
			thresholds[i] += newVal;
			preThresholds[i] = newVal;
		}
	}

	private void AdjustThreshold() {
		AdjustThreshold(outError, outThresholds, outPreThresholds);
		AdjustThreshold(hidError, hidThresholds, hidPreThresholds);
	}

	/*
	 * Sigmoid激发函数
	 */
	private double Sigmoid(double val) {
		return 1d / (1d + Math.exp(-val));
	}
}
