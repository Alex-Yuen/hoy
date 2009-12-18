/*
 * Hook.java
 *
 * Created on 2007年3月15日, 下午4:49
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import java.io.IOException;
import javax.microedition.lcdui.game.Sprite;
/**
 *矿勾
 * @author MissYou
 */
public class Hook {
    public static final int SPEED = 5;
    public static final int INIX = 120;
    public static final int INIY = 37;
    private static final double [] TRIANGLE = {0.9848, 0.9397, 0.8660, 
        0.7660, 0.6427, 0.5, 0.3420, 0.1736 };
    private int nowSpeed;
    private Image Source;
    private double X, Y;
    private int currentImage;
    private Sprite HookSprite;
    
    /** Creates a new instance of Hook */
    public Hook() {
        try{
            Source = Image.createImage("/GoldHook.png");
            HookSprite = new Sprite(Source, 30, 30);
            HookSprite.defineReferencePixel(17, 8);
            Initialize();
        } catch(IOException ioe){
            System.out.println("GoldHook can't be loaded.");
        }
    }
    
    public void Initialize(){
        getHookSprite().setVisible(true);
        getHookSprite().setFrame(0);
        getHookSprite().setTransform(Sprite.TRANS_NONE);
        nowSpeed = SPEED;
        X = INIX;
        Y = INIY;
        currentImage = 0;
        HookSprite.setRefPixelPosition((int)X,(int)Y);
    }
    
    public int getnowSpeed(){
        return nowSpeed;
    }
    
    public int getX(){
        return (int)X;
    }
    
    public int getY(){
        return (int)Y;
    }
    
    public void setXY(int x, int y){
        X = x;
        Y = y;
    }
    
    public void setnowSpeed(int nowspeed){
        nowSpeed = nowspeed;
    }
    
    public void setCurrentImage( int currentimage ){
        currentImage = currentimage;
    }
    
    public Image getSource(){
        return Source;
    }
    
    public Sprite getHookSprite(){
        return HookSprite;
    }   
    
    public void extReSetXY( boolean isLeft ){
        if ( HookSprite.getFrame() == 8 ){
            Y = Y+nowSpeed;
        }
        else{
            if ( isLeft == true ){
                X = X-nowSpeed*TRIANGLE[HookSprite.getFrame()];
                Y = Y+nowSpeed*TRIANGLE[TRIANGLE.length-HookSprite.getFrame()-1];
            }
            else{
                X = X+nowSpeed*TRIANGLE[TRIANGLE.length-HookSprite.getFrame()-1];           
                Y = Y+nowSpeed*TRIANGLE[HookSprite.getFrame()];
            }
        }
        HookSprite.setRefPixelPosition((int)X, (int)Y);
    }
    
    public void backReSetXY( boolean isLeft ){
        if ( HookSprite.getFrame() == 8 ){
            Y = Y-nowSpeed;
        }
        else{
            if ( isLeft == true ){
                X = X+nowSpeed*TRIANGLE[HookSprite.getFrame()];
                Y = Y-nowSpeed*TRIANGLE[TRIANGLE.length-HookSprite.getFrame()-1];
            }
            else{
                X = X-nowSpeed*TRIANGLE[TRIANGLE.length-HookSprite.getFrame()-1];           
                Y = Y-nowSpeed*TRIANGLE[HookSprite.getFrame()];
            }
        }
        HookSprite.setRefPixelPosition((int)X, (int)Y);
    }
}