/*
 * Gold.java
 *
 * Created on 2007年3月14日, 上午10:00
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */
import javax.microedition.lcdui.Image;
import java.io.IOException;
import javax.microedition.lcdui.game.Sprite;
/**
 *金块类
 * @author MissYou
 */
public class Gold extends Target {
       
    private static final int PERVALUE = 100;
    
    public Gold(){
        super();
    }
    
    public Gold( int x, int y, int size ) {
        String filename = "/G" + String.valueOf(size) + ".png";
        try{        
            super.setAll(x, y, size, new Sprite(Image.createImage(filename)) );
        }catch( IOException ioe ){
            System.out.println("Can't Load Gold file.");
        }
    }
    
    public int getRelValue(){
        return getSize()*PERVALUE;
    }
}
