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

import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

/**
 * @author padeler
 *
 */
public class FlyAnimation extends Animation
{
	public static final long MILISECONDS_PER_FRAME=40;
	private static final int MAX_STEP=4;
	
	private boolean running=true;
	private long lastFrame;
	private int xstep=0,ystep=0,step=0;
	private int endX,endY;
	
	
	private Image offscreen=null;
	
	public FlyAnimation(int startX,int startY,int endX,int endY,int w,int h)
	{
		this.endX = endX;
		this.endY = endY;
		
		int totalX = endX-startX;
		int totalY = endY-startY;
		xstep = totalX/MAX_STEP;
		ystep = totalY/MAX_STEP;
		setX(startX);
		setY(startY);
		width = w;
		height = h;
	}

	public boolean isRunning()
	{
		return running;
	}

	/* (non-Javadoc)
	 * @see gr.fire.core.Animation#paint(javax.microedition.lcdui.Graphics)
	 */
	public void paint(Graphics g)
	{
		if(offscreen==null)
		{
			if(!parent.isValid()) return;
			offscreen = Image.createImage(parent.getWidth(),parent.getHeight());
			parent.paint(offscreen.getGraphics());
		}
		g.drawImage(offscreen,0,0,Graphics.TOP|Graphics.LEFT);
	}

	public boolean step()
	{
		long now = System.currentTimeMillis();
		
		if(step>=MAX_STEP)
		{
			stop();
		}
		else if(now-lastFrame>=MILISECONDS_PER_FRAME)
		{
			lastFrame = now;
			move(xstep,ystep);
			step++;
		}
		return running;
	}

	public void stop()
	{
		setPosition(endX,endY);
		running=false;
	}

}
