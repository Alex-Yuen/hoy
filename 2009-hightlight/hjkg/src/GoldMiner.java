/*
 * GoldMiner.java
 *
 * Created on 2007年3月14日, 上午9:59
 */

import javax.microedition.midlet.*;
import javax.microedition.lcdui.*;

/**
 *
 * @author  MissYou
 * @version
 */
public class GoldMiner extends MIDlet {
    private GameCanvas gamecanvas;
    
    public GoldMiner(){
        if (gamecanvas == null){
            gamecanvas = new GameCanvas(this);
            Display.getDisplay(this).setCurrent(gamecanvas);
        }
        
    }
    
    public void startApp() {
        gamecanvas.doStartApp();
    }
    
    public void pauseApp(){
        gamecanvas.doPauseApp();
    }
    
    protected void destroyApp(boolean unconditional)
        throws MIDletStateChangeException {}
    
    public void doExit() {
        try {
            destroyApp(false);
            notifyDestroyed();
        }catch(MIDletStateChangeException e) {}
    }
}
