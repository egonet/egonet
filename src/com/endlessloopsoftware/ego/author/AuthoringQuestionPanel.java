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
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.Arrays;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.egonet.exceptions.DuplicateQuestionException;

import com.endlessloopsoftware.ego.Question;

/**
 * Generic Panel creation and handling routines for question editing
 */
public class AuthoringQuestionPanel extends EgoQPanel
{
	private final	int						questionType;
	private 			boolean				inUpdate;

	private final JSplitPane				question_split						= new JSplitPane();
	private final JList					question_list						= new JList();
	private final JScrollPane			question_list_scroll				= new JScrollPane(question_list);
	private final JPanel					question_panel_right				= new RightPanel();
	private final JLabel					question_title_label				= new JLabel("Title:");
	private final JLabel					question_question_label			= new JLabel("Question:");
	private final JLabel					question_citation_label			= new JLabel("Citation:");
	private final JLabel					question_type_label				= new JLabel("Question Type:");
	private final JComboBox				question_type_menu					= new JComboBox(questionTypes);
	private final JLabel					question_answer_type_label		= new JLabel("Answer Type:");
	private final JButton					question_answer_type_button		= new JButton("Selections");
	private final JLabel					question_link_label				= new JLabel("Question Link:");
	private final JLabel					question_link_field				= new JLabel("None");
	private final JLabel					question_follows_label			= new JLabel("Follows Question:");
	private final JComboBox				question_answer_type_menu		= new JComboBox(answerTypes);
	private final JComboBox				question_follows_menu				= new JComboBox();
	private final JTextArea				question_question_field			= new NoTabTextArea();
	private final JTextArea				question_citation_field			= new NoTabTextArea();
	private final JTextField				question_title_field				= new JTextField();
	private final JButton					question_new_button				= new JButton("New");
	private final JButton					question_duplicate_button				= new JButton("Duplicate");
	private final JButton					question_link_button				= new JButton("Set Link");
	private final JButton					question_delete_button			= new JButton("Delete");
	private final JLabel					question_central_label			= new JLabel();
	private final CategoryInputPane	selectionsDialog;
	private final QuestionLinkDialog	questionLinkDialog;
	private final Border					listBorder;

	private final static String[] questionTypes = {Question.questionTypeString(1), Question.questionTypeString(2),
													Question.questionTypeString(3), Question.questionTypeString(4)};
	private final static String[] answerTypes = {"Categorical", "Numerical", "Text"};

	/**
	 * Generates Panel for question editing to insert in file tab window
	 * @param 	type	Type of questions on Page (e.g. Alter Questions)
	 * @param	parent	parent frame for referencing composed objects
	 */
	public AuthoringQuestionPanel(int type)
	{
		questionType					= type;
		questionLinkDialog			= new QuestionLinkDialog();
		selectionsDialog			= new CategoryInputPane(question_list);

		listBorder 					= BorderFactory.createCompoundBorder(
			new TitledBorder(new EtchedBorder(EtchedBorder.RAISED,Color.white,new Color(178, 178, 178)),
			Question.questionTypeString(questionType)),
			BorderFactory.createEmptyBorder(10,10,10,10));

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
	 * @throws Exception
	 */
	private void jbInit()
			throws Exception
	{
		inUpdate = true;

		// Configure Split Frame
		question_split.setMinimumSize(new Dimension(430, 330));
		question_split.setPreferredSize(new Dimension(430, 330));
		question_split.setResizeWeight(.33);
		question_split.setDividerLocation(.33);
		question_list_scroll.setRequestFocusEnabled(false);
		question_split.add(question_list_scroll, JSplitPane.LEFT);
		question_split.add(question_panel_right, JSplitPane.RIGHT);

		this.setLayout(new GridLayout());

		// Configure List
		question_list_scroll.setBorder(listBorder);
		question_list_scroll.setMinimumSize(new Dimension(150, 150));
		question_list_scroll.setPreferredSize(new Dimension(150, 150));
		question_list_scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

		question_list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
//		question_list.setCellRenderer(new QuestionListCellRenderer());

		// Configure question fields
		question_panel_right.setLayout(new GridBagLayout());
		question_question_field.setMaximumSize(new Dimension(280, 64));
		question_question_field.setMinimumSize(new Dimension(72, 16));
		question_question_field.setPreferredSize(new Dimension(72, 16));
		question_question_field.setLineWrap(true);
		question_question_field.setRows(1);
		question_question_field.setTabSize(4);
		question_question_field.setWrapStyleWord(true);

		question_citation_field.setMaximumSize(new Dimension(280, 64));
		question_citation_field.setMinimumSize(new Dimension(72, 16));
		question_citation_field.setPreferredSize(new Dimension(72, 16));
		question_citation_field.setLineWrap(true);
		question_citation_field.setRows(1);
		question_citation_field.setTabSize(4);
		question_citation_field.setWrapStyleWord(true);

		/* Question Layout */
		question_panel_right.add(question_title_label, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0
			,GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10, 10, 10, 10), 0, 0));
		question_panel_right.add(question_title_field, new GridBagConstraints(1, 0, 2, 1, 0.0, 0.0
			,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(10, 10, 10, 10), 0, 4));
		question_panel_right.add(question_question_label, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0
			,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 10, 10, 10), 0, 0));
		question_panel_right.add(question_question_field, new GridBagConstraints(1, 1, 2, 3, 0.0, 0.4
			,GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(10, 10, 10, 10), 0, 0));
		question_panel_right.add(question_citation_label, new GridBagConstraints(0, 4, 1, 1, 0.0, 0.0
			,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 10, 10, 10), 0, 0));
		question_panel_right.add(question_citation_field, new GridBagConstraints(1, 4, 2, 3, 0.0, 0.3
			,GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(10, 10, 10, 10), 0, 0));
		question_panel_right.add(question_type_label, new GridBagConstraints(0, 7, 1, 1, 0.0, 0.0
			 ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 10, 10, 10), 0, 0));
		question_panel_right.add(question_type_menu, new GridBagConstraints(1, 7, 1, 1, 0.0, 0.0
			 ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 10, 10, 10), 0, 0));
		question_panel_right.add(question_central_label, new GridBagConstraints(2, 7, 1, 1, 0.2, 0.0
				,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(10, 10, 10, 10), 0, 0));
		question_panel_right.add(question_answer_type_label, new GridBagConstraints(0, 8, 1, 1, 0.0, 0.0
			,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 10, 10, 10), 0, 0));
		question_panel_right.add(question_answer_type_menu,  new GridBagConstraints(1, 8, 1, 1, 0.0, 0.0
			,GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10, 10, 10, 10), 0, 0));
		question_panel_right.add(question_answer_type_button, new GridBagConstraints(2, 8, 1, 1, 0.2, 0.0
			,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(10, 10, 10, 10), 0, 0));
		question_panel_right.add(question_follows_label, new GridBagConstraints(0, 9, 1, 1, 0.0, 0.0
			,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 10, 10, 10), 0, 0));
		question_panel_right.add(question_follows_menu,  new GridBagConstraints(1, 9, 1, 1, 0.0, 0.0
			,GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10, 10, 10, 10), 0, 0));
		question_panel_right.add(question_link_label, new GridBagConstraints(0, 10, 1, 1, 0.0, 0.0
			,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 10, 10, 10), 0, 0));
		question_panel_right.add(question_link_field, new GridBagConstraints(1, 10, 2, 1, 0.0, 0.0
			,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 10, 10, 10), 0, 0));
		question_panel_right.add(question_new_button, new GridBagConstraints(0, 11, 1, 1, 0.33, 0.0
			,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(10, 10, 10, 10), 0, 0));
		question_panel_right.add(question_link_button, new GridBagConstraints(1, 11, 1, 1, 0.33, 0.0
			,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(10, 10, 10, 10), 0, 0));
		question_panel_right.add(question_delete_button, new GridBagConstraints(2, 11, 1, 1, 0.33, 0.0
			,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(10, 10, 10, 10), 0, 0));
		question_panel_right.add(question_duplicate_button, new GridBagConstraints(3, 11, 1, 1, 0.33, 0.0
				,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(10, 10, 10, 10), 0, 0));

		question_list.setModel(new DefaultListModel());
		EgoNet.study.fillList(questionType, (DefaultListModel) question_list.getModel());

		question_list.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				question_list_selectionChanged(e); }});

		question_new_button.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(ActionEvent e) {
				question_new_button_actionPerformed(e);}});

		question_delete_button.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(ActionEvent e) {
				question_delete_button_actionPerformed(e);}});

		question_duplicate_button.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(ActionEvent e) {
				question_duplicate_button_actionPerformed(e);}});
		
		question_link_button.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(ActionEvent e) {
				question_link_button_actionPerformed(e);}});

		question_follows_menu.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(ActionEvent e) {
				question_follows_menu_actionPerformed(e);}});

		question_type_menu.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(ActionEvent e) {
				question_type_menu_actionPerformed(e);}});

		question_answer_type_menu.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(ActionEvent e) {
				question_answer_type_menu_actionPerformed(e);}});

		question_answer_type_button.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(ActionEvent e) {
				set_selections_button_actionPerformed(e);}});

		question_title_field.getDocument().addDocumentListener(new DocumentListener() {
			public void insertUpdate(DocumentEvent e) { questionTitleEvent(); }
			public void changedUpdate(DocumentEvent e) { questionTitleEvent(); }
			public void removeUpdate(DocumentEvent e) { questionTitleEvent(); }});

		question_question_field.getDocument().addDocumentListener(new DocumentListener() {
			public void insertUpdate(DocumentEvent e) { questionTextEvent(); }
			public void changedUpdate(DocumentEvent e) { questionTextEvent(); }
			public void removeUpdate(DocumentEvent e) { questionTextEvent(); }});

		question_citation_field.getDocument().addDocumentListener(new DocumentListener() {
			public void insertUpdate(DocumentEvent e) { questionCitationEvent(); }
			public void changedUpdate(DocumentEvent e) { questionCitationEvent(); }
			public void removeUpdate(DocumentEvent e) { questionCitationEvent(); }});

		this.add(question_split, null);

		inUpdate = false;
	}

	/**
	 * Updates right side question fields when the selection changes
	 * @param 	e event generated by selection change.
	 */
	private void question_list_selectionChanged(ListSelectionEvent e)
	{
		if (!e.getValueIsAdjusting())
		{
			if (!inUpdate)
			{
				questionUpdate();
			}
		}
	}

	/****
	 * fill List with appropriate questions
	 * Set other fields to selected question
	 */
	public void fillPanel()
	{
		if (questionType == EgoNet.frame.curTab)
		{
			storageUpdate();
			questionUpdate();
		}
	}

	/**
	 * Called when file changes to load new questions into list
	 */
	private void storageUpdate()
	{
		inUpdate = true;

		if (questionType == EgoNet.frame.curTab)
		{
			Object o = question_list.getSelectedValue();
			((DefaultListModel) question_list.getModel()).removeAllElements();
			EgoNet.study.fillList(questionType, (DefaultListModel) question_list.getModel());
			question_list.setSelectedValue(o, true);
		}

		inUpdate = false;
	}

	private void questionUpdate()
	{
		Question 	q;
		int			index;

		inUpdate = true;

		if (questionType == EgoNet.frame.curTab)
		{
			/* If no element selected, assume first */
			index = question_list.getSelectedIndex();
			if ((index == -1) && (question_list.getModel().getSize() > 0))
			{
				index = 0;
			}

			/* Load questions from list into follows menu */
			question_follows_menu.removeAllItems();
			question_follows_menu.addItem(EgoNet.study.getFirstQuestion());
			for (int i = 0; i < question_list.getModel().getSize(); i++)
			{
				if (i != index)
				{
					question_follows_menu.addItem(question_list.getModel().getElementAt(i));
				}
			}

			question_list.setSelectedIndex(index);
			q = (Question) question_list.getSelectedValue();
			if (q != null)
			{
				question_type_menu.setSelectedIndex(questionType - 1);
				question_answer_type_menu.setSelectedIndex(q.answerType);
				question_question_field.setText(q.text);
				question_citation_field.setText(q.citation);
				question_title_field.setText(q.title);
				question_follows_menu.setSelectedIndex(index);

				question_type_menu.setEnabled(true);
				question_answer_type_menu.setEnabled(q.questionType != Question.ALTER_PROMPT);
				
				question_answer_type_button.setEnabled(q.answerType == Question.CATEGORICAL);
				question_question_field.setEditable(true);
				question_citation_field.setEditable(true);
				question_title_field.setEditable(true);
				question_delete_button.setEnabled(true);
				question_duplicate_button.setEnabled(true);
				question_link_button.setEnabled(true);

				/* Box only appears on alter pair page */
				question_central_label.setVisible(false);
				if (q.answerType == Question.CATEGORICAL)
				{
					if (q.selections.length == 0)
					{
						question_central_label.setText("No Selections");
						question_central_label.setForeground(Color.red);
						question_central_label.setVisible(true);
					}
					else if (questionType == Question.ALTER_PAIR_QUESTION)
					{
						question_central_label.setText("No Adjacency Selections");
						question_central_label.setForeground(Color.red);

						for (int i = 0; i < q.selections.length; i++)
						{
							if (q.selections[i].isAdjacent())
							{
								question_central_label.setText("Adjacency Selections Set");
								question_central_label.setForeground(Color.black);
							}
						}

						question_central_label.setVisible(true);
					}
				}

				/* Fill in link field */
				if (q.link.active)
				{
					Question linkQuestion = EgoNet.study.getQuestions().getQuestion(q.link.answer.questionId);

					if (linkQuestion == null)
					{
						question_link_field.setText("< none >");
					}
					else
					{
						if (linkQuestion.title.length() > 32)
						{
							question_link_field.setText(linkQuestion.title.substring(0, 32) + ": " + q.link.answer.string);
						}
						else
						{
							question_link_field.setText(linkQuestion.title + ": " + q.link.answer.string);
						}
					}
				}
				else
				{
					question_link_field.setText("< none >");
				}
			}
			else
			{
				question_answer_type_menu.setSelectedIndex(0);
				question_question_field.setText(null);
				question_citation_field.setText(null);
				question_title_field.setText(null);
				question_central_label.setVisible(false);
				question_link_field.setText("");

				question_type_menu.setEnabled(false);
				question_answer_type_menu.setEnabled(false);
				question_answer_type_button.setEnabled(false);
				question_question_field.setEditable(false);
				question_citation_field.setEditable(false);
				question_title_field.setEditable(false);
				question_delete_button.setEnabled(false);
				question_link_button.setEnabled(false);
				
				question_duplicate_button.setEnabled(false);
			}
		}

		inUpdate = false;
	}


	/****
	 * Clear all on screen editable fields
	 * Generally called when a new survey is started
	 */
	public void clearPanel()
	{
		inUpdate = true;
		((DefaultListModel) question_list.getModel()).removeAllElements();
		inUpdate = false;
	}

	/****
	 * Document event handler used to read text fields
	 */
	private void questionTitleEvent()
	{
		Question q = (Question) question_list.getSelectedValue();
		String s;

		if ((q != null) && !inUpdate)
		{
			s = question_title_field.getText().trim();
			if ((q.title == null) || (!q.title.equals(s)))
			{
				q.title = question_title_field.getText().trim();
				EgoNet.study.setModified(true);
				question_list.repaint();
			}
		}
	}


	/****
	 * Document event handler used to read text fields
	 */
	private void questionTextEvent()
	{
		Question q = (Question) question_list.getSelectedValue();
		String s;

		if ((q != null) && !inUpdate)
		{
			s = question_question_field.getText().trim();
			if ((q.text == null) || (!q.text.equals(s)))
			{
				q.text = question_question_field.getText().trim();
				EgoNet.study.setModified(true);
			}
		}
	}


	/****
	 * Document event handler used to read text fields
	 */
	private void questionCitationEvent()
	{
		Question q = (Question) question_list.getSelectedValue();
		String s;

		if ((q != null) && !inUpdate)
		{
			s = question_citation_field.getText().trim();
			if ((q.citation == null) || (!q.citation.equals(s)))
			{
				q.citation = question_citation_field.getText().trim();
				EgoNet.study.setModified(true);
			}
		}
	}



	/****
	 * Event handler for new question button
	 * @param e Action Event
	 */
	private void question_new_button_actionPerformed(ActionEvent e)
	{
		if (EgoNet.study.confirmIncompatibleChange(EgoNet.frame))
		{
			Question q = new Question();
	
			q.questionType = questionType;
			q.title = new String(Question.questionTypeString(questionType) + ":Untitled Question");
         
         if (q.questionType == Question.ALTER_PROMPT)
         {
            q.answerType = Question.TEXT;
         }
	
			try
			{
				EgoNet.study.addQuestion(q);
			}
			catch (DuplicateQuestionException e1)
			{
            e1.printStackTrace();
			}
			
			fillPanel();
			question_list.setSelectedValue(q, true);
	
			question_title_field.requestFocus();
			question_title_field.setSelectionStart(0);
			question_title_field.setSelectionEnd(question_title_field.getText().length());
	
			EgoNet.study.setModified(true);
			EgoNet.study.setCompatible(false);
		}
	}


	/****
	 * Event handler for delete question button
	 * @param e Action Event
	 */
	private void question_delete_button_actionPerformed(ActionEvent e)
	{
		Question	q = (Question) question_list.getSelectedValue();

		if (q != null)
		{
			int confirm = JOptionPane.showConfirmDialog(EgoNet.frame, "Permanently remove this questions?",
														"Delete Question", JOptionPane.OK_CANCEL_OPTION);

			if ((confirm == JOptionPane.OK_OPTION)  && EgoNet.study.confirmIncompatibleChange(EgoNet.frame))
			{
				EgoNet.study.removeQuestion(q);
				EgoNet.study.setModified(true);
				EgoNet.study.setCompatible(false);
				fillPanel();
			}
		}
	}
	
	/****
	 * Event handler for dupe question button
	 * @param e Action Event
	 */
	private void question_duplicate_button_actionPerformed(ActionEvent e)
	{
		Question	q_old = (Question) question_list.getSelectedValue();
		if(q_old==null)
		{
			JOptionPane.showMessageDialog(EgoNet.frame, "Select a question first!",
						"Dupe Question", JOptionPane.OK_OPTION);
			return;
		}

		if (EgoNet.study.confirmIncompatibleChange(EgoNet.frame))
		{
			Question q = new Question();
	
			q.questionType = q_old.questionType;
			q.title = new String(Question.questionTypeString(questionType) + ": "+q_old.title+
					(q_old.title != null && q_old.title.endsWith("Duplicate Question") ? "" : " (Duplicate Question)")
						);
			q.answerType = q_old.answerType;
			q.citation = q_old.citation;
			q.statable = q_old.statable;
			q.text = q_old.text;
	
			try
			{
				EgoNet.study.addQuestion(q);
				q.selections = Arrays.copyOf(q_old.selections, q_old.selections.length);
			}
			catch (DuplicateQuestionException e1)
			{
            e1.printStackTrace();
			}
			
			fillPanel();
			question_list.setSelectedValue(q, true);
	
			question_title_field.requestFocus();
			question_title_field.setSelectionStart(0);
			question_title_field.setSelectionEnd(question_title_field.getText().length());
	
			EgoNet.study.setModified(true);
			EgoNet.study.setCompatible(false);
		}
	}

	/**
	 * Opens Set Link Dialog
	 * @param e UI event
	 */
	void question_link_button_actionPerformed(ActionEvent e)
	{
		Question q = (Question) question_list.getSelectedValue();
		questionLinkDialog.pack();
		questionLinkDialog.activate(q);
	}

	/**
	 * Change question type in question record, move to new ordering list
	 * @param e UI event
	 */
	void question_type_menu_actionPerformed(ActionEvent e)
	{
		if (!inUpdate)
		{
			if (EgoNet.study.confirmIncompatibleChange(EgoNet.frame))
			{
				Question	q 		= (Question) question_list.getSelectedValue();
				int 		type 	= question_type_menu.getSelectedIndex() + 1;
	
				EgoNet.study.changeQuestionType(q, type);
				EgoNet.study.setCompatible(false);
				fillPanel();
			}
			else
			{
				questionUpdate();
			}
		}
	}

	/**
	 * Change answer type in pop up menu, save in question record
	 * @param e UI event
	 */
	void question_answer_type_menu_actionPerformed(ActionEvent e)
	{
		if (!inUpdate)
		{
			if (EgoNet.study.confirmIncompatibleChange(EgoNet.frame))
			{
				int 		i = question_answer_type_menu.getSelectedIndex();
				Question	q = (Question) question_list.getSelectedValue();
	
				if (q != null)
				{
					if (q.answerType != i)
					{
						q.answerType = i;
						EgoNet.study.setModified(true);
						EgoNet.study.setCompatible(false);
						questionUpdate();
					}
				}
			}
			else
			{
				// Change back
				questionUpdate();
			}
		}
	}

	/**
	 * Brings up category selection modal dialog box
	 * @param e UI event
	 */
	void set_selections_button_actionPerformed(ActionEvent e)
	{
		selectionsDialog.activate();
	}

	/**
	 * Changes order of questions
	 * @param e UI event
	 */
	void question_follows_menu_actionPerformed(ActionEvent e)
	{
		if (!inUpdate)
		{
			if (EgoNet.study.confirmIncompatibleChange(EgoNet.frame))
			{
				Question follows 	= (Question) question_follows_menu.getSelectedItem();
				Question q			= (Question) question_list.getSelectedValue();
	
				EgoNet.study.moveQuestionAfter(q, follows);
				EgoNet.study.setCompatible(false);
				EgoNet.study.setModified(true);
				fillPanel();
			}
			else
			{
				questionUpdate();
			}
		}
	}

	void question_central_checkBox_actionPerformed(ActionEvent e)
	{
		Question q = (Question) question_list.getSelectedValue();
		EgoNet.study.setCentralQuestion(q);
	}
}


/**
 * Implements ListCellRenderer to differentiate between base and custom questions
 */
class QuestionListCellRenderer
		implements ListCellRenderer
{
	protected final DefaultListCellRenderer defaultRenderer = new DefaultListCellRenderer();

	public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus)
	{
		JLabel renderer = (JLabel) defaultRenderer.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
		return renderer;
	}
}