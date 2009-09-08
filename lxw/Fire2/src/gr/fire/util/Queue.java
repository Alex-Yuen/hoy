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
package gr.fire.util;
import gr.fire.core.Animation;
import gr.fire.core.Component;

import java.util.Vector;

/**
 * @author padeler
 *
 */
public final class Queue
{
	private final Vector queue = new Vector();
	private final Object lock = new Object();
	
	private int pointer;
	
	public Queue()
	{
		pointer=0;
	}

	public Object getNext() throws InterruptedException
	{
		synchronized (lock)
		{
			while(queue.size()==0)
			{
				lock.wait(); 
			}
			++pointer;
			if(pointer>=queue.size()) pointer=0;
		
			return queue.elementAt(pointer); 
		}
	}
	
	public void add(Animation obj)
	{
		if(obj==null) throw new NullPointerException("Parameter cannot be null");
		
		synchronized (lock)
		{
			queue.addElement(obj);
			// set pointer to a position that the next object served will be this one.
			pointer = queue.size()-2;
			lock.notify(); // notify any thread waiting for the new object.
		}
	}
	
	public boolean remove(Animation obj)
	{
		if(obj==null) throw new NullPointerException("Parameter cannot be null");
		synchronized (lock)
		{
			// set pointer to a safe position.
			pointer = 0;
			return queue.removeElement(obj);
		}
	}
	
	public void removeAll()
	{
		synchronized (lock)
		{
			// set pointer to a safe position.
			pointer = 0;
			queue.removeAllElements();
		}
	}
	
	public void removeAllWithParent(Component parent)
	{
		synchronized (lock)
		{
			// set pointer to a safe position.
			pointer = 0;
			for(int i=queue.size()-1;i>=0;--i)
			{
				Animation a = (Animation)queue.elementAt(i);
				if(a.getParent()==parent)
				{
					queue.removeElementAt(i);
				}
			}
		}
	}
	
	public int size()
	{
		return queue.size();
	}
}