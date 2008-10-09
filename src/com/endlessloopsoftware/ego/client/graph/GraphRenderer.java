/***
 * Copyright (c) 2008, Endless Loop Software, Inc.
 * 
 * This file is part of EgoNet.
 * 
 * EgoNet is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 * 
 * EgoNet is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.endlessloopsoftware.ego.client.graph;

import com.endlessloopsoftware.ego.client.graph.GraphSettingsEntry.GraphSettingType;
import java.lang.reflect.Constructor;
import java.util.*;
import java.util.List;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;

import javax.swing.*;

import edu.uci.ics.jung.visualization.PluggableRenderer;
import org.egonet.util.listbuilder.Selection;
import com.endlessloopsoftware.ego.client.EgoClient;
import com.endlessloopsoftware.ego.Question;
import com.endlessloopsoftware.ego.client.statistics.Statistics;

import edu.uci.ics.jung.graph.ArchetypeVertex;
import edu.uci.ics.jung.graph.Edge;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.Vertex;
import edu.uci.ics.jung.utils.Pair;
import edu.uci.ics.jung.graph.decorators.ConstantEdgePaintFunction;
import edu.uci.ics.jung.graph.decorators.ConstantEdgeStrokeFunction;
import edu.uci.ics.jung.graph.decorators.ConstantVertexPaintFunction;
import edu.uci.ics.jung.graph.decorators.ToolTipFunction;
import edu.uci.ics.jung.graph.decorators.EdgeShape;
import edu.uci.ics.jung.graph.decorators.VertexStringer;
import edu.uci.ics.jung.graph.decorators.VertexPaintFunction;
import edu.uci.ics.jung.graph.decorators.EdgeStrokeFunction;
import edu.uci.ics.jung.visualization.FRLayout;
import edu.uci.ics.jung.visualization.Layout;
import edu.uci.ics.jung.graph.decorators.EdgeStringer;
import edu.uci.ics.jung.visualization.ShapePickSupport;
import edu.uci.ics.jung.graph.decorators.PickableEdgePaintFunction;
import edu.uci.ics.jung.graph.decorators.EdgePaintFunction;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.CrossoverScalingControl;
import edu.uci.ics.jung.visualization.control.DefaultModalGraphMouse;
import edu.uci.ics.jung.visualization.control.ScalingControl;
import edu.uci.ics.jung.visualization.DefaultVisualizationModel;
import edu.uci.ics.jung.visualization.GraphZoomScrollPane;
import edu.uci.ics.jung.visualization.VisualizationModel;
import edu.uci.ics.jung.visualization.control.SatelliteVisualizationViewer;
import edu.uci.ics.jung.graph.ArchetypeEdge;
import edu.uci.ics.jung.graph.decorators.EdgeShapeFunction;
import edu.uci.ics.jung.graph.impl.SparseVertex;
import edu.uci.ics.jung.graph.impl.UndirectedSparseEdge;
import edu.uci.ics.jung.graph.impl.UndirectedSparseGraph;
import edu.uci.ics.jung.graph.decorators.VertexShapeFunction;

public class GraphRenderer extends PluggableRenderer implements
		VertexShapeFunction, VertexPaintFunction, EdgeShapeFunction,
		EdgePaintFunction, EdgeStringer, VertexStringer, EdgeStrokeFunction,
		ToolTipFunction {

	public static GraphSettings graphSettings;

	private static VisualizationViewer visualizationViewer;

	private GraphZoomScrollPane visualizationViewerScrollPane;

	private SatelliteVisualizationViewer satelliteVisualizationViewer;

	private GraphZoomScrollPane satelliteVisualizationViewerScrollPane;

	private DefaultModalGraphMouse graphMouse;

	private VisualizationModel visualizationModel;

	private static Vertex[] vertexArray = null;

	private String[] alterList;

	private Statistics stats;

	private static Graph graph;

	public static boolean showEdgeWeights = false;

	private boolean showNodeLabels = true;

	private EgoClient egoClient;
	
	public GraphRenderer(EgoClient egoClient) {
		this.egoClient=egoClient;
		graph = new UndirectedSparseGraph();
		stats = egoClient.getInterview().getStats();
			alterList = stats.alterList;
			vertexArray = new Vertex[alterList.length];
			for (int i = 0; i < alterList.length; ++i) {
				vertexArray[i] = new SparseVertex();
				graph.addVertex(vertexArray[i]);
			}
		
		graphSettings = new GraphSettings(egoClient, this);
		this.setVertexShapeFunction(this);
		this.setVertexPaintFunction(this);
		this.setEdgeShapeFunction(this);
		this.setEdgePaintFunction(this);
		this.setEdgeStrokeFunction(this);
		EdgeStringer stringer = new EdgeStringer() {
			public String getLabel(ArchetypeEdge e) {
				return "";
			}
		};
		this.setEdgeStringer(stringer);
		this.setVertexStringer(this);
	}

	/**
	 * Redraws the graph with the provided layout
	 * 
	 * @param Class Layout
	 */
	public void changeLayout(Class layout) throws Exception {
			Constructor constructor = layout.getConstructor(new Class[] { Graph.class });
			Object o = constructor.newInstance(graph);
			Layout l = (Layout) o;

			if (l instanceof FRLayout) {
				FRLayout frLayout = (FRLayout) l;
				frLayout.setMaxIterations(1000);
			}

			// TODO: change required with spring layout not FR layout
			if (l instanceof SpringLayout) {
				SpringLayout springLayout = (SpringLayout) l;
			}
			visualizationViewer.stop();
			visualizationViewer.setGraphLayout(l, false);
			visualizationViewer.restart();

	}

	/**
	 * Changes the size of the layout (canvas size) then redraws the graph 
	 * to fit the nodes to the new layout size
	 * 
	 * @param int x -- width
	 * @param int y -- height
	 */
	public void changeLayoutSize(int x, int y) {
		try {
			Dimension dim = visualizationViewer.getGraphLayout().getCurrentSize();
			Layout layout = visualizationViewer.getGraphLayout();
			
			if(dim.width + x < 5 || dim.height + y < 5){
				new JOptionPane().showMessageDialog(null, 
						new String("Layout Size Out of Bounds"), "Error", 
						JOptionPane.ERROR_MESSAGE);
				System.out.println("Less than 5");
			}
			else{
				layout.resize(new Dimension(dim.width + x, dim.height + y));	
				visualizationViewer.stop();
				visualizationViewer.setGraphLayout(layout, false);
				visualizationViewer.restart();
			}					
		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}

	/**
	 * Redraws the graph without changing the layout or layout size
	 *
	 */
	public void reiterate(){
		Dimension dim = visualizationViewer.getGraphLayout().getCurrentSize();
		Layout layout = visualizationViewer.getGraphLayout();
		
		layout.initialize(dim);
		
		visualizationViewer.stop();
		visualizationViewer.setGraphLayout(layout, true);
		visualizationViewer.restart();
	}
	
	/**
	 * create the main viewable Graph by for display on a panel use the JUNG
	 * classes
	 * 
	 * @return
	 */
	public JComponent createGraph() {
		ToolTipManager.sharedInstance().setDismissDelay(10000);
		// setVertexStringer(undirectedLabeler);
		graphMouse = new DefaultModalGraphMouse();

		// create the model that drives layouts and view updates
		visualizationModel = new DefaultVisualizationModel(new ELSFRLayout2(graph));

		// create the regular viewer and scroller
		visualizationViewer = new VisualizationViewer(visualizationModel, this);
		visualizationViewer.setPickSupport(new ShapePickSupport());
		visualizationViewer.setToolTipFunction(this);
		visualizationViewer.setGraphMouse(graphMouse);
		visualizationViewer.setBackground(Color.WHITE);

		visualizationViewerScrollPane = new GraphZoomScrollPane(
				visualizationViewer);
		final ScalingControl scaler = new CrossoverScalingControl();

		JButton plus = new JButton("+");
		plus.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				scaler.scale(visualizationViewer, 1.1f, visualizationViewer
						.getCenter());
			}
		});
		// plus.setMaximumSize(new Dimension(20,20));
		JButton minus = new JButton("-");
		minus.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				scaler.scale(visualizationViewer, 1 / 1.1f, visualizationViewer
						.getCenter());
			}
		});
		// create the sat viewer and scroller
		satelliteVisualizationViewer = new SatelliteVisualizationViewer(
				visualizationViewer, visualizationModel,
				new PluggableRenderer()); // TODO: fix renderer and change
		// back to this
		satelliteVisualizationViewer.setPreferredSize(new Dimension(150, 150));
		satelliteVisualizationViewer.setToolTipFunction(this);
		satelliteVisualizationViewer.setBackground(visualizationViewer
				.getBackground());

		satelliteVisualizationViewerScrollPane = new GraphZoomScrollPane(
				satelliteVisualizationViewer);

		return visualizationViewerScrollPane;
	}

	/**
	 * creates the nodes for every alter creates edges for entries in adjacency
	 * matrix
	 */
	public  void updateEdges() {
		graph.removeAllEdges();
		Iterator edgeIterator = graphSettings.getEdgeIterator();
		while (edgeIterator.hasNext()) {
			Edge edge = (Edge) edgeIterator.next();
			try {
				if (graphSettings.isEdgeVisible(edge)) {
					graph.addEdge(edge);
				}
			} catch (edu.uci.ics.jung.exceptions.ConstraintViolationException ex) {
				System.err.println(ex.getMessage());
			}
		}
	}

	public String getLabel(ArchetypeVertex v) {
		return graphSettings.getNodeLabel(v);
	}

	/**
	 * Creates the small thumdnail viewer for the main graph. You MUST call
	 * createGraph first.
	 * 
	 * @return
	 */
	public JComponent createSatellitePane() {
		return satelliteVisualizationViewerScrollPane;
	}

	/**
	 * Displays the edges of graph used to draw the edge
	 */
	public void drawEdgeLabels() {
		this.setEdgeStringer(this);
		this.setEdgePaintFunction(new PickableEdgePaintFunction(this,
				Color.black, Color.cyan));
		visualizationViewer.repaint();
	}

	/**
	 * Displays the labels of nodes
	 */
	public void drawNodeLabels() {

		this.setVertexStringer(this);
		visualizationViewer.repaint();
	}

	public Vertex[] getvertexArray() {
		return vertexArray;
	}

	/**
	 * Implemented for VertexPaintFunction Returns the color of the outline of
	 * the vertex Draw paint color is defaulted BLACK
	 */
	public Paint getDrawPaint(Vertex v) {
		Color fillColor = graphSettings.getNodeColor(v);
		ConstantVertexPaintFunction cvpf = new ConstantVertexPaintFunction(
				Color.BLACK, fillColor);
		return cvpf.getDrawPaint(v);
	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see edu.uci.ics.jung.graph.decorators.EdgePaintFunction#getDrawPaint(edu.uci.ics.jung.graph.Edge)
	 */
	public Paint getDrawPaint(Edge e) {
		Color edgeColor = graphSettings.getEdgeColor(e);
		ConstantEdgePaintFunction cvpf = new ConstantEdgePaintFunction(
				edgeColor, null);
		return cvpf.getDrawPaint(e);
	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see edu.uci.ics.jung.graph.decorators.EdgePaintFunction#getFillPaint(edu.uci.ics.jung.graph.Edge)
	 */
	public Paint getFillPaint(Edge e) {
		Color edgeColor = graphSettings.getEdgeColor(e);
		ConstantEdgePaintFunction cvpf = new ConstantEdgePaintFunction(
				edgeColor, null);
		return cvpf.getFillPaint(e);
	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see edu.uci.ics.jung.graph.decorators.EdgeStrokeFunction#getStroke(edu.uci.ics.jung.graph.Edge)
	 */
	public Stroke getStroke(Edge e) {
		ConstantEdgeStrokeFunction edgeStrokeFunction = new ConstantEdgeStrokeFunction(
				(float) graphSettings.getEdgeSize(e));
		return edgeStrokeFunction.getStroke(e);
	}

	/**
	 * Implemented for VertexPaintFunction Returns the color with which the
	 * vertex needs to be filled The color is determined by map entry
	 */
	public Paint getFillPaint(Vertex v) {
		Color fillColor = graphSettings.getNodeColor(v);
		ConstantVertexPaintFunction cvpf = new ConstantVertexPaintFunction(
				Color.BLACK, fillColor);

		return cvpf.getFillPaint(v);
	}

	public static Graph getGraph() {
		return graph;
	}

	public DefaultModalGraphMouse getGraphMouse() {
		return graphMouse;
	}

	public JComponent getGzsp() {
		return visualizationViewerScrollPane;
	}

	/**
	 * Implementes for EdgeStringer Retruns the edgeLabel for a given edge
	 */
	public String getLabel(ArchetypeEdge e) {

		return graphSettings.getEdgeLabel((Edge) e);
	}

	/**
	 * Implemented for VertexShapeFunction Returns shape of vertex by looking
	 * for an entry in map
	 */
	public Shape getShape(Vertex v) {
		NodeProperty.NodeShape shape = graphSettings.getNodeShape(v);
		int size = graphSettings.getNodeSize(v);
		EllipseVertexShapeFunction basicCircle = new EllipseVertexShapeFunction();
		Shape returnShape = basicCircle.getShape(v, 10 + (5 * size));
		switch (shape) {
		case Circle:
			EllipseVertexShapeFunction circle = new EllipseVertexShapeFunction();
			returnShape = circle.getShape(v, 10 + (5 * size));
			break;
		case Square:
			PolygonVertexShapeFunction square = new PolygonVertexShapeFunction();
			returnShape = square.getShape(v, 10 + (5 * size), 4);
			break;
		case Pentagon:
			PolygonVertexShapeFunction pentagon = new PolygonVertexShapeFunction();
			returnShape = pentagon.getShape(v, 10 + (5 * size), 5);
			break;
		case Hexagon:
			PolygonVertexShapeFunction hexagon = new PolygonVertexShapeFunction();
			returnShape = hexagon.getShape(v, 10 + (5 * size), 6);
			break;
		case Triangle:
			PolygonVertexShapeFunction triangle = new PolygonVertexShapeFunction();
			returnShape = triangle.getShape(v, 10 + (5 * size), 3);
			break;
		case Star:
			EllipseVertexShapeFunction star = new EllipseVertexShapeFunction();
			returnShape = star.getShape(v, NodeProperty.NodeShape.Star,
					10 + (5 * size));
			break;
		case RoundedRectangle:
			EllipseVertexShapeFunction roundRect = new EllipseVertexShapeFunction();
			returnShape = roundRect.getShape(v,
					NodeProperty.NodeShape.RoundedRectangle, 10 + (5 * size));
			break;
		}
		return returnShape;
	}

	public VisualizationModel getVisualizationModel() {
		return visualizationModel;
	}

	public static VisualizationViewer getVv() {
		return visualizationViewer;
	}

	/**
	 * Hides edge labels
	 */
	public void hideEdgeLabels() {
		EdgeStringer stringer = new EdgeStringer() {
			public String getLabel(ArchetypeEdge e) {
				return "";
			}
		};
		this.setEdgeStringer(stringer);
		visualizationViewer.repaint();
	}

	/**
	 * Hides the labels of nodes
	 */
	public void hideNodeLabels() {
		VertexStringer vertexStringer = new VertexStringer() {
			public String getLabel(ArchetypeVertex v) {
				return null;
			}
		};
		this.setVertexStringer(vertexStringer);
		visualizationViewer.repaint();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.uci.ics.jung.graph.decorators.EdgeShapeFunction#getShape(edu.uci.ics.jung.graph.Edge)
	 */
	public Shape getShape(Edge e) {
		Shape returnShape = null;
		EdgeShapeFunction edgeShapeFunction;
		switch (graphSettings.getEdgeShape(e)) {
		case Line:
			edgeShapeFunction = new EdgeShape.Line();
			returnShape = edgeShapeFunction.getShape(e);
			break;
		case QuadCurve:
			edgeShapeFunction = new EdgeShape.QuadCurve();
			returnShape = edgeShapeFunction.getShape(e);
			break;
		case CubicCurve:
			edgeShapeFunction = new EdgeShape.CubicCurve();
			returnShape = edgeShapeFunction.getShape(e);
			break;
		}
		return returnShape;
		// edgeShapeFunction = new EdgeShape.Line();
		// returnShape = edgeShapeFunction.getShape(e);
		// return returnShape;
	}

	/**
	 * Resizes edges by adjusting thickness
	 */
	public void reSizeEdges(float strokeWeight) {
		this
				.setEdgeStrokeFunction(new ConstantEdgeStrokeFunction(
						strokeWeight));
		visualizationViewer.repaint();

	}

	public void updateGraphSettings() {
		Iterator iterator = graphSettings.getQAsettingsIterator();
		// graphSettings.emptyEdgeSettingsMap();
		graph.removeAllEdges();
		while (iterator.hasNext()) {
			GraphSettingsEntry entry = (GraphSettingsEntry) iterator.next();
			GraphQuestionSelectionPair graphQuestion = entry.getGraphQuestion();
			if ((graphQuestion.getCategory() == Question.ALTER_QUESTION)
					&& (entry.getType() == GraphSettingType.Node)) {
				NodeProperty nodeProperty = (NodeProperty) entry.getProperty();
				NodeProperty.NodePropertyType prop = nodeProperty.getProperty();
				Question question = graphQuestion.getQuestion();
				Selection selection = graphQuestion.getSelection();
				GraphData graphData = new GraphData(egoClient);
				List<Integer> alterList = graphData.getAlterNumbers(question,
						selection);

				switch (prop) {
				case Color:
					for (int alter : alterList) {
						graphSettings.setNodeColor(vertexArray[alter],
								nodeProperty.getColor());
					}
					break;
				case Shape:
					for (int alter : alterList) {
						graphSettings.setNodeShape(vertexArray[alter],
								nodeProperty.getShape());
					}
					break;
				case Size:
					for (int alter : alterList) {
						graphSettings.setNodeSize(vertexArray[alter],
								nodeProperty.getSize());
					}
					break;
				case Label:
					for (int alter : alterList) {
						graphSettings.setNodeLabel(vertexArray[alter],
								nodeProperty.getLabel());
					}
					break;
				}
			} else if (graphQuestion.getCategory() == 0) // structural
			// measure
			{
				NodeProperty nodeProperty = (NodeProperty) entry.getProperty();
				NodeProperty.NodePropertyType prop = nodeProperty.getProperty();
				if (graphQuestion.getSelection().getString() == "DegreeCentrality") {
					switch (prop) {
					case Color:
						applyDegreeCentrality(NodeProperty.NodePropertyType.Color);
						break;
					case Size:
						applyDegreeCentrality(NodeProperty.NodePropertyType.Size);
						break;
					}
				} else { // Degree centrality
					switch (prop) {
					case Color:
						applyBetweennessCentrality(NodeProperty.NodePropertyType.Color);
						break;
					case Size:
						applyBetweennessCentrality(NodeProperty.NodePropertyType.Size);
						break;
					}
				}
			}
			// Edge property manipulation
			else if ((graphQuestion.getCategory() == Question.ALTER_PAIR_QUESTION)
					&& (entry.getType() == GraphSettingType.Edge)) {
				EdgeProperty edgeProperty = (EdgeProperty) entry.getProperty();
				EdgeProperty.EdgePropertyType prop = edgeProperty.getProperty();
				//System.out.println("prop value is " +prop.toString());
				
				GraphData graphData = new GraphData(egoClient);
				List<Pair> vPair = graphData.getAlterPairs(graphQuestion);
				//System.out.println("Property to be updated:" + prop
				//		+ " GraphQuestion:" + graphQuestion.toString());
				switch (prop) {
				case Color:
					for (Pair pair : vPair) {
						Iterator edgeIterator = graphSettings.getEdgeIterator();
						boolean edgeUpdated = false;
						while (edgeIterator.hasNext()) {
							Edge edge = (Edge) edgeIterator.next();
							 //System.out.println("Edge:" + edge.toString());
							if ((edge.getEndpoints().getFirst()
									.equals(vertexArray[(Integer) pair
											.getFirst()]))
									&& (edge.getEndpoints().getSecond()
											.equals(vertexArray[(Integer) pair
													.getSecond()]))) {
							
								 //System.out.println("EDGE color is " +edgeProperty.getColor());
								graphSettings.setEdgeColor(edge, edgeProperty
										.getColor());
								graphSettings.setEdgeVisible(edge, edgeProperty.isVisible());
								edgeUpdated = true;
								break;
							}
						}
						if (edgeUpdated == false) {
							UndirectedSparseEdge newEdge = new UndirectedSparseEdge(
									vertexArray[(Integer) pair.getFirst()],
									vertexArray[(Integer) pair.getSecond()]);
							// System.out.println(newEdge.toString()
							// + " created with chosen color");
							graphSettings.setEdgeColor(newEdge, edgeProperty
									.getColor());
							graphSettings.setEdgeVisible(newEdge, edgeProperty.isVisible());
						}
					}

					break;
				case Shape:
					for (Pair pair : vPair) {
						Iterator edgeIterator = graphSettings.getEdgeIterator();
						boolean edgeUpdated = false;
						while (edgeIterator.hasNext()) {
							Edge edge = (Edge) edgeIterator.next();
							if ((edge.getEndpoints().getFirst()
									.equals(vertexArray[(Integer) pair
											.getFirst()]))
									&& (edge.getEndpoints().getSecond()
											.equals(vertexArray[(Integer) pair
													.getSecond()]))) {
								graphSettings.setEdgeShape(edge, edgeProperty
										.getShape());
								graphSettings.setEdgeVisible(edge, edgeProperty.isVisible());
								edgeUpdated = true;
								break;
							}
						}
						if (edgeUpdated == false) {
							UndirectedSparseEdge newEdge = new UndirectedSparseEdge(
									vertexArray[(Integer) pair.getFirst()],
									vertexArray[(Integer) pair.getSecond()]);
							graphSettings.setEdgeShape(newEdge, edgeProperty
									.getShape());
							graphSettings.setEdgeVisible(newEdge, edgeProperty.isVisible());
						}
					}
					break;
				case Size:
					for (Pair pair : vPair) {
						Iterator edgeIterator = graphSettings.getEdgeIterator();
						boolean edgeUpdated = false;
						while (edgeIterator.hasNext()) {
							Edge edge = (Edge) edgeIterator.next();
							if ((edge.getEndpoints().getFirst()
									.equals(vertexArray[(Integer) pair
											.getFirst()]))
									&& (edge.getEndpoints().getSecond()
											.equals(vertexArray[(Integer) pair
													.getSecond()]))) {
								graphSettings.setEdgeSize(edge, edgeProperty
										.getSize());
								graphSettings.setEdgeVisible(edge, edgeProperty.isVisible());
								edgeUpdated = true;
								break;
							}
						}
						if (edgeUpdated == false) {
							UndirectedSparseEdge newEdge = new UndirectedSparseEdge(
									vertexArray[(Integer) pair.getFirst()],
									vertexArray[(Integer) pair.getSecond()]);
							graphSettings.setEdgeSize(newEdge, edgeProperty
									.getSize());
							graphSettings.setEdgeVisible(newEdge, edgeProperty.isVisible());
						}
					}
				}
			}
		}
		updateEdges();
		visualizationViewer.repaint();
	}

	private float max(float[] array) {
		float max = array[0];
		for (int i = 1; i < array.length; i++) {
			if (array[i] > max) {
				max = array[i];
			}
		}
		return max;
	}

	private void applyDegreeCentrality(NodeProperty.NodePropertyType property) {

		float[] degreeCentrality = new float[egoClient.getInterview().getNumAlters()];
		float[] scaledDegreeCentrality = new float[egoClient.getInterview()
				.getNumAlters()];
		for (int i = 0; i < egoClient.getInterview().getNumAlters(); i++) {
			degreeCentrality[i] = new Float(
					egoClient.getInterview().getStats().degreeArray[i]
							/ ((float) (egoClient.getInterview().getStats().proximityMatrix.length - 1)));
		}
		// scale the values
		float maximum = max(degreeCentrality);
		for (int i = 0; i < degreeCentrality.length; i++) {
			scaledDegreeCentrality[i] = (1 / maximum) * degreeCentrality[i];
		}

		for (int i = 0; i < scaledDegreeCentrality.length; i++) {
			float grayPercentage = 1 - scaledDegreeCentrality[i];
			if (property == NodeProperty.NodePropertyType.Color) {
				Color nodeColor = new Color(grayPercentage, grayPercentage,
						grayPercentage);
				graphSettings.setNodeColor(vertexArray[i], nodeColor);
			} else if (property == NodeProperty.NodePropertyType.Size) {
				int size = Math.round(1 + 2 * scaledDegreeCentrality[i]);
				graphSettings.setNodeSize(vertexArray[i], size);
			}
		}
	}

	private void applyBetweennessCentrality(NodeProperty.NodePropertyType property) {

		float[] betweennessCentrality = new float[egoClient.getInterview()
				.getNumAlters()];
		float[] scaledBetweennessCentrality = new float[egoClient.getInterview()
				.getNumAlters()];
		for (int i = 0; i < egoClient.getInterview().getNumAlters(); i++) {
			double big = egoClient.getInterview().getStats().proximityMatrix.length - 1;
			big *= big;
			betweennessCentrality[i] = new Float(
					egoClient.getInterview().getStats().betweennessArray[i] / big);
		}
		// scale the values
		float maximum = max(betweennessCentrality);
		for (int i = 0; i < betweennessCentrality.length; i++) {
			scaledBetweennessCentrality[i] = (1 / maximum)
					* betweennessCentrality[i];
		}

		for (int i = 0; i < scaledBetweennessCentrality.length; i++) {
			float grayPercentage = 1 - scaledBetweennessCentrality[i];
			if (property == NodeProperty.NodePropertyType.Color) {
				Color nodeColor = new Color(grayPercentage, grayPercentage,
						grayPercentage);
				graphSettings.setNodeColor(vertexArray[i], nodeColor);
			} else if (property == NodeProperty.NodePropertyType.Size) {
				int size = Math.round(1 + 2 * scaledBetweennessCentrality[i]);
				graphSettings.setNodeSize(vertexArray[i], size);
			}
		}
	}

	public void updateGraphSettings(Object updateValue, int nodeIndex,
			int updateParam) {
		// update param
		// 1: Label
		// 2: Color
		// 3: Shape
		// 4: Size
		switch (updateParam) {
		case 1:
			graphSettings.setNodeLabel(vertexArray[nodeIndex],
					(String) updateValue);
			break;
		case 2:
			graphSettings.setNodeColor(vertexArray[nodeIndex],
					(Color) updateValue);
			break;
		case 3:
			graphSettings.setNodeShape(vertexArray[nodeIndex],
					(NodeProperty.NodeShape) updateValue);
			break;
		case 4:
			graphSettings.setNodeSize(vertexArray[nodeIndex], Integer
					.parseInt((String) updateValue));
			break;

		}
	}

	public void addQAsettings(GraphQuestionSelectionPair graphQuestion,
			NodeProperty nodeProperty) {
		graphSettings.addQAsetting(graphQuestion, nodeProperty);
	}

	public void addQAsettings(GraphQuestionSelectionPair graphQuestion,
			EdgeProperty edgeProperty) {
		graphSettings.addQAsetting(graphQuestion, edgeProperty);
	}

	public String getToolTipText(Vertex v) {
		String text = graphSettings.getNodeToolTipText(v);
		// System.out.println(text);
		return text;
	}

	public String getToolTipText(Edge e) {
		return e.toString();
	}

	public String getToolTipText(MouseEvent event) {
		return ((JComponent) event.getSource()).getToolTipText();
	}

	public Iterator getSettingsIterator() {
		return graphSettings.getQAsettingsIterator();
	}

    public static GraphSettings getGraphSettings()
    {
        return graphSettings;
    }

    public static void setGraphSettings(GraphSettings gs){
    	
    	graphSettings = gs;
    }
    
    public static VisualizationViewer getVisualizationViewer()
    {
        return visualizationViewer;
    }

	public boolean getShowNodeLabels() {
		return showNodeLabels;
	}

	public void setShowNodeLabels(boolean showNodeLabels) {
		this.showNodeLabels = showNodeLabels;
	}
}
