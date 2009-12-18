import java.io.IOException;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.game.Sprite;
/*
 * NewClass.java
 *
 * Created on 2007年3月23日, 下午8:38
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

/**
 *
 * @author MissYou
 */
public class Gain {
    private Sprite GainSprite;
    /** Creates a new instance of NewClass */
    public Gain() {
        try{
            GainSprite = new Sprite(Image.createImage("/Gain.png"));
        }
        catch(IOException ioe){
            System.out.println("Can't load Gain file.");
        }
        GainSprite.defineReferencePixel(10, 7);
    }
    
    public Sprite getGainSprite(){
        return GainSprite;
    }
}
