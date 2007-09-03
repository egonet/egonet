package com.endlessloopsoftware.ego;

/****
 * 
 * Copyright (c) 2007, Endless Loop Software, Inc.
 * 
 *  This file is part of EgoNet.
 *
 *    EgoNet is free software; you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as published by
 *    the Free Software Foundation; either version 3 of the License, or
 *    (at your option) any later version.
 *
 *    EgoNet is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU General Public License for more details.
 *
 *    You should have received a copy of the GNU General Public License
 *    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

import com.endlessloopsoftware.ego.client.EgoClient;
import com.endlessloopsoftware.egonet.util.AnswerDataValue;

import electric.xml.Element;
import electric.xml.Elements;

public class Answer implements Cloneable
{
	public Long             questionId;
   public int[]            alters;
   public boolean          answered;
   public boolean          adjacent;
   public int              value;
   public String           string;

   public static final int NO_ANSWER    = -1;
   public static final int ALL_ADJACENT = -2;

	private Answer()
	{
	}

	public Answer(Long Id)
	{
		this(Id, null);
	}

	public Answer(Long Id, int[] alters)
	{
		questionId = Id;
		answered = false;
		adjacent = false;
		value = -1;
		string = "";

		if (alters == null)
		{
			this.alters = new int[0];
		}
		else
		{
			this.alters = alters;
		}
	}
	
	public Answer(AnswerDataValue data)
	{
		questionId   = data.getQuestionId();
		answered     = data.getAnswered();
		adjacent     = data.getAnswerAdjacent();
		value        = data.getAnswerValue();
		string       = data.getAnswerString();
		alters       = data.getAlters().toArray();
	}

	public Object clone() throws CloneNotSupportedException
	{
		return (super.clone());
	}

	/****************************************************************************
	 * Add answer information to an xml structure for output to a file
	 * 
	 * @param e
	 *           XML Element, parent of answer tree
	 */
	public void writeAnswer(Element e) throws Exception
	{
		Element answerElement = new Element("Answer");
		Element altersElement;

		try
		{
			answerElement.addElement("QuestionId").setLong(questionId.longValue());
			answerElement.addElement("Answered").setBoolean(answered);

			if (answered)
			{
				answerElement.addElement("Value").setInt(value);
				answerElement.addElement("Adjacent").setBoolean(adjacent);
				answerElement.addElement("String").setText(string);
			}

			if (alters.length > 0)
			{
				altersElement = answerElement.addElement("Alters");
				for (int i = 0; i < alters.length; i++)
				{
					altersElement.addElement("Index").setInt(alters[i]);
				}
			}

			e.addElement(answerElement);
		}
		catch (Exception ex)
		{
			System.err.println("Failure in Answer::writeAnswer; " + ex);
			ex.printStackTrace();
			throw ex;
		}
	}

	/****************************************************************************
	 * Read alter list from an xml tree
	 * 
	 * @param interview
	 *           Interview to read answers into
	 * @param e
	 *           XML Element, parent of alter list
	 */
	public static Answer readAnswer(Element e)
	{
		Answer 	r = null;
		Elements alterElems = null;
		int 		qAlters[] = null;
		Long 		qId = new Long(e.getLong("QuestionId"));
		Question q = (Question) EgoClient.study.getQuestions().getQuestion(qId);
		Element 	alterElem = e.getElement("Alters");

		if (alterElem != null)
		{
			alterElems = alterElem.getElements("Index");
			qAlters = new int[alterElems.size()];

			for (int i = 0; i < alterElems.size(); i++)
			{
				qAlters[i] = alterElems.next().getInt();
			}
		}

		r = new Answer(qId, qAlters);

		r.answered = e.getBoolean("Answered");

		if (r.answered)
		{
			r.string = e.getString("String");
			r.value = e.getInt("Value");
			r.adjacent = q.selectionAdjacent(r.value);
		}
		else
		{
			r.string = null;
		}

		return r;
	}

/*	public AnswerDataValue getDataValue()
	{
		AnswerDataValue adv = new AnswerDataValue();

		adv.setAnswered(this.answered);
		adv.setAnswerString(this.string);
		adv.setAnswerValue(this.value);
		adv.setAnswerAdjacent(this.adjacent);

		return adv;
	}
*/}