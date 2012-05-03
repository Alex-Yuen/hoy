package ws.hoyland.st;

import java.util.List;
import java.util.*;

import ws.hoyland.st.df.DefaultMonitor;

public abstract class Strategy {
	protected OutputMonitor monitor;
	protected Map<String, Object> v;
		
	public abstract void run(List<String> line);
	
	public Strategy(){
		this.monitor = new DefaultMonitor();
		this.v = new HashMap<String, Object>();
	}
}
