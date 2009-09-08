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
public class SoftKeyAnimation extends Animation
{
	public static final long MILISECONDS_PER_FRAME=50;
	private static final int FRAMES=3;
	private int frameCount=0;
	
	private String str;

	private boolean running=true;
	private Theme theme;
	
	private Font font;


	/* (non-Javadoc)
	 * @see gr.fire.core.Animation#isRunning()
	 */
	public boolean isRunning()
	{
		return running;
	}

	/* (non-Javadoc)
	 * @see gr.fire.core.Animation#paint(javax.microedition.lcdui.Graphics)
	 */
	public void paint(Graphics g)
	{
		int fh = font.getHeight();
		int y= getHeight()/2 - fh/2;
		if(frameCount==0)
		{ // normally paint the softkey
			if(backgroundColor!=Theme.TRANSPARENT)
			{
				g.setColor(backgroundColor);
				g.fillRect(0,y,getWidth(),fh);				
			}
			g.setColor(foregroundColor);
		}
		else
		{// just paint the softkey str with inversed colors.
			g.setColor(foregroundColor);
			g.fillRect(0,y,getWidth(),fh);				
			g.setColor(backgroundColor);
		}
		g.setFont(font);
		g.drawString(str,0,y,Graphics.TOP|Graphics.LEFT);
	}

	/* (non-Javadoc)
	 * @see gr.fire.core.Animation#setup(gr.fire.core.Component, gr.fire.core.Component, java.lang.Object)
	 */
	public SoftKeyAnimation(String str)
	{
		this.str=str;
		this.theme = FireScreen.getTheme();
		this.font = theme.getFontProperty("navbar.font");
	}

	/* (non-Javadoc)
	 * @see gr.fire.core.Animation#step()
	 */
	public boolean step()
	{
		frameCount++;
		if(frameCount>=FRAMES) running=false;
		return (frameCount==1);
	}

	/* (non-Javadoc)
	 * @see gr.fire.core.Animation#stop()
	 */
	public void stop()
	{
		running=false;
	}
}