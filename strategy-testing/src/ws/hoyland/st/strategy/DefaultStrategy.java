package ws.hoyland.st.strategy;

import ws.hoyland.st.Strategy;

public class DefaultStrategy extends Strategy {
	
	public DefaultStrategy(){
		super();
		this.cash = 1000000;
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
			this.buy(10000);
		}else {
			if((cost-close)/cost>=0.05){//>5%, 控制到2%
				//买入
//				close/0.98=(cost*size+close*sx)/(size+sx)
//				close*(size+sx)=0.98*(cost*size+close*sx)
//				close*size + close*sx = 0.98*cost*size + 0.98*close*sx
//				0.02*close*sx = (0.98*cost - close)size
//				
				//buy((int)Math.ceil((0.98*cost-close)*size/(close*0.02)));
				buy(10000);
			}else{
				//不做处理
			}
		}	
		
		this.lc = close;
	}


}
