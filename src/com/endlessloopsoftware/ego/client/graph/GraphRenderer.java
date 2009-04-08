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

import org.apache.commons.collections15.Transformer;
import org.egonet.util.listbuilder.Selection;
import com.endlessloopsoftware.ego.client.EgoClient;
import com.endlessloopsoftware.ego.client.statistics.Statistics;
import com.endlessloopsoftware.egonet.Interview;
import com.endlessloopsoftware.egonet.Question;
import com.endlessloopsoftware.egonet.Shared;
import com.endlessloopsoftware.egonet.Shared.QuestionType;

import edu.uci.ics.jung.algorithms.layout.FRLayout;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.UndirectedSparseGraph;
import edu.uci.ics.jung.graph.util.Context;
import edu.uci.ics.jung.graph.util.Pair;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.CrossoverScalingControl;
import edu.uci.ics.jung.visualization.control.DefaultModalGraphMouse;
import edu.uci.ics.jung.visualization.control.ScalingControl;
import edu.uci.ics.jung.visualization.DefaultVisualizationModel;
import edu.uci.ics.jung.visualization.GraphZoomScrollPane;
import edu.uci.ics.jung.visualization.RenderContext;
import edu.uci.ics.jung.visualization.VisualizationModel;
import edu.uci.ics.jung.visualization.decorators.AbstractEdgeShapeTransformer;
import edu.uci.ics.jung.visualization.decorators.EdgeShape;
import edu.uci.ics.jung.visualization.picking.ShapePickSupport;
import edu.uci.ics.jung.visualization.util.VertexShapeFactory;

public class GraphRenderer /*implements
		Transformer<Vertex,Shape>, Transformer<Vertex,Paint>, Transformer<Edge,Shape>,
		Transformer<Edge,Paint>, Transformer<Edge,String>, Transformer<Vertex,String>, Transformer<Edge,Stroke>,
		ToolTipFunction */ {

	public static GraphSettings graphSettings;

	private static VisualizationViewer<Vertex,Edge> visualizationViewer;

	private GraphZoomScrollPane visualizationViewerScrollPane;

	private DefaultModalGraphMouse graphMouse;

	private VisualizationModel<Vertex,Edge> visualizationModel;

	private final String[] alterList;

	private static Graph<Vertex, Edge> graph;

	public static boolean showEdgeWeights = false;

	private boolean showNodeLabels = true;

	private EgoClient egoClient;
	
	public GraphRenderer(EgoClient egoClient) {
		this.egoClient=egoClient;
		graph = new UndirectedSparseGraph<Vertex, Edge>();
		
		Interview interview = egoClient.getInterview();
		
		Statistics stats = interview.getStats();
		alterList = stats.alterList;
		
		for (String alter : alterList) {
				graph.addVertex(new Vertex(alter));
		}
		
		graphSettings = new GraphSettings(egoClient, this);
	}

	class EmptyEdgeStringer implements Transformer<Edge, String> {
		public String transform(Edge e) {
			return "";
		}
	};
	
	/**
	 * Redraws the graph with the provided layout
	 * 
	 * @param Class Layout
	 */
	@SuppressWarnings("unchecked")
	public void changeLayout(Class<?> layout) throws Exception {
			Constructor<?> constructor = layout.getConstructor(new Class<?>[] { Graph.class });
			Object o = constructor.newInstance(graph);
			Layout l = (Layout) o;

			
			if (l instanceof FRLayout) {
				FRLayout frLayout = (FRLayout) l;
				frLayout.setMaxIterations(1000);
			}
			
			l.reset();
			visualizationViewer.getModel().setGraphLayout(l);
			//visualizationViewer.stop();
			//visualizationViewer.setGraphLayout(l, false);
			//visualizationViewer.restart();

	}

	/**
	 * Changes the size of the layout (canvas size) then redraws the graph 
	 * to fit the nodes to the new layout size
	 * 
	 * @param int x -- width
	 * @param int y -- height
	 */
	@SuppressWarnings("unchecked")
	public void changeLayoutSize(int x, int y) {
		try {
			Dimension dim = visualizationViewer.getGraphLayout().getSize();
			Layout layout = visualizationViewer.getGraphLayout();
			
			if(dim.width + x < 5 || dim.height + y < 5){
				JOptionPane.showMessageDialog(null, 
						new String("Layout Size Out of Bounds"), "Error", 
						JOptionPane.ERROR_MESSAGE);
				System.out.println("Less than 5");
			}
			else{
				Dimension d = new Dimension(dim.width + x, dim.height + y);
				//visualizationViewer.stop();
				visualizationViewer.getModel().setGraphLayout(layout, d);
				//visualizationViewer.restart();
			}					
		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}

	/**
	 * Redraws the graph without changing the layout or layout size
	 *
	 */
	@SuppressWarnings("unchecked")
	public void reiterate(){
		Dimension dim = visualizationViewer.getGraphLayout().getSize();
		Layout layout = visualizationViewer.getGraphLayout();
		
		
		layout.setSize(dim);
		
		//visualizationViewer.stop();
		visualizationViewer.getModel().setGraphLayout(layout);
		//visualizationViewer.restart();
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
		visualizationModel = new DefaultVisualizationModel<Vertex,Edge>(new ELSFRLayout2(graph));

		// create the regular viewer and scroller
		visualizationViewer = new VisualizationViewer<Vertex,Edge>(visualizationModel);
		
		visualizationViewer.setPickSupport(new ShapePickSupport<Vertex,Edge>(visualizationViewer));
		//visualizationViewer.setToolTipFunction(this);
		visualizationViewer.setVertexToolTipTransformer(new VertexTooltipTransformer());
		
		
		visualizationViewer.setGraphMouse(graphMouse);
		visualizationViewer.setBackground(Color.WHITE);

		RenderContext<Vertex, Edge> ctx = visualizationViewer.getRenderContext();
		
		//this.setVertexShapeFunction(this);
		ctx.setVertexShapeTransformer(new VertexShapeTransformer());
		
		//this.setVertexPaintFunction(this);
		ctx.setVertexFillPaintTransformer(new VertexPaintTransformer());
		
		//this.setEdgeShapeFunction(this);
		ctx.setEdgeShapeTransformer(new EdgeShapeTransformer());
		
		//this.setEdgePaintFunction(this);
		ctx.setEdgeDrawPaintTransformer(new EdgePaintTransformer());
		
		//this.setEdgeStrokeFunction(this);
		ctx.setEdgeStrokeTransformer(new EdgeStrokeTransformer());

		//this.setEdgeStringer(emptyStringer);
		ctx.setEdgeLabelTransformer(new EmptyEdgeStringer());
		
		//this.setVertexStringer(this);
		ctx.setVertexLabelTransformer(new VertexLabelTransformer());

		
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

		return visualizationViewerScrollPane;
	}

	/**
	 * creates the nodes for every alter creates edges for entries in adjacency
	 * matrix
	 */
	public  void updateEdges() {
		removeAllEdges(graph);
		Iterator<Edge> edgeIterator = graphSettings.getEdgeIterator();
		while (edgeIterator.hasNext()) {
			Edge edge = edgeIterator.next();
			if (graphSettings.isEdgeVisible(edge)) {
				graph.addEdge(edge, Arrays.asList(new Vertex(edge.pair.getFirst()), new Vertex(edge.pair.getSecond())));
			}
		}
	}
	
	private void removeAllEdges(Graph<Vertex,Edge> g) {
		while(g.getEdgeCount() > 0) {
			for(Edge e : new ArrayList<Edge>(g.getEdges())) {
				g.removeEdge(e);
			}
		}
	}

	class VertexLabelTransformer implements Transformer<Vertex,String> {
		public String transform(Vertex v) {
			return graphSettings.getNodeLabel(v);
		}
	}

	/**
	 * Displays the edges of graph used to draw the edge
	 */
	public void drawEdgeLabels() {
		//this.setEdgeStringer(this);
		visualizationViewer.getRenderContext().setEdgeLabelTransformer(new EdgeLabelTransformer());
		
		// ??? not ported!
		//this.setEdgePaintFunction(new PickableEdgePaintTransformer(this,Color.black, Color.cyan));
		
		visualizationViewer.repaint();
	}

	/**
	 * Displays the labels of nodes
	 */
	public void drawNodeLabels() {
		visualizationViewer.getRenderContext().setVertexLabelTransformer(new VertexLabelTransformer());
		visualizationViewer.repaint();
	}

	/**
	 * Implemented for VertexPaintFunction Returns the color of the outline of
	 * the vertex Draw paint color is defaulted BLACK
	 */
	
	class VertexPaintTransformer implements Transformer<Vertex,Paint> {
		public Paint transform(Vertex v) {
			Color fillColor = graphSettings.getNodeColor(v);
			//ConstantVertexPaintFunction cvpf = new ConstantVertexPaintFunction(Color.BLACK, fillColor);
			
			return fillColor;
		}
	}
	
	/*class VertexPaintTransformer implements Transformer<Vertex,Paint> {
		public Paint transform(Vertex v) {
			Color fillColor = graphSettings.getNodeColor(v);
			return fillColor;
		}
	}*/

	/**
	 * (non-Javadoc)
	 * 
	 * @see edu.uci.ics.jung.graph.decorators.EdgePaintFunction#getFillPaint(edu.uci.ics.jung.graph.Edge)
	 * @see edu.uci.ics.jung.graph.decorators.EdgePaintFunction#getDrawPaint(edu.uci.ics.jung.graph.Edge)
	 */

	class EdgePaintTransformer implements Transformer<Edge,Paint> {
		public Paint transform(Edge e) {
			Color edgeColor = graphSettings.getEdgeColor(e);
			return edgeColor;
		}
	}

	/**
	 * (non-Javadoc)
	 * 
	 * @see edu.uci.ics.jung.graph.decorators.EdgeStrokeFunction#getStroke(edu.uci.ics.jung.graph.Edge)
	 
	public Stroke getStroke(Edge e) {
		ConstantEdgeStrokeFunction edgeStrokeFunction = new ConstantEdgeStrokeFunction(
				(float) graphSettings.getEdgeSize(e));
		return edgeStrokeFunction.getStroke(e);
	}
	*/


	public static Graph<Vertex, Edge> getGraph() {
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
	
	class EdgeLabelTransformer implements Transformer<Edge,String> {
		public String transform(Edge e) {
			return graphSettings.getEdgeLabel((Edge) e);
		}
	}

	/**
	 * Implemented for VertexShapeFunction Returns shape of vertex by looking
	 * for an entry in map
	 */
	class VertexShapeTransformer implements Transformer<Vertex,Shape> {
		
		VertexShapeFactory<Vertex> v;
		public Shape transform(Vertex v) {
			NodeProperty.NodeShape shape = graphSettings.getNodeShape(v);
			int size = graphSettings.getNodeSize(v);
			
			EllipseVertexShapeFunction basicCircle = new EllipseVertexShapeFunction();
			Shape returnShape = basicCircle.getShape(v, 10 + (5 * size));
			switch (shape) {
			case Circle:
				;
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
		
	}
		
	public VisualizationModel getVisualizationModel() {
		return visualizationModel;
	}

	public static VisualizationViewer<Vertex,Edge> getVv() {
		return visualizationViewer;
	}

	/**
	 * Hides edge labels
	 */
	public void hideEdgeLabels() {
		Transformer<Edge,String> edgeStringer = new Transformer<Edge, String>() {
			public String transform(Edge e) {
				return null;
			}
		};
		//this.setEdgeStringer(stringer);
		visualizationViewer.getRenderContext().setEdgeLabelTransformer(edgeStringer);
		visualizationViewer.repaint();
	}

	/**
	 * Hides the labels of nodes
	 */
	public void hideNodeLabels() {
		Transformer<Vertex,String> vertexStringer = new Transformer<Vertex,String>() {
			public String transform(Vertex v) {
				return null;
			}
		};
		visualizationViewer.getRenderContext().setVertexLabelTransformer(vertexStringer);
		visualizationViewer.repaint();
	}

	@SuppressWarnings("unchecked")
	class EdgeShapeTransformer implements Transformer<Context<Graph<Vertex, Edge>, Edge>, Shape> {
		public Shape transform(Context<Graph<Vertex, Edge>, Edge> edge) {
			Edge e = edge.element;
			
			AbstractEdgeShapeTransformer<Vertex,Edge> edgeShapeFunction;
			switch (graphSettings.getEdgeShape(e)) {
			case QuadCurve:
				edgeShapeFunction = new EdgeShape.QuadCurve();
				break;
			case CubicCurve:
				edgeShapeFunction = new EdgeShape.CubicCurve();
				break;
			default:
				edgeShapeFunction = new EdgeShape.Line();
				break;
			}
			
			
			return edgeShapeFunction.transform(edge);
			// edgeShapeFunction = new EdgeShape.Line();
			// returnShape = edgeShapeFunction.getShape(e);
			// return returnShape;
		}
	}

	class EdgeStrokeTransformer implements Transformer<Edge,Stroke> {
		BasicStroke stroke;
		
		public EdgeStrokeTransformer() {
			this.stroke = new BasicStroke();
		}
		
		public EdgeStrokeTransformer(float weight) {
			this.stroke = new BasicStroke(weight);
		}
		
		public Stroke transform(Edge arg0) {
			return stroke;
		}
		
	}
	
	public void reSizeEdges(float strokeWeight) {
		visualizationViewer.getRenderContext().setEdgeStrokeTransformer(new EdgeStrokeTransformer(strokeWeight));
		visualizationViewer.repaint();

	}

	public void updateGraphSettings() {
		Iterator iterator = graphSettings.getQAsettingsIterator();
		// graphSettings.emptyEdgeSettingsMap();
		removeAllEdges(graph);
		while (iterator.hasNext()) {
			GraphSettingsEntry entry = (GraphSettingsEntry) iterator.next();
			GraphQuestionSelectionPair graphQuestion = entry.getGraphQuestion();
			if ((graphQuestion.getCategory() == Shared.QuestionType.ALTER)
					&& (entry.getType() == GraphSettingType.Node)) {
				NodeProperty nodeProperty = (NodeProperty) entry.getProperty();
				NodeProperty.NodePropertyType prop = nodeProperty.getProperty();
				Question question = graphQuestion.getQuestion();
				Selection selection = graphQuestion.getSelection();
				GraphData graphData = new GraphData(egoClient);
				List<Integer> myAlterList = graphData.getAlterNumbers(question, selection);

				switch (prop) {
				case Color:
					for (int alter : myAlterList) {
						graphSettings.setNodeColor(new Vertex(alterList[alter]), nodeProperty.getColor());
					}
					break;
				case Shape:
					for (int alter : myAlterList) {
						graphSettings.setNodeShape(new Vertex(alterList[alter]), nodeProperty.getShape());
					}
					break;
				case Size:
					for (int alter : myAlterList) {
						graphSettings.setNodeSize(new Vertex(alterList[alter]), nodeProperty.getSize());
					}
					break;
				case Label:
					for (int alter : myAlterList) {
						graphSettings.setNodeLabel(new Vertex(alterList[alter]), nodeProperty.getLabel());
					}
					break;
				}
			} else if (graphQuestion.getCategory() == QuestionType.STUDY_CONFIG) // structural
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
			else if ((graphQuestion.getCategory() == Shared.QuestionType.ALTER_PAIR)
					&& (entry.getType() == GraphSettingType.Edge)) {
				EdgeProperty edgeProperty = (EdgeProperty) entry.getProperty();
				EdgeProperty.EdgePropertyType prop = edgeProperty.getProperty();
				//System.out.println("prop value is " +prop.toString());
				
				GraphData graphData = new GraphData(egoClient);
				List<Pair<Integer>> vPair = graphData.getAlterPairs(graphQuestion);
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
									.equals(alterList[(Integer) pair
											.getFirst()]))
									&& (edge.getEndpoints().getSecond()
											.equals(alterList[(Integer) pair
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
							Edge newEdge = new Edge(
									new Vertex(alterList[(Integer) pair.getFirst()]),
									new Vertex(alterList[(Integer) pair.getSecond()]));
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
							if ((edge.getEndpoints().getFirst().equals(alterList[(Integer) pair.getFirst()]))
											&& (edge.getEndpoints().getSecond().equals(alterList[(Integer) pair.getSecond()]))) {
								graphSettings.setEdgeShape(edge, edgeProperty
										.getShape());
								graphSettings.setEdgeVisible(edge, edgeProperty.isVisible());
								edgeUpdated = true;
								break;
							}
						}
						if (edgeUpdated == false) {
							Edge newEdge = new Edge(
									alterList[(Integer) pair.getFirst()],
									alterList[(Integer) pair.getSecond()]);
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
							if ((edge.pair.getFirst().equals(alterList[(Integer) pair.getFirst()]))
									&& (edge.pair.getSecond().equals(alterList[(Integer) pair.getSecond()]))) {
								graphSettings.setEdgeSize(edge, edgeProperty.getSize());
								graphSettings.setEdgeVisible(edge, edgeProperty.isVisible());
								edgeUpdated = true;
								break;
							}
						}
						if (edgeUpdated == false) {
							Edge newEdge = new Edge(
									alterList[(Integer) pair.getFirst()],
									alterList[(Integer) pair.getSecond()]);
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
				Color nodeColor = new Color(grayPercentage, grayPercentage,grayPercentage);
				graphSettings.setNodeColor(new Vertex(alterList[i]), nodeColor);
			} else if (property == NodeProperty.NodePropertyType.Size) {
				int size = Math.round(1 + 2 * scaledDegreeCentrality[i]);
				graphSettings.setNodeSize(new Vertex(alterList[i]), size);
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
				graphSettings.setNodeColor(new Vertex(alterList[i]), nodeColor);
			} else if (property == NodeProperty.NodePropertyType.Size) {
				int size = Math.round(1 + 2 * scaledBetweennessCentrality[i]);
				graphSettings.setNodeSize(new Vertex(alterList[i]), size);
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
			graphSettings.setNodeLabel(new Vertex(alterList[nodeIndex]),
					(String) updateValue);
			break;
		case 2:
			graphSettings.setNodeColor(new Vertex(alterList[nodeIndex]),
					(Color) updateValue);
			break;
		case 3:
			graphSettings.setNodeShape(new Vertex(alterList[nodeIndex]),
					(NodeProperty.NodeShape) updateValue);
			break;
		case 4:
			graphSettings.setNodeSize(new Vertex(alterList[nodeIndex]), Integer
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

	class VertexTooltipTransformer implements Transformer<Vertex,String> {
		public String transform(Vertex v) {
			String text = graphSettings.getNodeToolTipText(v);
			// System.out.println(text);
			return text;
		}
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

	public String[] getAlterList() {
		return alterList;
	}
}
