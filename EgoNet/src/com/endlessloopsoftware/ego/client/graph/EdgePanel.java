package com.endlessloopsoftware.ego.client.graph;

import org.jdesktop.layout.GroupLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.util.*;
import java.awt.event.ItemEvent;
import javax.swing.table.*;

import org.egonet.util.table.*;

import org.jdesktop.layout.GroupLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableModel;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.*;

import com.endlessloopsoftware.ego.Question;
import com.endlessloopsoftware.ego.QuestionList;
import com.endlessloopsoftware.ego.Study;
import com.endlessloopsoftware.ego.client.EgoClient;
import com.endlessloopsoftware.ego.client.graph.GraphQuestion;
import org.egonet.util.listbuilder.Selection;
import edu.uci.ics.jung.utils.Pair;

public class EdgePanel extends JPanel implements TableModelListener {

	private GroupLayout layout;

	GraphRenderer graphRenderer;

	List<GraphQuestion> graphQuestions = new ArrayList<GraphQuestion>();

	String[] sizes = { "1", "2", "3", "4", "5" };

	// Components used
	JLabel questionLabel;

	JComboBox questionCombo;

	JLabel selectionLabel;

	// For table entries

	JTable table;

	JButton colorButton;

	JComboBox sizeCombo;

	JComboBox shapeCombo;

	ColorChooserEditor colorChooser;

	List<Selection> selectionList = new ArrayList<Selection>();

	public EdgePanel(GraphRenderer gr) {

		layout = new GroupLayout(this);
		this.setLayout(layout);

		layout.setAutocreateGaps(true);
		layout.setAutocreateContainerGaps(true);

		this.graphRenderer = gr;
		this.graphQuestions = graphRenderer.getGQuestions();

		createComponents();
	}

	/**
	 * implemented method for TableModelListener
	 */
	public void tableChanged(TableModelEvent e) {
		int row = e.getFirstRow();
		int column = e.getColumn();
		TableModel model = (TableModel) e.getSource();
		updateMapEntry(model, row, column);
	}

	/**
	 * Updates map with newly selected properties
	 * 
	 * @param int
	 *            row: row at which a property was changed in the table int
	 *            column: corresponds to which property was chnged TableModel
	 *            tableModel: to get table properties
	 */
	private void updateMapEntry(TableModel tableModel, int row, int column) {
		// Get property settings in modified row
		String sizeStr = (String) tableModel.getValueAt(row, 4);
		int size = sizeStr != null ? Integer.parseInt(sizeStr) : -1;
		EdgeProperty.EdgeShape shape = (EdgeProperty.EdgeShape) tableModel
				.getValueAt(row, 3);
		Color color = (Color) tableModel.getValueAt(row, 2);

		// create a graph question with the question in question combo and
		// selection in modified row of the table
		Question question = (Question) questionCombo.getSelectedItem();
		int category = Question.ALTER_PAIR_QUESTION;
		Selection selection = (Selection) tableModel.getValueAt(row, 1);
		GraphQuestion gq = new GraphQuestion(question, selection, category);

		// look for entry in Map
		EdgeProperty currentProperty = (EdgeProperty) GraphRenderer.propertyMap
				.get(gq);

		// add/update entry only if selected
		if (tableModel.getValueAt(row, 0) == Boolean.TRUE) {
			// if entry not found
			if (currentProperty == null) {
				currentProperty = new EdgeProperty();
				currentProperty.setShape(shape);
				currentProperty.setSize(size);
				currentProperty.setColor(color);
				System.out.println("Entry will be added anew: "
						+ currentProperty);
			} else {
				switch (column) {
				case 2: // property modified is Color: Update Color only
					currentProperty.setColor(color);
					break;
				case 3: // propery modified is Shape : Update Shape only
					currentProperty.setShape(shape);
					break;
				case 4: // property modified is size : Update size only
					currentProperty.setSize(size);
					break;
				default:
					System.out.println("Invalid update attempted");
					break;
				}
				System.out.println("Re-added entry to map: " + currentProperty);
			}
			GraphRenderer.propertyMap.put(gq, currentProperty);

		} else {
			GraphRenderer.propertyMap.remove(gq);
		}

		// Add alter list for Graph Question also as a part of the
		// datastructure
		GraphData graphData = new GraphData();
		List<Integer> alterList = graphData.getAlterNumbers(gq);
		gq.setAlterList(alterList);
		// Get list pairs of alters for the GraphQuestion and create
		// corresponding vertex pair map

		displayPropertyMap();

		updateEdgeMap();

	}

	/**
	 * updates the GraphRenderer's edge Map
	 * 
	 */
	private void updateEdgeMap() {
		GraphData graphData = new GraphData();
		GraphRenderer.vertexPair.removeAll(GraphRenderer.vertexPair);
		GraphRenderer.edgePropertyList.clear();

		for (GraphQuestion gq : GraphRenderer.propertyMap.keySet()) {
			if (gq.getCategory() == Question.ALTER_PAIR_QUESTION) {
				List<Pair> vPair = graphData.getAlterPairs(gq);
				EdgeProperty edgeProperty = (EdgeProperty) GraphRenderer.propertyMap
						.get(gq);
				for (Pair pair : vPair) {
					GraphRenderer.vertexPair.add(pair);
					GraphRenderer.edgePropertyList.add(edgeProperty);
				}
			}

		}
		try {
		graphRenderer.createNodeEdge();
		} catch(Exception ex)
		{
			ex.printStackTrace(System.err);
		}
		graphRenderer.getVv().repaint();
	}

	/**
	 * Displays contents of the property Map Used for Tesing the process of
	 * adding values to Map
	 * 
	 */
	private void displayPropertyMap() {
		String question = "";
		String selection = "";
		String propValues = "";
		NodeProperty np;
		EdgeProperty ep;
		System.out
				.println("---------------MAP CONTENTS ----------------------------");
		for (Map.Entry<GraphQuestion,GraphProperty> entry : GraphRenderer.propertyMap.entrySet()) {
			question = entry.getKey().toString();
			selection = entry.getKey().getSelection().getString();
			if (entry.getKey().getCategory() == Question.ALTER_QUESTION
					|| entry.getKey().getCategory() == 0) {
				np = (NodeProperty) entry.getValue();
				if (np != null) {
					propValues = np.toString();
				}
			} else {
				ep = (EdgeProperty) entry.getValue();
				if (ep != null) {
					propValues = ep.toString();
				}
			}
			System.out.print("\n" + question + " | " + propValues + " | ");
			for (int alter : entry.getKey().getAlterList()) {
				System.out.print(+alter + ",");
			}
		}
		System.out
				.println("\n-------------------------------------------------------");
	}

	/**
	 * Creates a combo box each for Alterquestions and possible selections for
	 * the question. It also creates other options such as color, size, shape
	 */
	private void createComponents() {

		// create question label and combo pair: populated with
		// alter/alterpair Qs
		questionLabel = new JLabel("Question: ");
		questionLabel.setOpaque(true);

		List<Question> qList = new ArrayList<Question>();
		Study study = EgoClient.interview.getStudy();
		QuestionList questionList = study.getQuestions();

		Map<Long, Question> questionMap = questionList.getQuestionMap();
		for (Long key : questionMap.keySet()) {
			Question currentQuestion = questionMap.get(key);
			int questionType = currentQuestion.questionType;
			if (questionType == Question.ALTER_PAIR_QUESTION) {
				// populate the list box with only questions that have choices
				// as answers
				if (currentQuestion.selections.length >= 1)
					qList.add(currentQuestion);
			}
		}

		questionCombo = new JComboBox(qList.toArray());
		questionCombo.setPreferredSize(new Dimension(20, 20));
		questionCombo.setMaximumSize(new Dimension(20, 30));

		questionCombo.setAutoscrolls(true);
		questionCombo.setSelectedIndex(0);

		questionCombo.addItemListener(new java.awt.event.ItemListener() {
			public void itemStateChanged(ItemEvent e) {

				if (e.getStateChange() == ItemEvent.SELECTED) {
					createTable();
					drawPanel();
				}
			}
		});

		selectionLabel = new JLabel("Answers: ");
		selectionLabel.setOpaque(true);

		sizeCombo = new JComboBox(sizes);
		sizeCombo.setPreferredSize(new Dimension(20, 20));
		sizeCombo.setMaximumSize(new Dimension(20, 30));
		sizeCombo.setSelectedIndex(0);

		shapeCombo = new JComboBox(EdgeProperty.EdgeShape.values());
		shapeCombo.setPreferredSize(new Dimension(20, 20));
		shapeCombo.setMaximumSize(new Dimension(20, 30));
		shapeCombo.setSelectedIndex(0);

		createTable();
		drawPanel();
	}

	/**
	 * Creates table and sets the following properties: 1) The initial values of
	 * the controls based on entries in map 2) Sets table model to instance of
	 * NodePanel 3) Sets editors for the editable controls 3) Sets renderers for
	 * each cell
	 */
	private void createTable() {
		Question question = (Question) questionCombo.getSelectedItem();
		int category = Question.ALTER_PAIR_QUESTION;
		int noOfRows = question.selections.length;
		/* change the list of selections based on the selected question */
		if (!selectionList.isEmpty()) {
			selectionList.removeAll(selectionList);
		}
		for (Selection selection : question.selections) {
			selectionList.add(selection);
		}

		// populate controls values in map or with default settings of no entry
		// exists
		Object[][] tableData = new Object[noOfRows][5];
		for (int i = 0; i < noOfRows; i++) {
			Selection selection = selectionList.get(i);
			GraphQuestion gq = new GraphQuestion(question, selection, category);

			// Test code
			GraphData gd = new GraphData();
			gd.getAlterPairs(gq);
			// end of test code

			EdgeProperty currentProperty = (EdgeProperty) GraphRenderer.propertyMap
					.get(gq);

			if (currentProperty != null) {
				tableData[i][0] = Boolean.TRUE;
				tableData[i][1] = selection;
				tableData[i][2] = currentProperty.getColor();
				tableData[i][3] = currentProperty.getShape();
				tableData[i][4] = (new Integer(currentProperty.getSize()))
						.toString();

			} else {
				tableData[i][0] = Boolean.FALSE;
				tableData[i][1] = selection;
				tableData[i][2] = Color.BLACK;
				tableData[i][3] = EdgeProperty.EdgeShape.Line;
				tableData[i][4] = new String("1");

			}
		}

		table = new JTable(new TableModelBoolean(tableData));
		table.getModel().addTableModelListener(this);
		table.setRowHeight(25);
		table.setBorder(BorderFactory.createCompoundBorder(BorderFactory
				.createLineBorder(Color.black), getBorder()));
		TableColumnModel columnModel = table.getColumnModel();

		// Size Column - set renderer and editor
		DefaultCellEditor sizeEditor = new DefaultCellEditor(sizeCombo);
		columnModel.getColumn(4).setCellEditor(sizeEditor);
		columnModel.getColumn(4).setCellRenderer(
				new TableComboBoxRenderer(sizes));

		// Shape column - set renderer and editor
		DefaultCellEditor shapeEditor = new DefaultCellEditor(shapeCombo);
		columnModel.getColumn(3).setCellEditor(shapeEditor);
		columnModel.getColumn(3).setCellRenderer(
				new TableComboBoxRenderer(EdgeProperty.EdgeShape.values()));

		// Color column - Set editor and renderer
		TableCellEditor colorEditor = new ColorEditor();
		columnModel.getColumn(2).setCellEditor(colorEditor);
		ColorRenderer colorButtonRenderer = new ColorRenderer(true);
		columnModel.getColumn(2).setCellRenderer(colorButtonRenderer);

		// selection column - not editable, set renderer only
		LabelRenderer selectionRenderer = new LabelRenderer();
		columnModel.getColumn(1).setCellRenderer(selectionRenderer);

		columnModel.getColumn(0).setPreferredWidth(30);
		columnModel.getColumn(1).setPreferredWidth(250);
		columnModel.getColumn(2).setPreferredWidth(30);
		columnModel.getColumn(3).setPreferredWidth(100);
		columnModel.getColumn(4).setPreferredWidth(50);
	}

	/**
	 * Fits the components belonging to this panel using Group Layout
	 * 
	 */
	private void drawPanel() {
		this.removeAll();

		GroupLayout.SequentialGroup hGroup = layout.createSequentialGroup();
		hGroup.add(layout.createParallelGroup()
				.add(questionLabel).add(questionCombo)
				.add(selectionLabel).add(
						table.getTableHeader()).add(table));
		layout.setHorizontalGroup(hGroup);

		GroupLayout.SequentialGroup vGroup = layout.createSequentialGroup();
		vGroup.add(layout.createParallelGroup(
				GroupLayout.BASELINE).add(questionLabel));
		vGroup.add(layout.createParallelGroup(
				GroupLayout.BASELINE).add(questionCombo));
		vGroup.add(layout.createParallelGroup(
				GroupLayout.BASELINE).add(selectionLabel));
		vGroup.add(layout.createParallelGroup(
				GroupLayout.BASELINE).add(
				table.getTableHeader()));
		vGroup.add(layout.createParallelGroup(
				GroupLayout.BASELINE).add(table));
		layout.setVerticalGroup(vGroup);

	}

}