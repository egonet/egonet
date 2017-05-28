package org.egonet.gui.wholenet;

import java.awt.BorderLayout;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.filechooser.FileNameExtensionFilter;

import net.sf.functionalj.tuple.Pair;

import org.egonet.graph.ELSFRLayout2;
import org.egonet.graph.WholeNetwork;
import org.egonet.graph.WholeNetworkAlter;
import org.egonet.graph.WholeNetworkTie;
import org.egonet.io.AdjacencyWriter;
import org.egonet.io.ConsensusDataWriter;
import org.egonet.io.EdgeListWriter;
import org.egonet.model.Study;
import org.egonet.util.CatchingAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.com.bytecode.opencsv.CSVWriter;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.graph.SparseGraph;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.DefaultModalGraphMouse;
import edu.uci.ics.jung.visualization.decorators.EdgeShape;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;
import edu.uci.ics.jung.visualization.picking.ShapePickSupport;

public class WholeNetworkViewer extends JFrame {

	final private static Logger logger = LoggerFactory.getLogger(WholeNetworkViewer.class);
	
	final private WholeNetwork net;
	
	final private File studyFile;
	final private Study study;
	
	final private WholeNetworkViewer self;

	public WholeNetworkViewer(Study study, File studyFile, WholeNetwork net) throws HeadlessException {
		super("Whole Network Output");
		this.study = study;
		this.studyFile = studyFile;
		this.net = net;
		this.self = this;
		build();
	}
	
	public void build() {
		JMenu fileMenu = new JMenu("File");
		fileMenu.setMnemonic('F');
		
		saveEdgelistAction.setParent(this);
		JMenuItem saveEdgelist = new JMenuItem(saveEdgelistAction);
		fileMenu.add(saveEdgelist);
		
		saveAdjAction.setParent(this);
		fileMenu.add(new JMenuItem(saveAdjAction));

		saveAlterAttributesCSVAction.setParent(this);
		fileMenu.add(new JMenuItem(saveAlterAttributesCSVAction));
		
		saveConsensusAction.setParent(this);
		fileMenu.add(new JMenuItem(saveConsensusAction));
		
		JMenuBar mb = new JMenuBar();
		mb.add(fileMenu);
		setJMenuBar(mb);
		
		SparseGraph<WholeNetworkAlter,WholeNetworkTie> graph = new SparseGraph<WholeNetworkAlter,WholeNetworkTie>();
		
		for(WholeNetworkAlter alter : net.getWholeNetworkAlters().values()) {
			graph.addVertex(alter);
			logger.info("Adding vertex " + alter);
		}

		for(WholeNetworkTie tie : net.getWholeNetworkTies()) {
			graph.addEdge(tie, tie.getA(), tie.getB());
			logger.info("Adding edge " + tie);
		}
		
		Layout<WholeNetworkAlter,WholeNetworkTie> layout = new ELSFRLayout2<WholeNetworkAlter,WholeNetworkTie>(graph);
		VisualizationViewer<WholeNetworkAlter,WholeNetworkTie> vv = new VisualizationViewer<WholeNetworkAlter,WholeNetworkTie>(layout);
		vv.setGraphMouse(new DefaultModalGraphMouse());
		vv.setPickSupport(new ShapePickSupport<WholeNetworkAlter,WholeNetworkTie>(vv));
		vv.getRenderContext().setVertexLabelTransformer(new ToStringLabeller<WholeNetworkAlter>());
		vv.getRenderContext().setEdgeShapeTransformer(new EdgeShape.Line<WholeNetworkAlter,WholeNetworkTie>());

		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		JPanel panel = new JPanel(new BorderLayout());
		panel.add(vv, BorderLayout.CENTER);

		setContentPane(panel);
		pack();
	}
	
	final CatchingAction saveEdgelistAction = new CatchingAction("Save Edgelist") {
		@Override
		public void safeActionPerformed(ActionEvent e) throws Exception {
			
			String fileName;
			fileName = study.getStudyName() + "_wholenetwork_edgelist";
			File currentDirectory = new File(studyFile.getParent()
					+ "/Graphs");
			currentDirectory.mkdir();

			JFileChooser fileChooser = new JFileChooser();
			fileChooser.setFileFilter(new FileNameExtensionFilter("Comma-Separated Values","csv"));
			fileChooser.setCurrentDirectory(currentDirectory);
			fileChooser.setSelectedFile(new File(fileName + ".csv"));
			fileChooser.setDialogTitle("Save Whole Network EdgeList (CSV)");
			fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);

			int returnValue = JFileChooser.APPROVE_OPTION;
			while (returnValue == JFileChooser.APPROVE_OPTION) {
				returnValue = fileChooser.showSaveDialog(parent);
				File dataFile = fileChooser.getSelectedFile();
				try {
					if(dataFile != null && ! dataFile.isDirectory()) {
						String path = dataFile.getAbsolutePath();
						if(! path.endsWith(".csv")) {
							path += ".csv";
							dataFile = new File(path);
						}
						EdgeListWriter fw = new EdgeListWriter(dataFile);
						Pair<String[], int[][]> p = net.getAdjacencyMatrix();
						fw.writeEdgelist(p.getFirst(), p.getSecond());
						fw.close();
					}
				} catch (Exception e1) {
					throw new RuntimeException(e1);
				}
				break;
			}
		}
	};

	final CatchingAction saveAdjAction = new CatchingAction("Save Adjacency Matrix") {
		@Override
		public void safeActionPerformed(ActionEvent e) throws Exception {
			
			String fileName;
			fileName = study.getStudyName() + "_wholenetwork_edgelist";
			File currentDirectory = new File(studyFile.getParent()
					+ "/Graphs");
			currentDirectory.mkdir();

			JFileChooser fileChooser = new JFileChooser();
			fileChooser.setFileFilter(new FileNameExtensionFilter("Comma-Separated Values","csv"));
			fileChooser.setCurrentDirectory(currentDirectory);
			fileChooser.setSelectedFile(new File(fileName + ".csv"));
			fileChooser.setDialogTitle("Save Whole Network Adjacency Matrix (CSV)");
			fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);

			int returnValue = JFileChooser.APPROVE_OPTION;
			while (returnValue == JFileChooser.APPROVE_OPTION) {
				returnValue = fileChooser.showSaveDialog(parent);
				File dataFile = fileChooser.getSelectedFile();
				try {
					if(dataFile != null  && ! dataFile.isDirectory()) {
						String path = dataFile.getAbsolutePath();
						if(! path.endsWith(".csv")) {
							path += ".csv";
							dataFile = new File(path);
						}
						AdjacencyWriter fw = new AdjacencyWriter(dataFile);
						Pair<String[], int[][]> p = net.getAdjacencyMatrix();
						fw.writeAdjacency(p.getFirst(),p.getSecond());
						fw.close();
					}
				} catch (Exception e1) {
					throw new RuntimeException(e1);
				}
				break;
			}
		}
	};

	final CatchingAction saveConsensusAction = new CatchingAction("Save Consensus Matrix") {
		@Override
		public void safeActionPerformed(ActionEvent e) throws Exception {
			
			String fileName;
			fileName = study.getStudyName() + "_consensus_matrix";
			File currentDirectory = new File(studyFile.getParent());
			currentDirectory.mkdir();

			JFileChooser fileChooser = new JFileChooser();
			fileChooser.setFileFilter(new FileNameExtensionFilter("Comma-Separated Values","csv"));
			fileChooser.setCurrentDirectory(currentDirectory);
			fileChooser.setSelectedFile(new File(fileName + ".csv"));
			fileChooser.setDialogTitle("Save Consensus Matrix (CSV)");
			fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);

			int returnValue = JFileChooser.APPROVE_OPTION;
			while (returnValue == JFileChooser.APPROVE_OPTION) {
				returnValue = fileChooser.showSaveDialog(parent);
				File dataFile = fileChooser.getSelectedFile();
				try {
					if(dataFile != null  && ! dataFile.isDirectory()) {
						String path = dataFile.getAbsolutePath();
						if(! path.endsWith(".csv")) {
							path += ".csv";
							dataFile = new File(path);
						}
						Integer missingAllowed = 2;
						String missingString = (String)
							JOptionPane.showInputDialog(self,
									"How many alters can a reporter not report on?" +
										"\n    (Enter a number)",
									"Allowed missing values - filled in randomly",
									JOptionPane.PLAIN_MESSAGE,
									null,null,missingAllowed+"");
						try {
							missingAllowed = Integer.parseInt(missingString);
						} catch(Exception ex) {
							JOptionPane.showMessageDialog(self, "Didn't understand how many alters a reporter is allowed to not report on."+
									"\nFalling back on default value of "+missingAllowed+".");
						}
						String msg = new ConsensusDataWriter(net,missingAllowed).writeToFile(dataFile);
						JOptionPane.showMessageDialog(self, msg);
					}
				} catch (Exception e1) {
					throw new RuntimeException(e1);
				}
				break;
			}
		}
	};
	
	final CatchingAction saveAlterAttributesCSVAction = new CatchingAction("Save Alter Attributes") {
		@Override
		public void safeActionPerformed(ActionEvent e) throws Exception {
			
			String fileName;
			fileName = study.getStudyName() + "_wholenetwork_nodes";
			File currentDirectory = new File(studyFile.getParent()
					+ "/Graphs");
			currentDirectory.mkdir();

			JFileChooser fileChooser = new JFileChooser();
			fileChooser.setFileFilter(new FileNameExtensionFilter("Comma-Separated Values","csv"));
			fileChooser.setCurrentDirectory(currentDirectory);
			fileChooser.setSelectedFile(new File(fileName + ".csv"));
			fileChooser.setDialogTitle("Save Alter Attributes (CSV)");
			fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);

			int returnValue = JFileChooser.APPROVE_OPTION;
			while (returnValue == JFileChooser.APPROVE_OPTION) {
				returnValue = fileChooser.showSaveDialog(parent);
				File dataFile = fileChooser.getSelectedFile();
				try {
					if(dataFile != null && ! dataFile.isDirectory()) {
						String path = dataFile.getAbsolutePath();
						if(! path.endsWith(".csv")) {
							path += ".csv";
							dataFile = new File(path);
						}
						
						FileWriter fw = new FileWriter(dataFile);
						CSVWriter csv = new CSVWriter(fw);
						
						Set<String> questionSet = Sets.newHashSet();
						for(WholeNetworkAlter alter : net.getWholeNetworkAlters().values()) {
							questionSet.addAll(alter.getAttributes().keySet());
						}
						List<String> questionList = new ArrayList<String>(questionSet);
						
						List<String> heading = Lists.newArrayList("MappingId","Name");
						heading.addAll(questionList);
						csv.writeNext(heading.toArray(new String[]{}));

						for(WholeNetworkAlter alter : net.getWholeNetworkAlters().values()) {
							ArrayList<String> row = 
								Lists.newArrayList(
										alter.getId()+"",
										alter.getOccurences().get(0).toString().replaceAll("[^a-zA-Z_\\-0-9]+", "_"));
							Map<String,String> answers = alter.getAttributes();
							for(String question : questionList) {
								String value = answers.get(question);
								row.add(value == null ? "" : value);
							}
							csv.writeNext(row.toArray(new String[]{}));
						}
						
						csv.flush();
						fw.close();
					}
				} catch (Exception e1) {
					throw new RuntimeException(e1);
				}
				break;
			}
		}
	};
}
