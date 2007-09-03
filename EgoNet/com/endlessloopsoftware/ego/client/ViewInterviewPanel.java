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

import javax.swing.JTabbedPane;
import javax.swing.ProgressMonitor;

import com.endlessloopsoftware.ego.Shared;
import com.endlessloopsoftware.ego.client.statistics.StatisticsFrame;
import com.endlessloopsoftware.elsutils.SwingWorker;

public class ViewInterviewPanel
	extends JTabbedPane
{
	public ViewInterviewPanel(ProgressMonitor progress)
	{
		super();
		progress.setProgress(10);
		this.addTab("Interview", new QuestionPanel());
		progress.setProgress(15);
		this.addTab("Statistics", new StatisticsFrame());
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
		      	progressMonitor.setProgress(75);
	         EgoClient.frame.createMenuBar(EgoClient.VIEW_INTERVIEW);
	         EgoClient.frame.pack();
	         EgoClient.frame.setSize(640, 530);
	         
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
}


/**
 * $Log$
 * Revision 1.1  2007/09/03 13:51:17  schoaff
 * Initial Checkin
 *
 * Revision 1.5  2004/03/29 00:35:10  admin
 * Downloading Interviews
 * Fixing some bugs creating Interviews from Data Objects
 *
 * Revision 1.4  2004/03/28 17:31:32  admin
 * More error handling when uploading study to server
 * Server URL selection dialog for upload
 *
 * Revision 1.3  2004/03/21 15:38:08  admin
 * Using progress bar while bringing up Summary Panel
 *
 * Revision 1.2  2004/03/21 15:17:42  admin
 * Using progress Bar while bringing up question panel
 *
 * Revision 1.1  2004/03/19 20:28:45  admin
 * Converted statistics frome to a panel. Incorporated in a tabbed panel
 * as part of main frame.
 *
 */