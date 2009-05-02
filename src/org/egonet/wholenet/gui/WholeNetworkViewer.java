package org.egonet.wholenet.gui;

import java.awt.BorderLayout;
import java.awt.HeadlessException;
import javax.swing.JFrame;
import javax.swing.JPanel;

import org.egonet.wholenet.graph.WholeNetwork;
import org.egonet.wholenet.graph.WholeNetworkAlter;
import org.egonet.wholenet.graph.WholeNetworkTie;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.endlessloopsoftware.ego.client.graph.ELSFRLayout2;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.graph.SparseGraph;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.DefaultModalGraphMouse;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;
import edu.uci.ics.jung.visualization.picking.ShapePickSupport;

public class WholeNetworkViewer extends JFrame {

	final private static Logger logger = LoggerFactory.getLogger(WholeNetworkViewer.class);
	
	final private WholeNetwork net;

	public WholeNetworkViewer(WholeNetwork net) throws HeadlessException {
		super("Whole Network Output");
		this.net = net;
		build();
	}
	
	public void build() {

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

		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		JPanel panel = new JPanel(new BorderLayout());
		panel.add(vv, BorderLayout.CENTER);

		setContentPane(panel);
		pack();
	}
}
