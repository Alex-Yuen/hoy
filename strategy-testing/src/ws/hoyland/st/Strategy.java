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
		date = line.get(0);
		open = Float.parseFloat(line.get(1));
		high = Float.parseFloat(line.get(2));
		low = Float.parseFloat(line.get(3));
		close = Float.parseFloat(line.get(4));
	}
	
	public void buy(int volumn){
		if(cash-close*volumn>=0){
			cash = cash-close*volumn;
			cost = (cost*size+close*volumn)/(size+volumn);
			size += volumn;
			monitor.put(new Date(), "BUY:"+volumn);
		}else{
			monitor.put(new Date(), "CAN'T BUY");
		}
		print();
	}
	
	public void sell(int volumn){
		if(volumn>=size){
			cash = cash + close*volumn;
			cost = (cost*size-close*volumn)/(size-volumn);
			size -= volumn;
			monitor.put(new Date(), "SELL:"+volumn);
		}else{
			monitor.put(new Date(), "CAN'T SELL");
		}
		print();
	}
	
	public void print(){
		System.out.println("["+date+"]");
		System.out.print("Cash:"+cash);
		System.out.print("\tSize:"+size);
		System.out.println("\tCost:"+cost);
		System.out.println("\tClose:"+close);
		
		System.out.println("W/L:"+(close-cost)*size);
		System.out.println("W/L(%):"+(close-cost)/cost*100+"%");
		System.out.println();
	}
}
