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
package com.endlessloopsoftware.ego.client;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import org.egonet.exceptions.CorruptedInterviewException;

import com.cim.dlgedit.loader.DialogResource;
import com.endlessloopsoftware.ego.Study;

public class ClientPanel 
	extends JPanel
{
 	private JLabel 			titleLabel;
	private JButton 		selectStudyButton;
	private JButton 		statisticsButton;
	private JButton 		viewInterviewButton;
	private JButton 		startInterviewButton;

	private JLabel studyNameLabel = new JLabel();

	private final EgoClient egoClient;
	public ClientPanel(EgoClient egoClient)
	{
		this.egoClient = egoClient;
//			 Load up the dialog contents.
			java.io.InputStream is = this.getClass().getClassLoader().getResourceAsStream("com/endlessloopsoftware/ego/client/localSelect.gui_xml");
			JPanel panel = DialogResource.load(is);
			//JPanel panel = DialogResource.load("com/endlessloopsoftware/ego/client/localSelect.gui_xml");

//			 Attach beans to fields.
			selectStudyButton    = (JButton) DialogResource.getComponentByName(panel, "SelectStudy");
			viewInterviewButton 	= (JButton) DialogResource.getComponentByName(panel, "ViewInterview");
			statisticsButton 		= (JButton) DialogResource.getComponentByName(panel, "SummaryStatistics");
			startInterviewButton = (JButton) DialogResource.getComponentByName(panel, "StartInterview");
			titleLabel 				= (JLabel) DialogResource.getComponentByName(panel, "Title");
			studyNameLabel       = (JLabel) DialogResource.getComponentByName(panel, "StudyName");
			
			jbInit();
			
			this.setLayout(new GridLayout(1, 1));
			this.add(panel);
	}

	//Component initialization
	private void jbInit() 
	{
		titleLabel.setBackground(Color.lightGray);
		titleLabel.setBorder(BorderFactory.createRaisedBevelBorder());
		titleLabel.setHorizontalAlignment(SwingConstants.CENTER);

		studyNameLabel.setBorder(BorderFactory.createLoweredBevelBorder());
		studyNameLabel.setHorizontalAlignment(SwingConstants.CENTER);
		studyNameLabel.setText(" ");

		selectStudyButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(ActionEvent e) {
				doSelectStudy(e);}});

		viewInterviewButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(ActionEvent e) {
				doViewInterview(e);}});

		statisticsButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(ActionEvent e) {
				doSummaryStatistics(e);}});

		startInterviewButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(ActionEvent e) {
				doStartInterview(e);}});

		fillPanel();
	}

	void fillPanel()
	{
		startInterviewButton.setEnabled(egoClient.getStorage().getStudyLoaded());
		viewInterviewButton.setEnabled(egoClient.getStorage().getStudyLoaded());
		statisticsButton.setEnabled(egoClient.getStorage().getStudyLoaded());

		studyNameLabel.setText(egoClient.getStudy().getStudyName());
		if (studyNameLabel.getText() == null)
		{
			studyNameLabel.setText(" ");
		}

		if (egoClient.getStorage().getStudyLoaded())
		{

		}
	}

	private void doSelectStudy(ActionEvent e)
	{
		/* Clear out old data */
      egoClient.setStudy(new Study());
      egoClient.setStorage(new EgoStore(egoClient));
      egoClient.setInterview(null);

		/* Read new study */
      egoClient.getStorage().selectStudy();
      egoClient.getStorage().readPackage();
		studyNameLabel.setText(egoClient.getStudy().getStudyName());

		fillPanel();
	}

	private void doStartInterview(ActionEvent e)
	{
      egoClient.setUiPath(ClientFrame.DO_INTERVIEW);
      egoClient.getStorage().setPackageInUse();
		try
		{
         egoClient.setInterview(new Interview(egoClient.getStudy(), egoClient));
			if (!egoClient.getInterview()._statisticsAvailable)
			{
				/* No Structural question for this study, warn user */
				int option = JOptionPane.showConfirmDialog(egoClient.getFrame(), "<html><p>This study has no questions with specified adjacency selections.</p>" +
															 "<p>You will be unable to generate any structural statistics for it.</p>" +
															 "<p>Continue anyway?</p>",
															 "No Statistics Available", JOptionPane.YES_NO_OPTION);

				if (option == JOptionPane.NO_OPTION)
				{
					egoClient.setInterview(null);
				}
			}
		}
		catch (CorruptedInterviewException ex) {
			/* No Structural question for this study, warn user */
			JOptionPane.showMessageDialog(egoClient.getFrame(), "Unable to create an interview from this file",
					"No Statistics Available", JOptionPane.ERROR_MESSAGE);
			egoClient.setInterview(null);
		}

		if (egoClient.getInterview() != null)
		{
		    egoClient.getFrame().gotoStartPanel();
		}
	}

	private void doViewInterview(ActionEvent e)
	{
		egoClient.setUiPath(ClientFrame.VIEW_INTERVIEW);

		egoClient.getStorage().setInterviewFile(null);
		egoClient.setInterview(null);
		egoClient.getStorage().selectInterview();
	}

	private void doSummaryStatistics(ActionEvent e)
	{
		/* Warn User this could take awhile */
		int ok = JOptionPane.showConfirmDialog(egoClient.getFrame(), "This operation could take over a minute. Should I continue?",
				"Load Interview Statistics", JOptionPane.OK_CANCEL_OPTION);

		if (ok == JOptionPane.OK_OPTION)
		{
		    egoClient.getFrame().gotoSummaryPanel();
		}
	}
}