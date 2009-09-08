/**
 * 
 */
package gr.fire.core;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

/**
 * @author padeler
 *
 */
public class SplashScreen extends Canvas
{
	private Font loadfont = Font.getFont(Font.FACE_SYSTEM, Font.STYLE_ITALIC, Font.SIZE_SMALL);
	private Font titlefont = Font.getFont(Font.FACE_SYSTEM, Font.STYLE_BOLD, Font.SIZE_MEDIUM);
	
	private Image logo=null;
	private String title;
	private int bgColor=0xFFFFFFFF,fgColor=0x00000000;
	
	public SplashScreen()
	{
		super();
	}
	
	public void paint(Graphics g)
	{
		int width;
		int height;
		width = getWidth();
		height =getHeight();

		g.setColor(bgColor);

		g.fillRect(0, 0, width, height);

		g.setColor(fgColor);

		String txt = "Loading...";
		int llen = loadfont.stringWidth(txt)/2;
		int tlen=0;
		int imx=0,imy=0;
		if(logo!=null)
		{
			imx = logo.getWidth()+4;
			imy = logo.getHeight();
		}
		if(title!=null)
		{
			tlen = titlefont.stringWidth(title)/2;
		}
		else
		{
			imx = logo.getWidth()/2;
		}
		int logoy = titlefont.getHeight(); 
		int voff = logoy;
		if(voff<imy) voff = imy;
		
		if(logo!=null)
		{
			g.drawImage(logo,width/2 -tlen-imx,height/2 -voff,Graphics.TOP|Graphics.LEFT);
		}
		if(title!=null)
		{
			g.setFont(titlefont);
			g.drawString(title, (width / 2)-tlen, height / 2 -voff/2-logoy/2, Graphics.TOP | Graphics.LEFT);
		}
		g.setFont(loadfont);
		g.drawString(txt, (width / 2)-llen, height / 2, Graphics.TOP | Graphics.LEFT);
	}

	public String getTitle()
	{
		return title;
	}

	public void setTitle(String title)
	{
		this.title = title;
	}

	public Image getLogo()
	{
		return logo;
	}

	public void setLogo(Image logo)
	{
		this.logo = logo;
	}

	public int getBgColor()
	{
		return bgColor;
	}

	public void setBgColor(int bgColor)
	{
		this.bgColor = bgColor;
	}

	public int getFgColor()
	{
		return fgColor;
	}

	public void setFgColor(int fgColor)
	{
		this.fgColor = fgColor;
	}
}
