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
package org.egonet.gui.author;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.Observable;
import java.util.Observer;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.text.PlainDocument;

import org.egonet.model.answer.*;
import org.egonet.model.question.AlterPairQuestion;
import org.egonet.model.question.AlterPromptQuestion;
import org.egonet.model.question.Question;
import org.egonet.model.question.Selection;
import org.egonet.util.CatchingAction;
import org.egonet.util.WholeNumberDocument;

/**
 * Generic Panel creation and handling routines for question editing
 */
public class QuestionLinkDialog extends JDialog
		implements Observer
{
	private Question                  baseQuestion;
   private Answer                    linkAnswer           = new TextAnswer(new Long(-1));

   /* Containers */
   private final JSplitPane          questionSplit        = new JSplitPane();
   private final JList<Question>               questionList         = new JList<Question>();
   private final JScrollPane         questionListScroll   = new JScrollPane(questionList);
   private final JPanel              answerPanel          = new JPanel();
   private final JPanel              radioPanel           = new JPanel();
   private final JPanel              menuPanel            = new JPanel();
   private final JPanel              textPanel            = new JPanel();
   private final JPanel              questionPanelLeft    = new JPanel();
   private final JPanel              questionPanelRight   = new RightPanel();
   private final GridBagLayout       questionListLayout   = new GridBagLayout();
   private final GridBagLayout       answerRadioLayout    = new GridBagLayout();
   private final GridBagLayout       answerTextLayout     = new GridBagLayout();
   private final GridBagLayout       answerMenuLayout     = new GridBagLayout();
   private final GridLayout          answerPanelLayout    = new GridLayout();

   /* UI Elements */
   private final Border              listBorder;
   private final JLabel              titleText            = new JLabel();
   private final JTextArea           questionText         = new JTextArea();
   private final JTextArea           answerTextField      = new NoTabTextArea();
   private final JButton             questionButtonOK     = new JButton("OK");
   private final JButton             questionButtonCancel = new JButton("Cancel");
   private final JButton             questionButtonNone   = new JButton("No Link");
   private final JLabel              textAnswerLabel      = new JLabel("Answer: ");
   private final JLabel              menuAnswerLabel      = new JLabel("Answer: ");
   private final JLabel              radioAnswerLabel     = new JLabel("Answer: ");
   private final JComboBox<Selection>           answerMenu           = new JComboBox<Selection>();
   private final JCheckBox           allAdjacentCheck     = new JCheckBox("All Adjacent Selections");

   private final ButtonGroup         answerButtonGroup    = new ButtonGroup();
   private ActionListener            answerButtonListener;
   private final WholeNumberDocument wholeNumberDocument  = new WholeNumberDocument();
   private final PlainDocument       plainDocument        = new PlainDocument();

   /* Lists */
   private final static int          MAX_BUTTONS          = 5;
   private final JRadioButton[]      answerButtons        = { new JRadioButton(), new JRadioButton(),
         new JRadioButton(), new JRadioButton(), new JRadioButton(), new JRadioButton()};

   /* Question Iteration Variables */
   private Question                  question;

   private EgoNet egoNet;
   
   
	/**
	 * Generates Panel for question editing to insert in file tab window
	 * @param	parent	parent frame for referencing composed objects
	 */
	public QuestionLinkDialog(EgoNet egoNet) throws Exception
	{
		this.egoNet = egoNet;
		listBorder 		= BorderFactory.createCompoundBorder(
			new TitledBorder(new EtchedBorder(EtchedBorder.RAISED,Color.white,new Color(178, 178, 178)), "Questions"),
			BorderFactory.createEmptyBorder(10,10,10,10));
			jbInit();
	}

	/**
	 * Component initialization
	 * @throws Exception
	 */
	private void jbInit()
			throws Exception
	{
		/* Overview Layout */
		this.getContentPane().setLayout(new GridLayout());

		// Configure Split Frame
		questionSplit.setMinimumSize(new Dimension(430, 330));
		questionSplit.setPreferredSize(new Dimension(430, 330));
		questionSplit.setResizeWeight(.33);
		questionSplit.setDividerLocation(.33);
		questionText.setBackground(SystemColor.window);
		questionText.setFont(new java.awt.Font("Serif", 0, 16));
		questionText.setLineWrap(true);
		questionText.setTabSize(4);
		questionText.setWrapStyleWord(true);
		questionText.setEditable(false);

		questionSplit.add(questionPanelLeft, JSplitPane.LEFT);
		questionSplit.add(questionPanelRight, JSplitPane.RIGHT);

		questionPanelLeft.setLayout(questionListLayout);
		questionPanelLeft.setVisible(true);

		titleText.setFont(new java.awt.Font("Lucida Grande Bold", 0, 16));

		// Configure List
		questionListScroll.setBorder(listBorder);
		questionListScroll.setMinimumSize(new Dimension(150, 150));
		questionListScroll.setPreferredSize(new Dimension(150, 150));
		questionListScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

		questionList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		// Configure question fields
		questionPanelRight.setLayout(new GridBagLayout());
		radioPanel.setLayout(answerRadioLayout);
		textPanel.setLayout(answerTextLayout);
		menuPanel.setLayout(answerMenuLayout);

		questionText.setBorder(BorderFactory.createLoweredBevelBorder());
		answerMenu.setOpaque(true);
		answerPanel.setLayout(answerPanelLayout);

		answerTextField.setFont(new java.awt.Font("SansSerif", 0, 14));
		answerTextField.setMaximumSize(new Dimension(72, 64));
		answerTextField.setMinimumSize(new Dimension(72, 16));
		answerTextField.setPreferredSize(new Dimension(72, 16));
		answerTextField.setLineWrap(true);
		answerTextField.setRows(1);
		answerTextField.setTabSize(4);
		answerTextField.setWrapStyleWord(true);

		/* List Layout */
		questionPanelLeft.add(questionListScroll,    new GridBagConstraints(0, 0, 2, 1, 1.0, 1.0
			,GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 29, 302));

		/* Question Layout */
		questionPanelRight.add(titleText,        new GridBagConstraints(0, 1, 4, 1, 0.0, 0.1
			,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(10, 10, 10, 10), 0, 0));
		questionPanelRight.add(questionText,               new GridBagConstraints(0, 0, 4, 1, 1.0, 0.3
			,GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(5, 10, 10, 10), 0, 0));

		/* Radio Panel Answer Layout */
		radioPanel.add(radioAnswerLabel, new GridBagConstraints(0, 0, 1, 1, 0.3, 0.0
			,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 5, 0, 5), 0, 0));

		for (int i = 0; i < MAX_BUTTONS; i++)
		{
			radioPanel.add(answerButtons[i], new GridBagConstraints(1, i, 1, 1, 0.6, 0.2,
				GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
		}

		/* Text Answer Layout */
		textPanel.add(textAnswerLabel, new GridBagConstraints(0, 0, 1, 1, 0.3, 0.0
			,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0, 5, 0, 5), 0, 0));
		textPanel.add(answerTextField,  new GridBagConstraints(1, 0, 1, 1, 1.0, 1.0
			,GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));

		/* Popup Menu Answer Layout */
		menuPanel.add(menuAnswerLabel, new GridBagConstraints(0, 0, 1, 1, 0.3, 0.0
			,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0, 5, 0, 5), 0, 0));
		menuPanel.add(answerMenu,    new GridBagConstraints(1, 0, 1, 1, 1.0, 1.0
			,GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL, new Insets(0, 10, 0, 10), 0, 0));

		/* Misc Button Layout */
		questionPanelRight.add(questionButtonOK,   new GridBagConstraints(0, 11, 1, 1, 0.33, 0.1
			,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(10, 10, 10, 10), 0, 0));
		questionPanelRight.add(questionButtonNone,    new GridBagConstraints(2, 11, 1, 1, 0.33, 0.1
			,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(10, 10, 10, 10), 0, 0));
		questionPanelRight.add(questionButtonCancel,   new GridBagConstraints(3, 11, 1, 1, 0.33, 0.1
			,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(10, 10, 10, 10), 0, 0));
		questionPanelRight.add(answerPanel,  new GridBagConstraints(0, 5, 4, 4, 1.0, 0.5
			,GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(10, 10, 10, 10), 0, 0));
		questionPanelRight.add(allAdjacentCheck,    new GridBagConstraints(0, 10, 4, 1, 0.0, 0.0
			,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(10, 10, 10, 10), 0, 0));


		/* Install event Handlers */
		questionList.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				question_list_selectionChanged(e); }});

		questionButtonOK.addActionListener(new CatchingAction("questionButtonOK") {
			public void safeActionPerformed(ActionEvent e) throws Exception {
				questionButtonOK_actionPerformed(e);}});

		questionButtonNone.addActionListener(new CatchingAction("questionButtonNone") {
			public void safeActionPerformed(ActionEvent e) throws Exception {
				questionButtonNone_actionPerformed(e);}});

		questionButtonCancel.addActionListener(new CatchingAction("questionButtonCancel") {
			public void safeActionPerformed(ActionEvent e) throws Exception {
				questionButtonCancel_actionPerformed(e);}});

		answerButtonListener = new CatchingAction("answerButtonListener") {
			public void safeActionPerformed(ActionEvent e) throws Exception {
				questionAnsweredEventHandler(e);}};

		answerMenu.addActionListener(new CatchingAction("answerMenu") {
			public void safeActionPerformed(ActionEvent e) throws Exception {
				questionAnsweredEventHandler(e); }});

		wholeNumberDocument.addDocumentListener(new DocumentListener() {
			public void insertUpdate(DocumentEvent e) { answerTextEvent(e); }
			public void changedUpdate(DocumentEvent e) { answerTextEvent(e); }
			public void removeUpdate(DocumentEvent e) { answerTextEvent(e); }});

		plainDocument.addDocumentListener(new DocumentListener() {
			public void insertUpdate(DocumentEvent e) { answerTextEvent(e); }
			public void changedUpdate(DocumentEvent e) { answerTextEvent(e); }
			public void removeUpdate(DocumentEvent e) { answerTextEvent(e); }});

		answerButtonListener = new CatchingAction("") {
			public void safeActionPerformed(ActionEvent e) throws Exception {
				questionAnsweredEventHandler(e);}};

		allAdjacentCheck.addActionListener(new CatchingAction(""){
			public void safeActionPerformed(ActionEvent e) throws Exception {
				allAdjacentCheck_actionPerformed(e);}});

		for (int i = 0; i <= MAX_BUTTONS; i++)
		{
			answerButtonGroup.add(answerButtons[i]);
			answerButtons[i].addActionListener(answerButtonListener);
		}

		this.getContentPane().add(questionSplit, null);
	}


	void activate(Question q)
	{
		question        = null;
		linkAnswer      = null;
		baseQuestion    = q;

		// Question vars
		questionList.setModel(new DefaultListModel<Question>());
		egoNet.getStudy().fillList((DefaultListModel<Question>) questionList.getModel(), q.UniqueId);

		// Set Selection
		if (baseQuestion.link.isActive())
		{
			Question selected = egoNet.getStudy().getQuestions().getQuestion(baseQuestion.link.getAnswer().getQuestionId());
			questionList.setSelectedValue(selected, true);
		}

		if ((questionList.getSelectedIndex() == -1) && (questionList.getModel().getSize() > 0))
		{
			questionList.setSelectedIndex(0);
		}

		/* Prepare starting question and answer, fill with stored values */
		question = (Question) questionList.getSelectedValue();

		if (question != null)
		{
			
			linkAnswer = Answer.newInstance(question.answerType);
			linkAnswer.setQuestionId(question.UniqueId);

			if (baseQuestion.link.isActive() && (question.UniqueId.equals(baseQuestion.link.getAnswer().getQuestionId())))
			{
				linkAnswer.setValue(baseQuestion.link.getAnswer().getValue());
				linkAnswer.string  	= baseQuestion.link.getAnswer().string;
				linkAnswer.setAnswered(true);
			}
		}


		// Init
		fillPanel();

		this.setTitle("Set Link for Question \"" + q.title + "\"");
		this.setSize(550, 500);

		//Center the window
		/* Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		Dimension frameSize = this.getSize();
		if (frameSize.height > screenSize.height)
		{
			frameSize.height = screenSize.height;
		}
		if (frameSize.width > screenSize.width)
		{
			frameSize.width = screenSize.width;
		}
		this.setLocation((screenSize.width - frameSize.width) / 2, (screenSize.height - frameSize.height) / 2); */

		this.show();
	}


	/**
	 * Updates right side question fields when the selection changes
	 * @param 	e event generated by selection change.
	 */
	public void question_list_selectionChanged(ListSelectionEvent e)
	{
		if (!e.getValueIsAdjusting() && !questionList.isSelectionEmpty())
		{
			question = (Question) questionList.getSelectedValue();

			if (question != null) {
				linkAnswer = Answer.newInstance(question.answerType);
				linkAnswer.setQuestionId(question.UniqueId);

				if (baseQuestion.link.isActive() && (question.UniqueId.equals(baseQuestion.link.getAnswer().getQuestionId())))
				{
					linkAnswer.setValue(baseQuestion.link.getAnswer().getValue());
					linkAnswer.setIndex(baseQuestion.link.getAnswer().getIndex());
					linkAnswer.string  	= baseQuestion.link.getAnswer().string;
					linkAnswer.setAnswered(true);
				}
			}

			setOKButtonState();
			fillPanel();
		}
	}

	/****
	 * fill List with appropriate questions
	 * Set other fields to selected question
	 */
	public void fillPanel()
	{
		allAdjacentCheck.setVisible(false);

		if (question != null)
		{
			titleText.setText(question.title);

			answerPanel.setVisible(false);
			answerPanel.removeAll();
			questionText.setText("Please select a question and an answer which will trigger the inclusion of the current question");
			if (question instanceof AlterPromptQuestion)
			{
				// Should never be here
				//assert(false);
			}
			else if (question.answerType.equals(TextAnswer.class))
			{
				answerPanel.add(textPanel);
				answerPanel.validate();
				answerTextField.setDocument(plainDocument);
				answerTextField.requestFocus();

				if (linkAnswer.isAnswered())
				{
					answerTextField.setText(linkAnswer.string);
				}
				else
				{
					answerTextField.setText("");
				}
			}
			else if (question.answerType.equals(InformationalAnswer.class))
			{
				answerPanel.add(textPanel);
				answerTextField.setDocument(wholeNumberDocument);
				answerTextField.requestFocus();

				if (linkAnswer.isAnswered())
				{
					answerTextField.setText(linkAnswer.string);
				}
				else
				{
					answerTextField.setText("");
				}
			}
			else if (question.getSelections().size() <= 5)
			{
				allAdjacentCheck.setVisible(question instanceof AlterPairQuestion);
				questionText.setText(question.text);

				answerPanel.add(radioPanel);

				if (linkAnswer.isAnswered())
				{
					if (linkAnswer.getValue() == Answer.ALL_ADJACENT)
					{
						allAdjacentCheck.setSelected(true);
						answerButtons[MAX_BUTTONS].setSelected(true);
					}
					else
					{
						allAdjacentCheck.setSelected(false);
						answerButtons[question.getSelections().size() - (linkAnswer.getValue() + 1)].setSelected(true);
					}
				}
				else
				{
					answerButtons[MAX_BUTTONS].setSelected(true);
				}

				for (int i = 0; i < question.getSelections().size(); i++)
				{
					answerButtons[i].setText(question.getSelections().get(i).getString());
					answerButtons[i].setVisible(true);
					answerButtons[i].setEnabled(linkAnswer.getValue() != Answer.ALL_ADJACENT);
				}

				for (int i = question.getSelections().size(); i < MAX_BUTTONS; i++)
				{
					answerButtons[i].setVisible(false);
				}
			}
			else
			{
				allAdjacentCheck.setVisible(question instanceof AlterPairQuestion);
				questionText.setText(question.text);
				answerPanel.add(menuPanel);

				answerMenu.removeAllItems();

				answerMenu.addItem(new Selection("Select an answer"));
				for (int i = 0; i < question.getSelections().size(); i++)
				{
					answerMenu.addItem(question.getSelections().get(i));
				}

				if (linkAnswer.getValue() == Answer.ALL_ADJACENT)
				{
					allAdjacentCheck.setSelected(true);
					answerMenu.setEnabled(false);
				}
				else if (linkAnswer.getValue() != Answer.NO_ANSWER)
				{
					allAdjacentCheck.setSelected(false);
					answerMenu.setEnabled(true);
					answerMenu.setSelectedIndex(question.getSelections().size() - linkAnswer.getValue());
				}
				else
				{
					allAdjacentCheck.setSelected(false);
					answerMenu.setEnabled(true);
					answerMenu.setSelectedIndex(0);
				}
			}

			answerPanel.validate();
			answerPanel.setVisible(true);
		}
	}

	/****
	 * Clear all on screen editable fields
	 * Generally called when a new survey is started
	 */
	public void clearPanel()
	{
		((DefaultListModel) questionList.getModel()).removeAllElements();
	}

	/****
	 * Parses answer fields to retrieve answer to question
	 * @param answer Answer from interview to fill with correct values
	 */
	void fillAnswer()
	{
		linkAnswer.string = null;

		if(question.answerType.equals(NumericalAnswer.class)) {
			if (answerTextField.getText().length() > 0)
			{
				linkAnswer.string   	= answerTextField.getText();
				linkAnswer.setValue(Integer.valueOf(linkAnswer.string).intValue());
				linkAnswer.setAnswered(true);
			}
			else
			{
				linkAnswer.setValue(Answer.NO_ANSWER);
				linkAnswer.setAnswered(false);
			}
		} else if(question.answerType.equals(TextAnswer.class)) {
			linkAnswer.string   		= answerTextField.getText();
			linkAnswer.setValue(linkAnswer.string.length());
			linkAnswer.setAnswered((linkAnswer.getValue() != 0));
		} else if(question.answerType.equals(InformationalAnswer.class)) {
			linkAnswer.string = "informational";
			linkAnswer.setValue(1);
			linkAnswer.setAnswered(true);
		} else if(question.answerType.equals(CategoricalAnswer.class)) {
			
			// option/radio buttons
			if (question.getSelections().size() <= 5) {
				if (allAdjacentCheck.isSelected()) {
					linkAnswer.setValue(Answer.ALL_ADJACENT);
					linkAnswer.string   		= "All Adjacent";
					linkAnswer.setAnswered(true);
				}
				else {
					int button          		= selectedButtonIndex(answerButtons);
					linkAnswer.setAnswered( (button != MAX_BUTTONS));
					
					if (linkAnswer.isAnswered())
					{
						linkAnswer.setValue(question.getSelections().get(button).getValue());
						linkAnswer.setIndex(question.getSelections().get(button).getIndex());
						linkAnswer.string   	= answerButtons[button].getActionCommand();
					}
					else
					{
						linkAnswer.setValue(Answer.NO_ANSWER);
						linkAnswer.setIndex(Answer.NO_ANSWER);
						linkAnswer.string   	= "";
					}
				}
			}
			
			// drop down buttons
			else {
				if (allAdjacentCheck.isSelected())
				{
					linkAnswer.setValue(Answer.ALL_ADJACENT);
					linkAnswer.string   		= "All Adjacent";
					linkAnswer.setAnswered(true);
				}
				else if (answerMenu.getSelectedIndex() > 0) // 0th option is "Select an answer"
				{
					int selectionIndex = answerMenu.getSelectedIndex() - 1;
					linkAnswer.setValue(question.getSelections().get(selectionIndex).getValue());
					linkAnswer.string   		= answerMenu.getSelectedItem().toString();
					linkAnswer.setAnswered((selectionIndex <= question.getSelections().size()));
				}
				else
				{
					linkAnswer.setValue(Answer.NO_ANSWER);
					linkAnswer.string   		= "";
					linkAnswer.setAnswered(false);
				}
			}
		}
	}
	

	void jShowListButton_actionPerformed(ActionEvent e)
	{
		questionPanelLeft.setVisible(!questionPanelLeft.isVisible());
		questionSplit.setDividerLocation(.33);
		questionSplit.repaint();
	}

	void questionButtonNone_actionPerformed(ActionEvent e) throws IOException
	{
		if (egoNet.getStudy().confirmIncompatibleChange(egoNet.getFrame()))
		{
			baseQuestion.link.setAnswer(null);
			egoNet.getStudy().setModified(true);
			egoNet.getStudy().setCompatible(false);
		}
		
		egoNet.getFrame().fillCurrentPanel();
		this.hide();
	 }

	void questionButtonCancel_actionPerformed(ActionEvent e)
	{
		this.hide();
	}

	void questionButtonOK_actionPerformed(ActionEvent e) throws IOException
	{
		if ((linkAnswer != null) && (linkAnswer.isAnswered()) && egoNet.getStudy().confirmIncompatibleChange(egoNet.getFrame()))
		{
			baseQuestion.link.setAnswer(linkAnswer);
			egoNet.getStudy().setModified(true);
			egoNet.getStudy().setCompatible(false);
		}

		this.hide();
		egoNet.getFrame().fillCurrentPanel();
	}

	int selectedButtonIndex(JRadioButton[] group)
	{
		int ri = -1;

		for (int i = 0; i < group.length; i++)
		{
			if (group[i].isSelected())
			{
				ri = i;
				break;
			}
		}

		return (ri);
	}

	void setOKButtonState()
	{
		questionButtonOK.setEnabled((question != null) && linkAnswer.isAnswered());
	}


	void questionAnsweredEventHandler(ActionEvent e)
	{
		if (e.getActionCommand() != "Initialization")
		{
			fillAnswer();
			setOKButtonState();
		}
	}

	void answerTextEvent(DocumentEvent e)
	{
		fillAnswer();
		setOKButtonState();
	}

	public void update(Observable o, Object arg)
	{
		fillAnswer();
		setOKButtonState();
	}

	void allAdjacentCheck_actionPerformed(ActionEvent e)
	{
		fillAnswer();
		fillPanel();
		setOKButtonState();
	}
}