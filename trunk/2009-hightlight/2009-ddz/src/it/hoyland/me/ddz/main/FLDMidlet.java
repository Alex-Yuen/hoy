package it.hoyland.me.ddz.main;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;
import javax.microedition.lcdui.*;
import javax.microedition.rms.RecordStore;
import javax.microedition.rms.RecordStoreException;

public class FLDMidlet extends MIDlet{

    public static final int	AUDIO				= 0;
    public static final int	VIBRA				= 1;
	Display display;
  //  FLDEngine engine;
  //  MenuCanvas mCanvas;
  //  LordServer lServer;
    LordCanvas lCanvas;
    MenuCanvas menu;
    public boolean []set = {true,true};//系统设置
    protected int highScore;
    protected long backTime;
    protected static final String RS_NAME = "FLD";
    protected String lastGameDate = null;
	
	public FLDMidlet() {
		display=Display.getDisplay(this);
        lCanvas = new LordCanvas(this);
        menu = new MenuCanvas(this);
	}

	protected void destroyApp(boolean unconditional) throws MIDletStateChangeException {
		 
	}

	protected void pauseApp() {
        
	}
  public void stopGame()
    {
    	lCanvas.ispaused =true;
        menu.removeResumeGame();
        menu.insertResumeGame();
    	display.setCurrent(menu);
    }
	protected void startApp() throws MIDletStateChangeException {
		// TODO Auto-generated method stub
        display.setCurrent(new SplashScreen(this));
        readRecordStore();
        calcTime(backTime);
	}

    static Image createImage(String filename) {
        Image image = null;
        try
        { 
            image = Image.createImage(filename);
        }
        catch (java.io.IOException ex)
        {
            System.out.println("can't load image " + filename);

        }
        return image;

    }

    void StartGame() {
         lCanvas.StartGame();
         menu.removeResumeGame();
         display.setCurrent(lCanvas);
    }

    void quit() {
        highScore = lCanvas.lPlayer[1].score;
    	writeRecordStore();
    	calcTime(backTime);
        try {
             Audio.getInstance().shutdown();
             lCanvas.stop();
            destroyApp(false);
        } catch (MIDletStateChangeException ex) {
            ex.printStackTrace();
        }
        notifyDestroyed();
    }

    void resumeGame() {
        lCanvas.ispaused = false;
        lCanvas.activeplayer =1;
       display.setCurrent(lCanvas);
       
    }
    void showMenu()
    {       
        display.setCurrent(menu);
    }
      private void readRecordStore()
    {
        RecordStore rs = null;
        ByteArrayInputStream bais = null;
        DataInputStream dis = null;
        try
        {
            rs = RecordStore.openRecordStore(RS_NAME, false);
            byte[] data = rs.getRecord(1);
            bais = new ByteArrayInputStream(data);
            dis = new DataInputStream(bais);
            set[0]=dis.readBoolean();
            set[1]=dis.readBoolean();
            highScore=dis.readInt();
            backTime = dis.readLong();
        }
        catch (IOException ex)
        {
            // hasBestTime will still be false
        }
        catch (RecordStoreException ex)
        {
            // hasBestTime will still be false
        }
        finally
        {
            if (dis != null)
            {
                try
                {
                    dis.close();
                }
                catch (IOException ex)
                {
                    // no error handling necessary here
                }
            }
            if (bais != null)
            {
                try
                {
                    bais.close();
                }
                catch (IOException ex)
                {
                    // no error handling necessary here
                }
            }
            if (rs != null)
            {
                try
                {
                    rs.closeRecordStore();
                }
                catch (RecordStoreException ex)
                {
                	System.out.println("error");
                    // no error handling necessary here
                }
            }
        }

    }
    private void calcTime(long backTime){
    	Calendar cal = Calendar.getInstance();
      cal.setTime(new Date(backTime));
      lastGameDate = cal.get(Calendar.YEAR)+"年";
      int month = cal.get(Calendar.MONTH);
      switch(month)
      {
      case Calendar.JANUARY:
        lastGameDate += "1月";
        break;
      case Calendar.FEBRUARY:
        lastGameDate += "2月";
        break;
      case Calendar.MARCH:
        lastGameDate += "3月";
        break;
      case Calendar.APRIL:
        lastGameDate += "4月";
        break;
      case Calendar.MAY:
        lastGameDate += "5月";
        break;
      case Calendar.JUNE:
        lastGameDate += "6月";
        break;
      case Calendar.JULY:
        lastGameDate += "7月";
        break;
      case Calendar.AUGUST:
        lastGameDate += "8月";
        break;
      case Calendar.SEPTEMBER:
        lastGameDate += "9月";
        break;
      case Calendar.OCTOBER:
        lastGameDate += "10月";
        break;
      case Calendar.NOVEMBER:
        lastGameDate += "11月";
        break;
      case Calendar.DECEMBER:
        lastGameDate += "12月";
        break;
      }
      lastGameDate += cal.get(Calendar.DAY_OF_MONTH) + "日";
      int hour = cal.get(Calendar.HOUR_OF_DAY);
      lastGameDate += (hour < 10 ? " 0" : " ") + hour;
      int minute = cal.get(Calendar.MINUTE);
      lastGameDate += (minute < 10 ? ":0" : ":") + minute;
    }

    private void writeRecordStore()
    {   backTime = new Date().getTime();
        RecordStore rs = null;
        ByteArrayOutputStream baos = null;
        DataOutputStream dos = null;
        try
        {
            rs = RecordStore.openRecordStore(RS_NAME, true);
            baos = new ByteArrayOutputStream();
            dos = new DataOutputStream(baos);
            dos.writeBoolean(set[0]);
            dos.writeBoolean(set[1]);
            dos.writeInt(highScore);
            dos.writeLong(backTime);
            byte[] data = baos.toByteArray();
            if (rs.getNumRecords() == 0)
            {
                // new record store
                rs.addRecord(data, 0, data.length);
            }
            else
            {
                // existing record store: will have one record, id 1
                rs.setRecord(1, data, 0, data.length);
            }

        }
        catch (IOException ex)
        {
            // just leave the best time unrecorded
        }
        catch (RecordStoreException ex)
        {
            // just leave the best time unrecorded
        }
        finally
        {
            if (dos != null)
            {
                try
                {
                    dos.close();
                }
                catch (IOException ex)
                {
                    // no error handling necessary here
                }
            }
            if (baos != null)
            {
                try
                {
                    baos.close();
                }
                catch (IOException ex)
                {
                    // no error handling necessary here
                }
            }
            if (rs != null)
            {
                try
                {
                    rs.closeRecordStore();
                }
                catch (RecordStoreException ex)
                {
                    // no error handling necessary here
                }
            }
        }
    }
 public void vibrate(final int onInterval, final int offInterval, final int repeat)
  {
    if (set[1]==false) return;

    new Thread(new Runnable() {
      public void run()
      {
        for (int i = 0; i < repeat; i++)
        {
          getDisplay().vibrate(onInterval);
          try
          {
            Thread.sleep(onInterval);
          } catch (InterruptedException e) {}
          if (i < repeat - 1)
          {
            getDisplay().vibrate(0);
            try
            {
              Thread.sleep(offInterval);
            } catch (InterruptedException e) {}
          }
        }
        getDisplay().vibrate(0);
      }
    },"Vibra").start();
  }
  public Display getDisplay()
  {
    return display;
  }
    public boolean getBoolean(int m_key)
  {
     return set[m_key];
  }
  public void setBoolean(int m_key, boolean b)
  {
    set[m_key]=b;
  }
}
