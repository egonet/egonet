package com.endlessloopsoftware.ego.client.graph;

import java.util.*;

import com.endlessloopsoftware.ego.*;
import org.egonet.util.listbuilder.Selection;

public class GraphQuestionSelectionPair {

	// public static enum QuestionCategory { EGO, ALTER, ALTERPAIR }
	private Question question;

	private Selection selection;

	//used only for ALTER QUESTIONS
	private List<Integer> alterList = new ArrayList<Integer>();
	
	/**
	 * EGO_QUESTION = 1; ALTER_PROMPT = 2; ALTER_QUESTION = 3;
	 * ALTER_PAIR_QUESTION = 4;
	 */
	private int category;
	
	private boolean showLabel = false; 

	public GraphQuestionSelectionPair(Question question, Selection selection, int category) {
		this.question = question;
		this.category = category;
		this.selection = selection;
	}
	
	public boolean isShowLabel() {
		return showLabel;
	}

	public void setShowLabel(boolean showLabel) {
		this.showLabel = showLabel;
	}

	

	public Selection getSelection() {
		return selection;
	}

	public void setSelection(Selection selection) {
		this.selection = selection;
	}

	public int getCategory() {
		return category;
	}

	public void setCategory(int category) {
		this.category = category;
	}

	public Question getQuestion() {
		return question;
	}

	public void setQuestion(Question question) {
		this.question = question;
	}

	public String toString() {
		return "[title="+this.question.title + ",selection=" + this.selection.getString()+"]";
	}

	public boolean equals(Object obj) {
		if(!(obj instanceof GraphQuestionSelectionPair))
			return false;
		
		GraphQuestionSelectionPair gq = (GraphQuestionSelectionPair)obj;
		return (this.question.equals(gq.question)
				&& this.selection.equals(gq.selection)
				&& this.category == gq.category);
	}
	
	public int hashCode()
	{
		return this.question.hashCode() + this.selection.hashCode() + category;
	}

	public List<Integer> getAlterList() {
		return alterList;
	}

	public void setAlterList(List<Integer> alterList) {
		this.alterList = alterList;
	}
	
}
