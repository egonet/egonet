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
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;
import java.util.prefs.Preferences;

import javax.swing.*;

import com.cim.dlgedit.loader.DialogResource;
import com.cim.util.swing.DlgUtils;
import com.endlessloopsoftware.egonet.interfaces.ConfigurationSBRemote;
import com.endlessloopsoftware.egonet.interfaces.ConfigurationSBRemoteHome;
import com.endlessloopsoftware.egonet.interfaces.ConfigurationSBUtil;
import com.endlessloopsoftware.egonet.interfaces.StudySBRemote;
import com.endlessloopsoftware.egonet.interfaces.StudySBRemoteHome;
import com.endlessloopsoftware.egonet.interfaces.StudySBUtil;
import com.endlessloopsoftware.elsutils.security.SymmetricKeyEncryption;

public class SetActiveStudyDialog 
	extends JDialog 
	implements ActionListener
{
	// Declare beans.
	private 	JButton			loadStudies;
	private 	JButton			select;
	private	JList				surveyNameList;
	private	JTextField		serverURL;
	private 	JPasswordField	password;
	
   // Get default values
	private 	Preferences 		prefs = Preferences.userNodeForPackage(this.getClass());
	
   Properties prop = new Properties();
   {
      prop.setProperty("java.naming.factory.initial", "org.jnp.interfaces.NamingContextFactory");
   }
   
	private static final String		SERVER_URL	= "ServerURL";

	// Constructor.
	public SetActiveStudyDialog(Frame owner)
	{
		// This dialog is modal.
		super(owner, "Select Active Survey", true);

		// Load User Interface
		JPanel panel = DialogResource.load("com/endlessloopsoftware/ego/author/SelectStudyDialog.gui_xml");

		setContentPane(panel);

		// Attach beans to fields.
		loadStudies 		 = (JButton) DialogResource.getComponentByName(panel,        "loadStudies");
		select			    = (JButton) DialogResource.getComponentByName(panel,        "select");
		surveyNameList	 = (JList) DialogResource.getComponentByName(panel,          "surveyNames");
		password        = (JPasswordField) DialogResource.getComponentByName(panel, "password");
		serverURL       = (JTextField) DialogResource.getComponentByName(panel,     "serverURL");
		
		// Set default value
		serverURL.setText(prefs.get(SERVER_URL, ""));
      password.setText(null);
      surveyNameList.setModel(new DefaultListModel());
      
      // Fill In Survey List

		// Add dialog as ActionListener.
		loadStudies.addActionListener(this);
		select.addActionListener(this);

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

	public void onloadStudies() throws Exception
	{
      surveyNameList.setModel(getList());
	}

	public void onselect()
   {
      String   message = "";
      boolean  success = false;
      setWaitCursor(true);

      String studyName = (String) surveyNameList.getSelectedValue();

      if (!("".equals(studyName) || (studyName == null)))
      {
         try
         {
            String epassword = "215-121-242-47-99-238-5-61-133-183-0-216-187-250-253-30-115-177-254-142-161-83-108-56";//SymmetricKeyEncryption.encrypt(new String(password.getPassword()));
            
            ConfigurationSBRemoteHome configurationSBHome = ConfigurationSBUtil.getHome(prop);
            ConfigurationSBRemote configurationSession = configurationSBHome.create();
            configurationSession.setActiveStudy(studyName, epassword);

            success = true;
         }
         catch (Exception ex)
         {
            message = ex.getMessage();
         }
      }
      else
      {
         JOptionPane.showMessageDialog(this, "No Survey Selected");
      }

      prefs.put(SERVER_URL, serverURL.getText());

      if (success)
      {
         JOptionPane.showMessageDialog(this, studyName + " is now the active survey.");
      }
      else
      {
         JOptionPane.showMessageDialog(this, "Unable to set active survey.\n" + message, "Server Error", JOptionPane.ERROR_MESSAGE);
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

   /**
    * @author admin
    *
    * To change the template for this generated type comment go to
    * Window - Preferences - Java - Code Generation - Code and Comments
    */
   public ListModel getList() throws Exception
   {
      DefaultListModel listModel = new DefaultListModel();
      
      if ("".equals(serverURL.getText()))
         return listModel;
      
      try
      {
         setWaitCursor(true);
         
         prop.setProperty("java.naming.provider.url", serverURL.getText()+":1099");
         
         StudySBRemoteHome       studySBHome    = StudySBUtil.getHome(prop);
         StudySBRemote           studySession   = studySBHome.create();
         Set                     studyNames     = studySession.getStudyNames();
        
         for (Iterator it = studyNames.iterator(); it.hasNext();)
         {
            listModel.addElement(it.next());
         }
      }
      finally
      {
         setWaitCursor(false);
      }
            
      return (ListModel) listModel;
   }
   
   public String encryptPassword(String password)
   {
      return SymmetricKeyEncryption.encrypt(password);
   }
}