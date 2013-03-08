package ws.hoyland.digger;

import java.util.Random;

public class Ty {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		double earn = 0;
		double money = 100000;
		Random r = new Random();
		double bit = 0;
		
		for(int t=0;t<1;t++){
			money = 100000;
			earn = 0;
			for(int i=0;i<1000;i++){			
				if(money>100000){
					earn += money - 100000;
					money = 100000;
				}
	//			
				bit = money*0.01;
				//bit = 100;
				//System.out.println(bit);
				
				if(r.nextBoolean()){
					money += bit;
				}else{
					money -= bit;
				}
			}
			
			System.out.println(t+"->money:"+money);
			System.out.println(t+"->earn:"+earn);
		}
	}
}
