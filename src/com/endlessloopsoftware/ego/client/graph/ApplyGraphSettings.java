package com.endlessloopsoftware.ego.client.graph;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.egonet.util.listbuilder.Selection;
import org.jdesktop.layout.GroupLayout;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.endlessloopsoftware.ego.Question;
import com.endlessloopsoftware.ego.QuestionList;
import com.endlessloopsoftware.ego.Study;
import com.endlessloopsoftware.ego.client.EgoClient;
import com.endlessloopsoftware.ego.client.graph.EdgeProperty.EdgeShape;
import com.endlessloopsoftware.ego.client.graph.GraphSettingsEntry.GraphSettingType;

public class ApplyGraphSettings extends JPanel {

	private GraphRenderer graphRenderer;

	private JButton applyButton;
	
	private JLabel testLabel1;
	private JLabel testLabel2;
	private JLabel testLabel3;

	List<Selection> selectionList = new ArrayList<Selection>();

	private GroupLayout layout;
	
	public ApplyGraphSettings(GraphRenderer renderer) {
		this.graphRenderer = renderer;
		layout = new GroupLayout(this);
		this.setLayout(layout);
		layout.setAutocreateGaps(true);
		layout.setAutocreateContainerGaps(true);
		createComponents();

	}

	private void createComponents(){
		// create apply button
		applyButton = new JButton("Apply Settings");
		applyButton.setVisible(true);
		testLabel1 = new JLabel("Test1");
		testLabel2 = new JLabel("Test2");
		testLabel3 = new JLabel("Test3");
		
		testLabel1.setVisible(true);
		testLabel2.setVisible(true);
		testLabel3.setVisible(true);
		
		applyButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					updateEdgeColor();
				} catch (ParserConfigurationException e1) {
					// TODO Auto-generated catch block
					System.out.println("Parser Config Exception");
					e1.printStackTrace();
				} catch (SAXException e1) {
					System.out.println("SAX Exception");
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (IOException e1) {
					System.out.println("IOException");
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		
		drawPanel();
		
	}
	/*
	 * private void updateNodeColor() { Question question = (Question)
	 * questionCombo.getSelectedItem(); System.out.println("Question combo"
	 * +question.UniqueId);
	 * 
	 * if (question.answerType == Question.CATEGORICAL) { for (int i = 0; i <
	 * question.selections.length; i++) { Selection selection =
	 * question.selections[i];
	 * 
	 * GraphQuestion graphQuestion = new GraphQuestion(question, selection,
	 * Question.ALTER_QUESTION); NodeProperty nodeProperty = new NodeProperty();
	 * nodeProperty.setColor((Color) table.getValueAt(i, 1));
	 * nodeProperty.setProperty(NodeProperty.Property.Color);
	 * graphRenderer.addQAsettings(graphQuestion, nodeProperty);
	 * graphRenderer.updateGraphSettings(); } }else if (question.answerType ==
	 * Question.TEXT) { System.out.println("Applying labels for text
	 * questions"); for (int i =0;i < selectionList.size() ; i++) { Selection
	 * selection = selectionList.get(i); NodeProperty nodeProperty = new
	 * NodeProperty(); nodeProperty.setColor((Color) table.getValueAt(i, 1));
	 * nodeProperty.setProperty(NodeProperty.Property.Color); GraphQuestion
	 * graphQuestion = new GraphQuestion(question, selection,
	 * Question.ALTER_QUESTION); graphRenderer.addQAsettings(graphQuestion,
	 * nodeProperty); graphRenderer.updateGraphSettings(); } } //
	 * graphRenderer.getVv().repaint(); }
	 */

	private void updateEdgeColor() throws ParserConfigurationException, SAXException, IOException {

		GraphSettingsEntry graphSettingEntry = null;
		java.util.List<GraphSettingsEntry> graphSettingEntryList = Collections
				.synchronizedList(new ArrayList<GraphSettingsEntry>());
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document document = builder
				.parse("src/com/endlessloopsoftware/ego/client/test.xml");
		NodeList entryNodeList = document.getDocumentElement().getChildNodes();

		Study study = EgoClient.interview.getStudy();
		QuestionList questionList = study.getQuestions();
		Map<Long, Question> questionMap = questionList.getQuestionMap();

		for (int i = 0; i < entryNodeList.getLength(); i++) {
			Node entryNode = entryNodeList.item(i);
			if (entryNode.getNodeType() == Node.ELEMENT_NODE) {
				Element entryElement = (Element) entryNode;

				Element graphElement = (Element) entryElement
						.getElementsByTagName("GraphQuestion").item(0);
				Element propertyElement = (Element) entryElement
						.getElementsByTagName("Property").item(0);

				Element questionElement = (Element) graphElement
						.getElementsByTagName("Question").item(0);
				Element selectionElement = (Element) graphElement
						.getElementsByTagName("Selection").item(0);
				Element categoryElement = (Element) graphElement
						.getElementsByTagName("Category").item(0);

				Element colorElement = (Element) propertyElement
						.getElementsByTagName("Color").item(0);
				Element shapeElement = (Element) propertyElement
						.getElementsByTagName("Shape").item(0);
				Element sizeElement = (Element) propertyElement
						.getElementsByTagName("Size").item(0);

				Element propertyTypeElement = (Element) entryElement
						.getElementsByTagName("PropertyType").item(0);

				Question question = questionMap.get(Long
						.parseLong(questionElement.getAttribute("id")));

				for (int j = 0; j < question.selections.length; j++) {
					Selection selection = question.selections[j];
					GraphQuestion graphQuestion = new GraphQuestion(question,
							selection, Integer.parseInt(categoryElement
									.getAttribute("category")));
					
					if (propertyTypeElement.getAttribute("Type").equals("Edge")) {
						EdgeProperty ep = new EdgeProperty();
						
						ep.setColor(Color.decode(colorElement
								.getAttribute("color")));
						
						ep.setSize(Integer.parseInt(sizeElement
								.getAttribute("size")));
						ep.setShape(EdgeShape.CubicCurve);
						
						ep.setProperty(EdgeProperty.Property.Color);

						System.out.println("Color is " +ep.getColor());
						graphSettingEntry = new GraphSettingsEntry(graphQuestion, ep,
								GraphSettingType.Edge);
						graphRenderer.addQAsettings(graphQuestion, ep);
						
						//graphSettingEntryList.add(graphSettingEntry);
						//GraphRenderer.graphSettings.setQAsettings(graphSettingEntryList);
						graphRenderer.updateGraphSettings();
					}

				}
			}
		}
	}

	private void drawPanel() {
		this.removeAll();

		GroupLayout.SequentialGroup hGroup = layout.createSequentialGroup();
		hGroup.add(layout.createParallelGroup().add(applyButton).add(testLabel1));
			
		layout.setHorizontalGroup(hGroup);
		GroupLayout.SequentialGroup vGroup = layout.createSequentialGroup();
		vGroup.add(layout.createParallelGroup().add(testLabel2).add(testLabel3));
				
		layout.setVerticalGroup(vGroup);
	}
}
