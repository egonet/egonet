package com.endlessloopsoftware.ego.client.graph;

public class GraphSettingsEntry {

	GraphQuestion graphQuestion = null;
	GraphProperty property = null;
	GraphSettingType type;
	
	public GraphSettingsEntry (GraphQuestion gq, NodeProperty np, GraphSettingType type) {
		this.graphQuestion = gq;
		this.property = np;
		this.type = type;
	}
	
	public GraphSettingsEntry (GraphQuestion gq, EdgeProperty ep, GraphSettingType type) {
		this.graphQuestion = gq;
		this.property = ep;
		this.type = type;
	}

	public GraphQuestion getGraphQuestion() {
		return graphQuestion;
	}

	public GraphProperty getNodeProperty() {
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
}
