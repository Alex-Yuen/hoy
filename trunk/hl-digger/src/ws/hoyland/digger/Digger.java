package ws.hoyland.digger;

import java.io.*;
import java.util.*;

public class Digger {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		System.out.println("Testing...");
		// 测试信号出现情况下，2个交易日内增长2.2%的概率
		List<String> days = new ArrayList<String>();
		String line = null;
		final float HEIGHT = 0.022f;
		final int WIDTH_DAYS = 2;
		
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(Digger.class.getResourceAsStream("/160706.csv")));
			while((line=br.readLine())!=null){
				days.add(line);
			}
			int total = 0;
			int success = 0;
//			int fail = 0;
			double close = 0.0;
//			double low = 0.0;
			String[] today = null;
			String[] yesterday = null;
			String[] tomorrow = null;
			boolean fs = false;
//			boolean ff = false;
//			double tf = 0.0;
			
			for(int i=20;i<days.size()-20;i++){
				today = days.get(i).split(",");
				yesterday = days.get(i-1).split(",");
				fs = false;
//				ff = false;
				
				if(Float.parseFloat(yesterday[8])<Float.parseFloat(yesterday[9])&&Float.parseFloat(today[8])>=Float.parseFloat(today[9])){
					total++;
					close = Float.parseFloat(today[4])*(1+HEIGHT);
					//low = Float.parseFloat(today[4])*(1-HEIGHT);
					//System.out.println("Y:"+Float.parseFloat(yesterday[8])+"/"+Float.parseFloat(yesterday[9]));
					//System.out.println("T:"+Float.parseFloat(today[8])+"/"+Float.parseFloat(today[9]));
					for(int d=0;d<WIDTH_DAYS;d++){
						tomorrow = days.get(i+1+d).split(",");
						if(Float.parseFloat(tomorrow[2])>close){
							fs = true;
							break;
						}
					}
					
//					for(int d=0;d<WIDTH_DAYS;d++){
//						tomorrow = days.get(i+1+d).split(",");
//						if(Float.parseFloat(tomorrow[3])<=low){
//							ff = true;
//							break;
//						}
//					}
					
					if(fs){
						success++;
					}
					/**
					else{
						tomorrow = days.get(i+2).split(",");
						tf += (double)(Math.round((Float.parseFloat(tomorrow[4])-Float.parseFloat(today[4]))*1000/Float.parseFloat(today[4]))/1000);
					}
					**/
//					if(ff){
//						fail++;
//					}
				}
			}
			System.out.println(success+"/"+total+"="+(double)(Math.round(success*100/total)/100.0));
			//System.out.println(fail+"/"+total+"="+(double)(Math.round(fail*100/total)/100.0));
			//System.out.println(tf);
			System.out.println("OK");
		} catch (Exception e){
			e.printStackTrace();
		}
	}
}
