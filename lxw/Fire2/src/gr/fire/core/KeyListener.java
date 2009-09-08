
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

package gr.fire.core;

/**
 * A KeyListener is notified when a key is pressed, released or repeated on a Component.
 * @author padeler
 *
 */
public interface KeyListener 
{
	/**
	 * The key with keycode=code was pressed while the focus was in component src
	 * @param code
	 * @param src
	 */
	public void keyPressed(int code, Component src);
	/**
	 * The key with keycode=code was released while the focus was in component src
	 * @param code
	 * @param src
	 */
	public void keyReleased(int code, Component src);
	/**
	 * The key with keycode=code was repeated while the focus was in component src
	 * @param code
	 * @param src
	 */
	public void keyRepeated(int code, Component src);
}
