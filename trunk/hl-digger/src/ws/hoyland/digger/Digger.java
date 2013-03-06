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
		// 计算盈亏比
		List<String> days = new ArrayList<String>();
		String line = null;
//		final float HEIGHT_T = 0.015f;
//		final float HEIGHT_B = 0.00001f;
//		final int WIDTH_DAYS = 5;
		
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(Digger.class.getResourceAsStream("/601009.csv")));
			while((line=br.readLine())!=null){
				days.add(line);
			}
//			int total = 0;
//			int success = 0;//成功实现
//			int fail = 0;	//止损
//			int normal = 0; //正常退出
			double close = 0.0;
//			double low = 0.0;
			String[] today = null;
			String[] yesterday = null;
//			String[] tomorrow = null;
//			boolean fs = false;
//			boolean ff = false;
//			double tf = 0.0;

			//盈亏比
//			double tts = 0;
//			double ttf = 0;
//			double upheight = 0.0;
			double money = 1000000;
			int size = 0;
			int buyt = 0;
			int sellt = 0;
			
			for(int i=20;i<days.size()-20;i++){
				today = days.get(i).split(",");
				yesterday = days.get(i-1).split(",");
//				fs = false;
//				ff = false;
//				upheight = (double)(Math.round((Float.parseFloat(today[5])-Float.parseFloat(yesterday[5]))*100/Float.parseFloat(yesterday[5]))/100.0);
				
				if(Float.parseFloat(yesterday[8])<Float.parseFloat(yesterday[9])&&Float.parseFloat(today[8])>=Float.parseFloat(today[9])){
				//if(Float.parseFloat(yesterday[8])<Float.parseFloat(yesterday[9])&&Float.parseFloat(today[8])>=Float.parseFloat(today[9]) && Float.parseFloat(yesterday[5])<=Float.parseFloat(today[5])){
					//buy
					close = Float.parseFloat(today[4]);
					size += 1000;
					money -= close*size;
					buyt++;
//					
//					low = Float.parseFloat(today[4])*(1-HEIGHT_B);
//					//System.out.println("Y:"+Float.parseFloat(yesterday[8])+"/"+Float.parseFloat(yesterday[9]));
//					//System.out.println("T:"+Float.parseFloat(today[8])+"/"+Float.parseFloat(today[9]));
//					for(int d=0;d<WIDTH_DAYS;d++){
//						tomorrow = days.get(i+1+d).split(",");
//						if(Float.parseFloat(tomorrow[2])>close&&Float.parseFloat(tomorrow[3])<=low){//同时成立情况下
//							if(Float.parseFloat(tomorrow[1])>Float.parseFloat(tomorrow[4])){
//								System.out.println(tomorrow[0]);
//								fs = true;
//							}else{
//								ff = true;
//							}
//							break;
//						}else if(Float.parseFloat(tomorrow[2])>close){
//							System.out.println(tomorrow[0]);
//							fs = true;
//							break;
//						}else if(Float.parseFloat(tomorrow[3])<=low){
//							ff = true;
//							break;
//						}
//					}
//					
//					if(fs){
//						success++;
//						money = money*(1+HEIGHT_T);
//					}
//					/**
//					else{
//						tomorrow = days.get(i+2).split(",");
//						tf += (double)(Math.round((Float.parseFloat(tomorrow[4])-Float.parseFloat(today[4]))*1000/Float.parseFloat(today[4]))/1000);
//					}
//					**/
//					if(ff){
//						fail++;
//						money = money*(1-HEIGHT_B);
//					}
//					
//					if(!ff&&!fs){//2天后收盘价出局
//						tomorrow = days.get(i+2).split(",");
//						normal++;
//						//System.out.println(Math.round((Float.parseFloat(tomorrow[4])-Float.parseFloat(today[4]))*1000/Float.parseFloat(today[4])));
//						double h = (double)(Math.round((Float.parseFloat(tomorrow[4])-Float.parseFloat(today[4]))*1000/Float.parseFloat(today[4]))/1000.0);
//						tf += h;
//						money = money*(1+h);
//					}
				}else if(Float.parseFloat(yesterday[8])>Float.parseFloat(yesterday[9])&&Float.parseFloat(today[8])<=Float.parseFloat(today[9])){
					//sell
					close = Float.parseFloat(today[4]);
					money += close*size;
					size = 0;
					sellt++;
				}
			}
			System.out.print("买入次数:");
			System.out.println(buyt);
			System.out.print("卖出次数:");
			System.out.println(sellt);

			System.out.print("最终结果[100,000]");
			System.out.println(money);
			System.out.println(money+"/100,000"+"="+(double)(Math.round(money*100/100000)/100.0));
			System.out.println("OK");
		} catch (Exception e){
			e.printStackTrace();
		}
	}
}
