package bobble;



import javax.microedition.lcdui.Display;
import javax.microedition.midlet.MIDlet;

public class MainMidlet extends MIDlet{
	
	private static MainCanvas mainCanvas;
	private static MainMidlet mainMidlet;
	public MainMidlet(){
		mainMidlet = this;
                mainCanvas = new MainCanvas();
	}

        public static void reset()
        {
            Display.getDisplay(mainMidlet).setCurrent(mainCanvas);
        }

        public static MainMidlet getMainMidlet()
        {
            return mainMidlet;
        }
	
	public static MainCanvas getCanvasInstance(){
		return mainCanvas;
	}
	
	public void startApp(){
		Display.getDisplay(this).setCurrent(mainCanvas);
               // mainCanvas.run();
	}
	
	public void pauseApp()
        {
	}
	
	public static void exitApp(){
              mainCanvas.jump();
		mainMidlet.destroyApp(true);
		mainMidlet.notifyDestroyed();
	}

	public void destroyApp(boolean condition){
		
	}
}