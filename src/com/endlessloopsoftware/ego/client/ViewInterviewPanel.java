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

import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import javax.swing.ProgressMonitor;

import java.util.*;

import com.endlessloopsoftware.ego.Shared;
import com.endlessloopsoftware.ego.client.statistics.StatisticsFrame;
import com.endlessloopsoftware.elsutils.SwingWorker;
import com.endlessloopsoftware.ego.client.graph.*;

public class ViewInterviewPanel
	extends JTabbedPane
{
	GraphPanel graphPanel;
	public ViewInterviewPanel(ProgressMonitor progress)
	{
		super();
		progress.setProgress(10);
		this.addTab("Interview", new ClientQuestionPanel());
		progress.setProgress(15);
		this.addTab("Statistics", new StatisticsFrame());
		graphPanel = new GraphPanel();
		this.addTab("Graph", graphPanel);
		progress.setProgress(70);
	}
	
   static void gotoPanel()
   {
      final ProgressMonitor progressMonitor = new ProgressMonitor(EgoClient.frame, "Calculating Statistics", "", 0, 100);
   		final SwingWorker worker = new SwingWorker() 
		{
   			public Object construct() 
   			{
   				// Build Screen
   				EgoClient.frame.setVisible(false);
   				Shared.setWaitCursor(EgoClient.frame, true);
   				progressMonitor.setProgress(5);
   				EgoClient.frame.setContentPane(new ViewInterviewPanel(progressMonitor));
   				progressMonitor.setProgress(95);
   				EgoClient.frame.createMenuBar(EgoClient.VIEW_INTERVIEW);
   				EgoClient.frame.pack();
   				// EgoClient.frame.setSize(640, 530);
   				EgoClient.frame.setExtendedState(EgoClient.frame.getExtendedState()|JFrame.MAXIMIZED_BOTH);

   				return EgoClient.frame;
	      }
	      
	      public void finished()
			{
            Shared.setWaitCursor(EgoClient.frame, false);
	         progressMonitor.close();
	      		EgoClient.frame.setVisible(true);
	      }
	  };
	  
     progressMonitor.setProgress(0);
     progressMonitor.setMillisToDecideToPopup(0);
     progressMonitor.setMillisToPopup(0);
  	
     worker.start();
   }
   
   public Iterator settingsIterator() {
	   return graphPanel.getSettingsIterator();
   }
}