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
package com.endlessloopsoftware.ego.author;

import javax.swing.JFrame;
import com.endlessloopsoftware.ego.Study;

public class EgoNet
{
	public static final EgoStore	storage		= new EgoStore();
	public static Study				study			= new Study();
	public static final EgoFrame	frame			= new EgoFrame();
	
	//Construct the application
	public EgoNet()
	{
		frame.validate();
		frame.setVisible(true);
		frame.setExtendedState(frame.getExtendedState()|JFrame.MAXIMIZED_BOTH);
	}

	//Main method
	public static void main(String[] args)
	{
		//Shared.configureUI();
		new EgoNet();
	}
}