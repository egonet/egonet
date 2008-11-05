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
package com.endlessloopsoftware.ego.client.graph;

import org.jdesktop.layout.GroupLayout;
import javax.swing.*;
import javax.swing.table.TableColumnModel;

import java.awt.Color;
import java.awt.Dimension;
import java.util.*;
import java.awt.event.*;

import org.egonet.util.listbuilder.Selection;
import org.egonet.util.table.LabelTableModel;
import org.egonet.util.table.LabelRenderer;
import com.endlessloopsoftware.ego.client.EgoClient;
import com.endlessloopsoftware.egonet.Answer;
import com.endlessloopsoftware.egonet.Question;
import com.endlessloopsoftware.egonet.QuestionList;
import com.endlessloopsoftware.egonet.Study;
import com.endlessloopsoftware.egonet.Question.QuestionType;

public class NodeLabelPanel extends JPanel {

	private JComboBox questionCombo;

	private JRadioButton questionRadio;

	private JRadioButton alterNamesRadio;

	private JButton applyButton;

	private JTable table;

	private GroupLayout layout;

	private GraphRenderer graphRenderer;

	List<Selection> selectionList = new ArrayList<Selection>();

	private EgoClient egoClient;

	public NodeLabelPanel(EgoClient egoClient, GraphRenderer renderer) {
		this.egoClient=egoClient;
		this.graphRenderer = renderer;
		layout = new GroupLayout(this);
		this.setLayout(layout);
		layout.setAutocreateGaps(true);
		layout.setAutocreateContainerGaps(true);
		createComponents();
	}

	private void createComponents() {

		// create questionCombo
		List<Question> qList = new ArrayList<Question>();
		Study study = egoClient.getInterview().getStudy();
		QuestionList questionList = study.getQuestions();
		
		for (Long key : questionList.keySet()) {
			Question currentQuestion = questionList.get(key);
			QuestionType questionType = currentQuestion.questionType;
			if (questionType == Question.QuestionType.ALTER) {
				// populate the list box with only questions that have choices
				// as answers
				if (currentQuestion.answerType == Question.AnswerType.CATEGORICAL
						|| currentQuestion.answerType == Question.AnswerType.TEXT)
					qList.add(currentQuestion);
			}
		}
		questionCombo = new JComboBox(qList.toArray());
		questionCombo.setVisible(true);
		questionCombo.setEnabled(false);
		questionCombo.setPreferredSize(new Dimension(20, 20));
		questionCombo.setMaximumSize(new Dimension(20, 30));
		questionCombo.setAutoscrolls(true);
		questionCombo.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(ActionEvent e) {
				createTable();
				drawPanel();
			}
		});

		questionRadio = new JRadioButton("Label by question");
		alterNamesRadio = new JRadioButton("Label by alter names");
		ButtonGroup buttonGroup = new ButtonGroup();
		buttonGroup.add(questionRadio);
		buttonGroup.add(alterNamesRadio);
		alterNamesRadio.setSelected(true);
		questionRadio.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent e) {
				if (questionRadio.isSelected() == true) {
					questionCombo.setEnabled(true);
					applyButton.setEnabled(true);
				}
			}
		});

		alterNamesRadio.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent e) {
				if (alterNamesRadio.isSelected() == true) {
					// table.setEnabled(false);
					questionCombo.setEnabled(false);
					applyButton.setEnabled(false);
					updateNodeLabels(true);
				}
			}
		});

		// create apply button
		applyButton = new JButton("Apply Label");
		applyButton.setVisible(true);
		applyButton.setEnabled(false);
		applyButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				updateNodeLabels(false);
			}
		});

		createTable();
		drawPanel();
	}

	private Selection[] getRowData() {
		if(questionCombo.getSelectedIndex() == -1)
			return new Selection[0];
		
		Question question = (Question) questionCombo.getSelectedItem();
		// System.out.println("Question examining:" + question.UniqueId);

		if (question.answerType == Question.AnswerType.CATEGORICAL) {
			int noOfRows = question.getSelections().length;
			Selection[] rowData = new Selection[noOfRows];
			/* change the list of selections based on the selected question */
			if (!selectionList.isEmpty()) {
				selectionList.removeAll(selectionList);
			}
			for (Selection selection : question.getSelections()) {
				selectionList.add(selection);
			}
			for (int i = 0; i < noOfRows; i++) {
				rowData[i] = selectionList.get(i);
			}
			return rowData;

		} else {
			// System.out.println("Populating text answers!!!");
			if (!selectionList.isEmpty()) {
				selectionList.removeAll(selectionList);
			}
			Answer[] answers = egoClient.getInterview().get_answers();
			for (int i = 0; i < answers.length; i++) {
				// System.out.println("Question examining:"
				// + answers[i].questionId + "," + question.UniqueId);
				if (answers[i].questionId.equals(question.UniqueId)) {

					if (answers[i].string == null
							|| isPresentInSelectionList(answers[i].string)) {
						continue;
					}
					Selection selection = new Selection();
					selection.setString(answers[i].string);
					// System.out.println("Selection:" + selection);
					selectionList.add(selection);
				}

			}
			Selection[] rowData = new Selection[selectionList.size()];
			for (int i = 0; i < selectionList.size(); i++) {
				rowData[i] = selectionList.get(i);
			}
			return rowData;
		}

	}

	private void createTable() {
		Selection[] rowData = getRowData();
		table = new JTable(new LabelTableModel(rowData));
		table.setPreferredScrollableViewportSize(table.getPreferredSize());
		table.setRowHeight(25);
		table.setVisible(true);
		table.setBorder(BorderFactory.createCompoundBorder(BorderFactory
				.createLineBorder(Color.black), getBorder()));

		TableColumnModel columnModel = table.getColumnModel();

		LabelRenderer selectionRenderer = new LabelRenderer();
		columnModel.getColumn(0).setCellRenderer(selectionRenderer);
		columnModel.getColumn(0).setMaxWidth(200);
	}

	private boolean isPresentInSelectionList(String str) {
		for (int i = 0; i < selectionList.size(); i++) {
			if (selectionList.get(i).getString().equals(str))
				return true;
		}
		return false;
	}

	private void updateNodeLabels(boolean defaultNames) {
		if (defaultNames) {
			int noOfAlters = egoClient.getInterview().getNumAlters();
			for (int i = 0; i < noOfAlters; i++) {
				String alterName = egoClient.getInterview().getAlterList()[i];
				graphRenderer.updateGraphSettings(alterName, i, 1);
			}
		} else {
			Question question = (Question) questionCombo.getSelectedItem();
			if (question.answerType == Question.AnswerType.CATEGORICAL) {
				for (Selection selection : question.getSelections()) {
					GraphQuestionSelectionPair graphQuestion = new GraphQuestionSelectionPair(question,
							selection, Question.QuestionType.ALTER);
					NodeProperty nodeProperty = new NodeProperty();
					nodeProperty.setLabel(selection.getString());
					nodeProperty.setProperty(NodeProperty.NodePropertyType.Label);
					graphRenderer.addQAsettings(graphQuestion, nodeProperty);
					graphRenderer.updateGraphSettings();
				}
			} else if (question.answerType == Question.AnswerType.TEXT) {
				System.out.println("Applying labels for text questions");
				for (Selection selection : selectionList) {
					NodeProperty nodeProperty = new NodeProperty();
					nodeProperty.setLabel(selection.getString());
					nodeProperty.setProperty(NodeProperty.NodePropertyType.Label);
					GraphQuestionSelectionPair graphQuestion = new GraphQuestionSelectionPair(question,
							selection, Question.QuestionType.ALTER);
					graphRenderer.addQAsettings(graphQuestion, nodeProperty);
					graphRenderer.updateGraphSettings();
				}
			}
		}
		GraphRenderer.getVv().repaint();
	}

	private void drawPanel() {

		this.removeAll();
		GroupLayout.SequentialGroup hGroup = layout.createSequentialGroup();
		hGroup.add(layout.createParallelGroup().add(questionRadio).add(
				questionCombo).add(table.getTableHeader()).add(table).add(
				applyButton).add(alterNamesRadio));

		layout.setHorizontalGroup(hGroup);

		GroupLayout.SequentialGroup vGroup = layout.createSequentialGroup();
		vGroup.add(layout.createParallelGroup(GroupLayout.BASELINE).add(
				questionRadio));
		vGroup.add(layout.createParallelGroup(GroupLayout.BASELINE).add(
				questionCombo));
		vGroup.add(layout.createParallelGroup(GroupLayout.BASELINE).add(
				table.getTableHeader()));
		vGroup.add(layout.createParallelGroup(GroupLayout.BASELINE).add(table));
		vGroup.add(layout.createParallelGroup(GroupLayout.BASELINE).add(
				applyButton));
		vGroup.add(layout.createParallelGroup(GroupLayout.BASELINE).add(
				alterNamesRadio));

		layout.setVerticalGroup(vGroup);
	}
}
