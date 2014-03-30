package org.egonet.util;


import java.awt.Window;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;

/**
 * A fancier AbstractAction that will catch and gracefully attempt to report
 * exceptions thrown by the action code. This prevents uncaught exceptions, at
 * the risk of not halting execution of the caller. Using this in a table's code
 * may mean you're no longer stopping the redraw of subsequent rows on an error
 * condition.
 * 
 * @author martins
 */
public abstract class CatchingAction extends AbstractAction implements Action
{
	protected final String location;
	protected Window parent;

	/**
	 * @param name as it appears on any button or menu item
	 */
	public CatchingAction(String name)
	{
		super(name);
		this.location = this.getClass().getSimpleName() +" (inferred location)";
	}

	
	/**
	 * @param location an identifier to locate the caller who threw the error
	 */
	public CatchingAction(String name, String location)
	{
		super(name);
		this.location = location;
	}
	
	public final void actionPerformed(ActionEvent e)
	{
		try {
			safeActionPerformed(e);
		} catch (Throwable ex)
		{
			String failMsg = "Error that did not include a detailed explanation.";
			String err = (ex != null && ex.getMessage() != null ? ex.getMessage() : failMsg);
			
			// the EDT may catch its own exceptions,
			// so be ready to do the right thing here in the future
			// (get the current thread and call the uncaught handler directly)
			throw new RuntimeException(err, ex);
		}
	}
	
	public abstract void safeActionPerformed(ActionEvent e) throws Exception;


	public Window getParent() {
		return parent;
	}


	public void setParent(Window parent) {
		this.parent = parent;
	}


	public String getLocation() {
		return location;
	}
}

