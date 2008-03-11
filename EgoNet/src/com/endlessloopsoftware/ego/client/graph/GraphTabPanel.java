package com.endlessloopsoftware.ego.client.graph;

import org.jdesktop.layout.GroupLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.*;

import java.util.*;

import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.visualization.FRLayout;
import edu.uci.ics.jung.visualization.ISOMLayout;
import edu.uci.ics.jung.visualization.contrib.KKLayout;
import edu.uci.ics.jung.visualization.SpringLayout;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.contrib.CircleLayout;
import edu.uci.ics.jung.visualization.control.DefaultModalGraphMouse;

public class GraphTabPanel extends JPanel {
	private List layoutOptions;
	private DefaultModalGraphMouse graphMouse;

	private VisualizationViewer vv;
	
	private GraphRenderer graphRenderer;

	private GroupLayout layout;

	private Graph g;
	public GraphTabPanel(Graph g, VisualizationViewer vv,
			DefaultModalGraphMouse graphMouse) {
		
		this.g= g;
		this.graphMouse = graphMouse;
		this.vv = vv;
		createComponents();
	}
	public GraphTabPanel(GraphRenderer gr)
	{
		this.graphRenderer = gr;
		this.g = gr.getGraph();
		this.graphMouse = gr.getGraphMouse();
		this.vv= gr.getVv();
		createComponents();
	}

	private void createComponents() {

		layout = new GroupLayout(this);
		this.setLayout(layout);
		
		layout.setAutocreateGaps(true);
		layout.setAutocreateContainerGaps(true);
		
		// label combo pair for choosing different layouts
		JLabel layoutLabel = new JLabel("Choose Layout");
		layoutLabel.setOpaque(true);
		
		//add all possible layouts to the layout combo
		layoutOptions = new ArrayList();
		layoutOptions.add(KKLayout.class);
		layoutOptions.add(FRLayout.class);
		layoutOptions.add(CircleLayout.class);
		//layoutOptions.add(SpringLayout.class);
		layoutOptions.add(ISOMLayout.class);
        Class[] layoutList =  (Class[]) layoutOptions.toArray(new Class[0]);
        
		JComboBox layoutCombo = new JComboBox(layoutList);
		
		//use a renderer to shorten the layout name presentation
        layoutCombo.setRenderer(new DefaultListCellRenderer() {
            public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                String valueString = value.toString();
                valueString = valueString.substring(valueString.lastIndexOf('.')+1);
                return super.getListCellRendererComponent(list, valueString, index, isSelected, cellHasFocus);
            }
        });
        
		layoutCombo.setPreferredSize(new Dimension(20,20));
		layoutCombo.setMaximumSize(new Dimension(20,30));
		layoutCombo.setSelectedItem(FRLayout.class);
		layoutCombo.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(ActionEvent e) {
			//call renderer to render the graph with selected layout
				JComboBox cb = (JComboBox) e.getSource();
				Class layoutC = (Class) cb.getSelectedItem();
	            Class lay = layoutC;
				graphRenderer.changeLayout(lay);
			}
		});

		// label combo pair to choose mode of operation
		JLabel modeLabel = new JLabel("Choose Mode");
		modeLabel.setOpaque(true);

		JComboBox modeCombo = graphMouse.getModeComboBox();
		modeCombo.setPreferredSize(new Dimension(20,20));
		modeCombo.setMaximumSize(new Dimension(20,30));
		modeCombo.addItemListener(((DefaultModalGraphMouse) vv.getGraphMouse())
				.getModeListener());

		// add label button pair for changing background color
		JLabel bgcolorLabel = new JLabel("BackGround");
		bgcolorLabel.setOpaque(true);

		JButton bgcolorButton = new JButton();
		bgcolorButton.setText(" ");
		bgcolorButton.setBackground(vv.getBackground());
		Color chosenColor;
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
		JCheckBox showLabelChkBox = new JCheckBox("Show Node Labels");
		showLabelChkBox.setSelected(true);
		showLabelChkBox.addItemListener(new ItemListener(){
            public void itemStateChanged(ItemEvent e) {
                if(e.getStateChange() == ItemEvent.SELECTED)   	
                	graphRenderer.drawNodeLabels();
                else
                	graphRenderer.hideNodeLabels();
            	
            }
        });
		
		JCheckBox showWeightChkBox = new JCheckBox("Show Edge Weights");
		showWeightChkBox.setSelected(false);
		showWeightChkBox.addItemListener(new ItemListener(){
            public void itemStateChanged(ItemEvent e) {
                if(e.getStateChange() == ItemEvent.SELECTED)
                {
                	GraphRenderer.showEdgeWeights= true;
                	graphRenderer.drawEdgeLabels();
                	
                }
                else
                	graphRenderer.hideEdgeLabels();
            	
            }
        });
		
		

		// disply in the panel using GroupLayout
		GroupLayout.SequentialGroup hGroup = layout.createSequentialGroup();

		// The sequential group in turn contains two parallel groups.
		// One parallel group contains the labels, the other the text fields.
		// Putting the labels in a parallel group along the horizontal axis
		// positions them at the same x location.
		//
		// Variable indentation is used to reinforce the level of grouping.
		hGroup.add(layout.createParallelGroup().add(layoutLabel)
				.add(modeLabel).add(bgcolorLabel)
				.add(showLabelChkBox).add(showWeightChkBox));

		hGroup.add(layout.createParallelGroup().add(layoutCombo)
				.add(modeCombo).add(bgcolorButton));

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
		vGroup.add(layout.createParallelGroup(
				GroupLayout.BASELINE).add(layoutLabel)
				.add(layoutCombo));
		vGroup.add(layout.createParallelGroup(
				GroupLayout.BASELINE).add(modeLabel)
				.add(modeCombo));
		vGroup.add(layout.createParallelGroup(
				GroupLayout.BASELINE).add(bgcolorLabel)
				.add(bgcolorButton));
		vGroup.add(layout.createParallelGroup(
				GroupLayout.BASELINE).add(showLabelChkBox));
		vGroup.add(layout.createParallelGroup(
				GroupLayout.BASELINE).add(showWeightChkBox));
		layout.setVerticalGroup(vGroup);
	}

}
