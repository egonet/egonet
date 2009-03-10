package org.egonet.gui;

import javax.swing.*;

import org.egonet.mdi.MDIContext;

public abstract class MDIChildFrame extends JInternalFrame {
	public abstract void setMdiContext(final MDIContext context);
	public abstract JInternalFrame getInternalFrame();
	public abstract void focusActivated();
	public abstract void focusDeactivated();
}
