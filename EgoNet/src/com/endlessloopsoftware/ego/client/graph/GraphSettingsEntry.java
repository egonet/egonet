package com.endlessloopsoftware.ego.client.graph;

import org.w3c.dom.*;
import org.w3c.dom.Element;

public class GraphSettingsEntry {

    public enum GraphSettingType {
        Node, Edge
    }
    
	GraphQuestion graphQuestion = null;

	GraphProperty property = null;

	GraphSettingType type;

	public GraphSettingsEntry(GraphQuestion gq, NodeProperty np,
			GraphSettingType type) {
		this.graphQuestion = gq;
		this.property = np;
		this.type = type;
	}

	public GraphSettingsEntry(GraphQuestion gq, EdgeProperty ep,
			GraphSettingType type) {
		this.graphQuestion = gq;
		this.property = ep;
		this.type = type;
	}

	public GraphQuestion getGraphQuestion() {
		return graphQuestion;
	}

	public GraphProperty getProperty() {
		return property;
	}

	public String toString() {
		String string = "";
		string = graphQuestion.toString() + "||" + property.toString();
		return string;
	}

	public GraphSettingType getType() {
		return type;
	}

	public void writeEntryElement(Document doc, Element rootElement) {
		Element entryElement = doc.createElement("Entry");

		// Record Graph Question (Question , Answer)
		Element graphQuestionElement = doc.createElement("GraphQuestion");
		// Question
		Element questionElement = doc.createElement("Question");
		questionElement.setAttribute("id", graphQuestion.getQuestion().UniqueId
				.toString());
		// Selection
		Element selectionElement = doc.createElement("Selection");
		selectionElement.setAttribute("text", graphQuestion.getSelection()
				.getString());
		// Type
		Element categoryElement = doc.createElement("Category");
		String category = ((Integer) graphQuestion.getCategory()).toString();
		categoryElement.setAttribute("category", category);
		// Append all three to graphQuestion Element
		graphQuestionElement.appendChild(questionElement);
		graphQuestionElement.appendChild(selectionElement);
		graphQuestionElement.appendChild(categoryElement);

		// Record Property(Color Shape Size Label)
		Element propertyElement = doc.createElement("Property");
		// Color
		Element colorElement = doc.createElement("Color");
		String rgb = ((Integer) property.getColor().getRGB()).toString();
		colorElement.setAttribute("color", rgb);
		// Shape
		Element shapeElement = doc.createElement("Shape");
		if (property.getClass() == NodeProperty.class) {
			String shape = ((NodeProperty) property).getShape().toString();
			shapeElement.setAttribute("shape", shape);
		} else {
			String shape = ((EdgeProperty) property).getShape().toString();
			shapeElement.setAttribute("shape", shape);
		}
		// Size
		Element sizeElement = doc.createElement("Size");
		String size = ((Integer) property.getSize()).toString();
		sizeElement.setAttribute("size", size);
		// Append all three to property Element
		propertyElement.appendChild(colorElement);
		propertyElement.appendChild(shapeElement);
		propertyElement.appendChild(sizeElement);

		// Record property type (Node/Edge)
		Element propertyTypeElement = doc.createElement("PropertyType");
		propertyTypeElement.setAttribute("Type", type.toString());

		// Append all three for entry element
		entryElement.appendChild(graphQuestionElement);
		entryElement.appendChild(propertyElement);
		entryElement.appendChild(propertyTypeElement);
		rootElement.appendChild(entryElement);
	}

}
