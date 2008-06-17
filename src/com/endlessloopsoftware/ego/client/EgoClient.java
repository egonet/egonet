package com.endlessloopsoftware.ego.client;

import com.endlessloopsoftware.ego.Shared;
import com.endlessloopsoftware.ego.Study;
import javax.swing.JFrame;

/**
 * <p>Title: Egocentric Networks Client Program</p>
 * <p>Description: Subject Interview Client.</p>
 * <p>Copyright: Copyright (c) 2002 - 2004 </p>
 * <p>Company: Endless Loop Software</p>
 * @author Peter Schoaff
 *
 * $Id: EgoClient.java,v 1.1 2005/08/02 19:36:01 samag Exp $
 */

public class EgoClient
{
	public static Study			study				= new Study();
	public static EgoStore		storage				= new EgoStore();
	public static ClientFrame	frame				= new ClientFrame();
	public static Interview		interview			= null;
	public static int			uiPath;

	/**
	 * Used to create drop down menus of different "modes"
	 */
	public static final int		SELECT				= 0;
	public static final int		DO_INTERVIEW		= 1;
	public static final int		VIEW_INTERVIEW		= 2;
	public static final int		VIEW_SUMMARY		= 3;

	//Construct the application
	public EgoClient()
	{
      SourceSelectPanel.gotoPanel(true);

		frame.setVisible(true);
		frame.setExtendedState(frame.getExtendedState()|JFrame.MAXIMIZED_BOTH);

	}

	public static ClientFrame getFrame()
	{
		return (frame);
	}

	//Main method
	public static void main(String[] args)
	{
		// save this -- it might be useful for the web part later
		/*System.setProperty("java.security.policy", "security.policy");
		if(System.getSecurityManager() == null) {
		     System.setSecurityManager(new java.rmi.RMISecurityManager());
		}*/
		
		//if(true) return;
		Shared.configureUI();
		new EgoClient();
	}
}