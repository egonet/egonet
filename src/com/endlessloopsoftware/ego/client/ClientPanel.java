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
import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.JFileChooser;

import net.miginfocom.swing.MigLayout;

import org.egonet.exceptions.CorruptedInterviewException;
import org.egonet.util.CatchingAction;
import org.egonet.util.ExtensionFileFilter;

import com.endlessloopsoftware.egonet.Interview;
import com.endlessloopsoftware.egonet.Study;

import org.egonet.io.RawDataCSVWriter;;

public class ClientPanel 
	extends JPanel
{
 	private JLabel 			titleLabel;
	private JButton 		selectStudyButton;
	private JButton 		statisticsButton;
	private JButton 		rawDataButton;
	private JButton 		viewInterviewButton;
	private JButton 		startInterviewButton;

	private final EgoClient egoClient;
	public ClientPanel(EgoClient egoClient)
	{
		this.egoClient = egoClient;
		initComponents();
	}
	
	private void initComponents() {
			
		// Create components
		
		titleLabel = new JLabel("Egocentric Network Study");
		titleLabel.setBackground(Color.lightGray);
		titleLabel.setBorder(BorderFactory.createRaisedBevelBorder());
		titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
		
		selectStudyButton = new JButton("Select Study");
		selectStudyButton.addActionListener(new CatchingAction("doSelectStudy") {
			public void safeActionPerformed(ActionEvent e) throws Exception {
				doSelectStudy(e);
			}
		});
		
		viewInterviewButton = new JButton("View Interview");
		viewInterviewButton.addActionListener(new CatchingAction("doViewInterview") {
			public void safeActionPerformed(ActionEvent e) throws Exception {
				doViewInterview(e);
				}
			});
		
		rawDataButton = new JButton("Raw Data");
		rawDataButton.addActionListener(new CatchingAction("saveRawDataAsCSV") {
			public void safeActionPerformed(ActionEvent e) throws Exception {
				saveRawDataAsCSV();
			}
		});
		
		statisticsButton = new JButton("Summary Statistics");
		statisticsButton.addActionListener(new CatchingAction("doSummaryStatistics") {
			public void safeActionPerformed(ActionEvent e) throws Exception {
				doSummaryStatistics(e);
				}
			});
		
		startInterviewButton = new JButton("Start Interview");
		startInterviewButton.addActionListener(new CatchingAction("doStartInterview") {
			public void safeActionPerformed(ActionEvent e) throws Exception {
				doStartInterview(e);
				}
			});
		
		// Layout components
		this.setLayout(
				new MigLayout(
						"gapx 10, gapy 15",
						"[grow]", "[grow]"));
		this.add(this.titleLabel, "gaptop 10, span, growx");
		this.add(this.selectStudyButton, "span, growx");
		this.add(this.viewInterviewButton, "sg 1");
		this.add(this.rawDataButton, "sg 1");
		this.add(this.statisticsButton, "sg 1, wrap");
		this.add(this.startInterviewButton, "span, growx");
		
		// Part of building is a set of adjustments that need to be repeated when a study is selected.
		adjustControlState();
	}

	void adjustControlState()
	{
		Boolean loaded = egoClient.getStorage().getStudyLoaded();
		this.viewInterviewButton.setEnabled(loaded);
		this.rawDataButton.setEnabled(loaded);
		this.statisticsButton.setEnabled(loaded);
		this.startInterviewButton.setEnabled(loaded);
		this.selectStudyButton.setText(
				loaded ? "Study: "+egoClient.getStudy().getStudyName() : "Select Study");
	}

	private void doSelectStudy(ActionEvent e) throws Exception
	{
		/* Clear out old data */
		egoClient.setStudy(new Study());
		egoClient.setStorage(new EgoStore(egoClient));
		egoClient.setInterview(null);

		/* Read new study */
		egoClient.getStorage().selectStudy();
		egoClient.getStorage().readPackage();
		
		/* Selecting a study enables some controls and changes the appearance of others. */
		adjustControlState();
	}

	private void doStartInterview(ActionEvent e)
	{
      egoClient.setUiPath(ClientFrame.DO_INTERVIEW);
      egoClient.getStorage().setPackageInUse();
		try
		{
         egoClient.setInterview(new Interview(egoClient.getStudy()));
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
	
	private void saveRawDataAsCSV() throws Exception {
		File studyDirectory = 
			new File(egoClient.getStorage().getPackageFile().getParent());
		JFileChooser fc = new JFileChooser(studyDirectory);
		fc.addChoosableFileFilter(new ExtensionFileFilter("CSV Files","csv"));
		fc.setDialogTitle("Save raw data as CSV file");
		if(fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
			File interviewDirectory = 
				new File(egoClient.getStorage().getPackageFile().getParent(), 
						"/Interviews/");
			File outputCSV = fc.getSelectedFile();
			new RawDataCSVWriter(egoClient.getStudy())
			.writeFromInterviewDirectoryToFile(
					interviewDirectory, outputCSV);
		}
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
