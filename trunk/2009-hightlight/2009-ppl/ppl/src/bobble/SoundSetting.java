/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package bobble;


import bobble.utils.Key;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import javax.microedition.media.MediaException;

/**
 *
 * @author Administrator
 */
public class SoundSetting implements Painter {

    private Painter own = null;
    private Image buffer = Image.createImage(Datas.width, Datas.height);


    public SoundSetting(Painter own) {
        this();
        this.own = own;
    }

    public SoundSetting() {
        try
        {
            Image sound = Image.createImage("/sound.png");
            int drawY = Datas.height >> 1;
            int drawX = Datas.width >> 1;
            Graphics g = buffer.getGraphics();
            g.setColor(0);
            g.fillRect(0, 0, Datas.width, Datas.height);
            g.drawImage(sound, drawX, drawY, 3);
            g.drawRegion(sound, 0, 0, 24, 23, 0, 0, Datas.height, 36);
            g.drawRegion(sound, 24, 0, 24, 23, 0, Datas.width, Datas.height, 40);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
    
    private boolean finished = false;

    public void paint(Graphics g) {
        update();
        g.drawImage(buffer, 0, 0, 0);
        if (finished) {
            next();
        }
       
    }

    private void next() {
        if (own == null)
        {
            MainCanvas.switchScreen(new GameScreen(1));
        }
        else
        {
            MainCanvas.switchScreen(own);
        }
        buffer = null;
    }

    private void update() {
        if (!finished)
        {
            if ((MainCanvas.keyPressed & Key.SOFT_L) != 0)
            {
                if(MainCanvas.sound == false)
                {
                    MainCanvas.sound = true;
                    MainCanvas.player.setLoopCount(-1);
                    try
                    {
                        MainCanvas.player.start();
                    } 
                    catch (MediaException ex)
                    {
                        ex.printStackTrace();
                    }
                }
                finished = true;
            }
            else if ((MainCanvas.keyPressed & Key.SOFT_R) != 0)
            {
                if(MainCanvas.sound == true)
                {
                    MainCanvas.sound = false;
                    try
                    {
                        MainCanvas.player.stop();
                    }
                    catch (MediaException ex)
                    {
                        ex.printStackTrace();
                    }
                }
                finished = true;
            }
        }
        MainCanvas.keyPressed = 0;
    }
}
