package bobble;
import bobble.utils.Utils;
import java.io.InputStream;
import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;
import javax.microedition.media.Manager;
import javax.microedition.media.Player;
import javax.microedition.media.control.VolumeControl;


public class MainCanvas extends Canvas implements Runnable {//??????????????????

    public static int keyPressed = 0;   // keyPressed in this paint cycle
    public static int keyReleased = 0;   // keyReleased in this paint cycle
    public static int keyKeptPressed = 0; //???????????
    // temp key events
    int currentKeyPressed = 0;
    int currentKeyReleased = 0;

    public static boolean sound = false;//?????????
    private static Painter currentScreen;

    public static Player player = null;

    private boolean jump = true;


    public MainCanvas() {
        setFullScreenMode(true);
        if(this.getHeight() > Datas.height)
        {
            Datas.height = this.getHeight();
        }
        if(this.getWidth() > Datas.width)
        {
            Datas.width = this.getWidth();
        }

         try
        {
             if(player == null)
             {
                InputStream is = getClass().getResourceAsStream("/sound.mid");
                player = Manager.createPlayer(is,"audio/midi");
                player.realize();
                VolumeControl vc = (VolumeControl)player.getControl("VolumeControl");
                vc.setLevel(50);
             }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        switchScreen(new SoundSetting());
        new Thread(this).start();
    }

    //public static boolean paint = true;
    public void run() {
        try
        {
            long time;
            while (jump)
            {
                time = System.currentTimeMillis();
                Datas.now = time;
                repaint();
                serviceRepaints();
                long timeTakenForPainting = System.currentTimeMillis() - time;
                if (timeTakenForPainting < 40)
                {
                    Thread.sleep(40 - timeTakenForPainting);
                }
            }
        } catch (Exception e) {
            System.out.println("Error " + e);
        }
    }

    public void jump()
    {
        jump = false;
    }

    protected void keyPressed(int keyCode) {
        int myKey = Utils.getKeyMask(keyCode);
        currentKeyPressed |= myKey;
        keyPressed |= myKey;
        keyKeptPressed |= myKey;
    }

    protected void keyReleased(int keyCode) {
        int myKey = Utils.getKeyMask(keyCode);
        keyReleased |= myKey;
        keyKeptPressed &= ~myKey;
    }
    public static Font smallFont = Font.getFont(Font.FACE_SYSTEM, Font.STYLE_PLAIN, Font.SIZE_SMALL);

    public void paint(Graphics g) {
        g.setFont(smallFont);
        g.setColor(0);
        g.fillRect(0, 0, Datas.width, Datas.height);
        if(currentScreen!=null)currentScreen.paint(g);
    }

    public static void switchScreen(Painter s) {
        keyPressed = 0;   // keyPressed in this paint cycle
        keyReleased = 0;   // keyReleased in this paint cycle
        keyKeptPressed = 0;
        currentScreen = null;
        currentScreen = s;
    }
    
    public Painter getCurrentScreen()
    {
        return currentScreen;
    }

    protected void hideNotify() {
        super.hideNotify();
    }
}

