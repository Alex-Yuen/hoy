package ws.hoyland.st.df;

import ws.hoyland.st.OutputMonitor;

public class DefaultMonitor implements OutputMonitor {

	@Override
	public void put(String date, String message) {
		System.out.println("["+date+"]:"+message);
		
	}

}
