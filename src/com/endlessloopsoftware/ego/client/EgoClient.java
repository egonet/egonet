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

import org.egonet.gui.EgoStore;
import org.egonet.util.EgonetAnalytics;

import com.endlessloopsoftware.egonet.Interview;
import com.endlessloopsoftware.egonet.Study;

/**
 * ONLY USE THIS CLASS IN INTERVIEWING PART OF THE TOOL Session object
 * representing the session of the user and the EgoNet software
 * 
 * 
 * @author peters
 * @author martins
 * 
 */
public class EgoClient {
	private EgoStore storage;
	private ClientFrame frame;
	private int uiPath;

	// Construct the application
	public EgoClient() {
		EgonetAnalytics.track("interviewing client startup"); // track!
		
		storage = new EgoStore(null);
		frame = new ClientFrame(this);

		frame.gotoSourceSelectPanel();
		frame.setVisible(true);
		
	}

	// Main method
	public static void main(String[] args) throws Exception {
		// new Console();
		new EgoClient().getFrame().setVisible(true);
	}

	public Study getStudy() {
		return storage.getStudy();
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
		return storage.getInterview();
	}

	public int getUiPath() {
		return uiPath;
	}

	public void setUiPath(int uiPath) {
		this.uiPath = uiPath;
	}
}