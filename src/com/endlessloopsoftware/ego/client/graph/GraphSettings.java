package com.endlessloopsoftware.ego.client.graph;

import edu.uci.ics.jung.graph.ArchetypeVertex;
import java.util.*;
import java.util.List;
import java.awt.*;
import java.io.*;

import com.endlessloopsoftware.ego.Question;
import com.endlessloopsoftware.ego.Answer;
import org.egonet.util.listbuilder.Selection;

import com.endlessloopsoftware.ego.client.*;
import com.endlessloopsoftware.ego.client.graph.NodeProperty.NodeShape;

public class GraphSettings {

	private Map<ArchetypeVertex, NodeProperty> propertySettingsMap = Collections
			.synchronizedMap(new HashMap<ArchetypeVertex, NodeProperty>());

	private java.util.List<GraphSettingsEntry> QAsettings = Collections
			.synchronizedList(new ArrayList<GraphSettingsEntry>());

	GraphRenderer renderer;

	public GraphSettings(GraphRenderer renderer) {
		this.renderer = renderer;
		try {
			init();
		} catch (Exception e) {
			e.printStackTrace(System.err);
		}
	}

	private void init() throws Exception{
		int noOfAlters = EgoClient.interview.getNumAlters();
		for (int i = 0; i < noOfAlters; i++) {
			String alterName = EgoClient.interview.getAlterList()[i];
			Color color = Color.RED;
			int size = 1;
			NodeShape shape = NodeShape.Circle;
			NodeProperty nodeProperty = new NodeProperty(alterName, color,
					shape, size);
			String toolTipText = getAlterInfo(i);
			nodeProperty.setToolTipText(toolTipText);
			propertySettingsMap
					.put(renderer.get_vertexArray()[i], nodeProperty);

		}
	}

	public int getNodeSize(ArchetypeVertex node) {
		NodeProperty nodeProperty = propertySettingsMap.get(node);
		return nodeProperty.getSize();
	}

	public void setNodeSize(ArchetypeVertex node, int nodeSize) {
		propertySettingsMap.get(node).setSize(nodeSize);
	}

	public NodeShape getNodeShape(ArchetypeVertex node) {
		NodeProperty nodeProperty = propertySettingsMap.get(node);
		return nodeProperty.getShape();
	}

	public void setNodeShape(ArchetypeVertex node, NodeShape nodeShape) {
		propertySettingsMap.get(node).setShape(nodeShape);
	}

	public Color getNodeColor(ArchetypeVertex node) {
		NodeProperty nodeProperty = propertySettingsMap.get(node);
		return nodeProperty.getColor();
	}

	public void setNodeColor(ArchetypeVertex node, Color nodeColor) {
		propertySettingsMap.get(node).setColor(nodeColor);
	}

	public String getNodeLabel(ArchetypeVertex node) {
		NodeProperty nodeProperty = propertySettingsMap.get(node);
		return nodeProperty.getLabel();
	}

	public void setNodeLabel(ArchetypeVertex node, String nodeLabel) {
		propertySettingsMap.get(node).setLabel(nodeLabel);
	}

	public String getNodeToolTipText(ArchetypeVertex node) {
		NodeProperty nodeProperty = propertySettingsMap.get(node);
		return nodeProperty.getToolTipText();
	}

	public void addQAsetting(GraphQuestion graphQuestion,
			NodeProperty nodeProperty) {
		GraphSettingsEntry entry = new GraphSettingsEntry(graphQuestion,
				nodeProperty, GraphSettingType.Node);
		QAsettings.add(entry);
		displaySettings();
	}

	public void addQAsetting(GraphQuestion graphQuestion,
			EdgeProperty edgeProperty) {
		GraphSettingsEntry entry = new GraphSettingsEntry(graphQuestion,
				edgeProperty, GraphSettingType.Edge);
		QAsettings.add(entry);
		displaySettings();
	}
	
	private void displaySettings() {

		System.out
				.println("---------------QA SETTINGS CONTENTS ----------------------------");
		int size = QAsettings.size();
		for (int i = 0; i < size; i++) {
			GraphSettingsEntry entry = QAsettings.get(i);
			System.out.println(entry.toString());
		}
		System.out
				.println("\n-------------------------------------------------------");
	}

	public Iterator getQAsettingsIterator() {
		return QAsettings.iterator();
	}

	private String getAlterInfo(int alterIndex) {
		String[] alterToolTip = new String[EgoClient.interview.getNumAlters()];
		for (int i = 0; i < alterToolTip.length; i++) {
			alterToolTip[i] = "<html>" + EgoClient.interview.getAlterList()[i] + "<br>";
		}
		Answer[] answers = EgoClient.interview.get_answers();
		for (Answer answer : answers) {
			String questionTitle = "";
			String answerString = "";
			Question question = EgoClient.study.getQuestion(answer.questionId);
			if (question.questionType == Question.ALTER_QUESTION) {
				// System.out.println(answer.getString());
				questionTitle = question.title;
				answerString = answer.string;
				int[] alters = answer.getAlters();
				for (int alter : alters) {
					alterToolTip[alter] += questionTitle + " : " + answerString
							+ "<br>";
				}
			}

		}
		return alterToolTip[alterIndex];

	}
}
