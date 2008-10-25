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
package com.endlessloopsoftware.egonet;
import com.endlessloopsoftware.ego.client.EgoClient;
import electric.xml.Element;
import electric.xml.Elements;
import java.util.Date;
import java.text.*;

public class Answer implements Cloneable {
	/**
	 * Unique ID for every question
	 */
	public Long questionId;

	private int[] alters;

	public boolean answered;

	public boolean adjacent;

	private int value;

	private int index;
	
	public String string;

	public String timestamp;

	public static final int NO_ANSWER = -1;

	public static final int ALL_ADJACENT = -2;

	public Answer(Long Id) {
		this(Id, null);
	}

	public Answer(Long Id, int[] alters) {
		questionId = Id;
		answered = false;
		adjacent = false;
		setValue(-1);
		string = "";
		timestamp = DateFormat.getDateInstance().format(new Date());

		if (alters == null) {
			this.alters = new int[0];
		} else {
			this.alters = alters;
		}
	}

	public Object clone() throws CloneNotSupportedException {
		return (super.clone());
	}

	/***************************************************************************
	 * Add answer information to an xml structure for output to a file
	 * 
	 * @param e
	 *            XML Element, parent of answer tree
	 */
	public void writeAnswer(Element e) throws Exception {
		Element answerElement = new Element("Answer");
		Element altersElement;

			answerElement.addElement("QuestionId").setLong(
					questionId.longValue());
			answerElement.addElement("Answered").setBoolean(answered);

			if (answered) {
				if(questionId.equals(1205185478364L)) System.err.println("Printed a value into the XML file that was zero: " + getString());
				answerElement.addElement("Value").setInt(getValue());
				answerElement.addElement("Index").setInt(getIndex());
				answerElement.addElement("Adjacent").setBoolean(adjacent);
				answerElement.addElement("String").setText(string);
				answerElement.addElement("TimeStamp").setText(timestamp);
			}

			if (alters.length > 0) {
				altersElement = answerElement.addElement("Alters");
				for (int i = 0; i < alters.length; i++) {
					altersElement.addElement("Index").setInt(alters[i]);
				}
			}

			e.addElement(answerElement);
	}

	/***************************************************************************
	 * Read alter list from an xml tree
	 * 
	 * @param interview
	 *            Interview to read answers into
	 * @param e
	 *            XML Element, parent of alter list
	 */
	public static Answer readAnswer(EgoClient egoClient, Element e) {
		Answer r = null;
		Elements alterElems = null;
		int qAlters[] = null;
		Long qId = new Long(e.getLong("QuestionId"));
		Question q = (Question) egoClient.getStudy().getQuestions().getQuestion(qId);
		Element alterElem = e.getElement("Alters");

		if (alterElem != null) {
			alterElems = alterElem.getElements("Index");
			qAlters = new int[alterElems.size()];

			for (int i = 0; i < alterElems.size(); i++) {
				qAlters[i] = alterElems.next().getInt();
			}
		}

		r = new Answer(qId, qAlters);

		r.answered = e.getBoolean("Answered");

		if (r.answered) {
			r.string = e.getString("String");
			r.setValue(e.getInt("Value"));
			r.setIndex(e.getInt("Index"));
			r.adjacent = q.selectionAdjacent(r.getValue());
		} else {
			r.string = null;
		}

		return r;
	}

	public int[] getAlters() {
		return alters;
	}

	public void setAlters(int[] alters) {
		this.alters = alters;
	}

	/*
	 * public AnswerDataValue getDataValue() { AnswerDataValue adv = new
	 * AnswerDataValue();
	 * 
	 * adv.setAnswered(this.answered); adv.setAnswerString(this.string);
	 * adv.setAnswerValue(this.value); adv.setAnswerAdjacent(this.adjacent);
	 * 
	 * return adv; }
	 */
	public String toString() {
		String str = null;
		if (string == null) {
			Integer val = getValue();
			str = val.toString();
			;
		} else {
			str = string;
		}
		return str;
	}
	
	public String getString() {
		String str = "";
		str = "questionId=" + questionId + ", answered=" + answered + ", string=" + string + ", index="+getIndex()+", value=" + getValue();
		return str;
		
	}

	public void setValue(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public int getIndex() {
		return index;
	}
}