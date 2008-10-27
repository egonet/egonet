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
import java.io.IOException;
import java.util.*;

import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import org.egonet.exceptions.DuplicateQuestionException;
import org.egonet.exceptions.MalformedQuestionException;

import electric.xml.Document;
import electric.xml.Element;
import electric.xml.Elements;

/*******************************************************************************
 * Stores basic configuration data for the study including question order lists
 */
public class Study extends Observable
{
	private long              _uniqueId       = -1L;
   private String            _uiType         = Shared.TRADITIONAL_QUESTIONS;
   private int               _numAlters      = -1;
   private boolean           _studyDirty     = false;
   private boolean           _compatible     = true;
   private boolean           _inUse          = false;
   private String            _studyName      = "New Study";
   
   @SuppressWarnings("unchecked")
   private List<Long>[]            _questionOrder  = new List[Question.NUM_QUESTION_TYPES];
   
   private Question          _firstQuestion  = new Question("none");
   private QuestionList      _questions      = new QuestionList();

   /**
   * Instantiates Default Study
   */
	public Study()
	{
		_studyName 	= "New Study";
      _uiType      = Shared.TRADITIONAL_QUESTIONS;
		_numAlters 	= 40;
		_studyDirty 	= false;
		_compatible	= true;
      _uniqueId    = -1;
		
		// @TODO Move storage to parent package
		//		EgoNet.storage.setStudyFile(null);
		this.getQuestions().clear();

		for (int i = 0; i < _questionOrder.length; i++)
		{
			_questionOrder[i] = new ArrayList<Long>();
		}
	}
	
  /**********
    * Instantiates study from an XML Document
    * @param document
    */
	public Study(Document document)
	{
      // Start with default study
		this();

		readPackageStudy(document);
		readQuestions(document);
		verifyStudy();
	}


	/***************************************************************************
	 * Returns UniqueId of study read from file
	 * 
	 * @return long Unique Id of study
	 */
	public long getStudyId()
	{
		return (_uniqueId);
	}

	/***************************************************************************
	 * Notifies observers that a field in the study has changed
	 */
	public void notifyObservers()
	{
		setChanged();
		super.notifyObservers(this);
	}

	/***************************************************************************
	 * Returns name of study
	 * 
	 * @return name name of study
	 */
	public String getStudyName()
	{
		return (_studyName);
	}

	/***************************************************************************
	 * Returns number of alters for which to prompt
	 * 
	 * @return numAlters number of alters for which to prompt
	 */
	public int getNumAlters()
	{
		return (_numAlters);
	}

	/***************************************************************************
	 * Returns array of question order lists
	 * 
	 * @return questionOrder array of lists
	 */
	public List<Long>[] getQuestionOrderArray()
	{
		return (_questionOrder);
	}

	/**
	 * @return Returns the questions.
	 */
	public QuestionList getQuestions()
	{
		return _questions;
	}

   /**
    * @return Returns the questions.
    */
   public Question getQuestion(Long id)
   {
      return getQuestions().getQuestion(id);
   }


	/**
	 * @return Returns the firstQuestion.
	 */
	public Question getFirstQuestion()
	{
		return _firstQuestion;
	}

	/***************************************************************************
	 * Returns array of questions for a specified category
	 * 
	 * @param category
	 *            category of questions to return
	 * @return questionOrder array of question Ids
	 * @throws NoSuchElementException
	 *             for category out of range
	 */
	public List<Long> getQuestionOrder(int category) throws NoSuchElementException
	{
		if (category >= _questionOrder.length)
		{
			throw (new NoSuchElementException());
		}

		return (_questionOrder[category]);
	}

	/***************************************************************************
	 * Working forward from beginning, find first unanswered question
	 * 
	 * @return index of first unanswered question
	 */
	public Question getFirstStatableQuestion()
	{
		Question statable = null;
		Iterator questions;

		/**
		 * Try to find one all alters answer
		 */
		questions = getQuestionOrder(Question.ALTER_PAIR_QUESTION).iterator();
		while (questions.hasNext())
		{
			Question q = (Question) getQuestions().getQuestion((Long) questions.next());

			if (q.statable && !q.link.active)
			{
				statable = q;
				break;
			}
		}

		/**
		 * Settle for any statable
		 */
		if (statable == null)
		{
			questions = getQuestionOrder(Question.ALTER_QUESTION).iterator();
			while (questions.hasNext())
			{
				Question q = (Question) getQuestions().getQuestion((Long) questions.next());

				if (q.statable)
				{
					statable = q;
				}
			}
		}

		return (statable);
	}

	/***************************************************************************
	 * Returns UniqueId of study read from file
	 * 
	 * @param long
	 *            Unique Id of study
	 */
	public void setStudyId(long id)
	{
		_uniqueId = id;
	}

	/***************************************************************************
	 * Sets name of study
	 * 
	 * @param name
	 *            name of study
	 */
	public void setStudyName(String name)
	{
		if (!_studyName.equals(name))
		{
			_studyName = name;
			setModified(true);
		}
	}

	/***************************************************************************
	 * Sets numAlters variable and notifies observers of change to study
	 * 
	 * @param n
	 *            number of alters for which to elicit
	 */
	public void setNumAlters(int n)
	{
		if (_numAlters != n)
		{
			_numAlters = n;
			setModified(true);
		}
	}

	/***************************************************************************
	 * Sets study dirty flag; generally done when the study is written to a
	 * file
	 */
	public void setModified(boolean dirty)
	{
		_studyDirty = dirty;
		notifyObservers();
	}

	/***************************************************************************
	 * gets dirty state of study
	 * 
	 * @return dirty
	 */
	public boolean isModified()
	{
		return (_studyDirty);
	}

	/**
	 * @return Returns the compatible.
	 */
	public boolean isCompatible()
	{
		return _compatible;
	}
	
	/**
	 * @param compatible The compatible to set.
	 */
	public void setCompatible(boolean compatible)
	{
		this._compatible = compatible;
		notifyObservers();
	}
	
	/**
	 * @return Returns the inUse.
	 */
	public boolean isInUse()
	{
		return _inUse;
	}
	
	/**
	 * @param inUse The inUse to set.
	 */
	public void setInUse(boolean inUse)
	{
		this._inUse = inUse;
	}
   
   /**
    * @return Returns the uiType
    */
   public String getUIType()
   {
      return _uiType;
   }
   
   public boolean isAppletUI()
   {
      return (getUIType().equals(Shared.PAIR_ELICITATION) || getUIType().equals(Shared.THREE_STEP_ELICITATION));
   }

	/**
	 * Warn user this change will make study no longer compatible with previous interviews
	 * @param q
	 * @throws DuplicateQuestionException
	 */
	public boolean confirmIncompatibleChange(JFrame frame)
	{
		boolean ok = true;
		if (isInUse() && isCompatible())
		{
         int confirm =
            JOptionPane.showConfirmDialog(
            		frame,
            		"This study has already been used for at least one interview.\n" +
						"If you make this change you will have to save this as a new study and will \n" +
						"no longer be able to access prior interviews with this study.\n" +
						"Do you still wish to make this change?",
						"Incompatible Study Modification",
						JOptionPane.OK_CANCEL_OPTION);

         if (confirm != JOptionPane.OK_OPTION)
         {
            ok = false;
         }
		}
		
		return ok;
	}
	
	/***************************************************************************
	 * Adds a question to the full question list
	 * 
	 * @param q
	 *            question to add
	 */
	public void addQuestion(Question q) throws DuplicateQuestionException
	{
		if (!this.getQuestions().contains(q))
		{
			this.getQuestions().addQuestion(q);
			setModified(true);
		}
		else
		{
			throw new DuplicateQuestionException();
		}

		/* If not in appropriate array list, add to that list too */
		if (!_questionOrder[q.questionType].contains(q.UniqueId))
		{
			_questionOrder[q.questionType].add(q.UniqueId);
		}
	}

	/***************************************************************************
	 * Changes position of a question in an order list
	 * 
	 * @param q
	 *            question to move
	 * @param follow
	 *            question q should follow
	 */
	public void moveQuestionAfter(Question q, Question follow)
	{
		int followloc;

		if (_questionOrder[q.questionType].contains(follow.UniqueId) || (follow == _firstQuestion))
		{
			_questionOrder[q.questionType].remove(q.UniqueId);

			if (follow == _firstQuestion)
			{
				_questionOrder[q.questionType].add(0, q.UniqueId);
			}
			else
			{
				followloc = _questionOrder[q.questionType].indexOf(follow.UniqueId);
				_questionOrder[q.questionType].add(followloc + 1, q.UniqueId);
			}

			if (q.link.active)
			{
				if (!doesQuestionPreceed(q.link.answer.questionId, q.UniqueId))
				{
					q.link.active = false;
					q.link.answer = null;
				}
			}

			setModified(true);
		}
	}

	/***************************************************************************
	 * moves question from one order list to another
	 * 
	 * @param q
	 *            question to move
	 * @param type
	 *            new question type
	 */
	public void changeQuestionType(Question q, int type)
	{
		removeQuestion(q);

		q.questionType = type;

		try
		{
			addQuestion(q);
		}
		catch (DuplicateQuestionException e)
		{
			// This shouldn't happen
			e.printStackTrace();
		}

		setModified(true);
	}

	/***************************************************************************
	 * Go through question list making sure any interquestion dependencies are
	 * met
	 * 
	 * @param q
	 *            question to move
	 * @param type
	 *            new question type
	 */
	public void setCentralQuestion(Question q)
	{
		Long key;
		Question listQ;
		Iterator i = _questionOrder[Question.ALTER_PAIR_QUESTION].iterator();

		while (i.hasNext())
		{
			key = (Long) i.next();
			listQ = this.getQuestions().getQuestion(key);

			if (listQ != null)
			{
				if (listQ.equals(q))
				{
					if (!listQ.centralMarker)
					{
						listQ.centralMarker = true;
						setModified(true);
					}
				}
				else
				{
					/* Only one centralMarker allowed */
					if (listQ.centralMarker)
					{
						listQ.centralMarker = false;
						setModified(true);
					}
				}
			}
		}
	}

	/***************************************************************************
	 * Go through question list making sure any interquestion dependencies are
	 * met
	 * 
	 * @param q
	 *            question to move
	 * @param type
	 *            new question type
	 */
	public void validateQuestions()
	{
		Long key;
		Question q;
		boolean foundCentral = false;
		;
		Iterator i = _questionOrder[Question.ALTER_PAIR_QUESTION].iterator();

		while (i.hasNext())
		{
			key = (Long) i.next();
			q = this.getQuestions().getQuestion(key);

			if (q != null)
			{
				if (!foundCentral && q.centralMarker)
				{
					foundCentral = true;
				}
				else
				{
					/* Only one centralMarker allowed */
					q.centralMarker = false;
				}
			}
		}

		if (!foundCentral)
		{
			/* Tag first Alter pair categorical question */
			i = _questionOrder[Question.ALTER_PAIR_QUESTION].iterator();

			while (i.hasNext() && !foundCentral)
			{
				key = (Long) i.next();
				q = this.getQuestions().getQuestion(key);

				if ((q != null) && (q.answerType == Question.CATEGORICAL))
				{
					q.centralMarker = true;
					foundCentral = true;
				}
			}
		}
	}

	/***************************************************************************
	 * Searches question list for all questions of a given tpe, places them in
	 * list
	 * 
	 * @param questionType
	 *            type filter for question list
	 * @param dlm
	 *            list model to use in inserting questions
	 */
	public void fillList(int questionType, DefaultListModel dlm)
	{
		int startType, endType;
		Iterator i;
		Long key;

		if (questionType == Question.ALL_QUESTION_TYPES)
		{
			startType = Question.MIN_QUESTION_TYPE;
			endType = Question.MAX_QUESTION_TYPE;
		}
		else
		{
			startType = questionType;
			endType = questionType;
		}

		for (int type = startType; type <= endType; type++)
		{
			if ((questionType != Question.ALL_QUESTION_TYPES) || (type != Question.ALTER_PROMPT))
			{
				i = _questionOrder[type].iterator();
				while (i.hasNext())
				{
					key = (Long) i.next();
					if (this.getQuestions().contains(key))
					{
						dlm.addElement(this.getQuestions().getQuestion(key));
					}
				}
			}
		}
	}

	/***************************************************************************
	 * Searches question list for all questions of a given tpe, places them in
	 * list until a given question is reached
	 * 
	 * @param questionType
	 *            type filter for question list
	 * @param dlm
	 *            list model to use in inserting questions
	 * @param endId
	 *            question list end, stop when you see this question
	 */
	public void fillList(int questionType, DefaultListModel dlm, Long endId)
	{
		int startType, endType;
		Iterator i;
		Long key;
		boolean found = false;

		if (questionType == Question.ALL_QUESTION_TYPES)
		{
			startType = Question.MIN_QUESTION_TYPE;
			endType = Question.MAX_QUESTION_TYPE;
		}
		else
		{
			startType = questionType;
			endType = questionType;
		}

		for (int type = startType;(type <= endType) && !found; type++)
		{
			if ((questionType != Question.ALL_QUESTION_TYPES) || (type != Question.ALTER_PROMPT))
			{
				i = _questionOrder[type].iterator();
				while (i.hasNext() && !found)
				{
					key = (Long) i.next();
					if (key.equals(endId))
					{
						found = true;
					}
					else if (this.getQuestions().contains(key))
					{
						dlm.addElement(this.getQuestions().getQuestion(key));
					}
				}
			}
		}
	}

	/***************************************************************************
	 * Returns true iff q1 preceeds q2 in study
	 * 
	 * @param q1
	 *            Id of question which may preceed q2
	 * @param q2
	 *            Id of question which may be preceeded by q1
	 */
	public boolean doesQuestionPreceed(Long q1, Long q2)
	{
		int startType, endType;
		Iterator i;
		Long key;
		boolean found = false;

		startType = Question.MIN_QUESTION_TYPE;
		endType = Question.MAX_QUESTION_TYPE;

		for (int type = startType;(type <= endType) && !found; type++)
		{
			i = _questionOrder[type].iterator();
			while (i.hasNext() && !found)
			{
				key = (Long) i.next();
				if (key.equals(q1))
				{
					return true;
				}
				else if (key.equals(q2))
				{
					return false;
				}
			}
		}

		return false;
	}

	/***************************************************************************
	 * Essentially makes sure order list matches question list
	 */
	public void verifyStudy()
	{
		for (int i = 0; i < Question.NUM_QUESTION_TYPES; i++)
		{
			Iterator it = getQuestionIterator(i);

			while (it.hasNext())
			{
				Long qid = (Long) it.next();

				if (getQuestions().getQuestion(qid) == null)
				{
					it.remove();
				}
			}
		}
	}

	/***************************************************************************
	 * Returns a bi-directional list iterator of questions for a category
	 * 
	 * @param category
	 *            category of question
	 * @return iterator list iterator or questions
	 */
	public ListIterator getQuestionIterator(int category)
	{
		return (_questionOrder[category].listIterator());
	}

	/***************************************************************************
	 * Remove all base or custom Questions from question list and order lists
	 * 
	 * @param base
	 *            Remove questions from base file or custom file
	 */
	public void removeQuestions()
	{
		Question q;

		Iterator i = this.getQuestions().values().iterator();

		while (i.hasNext())
		{
			q = (Question) i.next();

			i.remove();
			removeQuestion(q);
		}
	}

	/***************************************************************************
	 * Remove one Question from question map and order lists
	 * 
	 * @param q
	 *            question to remove
	 */
	public void removeQuestion(Question q)
	{
		removeLinksToQuestion(q);

		for (int i = 0; i < _questionOrder.length; i++)
		{
			_questionOrder[i].remove(q.UniqueId);
		}

		this.getQuestions().remove(q);
		setModified(true);
	}

	/***************************************************************************
	 * Searches question list for any questions linked to a question about to
	 * be removed, and removes those question links.
	 * 
	 * @param questionType
	 *            type filter for question list
	 * @param dlm
	 *            list model to use in inserting questions
	 */
	public void removeLinksToQuestion(Question lq)
	{
		Question q;

		Iterator i = this.getQuestions().values().iterator();

		while (i.hasNext())
		{
			q = (Question) i.next();

			if (q.link.active && (q.link.answer.questionId.equals(lq.UniqueId)))
			{
				q.link.active = false;
				q.link.answer = null;
			}
		}
	}

	/***************************************************************************
	 * Writes study specific information to xml output file
	 */
	public void writeInterviewStudy(Element e)
	{
		e.setInt("numalters", getNumAlters());
	}

	/***************************************************************************
	 * Reads in study information from an XML input file Includes files paths
	 * and arrays of question orders
	 * 
	 * @param studyFile
	 *            File from which to read study
	 */
	public void readInterviewStudy(Element e)
	{
			if (e.getElement("numalters") != null)
			{
				setNumAlters(e.getInt("numalters"));
			}
	}

	/***************************************************************************
	 * Reads in study information from an XML input file Includes files paths
	 * and arrays of question orders
	 * 
	 * @param studyFile
	 *            File from which to read study
	 */
	public void readPackageStudy(Document document)
	{
	//	String data;

		try
		{
			Element root = document.getRoot();
			setStudyId(Long.parseLong(root.getAttributeValue("Id")));

			root = root.getElement("Study");

			if (root.getElement("name") != null)
			{
				setStudyName(root.getTextString("name"));
			}

			if (root.getElement("numalters") != null)
			{
				setNumAlters(root.getInt("numalters"));
			}

			Elements elements = root.getElements("questionorder");
			while (elements.hasMoreElements())
			{
				int qOrderId;
				List<Long> questionOrder;
				Elements ids;

				Element element = elements.next();
				qOrderId = Integer.parseInt(element.getAttribute("questiontype"));
				questionOrder = (getQuestionOrderArray())[qOrderId];

				ids = element.getElements("id");
				while (ids.hasMoreElements())
				{
					questionOrder.add(new Long(ids.next().getLong()));
				}
			}
		}
		catch (Exception e)
		{
			/** @todo handle exception */
			e.printStackTrace();
		}
	}

	/***************************************************************************
	 * Reads all the questions from a file
	 * 
	 * @param f
	 *            File from which to read questions
	 */
	public void readQuestions(Document document)
	{
		Element root;
		Elements questions;

		/**
		 * Parse XML file
		 */
		root = document.getRoot();
		root = root.getElement("QuestionList");
		questions = root.getElements("Question");

		while (questions.hasMoreElements())
		{
			Question q = null;
			try
			{
				/* Question complete, add it */
				 q = new Question(questions.next());
				addQuestion(q);
			}
			catch (MalformedQuestionException e)
			{
				/* Don't create this question. Incomplete */
				//System.out.println("Question:" + q.getString());
				System.err.println("Malformed Question in file.");
				
			}
			catch (DuplicateQuestionException e)
			{
				/* Don't create this question. Incomplete */
				System.err.println("Duplicate Question in file.");
			}
		}
	}

	/**************************************************************
	 * Writes Study information to a file for later retrieval 
	 * Includes files paths and arrays of question orders
	 * 
	 * @param document XML element to which to add study information 
	 * @todo prune order lists, possibly need to load question files to do this
	 */
	public void writeStudyData(Element document)
	{
		Iterator it;
		int i;

		try
		{
			Element study = document.addElement("Study");

			study.addElement("name").setText(getStudyName());
			study.addElement("numalters").setInt(getNumAlters());

			for (i = 1; i < Question.NUM_QUESTION_TYPES; i++)
			{
				Element qorder = new Element("questionorder");
				it = (getQuestionOrderArray())[i].iterator();

				if (it.hasNext())
				{
					study.addElement(qorder).setAttribute("questiontype", Integer.toString(i));
					while (it.hasNext())
					{
						qorder.addElement("id").setLong(((Long) it.next()).longValue());
					}
				}
			}
		}
		catch (Exception ex)
		{
			JOptionPane.showMessageDialog(
					null,
					"Unable to write to this study file",
					"Study Writing Error",
					JOptionPane.ERROR_MESSAGE);

		}
	}

	/***********************************************************
	 * Writes all questions to a package file for later use
	 * 
	 * @param document
	 *            XML tree to which to add question
	 * @throws IOException
	 */
	public void writeAllQuestionData(Element document) 
		throws IOException
	{
		
		Element element = document.addElement("QuestionList");
		for(Question q : getQuestions().values())
		{
			q.writeQuestion(element.addElement("Question"));
		}
	}
	
}
