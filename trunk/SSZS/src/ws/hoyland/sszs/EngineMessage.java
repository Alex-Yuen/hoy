package ws.hoyland.sszs;

public class EngineMessage {

	private int tid; //task id
	private int type;
	private Object data;
	
	public int getTid() {
		return tid;
	}

	public void setTid(int tid) {
		this.tid = tid;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}

	public EngineMessage() {
		// TODO Auto-generated constructor stub
	}

}
