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
package com.endlessloopsoftware.ego.author;
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