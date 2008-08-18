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
package com.endlessloopsoftware.egonet.web.applet;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JApplet;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;

import edu.uci.ics.jung.graph.Edge;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.Vertex;
import edu.uci.ics.jung.utils.TestGraphs;
import edu.uci.ics.jung.visualization.FRLayout;
import edu.uci.ics.jung.visualization.GraphDraw;
import edu.uci.ics.jung.visualization.ISOMLayout;
import edu.uci.ics.jung.visualization.Layout;
import edu.uci.ics.jung.visualization.PickedInfo;
import edu.uci.ics.jung.visualization.Renderer;
import edu.uci.ics.jung.visualization.SpringLayout;
import edu.uci.ics.jung.visualization.contrib.CircleLayout;
import edu.uci.ics.jung.visualization.contrib.KKLayout;

public class ShowLayouts extends JApplet {

	/**
	 * @author danyelf
	 */

	private static final class LayoutChooser implements ActionListener 
	{
		private final JComboBox jcb;
		private final Graph g;
		private final GraphDraw gd;
		private LayoutChooser(JComboBox jcb, Graph g, GraphDraw gd) 
		{
			super();
			this.jcb = jcb;
			this.g = g;
			this.gd = gd;
		}
		
		public void actionPerformed(ActionEvent arg0) 
		{
			Object[] constructorArgs = { g };

			Class layoutC = (Class) jcb.getSelectedItem();
			System.out.println("Setting to " + layoutC);
			Class lay = layoutC;
			
			try 
			{
				Constructor constructor = lay.getConstructor(constructorArgsWanted);
				Object o = constructor.newInstance(constructorArgs);
				Layout l = (Layout) o;
				gd.setGraphLayout(l);
				gd.restartLayout();
			} 
			catch (Exception e) 
			{
				System.out.println("Can't handle " + lay);
			}
		}
	}

	static final Class[] constructorArgsWanted = { Graph.class };

	public static void main(String[] args) {
		JPanel jp = getGraphPanel();

		JFrame jf = new JFrame();
		jf.getContentPane().add(jp);
		jf.pack();
		jf.show();
	}

	private static JPanel getGraphPanel() 
	{
		Graph g = TestGraphs.getOneComponentGraph();
		GraphDraw gd = new GraphDraw(g);
		gd.setBackground(Color.WHITE);
		gd.setRenderer(new MyRenderer());
		gd.hideStatus();
		JPanel jp = new JPanel();
		jp.setBackground(Color.WHITE);
		jp.setLayout(new BorderLayout());
		jp.add(gd, BorderLayout.CENTER);
		Class[] combos = getCombos();
		final JComboBox jcb = new JComboBox(combos);
		jcb.setSelectedItem(SpringLayout.class);
		jcb.addActionListener(new LayoutChooser(jcb, g, gd));
		jp.add(jcb, BorderLayout.NORTH);
		return jp;
	}

	public void start() {
		this.getContentPane().add(getGraphPanel());
	}

	/**
	 * @return
	 */
	private static Class[] getCombos() {
		List layouts = new ArrayList();
		layouts.add(KKLayout.class);
		layouts.add(FRLayout.class);
		layouts.add(CircleLayout.class);
		layouts.add(SpringLayout.class);
		layouts.add(ISOMLayout.class);
		return (Class[]) layouts.toArray(new Class[0]);
	}

	public static class MyRenderer implements Renderer {

		/**
		 * @see edu.uci.ics.jung.visualization.Renderer#paintVertex(java.awt.Graphics,
		 *         edu.uci.ics.jung.graph.Vertex, int, int)
		 */
		public void paintVertex(Graphics g, Vertex v, int x, int y) {
			g.setColor(Color.black);
			g.fillOval(x - 3, y - 3, 6, 6);
		}

		/**
		 * @see edu.uci.ics.jung.visualization.Renderer#paintEdge(java.awt.Graphics,
		 *         edu.uci.ics.jung.graph.Edge, int, int, int, int)
		 */
		public void paintEdge(Graphics g, Edge e, int x1, int y1, int x2, int y2) {
			g.setColor(Color.GRAY);
			g.drawLine(x1, y1, x2, y2);
		}

		/**
		 * @see edu.uci.ics.jung.visualization.Renderer#setPickedKey(edu.uci.ics.jung.visualization.PickedInfo)
		 */
		public void setPickedKey(PickedInfo pk) {
			// TODO Auto-generated method stub MyRenderer:setPickedKey

		}

	}

}
