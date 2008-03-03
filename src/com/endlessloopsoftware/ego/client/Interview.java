package com.endlessloopsoftware.ego.client;

/**
 * <p>Copyright: Copyright (c) 2002 - 2004 </p>
 * <p>Company: Endless Loop Software</p>
 * @author Peter Schoaff
 *
 * $Id: Interview.java,v 1.1 2005/08/02 19:35:59 samag Exp $
 */

import java.util.Collection;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.logging.Logger;

import javax.swing.DefaultListModel;
import javax.swing.JOptionPane;

import org.egonet.exceptions.CorruptedInterviewException;
import org.egonet.exceptions.MissingPairException;

import com.endlessloopsoftware.ego.Answer;
import com.endlessloopsoftware.ego.Question;
import com.endlessloopsoftware.ego.Study;
import com.endlessloopsoftware.ego.client.StatRecord.EgoAnswer;
import com.endlessloopsoftware.ego.client.statistics.Statistics;
import com.endlessloopsoftware.egonet.Shared;
import com.endlessloopsoftware.egonet.util.AnswerDataValue;
import com.endlessloopsoftware.egonet.util.InterviewDataValue;
import com.endlessloopsoftware.elsutils.ELSMath;
import com.endlessloopsoftware.elsutils.files.FileCreateException;

import electric.xml.Element;
import electric.xml.Elements;

public class Interview
{
   private final static Logger logger = Logger.getLogger("Interview");
   
	private final Answer[] _answers;
   private final Study    _study;
   private int[][]        _matrix;
   private Statistics     _stats               = null;
   private String[]       _egoName             = { "", ""};
   private boolean        _complete;
   private String[]       _alterList           = new String[0];

   private int            _qIndex              = 0;
   private final int      _numAlterPairs;
   private int            _numAnswers;
   private int            _numAlters;

   boolean                _statisticsAvailable = false;

	/********
	 * Create interview from question list
	 * @param client parent object for globals
	 * @param numAlters number of alters to be elicited
	 * @throws CorruptedInterviewException if unable to read interview
	 */
	public Interview(Study study) 
		throws CorruptedInterviewException
	{
		/* Locals */
		int j, k;
		Iterator questions;

		/* Calculate some interview values */
      _study          = study;
      _numAlters      = study.getNumAlters();
		_numAlterPairs  = ELSMath.summation(_numAlters - 1);
		_numAnswers =
			EgoClient.study.getQuestionOrder(Question.EGO_QUESTION).size()
				+ EgoClient.study.getQuestionOrder(Question.ALTER_PROMPT).size()
				+ (_numAlters * EgoClient.study.getQuestionOrder(Question.ALTER_QUESTION).size())
				+ (_numAlterPairs * EgoClient.study.getQuestionOrder(Question.ALTER_PAIR_QUESTION).size());
		_answers = new Answer[_numAnswers];

		/* Generate answer instances */
		_qIndex = 0;

		/* Ego Questions */
		questions = EgoClient.study.getQuestionOrder(Question.EGO_QUESTION).iterator();
		while (questions.hasNext())
		{
			Long questionId = (Long) questions.next();
			Question question = _study.getQuestions().getQuestion(questionId);

			if (question == null)
			{
				throw new CorruptedInterviewException();
			}
			else
			{
				_answers[_qIndex++] = new Answer(question.UniqueId, null);
			}
		}

		/* Alter Prompt Questions */
		questions = EgoClient.study.getQuestionOrder(Question.ALTER_PROMPT).iterator();
		while (questions.hasNext())
		{
			Long questionId = (Long) questions.next();
			Question question = _study.getQuestions().getQuestion(questionId);

			if (question == null)
			{
				throw new CorruptedInterviewException();
			}
			else
			{
				_answers[_qIndex++] = new Answer(question.UniqueId, null);
			}
		}

		/* Alter Questions */
		for (j = 0; j < _numAlters; j++)
		{
			questions = EgoClient.study.getQuestionOrder(Question.ALTER_QUESTION).iterator();
			int[] alter = { j };
			while (questions.hasNext())
			{
				Long questionId = (Long) questions.next();
				Question question = _study.getQuestions().getQuestion(questionId);
				if (question == null)
				{
					throw new CorruptedInterviewException();
				}
				else
				{
					_answers[_qIndex++] = new Answer(question.UniqueId, alter);
				}
			}
		}

		/* Alter Pair Questions */
		for (k = 0; k < _numAlters; k++)
		{
			for (j = (k + 1); j < _numAlters; j++)
			{
				questions = EgoClient.study.getQuestionOrder(Question.ALTER_PAIR_QUESTION).iterator();
				int[] alters = { k, j };
				while (questions.hasNext())
				{
					Question question = _study.getQuestions().getQuestion((Long) questions.next());

					if (question == null)
					{
						throw new CorruptedInterviewException();
					}
					else
					{
						if (question.statable)
						{
							_statisticsAvailable = true;
						}

						_answers[_qIndex++] = new Answer(question.UniqueId, alters);
					}
				}
			}
		}
	}

	/*****************************************
	 * Generate an interview from a datavalue downloaded from a server
	 * @param data
	 */
	public Interview(Study study, InterviewDataValue data)
	{
      System.out.println("Creating Interview from data object");
      _study          = study;
      _numAlters      = study.getNumAlters();
      _statisticsAvailable = true;
      
      _matrix         = data.getAdjacencyMatrix();
		_egoName 			 = new String[] {data.getFirstName(), data.getLastName()};
		_complete			 = data.getComplete().booleanValue();
		_alterList		 = data.getAlters();
		_numAnswers		 = data.getAnswerDataValues().length;
		
		_numAlterPairs  = ELSMath.summation(_alterList.length - 1);
		_numAnswers 		 = data.getAnswerDataValues().length;
		_answers 			 = new Answer[_numAnswers];
      
      //System.out.println(_study.getQuestions().size());
      //System.out.println(_study.getQuestions().dump());

		for (int i = 0; i < data.getAnswerDataValues().length; ++i)
		{
			AnswerDataValue answerData = data.getAnswerDataValues()[i];
         _answers[i] = new Answer(answerData);
         
         //System.out.println("Answer for: " + _study.getQuestion(_answers[i].questionId));
		}
	}
	
	/****
	 * Called when user shutting down program
	 */
	public void exit()
	{
		if (!_complete)
		{
			try
			{
            EgoClient.storage.writeInterviewFile();
			}
			catch (FileCreateException ignored)
			{
				System.err.println("Unable to write Interview File");
			}
		}
	}

	/****
	 * Searches question list for all questions and places them in list
	 * @param	dlm				list model to use in inserting questions
	 */
	public void fillList(DefaultListModel dlm)
	{
		dlm.removeAllElements();

		for (int i = 0; i < _numAnswers; i++)
		{
			//Question q = getQuestion(i);
         
         Question q  = _study.getQuestions().getQuestion(_answers[i].questionId);
         
         if (q.questionType == Question.ALTER_PAIR_QUESTION &&
             (_study.getUIType().equals(Shared.PAIR_ELICITATION) ||
              _study.getUIType().equals(Shared.THREE_STEP_ELICITATION)))
         {
            /* Skip Alter Pair Questions for Interactive Linking Studies */
         }
         else
         {
   			   String s = q.toString();

            if (q.questionType == Question.ALTER_QUESTION)
            {
               s = s + "; alter " + _answers[i].getAlters()[0];
            }
            else if (q.questionType == Question.ALTER_PAIR_QUESTION)
            {
               s = s + "; alters " + _answers[i].getAlters()[0] + "& " + _answers[i].getAlters()[1];
            }

            s = Question.questionTypeString(q.questionType) + ": " + s;

            dlm.addElement(s);
         }
		}
	}

	/****
	 * Returns total number of questions in an interview
	 * @return i number of questions
	 */
	public int getNumQuestions()
	{
		return _numAnswers;
	}

	/****
	 * Returns current answer from an interview
	 *  Note, multiple answers may refer to same question
	 * @return i question index
	 */
	private Answer getCurrentAnswer()
	{
		return _answers[_qIndex];
	}

	/****
	 * Sets current answer from an interview
	 * @param a new Answer
	 */
	private void setCurrentAnswer(Answer a)
	{
		/** @todo Validate answer */
		_answers[_qIndex] = a;
	}

	/****
	 * Returns current question from an interview
	 * @return i question index
	 */
	public int getQuestionIndex()
	{
		return _qIndex;
	}

	/****
	 * Returns current list of alters
	 * @return s String Array of alters
	 */
	public String[] getAlterList()
	{
		return _alterList;
	}

	/****
	 * Sets current list of alters
	 * @param s String Array of alters
	 */
	public void setAlterList(String[] s)
	{
		_alterList = s;
	}

	/****
	 * Gets a set containing all the answers which use a selected question
	 * @param qId Unique Identifier of question
	 * @return Set of answers using this question
	 */
	public Set getAnswerSubset(Long qId)
	{
		Set s = new HashSet(_numAlterPairs);

		for (int i = 0; i < _answers.length; i++)
		{
			if (_answers[i].questionId.equals(qId))
			{
				s.add(_answers[i]);
			}
		}

		return (s);
	}

	/****
	 * Gets a List containing all the answers to ego questions
	 * @return List of answers using this question
	 */
	public List getEgoAnswers()
	{
		List l = new ArrayList();
		int index = 0;
		Question q = _study.getQuestions().getQuestion(_answers[index].questionId);

		while (q.questionType == Question.EGO_QUESTION)
		{
			l.add(_answers[index]);
			q = _study.getQuestions().getQuestion(_answers[++index].questionId);
		}

		return (l);
	}
	
	/****
	 * Gets a List containing all the answers to alter questions
	 * @return List of answers using this question
	 */
	public List<Question> getAlterAnswers()
	{
		List<Question>  l = new ArrayList<Question> ();
		
		Collection<Question> questionList = _study.getQuestions().getQuestionMap().values();
		for(Question q : questionList)
		{
			if(q.questionType != Question.ALTER_QUESTION)
				continue;
			
			l.add(q);
		}
		
		return (l);
	}

	/****
	 * Gets name of interview subject
	 * @return String Array of first and last name
	 */
	public String[] getName()
	{
		String[] s = { _egoName[0], _egoName[1] };

		return s;
	}

	/****
	 * Sets name of interviewee
	 * @param first first name
	 * @param last last name
	 */
	public void setName(String first, String last)
	{
		_egoName[0] = first;
		_egoName[1] = last;
	}

	/****
	 * Sets current question Index
	 * @param i index of question
	 * @param force choose the answer even if it's not valid (e.g. may be linked)
	 * @return Choosen question
	 */
	public Question setInterviewIndex(int i, boolean force)
	{
		Question q = null;

		if (!force)
		{
			i = nextValidAnswer(i, true);
		}

		if ((i >= 0) && (i < _numAnswers))
		{
			_qIndex = i;

			q = (Question) getQuestion(_qIndex).clone();
			q.answer = _answers[_qIndex];
			
			// replaces $$n in String with alter name indexed as alter #n
			q.text = completeText(q.text, q.answer.getAlters());
		}

		return (q);
	}

	/****
	 * Working forward from beginning, find first unanswered question
	 * @return index of first unanswered question
	 */
	public int getFirstUnansweredQuestion()
	{
		int i = 0;
		int rv = getNumQuestions() - 1;

		i = nextValidAnswer(0, true);
		while (i != -1)
		{
			if (!_answers[i].answered)
			{
				rv = i;
				break;
			}

			i = nextValidAnswer(i + 1, true);
		}

		return (rv);
	}

	/****
	 * Working backwards from the current Answer, find the first answer
	 * which references a specified question. This is generally used for
	 * linked questions
	 * @param startIndex starting point from which to search
	 * @param id Unique Id of question
	 * @return answer Answer of matching question
	 */
	private Answer getPriorQuestionInstance(int startIndex, Long id)
	{
		Answer a = null;
		int i;

		for (i = (startIndex - 1); i >= 0; i--)
		{
			if (_answers[i].questionId.equals(id))
				break;
		}

		if (i >= 0)
		{
			a = _answers[i];
		}

		return (a);
	}

	/****
	 * Checks question link info of current answer to see if
	 * the question should be included in interview.
	 * @param index index of answer to check.
	 * @return bool true iff question passes link check
	 */
	private boolean checkQuestionLink(int index)
	{
		boolean b = false;
		Question q = getQuestion(index);

		if (q.link.active)
		{
			Answer a = getPriorQuestionInstance(index, q.link.answer.questionId);

			if (a != null)
			{
				if (q.link.answer.value == Answer.ALL_ADJACENT)
				{
					if (a.adjacent)
					{
						b = true;
					}
				}
				else
				{
					if (a.value == q.link.answer.value)
					{
						b = true;
					}
				}
			}
			else
			{
				/* This case means one of 2 things happened, the user linked to a non-existent question
					or the linked question comes after this question. Neither case is proper but the
					less evil appears to be to always ask this question in this case
				*/
				b = true;
			}
		}
		else
		{
			/* No link to check */
			b = true;
		}

		return (b);
	}

	/****
	 * Returns current question from an interview
	 * @return b true iff there are more questions
	 */
	public boolean hasNext()
	{
		int checkIndex = nextValidAnswer(_qIndex + 1, true);

		return (checkIndex != -1);
	}

	/****
	 * Returns next question from an interview which passes all link checks
	 * @param checkIndex answer at which to start
	 * @param forward true iff search forward, else search backwards
	 * @return answerIndex index of next or previous valid answer, -1 if none found
	 */
	private int nextValidAnswer(int checkIndex, boolean forward)
	{
		boolean b = false;

		while (!b && (checkIndex < _numAnswers) && (checkIndex >= 0))
		{
			if (checkQuestionLink(checkIndex))
			{
				b = true;
			}
			else
			{
				checkIndex = forward ? (checkIndex + 1) : (checkIndex - 1);
			}
		}

		if (!b)
		{
			checkIndex = -1;
		}

		return (checkIndex);
	}

	/****
	 * Returns current question from an interview
	 * @return i question index
	 */
	public boolean hasPrevious()
	{
		int checkIndex = nextValidAnswer(_qIndex - 1, false);

		return (checkIndex != -1);
	}

	/****
	 * Returns current question from an interview
	 * @return i question index
	 */
	public Question next()
	{
		Question q;

		_qIndex = nextValidAnswer(_qIndex + 1, true);

		q = (Question) getQuestion(_qIndex).clone();
		q.answer = _answers[_qIndex];
		q.text = completeText(q.text, q.answer.getAlters());

		if ((EgoClient.uiPath == EgoClient.DO_INTERVIEW) && ((_qIndex % 20) == 0))
		{
			try
			{
				EgoClient.storage.writeInterviewFile();
			}
			catch (FileCreateException ex)
			{
				/* reported at lower level */
			}
		}

		return (q);
	}

	/****
	 * Returns current question from an interview
	 * @return i question index
	 */
	public Question previous()
	{
		Question q;

		_qIndex = nextValidAnswer(_qIndex - 1, false);
		//		assert (qIndex != -1);

		q = (Question) getQuestion(_qIndex).clone();
		q.answer = _answers[_qIndex];
		q.text = completeText(q.text, q.answer.getAlters());

		return (q);
	}

	/****
	 * Is this question last alter prompt
	 * @return b true iff current question is last alter prompt
	 */
	public boolean isLastAlterPrompt()
	{
		boolean b = false;
		Question q = (Question) _study.getQuestions().getQuestion(_answers[_qIndex].questionId);

		b =
			(getQuestion(_qIndex).questionType == Question.ALTER_PROMPT)
				&& hasNext()
				&& (getQuestion(_qIndex + 1).questionType != Question.ALTER_PROMPT);

		return (b);
	}

	/****
	 */
	public String[] getAlterStrings(Question q)
	{
		String[] s = new String[2];

		try
		{
			if ((q.answer.getAlters().length > 0) && (q.answer.getAlters()[0] != -1))
			{
				s[0] = _alterList[q.answer.getAlters()[0]];
			}

			if ((q.answer.getAlters().length > 1) && (q.answer.getAlters()[1] != -1))
			{
				s[1] = _alterList[q.answer.getAlters()[1]];
			}
		}
		catch (Exception ex)
		{
			s[0] = "";
			s[1] = "";
		}

		return s;
	}

	/****
	 * Returns alter or alter pair from the index into an interview
	 * @param index question index
	 * @return pair int array containing alter pair
	 * @throws NoSuchElementException
	 */
	private int[] calculateAlterPair(int index)
	{
		int na = EgoClient.study.getNumAlters();
		int strip = 0;
		int primary = -1;
		int secondary = -1;
		int[] rval = new int[2];

		try
		{
			/* Start by stripping ego and alter prompt questions */
			strip =
				EgoClient.study.getQuestionOrder(Question.EGO_QUESTION).size()
					+ EgoClient.study.getQuestionOrder(Question.ALTER_PROMPT).size();

			if (index < strip)
			{
				throw new NoSuchElementException();
			}

			index -= strip;

			/* Check alter questions */
			strip = na * EgoClient.study.getQuestionOrder(Question.ALTER_QUESTION).size();

			if (index < strip)
			{
				/* It's an alter question */
				primary = index / EgoClient.study.getQuestionOrder(Question.ALTER_QUESTION).size();
				secondary = -1;
			}
			else
			{
				index -= strip;

				if (index >= _numAlterPairs)
				{
					throw new NoSuchElementException();
				}

				primary = 1;
				while ((na - primary) < index)
				{
					index -= primary;
					primary++;
				}
			}
		}
		catch (NoSuchElementException ex)
		{
			primary = -1;
			secondary = -1;
		}

		rval[0] = primary;
		rval[1] = secondary;
		return (rval);
	}

	private Question getQuestion(int index)
	{
		return ((Question) _study.getQuestion(_answers[index].questionId));
	}

	/****
	 * Replaces alter name placeholders with alter names
	 * @param s Question string to parse
	 * @param alters names of alters to put in placeholders
	 * @return modified string
	  */
	private String completeText(String s, int[] alters)
	{
		int parsePtr;
		String oldS = s;

		try
		{
			for (parsePtr = s.indexOf("$$1");(parsePtr != -1); parsePtr = s.indexOf("$$1"))
			{
				s = s.substring(0, parsePtr) + _alterList[alters[0]] + s.substring(parsePtr + 3);
			}

			for (parsePtr = s.indexOf("$$2");(parsePtr != -1); parsePtr = s.indexOf("$$2"))
			{
				s = s.substring(0, parsePtr) + _alterList[alters[1]] + s.substring(parsePtr + 3);
			}

         for (parsePtr = s.indexOf("$$-1");(parsePtr != -1); parsePtr = s.indexOf("$$-1"))
         {
            s = s.substring(0, parsePtr) + _alterList[alters[0] - 1] + s.substring(parsePtr + 4);
         }

			for (parsePtr = s.indexOf("$$");(parsePtr != -1); parsePtr = s.indexOf("$$"))
			{
				s = s.substring(0, parsePtr) + _alterList[alters[0]] + s.substring(parsePtr + 2);
			}
		}
		catch (Exception ex)
		{
			s = oldS;
		}

		return s;
	}

	/********
	 * Read interview information from an xml structure
	 * @param parent scope for extracting globals
	 * @param e XML Element, parent of interview tree
	 * @return Interview which is read
	 * @throws CorruptedInterviewException if unable to read interview
	 */
	public static Interview readInterview(Element e) throws CorruptedInterviewException
	{
		Interview interview;
		String[] lAlterList;
		Element alterListElem = e.getElement("AlterList");
		Element answerListElem = e.getElement("AnswerList");

		try
		{
			/* Read alter list so we can size interview record */
			lAlterList = readAlters(alterListElem);
			interview = new Interview(EgoClient.study);
			interview._alterList = lAlterList;

			/* Read answers */
         EgoClient.study.readInterviewStudy(e);
			interview._complete = e.getBoolean("Complete");

			/* Read interviewee name */
			Element egoNameElem = e.getElement("EgoName");

			if (egoNameElem != null)
			{
				interview._egoName[0] = egoNameElem.getString("First");
				interview._egoName[1] = egoNameElem.getString("Last");
			}
			readAnswers(interview, answerListElem);
		}
		catch (CorruptedInterviewException ex)
		{
			interview = null;
			throw (ex);
		}
		catch (Exception ex)
		{
			interview = null;
			ex.printStackTrace();
		}

		return (interview);
	}

	/********
	 * Read alter list from an xml tree
	 * @param e XML Element, parent of alter list
	 * @return list List of Alters
	 */
	private static String[] readAlters(Element e)
	{
		Elements alterIter = e.getElements("Name");
		String[] lAlterList;
		int lNumAlters;
		int index = 0;

		lNumAlters = alterIter.size();
		lAlterList = new String[lNumAlters];

		while (alterIter.hasMoreElements())
		{
			lAlterList[index++] = alterIter.next().getTextString();
		}

		return (lAlterList);
	}

	/********
	 * Read alter list from an xml tree
	 * @param interview Interview to read answers into
	 * @param e XML Element, parent of alter list
	 * @throws CorruptedInterviewException if unable to read interview
	 */
	private static void readAnswers(Interview interview, Element e) throws CorruptedInterviewException
	{
		Elements answerIter = e.getElements("Answer");
		int index = 0;

		if (interview._numAnswers != answerIter.size())
		{
			System.err.println("interview.numAnswers != answerIter.size in Interview::readAnswers; ");
			throw (new CorruptedInterviewException());
		}

		while (answerIter.hasMoreElements())
		{
			try
			{
				Answer oldAnswer = interview._answers[index];
				Answer newAnswer = Answer.readAnswer(answerIter.next());

				if (oldAnswer.questionId.equals(newAnswer.questionId))
				{
					interview._answers[index++] = newAnswer;
				}
				else
				{
					throw (new CorruptedInterviewException());
				}
			}
			catch (Exception ex)
			{
				System.err.println("Answer::readAnswer failed in Interview::readAnswers; " + ex);
			}
		}
	}

	/********
	 * Add interview information to an xml structure for output to a file
	 * @param e XML Element, parent of interview tree
	 */
	public void writeInterview(Element e)
	{
		boolean success = true;
		Element alterListElem = e.addElement("AlterList");
		Element answerListElem = e.addElement("AnswerList");
		Element egoNameElem = e.addElement("EgoName");

		EgoClient.study.writeInterviewStudy(e);
		e.addElement("Complete").setBoolean(_complete);
		egoNameElem.addElement("First").setString(_egoName[0]);
		egoNameElem.addElement("Last").setString(_egoName[1]);

		for (int i = 0; i < _alterList.length; i++)
		{
			alterListElem.addElement("Name").setText(_alterList[i]);
		}

		for (int i = 0; i < _answers.length; i++)
		{
			try
			{
				_answers[i].writeAnswer(answerListElem);
			}
			catch (Exception ex)
			{
				JOptionPane.showMessageDialog(
					EgoClient.frame,
					"An error occurred while attempting to write an answer to the interview file. " + ex,
					"Unable to Write Interview",
					JOptionPane.ERROR_MESSAGE);
				success = false;
			}
		}

		if (!success)
		{
			JOptionPane.showMessageDialog(
				EgoClient.frame,
				"An error occurred while attempting to write this interview.",
				"Unable to Write Interview",
				JOptionPane.ERROR_MESSAGE);
		}
	}

	/********
	 * Returns complete attribut for interview
	 * @returns complete
	 */
	public boolean isComplete()
	{
		return _complete;
	}

	/********
	 * Ego has answered all questions. Write file and generate stats
	 * @throws FileCreateException
	 */
	public void completeInterview() throws FileCreateException
	{
		/*****
		 * Generate statistics for the first statable question
		 */
		Question q = EgoClient.study.getFirstStatableQuestion();

		_complete = true;
		EgoClient.storage.writeInterviewFile();

		if (q != null)
		{
			Statistics stats = EgoClient.interview.generateStatistics(q);
			EgoClient.storage.writeStatisticsFiles(stats, _egoName);
		}
	}

	/********
	 * Add interview information to an xml structure for output to a file
	 * @param e XML Element, parent of interview tree
	 */
	public void writeEgoAnswers(Element e)
	{
		Iterator egoAnswers = getEgoAnswers().iterator();
		Element eqList = e.addElement("EgoAnswers");

		while (egoAnswers.hasNext())
		{
			Answer answer = (Answer) egoAnswers.next();

			try
			{
				Element aElement = eqList.addElement("EgoAnswer");
				aElement.addElement("Title").setString(_study.getQuestions().getQuestion(answer.questionId).title);

				if (answer.answered)
				{
					aElement.addElement("Answer").setString(answer.string);
					aElement.addElement("AnswerIndex").setInt(answer.value);
				}
				else
				{
					aElement.addElement("Answer").setString("N/A");
					aElement.addElement("AnswerIndex").setInt(Answer.NO_ANSWER);
				}
			}
			catch (Exception ex)
			{
				System.err.println("Failure in Interview::writeEgoAnswers; " + ex);
			}
		}
	}

   /********
    * Add interview information to an xml structure for output to a file
    * @param e XML Element, parent of interview tree
    */
   public EgoAnswer[] getEgoAnswerArray(StatRecord record)
   {
      Iterator egoAnswerIter  = getEgoAnswers().iterator();
      EgoAnswer[] egoAnswers  = new EgoAnswer[getEgoAnswers().size()];
      int index               = 0;

      while (egoAnswerIter.hasNext())
      {
         Answer answer = (Answer) egoAnswerIter.next();
         
         if (answer.answered)
         {
            egoAnswers[index++] = record.new EgoAnswer(_study.getQuestions().getQuestion(answer.questionId).title,
                                                       answer.string, answer.value);
         }
         else
         {
            egoAnswers[index++] = record.new EgoAnswer(_study.getQuestions().getQuestion(answer.questionId).title,
                                                       "N/A", Answer.NO_ANSWER);
         }
      }
      
      return egoAnswers;
   }

	/********
	 * Has ego completed all questions?
	 * @return Complete
	 */
	public boolean getInterviewComplete()
	{
		return (_complete);
	}
	
   /********
    * How many alters were used to generate this interview
    * @return _numAlters
    */
   public int getNumAlters()
   {
      return (_numAlters);
   }
   
   /********
    * What study is used for this interview
    * @return _numAlters
    */
   public Study getStudy()
   {
      return (_study);
   }

    /**
     * Returns statistics
     * @return the statistics
     */
	public Statistics getStats()
	{
		return _stats;
	}

	/**
	 * @param q The stats to set.
	 */
	public Statistics generateStatistics(Question q)
	{
		_stats = Statistics.generateInterviewStatistics(this, q);
		return _stats;
	}
   
   /********
    * Goes through Set of answers creating a matrix representing the adjacency graph
    * for a set of alters. For each pair of alters if answer value > 0 an edge is placed
    * between the alters
    * @param answers    Set of alter pair answers
    * @param numAlters Total alters in interview
    * @param weighted  whether to use actual answer values for matrix or 1:0
    * @return Matrix representing non-directed graph of alters
    * @throws MissingPairException
    */
   public int[][] generateAdjacencyMatrix(Question q, boolean weighted)
      throws MissingPairException
   {
      if (_study.getUIType().equals(Shared.TRADITIONAL_QUESTIONS))
      {
         int[][] m = new int[_numAlters][_numAlters];

         /*
          * init to make sure we get all pairs, in the case of linked questions
          * not all will be answered
          */
         
         for (int i = 0; i < _numAlters; i++)
         {
            for (int j = 0; j < _numAlters; j++)
            {
               m[i][j] = 0;
            }

            m[i][i] = 1;
         }

         for (Iterator it = getAnswerSubset(q.UniqueId).iterator(); it.hasNext();)
         {
            Answer a = (Answer) it.next();

            if (weighted)
            {
              // m[a.getAlters()[0]][a.getAlters()[1]] = a.value;
               //m[a.getAlters()[1]][a.getAlters()[0]] = a.value;
            	 m[a.getAlters()[0]][a.getAlters()[1]] = (a.adjacent) ? a.value : 0;
                 m[a.getAlters()[1]][a.getAlters()[0]] = (a.adjacent) ? a.value : 0;
            }
            else
            {
               m[a.getAlters()[0]][a.getAlters()[1]] = (a.adjacent) ? 1 : 0;
               m[a.getAlters()[1]][a.getAlters()[0]] = (a.adjacent) ? 1 : 0;
            }
         }
         
         _matrix = m;
      }

      return (_matrix);
   }

   public void rewind()
   {
	   while(hasPrevious())
		   previous();
   }

public Answer[] get_answers() {
	return _answers;
}

}

/**
 * $Log: Interview.java,v $
 * Revision 1.1  2005/08/02 19:35:59  samag
 * Initial checkin
 *
 * Revision 1.12  2004/04/11 00:17:13  admin
 * Improving display of Alter Prompt questions from Applet UI Interviews
 *
 * Revision 1.11  2004/04/08 15:06:07  admin
 * EgoClient now creates study summaries from Server
 * EgoAuthor now sets active study on server
 *
 * Revision 1.10  2004/04/06 20:29:22  admin
 * First pass as supporting interactive applet linking interviews
 *
 * Revision 1.9  2004/04/06 15:46:11  admin
 * cvs tags in headers
 *
 */