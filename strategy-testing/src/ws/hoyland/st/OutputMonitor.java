package ws.hoyland.st;

import java.util.Date;

public interface OutputMonitor {
	public void receive(Date time, String message);
}
