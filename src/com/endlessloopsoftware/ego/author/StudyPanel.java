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
import java.io.File;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.SwingConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Document;

import com.endlessloopsoftware.elsutils.WholeNumberDocument;

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
	private final JLabel				titleLabel					= new JLabel("EgoCentric Network Study Configuration");
	private final JTextPane			instructionPane			= new JTextPane();
	private final Document			altersDocument				= new WholeNumberDocument();

	private final String[]			instructionStrings		= {
			"Start by selecting a study or choosing \"New Study\" from the file menu.", "Please name the study.",
			"You may add predefined questions to the study by selecting Import Questions from the file menu"};


	private final EgoNet egoNet;
	
	/**
	 * Generates Panel for study configuration info
	 * @param 	parent	parent frame for this panel
	 */
	public StudyPanel(EgoNet egoNet) throws Exception
	{
		this.egoNet = egoNet;
		jbInit();
	}

	/**
	 * Component initialization
	 */
	private void jbInit()
	{
		this.setLayout(study_layout);
		this.setMinimumSize(new Dimension(300, 200));
		this.setPreferredSize(new Dimension(400, 400));
		study_num_alters_field.setDocument(altersDocument);

		/* Question Layout */
		titleLabel.setBorder(BorderFactory.createEtchedBorder());
		titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
		instructionPane.setBackground(Color.lightGray);
		instructionPane.setBorder(BorderFactory.createLoweredBevelBorder());
		instructionPane.setEditable(false);
		instructionPane.setText(instructionStrings[0]);

		add(titleLabel,       new GridBagConstraints(0, 0, 4, 1, 1.0, 0.0
			,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 10), 0, 0));
		add(study_name_label,          new GridBagConstraints(0, 1, 1, 1, 0.0, 0.1
			,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 10, 10, 10), 0, 4));
		add(study_name_field,              new GridBagConstraints(1, 1, 2, 1, 0.33, 0.0
			,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(10, 10, 10, 10), 0, 6));
		add(study_path_label,         new GridBagConstraints(0, 2, 1, 1, 0.0, 0.1
			,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 10, 10, 10), 0, 4));
		add(study_path_field,           new GridBagConstraints(1, 2, 2, 1, 0.33, 0.0
			,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(10, 10, 10, 10), 0, 4));
		add(study_num_alters_label,           new GridBagConstraints(0, 4, 2, 1, 0.0, 0.1
			,GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10, 10, 10, 10), 0, 0));
		add(study_num_alters_field,              new GridBagConstraints(2, 4, 1, 1, 0.0, 0.0
			,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 10, 10, 10), 50, 8));
		add(instructionPane,            new GridBagConstraints(0, 5, 4, 1, 1.0, 0.15
			,GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(10, 10, 10, 10), 0, 4));

		/****
		 * Action Listeners for buttons and other UI elements
		 * @param e event to be handled
		 */
		study_name_field.getDocument().addDocumentListener(new DocumentListener() {
			public void insertUpdate(DocumentEvent e) { studyNameTextEvent(); }
			public void changedUpdate(DocumentEvent e) { studyNameTextEvent(); }
			public void removeUpdate(DocumentEvent e) { studyNameTextEvent(); }});

		study_num_alters_field.getDocument().addDocumentListener(new DocumentListener() {
			public void insertUpdate(DocumentEvent e) { studyAltersTextEvent(); }
			public void changedUpdate(DocumentEvent e) { studyAltersTextEvent(); }
			public void removeUpdate(DocumentEvent e) { studyAltersTextEvent(); }});
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
	public void fillPanel()
	{
		boolean hasEgoNet = egoNet != null;
		boolean hasStorage = hasEgoNet && egoNet.getStorage() != null;
		boolean hasStudy = hasStorage && (egoNet.getStorage().getStudyFile() != null);

			study_name_field.			setEnabled(hasStudy);
			study_name_label.			setEnabled(hasStudy);
			study_num_alters_label.	setEnabled(hasStudy);
			study_num_alters_field.	setEnabled(hasStudy && !egoNet.getStorage().getStudyInUse());
			study_path_label.			setEnabled(hasStudy);
			study_path_field.			setEnabled(hasStudy);

			study_name_field.			setText(
					egoNet
					.getStudy()
					.getStudyName());
			
			try {
			study_path_field.			setText(filename(egoNet.getStorage().getStudyFile()));
			} catch (IOException ex)
			{
				throw new RuntimeException(ex);
			}

			study_num_alters_field.setText(Integer.toString(egoNet.getStudy().getNumAlters()));
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

	private void studyAltersTextEvent()
	{
		String 	s = study_num_alters_field.getText();
		int 		i = 0;
		
		if (!s.equals(""))
		{
			i = Integer.parseInt(s);
		}
		
		if (i != egoNet.getStudy().getNumAlters())
		{
			egoNet.getStudy().setNumAlters(i);
		}
	}
	
}