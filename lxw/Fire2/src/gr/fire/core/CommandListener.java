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

/*
 * Created on Aug 25, 2006
 */
package gr.fire.core;



import javax.microedition.lcdui.Command;


/**
 * All command events on the FireEngine components are passed to CommandListeners. 
 * The engine does not make a distinction between Commands on components or softkey/popup commands.
 * When a command is fired, the Listener is notified for the command as well as the Component that fired it. 
 * @author padeler
 *
 */
public interface CommandListener extends javax.microedition.lcdui.CommandListener
{
	
	/**
	 * Informs the listener about a Command Action.
	 * @param cmd the command that was fired 
	 * @param c the component that fired the command
	 */
	public void commandAction(Command cmd, Component c);
}
