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
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.SwingConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Document;

import org.egonet.util.WholeNumberDocument;

import com.endlessloopsoftware.egonet.Study;
import com.endlessloopsoftware.egonet.Shared.AlterNameModel;
import com.endlessloopsoftware.egonet.Shared.AlterSamplingModel;


/**
 * Generic Panel creation and handling routines for question editing
 */
public class StudyPanel extends JPanel
{
	private final GridBagLayout	study_layout				= new GridBagLayout();
	private final JLabel				study_path_label			= new JLabel("Study Path:");
	private final JLabel				study_path_field			= new JLabel("< none selected >");
	private final JLabel				study_name_label			= new JLabel("Study Name:");
	private final JTextField		study_name_field			= new JTextField("< none selected >");
	private final JLabel				study_num_alters_label	= new JLabel("Number of Alters:");
	private final JTextField		study_num_alters_field	= new JTextField();
	
	private JLabel lblAlterModel = new JLabel("Alter Sampling Method");
	private ButtonGroup alterSampleGroup = new ButtonGroup();
	
	private JRadioButton btnAlterModelAll = new JRadioButton("Select and ask about all alters");
	
	private JRadioButton btnAlterModelRandomSubset = new JRadioButton("Select a random subset of alters");
	private JTextField txtAlterModelRandomSubset = new JTextField("0");
	
	private JRadioButton btnAlterModelNth = new JRadioButton("Select every Nth alter");
	private JTextField txtAlterModelNth = new JTextField("0");
	
	private JLabel lblAlterNames = new JLabel("Alter names prompt");
	private ButtonGroup alternamesGroup = new ButtonGroup();
	
	private JRadioButton btnAlterNamesFirstLast = new JRadioButton("Ask for separate first and last names");
	private JRadioButton btnAlterNamesSingle = new JRadioButton("Ask for name as a single field");

	private JCheckBox btnAllowQuestionSkip = new JCheckBox("Always allow 'Next' button for skipping questions");
	
	private final JLabel				titleLabel					= new JLabel("EgoCentric Network Study Configuration");
	private final JTextPane			instructionPane			= new JTextPane();
	private final Document			altersDocument				= new WholeNumberDocument();

	private final String[]			instructionStrings		= {
			"Start by selecting a study or choosing \"New Study\" from the file menu.", "Please name the study.",
			"You may add predefined questions to the study by selecting Import Questions from the file menu"};


	private final EgoNet egoNet;
	private ActionListener alterModelGroupActionListener;
	private ActionListener alterNameActionListener;
	private ActionListener allowSkipListener;
	
	/**
	 * Generates Panel for study configuration info
	 * @param 	parent	parent frame for this panel
	 */
	public StudyPanel(EgoNet egoNet) throws Exception
	{
		this.egoNet = egoNet;
		study_num_alters_field.setName("study_num_alters_field");
		btnAlterModelRandomSubset.setName("btnAlterModelRandomSubset");
		txtAlterModelRandomSubset.setName("txtAlterModelRandomSubset");
		jbInit();
	}

	/**
	 * Component initialization
	 */
	private void jbInit()
	{
		this.setLayout(study_layout);
		this.setMinimumSize(new Dimension(300, 200));
		this.setPreferredSize(new Dimension(500, 500));
		study_num_alters_field.setDocument(altersDocument);
		
		alterNameActionListener = new ActionListener()
		{
			public void actionPerformed(ActionEvent e) {
				Study study = egoNet.getStudy();
				if(alternamesGroup.isSelected(btnAlterNamesFirstLast.getModel())) {
					study.setAlterNameModel(AlterNameModel.FIRST_LAST);
				}
				else if(alternamesGroup.isSelected(btnAlterNamesSingle.getModel())) {
					study.setAlterNameModel(AlterNameModel.SINGLE);	
				}
			}
		};
		
		allowSkipListener = new ActionListener()
		{
			public void actionPerformed(ActionEvent e) {
				Study study = egoNet.getStudy();
                if(study != null) {
                	study.setAllowSkipQuestions(btnAllowQuestionSkip.isSelected());
                }
			}
		};
		
		alterModelGroupActionListener = new ActionListener()
		{
			public void actionPerformed(ActionEvent e) {
				txtAlterModelRandomSubset.setEnabled(false);
				txtAlterModelNth.setEnabled(false);
				
				if(alterSampleGroup.getSelection() == null)
					return;
				
				Study study = egoNet.getStudy();
				
				if(alterSampleGroup.isSelected(btnAlterModelAll.getModel()))
				{
                    if(study != null)
                        study.setAlterSamplingModel(AlterSamplingModel.ALL);
				}
				else if(alterSampleGroup.isSelected(btnAlterModelRandomSubset.getModel()))
				{
					txtAlterModelRandomSubset.setEnabled(true);
                    if(study != null)
                    {
                        study.setAlterSamplingModel(AlterSamplingModel.RANDOM_SUBSET);
                        study.setAlterSamplingParameter(Integer.parseInt(txtAlterModelRandomSubset.getText()));
                    }
				}
				else if(alterSampleGroup.isSelected(btnAlterModelNth.getModel()))
				{
					txtAlterModelNth.setEnabled(true);
                    if(study != null)
                    {
                        study.setAlterSamplingModel(AlterSamplingModel.NTH_ALTER);
                        study.setAlterSamplingParameter(Integer.parseInt(txtAlterModelNth.getText()));
                    }
				}
			}
			
		};
		alterSampleGroup.add(btnAlterModelAll); btnAlterModelAll.addActionListener(alterModelGroupActionListener);
		alterSampleGroup.add(btnAlterModelRandomSubset); btnAlterModelRandomSubset.addActionListener(alterModelGroupActionListener);
		alterSampleGroup.add(btnAlterModelNth); btnAlterModelNth.addActionListener(alterModelGroupActionListener);
		alterModelGroupActionListener.actionPerformed(null);
		
		btnAlterModelAll.setEnabled(false);
		btnAlterModelRandomSubset.setEnabled(false);
		btnAlterModelNth.setEnabled(false);
		
		
		alternamesGroup.add(btnAlterNamesFirstLast); btnAlterNamesFirstLast.addActionListener(alterNameActionListener);
		alternamesGroup.add(btnAlterNamesSingle);btnAlterNamesSingle.addActionListener(alterNameActionListener);

		btnAllowQuestionSkip.addActionListener(allowSkipListener);

		/* Question Layout */
		titleLabel.setBorder(BorderFactory.createEtchedBorder());
		titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
		instructionPane.setBackground(Color.lightGray);
		instructionPane.setBorder(BorderFactory.createLoweredBevelBorder());
		instructionPane.setEditable(false);
		instructionPane.setText(instructionStrings[0]);
		
		                                                 //    x  y  w  h  weightx/y                    anchor                     fill        insets                     padx/y
		add(titleLabel, 				new GridBagConstraints(0, 0, 4, 1, 1.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 10), 0, 0));
		add(study_name_label, 			new GridBagConstraints(0, 1, 1, 1, 0.0, 0.1, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 10, 10, 10), 0, 4));
		add(study_name_field, 			new GridBagConstraints(1, 1, 2, 1, 0.33, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(10, 10, 10, 10), 0, 6));
		add(study_path_label, 			new GridBagConstraints(0, 2, 1, 1, 0.0, 0.1, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 10, 10, 10), 0, 4));
		add(study_path_field, 			new GridBagConstraints(1, 2, 2, 1, 0.33, 0.0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(10, 10, 10, 10), 0, 4));
		add(study_num_alters_label, 	new GridBagConstraints(0, 4, 2, 1, 0.0,	0.1, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10, 10, 10, 10), 0, 0));
		add(study_num_alters_field, 	new GridBagConstraints(1, 4, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 10, 10, 10), 50, 8));

		
		add(lblAlterNames, 				new GridBagConstraints(0, 5, 2, 1, 0.0,	0.1, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10, 10, 10, 10), 0, 0));
		add(btnAlterNamesFirstLast, 	new GridBagConstraints(0, 6, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10, 10, 10, 10), 50, 8));
		add(btnAlterNamesSingle, 		new GridBagConstraints(1, 6, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10, 10, 10, 10), 50, 8));
		
		add(lblAlterModel, 				new GridBagConstraints(0, 9, 2, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 50, 8));

		add(btnAlterModelAll, 			new GridBagConstraints(0, 10, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 50, 8));
		
		add(btnAlterModelRandomSubset, 	new GridBagConstraints(0, 11, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 50, 8));
		add(txtAlterModelRandomSubset, 	new GridBagConstraints(1, 11, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 50, 8));

		add(btnAlterModelNth, 			new GridBagConstraints(0, 12, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 50, 8));
		add(txtAlterModelNth, 			new GridBagConstraints(1, 12, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 50, 8));

		add(btnAllowQuestionSkip,       new GridBagConstraints(0, 13, 2, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5, 5, 5, 5), 50, 8));
		
		add(instructionPane,            new GridBagConstraints(0, 15, 4, 1, 1.0, 0.15, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(10, 10, 10, 10), 0, 4));

		/***********************************************************************
		 * Action Listeners for buttons and other UI elements
		 * 
		 * @param e
		 *            event to be handled
		 */
		study_name_field.getDocument().addDocumentListener(new DocumentListener() {
			public void insertUpdate(DocumentEvent e) { studyNameTextEvent(); }
			public void changedUpdate(DocumentEvent e) { studyNameTextEvent(); }
			public void removeUpdate(DocumentEvent e) { studyNameTextEvent(); }});

		study_num_alters_field.getDocument().addDocumentListener(new DocumentListener() {
			public void insertUpdate(DocumentEvent e) { studyAltersTextEvent(); }
			public void changedUpdate(DocumentEvent e) { studyAltersTextEvent(); }
			public void removeUpdate(DocumentEvent e) { studyAltersTextEvent(); }});
		
	      txtAlterModelRandomSubset.getDocument().addDocumentListener(new DocumentListener() {
	            public void insertUpdate(DocumentEvent e) { studyAltersParamTextEventRandom(); }
	            public void changedUpdate(DocumentEvent e) { studyAltersParamTextEventRandom(); }
	            public void removeUpdate(DocumentEvent e) { studyAltersParamTextEventRandom(); }});
          txtAlterModelNth.getDocument().addDocumentListener(new DocumentListener() {
              public void insertUpdate(DocumentEvent e) { studyAltersParamTextEventNth(); }
              public void changedUpdate(DocumentEvent e) { studyAltersParamTextEventNth(); }
              public void removeUpdate(DocumentEvent e) { studyAltersParamTextEventNth(); }});

	}


	/****
	 * Clear all on screen editable fields
	 * Generally called when a new survey is started
	 */
	public void clearPanel()
	{
		study_name_field.setText("");
		study_path_field.setText("< none selected >");
	}

	/****
	 * Clear all on screen editable fields
	 * Generally called when a new survey is started
	 */
	public void fillPanel() throws IOException
	{
		boolean hasEgoNet = egoNet != null;
		boolean hasStorage = hasEgoNet && egoNet.getStorage() != null;
		boolean hasStudy = hasStorage && (egoNet.getStorage().getStudyFile() != null);

		Study study = egoNet.getStudy();
		
			study_name_field.			setEnabled(hasStudy);
			study_name_label.			setEnabled(hasStudy);
			study_num_alters_label.	setEnabled(hasStudy);
			study_num_alters_field.	setEnabled(hasStudy);
			study_path_label.			setEnabled(hasStudy);
			study_path_field.			setEnabled(hasStudy);

			study_name_field.			setText(
					egoNet
					.getStudy()
					.getStudyName());
			
			btnAllowQuestionSkip.setSelected(hasStudy && study.getAllowSkipQuestions());
			
			study_path_field.			setText(filename(egoNet.getStorage().getStudyFile()));
			study_num_alters_field.setText(Integer.toString(egoNet.getStudy().getNetworkSize()));
			
			lblAlterNames.setEnabled(hasStudy);
			btnAlterNamesFirstLast.setEnabled(hasStudy);
			btnAlterNamesSingle.setEnabled(hasStudy);
			
			lblAlterModel.setEnabled(hasStudy);
			btnAlterModelAll.setEnabled(hasStudy);
			btnAlterModelRandomSubset.setEnabled(hasStudy);
			txtAlterModelRandomSubset.setEnabled(hasStudy);
			btnAlterModelNth.setEnabled(hasStudy);
			txtAlterModelNth.setEnabled(hasStudy);
			
			btnAllowQuestionSkip.setEnabled(hasStudy);
			
			if(hasStudy && study.getAlterNameModel().equals(AlterNameModel.FIRST_LAST)) {
				alterSampleGroup.setSelected(btnAlterNamesFirstLast.getModel(), true);
			} 
			else if(hasStudy && study.getAlterNameModel().equals(AlterNameModel.SINGLE)) {
				alterSampleGroup.setSelected(btnAlterNamesSingle.getModel(), true);
			}
			
			if(hasStudy && study.getAlterSamplingModel().equals(AlterSamplingModel.RANDOM_SUBSET)) {
				alterSampleGroup.setSelected(btnAlterModelRandomSubset.getModel(), true);
				txtAlterModelRandomSubset.setText((study.getAlterSamplingParameter() != null ? study.getAlterSamplingParameter() : 0)+"");
			} else if(hasStudy && study.getAlterSamplingModel().equals(AlterSamplingModel.NTH_ALTER)) {
				alterSampleGroup.setSelected(btnAlterModelNth.getModel(), true);
				txtAlterModelNth.setText((study.getAlterSamplingParameter() != null ? study.getAlterSamplingParameter() : 0)+"");
			} else if(hasStudy && study.getAlterSamplingModel().equals(AlterSamplingModel.ALL)) {
				alterSampleGroup.setSelected(btnAlterModelAll.getModel(), true);
			} else {
				alterSampleGroup.clearSelection();
			}
			alterModelGroupActionListener.actionPerformed(null);
	}

	private String filename(File f) throws IOException
	{
		if (f == null)
		{
			return("< none selected >");
		}
		else
		{
			return(f.getPath().toString());
		}
	}

	private void studyNameTextEvent()
	{
		String s = study_name_field.getText();

		if (!egoNet.getStudy().getStudyName().equals(s))
		{
			egoNet.getStudy().setStudyName(s);
		}
	}

	private void studyAltersSamplingParams(String s)
	{
        int         i = 0;
        
        if (!s.equals(""))
        {
            i = Integer.parseInt(s);
        }
        
        if (i != egoNet.getStudy().getAlterSamplingParameter())
        {
            egoNet.getStudy().setAlterSamplingParameter(i);
        }
	}
	
	   private void studyAltersParamTextEventRandom()
	    {
	        String  s = txtAlterModelRandomSubset.getText();
	        studyAltersSamplingParams(s);
	    }
	
	private void studyAltersParamTextEventNth()
    {
        String  s = txtAlterModelNth.getText();
        studyAltersSamplingParams(s);
    }

	private void studyAltersTextEvent()
	{
		String 	s = study_num_alters_field.getText();
		int 		i = 0;
		
		if (!s.equals(""))
		{
			i = Integer.parseInt(s);
		}
		
		if (i != egoNet.getStudy().getNetworkSize())
		{
			egoNet.getStudy().setNetworkSize(i);
		}
	}
	
}