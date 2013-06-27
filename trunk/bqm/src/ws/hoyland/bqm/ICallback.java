package ws.hoyland.bqm;

public interface ICallback {
	public static final int SUCC = 0x01;
	public void call(int key, int value);
}
