package ws.hoyland.vc;

import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.TargetDataLine;

import ws.hoyland.util.Converts;
import ws.hoyland.util.Util;

public class Sender implements Runnable {

	private SocketChannel sc;
	private boolean run;
	private byte[] data = new byte[1024]; // 此处的1024可以情况进行调整，应跟下面的1024应保持一致
	private int size = 0;
	private TargetDataLine line = null;
	private byte[] key = null;

	public Sender(SocketChannel sc) {
		this.sc = sc;
		this.run = true;
		this.key = Util.genKey(4);

		// 采集并发送
		AudioFormat format = new AudioFormat(//AudioFormat.Encoding.PCM_SIGNED,
				8000, // sampleRate
				16, // sampleSizeInBits
				2, // channels
				true,
				true); // bigEndian

		DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);

		try {
			line = (TargetDataLine) AudioSystem.getLine(info);
			line.open(format, line.getBufferSize());
		} catch (Exception e) {
			e.printStackTrace();
		}// end catch

		// line.start();
	}

	@Override
	public void run() {
		if(line==null){
			return;
		}
		
		line.start();
		
//		  FloatControl fc=(FloatControl)line.getControl(FloatControl.Type.PAN);
//          double value=2;
//          float dB = (float)
//                (Math.log(value==0.0?0.0001:value)/Math.log(10.0)*20.0);
//          fc.setValue(dB);
          
		while (run) {
			try {
//				synchronized (this) {
//					this.wait(100);
//				}

				size = line.read(data, 0, 1024);// 取数据（1024）的大小直接关系到传输的速度，一般越小越快，
			
				byte[] bs = new byte[size+4];
//				bs[0] = 0xF;
//				bs[1] = 0xE;
				int i=0;
				for(;i<4;i++){
					bs[i] = key[i];
				}
				for(;i<size+4;i++){
					bs[i] = data[i-4];
				}
				
				try {
//					sc.write(ByteBuffer.wrap(Util.slice(data, 0, size)));// 写入网络流
					//if(calculateRMSLevel(data)>51.5){
					if(caculateDB(data)>-28){
						sc.write(ByteBuffer.wrap(bs));// 写入网络流
						System.out.println(Converts.bytesToHexString(this.key)+" -> "+bs.length + "["+caculateDB(data)+"]");
					}
					//}
				} catch (Exception ex) {
					break;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		line.stop();
		line.close();
		line = null;
	}

	public void stop() {
		this.run = false;
	}
	
	private int caculateDB(byte[] buffer){
		long v = 0;
		short t = 0;
		for (int i = 0; i < buffer.length; i+=2) {
			t = (short)(((buffer[i]<<8) & 0xFF00) | (buffer[i+1]&0xFF));
			//v += buffer[i] * buffer[i];
			v += t*t;
		}
		int db = -90;
		if (v != 0) {
			db = (int) (20 * Math.log10(Math.sqrt(v/(buffer.length/2)) / 32768f));
		}
		return db;
	}
	
	protected double calculateRMSLevel(byte[] audioData)
	{ // audioData might be buffered data read from a data line
	    long lSum = 0;
	    for(int i=0; i<audioData.length; i++)
	        lSum = lSum + audioData[i];

	    double dAvg = lSum / audioData.length;

	    double sumMeanSquare = 0d;
	    for(int j=0; j<audioData.length; j++)
	        sumMeanSquare = sumMeanSquare + Math.pow(audioData[j] - dAvg, 2d);

	    double averageMeanSquare = sumMeanSquare / audioData.length;
	    return (Math.pow(averageMeanSquare, 0.5d) + 0.5);
	}
}
