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
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.Arrays;
import java.util.HashSet;
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

import com.cim.dlgedit.loader.DialogResource;
import com.endlessloopsoftware.ego.Answer;
import com.endlessloopsoftware.ego.Question;
import com.endlessloopsoftware.egonet.Shared;
import com.endlessloopsoftware.elsutils.documents.WholeNumberDocument;
import com.endlessloopsoftware.elsutils.files.FileCreateException;
import com.endlessloopsoftware.elsutils.layout.CardPanel;
import org.egonet.util.listbuilder.ListBuilder;
import org.egonet.util.listbuilder.Selection;
import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

//import java.text.*;
import java.util.Date;

/**
 * Generic Panel creation and handling routines for question editing
 */
public class ClientQuestionPanel extends JPanel implements Observer {
	/* Lists */
	private final JRadioButton[] answerButtons = { 
			new JRadioButton(), // 1
			new JRadioButton(), // 2
			new JRadioButton(), // 3
			new JRadioButton(), // 4
			new JRadioButton(), // 5
			new JRadioButton(), // 6
			new JRadioButton(), // 7
			new JRadioButton(), // 8
			new JRadioButton(), // 9
	};

	private final KeyStroke enter = KeyStroke
	.getKeyStroke(KeyEvent.VK_ENTER, 0);

	private final KeyStroke[] numKey = {
			KeyStroke.getKeyStroke(KeyEvent.VK_0, 0),
			KeyStroke.getKeyStroke(KeyEvent.VK_1, 0),
			KeyStroke.getKeyStroke(KeyEvent.VK_2, 0),
			KeyStroke.getKeyStroke(KeyEvent.VK_3, 0),
			KeyStroke.getKeyStroke(KeyEvent.VK_4, 0),
			KeyStroke.getKeyStroke(KeyEvent.VK_5, 0),
			KeyStroke.getKeyStroke(KeyEvent.VK_6, 0),
			KeyStroke.getKeyStroke(KeyEvent.VK_7, 0),
			KeyStroke.getKeyStroke(KeyEvent.VK_8, 0),
			KeyStroke.getKeyStroke(KeyEvent.VK_9, 0) };

	private final ActionListener keyActionListener = new java.awt.event.ActionListener() {
		public void actionPerformed(ActionEvent e) {
			numberKey_actionPerformed(e);
		}
	};

	private final String ALTER_CARD = "ALTER";

	private final String TEXT_CARD = "TEXT";

	private final String NUMERICAL_CARD = "NUMERICAL";

	private final String RADIO_CARD = "RADIO";

	private final String MENU_CARD = "MENU";

	/* UI Elements */
	private final Border listBorder;

	private JLabel titleText = new JLabel();

	private JTextArea questionText = new JTextArea();

	private final JTextArea answerTextField = new NoTabTextArea("answerTextField");

	private final JTextArea numericalTextField = new NoTabTextArea("numericalTextField");

	private JButton questionButtonPrevious;

	private JButton questionButtonNext;

	private JProgressBar questionProgress;

	private final JComboBox answerMenu = new JComboBox();

	private final ListBuilder alterList = new ListBuilder();

	private final JCheckBox noAnswerBox = new JCheckBox("Don't Know");

	/* Containers */
	private final JSplitPane questionSplit = new JSplitPane();

	private final JList questionList = new JList();

	private CardPanel answerPanel = new CardPanel();

	private final JPanel radioPanel = new RadioPanel();

	private final JPanel menuPanel = new MenuAnswerPanel();

	private final JPanel textPanel = new TextAnswerPanel();

	private final JPanel numericalPanel = new NumericalAnswerPanel();

	private final JPanel questionPanelLeft;

	private final JPanel questionPanelRight;

	private final ButtonGroup answerButtonGroup = new ButtonGroup();

	private ActionListener answerButtonListener;

	private final WholeNumberDocument wholeNumberDocument = new WholeNumberDocument();

	private final PlainDocument plainDocument = new PlainDocument();

	/* Question Iteration Variables */
	private Question question;

	private final EgoClient egoClient;

	/**
	 * Generates Panel for question editing to insert in file tab window
	 * 
	 * @param parent
	 *            parent frame for referencing composed objects
	 */
	public ClientQuestionPanel(EgoClient egoClient) {
		this.egoClient = egoClient;
		listBorder = BorderFactory.createCompoundBorder(new TitledBorder(
				new EtchedBorder(EtchedBorder.RAISED, Color.white, new Color(
						178, 178, 178)), "Questions"), BorderFactory
						.createEmptyBorder(10, 10, 10, 10));

		questionPanelLeft = getLeftPanel();
		questionPanelRight = getRightPanel();

		jbInit();
	}

	/**
	 * Component initialization
	 * 
	 * @throws Exception
	 */
	private void jbInit() {
		// Question vars
		question = egoClient.getInterview().setInterviewIndex(egoClient.getInterview()
				.getFirstUnansweredQuestion(), false);

		this.setMinimumSize(new Dimension(330, 330));
		this.setLayout(new GridLayout());

		// Configure Split Frame
		questionSplit.add(questionPanelLeft, JSplitPane.LEFT);
		questionSplit.add(questionPanelRight, JSplitPane.RIGHT);

		questionSplit.setOneTouchExpandable(false);
		questionSplit.setDividerLocation(0.33);

		// Provide minimum sizes for the two components in the split pane
		Dimension minimumSize = new Dimension(100, 100);
		questionPanelLeft.setMinimumSize(minimumSize);
		questionPanelRight.setMinimumSize(minimumSize);

		/* Install event Handlers */
		questionList.getSelectionModel().addListSelectionListener(
				new ListSelectionListener() {
					public void valueChanged(ListSelectionEvent e) {
						question_list_selectionChanged(e);
					}
				});

		questionButtonPrevious
		.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(ActionEvent e) {
				questionButtonPrevious_actionPerformed(e);
			}
		});

		questionButtonNext
		.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(ActionEvent e) {
				questionButtonNext_actionPerformed(e);
			}
		});

		answerButtonListener = new java.awt.event.ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.out.println("answerButtonListener: " + e);
				questionAnsweredEventHandler(e);
			}
		};

		answerMenu.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.out.println("answerMenu.addActionListener: " + e);
				questionAnsweredEventHandler(e);
			}
		});

		wholeNumberDocument.addDocumentListener(new DocumentListener() {
			public void insertUpdate(DocumentEvent e) {
				answerTextEvent(e);
			}

			public void changedUpdate(DocumentEvent e) {
				answerTextEvent(e);
			}

			public void removeUpdate(DocumentEvent e) {
				answerTextEvent(e);
			}
		});

		plainDocument.addDocumentListener(new DocumentListener() {
			public void insertUpdate(DocumentEvent e) {
				answerTextEvent(e);
			}

			public void changedUpdate(DocumentEvent e) {
				answerTextEvent(e);
			}

			public void removeUpdate(DocumentEvent e) {
				answerTextEvent(e);
			}
		});

		noAnswerBox.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(ActionEvent e) {
				noAnswerBox_actionPerformed(e);
			}
		});

		for (int i = 0; i < answerButtons.length; i++) {
			answerButtonGroup.add(answerButtons[i]);
			answerButtons[i].addActionListener(answerButtonListener);
		}

		// register all number buttons instead of till MAX_BUTTONS alone
		// for (int i = 0; i <= MAX_BUTTONS; i++)
		for (int i = 0; i <= 9; i++) {
			this.registerKeyboardAction(keyActionListener, Integer
					.toString(i + 1), numKey[i],
					JComponent.WHEN_IN_FOCUSED_WINDOW);
		}
		this.registerKeyboardAction(keyActionListener, Integer.toString(0),
				enter, JComponent.WHEN_IN_FOCUSED_WINDOW);
		numericalTextField.registerKeyboardAction(keyActionListener, Integer
				.toString(0), enter, JComponent.WHEN_FOCUSED);
		answerTextField.registerKeyboardAction(keyActionListener, Integer
				.toString(0), enter, JComponent.WHEN_FOCUSED);
		answerMenu.registerKeyboardAction(keyActionListener, Integer
				.toString(0), enter, JComponent.WHEN_FOCUSED);

		alterList.addListObserver(this);

		questionProgress.setMaximum(egoClient.getInterview().getNumQuestions());
		questionProgress.setValue(egoClient.getInterview().getQuestionIndex());
		questionProgress.setStringPainted(true);

		questionList.setModel(new DefaultListModel());
		egoClient.getInterview()
		.fillList((DefaultListModel) questionList.getModel());

		if (egoClient.getUiPath() == ClientFrame.VIEW_INTERVIEW)
			questionList.setSelectedIndex(0);

		this.add(questionSplit, null);

		/* Differences in UI in conducting interview vs. viewing it */
		questionPanelLeft
		.setVisible(egoClient.getUiPath() == ClientFrame.VIEW_INTERVIEW);

		fillPanel();
	}

	private JPanel getLeftPanel() {
		JPanel panel = new JPanel();
		panel.setLayout(new GridBagLayout());

		// Configure List
		JScrollPane questionListScroll = new JScrollPane(questionList);
		questionListScroll.setBorder(listBorder);
		questionListScroll.setMinimumSize(new Dimension(150, 150));
//		questionListScroll
//		.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		questionListScroll.setAutoscrolls(true);

		questionList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		/* List Layout */
		panel.add(questionListScroll, new GridBagConstraints(0, 0, 2, 1, 1.0,
				1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
				new Insets(0, 0, 0, 0), 29, 302));

		return panel;
	}

	private JPanel getRightPanel() {
		// Load up the dialog contents.
		JPanel panel = DialogResource
		.load("com/endlessloopsoftware/ego/client/QuestionPanel.gui_xml");

		// Attach beans to fields.
		questionButtonPrevious = (JButton) DialogResource.getComponentByName(panel, "questionButtonPrevious");
		questionButtonNext = (JButton) DialogResource.getComponentByName(panel,	"questionButtonNext");
		titleText = (JLabel) DialogResource.getComponentByName(panel, "titleText");
		answerPanel = (CardPanel) DialogResource.getComponentByName(panel, "answerPanel");
		questionProgress = (JProgressBar) DialogResource.getComponentByName( panel, "questionProgress");
		questionText = (JTextArea) DialogResource.getComponentByName(panel, "questionText");

		/* Set up answer panel cards */
		answerPanel.add(new JScrollPane(alterList), ALTER_CARD);
		answerPanel.add(new JScrollPane(textPanel), TEXT_CARD);
		answerPanel.add(new JScrollPane(numericalPanel), NUMERICAL_CARD);
		answerPanel.add(new JScrollPane(radioPanel), RADIO_CARD);
		answerPanel.add(new JScrollPane(menuPanel), MENU_CARD);

		titleText.setFont(new java.awt.Font("Lucida Grande Bold", 0, 15));

		questionText.setBackground(SystemColor.window);
		questionText.setFont(new java.awt.Font("Serif", 0, 16));
		questionText.setLineWrap(true);
		questionText.setTabSize(4);
		questionText.setWrapStyleWord(true);
		questionText.setEditable(false);
		questionText.setMargin(new Insets(4, 4, 4, 4));
		questionText.setBorder(BorderFactory.createLoweredBevelBorder());

		answerMenu.setOpaque(true);

		answerTextField.setFont(new java.awt.Font("SansSerif", 0, 14));
		answerTextField.setLineWrap(true);
		answerTextField.setRows(1);
		answerTextField.setTabSize(4);
		answerTextField.setWrapStyleWord(true);

		numericalTextField.setFont(new java.awt.Font("SansSerif", 0, 14));
		numericalTextField.setLineWrap(true);
		numericalTextField.setRows(1);
		numericalTextField.setTabSize(4);
		numericalTextField.setWrapStyleWord(false);

		return panel;
	}

	/**
	 * Updates right side question fields when the selection changes
	 * 
	 * @param e
	 *            event generated by selection change.
	 */
	public void question_list_selectionChanged(ListSelectionEvent e) {
		question = egoClient.getInterview().setInterviewIndex(questionList
				.getSelectedIndex(), true);
		fillPanel();
	}

	/***************************************************************************
	 * fill List with appropriate questions Set other fields to selected
	 * question
	 */
	public void fillPanel() {
		if (question == null)
			return; // danger will robinson!


		// we may need some string substitutions for $$1 and $$2
		String[] alterNames = egoClient.getInterview().getAlterStrings(question);

		// prepare the buttons
		setButtonNextState();
		questionButtonPrevious.setEnabled(egoClient.getInterview().hasPrevious());

		switch (question.questionType) {
		case Question.EGO_QUESTION:
			titleText.setText("Questions About You");
			break;

		case Question.ALTER_PROMPT:
			titleText.setText("Whom do you know?");
			break;

		case Question.ALTER_QUESTION:
			titleText.setText("<html><p>Questions About <nobr><b>"
					+ alterNames[0] + "</b></nobr></p></html>");
			break;

		case Question.ALTER_PAIR_QUESTION:
			titleText.setText("<html><p>Questions About <nobr><b>"
					+ alterNames[0] + "</b></nobr> and <nobr><b>"
					+ alterNames[1] + "</b></nobr></p></html>");
			break;
		}

		answerPanel.setVisible(false);
		if ((question.questionType == Question.ALTER_PROMPT)
				&& egoClient.getStudy().getUIType().equals(
						Shared.TRADITIONAL_QUESTIONS)) {
			String qs = "Enter the names of "
				+ egoClient.getStudy().getNumAlters() + " people. ";

			if (egoClient.getInterview().isLastAlterPrompt()) {
				qs += "After entering " + egoClient.getStudy().getNumAlters()
				+ " names you can continue.";
			} else {
				qs += "";
			}

			questionText.setText(question.text);

			answerPanel.showCard(ALTER_CARD);
			alterList.setMaxListSize(egoClient.getStudy().getNumAlters());
			alterList.setDescription(qs);
			alterList.setElementName("Name: ");
			alterList.setPresetListsActive(false);
			alterList.setNameList(true);
			alterList.setTitle("Your Acquaintances");

			// set alter Strings IF they exist yet
			alterList.setListStrings(egoClient.getInterview().getAlterList());
			alterList.setEditable(egoClient.getUiPath() == ClientFrame.DO_INTERVIEW);

			egoClient.getFrame().flood();
			answerPanel.setVisible(true);
			alterList.requestFocusOnFirstVisibleComponent();
		} else if (question.answerType == Question.TEXT) {
			questionText.setText(question.text);

			answerPanel.showCard(TEXT_CARD);
			answerPanel.validate();
			answerTextField.setDocument(plainDocument);
			answerTextField.requestFocus();

			if (question.answer.answered) {
				answerTextField.setText(question.answer.string);
			} else {
				answerTextField.setText("");
			}

			answerTextField
			.setEditable((egoClient.getUiPath() == ClientFrame.DO_INTERVIEW)
					|| (egoClient.getUiPath() == ClientFrame.VIEW_INTERVIEW));
			answerPanel.setVisible(true);
			answerTextField.requestFocusInWindow();
		} else if (question.answerType == Question.NUMERICAL) {
			questionText.setText(question.text);

			answerPanel.showCard(NUMERICAL_CARD);
			numericalTextField.setDocument(wholeNumberDocument);
			numericalTextField.requestFocusInWindow();

			if (question.answer.answered) {
				if (question.answer.getValue() != -1) {
					numericalTextField.setText(question.answer.string);
					noAnswerBox.setSelected(false);
				} else {
					noAnswerBox.setSelected(true);
					numericalTextField.setText("");
				}
			} else {
				numericalTextField.setText("");
				noAnswerBox.setSelected(false);
			}

			boolean doInterview = (egoClient.getUiPath() == ClientFrame.DO_INTERVIEW);
			numericalTextField.setEditable(doInterview
					|| (egoClient.getUiPath() == ClientFrame.VIEW_INTERVIEW));
			noAnswerBox.setEnabled(doInterview
					|| (egoClient.getUiPath() == ClientFrame.VIEW_INTERVIEW));
			answerPanel.setVisible(true);
			numericalTextField.requestFocusInWindow();
		} else if(question.answerType == Question.CATEGORICAL) {

			System.out.println("Displaying CATEGORICAL question: " + question.text);
			questionText.setText(question.text);
			
			// can we do radio buttons or do we need the dropdown?
			if (question.getSelections().length <= answerButtons.length) { // radio buttons!
				System.out.println(" -- doing radio buttons!");
				
				System.out.println("-- NO -- unsetting the buttons!");
				for(int i = 0; i < answerButtons.length; i++)
				{
					answerButtons[i].setActionCommand("Initialization");
					answerButtons[i].setSelected(false);
					answerButtons[i].setVisible(false);
					answerButtons[i].setActionCommand("User Input");
				}
				
				answerPanel.showCard(RADIO_CARD);

				for (int i = 0; i < question.getSelections().length && i < answerButtons.length; i++) {
					answerButtons[i].setActionCommand("Initialization");
					answerButtons[i].setText("(" + question.getSelections()[i].getValue() + ") " + question.getSelections()[i].getString());
					answerButtons[i].setVisible(true);
					answerButtons[i].setEnabled((egoClient.getUiPath() == ClientFrame.DO_INTERVIEW) || (egoClient.getUiPath() == ClientFrame.VIEW_INTERVIEW));
					answerButtons[i].setActionCommand("User Input");
				}
				
				System.out.println("-- answer: " + question.answer.answered + ", answer index: " + question.answer.getIndex());
				if (question.answer.answered && (question.answer.getIndex() >= -1)) {
					System.out.println(" -- was it actually answered with index >= -1 (not unselected)");
					//int idx = question.selections.length - (question.answer.getValue() + 1);
					if (answerButtons != null && answerButtons.length != 0) {
						answerButtons[question.answer.getIndex()].setSelected(true);
						System.out.println("-- YES -- setting a selection for the new loaded question's answer!");
					}
				}

				answerPanel.setVisible(true);
			} else { // drop downs!
				System.out.println(" -- doing drop downs!");
				questionText.setText(question.text);
				answerPanel.showCard(MENU_CARD);

				answerMenu.setActionCommand("Initialization"); // suspend the answer listener while we mess w/ the box
				answerMenu.removeAllItems();
				answerMenu.addItem("Select an answer");
				
				for (int i = 0; i < question.getSelections().length; i++) {
					answerMenu.addItem(question.getSelections()[i]);
				}

				if (question.answer.getIndex() > 0) {
					answerMenu.setSelectedIndex(question.answer.getIndex()-1);
				}
				answerMenu.setActionCommand("User Input"); // reactive the answer listener since we're done
				answerMenu.setEnabled((egoClient.getUiPath() == ClientFrame.DO_INTERVIEW) || (egoClient.getUiPath() == ClientFrame.VIEW_INTERVIEW));
			
				answerPanel.setVisible(true);
				answerMenu.requestFocusInWindow();
			}
			
			
			
			
		}
	}

	/***************************************************************************
	 * Clear all on screen editable fields Generally called when a new survey is
	 * started
	 */
	public void clearPanel() {
		((DefaultListModel) questionList.getModel()).removeAllElements();
	}

	/**
	 * Figure out the question type and the answer type, and store the
	 * appropriate data. Most controls that could provide selection of a
	 * particular answer have a listener that calls this method.
	 */
	private void fillAnswer(Answer answer) {
		// Don't touch values if we are just viewing interview
		// if (egoClient.getUiPath() == ClientFrame.VIEW_INTERVIEW)
		// return;
		
		System.out.println("fillAnswer called for " + answer.getString());

		if (question.questionType == Question.ALTER_PROMPT) {
			answer.string = "Egonet - University of Florida";
			answer.setValue((alterList.getListStrings().length));
			answer.answered = (!egoClient.getInterview().isLastAlterPrompt() || (answer.getValue() >= egoClient.getStudy()
					.getNumAlters()));
			egoClient.getInterview().setAlterList(alterList.getListStrings());
		} else {
			switch (question.answerType) {
			case Question.NUMERICAL:
				if (noAnswerBox.isSelected()
						|| (numericalTextField.getText().length() > 0)) {
					answer.timestamp = generateTimeStamp();
					//System.out.println("Timestamp: " + answer.timestamp);
					if (noAnswerBox.isSelected()) {
						answer.answered = true;
						answer.setValue((Answer.NO_ANSWER));
						answer.string = "No Answer";
					} else {
						answer.string = numericalTextField.getText();
						answer.setValue((Integer.valueOf(answer.string).intValue()));
						answer.answered = true;
					}
				} else {
					answer.setValue((Answer.NO_ANSWER));
					answer.answered = false;
				}
				break;

			case Question.TEXT:
				answer.timestamp = generateTimeStamp();
				//System.out.println("Timestamp: " + answer.timestamp);
				answer.string = answerTextField.getText();
				answer.setValue((answer.string.length()));
				answer.answered = (answer.getValue() != 0);
				break;

			case Question.CATEGORICAL:
				if (question.getSelections().length <= answerButtons.length) {
					int buttonIndex = selectedButtonIndex(answerButtons);
					answer.answered = (buttonIndex != -1);

					if (answer.answered) {
						answer.timestamp = generateTimeStamp();
						
						int selectionIndex = buttonIndex;
						System.out.println("Selected button (index: " + buttonIndex + ") with selections ("+Arrays.asList(question.getSelections())+") of size " + question.getSelections().length);
						answer.setValue((question.getSelections()[selectionIndex].getValue()));
						// added 11/27/2007
						answer.setIndex(question.getSelections()[selectionIndex].getIndex());
						// answer.index = question.selections.length - question.selections[button].getIndex();
						answer.adjacent = question.getSelections()[selectionIndex].isAdjacent();
						answer.string = question.getSelections()[selectionIndex].getString();
						//System.out.println("Timestamp: " + answer.timestamp + ", answer = " + answer.getString());
						// answer.timestamp = DateFormat.getDateInstance().format(new Date());
					}
				} else {
					// minus 1 because there's a "Please select an answer" option for 
					int selectionIndex = answerMenu.getSelectedIndex() - 1;
					answer.answered = (selectionIndex > 0) && (selectionIndex < question.getSelections().length+1);

					if (answer.answered) {
						answer.timestamp = generateTimeStamp();
						//System.out.println("Timestamp: " + answer.timestamp);

						answer.setValue((question.getSelections()[selectionIndex]
						                                          .getValue()));

						// added 11/27/2007
						answer.setIndex(question.getSelections()[selectionIndex]
						                                         .getIndex());

						answer.adjacent = question.getSelections()[selectionIndex]
						                                           .isAdjacent();
						answer.string = question.getSelections()[selectionIndex]
						                                         .getString();
						// answer.timestamp =
						// DateFormat.getDateInstance().format(new Date());
					}
				}
				break;
			}
		}
	}

	// Generate a time stamp of the form mmddyyyyhhmmss
	public String generateTimeStamp() {
		Date tempdate = new Date();
		String month = String.valueOf(tempdate.getMonth());
		month = month.length() < 2 ? "0" + month : month;
		String date = String.valueOf(tempdate.getDate());
		date = date.length() < 2 ? "0" + date : date;
		String hours = String.valueOf(tempdate.getHours());
		hours = hours.length() < 2 ? "0" + hours : hours;
		String minutes = String.valueOf(tempdate.getMinutes());
		minutes = minutes.length() < 2 ? "0" + minutes : minutes;
		String seconds = String.valueOf(tempdate.getSeconds());
		seconds = seconds.length() < 2 ? "0" + seconds : seconds;

		String timestamp = month + " " + date + " "
		+ String.valueOf(tempdate.getYear() + 1900) + " " + hours + " "
		+ minutes + " " + seconds;
		return timestamp;
	}

	private void numberKey_actionPerformed(ActionEvent e) {
		int key = Integer.parseInt(e.getActionCommand()) - 1;
		System.out.println("Key pressed " + key);
		if (key == -1 && questionButtonNext.isEnabled())
			questionButtonNext_actionPerformed(e);

		if (question.answerType == Question.CATEGORICAL) {
			for (Selection sel : question.getSelections()) {
				// int val = question.selections[i].value;
				// System.out.println("Selection value :" +sel.getValue());
				if (key == sel.getValue()) {
					// answerButtons[key - 1].setSelected(true);

					answerButtons[sel.getIndex()].setSelected(true);
					System.out.println("Number pressed w/ categorical question: numberKey_actionPerformed");
					questionAnsweredEventHandler(e);
					break;
				}

			}
		}
	}

	private void questionButtonNext_actionPerformed(ActionEvent e) {
		if (egoClient.getInterview().hasNext()) {
			question = egoClient.getInterview().next();

			if ((egoClient.getUiPath() == ClientFrame.DO_INTERVIEW)
					&& (question.questionType == Question.ALTER_PAIR_QUESTION)) {
				setDefaultAnswer();
			}

			fillPanel();

			if (egoClient.getUiPath() == ClientFrame.VIEW_INTERVIEW) {
				questionList.setSelectedIndex(egoClient.getInterview()
						.getQuestionIndex());
			}
		} else {
			try {
				egoClient.getInterview().completeInterview();
			} catch (FileCreateException ex) {
				JOptionPane.showMessageDialog(egoClient.getFrame(),
						"Unable to create interview statistics summary file.",
						"Statistics Error", JOptionPane.WARNING_MESSAGE);
			}

			JOptionPane.showMessageDialog(egoClient.getFrame(),
					"You have completed this interview.", "Interview Complete",
					JOptionPane.INFORMATION_MESSAGE);

			/* Return to first screen */
			egoClient.getFrame().gotoSourceSelectPanel(false);
		}

		questionProgress.setValue(egoClient.getInterview().getQuestionIndex());
	}

	private void questionButtonPrevious_actionPerformed(ActionEvent e) {
		if (egoClient.getInterview().hasPrevious()) {
			question = egoClient.getInterview().previous();
			fillPanel();

			if (egoClient.getUiPath() == ClientFrame.VIEW_INTERVIEW) {
				questionList.setSelectedIndex(egoClient.getInterview()
						.getQuestionIndex());
			}
		}

		questionProgress.setValue(egoClient.getInterview().getQuestionIndex());
	}

	private static int selectedButtonIndex(JRadioButton[] button) {
		for (int i = 0; i < button.length; i++) {
			if (button[i].isSelected()) {
				return i;
			}
		}

		return -1;
	}

	private void setButtonNextState() {
		if (question.questionType == Question.ALTER_PROMPT) {
			questionButtonNext.setEnabled(question.answer.answered);
			questionButtonNext.setText("Next Question");
		} else {
			boolean next = egoClient.getInterview().hasNext();

			if (next == false) {
				questionButtonNext.setText("Study Complete");
				questionButtonNext
				.setEnabled((egoClient.getUiPath() == ClientFrame.DO_INTERVIEW)
						&& question.answer.answered);
			} else {
				questionButtonNext.setText("Next Question");
				questionButtonNext.setEnabled(question.answer.answered);
			}
		}
	}

	private void questionAnsweredEventHandler(ActionEvent e) {
		System.out.println("questionAnsweredEventHandler");
		if (e.getActionCommand() != "Initialization") {
			fillAnswer(question.answer);
			setButtonNextState();
		}
	}

	private void answerTextEvent(DocumentEvent e) {
		System.out.println("answerTextEvent");
		fillAnswer(question.answer);
		setButtonNextState();
	}

	public void update(Observable o, Object arg) {
		System.out.println("update");
		fillAnswer(question.answer);
		setButtonNextState();
	}

	private void noAnswerBox_actionPerformed(ActionEvent e) {
		System.out.println("noAnswerBox_actionPerformed");
		if (noAnswerBox.isSelected()) {
			numericalTextField.setText("");
		}
		fillAnswer(question.answer);
		setButtonNextState();
	}

	private void setDefaultAnswer() {
		if (!question.answer.answered
				&& (question.answerType == Question.CATEGORICAL)
				&& (question.answer.getAlters()[1] > (question.answer
						.getAlters()[0] + 1))) {
			int defaultAnswer = -1;
			if (!question.getSelections()[question.getSelections().length - 1]
			                              .isAdjacent()) {
				defaultAnswer = question.getSelections().length - 1;
			} else {
				for (int i = 0; i < question.getSelections().length; i++) {
					if (!question.getSelections()[i].isAdjacent()) {
						defaultAnswer = i;
						break;
					}
				}
			}

			if ((defaultAnswer >= 0)
					&& (defaultAnswer < question.getSelections().length)) {
				question.answer.setValue((question.getSelections()[defaultAnswer].getValue()));
				question.answer.setIndex(question.getSelections()[defaultAnswer].getIndex());

				question.answer.string = question.getSelections()[defaultAnswer]
				                                                  .getString();
				question.answer.adjacent = false;
				question.answer.answered = true;
			}
		}
	}

	/**
	 * Radio Panel
	 */
	class RadioPanel extends JPanel {
		public RadioPanel() {
			FormLayout layout = new FormLayout("r:p, 4dlu, max(250dlu;p):g",
			"t:p, 4dlu, d, 4dlu, d, 4dlu, d, 4dlu, d, 4dlu, d, 4dlu, d, 4dlu, d, 4dlu, d,  4dlu, d");

			layout.setRowGroups(new int[][] { { 1, 3, 5, 7, 9, 11, 13, 15, 17 } });

			PanelBuilder builder = new PanelBuilder(layout);

			builder.addLabel("List-item Answer:");
			builder.nextColumn(2);
			builder.add(answerButtons[0]);

			for (int i = 0; i < answerButtons.length; i++) {
				builder.nextLine(2);
				builder.nextColumn(2);
				builder.add(answerButtons[i]);
			}

			CellConstraints cc = new CellConstraints();
			this.setLayout(new FormLayout("f:p", "t:p"));
			this.add(builder.getPanel(), cc.xy(1, 1));
		}
	}

	/**
	 * Single Field Panel
	 */
	class TextAnswerPanel extends JPanel {
		public TextAnswerPanel() {
			CellConstraints cc = new CellConstraints();
			FormLayout layout = new FormLayout("r:p, 4dlu, max(250dlu;p):g",
			"f:50dlu");

			PanelBuilder builder = new PanelBuilder(layout);

			builder.addLabel("Textual Answer:", cc.xy(1, 1, "right, top"));
			builder.nextColumn(2);
			builder.add(answerTextField);

			this.setLayout(new FormLayout("f:p", "t:p"));
			this.add(builder.getPanel(), cc.xy(1, 1));
		}
	}

	/**
	 * Single Field Panel
	 */
	class MenuAnswerPanel extends JPanel {
		public MenuAnswerPanel() {
			CellConstraints cc = new CellConstraints();
			FormLayout layout = new FormLayout("r:p, 4dlu, max(250dlu;p):g",
			"t:d:g");

			PanelBuilder builder = new PanelBuilder(layout);

			builder.addLabel("Menu Answer:", cc.xy(1, 1, "right, top"));
			builder.nextColumn(2);
			builder.add(answerMenu);

			this.setLayout(new FormLayout("f:p", "t:p"));
			this.add(builder.getPanel(), cc.xy(1, 1));
		}
	}

	/**
	 * Numerical Panel
	 */
	class NumericalAnswerPanel extends JPanel {
		public NumericalAnswerPanel() {
			CellConstraints cc = new CellConstraints();
			FormLayout layout = new FormLayout("r:p, 4dlu, max(250dlu;p):g",
			"f:d, 4dlu, d");

			PanelBuilder builder = new PanelBuilder(layout);

			builder.addLabel("Numerical Answer:", cc.xy(1, 1, "right, top"));
			builder.nextColumn(2);
			builder.add(numericalTextField);
			builder.nextLine(2);
			builder.nextColumn(2);
			builder.add(noAnswerBox);

			this.setLayout(new FormLayout("f:p", "t:p"));
			this.add(builder.getPanel(), cc.xy(1, 1));
		}
	}

}

/**
 * Extends JPanel class to keep focus in right panel of split question panel
 */
class RightPanel extends JPanel {
	public boolean isFocusCycleRoot() {
		return (true);
	}
}

/**
 * Extends JTextArea to make tabs focus change events in question text areas
 */
class NoTabTextArea extends JTextArea {
	public NoTabTextArea(String name) {
		super();
		super.setName(name);
		setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS, new HashSet<AWTKeyStroke>());
		setFocusTraversalKeys(KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS, new HashSet<AWTKeyStroke>());
	}
}