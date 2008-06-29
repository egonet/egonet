package com.endlessloopsoftware.ego.client;

import java.awt.AWTEvent;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.egonet.util.listbuilder.Selection;
import org.w3c.dom.*;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.endlessloopsoftware.elsutils.files.ExtensionFileFilter;
import com.endlessloopsoftware.elsutils.files.FileCreateException;
import com.endlessloopsoftware.ego.Question;
import com.endlessloopsoftware.ego.QuestionList;
import com.endlessloopsoftware.ego.Study;
import com.endlessloopsoftware.ego.client.graph.*;
import com.endlessloopsoftware.elsutils.files.FileHelpers;
import com.endlessloopsoftware.ego.client.graph.GraphData;
import com.endlessloopsoftware.ego.client.graph.EdgeProperty.EdgeShape;
import com.endlessloopsoftware.ego.client.graph.GraphSettingsEntry.GraphSettingType;

/**
 * <p>
 * Title: Egocentric Network Researcher
 * </p>
 * <p>
 * Description: Configuration Utilities for an Egocentric network study
 * </p>
 * <p>
 * Copyright: Copyright (c) 2004
 * </p>
 * <p>
 * Company: Endless Loop Software
 * </p>
 * 
 * @author Peter C. Schoaff
 * @version 2.1
 */

public class ClientFrame extends JFrame {
	private final JMenuBar jMenuBar1 = new JMenuBar();

	private final JMenu jMenuFile = new JMenu("File");

	private final JMenu jMenuHelp = new JMenu("Help");

	private final JMenu jMenuGraph = new JMenu("Graph");

	private final JMenuItem graphProperties = new JMenuItem("Graph Properties");

	private final JMenuItem nodeProperties = new JMenuItem("Node Properties");

	private final JMenuItem edgeProperties = new JMenuItem("Edge Properties");

	private final JMenuItem jMenuHelpAbout = new JMenuItem("About");

	private final JMenuItem saveStudySummary = new JMenuItem(
			"Save Study Summary");

	private final JMenuItem exit = new JMenuItem("Exit");

	public final JMenuItem saveAlterSummary = new JMenuItem(
			"Save Alter Summary");

	public final JMenuItem saveTextSummary = new JMenuItem(
			"Save Text Answer Summary");

	public final JMenuItem saveAdjacencyMatrix = new JMenuItem(
			"Save Adjacency Matrix");

	public final JMenuItem saveWeightedAdjacencyMatrix = new JMenuItem(
			"Save Weighted Adjacency Matrix");

	public final JMenuItem saveGraph = new JMenuItem("Save Graph as image");

	public final JMenuItem saveGraphSettings = new JMenuItem(
			"Save graph settings");

	public final JMenuItem applyGraphSettings = new JMenuItem(
			"Apply graph settings");

	public final JMenuItem saveInterview = new JMenuItem("Save Interview");

	public final JMenuItem recalculateStatistics = new JMenuItem(
			"Recalculate Statistics");

	public final JMenuItem close = new JMenuItem("Return to Main Menu");

	public final JMenuItem saveInterviewStatistics = new JMenuItem(
			"Save Interview Statistics");

	// Construct the frame
	public ClientFrame() {
		enableEvents(AWTEvent.WINDOW_EVENT_MASK);

		try {
			jbInit();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// Component initialization
	private void jbInit() throws Exception {
		this.setSize(new Dimension(700, 600));
		this.setExtendedState(this.getExtendedState() | JFrame.MAXIMIZED_BOTH);
		this.setTitle("Egocentric Networks Study Tool");

		createMenuBar(EgoClient.SELECT);

		this.setContentPane(new JPanel());

		jMenuHelpAbout.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				jMenuHelpAbout_actionPerformed(e);
			}
		});

		exit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				jMenuFileExit_actionPerformed(e);
			}
		});

		saveStudySummary.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(ActionEvent e) {
				saveStudySummary_actionPerformed(e);
			}
		});

		saveGraph.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(ActionEvent e) {
				saveGraph_actionPerformed(e);
			}
		});

		saveGraphSettings
				.addActionListener(new java.awt.event.ActionListener() {
					public void actionPerformed(ActionEvent e) {
						saveGraphSettings_actionPerformed(e);
					}
				});

		saveInterview.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					EgoClient.interview.completeInterview();
				} catch (FileCreateException ex) {
					ex.printStackTrace();
				}
			}
		});

		applyGraphSettings
				.addActionListener(new java.awt.event.ActionListener() {
					public void actionPerformed(ActionEvent e) {
						try {
							applyGraphSettings_actionPerformed(e);
						} catch (Exception e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					}
				});

		recalculateStatistics
				.addActionListener(new java.awt.event.ActionListener() {
					public void actionPerformed(ActionEvent e) {
						EgoClient.interview = EgoClient.storage.readInterview();
						if (EgoClient.interview != null)
							ViewInterviewPanel.gotoPanel();
					}
				});

		close.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(ActionEvent e) {
				SourceSelectPanel.gotoPanel(false);
			}
		});
	}

	public void flood() {
		Dimension size = this.getSize();
		this.pack();
		this.setSize(size);
		this.validate();
	}

	// File | Exit action performed
	public void jMenuFileExit_actionPerformed(ActionEvent e) {
		if (EgoClient.interview != null) {
			EgoClient.interview.exit();
		}

		System.exit(0);
	}

	// Help | About action performed
	public void jMenuHelpAbout_actionPerformed(ActionEvent e) {
		JOptionPane
				.showMessageDialog(
						this,
						"Egonet is an egocentric network study tool."
								+ "\n\nThanks to: Dr. Chris McCarty, University of Florida",
						"About Egonet", JOptionPane.PLAIN_MESSAGE);
	}

	// Overridden so we can exit when window is closed
	protected void processWindowEvent(WindowEvent e) {
		super.processWindowEvent(e);
		if (e.getID() == WindowEvent.WINDOW_CLOSING) {
			jMenuFileExit_actionPerformed(null);
		}
	}

	public void createMenuBar(int mode) {
		jMenuBar1.removeAll();
		jMenuFile.removeAll();
		jMenuHelp.removeAll();
		jMenuGraph.removeAll();

		// File Menu
		if (mode == EgoClient.VIEW_SUMMARY) {
			jMenuFile.add(saveStudySummary);
			jMenuFile.add(close);
			jMenuFile.addSeparator();
			jMenuFile.add(exit);
		} else if (mode == EgoClient.VIEW_INTERVIEW) {
			/*******************************************************************
			 * Create Menu Bar
			 ******************************************************************/
			jMenuFile.add(saveAlterSummary);
			jMenuFile.add(saveTextSummary);
			jMenuFile.add(saveAdjacencyMatrix);
			jMenuFile.add(saveWeightedAdjacencyMatrix);
			jMenuFile.add(saveGraph);
			jMenuFile.add(saveGraphSettings);
			jMenuFile.add(applyGraphSettings);
			jMenuFile.add(saveInterview);
			jMenuFile.add(recalculateStatistics);
			jMenuFile.addSeparator();
			jMenuFile.add(close);

			jMenuGraph.add(graphProperties);
			jMenuGraph.add(nodeProperties);
			jMenuGraph.add(edgeProperties);

		} else {
			jMenuFile.add(exit);
		}
		jMenuBar1.add(jMenuFile);
		// jMenuBar1.add(jMenuGraph);
		// Help Menu
		jMenuHelp.add(jMenuHelpAbout);
		jMenuBar1.add(jMenuHelp);

		this.setJMenuBar(jMenuBar1);
	}

	void saveStudySummary_actionPerformed(ActionEvent e) {
		String name = FileHelpers.formatForCSV(EgoClient.study.getStudyName());
		String filename = name + "_Summary";
		PrintWriter w = EgoClient.storage.newStatisticsPrintWriter(
				"Study Summary", "csv", filename);

		if (w != null) {
			try {
				((SummaryPanel) EgoClient.frame.getContentPane())
						.writeStudySummary(w);
			} finally {
				w.close();
			}
		}
	}

	void saveGraph_actionPerformed(ActionEvent e) {
		String fileName;
		fileName = EgoClient.interview.getName() + "_graph";
		File currentDirectory = new File(EgoClient.storage.getPackageFile()
				.getParent()
				+ "/Graphs");
		currentDirectory.mkdir();

		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setCurrentDirectory(currentDirectory);
		fileChooser.setSelectedFile(new File(fileName + ".jpg"));
		fileChooser.setDialogTitle("Save Graph");
		fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);

		// ExtensionFileFilter jpegFilter = new ExtensionFileFilter("JPEG
		// Image",".jpg");
		FileFilter imageFilter = new ImageFilter();
		fileChooser.addChoosableFileFilter(imageFilter);

		int returnValue = JFileChooser.APPROVE_OPTION;
		while (returnValue == JFileChooser.APPROVE_OPTION) {
			returnValue = fileChooser.showSaveDialog(this);
			File imageFile = fileChooser.getSelectedFile();

			String fmt = ImageFilter.getExtension(imageFile);
			if (fmt != null && imageFilter.accept(imageFile)) {
				System.out.println(imageFile.getName());
				GraphData.writeImage(imageFile, fmt);
				break;
			} else {
				JOptionPane
						.showMessageDialog(this,
								"I don't recognize that image format. Please try again.");
			}
		}

	}

	void saveGraphSettings_actionPerformed(ActionEvent e) {

		String[] name = EgoClient.interview.getName();
		String fileName = "/" + name[0] + "_" + name[1] + ".xml";

		final File currentDirectory = new File(EgoClient.storage
				.getPackageFile().getParent(), "Graphs");
		currentDirectory.mkdir();
		File file = new File(currentDirectory.getAbsolutePath() + fileName);

		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setCurrentDirectory(currentDirectory);
		fileChooser.setSelectedFile(new File(fileName + ".settings"));
		fileChooser.setDialogTitle("Save Graph Settings");
		fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		fileChooser.setSelectedFile(file);

		ExtensionFileFilter filter = new ExtensionFileFilter("Graph Settings",
				"settings");
		fileChooser.addChoosableFileFilter(filter);

		int returnValue = fileChooser.showSaveDialog(this);
		if (returnValue == JFileChooser.APPROVE_OPTION) {
			File settingsFile = fileChooser.getSelectedFile();

			GraphRenderer.getGraphSettings().saveSettingsFile(settingsFile);
		}
	}

	void applyGraphSettings_actionPerformed(ActionEvent e) throws Exception {

		// Add a file chooser
		String file = "src/com/endlessloopsoftware/ego/client/test.xml";
		//parseXMLFile(file);

	}

	public void parseXMLFile(String fileName) throws SAXException, IOException,
			ParserConfigurationException {
		GraphSettingsEntry graphSettingEntry = null;

		java.util.List<GraphSettingsEntry> graphSettingEntryList = Collections
				.synchronizedList(new ArrayList<GraphSettingsEntry>());
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document document = builder.parse(fileName);
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
					GraphQuestion gq = new GraphQuestion(question, selection,
							Integer.parseInt(categoryElement.getAttribute("category")));

					if (propertyTypeElement.getAttribute("Type").equals("Edge")) {
						EdgeProperty ep = new EdgeProperty();
						
						ep.setColor(Color.decode(colorElement
								.getAttribute("color")));
						
						ep.setSize(Integer.parseInt(sizeElement
								.getAttribute("size")));
						ep.setShape(EdgeShape.CubicCurve);
						
						ep.setProperty(EdgeProperty.Property.Color);

						System.out.println("Color is " +ep.getColor());
						graphSettingEntry = new GraphSettingsEntry(gq, ep,
								GraphSettingType.Edge);
						graphSettingEntryList.add(graphSettingEntry);

					} else {
						System.out.println("Property is of Type Node");
					}
				}

			}
		}

		GraphRenderer.graphSettings.setQAsettings(graphSettingEntryList);
		//GraphRenderer.updateGraphSettings();
	}
}