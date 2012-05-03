package ws.hoyland.st.strategy;

import java.util.Date;
import java.util.List;

import ws.hoyland.st.Strategy;

public class DefaultStrategy extends Strategy {
	
	public DefaultStrategy(){
		super();
	}
	
	@Override
	public void run(List<String> line) {
		this.monitor.put(new Date(), "OK");
	}


}
