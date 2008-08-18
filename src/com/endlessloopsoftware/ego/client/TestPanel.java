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
package com.endlessloopsoftware.ego.client;

import com.cim.dlgedit.loader.DialogResource;
import java.awt.GridLayout;

import javax.swing.JPanel;

public class TestPanel
extends JPanel{
	public TestPanel(){
	try{
		JPanel panel = DialogResource.load("com/endlessloopsoftware/ego/client/localSelect.gui_xml");
		jbInit();
		this.setLayout(new GridLayout());
		this.add(panel);
	}
	catch(Exception e){}
	}

	private void jbInit() throws Exception{
	
	}
}