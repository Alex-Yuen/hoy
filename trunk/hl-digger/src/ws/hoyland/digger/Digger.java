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
		final float HEIGHT = 1.022f;
		
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(Digger.class.getResourceAsStream("/000300.csv")));
			while((line=br.readLine())!=null){
				days.add(line);
			}
			int total = 0;
			int real = 0;
			double close = 0.0;
			
			for(int i=20;i<days.size()-20;i++){
				String[] today = days.get(i).split(",");
				String[] yesterday = days.get(i-1).split(",");
				String[] tomorrow = days.get(i+1).split(",");
				String[] tomorrow2 = days.get(i+2).split(",");
				
				if(Float.parseFloat(yesterday[8])<Float.parseFloat(yesterday[9])&&Float.parseFloat(today[8])>=Float.parseFloat(today[9])){
					total++;
					close = Float.parseFloat(today[4])*HEIGHT;
					System.out.println(Float.parseFloat(yesterday[8]));
					if(Float.parseFloat(tomorrow[4])>close||Float.parseFloat(tomorrow2[4])>close){
						real++;
					}
				}
			}
			System.out.println(real+"/"+total+"="+(double)(Math.round(real*100/total)/100.0));
			System.out.println("OK");
		} catch (Exception e){
			e.printStackTrace();
		}
	}
}
