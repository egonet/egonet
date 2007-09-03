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

package com.endlessloopsoftware.ego;

import java.awt.Cursor;
import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.UIManager;


/**
 * @author admin
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public final class Shared
{
	public static final String 			version 	= "2.0 Beta 10 (21 Jul 2004)";

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
