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
package org.egonet.gui.interview;

import javax.swing.JTabbedPane;
import javax.swing.ProgressMonitor;

import org.egonet.graph.GraphPanel;
import org.egonet.statistics.StatisticsFrame;

import java.util.*;

public class ViewInterviewPanel
	extends JTabbedPane
{
	GraphPanel graphPanel;
	public ViewInterviewPanel(EgoClient egoClient, ProgressMonitor progress)
	{
		super();
		progress.setProgress(10);
		this.addTab("Interview", new ClientQuestionPanel(egoClient));
		progress.setProgress(15);
		
		if(egoClient.getInterview().isComplete()) {
			this.addTab("Statistics", new StatisticsFrame(egoClient));
			graphPanel = new GraphPanel(egoClient);
			this.addTab("Graph", graphPanel);
		}
		progress.setProgress(70);
	}
	
   public Iterator settingsIterator() {
	   return graphPanel.getSettingsIterator();
   }
}