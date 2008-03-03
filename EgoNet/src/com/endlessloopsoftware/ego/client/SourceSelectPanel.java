/**
 * <p>Copyright: Copyright (c) 2002 - 2004 </p>
 * <p>Company: Endless Loop Software</p>
 * @author Peter Schoaff
 *
 * $Id: SourceSelectPanel.java,v 1.1 2005/08/02 19:36:01 samag Exp $
 */
package com.endlessloopsoftware.ego.client;

import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;

/**
 * @author admin
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public class SourceSelectPanel
	extends JTabbedPane
{
	public SourceSelectPanel()
	{
		super();
		this.addTab("Local Files", new ClientPanel());
	//	this.addTab("Remote Server", new ServerInterviewChooser());
		//this.addTab("Test Panel", new TestPanel());
	}
	
   public static void gotoPanel(boolean center)
   {
      /* Return to first screen */
//      EgoClient.frame.setVisible(false);
      EgoClient.frame.setContentPane(new SourceSelectPanel());
      EgoClient.frame.createMenuBar(EgoClient.SELECT);
      EgoClient.frame.pack();
      //EgoClient.frame.setSize(600, 500);
      EgoClient.frame.setExtendedState(EgoClient.frame.getExtendedState()|JFrame.MAXIMIZED_BOTH);
      
      if (center)
      {
   		//Center the window
   		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
   		Dimension frameSize = EgoClient.frame.getSize();
   		if (frameSize.height > screenSize.height)
   		{
   			frameSize.height = screenSize.height;
   		}
   		if (frameSize.width > screenSize.width)
   		{
   			frameSize.width = screenSize.width;
   		}
   		EgoClient.frame.setLocation((screenSize.width - frameSize.width) / 2, (screenSize.height - frameSize.height) / 2);
   		
      }
     
     EgoClient.frame.setVisible(true);
   }
}


/**
 * $Log: SourceSelectPanel.java,v $
 * Revision 1.1  2005/08/02 19:36:01  samag
 * Initial checkin
 *
 * Revision 1.2  2004/03/21 14:00:39  admin
 * Cleaned up Question Panel Layout using FOAM
 *
 * Revision 1.1  2004/03/20 18:13:59  admin
 * Adding remote selection dialog
 *
 * Revision 1.1  2004/03/19 20:28:45  admin
 * Converted statistics frome to a panel. Incorporated in a tabbed panel
 * as part of main frame.
 *
 */