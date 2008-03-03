package com.endlessloopsoftware.ego;

/**
 * <p>Title: Egocentric Networks Client Program</p>
 * <p>Description: Subject Interview Client</p>
 * <p>Copyright: Copyright (c) 2002 - 2004 </p>
 * <p>Company: Endless Loop Software</p>
 * @author Peter Schoaff
 *
 * $Id: Answer.java,v 1.1 2005/08/02 19:36:02 samag Exp $
 */
import com.endlessloopsoftware.ego.client.EgoClient;
import com.endlessloopsoftware.egonet.util.AnswerDataValue;

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

	public int value;

	public int index;
	
	public String string;

	public String timestamp;

	public static final int NO_ANSWER = -1;

	public static final int ALL_ADJACENT = -2;

	private Answer() {
	}

	public Answer(Long Id) {
		this(Id, null);
	}

	public Answer(Long Id, int[] alters) {
		questionId = Id;
		answered = false;
		adjacent = false;
		value = -1;
		string = "";
		timestamp = DateFormat.getDateInstance().format(new Date());

		if (alters == null) {
			this.alters = new int[0];
		} else {
			this.alters = alters;
		}
	}

	public Answer(AnswerDataValue data) {
		questionId = data.getQuestionId();
		answered = data.getAnswered();
		adjacent = data.getAnswerAdjacent();
		value = data.getAnswerValue();
		string = data.getAnswerString();
		alters = data.getAlters().toArray();
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

		try {
			answerElement.addElement("QuestionId").setLong(
					questionId.longValue());
			answerElement.addElement("Answered").setBoolean(answered);

			if (answered) {
				answerElement.addElement("Value").setInt(value);
				answerElement.addElement("Index").setInt(index);
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
		} catch (Exception ex) {
			System.err.println("Failure in Answer::writeAnswer; " + ex);
			ex.printStackTrace();
			throw ex;
		}
	}

	/***************************************************************************
	 * Read alter list from an xml tree
	 * 
	 * @param interview
	 *            Interview to read answers into
	 * @param e
	 *            XML Element, parent of alter list
	 */
	public static Answer readAnswer(Element e) {
		Answer r = null;
		Elements alterElems = null;
		int qAlters[] = null;
		Long qId = new Long(e.getLong("QuestionId"));
		Question q = (Question) EgoClient.study.getQuestions().getQuestion(qId);
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
			r.value = e.getInt("Value");
			r.index = e.getInt("Index");
			r.adjacent = q.selectionAdjacent(r.value);
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
			Integer val = value;
			str = val.toString();
			;
		} else {
			str = string;
		}
		return str;
	}
	
	public String getString() {
		String str = "";
		str = "Answered: " + answered + " string: " + string + " value : " + value;
		return str;
		
	}
}