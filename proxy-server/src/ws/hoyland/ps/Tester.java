package ws.hoyland.ps;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;

import javax.imageio.ImageIO;

import ij.IJ;

public class Tester {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		System.out.println("OK...");
		try{
			for(int i=1;i<16;i++){
				String path = i<10?"0"+i:""+i;
		 IJ.open("D:\\Juno\\workspace\\proxy-server\\src\\res\\pic\\"+path+".png");     
         
		 //IJ.run("Color Balance...");
		 IJ.setMinAndMax(0, 69);
	          
	        //另存图片  
		 IJ.save("D:\\Juno\\workspace\\proxy-server\\src\\res\\out\\out-"+path+"-1.png");
		 
		 //读取另存图片  
	     BufferedImage image = ImageIO.read(new FileInputStream("D:\\Juno\\workspace\\proxy-server\\src\\res\\out\\out-"+path+"-1.png"));
	     
	     int height = image.getHeight();  
	        int width = image.getWidth();  
	        
	        for(int y=0;y<height;y++){
	        	for(int x=0;x<width;x++){
	        		//image.get
	        		int px = image.getRGB(x, y);

//	        		if(x==15&&y==34){
//	        			System.out.println(px);
//	        		}
//	        		if(px==-16777216){
//	        			image.setRGB(x, y, -9408400);
//	        		}
	        		if(px!=-9408400){
	        			image.setRGB(x, y, -1);
	        		}
	        		//System.out.println(px);
	        	}
	        }
	        
	        ImageIO.write(image, "PNG", new File("D:\\Juno\\workspace\\proxy-server\\src\\res\\out\\out-"+path+"-2.png"));
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}

}
