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
		final int WIDTH_DAYS = 5;
		
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(Digger.class.getResourceAsStream("/000300.csv")));
			while((line=br.readLine())!=null){
				days.add(line);
			}
			int total = 0;
			int real = 0;
			double close = 0.0;
			String[] today = null;
			String[] yesterday = null;
			String[] tomorrow = null;
			boolean flag = false;
			
			for(int i=20;i<days.size()-20;i++){
				today = days.get(i).split(",");
				yesterday = days.get(i-1).split(",");
				flag = false;
				
				if(Float.parseFloat(yesterday[8])<Float.parseFloat(yesterday[9])&&Float.parseFloat(today[8])>=Float.parseFloat(today[9])){
					total++;
					close = Float.parseFloat(today[4])*HEIGHT;
					//System.out.println("Y:"+Float.parseFloat(yesterday[8])+"/"+Float.parseFloat(yesterday[9]));
					//System.out.println("T:"+Float.parseFloat(today[8])+"/"+Float.parseFloat(today[9]));
					for(int d=0;d<WIDTH_DAYS;d++){
						tomorrow = days.get(i+1+d).split(",");
						if(Float.parseFloat(tomorrow[2])>close){
							flag = true;
							break;
						}
					}
					
					if(flag){
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
