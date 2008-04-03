package com.endlessloopsoftware.ego;

/**
 * <p>Title: Egocentric Networks Client Program</p>
 * <p>Description: Subject Interview Client</p>
 * <p>Copyright: Copyright (c) 2002 - 2004 </p>
 * <p>Company: Endless Loop Software</p>
 * @author Peter Schoaff
 *
 * $Id: Study.java,v 1.1 2005/08/02 19:36:02 samag Exp $
 */

import java.io.IOException;
import java.util.*;

import javax.ejb.CreateException;
import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import org.egonet.exceptions.DuplicateQuestionException;
import org.egonet.exceptions.MalformedQuestionException;

import com.endlessloopsoftware.egonet.Shared;
import com.endlessloopsoftware.egonet.interfaces.StudySBRemote;
import com.endlessloopsoftware.egonet.interfaces.StudySBRemoteHome;
import com.endlessloopsoftware.egonet.interfaces.StudySBUtil;
import com.endlessloopsoftware.egonet.util.QuestionDataValue;
import com.endlessloopsoftware.egonet.util.StudyDataValue;
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
   private List[]            _questionOrder  = new List[Question.NUM_QUESTION_TYPES];
   private Question          _firstQuestion  = new Question("none");
   private QuestionList      _questions      = new QuestionList();
   private int               _totalQuestions = -1;
   private static Properties _prop           = new Properties();

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
		this.getQuestions().removeAll();

		for (int i = 0; i < _questionOrder.length; i++)
		{
			_questionOrder[i] = new ArrayList();
		}
	}
	
  /**
   * Instantiates Study based on a StudyDataValue downloaded from a survey server
   */
	public Study(StudyDataValue data)
	{
		_uniqueId	      = data.getId().longValue();
      _uiType        = data.getUIType();
		_numAlters     = data.getNumAlters();
		_studyName     = data.getStudyName();
		
		Long[][] orders	= data.getQuestionOrder();
		
		for (int i = 0; i < Question.NUM_QUESTION_TYPES; ++i)
		{
			if (orders[i] != null)
			{
				_questionOrder[i] = Arrays.asList(orders[i]);
			}
			else
			{
				_questionOrder[i] = new ArrayList();
			}
		}

		QuestionDataValue[] questionData = data.getQuestionDataValues();
		for (int i = 0; i < questionData.length; ++i)
		{
			Question question = new Question(questionData[i]);
         
         if ((question.questionType == Question.ALTER_PAIR_QUESTION) && isAppletUI())
         {
            question.statable = true;
         }
         
			_questions.addQuestion(question);
		}
      
      verifyStudy();
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
	public List[] getQuestionOrderArray()
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
	public List getQuestionOrder(int category) throws NoSuchElementException
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
	//	String data;

		try
		{
			if (e.getElement("numalters") != null)
			{
				setNumAlters(e.getInt("numalters"));
			}

		}
		catch (Exception ex)
		{
			/** @todo handle exception */
			ex.printStackTrace();
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
				List questionOrder;
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
		Element root, question;
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
		Iterator it;
		Element element;

		element = document.addElement("QuestionList");

		it = getQuestions().getQuestionMap().values().iterator();

		while (it.hasNext())
		{
			Question q = (Question) it.next();
			q.writeQuestion(element.addElement("Question"), getQuestions());
		}
	}

	/**
	 * @author Peter Schoaff
	 *
	 * Store study in selected server.
	 */
	public boolean writeDBStudy(JFrame frame, String server, char[] password)
	{
		boolean rval = true;
		
		try
		{
			_prop.setProperty("java.naming.factory.initial", "org.jnp.interfaces.NamingContextFactory");
			_prop.setProperty("java.naming.provider.url", server + ":1099");
			//System.out.println(_prop.getProperty("java.naming.provider.url"));
			
			StudySBRemoteHome studyHome 	= StudySBUtil.getHome(_prop);
			StudySBRemote		studySB		= studyHome.create();
			StudyDataValue    data        = new StudyDataValue();
			
         data.setUIType(com.endlessloopsoftware.egonet.Shared.THREE_STEP_ELICITATION);
			data.setNumAlters(getNumAlters());
			data.setStudyName(getStudyName());
			
			Long[][] questionOrder = new Long[Question.NUM_QUESTION_TYPES][];
			
			for (int i = 1; i < Question.NUM_QUESTION_TYPES; ++i)
			{
				List qorder = getQuestionOrder(i);
				questionOrder[i] = new Long[qorder.size()];
				qorder.toArray(questionOrder[i]);
			}
			
			data.setQuestionOrder(questionOrder);
			
			Iterator it = getQuestions().getQuestionMap().values().iterator();

			while (it.hasNext())
			{
				Question q = (Question) it.next();
				data.addQuestionDataValue(q.getDataValue(this.getQuestions(), data.getId()));
			}
			
         String epassword = "215-121-242-47-99-238-5-61-133-183-0-216-187-250-253-30-115-177-254-142-161-83-108-56";//SymmetricKeyEncryption.encrypt(new String(password));
			studySB.createStudy(data, epassword);
		}
		catch (CreateException e)
		{
			JOptionPane.showMessageDialog(frame,
				    "Unable to store study.\n" + e.getMessage(),
				    "Server error",
				    JOptionPane.ERROR_MESSAGE);
			
			rval = false;
		}
		catch (Exception e)
		{
			JOptionPane.showMessageDialog(frame,
				    "Unable to store study.\n",
				    "Server error",
				    JOptionPane.ERROR_MESSAGE);
			
			// TODO Auto-generated catch block
			e.printStackTrace();
			rval = false;
		}
		
		return rval;
	}
	
}

/**
 * $Log: Study.java,v $
 * Revision 1.1  2005/08/02 19:36:02  samag
 * Initial checkin
 *
 * Revision 1.14  2004/04/11 15:19:28  admin
 * Using password to access server
 *
 * Remote study summary in seperate thread with progress monitor
 *
 * Revision 1.13  2004/04/11 00:17:13  admin
 * Improving display of Alter Prompt questions from Applet UI Interviews
 *
 * Revision 1.12  2004/04/07 00:08:31  admin
 * updating manifests, jar creation. Removing author specific objects from
 * client specific references
 *
 * Revision 1.11  2004/04/06 20:29:22  admin
 * First pass as supporting interactive applet linking interviews
 *
 * Revision 1.10  2004/04/06 14:56:02  admin
 * Work to integrate with Applet Linking UI
 *
 * Revision 1.9  2004/04/02 19:48:58  admin
 * Keep Study Id when possible
 * Store updated time in file
 *
 * Revision 1.8  2004/03/29 00:35:09  admin
 * Downloading Interviews
 * Fixing some bugs creating Interviews from Data Objects
 *
 * Revision 1.7  2004/03/28 17:31:31  admin
 * More error handling when uploading study to server
 * Server URL selection dialog for upload
 *
 * Revision 1.6  2004/03/23 14:58:47  admin
 * Update UI
 * Study creation now occurs in instantiators
 *
 * Revision 1.5  2004/03/21 20:29:37  admin
 * Warn before making incompatible changes to in use study file
 *
 * Revision 1.4  2004/03/21 14:00:38  admin
 * Cleaned up Question Panel Layout using FOAM
 *
 * Revision 1.3  2004/02/10 20:10:42  admin
 * Version 2.0 beta 3
 *
 * Revision 1.2  2004/01/23 13:36:07  admin
 * Updating Libraries
 * Allowing upload to web server
 *
 * Revision 1.1  2003/12/05 19:15:43  admin
 * Extracting Study
 * Revision 1.3 2003/12/04 15:14:08 admin Merging EgoNet
 * and EgoClient projects so that they can share some common classes more
 * easily.
 * 
 * Revision 1.2 2003/11/25 19:25:44 admin Warn before closing window
 * 
 * Revision 1.1.1.1 2003/06/08 15:09:40 admin Egocentric Network Survey
 * Authoring Module
 * 
 * Revision 1.10 2002/08/11 22:26:06 admin Final Statistics window, new file
 * handling
 * 
 * Revision 1.9 2002/08/08 17:07:26 admin Preparing to change file system
 * 
 * Revision 1.8 2002/07/25 14:54:24 admin Question Links
 * 
 * Revision 1.7 2002/07/24 14:17:10 admin xml files, links
 * 
 * Revision 1.6 2002/07/18 14:43:06 admin New Alter Prompt Panel, packages
 * 
 * Revision 1.5 2002/06/30 15:59:18 admin Moving questions in lists, between
 * lists Better category input
 * 
 * Revision 1.4 2002/06/26 15:43:43 admin More selection dialog work File
 * loading fixes
 * 
 * Revision 1.3 2002/06/25 15:41:02 admin Lots of UI work
 * 
 * Revision 1.2 2002/06/21 22:47:12 admin question lists working again
 * 
 * Revision 1.1 2002/06/21 21:53:29 admin new files
 * 
 * Revision 1.2 2002/06/16 17:53:10 admin Working with files
 * 
 * Revision 1.1 2002/06/15 14:19:51 admin Initial Checkin of question and
 * survey General file system work
 *  
 */
