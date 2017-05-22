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

import java.util.*;

import org.egonet.model.question.Question;
import org.egonet.model.question.Selection;

public class GraphQuestionSelectionPair {

	private Question question;

	private Selection selection;

	//used only for ALTER QUESTIONS
	private List<Integer> alterList = new ArrayList<Integer>();
	
	private boolean showLabel = false; 

	public GraphQuestionSelectionPair(Question question, Selection selection) {
		this.question = question;
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

	public Class<? extends Question> getCategory() {
		return question.getClass();
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
				&& this.getClass().equals(gq.getClass()));
	}
	
	public int hashCode()
	{
		return this.question.hashCode() + this.selection.hashCode() + getClass().hashCode();
	}

	public List<Integer> getAlterList() {
		return alterList;
	}

	public void setAlterList(List<Integer> alterList) {
		this.alterList = alterList;
	}
	
}
