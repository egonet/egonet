package com.endlessloopsoftware.ego.author;

import java.awt.Dimension;
import java.awt.Toolkit;

import com.endlessloopsoftware.ego.Shared;
import com.endlessloopsoftware.ego.Study;

/**
 * <p>Title: Egocentric Network Researcher</p>
 * <p>Description: Configuration Utilities for an Egocentric network study</p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: Endless Loop Software</p>
 * @author Peter C. Schoaff
 * @version 1.0
 *
 * $Id$
 */


public class EgoNet
{
	public static final EgoStore	storage		= new EgoStore();
	public static Study				study			= new Study();
	public static final EgoFrame	frame			= new EgoFrame();
	
	//Construct the application
	public EgoNet()
	{
		frame.validate();

		//Center the window
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		Dimension frameSize = frame.getSize();
		if (frameSize.height > screenSize.height)
		{
			frameSize.height = screenSize.height;
		}
		if (frameSize.width > screenSize.width)
		{
			frameSize.width = screenSize.width;
		}
		frame.setLocation(
			(screenSize.width - frameSize.width) / 2,
			(screenSize.height - frameSize.height) / 2);
		frame.setVisible(true);
	}

	//Main method
	public static void main(String[] args)
	{
		new EgoNet();
	}
}

/**
 * $Log$
 * Revision 1.1  2007/09/03 13:51:18  schoaff
 * Initial Checkin
 *
 * Revision 1.6  2004/04/11 00:24:48  admin
 * Fixing headers
 *
 * Revision 1.5  2004/03/23 14:58:48  admin
 * Update UI
 * Study creation now occurs in instantiators
 *
 * Revision 1.4  2003/12/05 19:15:43  admin
 * Extracting Study
 *
 * Revision 1.3  2003/12/04 15:14:08  admin
 * Merging EgoNet and EgoClient projects so that they can share some
 * common classes more easily.
 *
 * Revision 1.2  2003/11/25 19:25:44  admin
 * Warn before closing window
 *
 * Revision 1.1.1.1  2003/06/08 15:09:40  admin
 * Egocentric Network Survey Authoring Module
 *
 * Revision 1.5  2002/06/30 15:59:18  admin
 * Moving questions in lists, between lists
 * Better category input
 *
 * Revision 1.4  2002/06/26 00:10:48  admin
 * UI Work including base question coloring and category selections
 *
 * Revision 1.3  2002/06/25 15:41:01  admin
 * Lots of UI work
 *
 * Revision 1.2  2002/06/15 14:19:50  admin
 * Initial Checkin of question and survey
 * General file system work
 *
 */