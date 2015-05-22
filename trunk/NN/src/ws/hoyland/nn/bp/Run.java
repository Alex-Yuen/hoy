package ws.hoyland.nn.bp;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class Run {
	private final static int vectorInDef = 6;    
	private final static int vectorHidDef = 12;    
	private final static int vectorOutDef = 8;
	
	public static void main(String[] args) throws NumberFormatException, IOException {
		BP bp = new BP(vectorInDef, vectorHidDef, vectorOutDef);
		double[] input = new double[vectorInDef];
		double[] output = new double[vectorOutDef];
		double[] target = new double[vectorOutDef];
		
		File trainFile = new File("D:\\train.txt");
		File testFile = new File("D:\\test.txt");
		BufferedReader brTrain = null;
		String s = null;
		
		for(int i = 0;i < 200;i++){
			brTrain = new BufferedReader(new FileReader(trainFile));
			while((s = brTrain.readLine())!=null){
				String str[] = s.split(" ");
				StandardTarget(Integer.parseInt(str[0]),target);
				for(int j = 0,length=input.length;j < length;j ++)
					input[j] = (double)Integer.parseInt(str[j+1])/100;
				bp.Train(input, target);
			}
		}
		System.out.println("训练完毕!");
		BufferedReader brTest = new BufferedReader(new FileReader(testFile));
		while((s = brTest.readLine())!=null){
			String str[] = s.split(" ");
			for(int k = 0,length=input.length;k < length;k ++)
				input[k] = (double)Integer.parseInt(str[k+1])/100;
			bp.Test(input,output);
			double max = -2;
			int idx = 0;
			for(int m = 0,length = output.length;m < length;m++){
				if(output[m] > max){
					max = output[m];
					idx = m;
				}
			}
			System.out.println(str[0] + " = " + idx);
			for(int m = 0,length = output.length;m < length;m++){
				System.out.print(output[m] + " ");
			}
			System.out.println();
		}
		
		if(brTest!=null){
			brTest.close();
		}
	}
	
	public static void StandardTarget(int ans,double[] target){
		switch(ans){
		case 0:
			target[0]=1;target[1]=0;target[2]=0;target[3]=0;
			target[4]=0;target[5]=0;target[6]=0;target[7]=0;
			break;
		case 1:
			target[0]=0;target[1]=1;target[2]=0;target[3]=0;
			target[4]=0;target[5]=0;target[6]=0;target[7]=0;
			break;
		case 2:
			target[0]=0;target[1]=0;target[2]=1;target[3]=0;
			target[4]=0;target[5]=0;target[6]=0;target[7]=0;
			break;
		case 3:
			target[0]=0;target[1]=0;target[2]=0;target[3]=1;
			target[4]=0;target[5]=0;target[6]=0;target[7]=0;
			break;
		case 4:
			target[0]=0;target[1]=0;target[2]=0;target[3]=0;
			target[4]=1;target[5]=0;target[6]=0;target[7]=0;
			break;
		case 5:
			target[0]=0;target[1]=0;target[2]=0;target[3]=0;
			target[4]=0;target[5]=1;target[6]=0;target[7]=0;
			break;
		case 6:
			target[0]=0;target[1]=0;target[2]=0;target[3]=0;
			target[4]=0;target[5]=0;target[6]=1;target[7]=0;
			break;
		case 7:
			target[0]=0;target[1]=0;target[2]=0;target[3]=0;
			target[4]=0;target[5]=0;target[6]=0;target[7]=1;
			break;
		}
	}
}
