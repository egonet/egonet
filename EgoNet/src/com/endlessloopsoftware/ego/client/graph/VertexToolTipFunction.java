package com.endlessloopsoftware.ego.client.graph;

import edu.uci.ics.jung.graph.decorators.ToolTipFunctionAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JComponent;
import edu.uci.ics.jung.graph.Edge;
import edu.uci.ics.jung.graph.Vertex;

public class VertexToolTipFunction extends ToolTipFunctionAdapter {
	/**
	 * @param v
	 *            the Vertex
	 * @return toString on the passed Vertex
	 */
	public String getToolTipText(Vertex v) {
		
		return v.toString();
	}

	/**
	 * @param e
	 *            the Edge
	 * @return toString on the passed Edge
	 */
	public String getToolTipText(Edge e) {
		return e.toString();
	}

	public String getToolTipText(MouseEvent e) {
		return ((JComponent) e.getSource()).getToolTipText();
	}
}
