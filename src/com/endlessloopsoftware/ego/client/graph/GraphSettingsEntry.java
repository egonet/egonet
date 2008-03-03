package com.endlessloopsoftware.ego.client.graph;

public class GraphSettingsEntry {

	GraphQuestion graphQuestion = null;
	NodeProperty nodeProperty = null;
	
	public GraphSettingsEntry (GraphQuestion gq, NodeProperty np) {
		this.graphQuestion = gq;
		this.nodeProperty = np;
	}

	public GraphQuestion getGraphQuestion() {
		return graphQuestion;
	}

	public NodeProperty getNodeProperty() {
		return nodeProperty;
	}
	
	public String toString() {
		String string = "";
		string = graphQuestion.toString() + "||" + nodeProperty.toString(); 
		return string;
	}
}
