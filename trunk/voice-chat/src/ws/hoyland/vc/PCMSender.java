package ws.hoyland.vc;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.List;

import ws.hoyland.util.Util;

public class PCMSender implements Runnable {
	private boolean run = false;
	private List<SocketChannel> scs = null;

	public PCMSender(List<SocketChannel> scs) {
		// TODO Auto-generated constructor stub
		this.scs = scs;
		this.run = true;
	}

	public void stop(){
		this.run = false;
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		// while(run){
		//
		// }
		try {
			File file = new File("D:\\2.pcm");
			System.out.println(file.length());
			int bufferSize = Integer.valueOf(String.valueOf(file.length()));
			byte[] audioData = new byte[bufferSize];
			InputStream in = new FileInputStream(file);
			in.read(audioData);
			in.close();
//			float sampleRate = 8000;
//			int sampleSizeInBits = 16;
//			int channels = 2;
//			boolean signed = true;
//			boolean bigEndian = false;
			// sampleRate - 每秒的样本数
			// sampleSizeInBits - 每个样本中的位数
			// channels - 声道数（单声道 1 个，立体声 2 个）
			// signed - 指示数据是有符号的，还是无符号的
			// bigEndian - 指示是否以 big-endian 字节顺序存储单个样本中的数据（false 意味着
			// little-endian）。
//			AudioFormat af = new AudioFormat(sampleRate, sampleSizeInBits,
//					channels, signed, bigEndian);
//			SourceDataLine.Info info = new DataLine.Info(SourceDataLine.class,
//					af, bufferSize);// bufferSize
//			SourceDataLine sdl = (SourceDataLine) AudioSystem.getLine(info);
//			sdl.open(af);
//			sdl.start();
			//int i = 0;
			
			
			int offset = 0;
			
			while (run) {
				//offset += sdl.write(audioData, offset, bufferSize);
				if(offset>=bufferSize) {
					//Thread.sleep(2000);
					offset = 0;
				}//else{
					Thread.sleep(64);
				//}
				//System.out.println("offset:"+offset);
				byte[] temp = Util.slice(audioData, offset, 1024);
				
				byte[] bs = new byte[temp.length+4];
//				bs[0] = 0xF;
//				bs[1] = 0xE;
				int i=0;
				for(;i<4;i++){
					bs[i] = (byte)i;
				}
				for(;i<temp.length+4;i++){
					bs[i] = temp[i-4];
				}
				
				for(SocketChannel sc: scs){ //除本身外，全部转发
					//if(sc!=channel){
//					System.out.println("WRITE:"+scs.size()+">"+bs.length);
						sc.write(ByteBuffer.wrap(bs));
					//}
				}
				offset = offset+1024;
//				Thread.sleep(2000);
//				i++;
			}

			// in2.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
