package ws.hoyland.st;

import java.util.*;

import ws.hoyland.st.df.DefaultMonitor;

public abstract class Strategy {
	protected OutputMonitor monitor;
	protected Map<String, Object> v;
	protected String date;
	protected float open;
	protected float high;
	protected float low;
	protected float close;
	protected int size;
	protected float cash;
	protected float lc;
	protected float cost;
	
	public abstract void run();
	
	public Strategy(){
		this.monitor = new DefaultMonitor();
		this.v = new HashMap<String, Object>();
	}

	public void setMonitor(OutputMonitor monitor) {
		this.monitor = monitor;
	}	
	
	public void init(List<String> line){
		//System.out.println(line);
		this.date = line.get(0);
		this.open = Float.parseFloat(line.get(1));
		this.high = Float.parseFloat(line.get(2));
		this.low = Float.parseFloat(line.get(3));
		this.close = Float.parseFloat(line.get(4));
	}
	
	public void complete(){
		this.lc = close;
		this.print();
	}
	
	public void buy(int volumn){
		if(cash-close*volumn>=0){
			cash = cash-close*volumn;
			cost = (cost*size+close*volumn)/(size+volumn);
			size += volumn;
			monitor.put(this.date, "BUY:"+volumn);
		}else{
			monitor.put(this.date, "/CAN'T BUY/");
		}
	}
	
	public void sell(int volumn){
		if(volumn>=size){
			cash = cash + close*volumn;
			cost = (cost*size-close*volumn)/(size-volumn);
			size -= volumn;
			monitor.put(date, "SELL:"+volumn);
		}else{
			monitor.put(date, "/CAN'T SELL/");
		}
	}
	
	private void print(){
		StringBuffer sb = new StringBuffer();
		sb.append("Cash:"+cash);
		sb.append("\tSize:"+size);
		sb.append("\tCost:"+cost);
		sb.append("\tClose:"+close+"\n");
		
		sb.append("W/L:"+(close-cost)*size+"\n");
		sb.append("W/L(%):"+(close-cost)/cost*100+"%\n\n");
		monitor.put(date, sb.toString());
	}
}
