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


import javax.swing.*;
import javax.swing.table.*;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.util.*;

import org.jdesktop.layout.GroupLayout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.egonet.gui.interview.EgoClient;
import org.egonet.gui.table.*;
import org.egonet.model.QuestionList;
import org.egonet.model.Study;
import org.egonet.model.answer.*;
import org.egonet.model.question.AlterQuestion;
import org.egonet.model.question.Question;
import org.egonet.model.question.Selection;


public class NodeColorPanel extends JPanel {

	final private static Logger logger = LoggerFactory.getLogger(NodeColorPanel.class);
	
	private JLabel questionLabel;

	private JComboBox<Question> questionCombo;

	ColorChooserEditor colorChooser;

	private JTable table;

	private GroupLayout layout;

	private GraphRenderer graphRenderer;

	private JButton applyButton;

	List<Selection> selectionList = new ArrayList<Selection>();

	private EgoClient egoClient;

	public NodeColorPanel(EgoClient egoClient, GraphRenderer renderer) {
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
			if (currentQuestion instanceof AlterQuestion) {
				// populate the list box with only questions that have choices
				// as answers
				if (currentQuestion.answerType.equals(CategoricalAnswer.class)
						|| currentQuestion.answerType.equals(TextAnswer.class))
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

		// create apply button
		applyButton = new JButton("Apply Colors");
		applyButton.setVisible(true);
		applyButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				updateNodeColor();
			}
		});

		createTable();
		drawPanel();

	}

	private Object[][] getRowData() {
		if(questionCombo.getSelectedIndex() == -1)
			return new Object[0][0];
		
		Question question = (Question) questionCombo.getSelectedItem();

		// logger.info("Question examining:" + question.UniqueId);

		if (question.answerType.equals(CategoricalAnswer.class)) {
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
			int noOfColors = question.getSelections().size();
			List<Color> colors = pick(noOfColors);
			
			for (int i = 0; i < noOfColors; i++) {
				rowData[i][1] = colors.get(i);
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
			int noOfColors = selectionList.size();
			List<Color> colors = pick(noOfColors);
			
			for (int i = 0; i < noOfColors && i < colors.size(); i++) {
				rowData[i][1] = colors.get(i);
			}
			return rowData;
		}
	}
	
	public static List<Color> pick(int num) {
        List<Color> colors = new ArrayList<Color>();
        
        if(num == 1)
        	colors.add(new Color(255, 0, 0));
        if (num < 2)
                return colors;
        
        float dx = 1.0f / (float) (num - 1);
        for (int i = 0; i < num; i++) {
                colors.add(get(i * dx));
        }
        return colors;
}

public static Color get(float x) {
        float r = 0.0f;
        float g = 0.0f;
        float b = 1.0f;
        if (x >= 0.0f && x < 0.2f) {
                x = x / 0.2f;
                r = 0.0f;
                g = x;
                b = 1.0f;
        } else if (x >= 0.2f && x < 0.4f) {
                x = (x - 0.2f) / 0.2f;
                r = 0.0f;
                g = 1.0f;
                b = 1.0f - x;
        } else if (x >= 0.4f && x < 0.6f) {
                x = (x - 0.4f) / 0.2f;
                r = x;
                g = 1.0f;
                b = 0.0f;
        } else if (x >= 0.6f && x < 0.8f) {
                x = (x - 0.6f) / 0.2f;
                r = 1.0f;
                g = 1.0f - x;
                b = 0.0f;
        } else if (x >= 0.8f && x <= 1.0f) {
                x = (x - 0.8f) / 0.2f;
                r = 1.0f;
                g = 0.0f;
                b = x;
        }
        return new Color(r, g, b);
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

		// populate the colors

		table = new JTable(new PropertyTableModel(rowData));
		table.setPreferredScrollableViewportSize(table.getPreferredSize());
		table.setRowHeight(25);
		table.setVisible(true);
		table.setBorder(BorderFactory.createCompoundBorder(BorderFactory
				.createLineBorder(Color.black), getBorder()));

		TableColumnModel columnModel = table.getColumnModel();
		LabelRenderer selectionRenderer = new LabelRenderer();
		columnModel.getColumn(0).setCellRenderer(selectionRenderer);

		TableCellEditor colorEditor = new ColorEditor();
		columnModel.getColumn(1).setCellEditor(colorEditor);
		ColorRenderer colorButtonRenderer = new ColorRenderer(true);
		columnModel.getColumn(1).setCellRenderer(colorButtonRenderer);

		columnModel.getColumn(0).setMaxWidth(200);
		columnModel.getColumn(1).setMaxWidth(150);
	}

	private void updateNodeColor() {
		Question question = (Question) questionCombo.getSelectedItem();
		logger.info("Question combo" +question.UniqueId);
		
		if (question.answerType.equals(CategoricalAnswer.class)) {
			for (int i = 0; i < question.getSelections().size(); i++) {
				Selection selection = question.getSelections().get(i);

				GraphQuestionSelectionPair graphQuestion = new GraphQuestionSelectionPair(question,selection);
				NodeProperty nodeProperty = new NodeProperty();
				nodeProperty.setColor((Color) table.getValueAt(i, 1));
				nodeProperty.setProperty(NodeProperty.NodePropertyType.Color);
				graphRenderer.addQAsettings(graphQuestion, nodeProperty);
				graphRenderer.updateGraphSettings();
			}
		}else if (question.answerType.equals(TextAnswer.class)) {
			logger.info("Applying labels for text questions");
			for (int i =0;i < selectionList.size() ; i++) {
				Selection selection = selectionList.get(i);
				NodeProperty nodeProperty = new NodeProperty();
				nodeProperty.setColor((Color) table.getValueAt(i, 1));
				nodeProperty.setProperty(NodeProperty.NodePropertyType.Color);
				GraphQuestionSelectionPair graphQuestion = new GraphQuestionSelectionPair(question, selection);
				graphRenderer.addQAsettings(graphQuestion, nodeProperty);
				graphRenderer.updateGraphSettings();
			}
		}
		// graphRenderer.getVv().repaint();
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
