package com.endlessloopsoftware.ego.client.graph;

import org.jdesktop.layout.GroupLayout;
import java.awt.event.*;
import javax.swing.*;

import org.egonet.util.listbuilder.Selection;

import com.endlessloopsoftware.ego.Question;

import java.awt.*;

public class StructuralMeasuresPanel extends JPanel {

	private JButton applySizeButton;

	private JButton applyColorButton;

	private JComboBox structuralCombo_1;

	private JComboBox structuralCombo_2;

	private JLabel sizeLabel;

	private JLabel colorLabel;

	private GroupLayout layout;

	private GraphRenderer graphRenderer;

	public static enum StructuralMeasures {
		DegreeCentrality, BetweennessCentrality
	};

	public StructuralMeasuresPanel(GraphRenderer renderer) {
		this.graphRenderer = renderer;
		layout = new GroupLayout(this);
		this.setLayout(layout);
		layout.setAutocreateGaps(true);
		layout.setAutocreateContainerGaps(true);
		createComponents();
	}

	private void createComponents() {
		sizeLabel = new JLabel("Size nodes based on : ");
		colorLabel = new JLabel("Color nodes based on : ");
		structuralCombo_1 = new JComboBox(StructuralMeasures.values());
		structuralCombo_1.setMaximumSize(new Dimension(50, 100));
		structuralCombo_2 = new JComboBox(StructuralMeasures.values());
		structuralCombo_2.setMaximumSize(new Dimension(50, 100));
		applySizeButton = new JButton("Apply Size");
		applyColorButton = new JButton("Apply Color");

		applySizeButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				StructuralMeasures measure = (StructuralMeasures) structuralCombo_1
						.getSelectedItem();
				NodeProperty.Property param = NodeProperty.Property.Size;
				System.out.println("Size by : " + measure.toString());
				addStructuralElement(measure, param);
				graphRenderer.updateGraphSettings();
			}
		});
		applyColorButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				StructuralMeasures measure = (StructuralMeasures) structuralCombo_2
						.getSelectedItem();
				NodeProperty.Property param = NodeProperty.Property.Color;
				addStructuralElement(measure, param);
				graphRenderer.updateGraphSettings();
				System.out.println("Color by : " + measure.toString());
				
			}
		});

		drawPanel();
	}

	private void addStructuralElement(StructuralMeasures measure,
			NodeProperty.Property property) {

		Question question = new Question("Structural_Properties");
		Selection selection = null;
		switch (measure) {
		case BetweennessCentrality:
			selection = new Selection("BetweennessCentrality", 0, 0, false);
			break;
		case DegreeCentrality:
			selection = new Selection("DegreeCentrality", 0, 0, false);
			break;
		}
		// the 3rd argument to constructor is 0 to say that it is a structural
		// question
		GraphQuestion gq = new GraphQuestion(question, selection, 0);
		NodeProperty nodeProperty = new NodeProperty();
		nodeProperty.setColor(Color.BLACK);
		nodeProperty.setSize(15);
		nodeProperty.setShape(NodeProperty.NodeShape.Circle);
		nodeProperty.setProperty(property);
		graphRenderer.addQAsettings(gq, nodeProperty);
	}

	private void drawPanel() {
		this.removeAll();

		GroupLayout.SequentialGroup hGroup = layout.createSequentialGroup();
		hGroup.add(layout.createParallelGroup().add(sizeLabel)
				.add(30).add(applySizeButton).add(30)
				.add(colorLabel).add(30).add(
						applyColorButton));
		hGroup.add(layout.createParallelGroup().add(
				structuralCombo_1).add(structuralCombo_2));
		layout.setHorizontalGroup(hGroup);
		GroupLayout.SequentialGroup vGroup = layout.createSequentialGroup();
		vGroup.add(layout.createParallelGroup(
				GroupLayout.BASELINE).add(sizeLabel)
				.add(structuralCombo_1));
		vGroup.add(layout.createParallelGroup(
				GroupLayout.BASELINE).add(30));
		vGroup.add(layout.createParallelGroup(
				GroupLayout.BASELINE).add(applySizeButton));
		vGroup.add(layout.createParallelGroup(
				GroupLayout.BASELINE).add(30));
		vGroup.add(layout.createParallelGroup(
				GroupLayout.BASELINE).add(colorLabel)
				.add(structuralCombo_2));
		vGroup.add(layout.createParallelGroup(
				GroupLayout.BASELINE).add(30));
		vGroup.add(layout.createParallelGroup(
				GroupLayout.BASELINE).add(applyColorButton));
		layout.setVerticalGroup(vGroup);
	}
}