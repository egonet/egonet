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

import com.endlessloopsoftware.egonet.Study;

public class EgoClient
{
	private Study			study;
	private EgoStore		storage;
	private ClientFrame	frame;
	private Interview		interview;
	private int			uiPath;

	//Construct the application
	private EgoClient()
	{
		study = new Study();
		storage = new EgoStore(this);
		frame = new ClientFrame(this);
		interview = null;

		
		frame.gotoSourceSelectPanel(true);
		frame.setVisible(true);

	}

	private static EgoClient en = null;
	public static synchronized EgoClient getInstance() throws Exception
	{
		if(en == null)
		{
			en = new EgoClient();
		}
		return en;
	}

	//Main method
	public static void main(String[] args) throws Exception
	{
		//new Console();
		getInstance().frame.setVisible(true);
	}

	public Study getStudy() {
		return study;
	}

	public void setStudy(Study study) {
		this.study = study;
	}

	public EgoStore getStorage() {
		return storage;
	}

	public void setStorage(EgoStore storage) {
		this.storage = storage;
	}

	public ClientFrame getFrame() {
		return frame;
	}

	public void setFrame(ClientFrame frame) {
		this.frame = frame;
	}

	public Interview getInterview() {
		return interview;
	}

	public void setInterview(Interview interview) {
		this.interview = interview;
	}

	public int getUiPath() {
		return uiPath;
	}

	public void setUiPath(int uiPath) {
		this.uiPath = uiPath;
	}
}