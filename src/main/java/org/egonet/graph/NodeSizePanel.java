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
package org.egonet.graph;

import org.jdesktop.layout.GroupLayout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.table.TableColumnModel;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.util.*;

import org.egonet.gui.interview.EgoClient;
import org.egonet.gui.util.*;
import org.egonet.model.Answer;
import org.egonet.model.Question;
import org.egonet.model.QuestionList;
import org.egonet.model.Selection;
import org.egonet.model.Shared.AnswerType;
import org.egonet.model.Shared.QuestionType;
import org.egonet.model.Study;


public class NodeSizePanel extends JPanel {

	final private static Logger logger = LoggerFactory.getLogger(NodeSizePanel.class);

	private JLabel questionLabel;

	private JComboBox<Question> questionCombo;

	private JComboBox<String> sizeCombo;

	private JTable table;

	private GroupLayout layout;

	private GraphRenderer graphRenderer;

	private JButton applyButton;

	String[] sizes = { "1", "2", "3", "4", "5", "6", "7", "8", "9", "10" };

	List<Selection> selectionList = new ArrayList<Selection>();

	private EgoClient egoClient;

	public NodeSizePanel(EgoClient egoClient, GraphRenderer renderer) {
		this.egoClient=egoClient;
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
			if (currentQuestion.questionType == QuestionType.ALTER) {
				// populate the list box with only questions that have choices
				// as answers
				if (currentQuestion.answerType.equals(AnswerType.CATEGORICAL)
						|| currentQuestion.answerType.equals(AnswerType.TEXT))
					qList.add(currentQuestion);
			}
		}
		questionCombo = new JComboBox<Question>(qList.toArray(new Question[0]));
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

		// create size combo for table cell
		sizeCombo = new JComboBox<String>(sizes);
		sizeCombo.setPreferredSize(new Dimension(20, 20));
		sizeCombo.setMaximumSize(new Dimension(20, 30));
		sizeCombo.setSelectedIndex(0);

		// create apply button
		applyButton = new JButton("Apply Size");
		applyButton.setVisible(true);
		applyButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				updateNodeSize();
			}
		});

		createTable();
		drawPanel();

	}

	private Object[][] getRowData() {
		if(questionCombo.getSelectedIndex() == -1)
			return new Object[0][0];

		Question question = (Question) questionCombo.getSelectedItem();;

		// logger.info("Question examining:" + question.UniqueId);

		if (question.answerType.equals(AnswerType.CATEGORICAL)) {
			int noOfRows = question.getSelections().size();
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
			int noOfSizes = question.getSelections().size();
			for (int i = 1; i <= noOfSizes; i++) {
				if (i < sizes.length) {
					String num = (new Integer(i)).toString();
					rowData[i - 1][1] = num;
				} else {
					String num = (new Integer(i - sizes.length)).toString();
					rowData[i - 1][1] = num;
				}
			}
			return rowData;

		} else {
			// logger.info("Populating text answers!!!");
			if (!selectionList.isEmpty()) {
				selectionList.removeAll(selectionList);
			}
			Answer[] answers = egoClient.getInterview().get_answers();
			for (int i = 0; i < answers.length; i++) {
				// logger.info("Question examining:"
				// + answers[i].questionId + "," + question.UniqueId);
				if (answers[i].getQuestionId().equals(question.UniqueId)) {

					if (answers[i].string == null
							|| isPresentInSelectionList(answers[i].string)) {
						continue;
					}
					Selection selection = new Selection();
					selection.setString(answers[i].string);
					// logger.info("Selection:" + selection);
					selectionList.add(selection);
				}

			}
			Object[][] rowData = new Object[selectionList.size()][2];
			for (int i = 0; i < selectionList.size(); i++) {
				rowData[i][0] = selectionList.get(i);
			}
			// populate the shapes
			int noOfSizes = selectionList.size();
			for (int i = 1; i <= noOfSizes; i++) {
				if (i < sizes.length) {
					String num = (new Integer(i)).toString();
					rowData[i - 1][1] = num;
				} else {
					String num = (new Integer(i - sizes.length)).toString();
					rowData[i - 1][1] = num;
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

		DefaultCellEditor sizeEditor = new DefaultCellEditor(sizeCombo);
		columnModel.getColumn(1).setCellEditor(sizeEditor);
		columnModel.getColumn(1).setCellRenderer(new TableComboBoxRenderer<String>(sizes));

		columnModel.getColumn(0).setMaxWidth(200);
		columnModel.getColumn(1).setMaxWidth(80);
	}

	private void updateNodeSize() {
		Question question = (Question) questionCombo.getSelectedItem();
		if (question.answerType.equals(AnswerType.CATEGORICAL)) {
			for (int i = 0; i < question.getSelections().size(); i++) {
				Selection selection = question.getSelections().get(i);

				GraphQuestionSelectionPair graphQuestion = new GraphQuestionSelectionPair(question,selection);
				NodeProperty nodeProperty = new NodeProperty();
				nodeProperty.setSize(Integer.parseInt((String) table
						.getValueAt(i, 1)));
				nodeProperty.setProperty(NodeProperty.NodePropertyType.Size);
				graphRenderer.addQAsettings(graphQuestion, nodeProperty);
				graphRenderer.updateGraphSettings();
			}
		}
		else if (question.answerType.equals(AnswerType.TEXT)) {
			logger.info("Applying labels for text questions");
			for (int i =0;i < selectionList.size() ; i++) {
				Selection selection = selectionList.get(i);
				NodeProperty nodeProperty = new NodeProperty();
				nodeProperty.setSize(Integer.parseInt((String) table
						.getValueAt(i, 1)));
				nodeProperty.setProperty(NodeProperty.NodePropertyType.Size);
				GraphQuestionSelectionPair graphQuestion = new GraphQuestionSelectionPair(question,selection);
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
