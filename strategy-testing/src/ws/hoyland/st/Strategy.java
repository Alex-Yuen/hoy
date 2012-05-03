package ws.hoyland.st;

import java.util.List;
import java.util.*;

import ws.hoyland.st.df.DefaultMonitor;

public abstract class Strategy {
	protected static OutputMonitor Monitor = new DefaultMonitor();
	protected static Map<String, Object> V = new HashMap<String, Object>();
		
	public abstract void run(List<String> line);
	
	public Strategy(){

	}

	public static void setMonitor(OutputMonitor monitor) {
		Monitor = monitor;
	}	
	
}
