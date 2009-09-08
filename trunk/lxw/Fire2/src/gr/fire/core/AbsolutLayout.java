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
package gr.fire.core;

/**
 * @author padeler
 * 
 */
public class AbsolutLayout implements LayoutManager
{
	public static final AbsolutLayout defaultLayout= new AbsolutLayout();
	
	/**
	 * @see gr.fire.core.LayoutManager#layoutContainer(gr.fire.core.Container)
	 */
	public void layoutContainer(Container parent)
	{
		
		// the layout manager desides on the size and location of the
		// components.
		// implementation of the null layoutManager (AbsolutLayout)
		// give the components their preffered size
		int maxW = 0;
		int maxH = 0;
		for (int i = 0; i < parent.components.size(); ++i)
		{
			Component cmp = ((Component) parent.components.elementAt(i));
			if (cmp instanceof Container)
			{
				((Container) cmp).layoutManager.layoutContainer((Container) cmp);
			}
			int []prefSize = cmp.getPrefSize();
//			System.out.println("LAYOUT OF CMP "+cmp+":"+ prefSize);
			if(prefSize==null){ prefSize = cmp.getMinSize();}
			
			if (prefSize != null)
			{
				cmp.width = prefSize[0];
				cmp.height = prefSize[1];
				int mw = cmp.x + cmp.width;
				int mh = cmp.y + cmp.height;
//				System.out.println("------------x/y: ["+cmp.x+"/"+cmp.y+"]--------------MW/MH: "+mw+"/"+mh);
				if (mw > maxW)
					maxW = mw;
				if (mh > maxH)
					maxH = mh;
			}
		}
		
		int []d = parent.getPrefSize();
		if(d==null)
		{
			d = parent.getMinSize();
			
			parent.width = d[0]>maxW?d[0]:maxW;
			parent.height = d[1]>maxH?d[1]:maxH;
		}
		else
		{
			parent.width=d[0];
			parent.height=d[1];
		}
	}
}