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
import java.awt.event.*;
import javax.swing.*;

import org.egonet.util.listbuilder.Selection;

import com.endlessloopsoftware.egonet.Question;
import com.endlessloopsoftware.egonet.Question.QuestionType;

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
				NodeProperty.NodePropertyType param = NodeProperty.NodePropertyType.Size;
				System.out.println("Size by : " + measure.toString());
				addStructuralElement(measure, param);
				graphRenderer.updateGraphSettings();
			}
		});
		applyColorButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				StructuralMeasures measure = (StructuralMeasures) structuralCombo_2
						.getSelectedItem();
				NodeProperty.NodePropertyType param = NodeProperty.NodePropertyType.Color;
				addStructuralElement(measure, param);
				graphRenderer.updateGraphSettings();
				System.out.println("Color by : " + measure.toString());
				
			}
		});

		drawPanel();
	}

	private void addStructuralElement(StructuralMeasures measure,
			NodeProperty.NodePropertyType property) {

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
		GraphQuestionSelectionPair gq = new GraphQuestionSelectionPair(question, selection, QuestionType.STUDY_CONFIG);
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
