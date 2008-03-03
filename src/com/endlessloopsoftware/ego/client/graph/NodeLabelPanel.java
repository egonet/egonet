package com.endlessloopsoftware.ego.client.graph;

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

	private GraphData graphData;

	List<Selection> selectionList = new ArrayList<Selection>();

	public NodeLabelPanel(GraphRenderer renderer) {
		this.graphRenderer = renderer;
		layout = new GroupLayout(this);
		this.setLayout(layout);
		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(true);
		graphData = new GraphData();
		createComponents();
	}

	private void createComponents() {

//		questionLabel = new JLabel("Label by question");
//		questionLabel.setVisible(true);

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
					if(applyButton != null)
						applyButton.setEnabled(true);
				}
			}
		});

		alterNamesRadio.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent e) {
				if (alterNamesRadio.isSelected() == true) {
					//table.setEnabled(false);
					questionCombo.setEnabled(false);
					if(applyButton!= null )
						applyButton.setEnabled(false);
					updateNodeLabels(true);
				}
			}
		});

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
				if (currentQuestion.selections.length >= 1)
					qList.add(currentQuestion);
			}
		}
		questionCombo = new JComboBox(qList.toArray());
		questionCombo.setVisible(true);
		questionCombo.setEnabled(false);
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

		// show only questin combo and label
		GroupLayout.SequentialGroup hGroup = layout.createSequentialGroup();
		hGroup.addGroup(layout.createParallelGroup()
				.addComponent(questionRadio).addComponent(questionCombo)
				.addComponent(alterNamesRadio));

		layout.setHorizontalGroup(hGroup);

		GroupLayout.SequentialGroup vGroup = layout.createSequentialGroup();
		vGroup.addGroup(layout.createParallelGroup(
				GroupLayout.Alignment.BASELINE).addComponent(questionRadio));
		vGroup.addGroup(layout.createParallelGroup(
				GroupLayout.Alignment.BASELINE).addComponent(questionCombo));
		vGroup.addGroup(layout.createParallelGroup(
				GroupLayout.Alignment.BASELINE).addComponent(alterNamesRadio));
		layout.setVerticalGroup(vGroup);
	}

	private void createTable() {
		Question question = (Question) questionCombo.getSelectedItem();
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

		// create apply button
		applyButton = new JButton("Apply Label");
		applyButton.setVisible(true);
		applyButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				updateNodeLabels(false);
			}
		});
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
			for (Selection selection : question.selections) {
				List<Integer> alterList = graphData.getAlterNumbers(question,
						selection);
				
				GraphQuestion graphQuestion = new GraphQuestion(question, selection, Question.ALTER_QUESTION);
				NodeProperty nodeProperty = new NodeProperty();
				nodeProperty.setLabel(selection.getString());
				nodeProperty.setProperty(NodeProperty.Property.Label);
				graphRenderer.addQAsettings(graphQuestion, nodeProperty);
				graphRenderer.updateGraphSettings();
			}
		}
	graphRenderer.getVv().repaint();
	}

	private void drawPanel() {
		this.removeAll();
		GroupLayout.SequentialGroup hGroup = layout.createSequentialGroup();
		hGroup.addGroup(layout.createParallelGroup()
				.addComponent(questionRadio).addComponent(questionCombo)
				.addComponent(table.getTableHeader()).addComponent(table)
				.addComponent(applyButton).addComponent(alterNamesRadio));

		layout.setHorizontalGroup(hGroup);

		GroupLayout.SequentialGroup vGroup = layout.createSequentialGroup();
		vGroup.addGroup(layout.createParallelGroup(
				GroupLayout.Alignment.BASELINE).addComponent(questionRadio));
		vGroup.addGroup(layout.createParallelGroup(
				GroupLayout.Alignment.BASELINE).addComponent(questionCombo));
		vGroup.addGroup(layout.createParallelGroup(
				GroupLayout.Alignment.BASELINE).addComponent(
				table.getTableHeader()));
		vGroup.addGroup(layout.createParallelGroup(
				GroupLayout.Alignment.BASELINE).addComponent(table));
		vGroup.addGroup(layout.createParallelGroup(
				GroupLayout.Alignment.BASELINE).addComponent(applyButton));
		vGroup.addGroup(layout.createParallelGroup(
				GroupLayout.Alignment.BASELINE).addComponent(alterNamesRadio));

		layout.setVerticalGroup(vGroup);
	}
}
