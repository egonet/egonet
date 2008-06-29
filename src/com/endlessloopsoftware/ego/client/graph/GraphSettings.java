package com.endlessloopsoftware.ego.client.graph;

import com.endlessloopsoftware.ego.client.graph.GraphSettingsEntry.GraphSettingType;
import edu.uci.ics.jung.graph.ArchetypeVertex;
import edu.uci.ics.jung.graph.Edge;
import edu.uci.ics.jung.graph.impl.UndirectedSparseEdge;

import java.util.*;
import java.awt.*;
import java.io.*;

import com.endlessloopsoftware.ego.Question;
import com.endlessloopsoftware.ego.Answer;

import com.endlessloopsoftware.ego.client.*;
import com.endlessloopsoftware.ego.client.graph.NodeProperty.NodeShape;
import com.endlessloopsoftware.ego.client.graph.EdgeProperty.EdgeShape;

import org.w3c.dom.*;

import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.*;
import javax.xml.transform.stream.*;

public class GraphSettings {

	private Map<ArchetypeVertex, NodeProperty> nodeSettingsMap = Collections
			.synchronizedMap(new HashMap<ArchetypeVertex, NodeProperty>());

	private Map<Edge, EdgeProperty> edgeSettingsMap = Collections
			.synchronizedMap(new HashMap<Edge, EdgeProperty>());

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

	private void init() throws Exception {
		int noOfAlters = EgoClient.interview.getNumAlters();
		// initialize nodes with default settings
		for (int i = 0; i < noOfAlters; i++) {
			String alterName = EgoClient.interview.getAlterList()[i];
			Color color = Color.RED;
			int size = 1;
			NodeShape shape = NodeShape.Circle;
			NodeProperty nodeProperty = new NodeProperty(alterName, color,
					shape, size);
			String toolTipText = getAlterInfo(i);
			nodeProperty.setToolTipText(toolTipText);
			nodeSettingsMap.put(renderer.getvertexArray()[i], nodeProperty);
		}
		// initialize edges with default settings
		renderer.getGraph().removeAllEdges();
		GraphData graphData = new GraphData();
		int[][] adjacencyMatrix = graphData.getAdjacencyMatrix();
		for (int i = 0; i < adjacencyMatrix.length; ++i) {
			for (int j = i + 1; j < adjacencyMatrix[i].length; ++j) {
				if (adjacencyMatrix[i][j] > 0) {
					UndirectedSparseEdge edge = new UndirectedSparseEdge(
							renderer.getvertexArray()[i], renderer
									.getvertexArray()[j]);
					renderer.getGraph().addEdge(edge);
					String label = ((Integer) EgoClient.interview.getStats().proximityMatrix[i][j])
							.toString();
					EdgeProperty edgeProperty = new EdgeProperty(label,
							Color.BLACK, EdgeShape.Line, 1);
					edgeProperty.setVisible(true);
					edgeSettingsMap.put(edge, edgeProperty);
				}
			}
		}
	}

	public void saveSettingsFile(File file) {
		try {
			DocumentBuilderFactory dbfac = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = dbfac.newDocumentBuilder();
			Document doc = docBuilder.newDocument();

			Element studyElement = doc.createElement("GraphSettings");
			String studyID = ((Long) EgoClient.study.getStudyId()).toString();
			studyElement.setAttribute("StudyId", studyID);
			doc.appendChild(studyElement);

			for(GraphSettingsEntry entry : QAsettings)
			    entry.writeEntryElement(doc, studyElement);
            
			TransformerFactory tranFactory = TransformerFactory.newInstance();
			Transformer aTransformer = tranFactory.newTransformer();
			aTransformer.setOutputProperty("indent", "yes");
			aTransformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

			Source src = new DOMSource(doc);
			Result dest = new StreamResult(file);
			aTransformer.transform(src, dest);

		} catch (Throwable ex) {
			throw new RuntimeException(ex);
		}
	}

	public int getNodeSize(ArchetypeVertex node) {
		NodeProperty nodeProperty = nodeSettingsMap.get(node);
		return nodeProperty.getSize();
	}

	public void setNodeSize(ArchetypeVertex node, int nodeSize) {
		nodeSettingsMap.get(node).setSize(nodeSize);
	}

	public NodeShape getNodeShape(ArchetypeVertex node) {
		NodeProperty nodeProperty = nodeSettingsMap.get(node);
		return nodeProperty.getShape();
	}

	public void setNodeShape(ArchetypeVertex node, NodeShape nodeShape) {
		nodeSettingsMap.get(node).setShape(nodeShape);
	}

	public Color getNodeColor(ArchetypeVertex node) {
		NodeProperty nodeProperty = nodeSettingsMap.get(node);
		return nodeProperty.getColor();
	}

	public void setNodeColor(ArchetypeVertex node, Color nodeColor) {
		nodeSettingsMap.get(node).setColor(nodeColor);
	}

	public String getNodeLabel(ArchetypeVertex node) {
		NodeProperty nodeProperty = nodeSettingsMap.get(node);
		return nodeProperty.getLabel();
	}

	public void setNodeLabel(ArchetypeVertex node, String nodeLabel) {
		nodeSettingsMap.get(node).setLabel(nodeLabel);
	}

	public String getNodeToolTipText(ArchetypeVertex node) {
		NodeProperty nodeProperty = nodeSettingsMap.get(node);
		return nodeProperty.getToolTipText();
	}

	public int getEdgeSize(Edge edge) {
		EdgeProperty edgeProperty = edgeSettingsMap.get(edge);
		return edgeProperty.getSize();
	}

	public void setEdgeSize(Edge edge, int edgeSize) {
		EdgeProperty edgeProperty = edgeSettingsMap.get(edge);
		if (edgeProperty != null) {
			edgeProperty.setSize(edgeSize);
		} else {
			edgeProperty = new EdgeProperty(Color.BLACK, EdgeShape.Line,
					edgeSize);
		}
		edgeSettingsMap.put(edge, edgeProperty);
	}

	public EdgeShape getEdgeShape(Edge edge) {
		EdgeProperty edgeProperty = edgeSettingsMap.get(edge);
		return edgeProperty.getShape();
	}

	public void setEdgeShape(Edge edge, EdgeShape edgeShape) {
		EdgeProperty edgeProperty = edgeSettingsMap.get(edge);
		if (edgeProperty != null) {
			edgeProperty.setShape(edgeShape);
		} else {
			edgeProperty = new EdgeProperty(Color.BLACK, edgeShape, 1);
		}
		edgeSettingsMap.put(edge, edgeProperty);
	}

	public Color getEdgeColor(Edge edge) {
		EdgeProperty edgeProperty = edgeSettingsMap.get(edge);
		return edgeProperty.getColor();
	}

	public void setEdgeColor(Edge edge, Color edgeColor) {
		EdgeProperty edgeProperty = edgeSettingsMap.get(edge);
		if (edgeProperty != null) {
			edgeProperty.setColor(edgeColor);
		} else {
			edgeProperty = new EdgeProperty(edgeColor, EdgeShape.Line, 1);
		}
		edgeSettingsMap.put(edge, edgeProperty);
	}

	public String getEdgeLabel(Edge edge) {
		EdgeProperty edgeProperty = edgeSettingsMap.get(edge);
		return edgeProperty.getLabel();
	}

	public void setEdgeLabel(Edge edge, String edgeLabel) {
		edgeSettingsMap.get(edge).setLabel(edgeLabel);
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
		//updateSettingsFile(entry);
		displaySettings();
	}

	private void displaySettings() {
		
		int size = QAsettings.size();
		for (int i = 0; i < size; i++) {
			GraphSettingsEntry entry = QAsettings.get(i);
			System.out.println(entry.toString());
		}
	}

	public Iterator<GraphSettingsEntry> getQAsettingsIterator() {
		return QAsettings.iterator();
	}

	private String getAlterInfo(int alterIndex) {
		String[] alterToolTip = new String[EgoClient.interview.getNumAlters()];
		for (int i = 0; i < alterToolTip.length; i++) {
			alterToolTip[i] = "<html>" + EgoClient.interview.getAlterList()[i]
					+ "<br>";
		}
		Answer[] answers = EgoClient.interview.get_answers();
		for (Answer answer : answers) {
			String questionTitle = "";
			String answerString = "";
			Question question = EgoClient.study.getQuestion(answer.questionId);
			if (question.questionType == Question.ALTER_QUESTION) {
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

	public Iterator<Edge> getEdgeIterator() {
		return edgeSettingsMap.keySet().iterator();
	}

	public static void writeSettings() {
		// Iterator iterator = getQAsettingsIterator();
	}

	public void emptyEdgeSettingsMap() {
		edgeSettingsMap.clear();
	}

	public boolean isEdgeVisible(Edge edge) {
		EdgeProperty edgeProperty = edgeSettingsMap.get(edge);
		return edgeProperty.isVisible();
	}

	public void setEdgeVisible(Edge edge, boolean b) {
		edgeSettingsMap.get(edge).setVisible(b);
	}

	public Map<Edge, EdgeProperty> getEdgeSettingsMap()
	{
		return edgeSettingsMap;
	}

	public void setEdgeSettingsMap(Map<Edge, EdgeProperty> edgeSettingsMap)
	{
		this.edgeSettingsMap = edgeSettingsMap;
	}

	public Map<ArchetypeVertex, NodeProperty> getNodeSettingsMap()
	{
		return nodeSettingsMap;
	}

	public void setNodeSettingsMap(
			Map<ArchetypeVertex, NodeProperty> nodeSettingsMap)
	{
		this.nodeSettingsMap = nodeSettingsMap;
	}

	public java.util.List<GraphSettingsEntry> getQAsettings()
	{
		return this.QAsettings;
	}

	public void setQAsettings(java.util.List<GraphSettingsEntry> asettings)
	{
		this.QAsettings = asettings;
	}
}
