package com.endlessloopsoftware.ego.author;

/**
 * <p>Title: Egocentric Network Researcher</p>
 * <p>Description: Configuration Utilities for an Egocentric network study</p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: Endless Loop Software</p>
 * @author Peter C. Schoaff
 * @version 1.0
 */

import javax.swing.JPanel;

/**
 * Extends JPanel class to keep focus in right panel of split question panel
 */
public class RightPanel extends JPanel
{
	public boolean isFocusCycleRoot()
	{
		return (true);
	}
}
