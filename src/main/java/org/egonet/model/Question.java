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
package org.egonet.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.egonet.model.Shared.AnswerType;
import org.egonet.model.Shared.QuestionType;


/*******************************************************************************
 * Routines for creating and handling atomic question elements
 *
 *
 *
 *
 *
 */
public class Question implements Cloneable {

	public String getNiceName() {
		return questionType.niceName;
	}
	public String getTitle() {
		return questionType.title;
	}

	public boolean centralMarker = false;

	private boolean statable = false;

	public boolean followupOnly = false;

	public boolean isFollowupOnly() {
		return followupOnly;
	}

	public void setFollowupOnly(boolean followupOnly) {
		this.followupOnly = followupOnly;
	}

	public Long UniqueId = new Long(new Date().getTime());

	public QuestionType questionType = QuestionType.EGO;

	public String title = "";

	public String text = "";

	public String citation = "";

	public AnswerType answerType = AnswerType.TEXT;

	public QuestionLink link = new QuestionLink();

	private List<Selection> selections = new ArrayList<Selection>(0);

	private Answer answer = new Answer();

	public static final int MAX_CATEGORICAL_CHOICES = 9;

	/***************************************************************************
	 * Creates question
	 *
	 * @return question new question
	 */
	public Question() {
	}

	/***************************************************************************
	 * Creates question with string as title
	 *
	 * @param s
	 *            question title
	 * @return question new question
	 */
	public Question(String s) {
		this.title = s;
	}

	public Question(QuestionType t) {
		this.questionType = t;
	}


	/***************************************************************************
	 * Returns whether a given selection is adjacent based on the values stored
	 * in the question. Is used to override value found in an interview file
	 *
	 * @param value
	 * @return true iff that selection is marked as adjacent
	 */
	public boolean selectionAdjacent(int value) {
		boolean rval = false;

		if(getSelections() == null || getSelections().size() <= 0)
			return false;

		for (Selection sel : getSelections()) {
			if (value == sel.getValue()) {
				rval = sel.isAdjacent();
				break;
			}
		}

		return rval;
	}

	/**
	 * Does the answer to this question determine whether alters are adjacent?
	 */
	public boolean determinesAdjacency() {
		for(Selection selection : getSelections()) {
			if(selection.isAdjacent()) {
				return true;
			}
		}
		return false;
	}

	/***************************************************************************
	 * Overrides toString method for question, returns title
	 *
	 * @return String title of question
	 */
	public String toString() {
		if (title == null) {
			return new String("Untitled");
		} else {
			return title;
		}
	}

	/***************************************************************************
	 * Implements Clone interface for conducting an interview and cloning the study question. The clone is attached to the interview.
	 *
	 * @return Clone of Question
	 */
	public Object clone() {
		Question q;

		try {
			q = (Question) super.clone();
			q.link = (QuestionLink) this.link.clone();
			q.followupOnly = this.followupOnly;

			/*******************************************************************
			 * Dangerous to clone answers as multiple answers refer to same
			 * question Make sure they are assigned explicitly
			 */
			q.setAnswer(null);
		} catch (CloneNotSupportedException ex) {
			q = null;
		}

		return q;
	}

	public String getString() {
		String str = "";
		str = "ID : " + UniqueId + ", Qtype="+questionType+",Atype="+answerType+", Title : " + title + " text : " + text
				+ "\nAnswer : " + getAnswer().getString();
		return str;
	}

	public void setSelections(List<Selection> selections) {
		this.selections = selections;
	}

	public List<Selection> getSelections() {
		return selections;
	}

	public Answer getAnswer() {
		return answer;
	}

	public void setAnswer(Answer answer) {
		this.answer = answer;
	}

	public boolean isStatable() {
		return statable;
	}

	public void setStatable(boolean statable) {
		this.statable = statable;
	}
}
