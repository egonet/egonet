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

import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.JTabbedPane;

/**
 * @author admin
 * 
 * To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Generation - Code and Comments
 */
public class SourceSelectPanel
   extends JTabbedPane
{
   public SourceSelectPanel()
   {
      super();
      this.addTab("Local Files", new ClientPanel());
      this.addTab("Remote Server", new ServerInterviewChooser());
   }

   public static void gotoPanel(boolean center)
   {
      /* Return to first screen */
      //      EgoClient.frame.setVisible(false);
      EgoClient.frame.setContentPane(new SourceSelectPanel());
      EgoClient.frame.createMenuBar(EgoClient.SELECT);
      EgoClient.frame.pack();
      EgoClient.frame.setSize(600, 500);

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
         
         EgoClient.frame.setLocation((screenSize.width - frameSize.width) / 2,
                                     (screenSize.height - frameSize.height) / 2);
      }

      //      EgoClient.frame.setVisible(true);
   }
}

/**
 * $Log$
 * Revision 1.1  2007/09/03 13:51:18  schoaff
 * Initial Checkin
 *
 * Revision 1.3  2004/05/28 15:21:03  admin
 * Update EgoEJB-client.jar lib to match recent EgoEJB project
 * Revision 1.2 2004/03/21 14:00:39 admin
 * Cleaned up Question Panel Layout using FOAM
 * 
 * Revision 1.1 2004/03/20 18:13:59 admin Adding remote selection dialog
 * 
 * Revision 1.1 2004/03/19 20:28:45 admin Converted statistics frome to a panel.
 * Incorporated in a tabbed panel as part of main frame.
 *  
 */