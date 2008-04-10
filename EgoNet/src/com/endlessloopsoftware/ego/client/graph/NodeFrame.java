package com.endlessloopsoftware.ego.client.graph;

import javax.swing.*;

public class NodeFrame extends JFrame{
	public NodeFrame(GraphRenderer graphRenderer) {
		JTabbedPane tabs = new JTabbedPane();
		tabs.add("Node Label", new NodeLabelPanel(graphRenderer));
		tabs.add("Node Color", new NodeColorPanel(graphRenderer));
		tabs.add("Node Shape", new NodeShapePanel(graphRenderer));
		tabs.add("Node Size", new NodeSizePanel(graphRenderer));
	}
}
