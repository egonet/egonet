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

import java.awt.*;
import java.util.*;
import javax.swing.*;

import com.endlessloopsoftware.ego.client.graph.GraphTabPanel;

public class GraphPanel extends javax.swing.JPanel {
	private JSplitPane mainSplitPane;

	private JSplitPane rightSplitPane;

	// private JPanel mainPanel;

	private JPanel leftPanel;

	private JPanel topRightPanel;

	GraphRenderer graphRenderer;

	public GraphPanel() {
		init();
	}

	/**
	 * calls createRenderer(Statistics Frame) calls methods to create split
	 * panes
	 * 
	 * @param frame
	 */
	public void init() {
		graphRenderer = new GraphRenderer();
		// split the window into 3 and populate with appropriate panels
		createSplitPanel();
		this.setLayout(new BorderLayout());
		this.add(mainSplitPane, BorderLayout.CENTER);
	}

	/*
	 * Creating and populating separate panels for left pane, top right pane and
	 * bottom right pane
	 */

	/**
	 * Mehtod to create left panel and add graph to it
	 */
	private void createLeftPanel() {
		leftPanel = new JPanel();
		JComponent gzsp = graphRenderer.createGraph();
		leftPanel.setLayout(new BorderLayout());
		leftPanel.setPreferredSize(new Dimension(600, 600));
		leftPanel.add(gzsp, BorderLayout.CENTER);
	}

	/**
	 * Create Top right panel with graph, node and edge tabs
	 * 
	 */
	private void createTopRightPanel() {
		topRightPanel = new JPanel(new BorderLayout());

		JTabbedPane tabs = new JTabbedPane();

		tabs.add("Graph", new GraphTabPanel(graphRenderer));
		tabs.add("Edge Color", new EdgeColorPanel(graphRenderer));
		tabs.add("Edge Shape", new EdgeShapePanel(graphRenderer));
		tabs.add("Edge Size", new EdgeSizePanel(graphRenderer));
		tabs.add("Node Label", new NodeLabelPanel(graphRenderer));
		tabs.add("Node Color", new NodeColorPanel(graphRenderer));
		tabs.add("Node Shape", new NodeShapePanel(graphRenderer));
		tabs.add("Node Size", new NodeSizePanel(graphRenderer));
		tabs.add("Structural Measures", new StructuralMeasuresPanel(graphRenderer));

		topRightPanel.add(tabs);
	}

	/**
	 * Create panels for individual parts of the split window Create split panes
	 * to divide total window into three set properties of set property to male
	 * split pane contractible Split panes are populated with individual panels
	 * for the split window
	 */
	private void createSplitPanel() {
		createLeftPanel();
		createTopRightPanel();

		// Topright: 3 tabbed window; Bottom right: satellite view; left: graph
		// display
		JScrollPane topRightScrollPanel = new JScrollPane(topRightPanel);

		JComponent satellitePane = graphRenderer.createSatellitePane();
		satellitePane.setMinimumSize(satellitePane.getPreferredSize());

		rightSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
				topRightScrollPanel, satellitePane);

		rightSplitPane.setResizeWeight(0.75);
		rightSplitPane.setContinuousLayout(true);

		// with staellite pane
		// mainSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, leftPanel,
		// rightSplitPane);

		// without satellite pane
		mainSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, leftPanel,
				topRightScrollPanel);

		mainSplitPane.setResizeWeight(0.8);
		mainSplitPane.setContinuousLayout(true);
		mainSplitPane.setOneTouchExpandable(true);
	}
	
	public Iterator getSettingsIterator() {
		Iterator iterator = graphRenderer.getSettingsIterator();
		return iterator;
	}

}
