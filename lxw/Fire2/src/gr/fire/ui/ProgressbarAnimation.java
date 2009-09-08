/*
 * Fire (Flexible Interface Rendering Engine) is a set of graphics widgets for creating GUIs for j2me applications. 
 * Copyright (C) 2006-2008 Bluevibe (www.bluevibe.net)
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 * 
 */

/**
 * 
 */
package gr.fire.ui;

import gr.fire.core.Animation;
import gr.fire.core.FireScreen;
import gr.fire.core.Theme;

import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;

/**
 * @author padeler
 *
 */
public class ProgressbarAnimation extends Animation
{
	public static final long MILISECONDS_PER_FRAME=500;
	private static final int MAX_STEP=10;
	
	private String message;
	private boolean running=true;
	private Theme theme;
	private long lastFrame;
	private Font font;
	
	private int step=0;
	
	private int progressbarColor,fgcolor,fillColor;
	
	
	public void paint(Graphics g)
	{
		int w = getWidth();
		int h = getHeight();
		g.setColor(fillColor);
		g.fillRect(0,0,w,h);
		if(step>0)
		{
			g.setColor(progressbarColor);
			g.fillRoundRect(-5,0,((w)*step)/MAX_STEP,h,8,8);
		}
		g.setFont(font);
		g.setColor(fgcolor);
		int y = h/2-font.getHeight()/2;
		g.drawString(message,0,y,Graphics.TOP|Graphics.LEFT);
	}
	
	public boolean isRunning()
	{
		return running;
	}

	public ProgressbarAnimation(String str)
	{
		theme = FireScreen.getTheme();
		font = theme.getFontProperty("progressbar.font");
		progressbarColor = theme.getIntProperty("progressbar.gradient.end.color");
		fgcolor = theme.getIntProperty("progressbar.fg.color");
		fillColor = theme.getIntProperty("progressbar.gradient.start.color");
		this.message = str;
	}
	
	boolean repaintNeeded=false;

	public void progress()
	{
		step++;
		step = step%(MAX_STEP+1);	
		repaintNeeded=true;
	}
	
	public void progress(int percent)
	{
		step = (percent*MAX_STEP)/100;
		step = step%(MAX_STEP+1);	
		repaintNeeded=true;
	}

	public boolean step()
	{
		long now = System.currentTimeMillis();
		
		if(now-lastFrame>=MILISECONDS_PER_FRAME && repaintNeeded && running)
		{
			lastFrame = now;
			repaintNeeded=false;
			return true;
		}
		return false;
	}

	public void stop()
	{
		running=false;
	}

	public String getMessage()
	{
		return message;
	}

	public void setMessage(String message)
	{
		this.message = message;
		repaintNeeded=true;
	}
}