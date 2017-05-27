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
import java.awt.event.ItemEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.egonet.exceptions.DuplicateQuestionException;
import org.egonet.gui.interview.ClientQuestionPanel;
import org.egonet.model.Question;
import org.egonet.model.Selection;
import org.egonet.model.Shared;
import org.egonet.model.Shared.AnswerType;
import org.egonet.model.Shared.QuestionType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Generic Panel creation and handling routines for question editing
 */
public class AuthoringQuestionPanel extends EgoQPanel
{
	final private static Logger logger = LoggerFactory.getLogger(AuthoringQuestionPanel.class);

    private boolean inUpdate;

    private final JSplitPane question_split = new JSplitPane();
    private final JList<Question> question_list = new JList<Question>();
    private final JScrollPane question_list_scroll = new JScrollPane(question_list);
    private final JPanel question_panel_right = new RightPanel();
    private final JLabel question_title_label = new JLabel("Title:");
    private final JLabel question_question_label = new JLabel("Question:");
    private final JLabel question_citation_label = new JLabel("Citation:");
    private final JLabel question_type_label = new JLabel("Question Type:");
    private final JComboBox<QuestionType> question_type_menu = new JComboBox<QuestionType>();
    private final JLabel question_answer_type_label = new JLabel("Answer Type:");
    private final JButton question_answer_type_button = new JButton("Selections");
    private final JLabel question_link_label = new JLabel("Question Link:");
    private final JLabel question_link_field = new JLabel("None");
    private final JLabel question_follows_label = new JLabel("Follows Question:");
    private final JComboBox<AnswerType> question_answer_type_menu = new JComboBox<AnswerType>(Shared.AnswerType.values());
    private final JComboBox<Question> question_follows_menu = new JComboBox<Question>();

    private final JLabel question_followup_only_label = new JLabel("Follow up protocols only:");
    private final JCheckBox question_followup_only_combo = new JCheckBox();

    private final JTextArea question_question_field = new NoTabTextArea();
    private final JTextArea question_citation_field = new NoTabTextArea();
    private final JTextField question_title_field = new JTextField();
    private final JButton question_new_button = new JButton("New");
    private final JButton question_preview_button = new JButton("Preview");
    private final JButton question_duplicate_button = new JButton("Duplicate");
    private final JButton question_link_button = new JButton("Set Link");
    private final JButton question_delete_button = new JButton("Delete");
    private final JLabel question_central_label = new JLabel();
    private final CategoryInputPane selectionsDialog;
    private final QuestionLinkDialog questionLinkDialog;
    private final Border listBorder;

    private final EgoNet egoNet;

    /**
     * Generates Panel for question editing to insert in file tab window
     *
     * @param type Type of questions on Page (e.g. Alter Questions)
     * @param parent parent frame for referencing composed objects
     */
    public AuthoringQuestionPanel(EgoNet egoNet, QuestionType type) throws Exception
    {
        super(type);
        this.egoNet = egoNet;

        DefaultComboBoxModel<QuestionType> model = new DefaultComboBoxModel<QuestionType>();
        for(QuestionType qType : Shared.QuestionType.values())
        {
        	if(qType.equals(QuestionType.STUDY_CONFIG))
        		continue;

        	model.addElement(qType);
        }
        question_type_menu.setModel(model);


        questionLinkDialog = new QuestionLinkDialog(egoNet);
        selectionsDialog = new CategoryInputPane(egoNet, question_list);
        question_title_field.setName("question_title_field");
        question_question_field.setName("question_question_field");
        question_citation_field.setName("question_citation_field");

        question_answer_type_menu.setName("question_answer_type_menu");

        listBorder = BorderFactory.createCompoundBorder(new TitledBorder(new EtchedBorder(EtchedBorder.RAISED, Color.white,
                new Color(178, 178, 178)), questionType.niceName), BorderFactory.createEmptyBorder(10,
                10, 10, 10));

        jbInit();
    }

    private Question getSelectedQuestion() {
    	return (Question) question_list.getSelectedValue();
    }

    /**
     * Component initialization
     *
     * @throws Exception
     */
    private void jbInit() throws Exception
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
        // question_list.setCellRenderer(new QuestionListCellRenderer());

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
        question_panel_right.add(question_title_label, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
                GridBagConstraints.HORIZONTAL, new Insets(10, 10, 10, 10), 0, 0));
        question_panel_right.add(question_title_field, new GridBagConstraints(1, 0, 2, 1, 0.0, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(10, 10, 10, 10), 0, 4));
        question_panel_right.add(question_question_label, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 10, 10, 10), 0, 0));
        question_panel_right.add(new JScrollPane(question_question_field), new GridBagConstraints(1, 1, 2, 3, 0.0, 0.4,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(10, 10, 10, 10), 0, 0));
        question_panel_right.add(question_citation_label, new GridBagConstraints(0, 4, 1, 1, 0.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 10, 10, 10), 0, 0));
        question_panel_right.add(new JScrollPane(question_citation_field), new GridBagConstraints(1, 4, 2, 3, 0.0, 0.3,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(10, 10, 10, 10), 0, 0));
        question_panel_right.add(question_type_label, new GridBagConstraints(0, 7, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
                GridBagConstraints.NONE, new Insets(10, 10, 10, 10), 0, 0));
        question_panel_right.add(question_type_menu, new GridBagConstraints(1, 7, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
                GridBagConstraints.NONE, new Insets(10, 10, 10, 10), 0, 0));
        question_panel_right.add(question_central_label, new GridBagConstraints(2, 7, 1, 1, 0.2, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(10, 10, 10, 10), 0, 0));
        question_panel_right.add(question_answer_type_label, new GridBagConstraints(0, 8, 1, 1, 0.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 10, 10, 10), 0, 0));
        question_panel_right.add(question_answer_type_menu, new GridBagConstraints(1, 8, 1, 1, 0.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10, 10, 10, 10), 0, 0));
        question_panel_right.add(question_answer_type_button, new GridBagConstraints(2, 8, 1, 1, 0.2, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(10, 10, 10, 10), 0, 0));

        question_panel_right.add(question_follows_label, new GridBagConstraints(0, 9, 1, 1, 0.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 10, 10, 10), 0, 0));
        question_panel_right.add(question_follows_menu, new GridBagConstraints(1, 9, 1, 1, 0.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10, 10, 10, 10), 0, 0));

        question_panel_right.add(question_followup_only_label, new GridBagConstraints(0, 13, 1, 1, 0.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 10, 10, 10), 0, 0));
        question_panel_right.add(question_followup_only_combo, new GridBagConstraints(1, 13, 1, 1, 0.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10, 10, 10, 10), 0, 0));


        question_panel_right.add(question_link_label, new GridBagConstraints(0, 10, 1, 1, 0.0, 0.0, GridBagConstraints.WEST,
                GridBagConstraints.NONE, new Insets(10, 10, 10, 10), 0, 0));
        question_panel_right.add(question_link_field, new GridBagConstraints(1, 10, 2, 1, 0.0, 0.0, GridBagConstraints.WEST,
                GridBagConstraints.NONE, new Insets(10, 10, 10, 10), 0, 0));

        question_panel_right.add(question_new_button, new GridBagConstraints(0, 11, 1, 1, 0.33, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(10, 10, 10, 10), 0, 0));
        question_panel_right.add(question_preview_button, new GridBagConstraints(1, 11, 1, 1, 0.33, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(10, 10, 10, 10), 0, 0));
        question_panel_right.add(question_delete_button, new GridBagConstraints(2, 11, 1, 1, 0.33, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(10, 10, 10, 10), 0, 0));

        question_panel_right.add(question_link_button, new GridBagConstraints(0, 12, 1, 1, 0.33, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(10, 10, 10, 10), 0, 0));
        question_panel_right.add(question_duplicate_button, new GridBagConstraints(1, 12, 1, 1, 0.33, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(10, 10, 10, 10), 0, 0));

        question_list.setModel(new DefaultListModel<Question>());
        egoNet.getStudy().fillList(questionType, (DefaultListModel<Question>)question_list.getModel());

        question_list.getSelectionModel().addListSelectionListener(new ListSelectionListener()
        {
            public void valueChanged(ListSelectionEvent e)
            {
                question_list_selectionChanged(e);
            }
        });

        question_list.addKeyListener(new KeyAdapter() {

        	public void keyPressed(KeyEvent ke){

        		// is anything selected
        		if(question_list.getSelectedIndex() == -1)
        			return;

        		// did we see something that wasn't UP or DOWN
                if(ke.getKeyCode() != KeyEvent.VK_DOWN)
                	return;

                // did it not have a "SHIFT" key
            	int modifiersEx = ke.getModifiersEx();
            	String tmpString = KeyEvent.getModifiersExText(modifiersEx);
            	if(!tmpString.contains("Shift"))
            		return;

            	// we got a shift-up or shift-down
                ke.consume();

                Question q_old = (Question) question_list.getSelectedValue();
                if (q_old == null)
                {
                    JOptionPane.showMessageDialog(egoNet.getFrame(), "Select a question first!", "Dupe Question",
                            JOptionPane.OK_OPTION);
                    return;
                }


                Question q = (Question) question_list.getSelectedValue();

                try {
	                egoNet.getStudy().removeQuestion(q);
	                egoNet.getStudy().addQuestion(q);
	                egoNet.getStudy().setModified(true);
                } catch (Exception ex) {
                	throw new RuntimeException(ex);
                }

                fillPanel();

        	}
        });

        question_new_button.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                question_new_button_actionPerformed(e);
            }
        });

        question_preview_button.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
            	AnswerType answerType = (AnswerType)question_answer_type_menu.getSelectedItem();

            	Question question = getSelectedQuestion();
            	ClientQuestionPanel.showPreview(
            			question_title_field.getText(),question_question_field.getText(),
            			questionType,answerType,question.getSelections());
            }
        });

        question_delete_button.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                question_delete_button_actionPerformed(e);
            }
        });

        question_duplicate_button.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                question_duplicate_button_actionPerformed(e);
            }
        });

        question_link_button.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                question_link_button_actionPerformed(e);
            }
        });

        question_follows_menu.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                question_follows_menu_actionPerformed(e);
            }
        });

        question_followup_only_combo.addItemListener(new java.awt.event.ItemListener()
        {
			@Override
			public void itemStateChanged(ItemEvent e) {
				question_followup_only_combo_actionPerformed(e);

			}
        });

        question_type_menu.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                question_type_menu_actionPerformed(e);
            }
        });

        question_answer_type_menu.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                question_answer_type_menu_actionPerformed(e);
            }
        });

        question_answer_type_button.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                set_selections_button_actionPerformed(e);
            }
        });

        question_title_field.getDocument().addDocumentListener(new DocumentListener()
        {
            public void insertUpdate(DocumentEvent e)
            {
                questionTitleEvent();
            }

            public void changedUpdate(DocumentEvent e)
            {
                questionTitleEvent();
            }

            public void removeUpdate(DocumentEvent e)
            {
                questionTitleEvent();
            }
        });

        question_question_field.getDocument().addDocumentListener(new DocumentListener()
        {
            public void insertUpdate(DocumentEvent e)
            {
                questionTextEvent();
            }

            public void changedUpdate(DocumentEvent e)
            {
                questionTextEvent();
            }

            public void removeUpdate(DocumentEvent e)
            {
                questionTextEvent();
            }
        });

        question_citation_field.getDocument().addDocumentListener(new DocumentListener()
        {
            public void insertUpdate(DocumentEvent e)
            {
                questionCitationEvent();
            }

            public void changedUpdate(DocumentEvent e)
            {
                questionCitationEvent();
            }

            public void removeUpdate(DocumentEvent e)
            {
                questionCitationEvent();
            }
        });

        this.add(question_split, null);

        inUpdate = false;
    }

    /**
     * Updates right side question fields when the selection changes
     *
     * @param e event generated by selection change.
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
     * fill List with appropriate questions Set other fields to selected
     * question
     */
    public void fillPanel()
    {
        if (questionType == egoNet.getFrame().curTab)
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

        if (questionType == egoNet.getFrame().curTab)
        {
        	DefaultListModel<Question> listModel = ((DefaultListModel<Question>) question_list.getModel());

            Object o = question_list.getSelectedValue();
            int selectedIndex = question_list.getSelectedIndex();

            listModel.removeAllElements();
            egoNet.getStudy().fillList(questionType, (DefaultListModel<Question>) question_list.getModel());

            /* Goal below is to stay near the previously selected element, somehow */
            // if the previously selected element is still in the model
            if(listModel.contains(o)) {
            	// select it
            	question_list.setSelectedValue(o, true);
            }
            // if the index is still valid in the model
            else if(selectedIndex >= 0 && selectedIndex < listModel.getSize()){
            	// select it
            	question_list.setSelectedIndex(selectedIndex);
            }
            // if the index-1 is still valid in the model
            else if(selectedIndex-1 >= 0 && selectedIndex-1 < listModel.getSize()){
            	question_list.setSelectedIndex(selectedIndex-1);
            }
            // if the index+1 is still valid in the model
            else if(selectedIndex+1 >= 0 && selectedIndex+1 < listModel.getSize()){
            	question_list.setSelectedIndex(selectedIndex+1);
            }

            logger.info("o is " + (o == null ? "": "NOT ") + "null, selectedIndex="+selectedIndex+", new selected index = "+question_list.getSelectedIndex());

        }

        inUpdate = false;
    }

    private void questionUpdate()
    {
        Question q;
        int index;

        inUpdate = true;

        if (questionType == egoNet.getFrame().curTab)
        {
            /* If no element selected, assume first */
            index = question_list.getSelectedIndex();
            if ((index == -1) && (question_list.getModel().getSize() > 0))
            {
                index = 0;
            }

            /* Load questions from list into follows menu */
            question_follows_menu.removeAllItems();
            question_follows_menu.addItem(egoNet.getStudy().getFirstQuestion());
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
                question_type_menu.setSelectedItem(questionType);
                question_answer_type_menu.setSelectedItem(q.answerType);
                question_question_field.setText(q.text);
                question_citation_field.setText(q.citation);
                question_title_field.setText(q.title);
                question_follows_menu.setSelectedIndex(index);
                question_followup_only_combo.setSelected(q.followupOnly);

                question_type_menu.setEnabled(true);
                question_answer_type_menu.setEnabled(!(q.questionType == QuestionType.ALTER_PROMPT));

                question_answer_type_button.setEnabled(q.answerType.equals(AnswerType.CATEGORICAL));
                question_question_field.setEditable(true);
                question_citation_field.setEditable(true);
                question_title_field.setEditable(true);
                question_delete_button.setEnabled(true);
                question_duplicate_button.setEnabled(true);
                question_link_button.setEnabled(true);

                /* Box only appears on alter pair page */
                question_central_label.setVisible(false);
                if (q.answerType.equals(AnswerType.CATEGORICAL))
                {
                    if (q.getSelections().size() == 0)
                    {
                        question_central_label.setText("No Selections");
                        question_central_label.setForeground(Color.red);
                        question_central_label.setVisible(true);
                    }
                    else if (questionType == QuestionType.ALTER_PAIR)
                    {
                        question_central_label.setText("No Adjacency Selections");
                        question_central_label.setForeground(Color.red);

                        for (int i = 0; i < q.getSelections().size(); i++)
                        {
                            if (q.getSelections().get(i).isAdjacent())
                            {
                                question_central_label.setText("Adjacency Selections Set");
                                question_central_label.setForeground(Color.black);
                            }
                        }

                        question_central_label.setVisible(true);
                    }
                }

                /* Fill in link field */
                if (q.link.isActive())
                {
                    Question linkQuestion = egoNet.getStudy().getQuestions().getQuestion(q.link.getAnswer().getQuestionId());

                    if (linkQuestion == null)
                    {
                        question_link_field.setText("< none >");
                    }
                    else
                    {
                        if (linkQuestion.title.length() > 32)
                        {
                            question_link_field.setText(linkQuestion.title.substring(0, 32) + ": " + q.link.getAnswer().string);
                        }
                        else
                        {
                            question_link_field.setText(linkQuestion.title + ": " + q.link.getAnswer().string);
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
     * Clear all on screen editable fields Generally called when a new survey is
     * started
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
                egoNet.getStudy().setModified(true);
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
                egoNet.getStudy().setModified(true);
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
                egoNet.getStudy().setModified(true);
            }
        }
    }

    /****
     * Event handler for new question button
     *
     * @param e Action Event
     */
    private void question_new_button_actionPerformed(ActionEvent e)
    {
        if (egoNet.getStudy().confirmIncompatibleChange(egoNet.getFrame()))
        {
            Question q = new Question(questionType);

            q.title = new String(q.getNiceName() + ":Untitled Question");

            if (q.questionType == QuestionType.ALTER_PROMPT) {
                q.answerType = AnswerType.TEXT;
            }

            try
            {
                egoNet.getStudy().addQuestion(q);
            }
            catch (DuplicateQuestionException e1)
            {
                throw new RuntimeException(e1);
            }

            fillPanel();
            question_list.setSelectedValue(q, true);

            question_title_field.requestFocus();
            question_title_field.setSelectionStart(0);
            question_title_field.setSelectionEnd(question_title_field.getText().length());

            egoNet.getStudy().setModified(true);
            egoNet.getStudy().setCompatible(false);
        }
    }

    /****
     * Event handler for delete question button
     *
     * @param e Action Event
     */
    private void question_delete_button_actionPerformed(ActionEvent e)
    {
        Question q = (Question) question_list.getSelectedValue();

        if (q != null)
        {
            int confirm = JOptionPane.showConfirmDialog(egoNet.getFrame(), "Permanently remove this questions?","Delete Question", JOptionPane.OK_CANCEL_OPTION);

            if ((confirm == JOptionPane.OK_OPTION) && egoNet.getStudy().confirmIncompatibleChange(egoNet.getFrame()))
            {
                egoNet.getStudy().removeQuestion(q);
                egoNet.getStudy().setModified(true);
                egoNet.getStudy().setCompatible(false);
                fillPanel();
            }
        }
    }

    /****
     * Event handler for dupe question button
     *
     * @param e Action Event
     */
    private void question_duplicate_button_actionPerformed(ActionEvent e)
    {
        Question q_old = (Question) question_list.getSelectedValue();
        if (q_old == null)
        {
            JOptionPane.showMessageDialog(egoNet.getFrame(), "Select a question first!", "Dupe Question",
                    JOptionPane.OK_OPTION);
            return;
        }

        if (egoNet.getStudy().confirmIncompatibleChange(egoNet.getFrame()))
        {
            Question q = new Question();

            q.title = new String((questionType.niceName) + ": " + q_old.title
                    + (q_old.title != null && q_old.title.endsWith("Duplicate Question") ? "" : " (Duplicate Question)"));
            q.answerType = q_old.answerType;
            q.citation = q_old.citation;
            q.setStatable(q_old.isStatable());
            q.text = q_old.text;
            q.followupOnly = q_old.followupOnly;

            try {
                egoNet.getStudy().addQuestion(q);
                q.setSelections(new ArrayList<Selection>(q_old.getSelections()));
            }
            catch (DuplicateQuestionException e1)
            {
                throw new RuntimeException(e1);
            }

            fillPanel();
            question_list.setSelectedValue(q, true);

            question_title_field.requestFocus();
            question_title_field.setSelectionStart(0);
            question_title_field.setSelectionEnd(question_title_field.getText().length());

            egoNet.getStudy().setModified(true);
            egoNet.getStudy().setCompatible(false);
        }
    }

    /**
     * Opens Set Link Dialog
     *
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
     *
     * @param e UI event
     */
    void question_type_menu_actionPerformed(ActionEvent e)
    {
        if (!inUpdate) {
            if (egoNet.getStudy().confirmIncompatibleChange(egoNet.getFrame())) {
                Question q = (Question) question_list.getSelectedValue();

				QuestionType type = (QuestionType)question_type_menu.getSelectedItem();

                try {
                	egoNet.getStudy().changeQuestionType(q, type);
                	egoNet.getStudy().setCompatible(false);
                } catch (DuplicateQuestionException ex)
                {
                    throw new RuntimeException(ex);
                }
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
     *
     * @param e UI event
     */
    void question_answer_type_menu_actionPerformed(ActionEvent e)
    {
        if (!inUpdate) {
            if (egoNet.getStudy().confirmIncompatibleChange(egoNet.getFrame())) {
				AnswerType i = (AnswerType)question_answer_type_menu.getSelectedItem();

                Question q = (Question) question_list.getSelectedValue();

                if (q != null) {
                    if (q.answerType.equals(i)) {
                        q.answerType = i;
                        egoNet.getStudy().setModified(true);
                        egoNet.getStudy().setCompatible(false);
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
     *
     * @param e UI event
     */
    void set_selections_button_actionPerformed(ActionEvent e)
    {
        selectionsDialog.activate();
    }

    /**
     * Changes order of questions
     *
     * @param e UI event
     */
    void question_follows_menu_actionPerformed(ActionEvent e)
    {
        if (!inUpdate)
        {
            if (egoNet.getStudy().confirmIncompatibleChange(egoNet.getFrame()))
            {
                Question follows = (Question) question_follows_menu.getSelectedItem();
                Question q = (Question) question_list.getSelectedValue();

                egoNet.getStudy().moveQuestionAfter(q, follows);
                egoNet.getStudy().setCompatible(false);
                egoNet.getStudy().setModified(true);
                fillPanel();
            }
            else
            {
                questionUpdate();
            }
        }
    }

    void question_followup_only_combo_actionPerformed(ItemEvent e)
    {
        if (!inUpdate)
        {
            if (egoNet.getStudy().confirmIncompatibleChange(egoNet.getFrame()))
            {
                Question q = (Question) question_list.getSelectedValue();
                q.setFollowupOnly(e.getStateChange() != ItemEvent.DESELECTED);

                egoNet.getStudy().setCompatible(false);
                egoNet.getStudy().setModified(true);
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
        egoNet.getStudy().setCentralQuestion(q);
    }

    public QuestionType getQuestionType()
    {
        return questionType;
    }
}

/**
 * Implements ListCellRenderer to differentiate between base and custom
 * questions
 */
class QuestionListCellRenderer<T> implements ListCellRenderer<T>
{
    protected final DefaultListCellRenderer defaultRenderer = new DefaultListCellRenderer();

    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected,
            boolean cellHasFocus)
    {
        JLabel renderer = (JLabel) defaultRenderer.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        return renderer;
    }
}
