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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.util.*;

import org.egonet.util.listbuilder.Selection;
import org.egonet.util.table.*;

import com.endlessloopsoftware.ego.client.EgoClient;
import com.endlessloopsoftware.egonet.Answer;
import com.endlessloopsoftware.egonet.Question;
import com.endlessloopsoftware.egonet.QuestionList;
import com.endlessloopsoftware.egonet.Shared;
import com.endlessloopsoftware.egonet.Study;
import com.endlessloopsoftware.egonet.Shared.QuestionType;

public class NodeShapePanel extends JPanel {

	private JLabel questionLabel;

	private JComboBox questionCombo;

	private JComboBox shapeCombo;

	private JTable table;

	private GroupLayout layout;

	private GraphRenderer graphRenderer;

	private JButton applyButton;

	List<Selection> selectionList = new ArrayList<Selection>();

	private EgoClient egoClient;

	public NodeShapePanel(EgoClient egoClient, GraphRenderer renderer) {
		this.egoClient = egoClient;
		this.graphRenderer = renderer;
		layout = new GroupLayout(this);
		this.setLayout(layout);
		layout.setAutocreateGaps(true);
		layout.setAutocreateContainerGaps(true);
		createComponents();
	}

	private void createComponents() {

		questionLabel = new JLabel("Choose question to shape");
		questionLabel.setVisible(true);

		// create questionCombo
		List<Question> qList = new ArrayList<Question>();
		Study study = egoClient.getInterview().getStudy();
		QuestionList questionList = study.getQuestions();
		
		for (Long key : questionList.keySet()) {
			Question currentQuestion = questionList.get(key);
			QuestionType questionType = currentQuestion.questionType;
			if (questionType == Shared.QuestionType.ALTER) {
				// populate the list box with only questions that have choices
				// as answers
				if (currentQuestion.answerType == Shared.AnswerType.CATEGORICAL
						|| currentQuestion.answerType == Shared.AnswerType.TEXT)
					qList.add(currentQuestion);
			}
		}
		questionCombo = new JComboBox(qList.toArray());
		questionCombo.setVisible(true);
		questionCombo.setEnabled(true);
		questionCombo.setPreferredSize(new Dimension(20, 20));
		questionCombo.setMaximumSize(new Dimension(20, 30));
		questionCombo.setAutoscrolls(true);

		questionCombo.addItemListener(new java.awt.event.ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					createTable();
					drawPanel();
				}
			}
		});

		// create shape combo for table cell
		shapeCombo = new JComboBox(NodeProperty.NodeShape.values());
		shapeCombo.setPreferredSize(new Dimension(20, 20));
		shapeCombo.setMaximumSize(new Dimension(20, 30));
		// shapeCombo.setSelectedIndex(0);

		// create apply button
		applyButton = new JButton("Apply Shape");
		applyButton.setVisible(true);
		applyButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				updateNodeShape();
			}
		});

		createTable();
		drawPanel();

	}

	private Object[][] getRowData() {
		if(questionCombo.getSelectedIndex() == -1)
			return new Object[0][0];
		
		Question question = (Question) questionCombo.getSelectedItem();

		// System.out.println("Question examining:" + question.UniqueId);

		if (question.answerType == Shared.AnswerType.CATEGORICAL) {
			int noOfRows = question.getSelections().length;
			Object[][] rowData = new Object[noOfRows][2];
			/* change the list of selections based on the selected question */
			if (!selectionList.isEmpty()) {
				selectionList.removeAll(selectionList);
			}
			for (Selection selection : question.getSelections()) {
				selectionList.add(selection);
			}
			for (int i = 0; i < noOfRows; i++) {
				rowData[i][0] = selectionList.get(i);
			}

			// populate the shapes
			int maxNoOfShapes = NodeProperty.NodeShape.values().length;
			for (int i = 0; i < noOfRows; i++) {
				if (i < maxNoOfShapes) {
					NodeProperty.NodeShape shape = NodeProperty.NodeShape
							.values()[i];
					rowData[i][1] = shape;
				} else {
					NodeProperty.NodeShape shape = NodeProperty.NodeShape
							.values()[i % maxNoOfShapes];
					rowData[i][1] = shape;
				}
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
			Object[][] rowData = new Object[selectionList.size()][2];
			for (int i = 0; i < selectionList.size(); i++) {
				rowData[i][0] = selectionList.get(i);
			}
			int noOfRows = selectionList.size();

			// populate the shapes
			int maxNoOfShapes = NodeProperty.NodeShape.values().length;
			for (int i = 0; i < noOfRows; i++) {
				if (i < maxNoOfShapes) {
					NodeProperty.NodeShape shape = NodeProperty.NodeShape
							.values()[i];
					rowData[i][1] = shape;
				} else {
					NodeProperty.NodeShape shape = NodeProperty.NodeShape
							.values()[i % maxNoOfShapes];
					rowData[i][1] = shape;
				}
			}
			return rowData;
		}
	}

	private boolean isPresentInSelectionList(String str) {
		for (int i = 0; i < selectionList.size(); i++) {
			if (selectionList.get(i).getString().equals(str))
				return true;
		}
		return false;
	}

	private void createTable() {

		Object[][] rowData = getRowData();
		table = new JTable(new PropertyTableModel(rowData));
		table.setPreferredScrollableViewportSize(table.getPreferredSize());
		table.setRowHeight(25);
		table.setVisible(true);
		table.setBorder(BorderFactory.createCompoundBorder(BorderFactory
				.createLineBorder(Color.black), getBorder()));

		TableColumnModel columnModel = table.getColumnModel();
		LabelRenderer selectionRenderer = new LabelRenderer();
		columnModel.getColumn(0).setCellRenderer(selectionRenderer);

		DefaultCellEditor shapeEditor = new DefaultCellEditor(shapeCombo);
		columnModel.getColumn(1).setCellEditor(shapeEditor);
		columnModel.getColumn(1).setCellRenderer(
				new TableComboBoxRenderer(NodeProperty.NodeShape.values()));

		columnModel.getColumn(0).setMaxWidth(200);
		columnModel.getColumn(1).setMaxWidth(150);
	}

	private void updateNodeShape() {
		Question question = (Question) questionCombo.getSelectedItem();
		if (question.answerType == Shared.AnswerType.CATEGORICAL) {
			for (int i = 0; i < question.getSelections().length; i++) {
				Selection selection = question.getSelections()[i];

				GraphQuestionSelectionPair graphQuestion = new GraphQuestionSelectionPair(question,
						selection, Shared.QuestionType.ALTER);
				NodeProperty nodeProperty = new NodeProperty();
				nodeProperty.setShape((NodeProperty.NodeShape) table
						.getValueAt(i, 1));
				nodeProperty.setProperty(NodeProperty.NodePropertyType.Shape);
				graphRenderer.addQAsettings(graphQuestion, nodeProperty);
				graphRenderer.updateGraphSettings();
			}
		}
		else if (question.answerType == Shared.AnswerType.TEXT) {
			System.out.println("Applying labels for text questions");
			for (int i =0;i < selectionList.size() ; i++) {
				Selection selection = selectionList.get(i);
				NodeProperty nodeProperty = new NodeProperty();
				nodeProperty.setShape((NodeProperty.NodeShape) table
						.getValueAt(i, 1));
				nodeProperty.setProperty(NodeProperty.NodePropertyType.Shape);
				GraphQuestionSelectionPair graphQuestion = new GraphQuestionSelectionPair(question,
						selection, Shared.QuestionType.ALTER);
				graphRenderer.addQAsettings(graphQuestion, nodeProperty);
				graphRenderer.updateGraphSettings();
			}
		}
	}

	private void drawPanel() {
		this.removeAll();

		GroupLayout.SequentialGroup hGroup = layout.createSequentialGroup();
		hGroup.add(layout.createParallelGroup().add(questionLabel).add(
				questionCombo).add(table.getTableHeader()).add(table).add(
				applyButton));

		layout.setHorizontalGroup(hGroup);

		GroupLayout.SequentialGroup vGroup = layout.createSequentialGroup();
		vGroup.add(layout.createParallelGroup(GroupLayout.BASELINE).add(
				questionLabel));
		vGroup.add(layout.createParallelGroup(GroupLayout.BASELINE).add(
				questionCombo));
		vGroup.add(layout.createParallelGroup(GroupLayout.BASELINE).add(
				table.getTableHeader()));
		vGroup.add(layout.createParallelGroup(GroupLayout.BASELINE).add(table));
		vGroup.add(layout.createParallelGroup(GroupLayout.BASELINE).add(
				applyButton));

		layout.setVerticalGroup(vGroup);
	}
}
