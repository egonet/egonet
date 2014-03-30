/***
 * Copyright (c) 2008, Endless Loop Software, Inc.
 * 
 * This file is part of EgoNet.
 * 
 * EgoNet is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 * 
 * EgoNet is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.egonet.util.listbuilder;


import javax.swing.JFrame;

import org.egonet.util.ObservableList;
import org.egonet.util.Selection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Observable;
import java.util.Observer;
import java.util.Iterator;

public class OldListTester implements Observer
{
	final private static Logger logger = LoggerFactory.getLogger(OldListTester.class);

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		logger.info("Before");
		new OldListTester();
		logger.info("After");
	}
	
	private ListBuilder listBuilder;
	public OldListTester()
	{
		super();
		this.listBuilder = new ListBuilder();
		this.listBuilder.addListObserver(this);
		this.listBuilder.setName("name field");
		this.listBuilder.setTitle("title field");
		
		String s = "";
		for(int i = 0; i < 10; i++)
			s+= "Lorem ipsum dolor. ";
		
		this.listBuilder.setDescription(s);
		this.listBuilder.setEditable(false);
		
		
		JFrame frame = new JFrame();
		frame.add(listBuilder);
		frame.setSize(400,500);
		frame.setVisible(true);
	}
	
	public void update(Observable o, Object arg)
	{
		if(arg.getClass().equals(ObservableList.class))
		{
			ObservableList lb = (ObservableList)arg;
			logger.info("o=" + o +", arg=" + arg );
			for(Iterator iterator = lb.iterator();iterator.hasNext();)
			{
				Object nextObject = iterator.next();
				if(nextObject.getClass().equals(Selection.class))
				{
					Selection selection = (Selection)nextObject;
					//logger.info("Selection: string=\"" + selection.string +"\", adjacent = " + selection.adjacent + ", index = " + selection.index + ", value = " + selection.value);
					logger.info("new Selection(\""+selection.string+"\", "+selection.value+", "+selection.index+", " + selection.adjacent + "),");
				} else {
					logger.info("Class = "+nextObject.getClass()+", value = " + nextObject);
				}
			}
		} else {
			logger.info("o=" + o +", arg=" + arg);
		}
	}

}
