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
package org.egonet.graph;

import org.egonet.graph.EdgeProperty.EdgePropertyType;
import org.egonet.graph.NodeProperty.NodePropertyType;
import org.w3c.dom.*;

public class GraphSettingsEntry {

    public enum GraphSettingType {
        Node, Edge
    }

	GraphQuestionSelectionPair graphQuestion = null;

	GraphProperty property = null;

	GraphSettingType type;

	public GraphSettingsEntry(GraphQuestionSelectionPair gq, NodeProperty np,
			GraphSettingType type) {
		this.graphQuestion = gq;
		this.property = np;
		this.type = type;
	}

	public GraphSettingsEntry(GraphQuestionSelectionPair gq, EdgeProperty ep,
			GraphSettingType type) {
		this.graphQuestion = gq;
		this.property = ep;
		this.type = type;
	}

	public GraphQuestionSelectionPair getGraphQuestion() {
		return graphQuestion;
	}

	public GraphProperty getProperty() {
		return property;
	}

	public String toString() {
		return "[questionSelectionPair=("+graphQuestion.toString() + "),(property=" + property.toString() + ")]";
	}

	public GraphSettingType getType() {
		return type;
	}

	public void writeEntryElement(Document doc, Element rootElement) {
		Element entryElement = doc.createElement("Entry");

		// Record Graph Question (Question , Answer)
		Element graphQuestionElement = doc.createElement("GraphQuestionSelectionPair");
		// Question
		Element questionElement = doc.createElement("Question");
		questionElement.setAttribute("id", graphQuestion.getQuestion().UniqueId
				.toString());
		// Selection
		Element selectionElement = doc.createElement("Selection");
		selectionElement.setAttribute("text", graphQuestion.getSelection().getString());
		// Type
		Element categoryElement = doc.createElement("Category");
		String category = graphQuestion.getCategory().niceName+"";
		categoryElement.setAttribute("category", category);
		// Append all three to graphQuestion Element
		graphQuestionElement.appendChild(questionElement);
		graphQuestionElement.appendChild(selectionElement);
		graphQuestionElement.appendChild(categoryElement);

		// Record Property(Color Shape Size Label)
		Element propertyElement = doc.createElement("Property");
		propertyElement.setAttribute("type", type.toString());

		if (property instanceof NodeProperty) {
			NodeProperty np = ((NodeProperty) property);
			if(np.getProperty().equals(NodePropertyType.Color))
			{
				String rgb = ((Integer) property.getColor().getRGB()).toString();
				propertyElement.setAttribute("color", rgb);
			} else if(np.getProperty().equals(NodePropertyType.Label)) {
				// doesn't save label yet!
			} else if(np.getProperty().equals(NodePropertyType.Shape)) {
				String shape = np.getShape().toString();
				propertyElement.setAttribute("shape", shape);
			} else if(np.getProperty().equals(NodePropertyType.Size)) {
				String size = ((Integer) property.getSize()).toString();
				propertyElement.setAttribute("size", size);
			}

			// no visible property on nodes
		} else {

			EdgeProperty ep = ((EdgeProperty) property);
			if(ep.getProperty().equals(EdgePropertyType.Color))
			{
				String rgb = ((Integer) property.getColor().getRGB()).toString();
				propertyElement.setAttribute("color", rgb);
			} else if(ep.getProperty().equals(EdgePropertyType.Label)) {
				// doesn't save label yet!
			} else if(ep.getProperty().equals(EdgePropertyType.Shape)) {
				String shape = ep.getShape().toString();
				propertyElement.setAttribute("shape", shape);
			} else if(ep.getProperty().equals(EdgePropertyType.Size)) {
				String size = ((Integer) property.getSize()).toString();
				propertyElement.setAttribute("size", size);
			}

			Element visibleElement = doc.createElement("Visible");
			if (((EdgeProperty) property).isVisible())
				visibleElement.setAttribute("visible", "true");
			else
				visibleElement.setAttribute("visible", "false");
			propertyElement.appendChild(visibleElement);
		}


		entryElement.appendChild(graphQuestionElement);
		entryElement.appendChild(propertyElement);

		rootElement.appendChild(entryElement);
	}

}
