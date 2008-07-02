package com.endlessloopsoftware.ego.client.graph;

import org.w3c.dom.*;

import com.endlessloopsoftware.ego.client.graph.EdgeProperty.EdgePropertyType;
import com.endlessloopsoftware.ego.client.graph.NodeProperty.NodePropertyType;

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
		String category = ((Integer) graphQuestion.getCategory()).toString();
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
