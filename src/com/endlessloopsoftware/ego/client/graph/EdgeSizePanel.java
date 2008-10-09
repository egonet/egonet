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

import com.endlessloopsoftware.ego.*;
import com.endlessloopsoftware.ego.client.EgoClient;

public class EdgeSizePanel extends JPanel {

	private JLabel questionLabel;

	private JComboBox questionCombo;

	private JComboBox sizeCombo;

	private JTable table;

	private GroupLayout layout;

	private GraphRenderer graphRenderer;

	private JButton applyButton;


	String[] sizes = { "1", "2", "3", "4", "5" };

	List<Selection> selectionList = new ArrayList<Selection>();

	public static boolean[][] edgesSelected;

	List<Question> qList = new ArrayList<Question>();

	private EgoClient egoClient;

	public EdgeSizePanel(EgoClient egoClient, GraphRenderer renderer) {
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
		//List<Question> qList = new ArrayList<Question>();
		Study study = egoClient.getInterview().getStudy();
		QuestionList questionList = study.getQuestions();
		Map<Long, Question> questionMap = questionList.getQuestionMap();
		for (Long key : questionMap.keySet()) {
			Question currentQuestion = questionMap.get(key);
			int questionType = currentQuestion.questionType;
			if (questionType == Question.ALTER_PAIR_QUESTION) {
				// populate the list box with only questions that have choices
				// as answers
				if (currentQuestion.getSelections().length >= 1)
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

		// create size combo for table cell
		sizeCombo = new JComboBox(sizes);
		sizeCombo.setPreferredSize(new Dimension(20, 20));
		sizeCombo.setMaximumSize(new Dimension(20, 30));
		sizeCombo.setSelectedIndex(0);

		// create apply button
		applyButton = new JButton("Apply Size");
		applyButton.setVisible(true);
		applyButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				updateEdgeSize();
			}
		});

		Question question = (Question) questionCombo.getSelectedItem();
		edgesSelected = new boolean[qList.size()][question.getSelections().length];
		for(int i = 0; i<qList.size(); i++) {
			for (int j =0; j<question.getSelections().length;j++) {
				edgesSelected[i][j] = false;
			}
		}
		
		createTable();
		drawPanel();

	}

	private void createTable() {
		Question question = (Question) questionCombo.getSelectedItem();
		int noOfRows = question.getSelections().length;
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
		int noOfSizes = question.getSelections().length;
		for (int i = 1; i <= noOfSizes; i++) {
			if (i < sizes.length) {
				String num = (new Integer(i)).toString();
				rowData[i - 1][2] = num;
			} else {
				String num = (new Integer(i - sizes.length)).toString();
				rowData[i - 1][2] = num;
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

		DefaultCellEditor sizeEditor = new DefaultCellEditor(sizeCombo);
		columnModel.getColumn(2).setCellEditor(sizeEditor);
		columnModel.getColumn(2).setCellRenderer(
				new TableComboBoxRenderer(sizes));

		columnModel.getColumn(0).setMaxWidth(50);
		columnModel.getColumn(1).setMaxWidth(200);
		columnModel.getColumn(2).setMaxWidth(80);
	}

	private void updateEdgeSize() {

		Question question = (Question) questionCombo.getSelectedItem();

		int selectedQuestionIndex = qList.indexOf(question);
		
		System.out.println("SizePanel:SelectedQuestionIndex:" + selectedQuestionIndex  + " " + question.toString());

		for (int i = 0; i < question.getSelections().length; i++) {
			Selection selection = question.getSelections()[i];
			GraphQuestionSelectionPair graphQuestion = new GraphQuestionSelectionPair(question,
					selection, Question.ALTER_PAIR_QUESTION);

			if (((Boolean) table.getValueAt(i, 0)) == true) {
				EdgeProperty edgeProperty = new EdgeProperty();
				edgeProperty.setProperty(EdgeProperty.EdgePropertyType.Size);
				String sizeStr = (String) table.getValueAt(i, 2);
				int size = sizeStr != null ? Integer.parseInt(sizeStr) : -1;
				edgeProperty.setSize(size);
				edgeProperty.setVisible(true);
				edgesSelected[selectedQuestionIndex][i] = true;
				graphRenderer.addQAsettings(graphQuestion, edgeProperty);
				graphRenderer.updateGraphSettings();
			} else {
				
				edgesSelected[selectedQuestionIndex][i] = false;
				if (EdgeColorPanel.edgesSelected[selectedQuestionIndex][i] == false
						&& EdgeShapePanel.edgesSelected[selectedQuestionIndex][i] == false) {
					EdgeProperty edgeProperty = new EdgeProperty();
					edgeProperty.setProperty(EdgeProperty.EdgePropertyType.Size);
//					String sizeStr = (String) table.getValueAt(i, 2);
//					int size = sizeStr != null ? Integer.parseInt(sizeStr) : -1;
//					edgeProperty.setSize(size);
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
