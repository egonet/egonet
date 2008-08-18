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
package com.endlessloopsoftware.ego;

import java.awt.Cursor;
//import java.awt.Dimension;

import javax.swing.JFrame;

/**
 * @author admin
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public final class Shared
{
	public static final String 			version 	= "2.0 Beta 9 (7 Apr 2004)";
	
   /**
    * Configures the UI; tries to set the system look on Mac,
    * <code>ExtWindowsLookAndFeel</code> on general Windows, and
    * <code>Plastic3DLookAndFeel</code> on Windows XP and all other OS.<p>
    * 
    * The JGoodies Swing Suite's <code>ApplicationStarter</code>,
    * <code>ExtUIManager</code>, and <code>LookChoiceStrategies</code>
    * classes provide a much more fine grained algorithm to choose and
    * restore a look and theme.
    */
	/* Added by sonam : 08/20.2007 */
	
	public static final String USE_SYSTEM_FONTS_APP_KEY =
			"Application.useSystemFontSettings";
	
	/* end of code added by sonam */
   
   public static void setWaitCursor(JFrame frame, boolean waitCursor)
   {
      if (waitCursor)
      {
         frame.getGlassPane().setVisible(true);
         frame.getGlassPane().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
      }
      else
      {
         frame.getGlassPane().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
         frame.getGlassPane().setVisible(false);
      }
   }

}
