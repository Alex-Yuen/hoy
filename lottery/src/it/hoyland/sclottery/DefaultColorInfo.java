package it.hoyland.sclottery;

import java.util.Hashtable;
import javax.microedition.lcdui.Display;

public class DefaultColorInfo implements ColorInfo {

	private static Hashtable INSTANCE = new Hashtable(1);
	private Display display;
	
	private DefaultColorInfo(Display display) {
		this.display = display;
	}

	public static DefaultColorInfo getInstance(Display display){
		if(display == null){
            throw new IllegalArgumentException("Display parameter cannot be null");
        }
		
		DefaultColorInfo ret = null;
		
		if((ret = (DefaultColorInfo)INSTANCE.get(display)) == null){
			ret = new DefaultColorInfo(display);
			INSTANCE.put(display, ret);
		}
		
		return ret;		
	}
	
	public int getColor(int colorSpecifier) {
		return this.display.getColor(colorSpecifier);
		
	}

}
