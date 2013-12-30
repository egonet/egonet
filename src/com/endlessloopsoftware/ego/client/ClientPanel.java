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
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.JFileChooser;

import net.miginfocom.swing.MigLayout;
import net.sf.functionalj.Function0;
import net.sf.functionalj.FunctionException;

import org.egonet.exceptions.CorruptedInterviewException;
import org.egonet.gui.EgoStore;
import org.egonet.util.CatchingAction;
import org.egonet.util.EgonetAnalytics;
import org.egonet.util.ExtensionFileFilter;
import org.egonet.util.SwingWorker;
import org.egonet.wholenet.gui.InterviewFileSelectionFrame;

import com.endlessloopsoftware.egonet.Interview;

import org.egonet.io.RawDataCSVWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClientPanel 
	extends JPanel
{
 	private JLabel 			titleLabel;
	private JButton 		selectStudyButton;
	private JButton 		wholeNetworkButton;
	private JButton 		statisticsButton;
	private JButton 		rawDataButton;
	private JButton 		viewInterviewButton;
	private JButton 		startInterviewButton;
	
	final private static Logger logger = LoggerFactory.getLogger(ClientPanel.class);

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
		
		wholeNetworkButton = new JButton("Whole Network Analysis");
		wholeNetworkButton.addActionListener(new CatchingAction("wholeNetworkButton") {
			public void safeActionPerformed(ActionEvent e) throws Exception {
				doWholeNetworkAnalysis(e);
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
		
		statisticsButton = new JButton("Save Summary Statistics");
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
		this.add(this.wholeNetworkButton, "span, growx");
		this.add(this.viewInterviewButton, "sg 1");
		this.add(this.rawDataButton, "sg 1");
		this.add(this.statisticsButton, "sg 1, wrap");
		this.add(this.startInterviewButton, "span, growx");
		
		// Part of building is a set of adjustments that need to be repeated when a study is selected.
		adjustControlState();
	}

	void adjustControlState()
	{
		Boolean loaded = egoClient.getStorage().isStudyLoaded();
		this.viewInterviewButton.setEnabled(loaded);
		this.rawDataButton.setEnabled(loaded);
		this.statisticsButton.setEnabled(loaded);
		this.startInterviewButton.setEnabled(loaded);
		this.selectStudyButton.setText(loaded ? "Study: "+egoClient.getStudy().getStudyName() : "Select Study");
		this.wholeNetworkButton.setEnabled(loaded);
	}

	private void doSelectStudy(ActionEvent e) throws Exception
	{
		/* Clear out old data */
		egoClient.setStorage(new EgoStore(null));
		egoClient.getStorage().unsetCurrentInterview();

		/* Read new study */
		egoClient.getStorage().chooseStudy();
		
		/* Selecting a study enables some controls and changes the appearance of others. */
		adjustControlState();
	}
	
	private void doWholeNetworkAnalysis(ActionEvent e) throws Exception
	{
		logger.info("Attempting whole network analysis");
		final EgoStore storage = egoClient.getStorage();
		if(storage.isStudyLoaded()) {
			
			SwingWorker sw = new SwingWorker() {

				JFrame frame;
				
				@Override
				public Object construct() {
					frame = new InterviewFileSelectionFrame(storage.getStudyFile(), storage.getStudy());
					return frame;
				}
				
				public void finished() {
					frame.setVisible(true);		
				}
				
			};
			
			sw.start();
		}
		else {
			logger.info("Storage/study wasn't loaded");
			JOptionPane.showMessageDialog(egoClient.getFrame(), "No study loaded");
		}
	}


	private void doStartInterview(ActionEvent e) throws Exception
	{
		EgonetAnalytics.track("start interview"); // track!
		
      egoClient.setUiPath(ClientFrame.DO_INTERVIEW);
      egoClient.getStorage().setPackageInUse();
		try
		{
			File fInterview = egoClient.getStorage().getInterviewFile();
			String sIntName = fInterview != null ? fInterview.getName().replace(".int", "") :"";
         egoClient.getStorage().setCurrentInterview(new Interview(egoClient.getStudy(), sIntName), null);
         
         
         
         
			if (!egoClient.getInterview()._statisticsAvailable) {
				/* No Structural question for this study, warn user */
				int option = JOptionPane.showConfirmDialog(egoClient.getFrame(), "<html><p>This study has no questions with specified adjacency selections.</p>" +
															 "<p>You will be unable to generate any structural statistics for it.</p>" +
															 "<p>Continue anyway?</p>",
															 "No Statistics Available", JOptionPane.YES_NO_OPTION);

				if (option == JOptionPane.NO_OPTION) {
					egoClient.getStorage().unsetCurrentInterview();
				}
			}
		}
		catch (CorruptedInterviewException ex) {
			/* No Structural question for this study, warn user */
			JOptionPane.showMessageDialog(egoClient.getFrame(), "Unable to create an interview from this file",
					"No Statistics Available", JOptionPane.ERROR_MESSAGE);
			egoClient.getStorage().unsetCurrentInterview();
		}

		if (egoClient.getInterview() != null) {
		    egoClient.getFrame().gotoStartPanel();
		}
	}

	private void doViewInterview(ActionEvent e)
	{
		egoClient.setUiPath(ClientFrame.VIEW_INTERVIEW);

		EgoStore storage = egoClient.getStorage();

		Function0<Void> openWhenDone = new Function0<Void>() {
			public Void call() throws FunctionException {
				if (egoClient.getInterview() != null)
				    egoClient.getFrame().gotoViewInterviewPanel();
				return null;
			}
		};

		
		storage.unsetCurrentInterview();
		storage.selectInterview(openWhenDone);
		
	}
	
	private void saveRawDataAsCSV() throws Exception {
		File studyDirectory = new File(egoClient.getStorage().getStudyFile().getParent());
		
		JFileChooser jfcInterviewDirectory = new JFileChooser(studyDirectory);
		jfcInterviewDirectory.setDialogTitle("Choose a directory to search for interview files");
		jfcInterviewDirectory.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		
		
		if (jfcInterviewDirectory.showOpenDialog(this) != JFileChooser.APPROVE_OPTION)
			return;
		
		File fInterviewDirectory = jfcInterviewDirectory.getSelectedFile();
		if(!fInterviewDirectory.isDirectory())
			return;
		
		
		
		JFileChooser fc = new JFileChooser(studyDirectory);
		fc.addChoosableFileFilter(new ExtensionFileFilter("CSV Files","csv"));
		fc.setDialogTitle("Choose a new file to write raw CSV data");
		if(fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
			//File interviewDirectory = new File(egoClient.getStorage().getStudyFile().getParent(), "/Interviews/");
			File outputCSV = fc.getSelectedFile();
			if(outputCSV != null) {
				String path = outputCSV.getPath();
				if( !(path == null || path.isEmpty() || path.endsWith("/") || 
						path.endsWith("\\") || path.endsWith("."))) 
				{
					if(! path.toLowerCase().endsWith(".csv")) {
						outputCSV = new File(path+".csv");
					}
					new RawDataCSVWriter(egoClient.getStudy()).writeFromInterviewDirectoryToFile(fInterviewDirectory, outputCSV);
					
				}
			}
		}
	}

	private void doSummaryStatistics(ActionEvent e)
	{
		/* Warn User this could take awhile */
		int ok = JOptionPane.showConfirmDialog(egoClient.getFrame(), "This operation could take over a minute. Should I continue?",
				"Load Interview Statistics", JOptionPane.OK_CANCEL_OPTION);

		if (ok == JOptionPane.OK_OPTION)
		{
		    // as the commented-out method isn't called any more, lots of code could be deleted or cleaned up
			// egoClient.getFrame().gotoSummaryPanel();
			
		    egoClient.getFrame().quickSaveSummary();
		}
	}
}

