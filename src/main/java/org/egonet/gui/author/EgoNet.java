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
package org.egonet.gui.author;

import org.egonet.gui.EgoStore;
import org.egonet.model.Study;
import org.egonet.util.EgonetAnalytics;

/** ONLY USE THIS CLASS IN AUTHOR PART OF THE TOOL */
public class EgoNet
{
	private final EgoStore storage;
	private final EgoFrame	frame;
	
	public EgoNet() {
		EgonetAnalytics.track("authoring client startup"); // track!
		storage	= new EgoStore(null);
		storage.createNewStudy();
		
		frame = new EgoFrame(this);
		frame.validate();
	}

	public static void main(String[] args) throws Exception
	{
		//new Console();
		new EgoNet().getFrame().setVisible(true);
	}

	public EgoStore getStorage() {
		return storage;
	}

	public EgoFrame getFrame() {
		return frame;
	}
	
	public Study getStudy() {
		return storage.getStudy();
	}
	
	
}