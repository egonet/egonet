package com.endlessloopsoftware.ego.client.graph;

import org.jdesktop.layout.GroupLayout;
import javax.swing.*;
import javax.swing.table.TableColumnModel;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ItemEvent;
import java.util.*;
import java.awt.event.*;

import org.egonet.util.listbuilder.Selection;
import org.egonet.util.table.LabelTableModel;
import org.egonet.util.table.LabelRenderer;
import com.endlessloopsoftware.ego.*;
import com.endlessloopsoftware.ego.client.EgoClient;
import com.endlessloopsoftware.ego.client.graph.NodeProperty.NodeShape;

public class NodeLabelPanel extends JPanel {

	private JComboBox questionCombo;

	private JLabel questionLabel;

	private JRadioButton questionRadio;

	private JRadioButton alterNamesRadio;

	private JButton applyButton;

	private LabelTableModel tableModel;

	private JTable table;

	private GroupLayout layout;

	private GraphRenderer graphRenderer;

	List<Selection> selectionList = new ArrayList<Selection>();

	public NodeLabelPanel(GraphRenderer renderer) {
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
		Study study = EgoClient.interview.getStudy();
		QuestionList questionList = study.getQuestions();
		Map<Long, Question> questionMap = questionList.getQuestionMap();
		for (Long key : questionMap.keySet()) {
			Question currentQuestion = questionMap.get(key);
			int questionType = currentQuestion.questionType;
			if (questionType == Question.ALTER_QUESTION) {
				// populate the list box with only questions that have choices
				// as answers
				if (currentQuestion.answerType == Question.CATEGORICAL
						|| currentQuestion.answerType == Question.TEXT)
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
		Question question;
		question = (Question) questionCombo.getSelectedItem();

		// System.out.println("Question examining:" + question.UniqueId);

		if (question.answerType == Question.CATEGORICAL) {
			int category = Question.ALTER_QUESTION;
			int noOfRows = question.selections.length;
			Selection[] rowData = new Selection[noOfRows];
			/* change the list of selections based on the selected question */
			if (!selectionList.isEmpty()) {
				selectionList.removeAll(selectionList);
			}
			for (Selection selection : question.selections) {
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
			Answer[] answers = EgoClient.interview.get_answers();
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
			int noOfAlters = EgoClient.interview.getNumAlters();
			for (int i = 0; i < noOfAlters; i++) {
				String alterName = EgoClient.interview.getAlterList()[i];
				graphRenderer.updateGraphSettings(alterName, i, 1);
			}
		} else {
			Question question = (Question) questionCombo.getSelectedItem();
			if (question.answerType == Question.CATEGORICAL) {
				for (Selection selection : question.selections) {
					GraphQuestion graphQuestion = new GraphQuestion(question,
							selection, Question.ALTER_QUESTION);
					NodeProperty nodeProperty = new NodeProperty();
					nodeProperty.setLabel(selection.getString());
					nodeProperty.setProperty(NodeProperty.Property.Label);
					graphRenderer.addQAsettings(graphQuestion, nodeProperty);
					graphRenderer.updateGraphSettings();
				}
			} else if (question.answerType == Question.TEXT) {
				System.out.println("Applying labels for text questions");
				for (Selection selection : selectionList) {
					NodeProperty nodeProperty = new NodeProperty();
					nodeProperty.setLabel(selection.getString());
					nodeProperty.setProperty(NodeProperty.Property.Label);
					GraphQuestion graphQuestion = new GraphQuestion(question,
							selection, Question.ALTER_QUESTION);
					graphRenderer.addQAsettings(graphQuestion, nodeProperty);
					graphRenderer.updateGraphSettings();
				}
			}
		}
		graphRenderer.getVv().repaint();
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
