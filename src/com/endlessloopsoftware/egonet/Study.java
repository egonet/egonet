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
import java.util.*;
import java.util.Map.Entry;

import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JOptionPane;

import org.egonet.exceptions.DuplicateQuestionException;

import com.endlessloopsoftware.egonet.Shared.AlterNameModel;
import com.endlessloopsoftware.egonet.Shared.AlterSamplingModel;
import com.endlessloopsoftware.egonet.Shared.QuestionType;

/*******************************************************************************
 * Stores basic configuration data for the study including question order lists
 */
public class Study extends Observable implements Comparable<Study>
{
	private long              _uniqueId       = -1L;
   private String            _uiType         = Shared.TRADITIONAL_QUESTIONS;
   private int               _numAlters      = 40;
   private boolean           _studyDirty     = false;
   private boolean           _compatible     = true;
   private boolean           _inUse          = false;
   private String            _studyName      = "New Study";
   private boolean			skipQuestions = false;
   
   private Map<QuestionType,List<Long>> _questionOrder  = new HashMap<QuestionType,List<Long>>();
   
   private Question          _firstQuestion  = new Question("none");
   private QuestionList      _questions      = new QuestionList();

   /* Added for UNC */
   private AlterSamplingModel alterSamplingModel = AlterSamplingModel.ALL;
   private AlterNameModel alterNameModel = AlterNameModel.FIRST_LAST;
   private Integer alterSamplingParameter = null; 
   
   
   /**
   * Instantiates Default Study
   */
	public Study()
	{
		this._questions.clear();
		for(QuestionType type : QuestionType.values())
		    _questionOrder.put(type, new ArrayList<Long>());
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
	public int getNetworkSize()
	{
		return (_numAlters);
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
      return _questions.getQuestion(id);
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
	public List<Long> getQuestionOrder(QuestionType category) throws NoSuchElementException
	{
		return _questionOrder.get(category);
	}

	/***************************************************************************
	 * Working forward from beginning, find first unanswered question
	 * 
	 * @return index of first unanswered question
	 */
	public Question getFirstStatableQuestion()
	{
		Question statable = null;
		
		/**
		 * Try to find one all alters answer
		 */
		 Iterator<Long> questions = getQuestionOrder(Shared.QuestionType.ALTER_PAIR).iterator();
		while (questions.hasNext())
		{
			Question q = (Question) _questions.getQuestion(questions.next());

			if (q.isStatable() && !q.link.isActive())
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
			questions = getQuestionOrder(Shared.QuestionType.ALTER).iterator();
			while (questions.hasNext())
			{
				Question q = (Question) _questions.getQuestion(questions.next());

				if (q.isStatable())
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
	public void setNetworkSize(int n)
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
   
   public boolean confirmIncompatibleChange(JInternalFrame frame)
   {
	   return confirmIncompatibleChange((JFrame)null);
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
		if (_questions.contains(q.UniqueId))
			throw new DuplicateQuestionException("Question with uniqueId "+q.UniqueId+" already added to study: " + _questions.toString());
		
		_questions.addQuestion(q);
		setModified(true);
		
		/* If not in appropriate array list, add to that list too */
		if (!_questionOrder.get(q.questionType).contains(q.UniqueId))
		{
		    _questionOrder.get(q.questionType).add(q.UniqueId);
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

		if (_questionOrder.get(q.questionType).contains(follow.UniqueId) || (follow == _firstQuestion))
		{
			_questionOrder.get(q.questionType).remove(q.UniqueId);

			if (follow == _firstQuestion)
			{
				_questionOrder.get(q.questionType).add(0, q.UniqueId);
			}
			else
			{
				followloc = _questionOrder.get(q.questionType).indexOf(follow.UniqueId);
				_questionOrder.get(q.questionType).add(followloc + 1, q.UniqueId);
			}

			if (q.link.isActive())
			{
				if (!doesQuestionPreceed(q.link.getAnswer().questionId, q.UniqueId))
				{
					q.link.setAnswer(null);
				}
			}

			setModified(true);
		}
	}

	public void changeQuestionType(Question q, QuestionType type) throws DuplicateQuestionException
	{
		removeQuestion(q);
		q.questionType = type;
		addQuestion(q);
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
		Iterator<Long> i = _questionOrder.get(Shared.QuestionType.ALTER_PAIR).iterator();

		while (i.hasNext())
		{
			Long key = (Long) i.next();
			Question listQ = this._questions.getQuestion(key);

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
		boolean foundCentral = false;
		Iterator<Long> it = _questionOrder.get(Shared.QuestionType.ALTER_PAIR).iterator();

		while (it.hasNext())
		{
			Long key = it.next();
			Question q = this._questions.getQuestion(key);

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
			Iterator<Long> it2 = _questionOrder.get(Shared.QuestionType.ALTER_PAIR).iterator();

			while (it2.hasNext() && !foundCentral)
			{
				Long key = it2.next();
				Question q = this._questions.getQuestion(key);

				if ((q != null) && (q.answerType == Shared.AnswerType.CATEGORICAL))
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
	public void fillList(QuestionType questionType, DefaultListModel<Question> dlm)
	{
        for(Map.Entry<QuestionType,List<Long>> entry : _questionOrder.entrySet())
        {
            if(!entry.getKey().equals(questionType))
                continue;
            
            for(Long id : entry.getValue())
            {
                if(_questions.contains(id))
                    dlm.addElement(_questions.getQuestion(id));
            }
        }
	}
	
	   /***************************************************************************
     * Searches question list for all questions, places them in
     * list
     * 
     * @param questionType
     *            type filter for question list
     * @param dlm
     *            list model to use in inserting questions
     */
    public void fillList(DefaultListModel dlm)
    {
        Set<Entry<QuestionType, List<Long>>> entries = _questionOrder.entrySet();
        
        for(Map.Entry<QuestionType,List<Long>> entry : entries)
        {
            if(entry.getKey().equals(QuestionType.ALTER_PROMPT))
                continue;
            
            List<Long> questions = entry.getValue();
            for(Long id : questions)
            {
                if(_questions.contains(id))
                    dlm.addElement(_questions.getQuestion(id));
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
    public void fillList(DefaultListModel dlm, Long endId)
    {
        for(Map.Entry<QuestionType,List<Long>> entry : _questionOrder.entrySet())
        {
            if(entry.getKey().equals(QuestionType.ALTER_PROMPT))
                continue;
            
            for(Long id : entry.getValue())
            {
                if(id.equals(endId))
                    return;
                else if(_questions.contains(id))
                    dlm.addElement(_questions.getQuestion(id));
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
	public void fillList(QuestionType questionType, DefaultListModel dlm, Long endId)
	{
        for(Map.Entry<QuestionType,List<Long>> entry : _questionOrder.entrySet())
        {
            if(!entry.getKey().equals(questionType))
                continue;
            
            for(Long id : entry.getValue())
            {
                if(id.equals(endId))
                    return;
                else if(_questions.contains(id))
                    dlm.addElement(_questions.getQuestion(id));
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
		for(QuestionType qT : QuestionType.values())
		{
		    if(qT.equals(QuestionType.STUDY_CONFIG))
		        continue;
		    
		    List<Long> questionList = _questionOrder.get(qT);
		    for(Long key : questionList)
		    {
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
	    for (QuestionType type : QuestionType.values())
        {
            Iterator<Long> it = getQuestionIterator(type);

			while (it.hasNext())
			{
				Long qid = (Long) it.next();

				if (_questions.getQuestion(qid) == null)
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
	public ListIterator<Long> getQuestionIterator(QuestionType category)
	{
		return (_questionOrder.get(category).listIterator());
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

		Iterator i = this._questions.values().iterator();

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

		for (List<Long> orderList : _questionOrder.values())
		    orderList.remove(q.UniqueId);

		_questions.remove(q.UniqueId);
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
		Iterator<Question> i = this._questions.values().iterator();

		while (i.hasNext())
		{
			Question q = (Question) i.next();

			if (q.link.isActive() && (q.link.getAnswer().questionId.equals(lq.UniqueId)))
			{
				q.link.setAnswer(null);
			}
		}
	}

	public AlterSamplingModel getAlterSamplingModel() {
		return alterSamplingModel;
	}

	public void setAlterSamplingModel(AlterSamplingModel alterSamplingModel) {
		this.alterSamplingModel = alterSamplingModel;
	}

	public Integer getAlterSamplingParameter() {
		return alterSamplingParameter;
	}

	public void setAlterSamplingParameter(Integer alterSamplingParameter) {
		this.alterSamplingParameter = alterSamplingParameter;
	}

    public int getNumAlters()
    {
        if(alterSamplingModel.equals(AlterSamplingModel.RANDOM_SUBSET))
            return alterSamplingParameter;
        else if(alterSamplingModel.equals(AlterSamplingModel.NTH_ALTER))
            return getNetworkSize()/alterSamplingParameter;
        else
            return getNetworkSize();
    }

	public int compareTo(Study o) {
		return (int)(getStudyId() - o.getStudyId());
	}

	public AlterNameModel getAlterNameModel() {
		return alterNameModel;
	}

	public void setAlterNameModel(AlterNameModel alterNameModel) {
		this.alterNameModel = alterNameModel;
	}
	

	public String toString() {
		return getStudyName() + " (" + getStudyId() + ")";
	}

	public void setAllowSkipQuestions(boolean selected) {
		this.skipQuestions = selected;
		
	}
	
	public boolean getAllowSkipQuestions() {
		return skipQuestions;
	}
}
