package ws.hoyland.st.strategy;

import ws.hoyland.st.Strategy;

public class DefaultStrategy extends Strategy {
	
	public DefaultStrategy(){
		super();
		this.cash = 100000;
		this.size = 0;
		this.lc = 0;
		this.cost = 0;
	}
	
	@Override
	public void run() {
//		for(String word:line){
//			Monitor.put(new Date(), word);
//		}
		
		
		if(lc==0){
			this.buy(1000);
		}else {
			if((cost-close)/cost>=0.10){//>10%, 控制到5%
				//买入
//				close/0.98=(cost*size+close*sx)/(size+sx)
//				close*(size+sx)=0.98*(cost*size+close*sx)
//				close*size + close*sx = 0.98*cost*size + 0.98*close*sx
//				0.02*close*sx = (0.98*cost - close)size
//				
				int tot = (int)Math.ceil((0.95*cost-close)*size/(close*0.05));
				if((tot-tot%1000)!=0){
					buy(tot-tot%1000);
				}
				//buy(10000);
			}else if((close-cost)/cost>0){
				//不做处理
			}
		}	
		
		this.lc = close;
	}


}
