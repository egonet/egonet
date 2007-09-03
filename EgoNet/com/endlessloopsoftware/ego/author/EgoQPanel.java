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

public abstract class EgoQPanel extends JPanel
{
	abstract public void fillPanel();
	abstract public void clearPanel();
}