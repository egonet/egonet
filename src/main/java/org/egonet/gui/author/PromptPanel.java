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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;

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
import org.egonet.model.Shared.QuestionType;


/**
 * Generic Panel creation and handling routines for question editing
 */
public class PromptPanel extends EgoQPanel
{
    private boolean inUpdate;

    private final JSplitPane question_split = new JSplitPane();
    private final JList<Question> question_list = new JList<Question>();
    private final JScrollPane question_list_scroll = new JScrollPane(question_list);
    private final JPanel question_panel_right = new RightPanel();
    private final JLabel question_title_label = new JLabel("Title:");
    private final JLabel question_question_label = new JLabel("Question:");
    private final JLabel question_citation_label = new JLabel("Citation:");
    private final JLabel question_follows_label = new JLabel("Follows Question:");
    private final JTextArea question_question_field = new NoTabTextArea();
    private final JTextArea question_citation_field = new NoTabTextArea();
    private final JTextField question_title_field = new JTextField();
    private final JButton question_new_button = new JButton("New");
    private final JButton question_preview_button = new JButton("Preview");
    private final JComboBox<Question> question_follows_menu = new JComboBox<Question>();
    private final JButton question_delete_button = new JButton("Delete");
    private final Border listBorder;

    private final EgoNet egoNet;

    /**
     * Generates Panel for question editing to insert in file tab window
     *
     * @param type Type of questions on Page (e.g. Alter Questions)
     * @param parent parent frame for referencing composed objects
     */
    public PromptPanel(EgoNet egoNet, QuestionType type) throws Exception
    {
        super(type);
        this.egoNet = egoNet;


        question_question_field.setName("question_question_field");
        question_citation_field.setName("question_citation_field");
        question_title_field.setName("question_title_field");

		listBorder 		= BorderFactory.createCompoundBorder(
			new TitledBorder(new EtchedBorder(EtchedBorder.RAISED,Color.white,new Color(178, 178, 178)),
					questionType.niceName),
			BorderFactory.createEmptyBorder(10,10,10,10));

        jbInit();
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
        question_list.setCellRenderer(new QuestionListCellRenderer<Question>());

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
        question_panel_right.add(new JScrollPane(question_question_field), new GridBagConstraints(1, 1, 2, 2, 0.0, 0.4,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(10, 10, 10, 10), 0, 0));
        question_panel_right.add(question_citation_label, new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 10, 10, 10), 0, 0));
        question_panel_right.add(new JScrollPane(question_citation_field), new GridBagConstraints(1, 3, 2, 2, 0.0, 0.3,
                GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(10, 10, 10, 10), 0, 0));
        question_panel_right.add(question_new_button, new GridBagConstraints(0, 7, 1, 1, 0.33, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(10, 10, 10, 10), 0, 0));
        question_panel_right.add(question_preview_button, new GridBagConstraints(1, 7, 1, 1, 0.33, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(10, 10, 10, 10), 0, 0));
        question_panel_right.add(question_delete_button, new GridBagConstraints(2, 7, 1, 1, 0.33, 0.0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(10, 10, 10, 10), 0, 0));
        question_panel_right.add(question_follows_label, new GridBagConstraints(0, 5, 1, 1, 0.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 10, 10, 10), 0, 0));
        question_panel_right.add(question_follows_menu, new GridBagConstraints(1, 5, 1, 1, 0.0, 0.0,
                GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10, 10, 10, 10), 0, 0));

        DefaultListModel<Question> listModel = new DefaultListModel<Question>();
        question_list.setModel(listModel);
        egoNet.getStudy().fillList(questionType, listModel);

        question_list.getSelectionModel().addListSelectionListener(new ListSelectionListener()
        {
            public void valueChanged(ListSelectionEvent e)
            {
                question_list_selectionChanged(e);
            }
        });

        question_new_button.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(ActionEvent e) {
                question_new_button_actionPerformed(e);
            }
        });

        question_preview_button.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
            	ClientQuestionPanel.showPreview(
            			question_title_field.getText(),question_question_field.getText(),
            			QuestionType.ALTER_PROMPT,null,null);
            }
        });

        question_delete_button.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                question_delete_button_actionPerformed(e);
            }
        });

        question_follows_menu.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                question_follows_menu_actionPerformed(e);
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

        add(question_split);

        inUpdate = false;
    }

    /**
     * Updates right side question fields when the selection changes
     *
     * @param e event generated by selection change.
     */
    public void question_list_selectionChanged(ListSelectionEvent e)
    {
        if (!inUpdate)
        {
            questionUpdate();
        }
    }

    /****
     * fill List with appropriate questions Set other fields to selected
     * question
     */
    public void fillPanel()
    {
        //logger.info("fillPanel() called in PromptPanel but questionType was " + questionType + " and curTab was " + egoNet.getFrame().curTab);
        if (questionType == egoNet.getFrame().curTab)
        {
            storageUpdate();
            questionUpdate();
        }
    }

    /**
     * Called when file changes to load new questions into list
     */
    void storageUpdate()
    {
        inUpdate = true;

        if (questionType == egoNet.getFrame().curTab)
        {
            ((DefaultListModel) question_list.getModel()).removeAllElements();
            egoNet.getStudy().fillList(questionType, (DefaultListModel<Question>)question_list.getModel());
        }

        inUpdate = false;
    }

    void questionUpdate()
    {
        inUpdate = true;

        /** @todo Use List Data Listener? */
        if (questionType.equals(egoNet.getFrame().curTab)) {
            int index = question_list.getSelectedIndex();
            if ((question_list.getModel().getSize() > 0) && (index == -1))
            {
                index = 0;
            }

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
            Question q = (Question) question_list.getSelectedValue();
            if (q != null)
            {
                question_question_field.setText(q.text);
                question_citation_field.setText(q.citation);
                question_title_field.setText(q.title);
                question_follows_menu.setSelectedIndex(index);

                question_question_field.setEditable(true);
                question_citation_field.setEditable(true);
                question_title_field.setEditable(true);
                question_delete_button.setEnabled(true);
                question_follows_menu.setEnabled(true);
            }
            else
            {
                question_question_field.setText(null);
                question_citation_field.setText(null);
                question_title_field.setText(null);

                question_question_field.setEditable(false);
                question_citation_field.setEditable(false);
                question_title_field.setEditable(false);
                question_delete_button.setEnabled(false);
                question_follows_menu.setEnabled(false);
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
     *
     * @param e Document Event
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
     *
     * @param e Document Event
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
     *
     * @param e Document Event
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

    void question_new_button_actionPerformed(ActionEvent e) {

        Question q = new Question();
        q.title = new String("Untitled Question");

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
    }

    void question_delete_button_actionPerformed(ActionEvent e)
    {
        Question q = (Question) question_list.getSelectedValue();

        if (q != null)
        {
            int confirm = JOptionPane.showConfirmDialog(egoNet.getFrame(), "Permanently remove this questions?",
                    "Delete Question", JOptionPane.OK_CANCEL_OPTION);

            if (confirm == JOptionPane.OK_OPTION)
            {
                egoNet.getStudy().removeQuestion(q);
                fillPanel();
            }
        }
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
            Question follows = (Question) question_follows_menu.getSelectedItem();
            Question q = (Question) question_list.getSelectedValue();

            egoNet.getStudy().moveQuestionAfter(q, follows);
            fillPanel();
        }
    }
}
