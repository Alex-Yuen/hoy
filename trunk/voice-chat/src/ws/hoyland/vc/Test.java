package ws.hoyland.vc;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.SourceDataLine;

public class Test {

	public Test() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try{
//			Mixer.Info[] infos = AudioSystem.getMixerInfo();
//			for(int i=0;i<infos.length;i++){
//				System.out.println(infos[i].getName()+"="+infos[i].getDescription());
//
//				Mixer mixer = AudioSystem.getMixer(infos[i]);
//				System.out.println(mixer);
//				//System.out.println(mixer.getMaxLines(infos[i]));
//				Line[] ls = mixer.getSourceLines();
//				System.out.println(ls.length);
//				for(int x=0;x<ls.length;x++){
//					//System.out.println(ls[x]);
//				}
//			}
//			

			//Mixer.Info[] infos = AudioSystem.getMixerInfo();
			
			/**
			Mixer.Info[] minfoSet = AudioSystem.getMixerInfo();
			System.out.println("Mixers:");
			for (Mixer.Info minfo: minfoSet) {
			    System.out.println("   " + minfo.toString());

			    Mixer m = AudioSystem.getMixer(minfo);
			    System.out.println("    Mixer: " + m.toString());
			    System.out.println("      Source lines");
			    Line.Info[] slines = m.getSourceLineInfo();
			    for (Line.Info s: slines) {
				System.out.println("        " + s.toString());
			    }

			    Line.Info[] tlines = m.getTargetLineInfo();
			    System.out.println("      Target lines");
			    for (Line.Info t: tlines) {
				System.out.println("        " + t.toString());
			    }
			}
			**/
			
		     File file = new File("D:\\2.pcm");  
		     System.out.println(file.length());  
		     int offset = 0;  
		     int bufferSize = Integer.valueOf(String.valueOf(file.length())) ;  
		     byte[] audioData = new byte[bufferSize];  
		     InputStream in = new FileInputStream(file);  
		     in.read(audioData);  
	  
		     
//		     File file2 = new File("D:\\3.pcm");  
//		     System.out.println(file2.length());  
//		     //int offset2 = 0;  
//		     int bufferSize2 = Integer.valueOf(String.valueOf(file2.length())) ;  
//		     byte[] audioData2 = new byte[bufferSize2];  
//		     InputStream in2 = new FileInputStream(file2);  
//		     in2.read(audioData2);  
//		     
//		     int max = bufferSize;
//		     if(bufferSize2>bufferSize) max = bufferSize2;
//		     
//		     int min = bufferSize;
//		     if(bufferSize2<bufferSize) min = bufferSize2;
//		     
//		     
//		     byte[] rs = new byte[max];
//		     for(int i=0;i<rs.length;i++){
//		    	 if(i<min){
//		    		 rs[i] = (byte)((audioData[i]+audioData2[i])/2);
//		    	 }else{
//		    		 if(bufferSize<bufferSize2){
//		    			 rs[i] = audioData2[i];
//		    		 }else{
//		    			 rs[i] = audioData[i];
//		    		 }
//		    	 }
//		     }
		     
	         float sampleRate = 8000;  
	         int sampleSizeInBits = 16;  
	         int channels = 2;  
	         boolean signed = true;  
	         boolean bigEndian = false;  
	            // sampleRate - 每秒的样本数  
	            // sampleSizeInBits - 每个样本中的位数  
	            // channels - 声道数（单声道 1 个，立体声 2 个）  
	            // signed - 指示数据是有符号的，还是无符号的  
	            // bigEndian - 指示是否以 big-endian 字节顺序存储单个样本中的数据（false 意味着  
	            // little-endian）。  
	         AudioFormat af = new AudioFormat(sampleRate, sampleSizeInBits, channels, signed, bigEndian);  
	         SourceDataLine.Info info = new DataLine.Info(SourceDataLine.class, af, bufferSize);//bufferSize  
	         SourceDataLine sdl = (SourceDataLine) AudioSystem.getLine(info);  
	         sdl.open(af);  
	         sdl.start();  
	         while (offset < audioData.length) {  
	        	 offset += sdl.write(audioData, offset, bufferSize);  
	         } 
	         
	         in.close();
	         //in2.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}

}
