package ws.hoyland.st;

import java.util.List;

public class Tester {
	private DataSource ds;
	private Strategy st;
	
	public Tester(DataSource ds, Strategy st){		
		this.ds = ds;
		this.st = st;
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

	public void start(){
		List<String> line = null;
		while((line=this.ds.fire())!=null){
			this.st.init(line);
			this.st.run();
			this.st.complete();
		}
		this.st.getMonitor().draw();
	}
	
}
