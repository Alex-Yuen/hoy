package ws.hoyland.st;

import ws.hoyland.st.df.*;
import ws.hoyland.st.strategy.*;

public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Fee fee = new DefaultFee();
		OutputMonitor monitor = new DefaultMonitor();
		Tester tester = new Tester(new DefaultSource(), new DefaultStrategy(fee, monitor));
		tester.start();
		
	}

}
