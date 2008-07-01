package com.endlessloopsoftware.ego.client.graph;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.prefs.Preferences;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.filechooser.FileFilter;
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
import com.endlessloopsoftware.ego.author.EgoNet;
import com.endlessloopsoftware.ego.client.EgoClient;
import com.endlessloopsoftware.ego.client.graph.EdgeProperty.EdgePropertyType;
import com.endlessloopsoftware.ego.client.graph.NodeProperty.NodePropertyType;
import com.endlessloopsoftware.elsutils.files.ExtensionFileFilter;
import com.endlessloopsoftware.elsutils.files.FileReadException;

public class ApplyGraphSetting extends JPanel {
	private static FileFilter settingsFilter = new ExtensionFileFilter(
			"XML File", "xml");

	private GraphRenderer graphRenderer;

	private JButton applyButton;

	List<Selection> selectionList = new ArrayList<Selection>();

	private GroupLayout layout;

	public ApplyGraphSetting(GraphRenderer renderer) {
		this.graphRenderer = renderer;
		layout = new GroupLayout(this);
		this.setLayout(layout);
		layout.setAutocreateGaps(true);
		layout.setAutocreateContainerGaps(true);
		createComponents();

	}

	private void createComponents() {

		JFileChooser jNewStudyChooser = new JFileChooser();

		// create apply button
		applyButton = new JButton("Browse Settings File and Apply");
		applyButton.setVisible(true);

		applyButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				selectSettingsFile();
			}
		});

		drawPanel();

	}

	public void selectSettingsFile() {
		Preferences prefs = Preferences.userNodeForPackage(EgoNet.class);
		JFileChooser settingsFileChooser = new JFileChooser();
		File f;

		settingsFileChooser.addChoosableFileFilter(settingsFilter);
		settingsFileChooser.setDialogTitle("Select Setting File");

		if (JFileChooser.APPROVE_OPTION == settingsFileChooser
				.showOpenDialog(EgoNet.frame)) {
			f = settingsFileChooser.getSelectedFile();

			try {
				if (!f.canRead()) {
					throw new FileReadException();
				} else {
					System.out.println(f.toString());
					try {
						updateEdgeColor(f);
					} catch (ParserConfigurationException e1) {
						System.out.println("Parser Config Exception");
						e1.printStackTrace();
					} catch (SAXException e1) {
						System.out.println("SAX Exception");
						e1.printStackTrace();
					} catch (IOException e1) {
						System.out.println("IOException");
						e1.printStackTrace();
					}

				}
			} catch (Exception e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(null,
						"Unable to read Settings file", "File Error",
						JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	private void updateEdgeColor(File file)
			throws ParserConfigurationException, SAXException, IOException {

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document document = builder.parse(file);
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

							graphRenderer.addQAsettings(graphQuestion, epColor);
							graphRenderer.addQAsettings(graphQuestion, epShape);
							graphRenderer.addQAsettings(graphQuestion, epSize);

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

							graphRenderer.addQAsettings(graphQuestion, npColor);
							graphRenderer.addQAsettings(graphQuestion, npShape);
							graphRenderer.addQAsettings(graphQuestion, npSize);

						}

					}
				}
			}
		}
		graphRenderer.updateGraphSettings();
	}

	private void drawPanel() {
		this.removeAll();

		GroupLayout.SequentialGroup hGroup = layout.createSequentialGroup();
		hGroup.add(layout.createParallelGroup().add(applyButton));

		layout.setHorizontalGroup(hGroup);
		GroupLayout.SequentialGroup vGroup = layout.createSequentialGroup();
		vGroup.add(layout.createParallelGroup(GroupLayout.BASELINE).add(
				applyButton));

		layout.setVerticalGroup(vGroup);
	}
}