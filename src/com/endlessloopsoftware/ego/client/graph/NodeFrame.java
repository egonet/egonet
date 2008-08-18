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
