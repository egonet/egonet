package org.egonet.wholenet.gui;

import java.awt.BorderLayout;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import net.sf.functionalj.tuple.Pair;

import org.egonet.io.AdjacencyWriter;
import org.egonet.io.EdgeListWriter;
import org.egonet.util.CatchingAction;
import org.egonet.wholenet.graph.WholeNetwork;
import org.egonet.wholenet.graph.WholeNetworkAlter;
import org.egonet.wholenet.graph.WholeNetworkTie;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.endlessloopsoftware.ego.client.graph.ELSFRLayout2;
import com.endlessloopsoftware.egonet.Study;

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

	public WholeNetworkViewer(Study study, File studyFile, WholeNetwork net) throws HeadlessException {
		super("Whole Network Output");
		this.study = study;
		this.studyFile = studyFile;
		this.net = net;
		build();
	}
	
	public void build() {
		JMenu fileMenu = new JMenu("File");
		fileMenu.setMnemonic('F');
		
		saveEdgelistAction.setParent(this);
		JMenuItem saveEdgelist = new JMenuItem(saveEdgelistAction);
		fileMenu.add(saveEdgelist);
		
		saveAdjAction.setParent(this);
		JMenuItem saveAdj = new JMenuItem(saveAdjAction);
		fileMenu.add(saveAdj);

		
		JMenuBar mb = new JMenuBar();
		mb.add(fileMenu);
		setJMenuBar(mb);
		
		
		SparseGraph<WholeNetworkAlter,WholeNetworkTie> graph = new SparseGraph<WholeNetworkAlter,WholeNetworkTie>();
		
		for(WholeNetworkAlter alter : net.getWholeNetworkAlters().values()) {
			graph.addVertex(alter);
			logger.info("Adding vertex " + alter);
		}

		for(WholeNetworkTie tie : net.getWholeNetworkTies().values()) {
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
			fileChooser.setCurrentDirectory(currentDirectory);
			fileChooser.setSelectedFile(new File(fileName + ".csv"));
			fileChooser.setDialogTitle("Save Whole Network EdgeList");
			fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);

			int returnValue = JFileChooser.APPROVE_OPTION;
			while (returnValue == JFileChooser.APPROVE_OPTION) {
				returnValue = fileChooser.showSaveDialog(parent);
				File dataFile = fileChooser.getSelectedFile();
				try {
					
					EdgeListWriter fw = new EdgeListWriter(dataFile);
					Pair<String[], int[][]> p = net.getAdjacencyMatrix();
					fw.writeEdgelist(p.getFirst(), p.getSecond());
					fw.close();
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
			fileChooser.setCurrentDirectory(currentDirectory);
			fileChooser.setSelectedFile(new File(fileName + ".csv"));
			fileChooser.setDialogTitle("Save Whole Network EdgeList");
			fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);

			int returnValue = JFileChooser.APPROVE_OPTION;
			while (returnValue == JFileChooser.APPROVE_OPTION) {
				returnValue = fileChooser.showSaveDialog(parent);
				File dataFile = fileChooser.getSelectedFile();
				try {
					
					AdjacencyWriter fw = new AdjacencyWriter(dataFile);
					Pair<String[], int[][]> p = net.getAdjacencyMatrix();
					fw.writeAdjacency(p.getSecond());
					fw.close();
				} catch (Exception e1) {
					throw new RuntimeException(e1);
				}
				break;
			}
		}
	};
}
