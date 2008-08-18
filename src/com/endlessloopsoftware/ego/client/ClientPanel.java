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

	public ClientPanel()
	{
		try
		{
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
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	//Component initialization
	private void jbInit() throws Exception
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
		startInterviewButton.setEnabled(EgoClient.storage.getStudyLoaded());
		viewInterviewButton.setEnabled(EgoClient.storage.getStudyLoaded());
		statisticsButton.setEnabled(EgoClient.storage.getStudyLoaded());

		studyNameLabel.setText(EgoClient.study.getStudyName());
		if (studyNameLabel.getText() == null)
		{
			studyNameLabel.setText(" ");
		}

		if (EgoClient.storage.getStudyLoaded())
		{

		}
	}

	private void doSelectStudy(ActionEvent e)
	{
		/* Clear out old data */
      EgoClient.study 		= new Study();
      EgoClient.storage		= new EgoStore();
      EgoClient.interview 	= null;

		/* Read new study */
      EgoClient.storage.selectStudy();
      EgoClient.storage.readPackage();
		studyNameLabel.setText(EgoClient.study.getStudyName());

		fillPanel();
	}

	private void doStartInterview(ActionEvent e)
	{
      EgoClient.uiPath = EgoClient.DO_INTERVIEW;
      EgoClient.storage.setPackageInUse();
		try
		{
         EgoClient.interview = new Interview(EgoClient.study);
			if (!EgoClient.interview._statisticsAvailable)
			{
				/* No Structural question for this study, warn user */
				int option = JOptionPane.showConfirmDialog(EgoClient.frame, "<html><p>This study has no questions with specified adjacency selections.</p>" +
															 "<p>You will be unable to generate any structural statistics for it.</p>" +
															 "<p>Continue anyway?</p>",
															 "No Statistics Available", JOptionPane.YES_NO_OPTION);

				if (option == JOptionPane.NO_OPTION)
				{
					EgoClient.interview = null;
				}
			}
		}
		catch (CorruptedInterviewException ex) {
			/* No Structural question for this study, warn user */
			JOptionPane.showMessageDialog(EgoClient.frame, "Unable to create an interview from this file",
					"No Statistics Available", JOptionPane.ERROR_MESSAGE);
			EgoClient.interview = null;
		}

		if (EgoClient.interview != null)
		{
			StartPanel.gotoPanel();
		}
	}

	private void doViewInterview(ActionEvent e)
	{
		EgoClient.uiPath = EgoClient.VIEW_INTERVIEW;

		EgoClient.storage.setInterviewFile(null);
		EgoClient.interview = null;
		EgoClient.storage.selectInterview();
	}

	private void doSummaryStatistics(ActionEvent e)
	{
		/* Warn User this could take awhile */
		int ok = JOptionPane.showConfirmDialog(EgoClient.frame, "This operation could take over a minute. Should I continue?",
				"Load Interview Statistics", JOptionPane.OK_CANCEL_OPTION);

		if (ok == JOptionPane.OK_OPTION)
		{
			SummaryPanel.gotoPanel();
		}
	}
}