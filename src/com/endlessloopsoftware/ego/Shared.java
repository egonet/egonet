/*
 * Created on Jan 23, 2004
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package com.endlessloopsoftware.ego;

import java.awt.Cursor;
//import java.awt.Dimension;

import javax.swing.JFrame;
//import javax.swing.UIManager;

/*
 * import com.jgoodies.plaf.FontSizeHints;
 * import com.jgoodies.plaf.LookUtils;
 * import com.jgoodies.plaf.Options;
 */

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
	public static void configureUI()
	{
		
		/*
		 * Commented by sonam : 08/20/2007
		 * UIManager.put(Options.USE_SYSTEM_FONTS_APP_KEY, Boolean.TRUE);
		 * Options.setGlobalFontSizeHints(FontSizeHints.MIXED);
		 * Options.setDefaultIconSize(new Dimension(18, 18));
		 * String lafName = LookUtils.IS_OS_WINDOWS_XP ? 
				Options.getCrossPlatformLookAndFeelClassName() : 
				Options.getSystemLookAndFeelClassName();
		 */
		
		/*
		UIManager.put(USE_SYSTEM_FONTS_APP_KEY, Boolean.TRUE);
		String lafName = (System.getProperty("os.name").toLowerCase()=="windows xp") ? 
				UIManager.getCrossPlatformLookAndFeelClassName() : 
				UIManager.getSystemLookAndFeelClassName();
		try
		{
			UIManager.setLookAndFeel(lafName);
		}
		catch (Exception e)
		{
			System.err.println("Can't set look & feel:" + e);
		}
 		*/	
	
	}
   
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
