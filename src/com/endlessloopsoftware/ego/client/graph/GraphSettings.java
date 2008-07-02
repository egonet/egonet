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
import com.endlessloopsoftware.ego.QuestionList;
import com.endlessloopsoftware.ego.Study;

import com.endlessloopsoftware.ego.client.*;
import com.endlessloopsoftware.ego.client.graph.NodeProperty.NodePropertyType;
import com.endlessloopsoftware.ego.client.graph.NodeProperty.NodeShape;
import com.endlessloopsoftware.ego.client.graph.EdgeProperty.EdgePropertyType;
import com.endlessloopsoftware.ego.client.graph.EdgeProperty.EdgeShape;

import org.egonet.util.listbuilder.Selection;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

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

			Element graphElement = doc.createElement("GraphElement");
			
			// layoutElement
			Element layoutElement = doc.createElement("Layout");
			layoutElement.setAttribute("layout", renderer.getVv().getLayout().getClass().getName());
			System.out.println("renderer.getVv().getLayout().getClass().getName() " +renderer.getVv().getLayout().getClass().getName());
			
			// zoomElement
			Element zoomElement = doc.createElement("Zoom");
			zoomElement.setAttribute("zoom", renderer.getVv().getLayout().getClass().toString());
			
			// backGroundElement
			Element backGroundElement = doc.createElement("Background");
			String background = ((Integer) renderer.getVisualizationViewer().getBackground().getRGB()).toString();
			backGroundElement.setAttribute("background", background);
			
			// showNodeLabelElement
			Element showNodeLabelElement = doc.createElement("ShowNodeLabel");
			showNodeLabelElement.setAttribute("shownodelabel", renderer.getShowNodeLabels()? "true" : "false");
			
			graphElement.appendChild(layoutElement);
			graphElement.appendChild(backGroundElement);
			graphElement.appendChild(zoomElement);
			graphElement.appendChild(showNodeLabelElement);
			
			studyElement.appendChild(graphElement);
			
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
	
	@SuppressWarnings("unchecked")
	public void loadSettingsFile(File file) throws ParserConfigurationException, SAXException, IOException
	{

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document document = builder.parse(file);
		NodeList nodeList = document.getDocumentElement().getChildNodes();

		Study study = EgoClient.interview.getStudy();
		QuestionList questionList = study.getQuestions();
		Map<Long, Question> questionMap = questionList.getQuestionMap();

		Element graphElement = (Element) nodeList.item(0);
		
		Element layoutElement = (Element) graphElement.getElementsByTagName("Layout").item(0);
		Element zoomElement = (Element) graphElement.getElementsByTagName("Zoom").item(0);
		Element backgroundElement = (Element) graphElement.getElementsByTagName("Background").item(0);
		Element showNodeLabelElement = (Element) graphElement.getElementsByTagName("ShowNodeLabel").item(0);
		
		try {
			renderer.changeLayout(Class.forName(layoutElement.getAttribute("layout")));
		} catch (ClassNotFoundException e) {
			System.out.println("Specified class is not a Layout class");
			e.printStackTrace();
		}
		
		renderer.getVv().setBackground(Color.decode(backgroundElement
				.getAttribute("background")));
		if(showNodeLabelElement.getAttribute("shownodelabel").equals("true"))
				renderer.drawNodeLabels();
		
		for (int i = 1; i < nodeList.getLength(); i++) {
			Node entryNode = nodeList.item(i);
			if (entryNode.getNodeType() == Node.ELEMENT_NODE) {
				Element entryElement = (Element) entryNode;

				Element graphQuestionSelectionElement = (Element) entryElement
						.getElementsByTagName("GraphQuestionSelectionPair").item(0);
				Element propertyElement = (Element) entryElement
						.getElementsByTagName("Property").item(0);

				Element questionElement = (Element) graphQuestionSelectionElement
						.getElementsByTagName("Question").item(0);
				Element selectionElement = (Element) graphQuestionSelectionElement
						.getElementsByTagName("Selection").item(0);
				Element categoryElement = (Element) graphQuestionSelectionElement
						.getElementsByTagName("Category").item(0);

				Element colorElement = (Element) propertyElement
						.getElementsByTagName("Color").item(0);
				Element shapeElement = (Element) propertyElement
						.getElementsByTagName("Shape").item(0);
				Element sizeElement = (Element) propertyElement
						.getElementsByTagName("Size").item(0);

				Element visibleElement = (Element) propertyElement
						.getElementsByTagName("Visibile").item(0);
				Element typeElement = (Element) propertyElement
						.getElementsByTagName("PropertyType").item(0);

				Question question = questionMap.get(Long
						.parseLong(questionElement.getAttribute("id")));
				int category = Integer.parseInt(categoryElement
						.getAttribute("category"));

				for (int j = 0; j < question.selections.length; j++) {
					Selection selection = question.selections[j];

					if (selection.getString().equals(
							selectionElement.getAttribute("text"))) {
						GraphQuestionSelectionPair graphQuestion = new GraphQuestionSelectionPair(
								question, selection, category);

						if (typeElement.getAttribute("type").equals("Edge")) {
							EdgeProperty epColor = new EdgeProperty();
							EdgeProperty epShape = new EdgeProperty();
							EdgeProperty epSize = new EdgeProperty();

							epColor.setColor(Color.decode(colorElement
									.getAttribute("color")));
							epColor.setProperty(EdgePropertyType.Color);

							epSize.setSize(Integer.parseInt(sizeElement
									.getAttribute("size")));
							epSize.setProperty(EdgePropertyType.Size);

							epShape.setShapeFromString(shapeElement.getAttribute("shape"));
							epShape.setProperty(EdgePropertyType.Shape);

							epColor.setVisible(visibleElement.getAttribute(
									"visible").equals("true"));
							epSize.setVisible(visibleElement.getAttribute(
									"visible").equals("true"));
							epShape.setVisible(visibleElement.getAttribute(
									"visible").equals("true"));

							renderer.addQAsettings(graphQuestion, epColor);
							renderer.addQAsettings(graphQuestion, epShape);
							renderer.addQAsettings(graphQuestion, epSize);

						} else {
							// do same for node property

							NodeProperty npColor = new NodeProperty();
							NodeProperty npShape = new NodeProperty();
							NodeProperty npSize = new NodeProperty();

							npColor.setColor(Color.decode(colorElement
									.getAttribute("color")));
							npColor.setProperty(NodePropertyType.Color);

							npShape.setShapeFromString(shapeElement.getAttribute("shape"));
							npShape.setProperty(NodePropertyType.Shape);

							npSize.setSize(Integer.parseInt(sizeElement
									.getAttribute("size")));
							npSize.setProperty(NodePropertyType.Size);

							renderer.addQAsettings(graphQuestion, npColor);
							renderer.addQAsettings(graphQuestion, npShape);
							renderer.addQAsettings(graphQuestion, npSize);

						}

					}
				}
			}
		}
		renderer.updateGraphSettings();
		
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

	public void addQAsetting(GraphQuestionSelectionPair graphQuestion, NodeProperty nodeProperty) {
		GraphSettingsEntry entry = new GraphSettingsEntry(graphQuestion, nodeProperty, GraphSettingType.Node);
		QAsettings.add(entry);
		displaySettings();
	}

	public void addQAsetting(GraphQuestionSelectionPair graphQuestion, EdgeProperty edgeProperty) {
		GraphSettingsEntry entry = new GraphSettingsEntry(graphQuestion, edgeProperty, GraphSettingType.Edge);
		QAsettings.add(entry);
		displaySettings();
	}

	private void displaySettings() {
		
		int size = QAsettings.size();
		System.out.println("Graph settings ("+size+" entries):");
		for (int i = 0; i < size; i++) {
			GraphSettingsEntry entry = QAsettings.get(i);
			//System.out.println("Entry " + i + ": " + entry.toString());
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
