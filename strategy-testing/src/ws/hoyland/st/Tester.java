package ws.hoyland.st;

import java.util.List;

import ws.hoyland.st.df.*;
import ws.hoyland.st.strategy.*;

public class Tester {

	private DataSource ds;
	private Strategy st;
	private OutputMonitor monitor;
	private Fee fee;
	
	public Tester(){
		this.ds = new DefaultSource();
		this.st = new DefaultStrategy();
		this.monitor = new DefaultMonitor();
		this.fee = new DefaultFee();
	}
		
	public DataSource getDs() {
		return ds;
	}


	public void setDs(DataSource ds) {
		this.ds = ds;
	}


	public Strategy getSt() {
		return st;
	}


	public void setSt(Strategy st) {
		this.st = st;
	}


	public OutputMonitor getMonitor() {
		return monitor;
	}


	public void setMonitor(OutputMonitor monitor) {
		this.monitor = monitor;
	}


	public Fee getFee() {
		return fee;
	}


	public void setFee(Fee fee) {
		this.fee = fee;
	}

	public void start(){
		List<String> line = null;
		while((line=this.ds.fire())!=null){
			this.st.run(line);
		}
	}
	
}
