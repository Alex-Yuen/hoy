package it.hoyland.me.ddz.main;

import javax.microedition.lcdui.game.*;
import javax.microedition.lcdui.*;
class SplashScreen    extends GameCanvas    implements Runnable
{
    private final FLDMidlet midlet;
    private Image splashImage;
    private Image logoImage;
    private int state;
    Command beginCmd=new Command("¿ªÊ¼ÓÎÏ·",Command.ITEM,1);
    SplashScreen(FLDMidlet midlet)
    {
    	super(false);
        setFullScreenMode(true);
        this.midlet = midlet;
        splashImage =  FLDMidlet.createImage("/start1.PNG");
        logoImage = FLDMidlet.createImage("/Logo.png");
        state = 0;
        new Thread(this).start();

        
    }
    public void run()
    {
        synchronized(this)
        {
            try
            {
                wait(2000L);   // 2 seconds
                this.repaint();
                wait(2000L);
                 midlet.showMenu();
//                setFullScreenMode(false);
//                this.addCommand(beginCmd);
//                this.setCommandListener(this);
            }
            catch (InterruptedException e)
            {
                // can't happen in MIDP: no Thread.interrupt method
            }
           
        }
    }
    public void paint(Graphics g)
    {
        int CanvasWidth = getWidth();
        int CanvasHeight = getHeight();
 
        if (splashImage != null&&state==0)
        {
            g.drawImage(splashImage,
                        CanvasWidth/2,
                        CanvasHeight/2,
                        Graphics.VCENTER | Graphics.HCENTER);
            splashImage = null;
            state++;
            
        }
        else if(logoImage!=null)
        {
        	g.drawImage(logoImage, CanvasWidth/2,CanvasHeight/2,Graphics.VCENTER | Graphics.HCENTER);
        	logoImage = null;
        }
    } 
}
