package com.endlessloopsoftware.ego.client;

/**
 * <p>Copyright: Copyright (c) 2002 - 2004 </p>
 * <p>Company: Endless Loop Software</p>
 * @author Peter Schoaff
 *
 * $Id: ClientPanel.java,v 1.1 2005/08/02 19:36:01 samag Exp $
 */

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

/**
 * $Log: ClientPanel.java,v $
 * Revision 1.1  2005/08/02 19:36:01  samag
 * Initial checkin
 *
 * Revision 1.10  2004/04/08 15:06:06  admin
 * EgoClient now creates study summaries from Server
 * EgoAuthor now sets active study on server
 *
 * Revision 1.9  2004/04/06 15:46:11  admin
 * cvs tags in headers
 *
 */