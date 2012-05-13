package ws.hoyland.st.strategy;

import ws.hoyland.st.OutputMonitor;
import ws.hoyland.st.Strategy;

public class DefaultStrategy extends Strategy {
	
	public DefaultStrategy(OutputMonitor monitor){
		super(monitor);
		this.cash = 100000;
		this.size = 0;
		this.lc = 0;
		this.cost = 0;
	}
	
	@Override
	public void run() {
		
		if(lc==0){
			this.buy(5000);
		}else {
			if((cost-close)/cost>=0.10){//亏损情况下, >10%, 控制到5%
//				close/0.95=(cost*size+close*sx)/(size+sx)
//				close*(size+sx)=0.95*(cost*size+close*sx)
//				close*size + close*sx = 0.95*cost*size + 0.95*close*sx
//				0.05*close*sx = (0.95*cost - close)size
//				
				int tot = (int)Math.ceil((0.95*cost-close)*size/(close*0.05));
				if((tot-tot%1000)!=0){
					buy(tot-tot%1000);
				}
			}else if(close>cost){//盈利情况下, 如仓位过高, 自动减仓
				if(cost*size/(cost*size+cash)>=0.20){
//					cost*(size-sx)/(cost*(size-sx))+cash+close*sx=0.10
//					10(cost*size - cost*sx)=cost*size-cost*sx+cash+close*sx
//					9cost*size = 9cost*sx+cash+close*sx
					
					int tot = (int)Math.ceil((5*cost*size-cash)/(5*cost+close));
					if((tot-tot%1000)!=0){
						sell(tot-tot%1000);
					}
				}
			}
		}	
		
		this.lc = close;
	}


}
