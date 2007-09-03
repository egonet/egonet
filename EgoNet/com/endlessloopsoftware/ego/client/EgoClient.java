/****
 * 
 * Copyright (c) 2007, Endless Loop Software, Inc.
 * 
 *  This file is part of EgoNet.
 *
 *    EgoNet is free software; you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as published by
 *    the Free Software Foundation; either version 3 of the License, or
 *    (at your option) any later version.
 *
 *    EgoNet is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU General Public License for more details.
 *
 *    You should have received a copy of the GNU General Public License
 *    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.endlessloopsoftware.ego.client;

import com.endlessloopsoftware.ego.Shared;
import com.endlessloopsoftware.ego.Study;


public class EgoClient
{
	public static Study			study					= new Study();
	public static EgoStore		storage				= new EgoStore();
	public static ClientFrame	frame					= new ClientFrame();
	public static Interview		interview			= null;
	public static int				uiPath;

	public static final int		SELECT				= 0;
	public static final int		DO_INTERVIEW		= 1;
	public static final int		VIEW_INTERVIEW		= 2;
	public static final int		VIEW_SUMMARY		= 3;
	
	private boolean				packFrame			= false;

	//Construct the application
	public EgoClient()
	{
      SourceSelectPanel.gotoPanel(true);

		frame.setVisible(true);
	}

	public static ClientFrame getFrame()
	{
		return (frame);
	}

	//Main method
	public static void main(String[] args)
	{
		new EgoClient();
	}
}