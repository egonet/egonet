package com.endlessloopsoftware.ego.author;

/**
 * <p>Title: Egocentric Network Researcher</p>
 * <p>Description: Configuration Utilities for an Egocentric network study</p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: Endless Loop Software</p>
 * @author Peter C. Schoaff
 * @version 1.0
 *
 * $Id: StoreStudyDialog.java,v 1.1 2005/08/02 19:36:05 samag Exp $
 *
 */

import java.awt.Cursor;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.prefs.Preferences;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import com.cim.dlgedit.loader.DialogResource;
import com.cim.util.swing.DlgUtils;

public class StoreStudyDialog 
	extends JDialog 
	implements ActionListener
{
	// Declare beans.
	private 	JButton			cancel;
	private 	JButton			store;
	private	JLabel				surveyName;
	private	JTextField		serverURL;
	private 	JPasswordField	password;
	
	// Get default values
	private 	Preferences 		prefs = Preferences.userNodeForPackage(this.getClass());
	
	private static final String		SERVER_URL	= "ServerURL";

	// Constructor.
	public StoreStudyDialog(Frame owner)
	{
		// This dialog is modal.
		super(owner, "Upload Survey to Server", true);

		// Load User Interface
		JPanel panel = DialogResource.load("com/endlessloopsoftware/ego/author/StoreSurvey.gui_xml");

		setContentPane(panel);

		// Attach beans to fields.
		cancel 		= (JButton) DialogResource.getComponentByName(panel, 			"cancel");
		store 			= (JButton) DialogResource.getComponentByName(panel, 			"store");
		surveyName	= (JLabel) DialogResource.getComponentByName(panel, 			"surveyName");
		password 		= (JPasswordField) DialogResource.getComponentByName(panel, "password");
		serverURL 	= (JTextField) DialogResource.getComponentByName(panel, 		"serverURL");
		
		// Set default value
		surveyName.setText(EgoNet.study.getStudyName());
		serverURL.setText(prefs.get(SERVER_URL, ""));

		// Add dialog as ActionListener.
		cancel.addActionListener(this);
		store.addActionListener(this);

		pack();
		setLocationRelativeTo(owner);

		// Set Enter and Escape keys as default keys.
		//getRootPane().setDefaultButton(store);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
	}

	/**
	 * Invoke the onXxx() action handlers.
	 * 
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent e)
	{
		DlgUtils.invokeHandler(this, e);
	}

	public void oncancel()
	{
		this.hide();
	}

	public void onstore()
	{
		setWaitCursor(true);
		boolean success = EgoNet.study.writeDBStudy(EgoNet.frame, serverURL.getText(), password.getPassword());
		
		if (success)		
		{
			prefs.put(SERVER_URL, serverURL.getText());
		}
		setWaitCursor(false);
		
		this.hide();
	}
	
	protected void setWaitCursor(boolean waitCursor)
	{
		if (waitCursor)
		{
			this.getGlassPane().setVisible(true);
			this.getGlassPane().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		}
		else
		{
			this.getGlassPane().setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			this.getGlassPane().setVisible(false);
		}
	}


}

/**
 * $Log: StoreStudyDialog.java,v $
 * Revision 1.1  2005/08/02 19:36:05  samag
 * Initial checkin
 *
 * Revision 1.3  2004/04/11 00:24:48  admin
 * Fixing headers
 *
 */