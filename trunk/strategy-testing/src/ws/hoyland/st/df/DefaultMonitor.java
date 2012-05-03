package ws.hoyland.st.df;

import java.util.Date;

import ws.hoyland.st.OutputMonitor;

public class DefaultMonitor implements OutputMonitor {

	@Override
	public void put(Date time, String message) {
		System.out.println(time+":"+message);
		
	}

}
