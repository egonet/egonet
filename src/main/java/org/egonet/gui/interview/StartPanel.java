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
package org.egonet.gui.interview;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import org.egonet.exceptions.CorruptedInterviewException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;



public class StartPanel extends JPanel
{
	final private static Logger logger = LoggerFactory.getLogger(StartPanel.class);
	
	private final GridBagLayout gridBagLayout1 = new GridBagLayout();
	private final JLabel titleLabel = new JLabel("Create a new interview file");
	
	private final JButton startBrandNewInterviewButton = new JButton("Save or Continue a Respondent Interview (New Respondent)");
	private final JButton startLongitudinalInterviewButton = new JButton("Save New Longitudinal Interview (Existing Respondent)");
	
	private final EgoClient egoClient;

	public StartPanel(EgoClient egoClient) throws Exception
	{
		this.egoClient = egoClient;
		logger.info("Create of start panel using " + egoClient + " - " + egoClient.getStudy().getAlterNameModel());
		
		jbInit();
	}

	private void jbInit() throws Exception
	{
		this.setLayout(gridBagLayout1);

		titleLabel.setFont(new java.awt.Font("Lucida Grande", 1, 16));
		titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
		titleLabel.setHorizontalTextPosition(SwingConstants.CENTER);

		this.setBorder(BorderFactory.createEtchedBorder());
		this.add(
			titleLabel,
			new GridBagConstraints(
				0,
				0,
				2,
				1,
				1.0,
				0.2,
				GridBagConstraints.CENTER,
				GridBagConstraints.HORIZONTAL,
				new Insets(0, 0, 0, 0),
				0,
				0));
		
		logger.info(egoClient.getStudy().getAlterNameModel().toString());
		logger.info(egoClient.getStudy().getStudyName());

		this.add(
				startBrandNewInterviewButton,
			new GridBagConstraints(
				1,
				2,
				1,
				1,
				0.0,
				0.0,
				GridBagConstraints.CENTER,
				GridBagConstraints.HORIZONTAL,
				new Insets(10, 10, 10, 10),
				0,
				6));
		this.add(
				startLongitudinalInterviewButton,
			new GridBagConstraints(
				0,
				3,
				2,
				1,
				0.0,
				0.0,
				GridBagConstraints.CENTER,
				GridBagConstraints.HORIZONTAL,
				new Insets(10, 10, 10, 10),
				0,
				0));

		startBrandNewInterviewButton.addActionListener(new java.awt.event.ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				startBrandNewInterviewButton_actionPerformed(e);
			}
		});

		
		startLongitudinalInterviewButton.addActionListener(new java.awt.event.ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				startLongitudinalInterviewButton_actionPerformed(e);
			}

		});
	}
	
	private void startLongitudinalInterviewButton_actionPerformed(ActionEvent e) {
		boolean success = false;

		/* Logic */
		try
		{
			
			File studyFile = egoClient.getStorage().getStudyFile();
			File studyPath = studyFile.getParentFile();
			
			final JFileChooser fcOpen = new JFileChooser();
			
			final JFileChooser fcSave = new JFileChooser() {
			    
				// this chooser needs to understand the "overwrite?" confirmation
				
				@Override
			    public void approveSelection(){
			        File f = getSelectedFile();
			        if(f.exists() && getDialogType() == SAVE_DIALOG){
			            int result = JOptionPane.showConfirmDialog(this,"The file exists, overwrite?","Existing file",JOptionPane.YES_NO_CANCEL_OPTION);
			            switch(result){
			                case JOptionPane.YES_OPTION:
			                    super.approveSelection();
			                    return;
			                case JOptionPane.NO_OPTION:
			                    return;
			                case JOptionPane.CLOSED_OPTION:
			                    return;
			                case JOptionPane.CANCEL_OPTION:
			                    cancelSelection();
			                    return;
			            }
			        }
			        super.approveSelection();
			    }
			    
			};
			
			fcOpen.setCurrentDirectory(new File(studyPath.getAbsolutePath()+"/Interviews/"));
			fcSave.setCurrentDirectory(new File(studyPath.getAbsolutePath()+"/Interviews/"));
			
			int rOpen = fcOpen.showOpenDialog(this);
			if(rOpen != JFileChooser.APPROVE_OPTION)
				return;
			
			int rSave = fcSave.showSaveDialog(this);
			if(rSave != JFileChooser.APPROVE_OPTION)
				return;
			
			// don't warn about incomplete?
			// copy extant interview
			
			
			success = egoClient.getStorage().saveLongitudinalFile(fcOpen.getSelectedFile(), fcSave.getSelectedFile());
		}
		catch (IOException ex)
		{
			success = false;
		} catch (CorruptedInterviewException ex) {
			JOptionPane.showMessageDialog(this, "The original interview you selected is corrupted. Will not proceed.");
			success = false;
			logger.info("Corrupted interview while trying to start a longitudinal study", ex);
		}

		/* UI */
		if (success)
		{
		    egoClient.getFrame().gotoClientQuestionPanel();
		}
		else
		{
		    egoClient.getFrame().gotoSourceSelectPanel();
		}
	}

	void startBrandNewInterviewButton_actionPerformed(ActionEvent e)
	{
		boolean success = false;

		/* Logic */
		try
		{
			
			File studyFile = egoClient.getStorage().getStudyFile();
			File studyPath = studyFile.getParentFile();
			
			final JFileChooser fc = new JFileChooser() {
			    
				// this chooser needs to understand the "overwrite?" confirmation
				
				@Override
			    public void approveSelection(){
			        File f = getSelectedFile();
			        if(f.exists() && getDialogType() == SAVE_DIALOG){
			            int result = JOptionPane.showConfirmDialog(this,"The file exists, are you sure you want to continue with an existing interview?","Existing file",JOptionPane.YES_NO_CANCEL_OPTION);
			            switch(result){
			                case JOptionPane.YES_OPTION:
			                    super.approveSelection();
			                    return;
			                case JOptionPane.NO_OPTION:
			                    return;
			                case JOptionPane.CLOSED_OPTION:
			                    return;
			                case JOptionPane.CANCEL_OPTION:
			                    cancelSelection();
			                    return;
			            }
			        }
			        super.approveSelection();
			    }
			    
			};
			fc.setCurrentDirectory(new File(studyPath.getAbsolutePath()+"/Interviews/"));
			
			int result = fc.showSaveDialog(this);
			if(result != JFileChooser.APPROVE_OPTION)
				return;
			
			File fSelected = fc.getSelectedFile();
			if(fSelected.exists() && fSelected.canRead())
				success = egoClient.getStorage().continueInterview(fSelected);
			else
				success = egoClient.getStorage().saveInterview(fSelected);
		}
		catch (IOException ex)
		{
			success = false;
		} catch (CorruptedInterviewException ex) {
			JOptionPane.showMessageDialog(this, "The interview you selected is corrupted. Will not proceed.");
			success = false;
			logger.info("Corrupted interview while trying to continue a brand new interview", ex);
		}

		/* UI */
		if (success)
		{
		    egoClient.getFrame().gotoClientQuestionPanel();
		}
		else
		{
		    egoClient.getFrame().gotoSourceSelectPanel();
		}
	}

}