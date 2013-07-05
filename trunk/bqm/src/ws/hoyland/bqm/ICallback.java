package ws.hoyland.bqm;

public interface ICallback {
	public static final int SUCC = 0x01;
	public static final int FAIL = 0x02;
	public void call(int key, int value);
	public void log(String info);
}
