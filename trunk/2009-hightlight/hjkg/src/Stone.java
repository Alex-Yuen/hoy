/*
 * Stone.java
 *
 * Created on 2007年3月14日, 下午2:00
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */
import javax.microedition.lcdui.Image;
import java.io.IOException;
import javax.microedition.lcdui.game.Sprite;
/**
 *石头类
 * @author MissYou
 */
public class Stone extends Target{
    
    private static final int PERVALUE = 25;
    /** Creates a new instance of Stone */
    public Stone() {
        super();
    }
    
    public Stone( int x, int y, int size ) {
        String filename = "/S" + String.valueOf(size) + ".png";
        try{        
            super.setAll( x, y, size, new Sprite(Image.createImage(filename)) );
        }catch( IOException ioe ){
            System.out.println("Can't load Stone file.");
        }
    }
    
    public int getRelValue(){
        return getSize()*PERVALUE;
    }
}
