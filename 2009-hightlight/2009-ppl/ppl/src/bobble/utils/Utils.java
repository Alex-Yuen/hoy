package bobble.utils;

import java.io.DataInputStream;
import java.util.Random;
import javax.microedition.lcdui.Image;

public class Utils {
    static public int getKeyMask(int keyCode){
		switch (keyCode){
			case CanvasKey.NUM1:		return Key.NUM1;
			case CanvasKey.NUM2:		return Key.NUM2;
			case CanvasKey.NUM3:		return Key.NUM3;
			case CanvasKey.NUM4:		return Key.NUM4;
			case CanvasKey.NUM5:		return Key.NUM5;
			case CanvasKey.NUM6:		return Key.NUM6;
			case CanvasKey.NUM7:		return Key.NUM7;
			case CanvasKey.NUM8:		return Key.NUM8;
			case CanvasKey.NUM9:		return Key.NUM9;

			case CanvasKey.STAR:		return Key.STAR;
			case CanvasKey.NUM0:		return Key.NUM0;
			case CanvasKey.POUND:		return Key.POUND;

			case CanvasKey.UP:		return Key.UP;
			case CanvasKey.DOWN:		return Key.DOWN;
			case CanvasKey.LEFT:		return Key.LEFT;
			case CanvasKey.RIGHT:		return Key.RIGHT;
			case CanvasKey.SELECT:		return Key.SELECT;

			case CanvasKey.SOFT_L:		return Key.SOFT_L;
			case CanvasKey.SOFT_R:		return Key.SOFT_R;

			case CanvasKey.SEND:		return Key.SEND;
			case CanvasKey.END:		return Key.END;

			default:			return 0;
		}
	}

    static byte[] no = {1,2,3,4,5,6,7,8,9};
    static long l = 0l;
    static Random r = new Random();
    public static long getLong()
    {
        String n = "";
        for(int i = 0; i < 13; i ++)
        {
            n += no[r.nextInt() % 9];
        }
        long t = Long.parseLong(n);
        if(l == t)
        {
            return getLong();
        }
        else
        {
            l = t;
        }
        return t;
    }

    public static int getRank(int n)
    {
        return Math.abs(r.nextInt() % n);
    }

    public static Image createImage(String name)
    {
        DataInputStream dis = new DataInputStream(new String().getClass().getResourceAsStream(name));
        try
        {
            byte one = dis.readByte();
            byte two = dis.readByte();
            byte three = dis.readByte();
            int length = dis.readInt();
            byte[] bs = new byte[length];
            dis.read(bs);
            if(one != bs[length>>1] || two != bs[length>>2] || three != bs[length>>3])
            {
                return null;
            }
            dis.close();
            Image i = Image.createImage(bs, 0, length);
            bs = null;
            System.gc();
            return i;
        }
        catch (Exception ex)
        {
            //ex.printStackTrace();
        }
        return null;
    }


}
