package ws.hoyland.st;

import java.util.*;

public abstract class Strategy {
	protected Fee fee;
	protected OutputMonitor monitor;
	protected String date;
	protected float open;
	protected float high;
	protected float low;
	protected float close;
	protected int size;
	protected float cash;
	protected float lc;
	protected float cost;
	protected int volumn;
	protected float ema10;
	protected float ema5;
	
	public abstract void run();
	
	public Strategy(Fee fee, OutputMonitor monitor){
		this.fee = fee;
		this.monitor = monitor;
	}

	public OutputMonitor getMonitor() {
		return this.monitor;
	}
	
	public Fee getFee(){
		return this.fee;
	}
	
	public void init(List<String> line){
		//System.out.println(line);
		this.date = line.get(0);
		this.open = Float.parseFloat(line.get(1));
		this.high = Float.parseFloat(line.get(2));
		this.low = Float.parseFloat(line.get(3));
		this.close = Float.parseFloat(line.get(4));
		this.volumn = Integer.parseInt(line.get(5));
		this.ema10 = Float.parseFloat(line.get(7));
		this.ema5 = Float.parseFloat(line.get(8));
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
			System.out.println(date+"*buy:"+volumn);
			//monitor.put(this.date, "BUY:"+volumn);
		}else{
			System.out.println(date+"*can't buy:"+volumn);
			//monitor.put(this.date, "/CAN'T BUY/"+volumn);
		}
	}
	
	public void sell(int volumn){
		//System.out.println(volumn+">>>"+size);
		if(volumn<=size){
			cash = cash + close*volumn;
			cost = (cost*size-close*volumn)/(size-volumn);
			size -= volumn;
			System.out.println(date+"*sell:"+volumn);
			//monitor.put(date, "SELL:"+volumn);
		}else{
			System.out.println(date+"*can't sell:"+volumn);
			//monitor.put(date, "/CAN'T SELL/"+volumn);
		}
	}
	
	public void position(float ss){
//		close*nsize/(close*size+cash)=ss/1
//		close*nsize=ss*close*size+cash*ss
//		nsize=cash*ss/close+ss*size=ss(cash/close+size);
		
		int ns = (int)(ss*(cash/close+size));
		System.out.println("ns:="+ns);
		if(ns>size){
			buy(ns-size);
		}else{
			sell(size-ns);
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
		//monitor.put(date, sb.toString());
		monitor.put(date, close+":"+cost+":"+size+":"+cash+":"+volumn+":"+ema10+":"+ema5);
	}
}
