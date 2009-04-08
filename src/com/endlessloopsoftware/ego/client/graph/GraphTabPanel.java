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

import org.jdesktop.layout.GroupLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.*;

import java.util.*;

import edu.uci.ics.jung.algorithms.layout.CircleLayout;
import edu.uci.ics.jung.algorithms.layout.FRLayout;
import edu.uci.ics.jung.algorithms.layout.ISOMLayout;
import edu.uci.ics.jung.algorithms.layout.KKLayout;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.DefaultModalGraphMouse;
import edu.uci.ics.jung.visualization.control.CrossoverScalingControl;
import edu.uci.ics.jung.visualization.control.ScalingControl;

public class GraphTabPanel extends JPanel {
	private List<Class> layoutOptions;

	private DefaultModalGraphMouse graphMouse;

	private VisualizationViewer vv;

	private GraphRenderer graphRenderer;

	private GroupLayout layout;

	private JLabel layoutLabel;

	private JComboBox layoutCombo;

	private JLabel modeLabel;

	private JComboBox modeCombo;

	private JLabel bgcolorLabel;

	private JButton bgcolorButton;

	private JCheckBox showLabelChkBox;

	private JCheckBox showWeightChkBox;

	private JLabel zoomLabel;

	private JButton zoomInButton;

	private JButton zoomOutButton;
	
	private JLabel layoutSize;
	
	private JButton increaseLayoutSize;
	
	private JButton decreaseLayoutSize;
	
	private JButton reiterate;
	
	private ScalingControl scaler;

	public GraphTabPanel(Graph g, VisualizationViewer vv,
			DefaultModalGraphMouse graphMouse) {

		this.graphMouse = graphMouse;
		this.vv = vv;
		createComponents();
	}

	public GraphTabPanel(GraphRenderer gr) {
		this.graphRenderer = gr;
		GraphRenderer.getGraph();
		this.graphMouse = gr.getGraphMouse();
		this.vv = GraphRenderer.getVv();
		createComponents();
	}

	private void createComponents() {

		layout = new GroupLayout(this);
		this.setLayout(layout);
		scaler = new CrossoverScalingControl();
		
		layout.setAutocreateGaps(true);
		layout.setAutocreateContainerGaps(true);

		// label combo pair for choosing different layouts
		layoutLabel = new JLabel("Choose Layout");
		layoutLabel.setOpaque(true);

		// add all possible layouts to the layout combo
		layoutOptions = new ArrayList<Class>();
		layoutOptions.add(KKLayout.class); // Kamada-Kawai
		layoutOptions.add(FRLayout.class); // // Fruchterman-Reingold
		layoutOptions.add(CircleLayout.class); // Vertices randomly on a circle
		layoutOptions.add(ELSFRLayout2.class);
		layoutOptions.add(ISOMLayout.class); // Meyer's "Self-Organizing Map" layout
		Class[] layoutList = (Class[]) layoutOptions.toArray(new Class[0]);

		layoutCombo = new JComboBox(layoutList);

		// use a renderer to shorten the layout name presentation
		layoutCombo.setRenderer(new DefaultListCellRenderer() {
			public Component getListCellRendererComponent(JList list,
					Object value, int index, boolean isSelected,
					boolean cellHasFocus) {
				String valueString = value.toString();
				valueString = valueString.substring(valueString
						.lastIndexOf('.') + 1);
				return super.getListCellRendererComponent(list, valueString,
						index, isSelected, cellHasFocus);
			}
		});

		layoutCombo.setPreferredSize(new Dimension(20, 20));
		layoutCombo.setMaximumSize(new Dimension(20, 30));
		layoutCombo.setSelectedItem(ELSFRLayout2.class);
		layoutCombo.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// call renderer to render the graph with selected layout
				JComboBox cb = (JComboBox) e.getSource();
				Class lay = (Class) cb.getSelectedItem();
				try { graphRenderer.changeLayout(lay); }
				catch (Exception ex)
				{
					throw new RuntimeException(ex);
				}
			}
		});

		// label combo pair to choose mode of operation
		modeLabel = new JLabel("Choose Mode");
		modeLabel.setOpaque(true);

		modeCombo = graphMouse.getModeComboBox();
		modeCombo.setPreferredSize(new Dimension(20, 20));
		modeCombo.setMaximumSize(new Dimension(20, 30));
		modeCombo.addItemListener(((DefaultModalGraphMouse) vv.getGraphMouse())
				.getModeListener());

		// add label button pair for changing background color
		bgcolorLabel = new JLabel("BackGround");
		bgcolorLabel.setOpaque(true);

		bgcolorButton = new JButton();
		bgcolorButton.setText(" ");
		bgcolorButton.setBackground(vv.getBackground());
		
		bgcolorButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// code displaying a color box/palette to choose from
				Color c = JColorChooser.showDialog(null, "Choose color", vv
						.getBackground());
				if (c != null) {
					vv.setBackground(c);
					JButton colorButton = (JButton) e.getSource();
					colorButton.setBackground(c);
				}

			}
		});

		// create check boxes for showing labels and Edge weights
		showLabelChkBox = new JCheckBox("Show Node Labels");
		showLabelChkBox.setSelected(true);
		showLabelChkBox.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED)
					graphRenderer.drawNodeLabels();
				else
					graphRenderer.hideNodeLabels();

			}
		});

		showWeightChkBox = new JCheckBox("Show Edge Weights");
		showWeightChkBox.setSelected(false);
		showWeightChkBox.setVisible(false);
		showWeightChkBox.setEnabled(false);
		showWeightChkBox.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					GraphRenderer.showEdgeWeights = true;
					graphRenderer.drawEdgeLabels();
					graphRenderer.setShowNodeLabels(true);

				} else
					graphRenderer.hideEdgeLabels();
					graphRenderer.setShowNodeLabels(false);
			}
		});

		zoomLabel = new JLabel("Zoom: ");
		zoomInButton = new JButton("+");
		zoomInButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                scaler.scale(vv, 1.1f, vv.getCenter());
            }
        });
		zoomOutButton = new JButton("-");
		zoomOutButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                scaler.scale(vv, 1/1.1f, vv.getCenter());
            }
        });
		
		layoutSize = new JLabel("Layout Size:");
		increaseLayoutSize = new JButton("+");
		increaseLayoutSize.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				SwingWorker worker = new SwingWorker(){
					@Override
					protected Object doInBackground() throws Exception {
						decreaseLayoutSize.paintImmediately(new Rectangle());
						return null;
					}					
					public void done(){
						graphRenderer.changeLayoutSize(50, 50);
					}
				};
				worker.execute();				
			}
		});
		decreaseLayoutSize = new JButton("-");
		decreaseLayoutSize.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				SwingWorker worker = new SwingWorker(){
					@Override
					protected Object doInBackground() throws Exception {
						decreaseLayoutSize.paintImmediately(new Rectangle());
						return null;
					}					
					public void done(){
						graphRenderer.changeLayoutSize(-50, -50);
					}
				};
				worker.execute();				
			}
		});

		reiterate = new JButton("Reiterate");
		reiterate.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				SwingWorker worker = new SwingWorker(){
					@Override
					protected Object doInBackground() throws Exception {
						reiterate.paintImmediately(new Rectangle());
						return null;
					}
					public void done(){
						graphRenderer.reiterate();
					}			
				};	
				worker.execute();
			}
		});
		
		// display in the panel using GroupLayout
		GroupLayout.SequentialGroup hGroup = layout.createSequentialGroup();

		// The sequential group in turn contains two parallel groups.
		// One parallel group contains the labels, the other the text fields.
		// Putting the labels in a parallel group along the horizontal axis
		// positions them at the same x location.
		//
		// Variable indentation is used to reinforce the level of grouping.
		hGroup.add(layout.createParallelGroup().add(layoutLabel).add(modeLabel)
				.add(bgcolorLabel).add(showLabelChkBox).add(showWeightChkBox)
				.add(zoomLabel));
		
		hGroup.add(layout.createParallelGroup().add(layoutCombo).add(modeCombo)
				.add(bgcolorButton).add(zoomInButton).add(zoomOutButton));
		
		hGroup.add(layout.createParallelGroup().add(layoutSize));
		
		hGroup.add(layout.createParallelGroup().add(increaseLayoutSize).add(decreaseLayoutSize));
		
		hGroup.add(layout.createParallelGroup().add(reiterate));
		
		layout.setHorizontalGroup(hGroup);

		// Create a sequential group for the vertical axis.
		GroupLayout.SequentialGroup vGroup = layout.createSequentialGroup();

		// The sequential group contains two parallel groups that align
		// the contents along the baseline. The first parallel group contains
		// the first label and text field, and the second parallel group
		// contains
		// the second label and text field. By using a sequential group
		// the labels and text fields are positioned vertically after one
		// another.
		vGroup.add(layout.createParallelGroup(GroupLayout.BASELINE).add(
				layoutLabel).add(layoutCombo));
		vGroup.add(layout.createParallelGroup(GroupLayout.BASELINE).add(
				modeLabel).add(modeCombo));
		vGroup.add(layout.createParallelGroup(GroupLayout.BASELINE).add(
				bgcolorLabel).add(bgcolorButton));
		vGroup.add(layout.createParallelGroup(GroupLayout.BASELINE).add(
				zoomLabel).add(zoomInButton).add(layoutSize).add(increaseLayoutSize).add(reiterate));
		vGroup.add(layout.createParallelGroup(GroupLayout.BASELINE).add(
				zoomOutButton).add(decreaseLayoutSize));
		vGroup.add(layout.createParallelGroup(GroupLayout.BASELINE).add(
				showLabelChkBox));
		vGroup.add(layout.createParallelGroup(GroupLayout.BASELINE).add(
				showWeightChkBox));

		layout.setVerticalGroup(vGroup);
	}

}
