package ws.hoyland.digger;

import java.util.Random;

public class Ty {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
//		double earn = 0;
		double money = 100000;
		Random r = new Random();
		double bit = 0;
		int ss = 0;
		int sf = 0;
		
		int total = 0;
		
//		boolean flag = true;
			for(int t=0;t<1000;t++){
				money = 100000;
				ss = 0;
				sf = 0;
				bit = 0;
	//			earn = 0;
				for(int i=0;i<1000;i++){
					if(money<=80000){
						break;
					}
		//			
					bit = money*0.01;
	//				if(!flag){//加入上次失败
	//					bit = bit * 1.01;
	//				}
	//				bit = 100000 - money;
	//				if(bit==0){
	//					bit = 100;
	//				}
					//bit = 100;
					//System.out.println(bit);
					
					if(r.nextBoolean()){
	//					flag = true;
						money += bit;
						ss++;
					}else{
	//					flag = false;
						money -= bit;
						sf++;
					}
					
					if(money>110000){
						total++;
						break;
					}
	//				if(money>100000){
	//					earn += money - 100000;
	//					money = 100000;
	//				}
				}
				System.out.println(ss+"/"+sf);
				System.out.println("money:"+money);
	//			System.out.println("earn:"+earn);
			}
			System.out.println("total:"+total);
	}
}
