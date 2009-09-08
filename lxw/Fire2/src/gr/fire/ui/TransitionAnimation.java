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
import gr.fire.core.Component;

import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

/**
 * @author padeler
 *
 */
public class TransitionAnimation extends Animation
{
	public static final int TRANSITION_NONE = 0x00000000;

	public static final int TRANSITION_LEFT = 0x00000001;
	public static final int TRANSITION_RIGHT = 0x00000002;
	
	public static final int TRANSITION_CARD = 0x00000100;
	public static final int TRANSITION_SCROLL= 0x00000200;
	public static final int TRANSITION_FLIP= 0x00000400;
	
	
	private static Image offScreen = null;
	
	public static final long MILISECONDS_PER_FRAME=40;
	
	public static final int FRAMES = 3; // number of frames in this animation.
	
	private int properties;
	private int frameCount=0;
	
	
	private long lastFrame;
	
	private Component sourceCmp,destinationCmp;
	
	private int diff;
	
	private boolean paintOffScreen=false;
	
	private int animationX,animationY;
	
	public void paint(Graphics dest)
	{		
		if(offScreen==null) return;
		int transition = properties&0x0000FF00;
		int direction = properties & 0x000000FF;
		
		if(paintOffScreen)
		{
			paintOffScreen=false;
			Graphics g = offScreen.getGraphics();
			if(transition==TRANSITION_CARD)
			{
				destinationCmp.paint(g);
			}
			else if(transition==TRANSITION_SCROLL)
			{
				if(direction==TRANSITION_LEFT)
				{
					sourceCmp.paint(g);
					g.translate(-g.getTranslateX()+width,-g.getTranslateY());
					g.setClip(0,0,width,height);
					destinationCmp.paint(g);
				}
				else if(direction==TRANSITION_RIGHT)
				{
					destinationCmp.paint(g);
					g.translate(-g.getTranslateX()+width,-g.getTranslateY());
					g.setClip(0,0,width,height);
					sourceCmp.paint(g);
				}			
			}
		}	
		
		switch(transition)
		{
		case TRANSITION_CARD:
			{
				Graphics g = dest;
				g.drawImage(offScreen,animationX,animationY,Graphics.TOP|Graphics.LEFT);			
				break;
			}
		case TRANSITION_FLIP:
			
			break;
		case TRANSITION_SCROLL:
			{
				Graphics g = dest;
				if(direction==TRANSITION_RIGHT)
				{
					g.drawImage(offScreen,animationX,animationY,Graphics.TOP|Graphics.LEFT);
				}
				else
				{
					g.drawImage(offScreen,animationX-width,animationY,Graphics.TOP|Graphics.LEFT);
				}
				break;
			}
		}
	}
	
	public boolean isRunning()
	{
		return (frameCount<FRAMES);
	}

	public TransitionAnimation(Component sourceCmp, Component destinationCmp,int properties)
	{
		super(destinationCmp); 
		this.properties = properties;
		
		setX(sourceCmp.getX());
		setY(sourceCmp.getY());
		setWidth(sourceCmp.getWidth());
		setHeight(sourceCmp.getHeight());
		
		this.sourceCmp=sourceCmp;
		this.destinationCmp=destinationCmp;
		
		int transition = properties&0x0000FF00;
		if(offScreen==null || (offScreen!=null && (offScreen.getWidth()!=width || offScreen.getHeight()!=height)))
		{
			if(transition==TRANSITION_CARD)
				offScreen = Image.createImage(width,height);
			else if(transition==TRANSITION_SCROLL)
				offScreen = Image.createImage(2*width,height);
		}
		
		diff = width/FRAMES;

		int direction = properties & 0x000000FF;
		if(direction==TRANSITION_LEFT)
		{
			animationX = width;
		}
		else if(direction==TRANSITION_RIGHT)
		{
			animationX=-width;
		}
		animationY = 0;
		

		Graphics g = offScreen.getGraphics();
		g.translate(-g.getTranslateX(),-g.getTranslateY());
		g.setClip(0,0,offScreen.getWidth(),offScreen.getHeight());
		
		paintOffScreen=true;
		
		lastFrame = System.currentTimeMillis();
		
	}

	public boolean step()
	{
		long now = System.currentTimeMillis();
		if(now-lastFrame>=MILISECONDS_PER_FRAME)
		{
			lastFrame=now;
			frameCount++;
			int direction = properties & 0x000000FF;
			switch (direction)
			{
				case TRANSITION_LEFT:
					animationX -= diff;
					break;
				case TRANSITION_RIGHT:
					animationX += diff;
					break;
			}
			return true;
		}
		return false;
	}

	/* (non-Javadoc)
	 * @see gr.fire.core.Animation#stop()
	 */
	public void stop()
	{
		frameCount = FRAMES;
	}

}
