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

import gr.fire.core.CommandListener;
import gr.fire.core.Component;
import gr.fire.core.FireScreen;
import gr.fire.core.Panel;
import gr.fire.util.Lang;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.TextBox;

/**
 * @author padeler
 *
 */
public class TextArea extends TextBox implements CommandListener
{
	private Component lastComponent;
	private InputComponent owner;
	private Command ok;
	private Command back;
	
	public TextArea(InputComponent owner)
	{
		super("", owner.getValue(), owner.getMaxLen(), owner.getTextConstraints());
		this.owner = owner;
		
		ok = new Command(Lang.get("Ok"),Command.OK,1);
		back = new Command(Lang.get("Back"),Command.BACK,1);
		addCommand(ok);
		addCommand(back);
		setCommandListener(this);
		lastComponent = FireScreen.getScreen().getCurrent();
	}

	public void commandAction(Command cmd, Component c)
	{

	}

	public void commandAction(Command cmd, Displayable d)
	{
		if(cmd==ok)
		{ // update owner's value, return to lastComponent
			owner.setValue(getString());
		}
		// else its back
		FireScreen screen = FireScreen.getScreen();
		screen.setCurrent(lastComponent);
		screen.setSelectedComponent(owner);
		if(lastComponent instanceof Panel)
		{
			((Panel)lastComponent).scrollToSelectedComponent(-1,-1);
		}
		// fully repaint screen (some phones, like nokia s60, need this)
		screen.repaint();
	}
}
