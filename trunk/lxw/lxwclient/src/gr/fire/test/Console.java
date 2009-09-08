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
package gr.fire.test;

import gr.fire.core.BoxLayout;
import gr.fire.core.CommandListener;
import gr.fire.core.Component;
import gr.fire.core.Container;
import gr.fire.core.FireScreen;
import gr.fire.core.Panel;
import gr.fire.ui.TextComponent;
import gr.fire.util.Logger;

import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.Displayable;


/**
 * This is a console for viewing any logs send to the fire Log class.
 * It demonstrates how to create a custom Logger, and it is very usefull when debuging yout code :)
 * Check inside the source for details on how to create custom loggers.
 * 
 * @see Logger
 * 
 * @author padeler
 *
 */
public class Console extends Panel implements CommandListener,Logger
{
	private Command clear,back;

	/**
	 * Default constructor of Console.
	 */
	public Console()
	{
		// A simple panel with a container using BoxLayout as the Layout manager.
		super(new Container(new BoxLayout(BoxLayout.Y_AXIS)),VERTICAL_SCROLLBAR,true);
		setLabel("Console");
		
		// In order to make this class easily resusable it will handle its own commands
		// one to clear the logs and one to return to the previous screen
		clear = new Command("Clear",Command.OK,1);
		back = new Command("Back",Command.BACK,1);
		
		setLeftSoftKeyCommand(back);setRightSoftKeyCommand(clear);
		setCommandListener(this);
	}
		
	/**
	 * Implementation of the plrintln() method defined in {@link Logger#println(String)}.
	 * 
	 */
	public void println(String txt)
	{
		// create a new TextComponent to display the text.
		TextComponent cmp = new TextComponent(txt);
		// set the text's prefSize to minimum dimensions. Boxlayout will 
		// give it more space if available only if prefSize is not set. We want to avoid that.
		cmp.validate();// validate will cause the TextComponent to calculate its minimum size based on font and text
		int d[] = cmp.getMinSize();
		cmp.setPrefSize(d[0],d[1]); 
		container.add(cmp); // add component to the container 
		container.validate();  // update the container. This will force the container to recalculate its dimensions and layout its components.
		// finally scroll to the bottom in order for the last entry to be visible.
		int coords[] = getCoordsOfComponentInContainer(cmp,container);
		setViewPortPosition(coords[0],coords[1]); // scroll to the bottom.
	}
	

	public void commandAction(Command c, Displayable arg1)
	{

	}
	
	private Component last=null;
	/**
	 * Utility method to display the console. This way console knows the Component that was previously on the
	 * FireScreen and can restore it when the hideConsole method is called.
	 * @see #hideConsole()
	 */
	public void showConsole()
	{
		last = FireScreen.getScreen().getCurrent();
		FireScreen.getScreen().setCurrent(this);
	}
	/**
	 * Removes this Console component from the FireScreen and sets back to the screen the component 
	 * that was set when the showConsole method was called (if any).
	 * 
	 * @see #showConsole() 
	 */
	public void hideConsole()
	{
		if(last!=null)
		{
			FireScreen.getScreen().setCurrent(last);
			last= null;
		}
		else FireScreen.getScreen().removeComponent(0);
	}

	// handle my internal commands.
	public void commandAction(Command c, Component cmp)
	{
		if(c==clear)
		{
			container.removeAll();
		}
		else
		{
			hideConsole();
		}
	}
}
