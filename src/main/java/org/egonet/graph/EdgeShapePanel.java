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
import org.egonet.model.Question;
import org.egonet.model.QuestionList;
import org.egonet.model.Selection;
import org.egonet.model.Shared.QuestionType;
import org.egonet.model.Study;



public class EdgeShapePanel extends JPanel {

	final private static Logger logger = LoggerFactory.getLogger(EdgeShapePanel.class);

	private JLabel questionLabel;

	private JComboBox questionCombo;

	private JComboBox shapeCombo;

	private JTable table;

	private GroupLayout layout;

	private GraphRenderer graphRenderer;

	private JButton applyButton;

	List<Selection> selectionList = new ArrayList<Selection>();

	public static boolean[][] edgesSelected;

	List<Question> qList = new ArrayList<Question>();

	private EgoClient egoClient;

	public EdgeShapePanel(EgoClient egoClient, GraphRenderer renderer) {
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
		Study study = egoClient.getInterview().getStudy();
		QuestionList questionList = study.getQuestions();

		for (Long key : questionList.keySet()) {
			Question currentQuestion = questionList.get(key);
			if (currentQuestion.questionType == QuestionType.ALTER_PAIR) {
				// populate the list box with only questions that have choices
				// as answers
				if (currentQuestion.getSelections().size() >= 1)
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

		// create shape combo for table cell
		shapeCombo = new JComboBox<EdgeProperty.EdgeShape>(EdgeProperty.EdgeShape.values());
		shapeCombo.setPreferredSize(new Dimension(20, 20));
		shapeCombo.setMaximumSize(new Dimension(20, 30));
		// shapeCombo.setSelectedIndex(0);

		// create apply button
		applyButton = new JButton("Apply Shape");
		applyButton.setVisible(true);
		applyButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				updateEdgeShape();
			}
		});

		Question question = (Question) questionCombo.getSelectedItem();
		edgesSelected = new boolean[qList.size()][question.getSelections().size()];
		for (int i = 0; i < qList.size(); i++) {
			for (int j = 0; j < question.getSelections().size(); j++) {
				edgesSelected[i][j] = false;
			}
		}

		createTable();
		drawPanel();

	}

	private void createTable() {
		Question question = (Question) questionCombo.getSelectedItem();
		int noOfRows = question.getSelections().size();
		Object[][] rowData = new Object[noOfRows][3];
		/* change the list of selections based on the selected question */
		if (!selectionList.isEmpty()) {
			selectionList.removeAll(selectionList);
		}
		for (Selection selection : question.getSelections()) {
			selectionList.add(selection);
		}
		// populate the responses
		for (int i = 0; i < noOfRows; i++) {
			rowData[i][0] = Boolean.FALSE;
			rowData[i][1] = selectionList.get(i);
		}
		// populate the shapes
		int maxNoOfShapes = EdgeProperty.EdgeShape.values().length;
		for (int i = 0; i < noOfRows; i++) {
			if (i < maxNoOfShapes) {
				EdgeProperty.EdgeShape shape = EdgeProperty.EdgeShape.values()[i];
				rowData[i][2] = shape;
			} else {
				EdgeProperty.EdgeShape shape = EdgeProperty.EdgeShape.values()[i
						% maxNoOfShapes];
				rowData[i][2] = shape;
			}
		}

		table = new JTable(new ChoosablePropertyTableModel(rowData));
		table.setPreferredScrollableViewportSize(table.getPreferredSize());
		table.setRowHeight(25);
		table.setVisible(true);
		table.setBorder(BorderFactory.createCompoundBorder(BorderFactory
				.createLineBorder(Color.black), getBorder()));

		TableColumnModel columnModel = table.getColumnModel();
		LabelRenderer selectionRenderer = new LabelRenderer();
		columnModel.getColumn(1).setCellRenderer(selectionRenderer);

		DefaultCellEditor shapeEditor = new DefaultCellEditor(shapeCombo);
		columnModel.getColumn(2).setCellEditor(shapeEditor);
		columnModel.getColumn(2).setCellRenderer(new TableComboBoxRenderer<EdgeProperty.EdgeShape>(EdgeProperty.EdgeShape.values()));

		columnModel.getColumn(0).setMaxWidth(50);
		columnModel.getColumn(1).setMaxWidth(200);
		columnModel.getColumn(2).setMaxWidth(150);
	}

	private void updateEdgeShape() {

		Question question = (Question) questionCombo.getSelectedItem();

		int selectedQuestionIndex = qList.indexOf(question);
		logger.info("ShapePanel:SelectedQuestionIndex:"
				+ selectedQuestionIndex + " " + question.toString());
		for (int i = 0; i < question.getSelections().size(); i++) {
			Selection selection = question.getSelections().get(i);
			GraphQuestionSelectionPair graphQuestion = new GraphQuestionSelectionPair(question, selection);
			if (((Boolean) table.getValueAt(i, 0)) == true) {

				EdgeProperty edgeProperty = new EdgeProperty();
				edgeProperty.setProperty(EdgeProperty.EdgePropertyType.Shape);
				edgeProperty.setShape((EdgeProperty.EdgeShape) table
						.getValueAt(i, 2));
				edgeProperty.setVisible(true);
				edgesSelected[selectedQuestionIndex][i] = true;
				graphRenderer.addQAsettings(graphQuestion, edgeProperty);
				graphRenderer.updateGraphSettings();
			} else {
				edgesSelected[selectedQuestionIndex][i] = false;
				if (EdgeColorPanel.edgesSelected[selectedQuestionIndex][i] == false
						&& EdgeSizePanel.edgesSelected[selectedQuestionIndex][i] == false) {
					EdgeProperty edgeProperty = new EdgeProperty();
					edgeProperty
							.setProperty(EdgeProperty.EdgePropertyType.Shape);
					// edgeProperty.setShape((EdgeProperty.EdgeShape)table.getValueAt(i,
					// 2));
					edgeProperty.setVisible(false);
					graphRenderer.addQAsettings(graphQuestion, edgeProperty);
					graphRenderer.updateGraphSettings();
				}
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
