package com.endlessloopsoftware.ego.author;

/**
 * <p>Title: Egocentric Network Researcher</p>
 * <p>Description: Configuration Utilities for an Egocentric network study</p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: Endless Loop Software</p>
 * @author Peter C. Schoaff
 * @version 1.0
 *
 * $Id$
 */
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

import com.endlessloopsoftware.elsutils.documents.WholeNumberDocument;

/**
 * Generic Panel creation and handling routines for question editing
 */
public class StudyPanel extends JPanel
{
	private final EgoFrame 			frame;
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


	/**
	 * Generates Panel for study configuration info
	 * @param 	parent	parent frame for this panel
	 */
	public StudyPanel(EgoFrame parent)
	{
		frame 	= parent;

		try
		{
			jbInit();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
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
		boolean hasStudy = (EgoNet.storage.getStudyFile() != null);

		try
		{
			study_name_field.			setEnabled(hasStudy);
			study_name_label.			setEnabled(hasStudy);
			study_num_alters_label.	setEnabled(hasStudy);
			study_num_alters_field.	setEnabled(hasStudy && !EgoNet.storage.getStudyInUse());
			study_path_label.			setEnabled(hasStudy);
			study_path_field.			setEnabled(hasStudy);

			study_name_field.			setText(EgoNet.study.getStudyName());
			study_path_field.			setText(filename(EgoNet.storage.getStudyFile()));

			study_num_alters_field.setText(Integer.toString(EgoNet.study.getNumAlters()));
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	private String filename(File f)
			throws IOException
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

		if (!EgoNet.study.getStudyName().equals(s))
		{
			EgoNet.study.setStudyName(s);
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
		
		if (i != EgoNet.study.getNumAlters())
		{
			EgoNet.study.setNumAlters(i);
			//EgoNet.study.setCompatible(false);
		}
	}
	
}

/**
 * $Log$
 * Revision 1.1  2007/09/03 13:51:20  schoaff
 * Initial Checkin
 *
 * Revision 1.9  2004/03/22 18:01:28  admin
 * Fixed problem confirming alterNums by simply making it non-editable when
 * the study is in use
 *
 * Now confirming changes to number of selections
 *
 * Revision 1.8  2004/03/21 20:29:37  admin
 * Warn before making incompatible changes to in use study file
 *
 * Revision 1.7  2004/03/21 14:00:38  admin
 * Cleaned up Question Panel Layout using FOAM
 *
 * Revision 1.6  2004/02/26 21:19:17  admin
 * adding jardescs
 *
 * Revision 1.5  2004/02/10 20:10:43  admin
 * Version 2.0 beta 3
 *
 * Revision 1.4  2003/12/05 19:15:44  admin
 * Extracting Study
 *
 * Revision 1.3  2003/12/04 15:14:09  admin
 * Merging EgoNet and EgoClient projects so that they can share some
 * common classes more easily.
 *
 * Revision 1.2  2003/11/25 19:25:44  admin
 * Warn before closing window
 *
 * Revision 1.1.1.1  2003/06/08 15:09:40  admin
 * Egocentric Network Survey Authoring Module
 *
 * Revision 1.12  2002/08/30 16:50:28  admin
 * Using Selections
 *
 * Revision 1.11  2002/08/11 22:26:06  admin
 * Final Statistics window, new file handling
 *
 * Revision 1.10  2002/08/08 17:07:26  admin
 * Preparing to change file system
 *
 * Revision 1.9  2002/07/24 14:17:10  admin
 * xml files, links
 *
 * Revision 1.7  2002/06/30 15:59:18  admin
 * Moving questions in lists, between lists
 * Better category input
 *
 * Revision 1.6  2002/06/26 15:43:43  admin
 * More selection dialog work
 * File loading fixes
 *
 * Revision 1.5  2002/06/25 15:41:02  admin
 * Lots of UI work
 *
 * Revision 1.4  2002/06/21 22:47:13  admin
 * question lists working again
 *
 * Revision 1.3  2002/06/21 21:52:50  admin
 * Many changes to event handling, file handling
 *
 * Revision 1.2  2002/06/19 01:57:05  admin
 * Much UI work done
 *
 * Revision 1.1  2002/06/16 17:53:31  admin
 * new File
 *
 * Revision 1.2  2002/06/15 14:19:51  admin
 * Initial Checkin of question and survey
 * General file system work
 *
 */

