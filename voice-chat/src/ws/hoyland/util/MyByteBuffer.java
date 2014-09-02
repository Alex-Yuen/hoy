package ws.hoyland.util;

import java.io.ByteArrayOutputStream;

public class MyByteBuffer {
	private ByteArrayOutputStream bos = null;
	
	public MyByteBuffer() {
		bos = new ByteArrayOutputStream();
		
	}
	
	public void put(byte[] input){
		synchronized(this){
			try{
				bos.write(input);
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	}
	
	public void get(byte[] output){
		synchronized(this){
			byte[] temp = bos.toByteArray();
			int i = 0;
			for(;i<output.length;i++){
				output[i] = temp[i];
			}
			byte[] next = new byte[temp.length-i];
			for(int j=0;i<temp.length;i++,j++){
				next[j] = temp[i];
			}
			bos.reset();
			try{
				bos.write(next);
			}catch(Exception e){
				e.printStackTrace();
			}
		}	
	}
	
	public int size(){
		return bos.size();
	}
}
