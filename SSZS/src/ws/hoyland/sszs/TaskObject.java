package ws.hoyland.sszs;

public class TaskObject {
	private Object block;
	private Object data;

	public TaskObject(){
		this.block = new Object();
	}
	
	public Object getBlock() {
		return block;
	}
		
	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}
	
}
