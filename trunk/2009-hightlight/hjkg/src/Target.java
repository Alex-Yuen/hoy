/*
 * Target.java
 *
 * Created on 2007��3��14��, ����1:02
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */
import javax.microedition.lcdui.*;
import javax.microedition.lcdui.game.Sprite;

/**
 *Ŀ����࣬����������X��Y����СSize
 * @author MissYou
 */
public class Target {
    private int Size;
    private Sprite TargetSprite;
    
    /** Creates a new instance of Target */
    public Target() {
        Size = 0;
        TargetSprite = null;
    }
    
    public Target( int x, int y, int size, Sprite targesprite  ){
        setAll(x, y, size, targesprite);
    }
    
    public int getSize(){
        return Size;
    }
    
    public int getX(){
        return TargetSprite.getX();
    }
    
    public int getY(){
        return TargetSprite.getY();
    }
    
    public Sprite getTargetSprite(){
        return TargetSprite;
    }
    
    public void setXY( int x, int y ){
        TargetSprite.setPosition(x, y);
    }
    
    public void setAll( int x, int y, int size, Sprite targesprite ){        
        TargetSprite = targesprite;
        setXY(x, y);
        Size = size;
    }
}
