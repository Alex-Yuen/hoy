/**
 * 
 */
package gr.fire.ui;

import gr.fire.core.Animation;
import gr.fire.core.Component;

import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.game.Sprite;

/**
 * @author padeler
 *
 */
public class SpriteAnimation extends Animation
{
	private Sprite sprite=null;
	private boolean running=true;
	private boolean autoMove =false;
	
	private int dx=-4;
	private int dy=0;
	private int xmin,ymin,xmax,ymax;
	private boolean vrot,hrot;
	
	private long milisecondsPerFrame=40;
	
	private long lastFrame;
	
	
	
	public SpriteAnimation(Sprite sprite)
	{
		this.sprite=sprite;
		this.width = sprite.getWidth();
		this.height= sprite.getHeight();
	}
	
	public SpriteAnimation(Sprite sprite,Component owner)
	{
		super(owner);
		this.sprite=sprite;
		this.width = sprite.getWidth();
		this.height= sprite.getHeight();
	}
	
	public void setFps(int fps)
	{
		milisecondsPerFrame = 1000L/fps;
	}
	public int getFps()
	{
		return (int)((1000L)/milisecondsPerFrame);
	}
	
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
		sprite.paint(g);
	}
	
	

	public boolean step()
	{
		long now = System.currentTimeMillis();
		if((now-lastFrame)>=milisecondsPerFrame)
		{
			lastFrame= now;
			if(autoMove)
			{
				autoMove();
			}
			sprite.nextFrame();
			return true;
		}
		return false;
		
	}
	
	private void autoMove()
	{
		move(dx,dy);
		if(getX()<xmin || getX()>xmax)
		{
			dx = -dx;
			if(hrot)
			{
				if(dx>0)
				{
					sprite.setTransform(Sprite.TRANS_MIRROR);
					sprite.setPosition(0,0);
				}
				else
				{
					sprite.setTransform(Sprite.TRANS_NONE);
					sprite.setPosition(0,0);
				}
			}
		}
		if(getY()<ymin || getY()>ymax)
		{
			dy = -dy;
			if(vrot)
			{
				if(dy>0)
				{
					sprite.setTransform(Sprite.TRANS_MIRROR_ROT90);
					sprite.setPosition(0,0);
				}
				else
				{
					sprite.setTransform(Sprite.TRANS_NONE);
					sprite.setPosition(0,0);
				}
			}
		}
	}
	
	public void setTransform(int trans)
	{
		sprite.setTransform(trans);
		sprite.setPosition(0,0);
	}

	/* (non-Javadoc)
	 * @see gr.fire.core.Animation#stop()
	 */
	public void stop()
	{
		running=false;
	}

	public boolean isAutoMove()
	{
		return autoMove;
	}
	
	public void setAutoMoveData(int dx,int dy, int xmin,int ymin, int xmax, int ymax,boolean vrot,boolean hrot)
	{
		this.dx = dx;
		this.dy = dy;
		this.xmin = xmin;
		this.ymin = ymin;
		this.xmax = xmax;
		this.ymax = ymax;
		this.vrot = vrot;
		this.hrot = hrot;
		
	}

	public void setAutoMove(boolean autoMove)
	{
		this.autoMove = autoMove;
	}
}
