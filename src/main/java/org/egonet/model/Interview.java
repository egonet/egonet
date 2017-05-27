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
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.swing.DefaultListModel;

import org.egonet.exceptions.CorruptedInterviewException;
import org.egonet.exceptions.MissingPairException;
import org.egonet.gui.EgoStore;
import org.egonet.model.Shared.QuestionType;
import org.egonet.statistics.StatRecord;
import org.egonet.statistics.Statistics;
import org.egonet.statistics.StatRecord.EgoAnswer;
import org.egonet.util.ELSMath;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;

public class Interview implements Comparable<Interview> {

	final private static Logger logger = LoggerFactory.getLogger(Interview.class);

	private Answer[] _answers;

	private final Study _study;

	private int[][] _matrix;

	private Statistics _stats = null;

	private String notes = "";

	private boolean _complete;

	private String[] _alterList = new String[0]; // so alter pair is at least stateable

        /* This matrix will contain the alters of every question prompt. */
        private String[][] _alterQuestionPromptList;

	private int _qIndex = 0;

	private int _numAlterPairs;

	private int _numAnswers;

	public boolean _statisticsAvailable = false;

	private final String sIntName;

	private boolean followup = false;





	public boolean isFollowup() {
		return followup;
	}

	public void setFollowup(boolean followup) {
		this.followup = followup;
	}

	public String getIntName() {
		return sIntName;
	}


	/***************************************************************************
	 * Create interview from question list
	 *
	 * @param client
	 *            parent object for globals
	 * @param numAlters
	 *            number of alters to be elicited
	 * @throws CorruptedInterviewException
	 *             if unable to read interview
	 */
	public Interview(Study study, String sIntName) throws CorruptedInterviewException {

		this.sIntName = sIntName;
		_study = study;

                //Initializes the
                _alterQuestionPromptList = new String[_study.getQuestionOrder(QuestionType.ALTER_PROMPT).size()][0];
		//_alterList = new String[]{null};

		reinitializeAlterData();
	}

	/**
	 * Helps us find old answers based on question ID
	 * @param haystack old array of answers
	 * @param needle unique id to find
	 * @return the answer that matches in the array
	 */
	Answer findUniqueQuestion(Answer[] haystack, long needle) {
		Answer ret = null;
		for(Answer possible : haystack) {
			if(possible.getQuestionId().equals(needle))
				ret = possible;
		}

		return ret;
	}

	/**
	 * This method resets many data structures that are dependent on the number of alters. This used to happen
	 * in a constructor, but now that alter lists have a min and max number of elements, we need to resize these
	 * data on the fly.
	 * @throws CorruptedInterviewException
	 */
	public void reinitializeAlterData() throws CorruptedInterviewException {

                /* Calculate some interview values */
		int _numAlters = _alterList.length;
		_numAlterPairs = ELSMath.summation(_numAlters - 1);
		set_numAnswers(_study.getQuestionOrder(QuestionType.EGO).size()
				+ _study.getQuestionOrder(QuestionType.ALTER_PROMPT)
						.size()
				+ (_numAlters * _study.getQuestionOrder(
						QuestionType.ALTER).size())
				+ (_numAlterPairs * _study.getQuestionOrder(
						QuestionType.ALTER_PAIR).size()));


		// we need to preserve old data, so hold on to any interesting questions (avoid null _answers)
		Answer [] _oldanswers = new Answer[_answers != null ? _answers.length : 0];
		if(_answers != null)
			System.arraycopy(_answers, 0, _oldanswers, 0, _answers.length);

		_answers = new Answer[get_numAnswers()];

		/* Generate answer instances */
		int counter = 0;

		/* Ego Questions */
		Iterator questions = _study.getQuestionOrder(Shared.QuestionType.EGO).iterator();
		while (questions.hasNext()) {
			Long questionId = (Long) questions.next();
			Question question = _study.getQuestions().getQuestion(questionId);

			if (question == null) {
				throw new CorruptedInterviewException();
			}


			int newindex = counter++;
			Answer oldAnswer = findUniqueQuestion(_oldanswers, question.UniqueId);

			// if no previous, new, otherwise try to keep
			if(oldAnswer == null) {
            	Answer a = new Answer();
            	a.setQuestionId(question.UniqueId);
				_answers[newindex] = a;
			}
			else
				_answers[newindex] = oldAnswer;
		}

		/* Alter Prompt Questions */
		questions = _study.getQuestionOrder(QuestionType.ALTER_PROMPT).iterator();

                    while (questions.hasNext()) {
                            Long questionId = (Long) questions.next();
                            Question question = _study.getQuestions().getQuestion(questionId);

                            if (question == null) {
                                    throw new CorruptedInterviewException();
                            }
                            Answer oldAnswer = findUniqueQuestion(_oldanswers, question.UniqueId);
                            int newindex = counter++;

                            // if no previous, new, otherwise try to keep
                            if(oldAnswer == null) {
                            	Answer a = new Answer();
                            	a.setQuestionId(question.UniqueId);
                                _answers[newindex] = a;
                            }
                            else
                                _answers[newindex] = oldAnswer;
                            //_answers[counter++] = new Answer(question.UniqueId);



                }
		int j,k;

		/* Alter Questions */
		for (j = 0; j < _numAlters; j++) {
			questions = _study.getQuestionOrder(QuestionType.ALTER).iterator();
			int[] alter = { j };
			while (questions.hasNext()) {
				Long questionId = (Long) questions.next();
				Question question = _study.getQuestions().getQuestion(questionId);
				if (question == null) {
					throw new CorruptedInterviewException();
				}

				Answer answer = new Answer();
				answer.setQuestionId(question.UniqueId); answer.setAlters(alter);
				_answers[counter++] = answer;

			}
		}

		/* Alter Pair Questions */
		for (k = 0; k < _numAlters; k++) {
			for (j = (k + 1); j < _numAlters; j++) {
				questions = _study.getQuestionOrder(QuestionType.ALTER_PAIR).iterator();
				int[] alters = { k, j };
				while (questions.hasNext()) {
					Question question = _study.getQuestions().getQuestion((Long) questions.next());

					if (question == null) {
						throw new CorruptedInterviewException();
					}
					Answer answer = new Answer();
					answer.setQuestionId(question.UniqueId);
					answer.setAlters(alters);
					_answers[counter++] = answer;
				}
			}
		}

		// really shouldn't make stats available depend on alter answers
		questions = _study.getQuestionOrder(QuestionType.ALTER_PAIR).iterator();
		while (questions.hasNext()) {
			Question question = _study.getQuestions().getQuestion((Long) questions.next());
			if (question == null) {
				throw new CorruptedInterviewException();
			} else {
				if (question.isStatable()) {
					_statisticsAvailable = true;
				}
			}
		}

	}

	/***************************************************************************
	 * Searches question list for all questions and places them in list
	 *
	 * @param dlm
	 *            list model to use in inserting questions
	 */
	public void fillList(DefaultListModel<Question> dlm) {
		dlm.removeAllElements();

		for (int i = 0; i < get_numAnswers(); i++) {
			Question q = getQuestion(i);

			// Answer a = _answers[i];
			//Question q = _study.getQuestions().getQuestion(a.getQuestionId());
			//q.setAnswer(a);

			if (q.questionType == QuestionType.ALTER_PAIR
					&& (_study.getUIType().equals(Shared.PAIR_ELICITATION) || _study
							.getUIType().equals(Shared.THREE_STEP_ELICITATION))) {
				/* Skip Alter Pair Questions for Interactive Linking Studies */
			} else {
				dlm.addElement(q);
			}
		}
	}

	/***************************************************************************
	 * Returns total number of questions in an interview
	 *
	 * @return i number of questions
	 */
	public int getNumQuestions() {
		return get_numAnswers();
	}

	/***************************************************************************
	 * Returns current question from an interview
	 *
	 * @return i question index
	 */
	public int getQuestionIndex() {
		return _qIndex;
	}

	/***************************************************************************
	 * Returns current list of alters
	 *
	 * @return s String Array of alters
	 */
	public String[] getAlterList() {
		return _alterList;
	}

	/***************************************************************************
	 * Sets current list of alters
	 *
	 * @param s
	 *            String Array of alters
	 * @throws CorruptedInterviewException
	 */
	public void setAlterList(String[] s) throws CorruptedInterviewException {
		_alterList = s;
		reinitializeAlterData();
	}

	/***************************************************************************
	 * Gets a set containing all the answers which use a selected question
	 *
	 * @param qId
	 *            Unique Identifier of question
	 * @return Set of answers using this question
	 */
	public Set<Answer> getAnswerSubset(Long qId) {
		Set<Answer> s = new HashSet<Answer>(_numAlterPairs);

		for (int i = 0; i < _answers.length; i++) {
			if (_answers[i].getQuestionId().equals(qId)) {
				s.add(_answers[i]);
			}
		}

		return (s);
	}

	/***************************************************************************
	 * Gets a List containing all the answers to ego questions
	 *
	 * @return List of answers using this question
	 */
	public List<Answer> getEgoAnswers() {
		List<Answer> l = new ArrayList<Answer>();
		int index = 0;
		Question q = _study.getQuestions().getQuestion(_answers[index].getQuestionId());

		while (q.questionType == QuestionType.EGO) {
			l.add(_answers[index]);
			q = _study.getQuestions().getQuestion(_answers[++index].getQuestionId());
		}

		return (l);
	}

	/***************************************************************************
	 * Gets a List containing all the answers to alter questions
	 *
	 * @return List of answers using this question
	 */
	public List<Question> getAlterAnswers() {
		List<Question> l = new ArrayList<Question>();

		Collection<Question> questionList = _study.getQuestions().values();
		for (Question q : questionList) {
			if (!(q.questionType == QuestionType.ALTER))
				continue;

			l.add(q);
		}

		return (l);
	}

	/***************************************************************************
	 * Sets current question Index
	 *
	 * @param i
	 *            index of question
	 * @param force
	 *            choose the answer even if it's not valid (e.g. may be linked)
	 * @return Choosen question
	 */
	public Question setInterviewIndex(int i, boolean force) {
		Question q = null;

		if (!force) {
			i = nextValidAnswer(i, true);
		}

		if ((i >= 0) && (i < get_numAnswers())) {
			_qIndex = i;

			q = (Question) getQuestion(_qIndex).clone();
			q.setAnswer(_answers[_qIndex]);

			// replaces $$n in String with alter name indexed as alter #n
			q.text = completeText(q.text, q.getAnswer().getAlters());
		}

		return (q);
	}

	/***************************************************************************
	 * Working forward from beginning, find first unanswered question
	 *
	 * @return index of first unanswered question
	 */
	public int getFirstUnansweredQuestion() {
		int i = 0;
		int rv = getNumQuestions() - 1;

		i = nextValidAnswer(0, true);
		while (i != -1) {
			if (!_answers[i].isAnswered()) {
				rv = i;
				break;
			}

			i = nextValidAnswer(i + 1, true);
		}

		return (rv);
	}

	/***************************************************************************
	 * Working backwards from the current Answer, find the first answer which
	 * references a specified question. This is generally used for linked
	 * questions
	 *
	 * @param startIndex
	 *            starting point from which to search
	 * @param id
	 *            Unique Id of question
	 * @return answer Answer of matching question
	 */
	private Answer getPriorQuestionInstance(int startIndex, Long id) {
		Answer a = null;
		int i;

		for (i = (startIndex - 1); i >= 0; i--) {
			if (_answers[i].getQuestionId().equals(id))
				break;
		}

		if (i >= 0) {
			a = _answers[i];
		}

		return (a);
	}

	/***************************************************************************
	 * Checks question link info of current answer to see if the question should
	 * be included in interview.
	 *
	 * @param index
	 *            index of answer to check.
	 * @return bool true iff question passes link check
	 */
	private boolean checkQuestionLink(int index) {
		boolean b = false;
		Question q = getQuestion(index);

		if (q.link.isActive()) {
			//logger.info("Link active!");
			Answer a = getPriorQuestionInstance(index, q.link.getAnswer().getQuestionId());

			if (a != null) {
				//logger.info("\tLink is tied to an answer -- ");
				if (q.link.getAnswer().getValue() == Answer.ALL_ADJACENT) {
					//logger.info("\tAnswer.ALL_ADJACENT");
					if (a.adjacent) {
						//logger.info("\tif (a.adjacent) {b = true");
						b = true;
					} else {
						//logger.info("\tif (a.adjacent) {b = false");
					}
				} else {
					//logger.info("\tNOT NOT Answer.ALL_ADJACENT");
					if (a.getValue() == q.link.getAnswer().getValue()) {
						//logger.info("\tif (a.getValue() "+a.getValue()+" == q.link.answer.getValue() "+q.link.answer.getValue()+") {b = true");
						b = true;
					} else {
						//logger.info("\tif (a.getValue() "+a.getValue()+" == q.link.answer.getValue() "+q.link.answer.getValue()+") {b = false");
					}
				}
			} else {
				/*
				 * This case means one of 2 things happened, the user linked to
				 * a non-existent question or the linked question comes after
				 * this question. Neither case is proper but the less evil
				 * appears to be to always ask this question in this case
				 */
				b = true;
				//logger.info("\tNO answer tied to link! BAD BAD");
			}
		} else {
			//logger.info("Link inactive.");
			/* No link to check */
			b = true;
		}

		return (b);
	}

	/***************************************************************************
	 * Returns current question from an interview
	 *
	 * @return b true iff there are more questions
	 */
	public boolean hasNext() {
		int checkIndex = nextValidAnswer(_qIndex + 1, true);
		return (checkIndex != -1);
	}

	/***************************************************************************
	 * Returns next question from an interview which passes all link checks. Cannot be used while reading an interview, but can only be used while conducting one.
	 *
	 * @param checkIndex
	 *            answer at which to start
	 * @param forward
	 *            true iff search forward, else search backwards
	 * @return answerIndex index of next or previous valid answer, -1 if none
	 *         found
	 */
	private int nextValidAnswer(int checkIndex, boolean forward) {
		boolean b = false;

		while (!b && (checkIndex < get_numAnswers()) && (checkIndex >= 0)) {

			// validate follow up and link
			if (getQuestion(checkIndex).isFollowupOnly() && isFollowup() && checkQuestionLink(checkIndex)) {
				b = true;
			}
			// validate non-followup and link
			else if (!getQuestion(checkIndex).isFollowupOnly() && checkQuestionLink(checkIndex)) {
				b = true;
			} else {
				checkIndex = forward ? (checkIndex + 1) : (checkIndex - 1);
			}
		}

		if (!b) {
			checkIndex = -1;
		}

		return (checkIndex);
	}

	/***************************************************************************
	 * Returns current question from an interview
	 *
	 * @return i question index
	 */
	public boolean hasPrevious() {
		int checkIndex = nextValidAnswer(_qIndex - 1, false);

		return (checkIndex != -1);
	}

	/***************************************************************************
	 * Returns current question from an interview
	 *
	 * @return i question index
	 */
	public Question next() {
		Question q;

		_qIndex = nextValidAnswer(_qIndex + 1, true);

		q = (Question) getQuestion(_qIndex).clone();
		q.setAnswer(_answers[_qIndex]);
		q.text = completeText(q.text, q.getAnswer().getAlters());

		return (q);
	}

	/***************************************************************************
	 * Returns current question from an interview
	 *
	 * @return i question index
	 */
	public Question previous() {
		Question q;

		_qIndex = nextValidAnswer(_qIndex - 1, false);
		// assert (qIndex != -1);

		q = (Question) getQuestion(_qIndex).clone();
		q.setAnswer(_answers[_qIndex]);
		q.text = completeText(q.text, q.getAnswer().getAlters());

		return (q);
	}

	/***************************************************************************
	 * Is this question last alter prompt
	 *
	 * @return b true iff current question is last alter prompt
	 */
	public boolean isLastAlterPrompt() {
		boolean b = false;

		logger.info("Current question type: " + getQuestion(_qIndex).questionType);
		logger.info("hasNext: " + hasNext());

		if(_qIndex+1 >= _answers.length) // if there are no more questions, this is *definitely* the last alter prompt :)
			return true;
		logger.info("Next question type: " + getQuestion(_qIndex + 1).questionType);

		b = 		(getQuestion(_qIndex).questionType == QuestionType.ALTER_PROMPT) // current question is alter prompt
				&& 	hasNext() // there IS a next question
				&& 	(!(getQuestion(_qIndex + 1).questionType == QuestionType.ALTER_PROMPT)); // next question is alter prompt

		logger.info("-> isLastAlterPrompt = " + b);
		return (b);
	}

	/****
	 */
	public String[] getAlterStrings(Question q) {
		String[] s = new String[2];

		try {
			if ((q.getAnswer().hasAtLeastOneAlter())
					&& (q.getAnswer().firstAlter() != -1)) {
				s[0] = _alterList[q.getAnswer().firstAlter()];
			}

			if ((q.getAnswer().hasTwoAlters())
					&& (q.getAnswer().secondAlter() != -1)) {
				s[1] = _alterList[q.getAnswer().secondAlter()];
			}
		} catch (Exception ex) {
			s[0] = null;
			s[1] = null;
			logger.info("Failed to get alter strings", ex);
		}

		return s;
	}

	/**
	 * Get a question from the study using the answer's question ID
	 * @param index of an answer
	 * @return
	 */
	public Question getQuestion(int index) {
		int length = _answers.length;
		if(index > -1 && index < length) {
			Answer result = _answers[index];
			return _study.getQuestion(result.getQuestionId());
		}

		throw new RuntimeException("Requested a question at index " + index + " but there are only " + length + " questions!");
	}

	public List<Answer> getAnswersByUniqueId(long id) {
		List<Answer> list = new ArrayList<Answer>();
		for(Answer answer : _answers) {
			if(answer.getQuestionId().equals(id))
				list.add(answer);
		}

		return list;
	}

	private String completeText(String s, List<Integer> alters) {
	    int [] aa = new int[alters.size()];
	    int i = 0;
	    for(Integer alt : alters)
	        aa[i++] = alt;

	    return completeText(s, aa);
	}

	/***************************************************************************
	 * Replaces alter name placeholders with alter names
	 *
	 * @param s
	 *            Question string to parse
	 * @param alters
	 *            names of alters to put in placeholders
	 * @return modified string
	 */
	private String completeText(String s, int[] alters) {
		int parsePtr;
		String oldS = s;

		try {
			for (parsePtr = s.indexOf("$$1"); (parsePtr != -1); parsePtr = s
					.indexOf("$$1")) {
				s = s.substring(0, parsePtr) + _alterList[alters[0]]
						+ s.substring(parsePtr + 3);
			}

			for (parsePtr = s.indexOf("$$2"); (parsePtr != -1); parsePtr = s
					.indexOf("$$2")) {
				s = s.substring(0, parsePtr) + _alterList[alters[1]]
						+ s.substring(parsePtr + 3);
			}

			for (parsePtr = s.indexOf("$$-1"); (parsePtr != -1); parsePtr = s
					.indexOf("$$-1")) {
				s = s.substring(0, parsePtr) + _alterList[alters[0] - 1]
						+ s.substring(parsePtr + 4);
			}

			for (parsePtr = s.indexOf("$$"); (parsePtr != -1); parsePtr = s
					.indexOf("$$")) {
				s = s.substring(0, parsePtr) + _alterList[alters[0]]
						+ s.substring(parsePtr + 2);
			}
		} catch (Exception ex) {
			s = oldS;
			logger.error("Could not resolve dollar-dollars", ex.toString());
		}

		return s;
	}

	/***************************************************************************
	 * Returns complete attribut for interview
	 *
	 * @returns complete
	 */
	public boolean isComplete() {
		return _complete;
	}

	public void setComplete(boolean complete) {
		_complete = complete;
	}

	/***************************************************************************
	 * Ego has answered all questions. Write file and generate stats
	 *
	 * @throws IOException
	 * @throws IOException
	 */
	public void completeInterview(EgoStore storage) throws IOException {
		logger.info("Interview completion requested!");

		/***********************************************************************
		 * Generate statistics for the first statable question
		 */
		Question q = _study.getFirstStatableQuestion();
		_complete = true;

		storage.writeCurrentInterview();

		if (q != null) {
			Statistics stats = storage.getInterview().generateStatistics(q);
			if(stats == null)
				logger.error("generateStatistics produced null output. This is likely to cause problems later.");
			else if(stats.alterList == null || stats.alterList.length==0)
				logger.error("generateStatistics produced an empty alter list. This is likely to cause problems later.");
			else
				logger.trace(Arrays.asList(stats.alterList).toString());
			storage.writeStatisticsFiles(stats);
		}
		else {
			logger.info("Interview completion DID NOT generate statistics!");
		}
	}

	/***************************************************************************
	 * Add interview information to an xml structure for output to a file
	 *
	 * @param e
	 *            XML Element, parent of interview tree
	 */
	public EgoAnswer[] getEgoAnswerArray(StatRecord record) {
		Iterator egoAnswerIter = getEgoAnswers().iterator();
		EgoAnswer[] egoAnswers = new EgoAnswer[getEgoAnswers().size()];
		int index = 0;

		while (egoAnswerIter.hasNext()) {
			Answer answer = (Answer) egoAnswerIter.next();

			if (answer.isAnswered()) {
				egoAnswers[index++] = new EgoAnswer(_study
						.getQuestions().getQuestion(answer.getQuestionId()).title,
						answer.string, answer.getValue());
			} else {
				egoAnswers[index++] = new EgoAnswer(_study
						.getQuestions().getQuestion(answer.getQuestionId()).title,
						"N/A", Answer.NO_ANSWER);
			}
		}

		return egoAnswers;
	}

	/***************************************************************************
	 * How many alters were used to generate this interview
	 *
	 * @return _numAlters
	 */
	public int getNumberAlters() {
		return _alterList.length;
	}

	/***************************************************************************
	 * What study is used for this interview
	 *
	 * @return _numAlters
	 */
	public Study getStudy() {
		return (_study);
	}

	/**
	 * Returns statistics
	 *
	 * @return the statistics
	 */
	public Statistics getStats() {
		return _stats;
	}

	/**
	 * @param q
	 *            The stats to set.
	 */
	public Statistics generateStatistics(Question q) {
		_stats = Statistics.generateInterviewStatistics(this, q);
		return _stats;
	}

	/***************************************************************************
	 * Goes through Set of answers creating a matrix representing the adjacency
	 * graph for a set of alters. For each pair of alters if answer value > 0 an
	 * edge is placed between the alters
	 *
	 * @param answers
	 *            Set of alter pair answers
	 * @param numAlters
	 *            Total alters in interview
	 * @param weighted
	 *            whether to use actual answer values for matrix or 1:0
	 * @return Matrix representing non-directed graph of alters
	 * @throws MissingPairException
	 */
	public int[][] generateAdjacencyMatrix(Question q, boolean weighted) throws MissingPairException {

	    //logger.info("Adjacency matrix ("+(weighted ? "" : "non-")+"weighted) : ");
		int _numAlters = _alterList.length;

		if (_study.getUIType().equals(Shared.TRADITIONAL_QUESTIONS)) {
			int[][] m = new int[_numAlters][_numAlters];

			/*
			 * init to make sure we get all pairs, in the case of linked
			 * questions not all will be answered
			 */

			for (int i = 0; i < _numAlters; i++) {
				for (int j = 0; j < _numAlters; j++) {
					m[i][j] = 0;
				}

				m[i][i] = 1;
			}

			for (Iterator<Answer> it = getAnswerSubset(q.UniqueId).iterator(); it.hasNext();)
			{
				Answer a = it.next();

				// in a weighted one, print all values
				int weightedValue = a.getValue();

				// in unweighted, just print 0 or 1, ignore answer value
				int nonweightedValue = a.adjacent ? 1 : 0;

				int value = weighted ? weightedValue : nonweightedValue;
				//logger.info("Working on answer (adj="+a.adjacent+",v="+value+",nw="+nonweightedValue+",w="+weightedValue+") for adj: " + a.getString());

				m[a.firstAlter()][a.secondAlter()] = value;
				m[a.secondAlter()][a.firstAlter()] = value;
			}

			_matrix = m;
		}

		for(int x = 0; x < _matrix.length; x++)
		{
		    for(int y = 0; y < _matrix[x].length; y++)
		    {
		        //System.out.print(_matrix[x][y] + "\t");
		    }
		    //logger.info();
		}

		return (_matrix);
	}

	public void rewind() {
		while (hasPrevious())
			previous();
	}

	public Answer[] get_answers() {
		return _answers;
	}

	public Answer get_answerElement(int index){
		return _answers[index];
	}

	public void set_answerElement(int index, Answer value){
		_answers[index] = value;
	}

	public void set_numAnswers(int _numAnswers) {
		this._numAnswers = _numAnswers;
	}

	public int get_numAnswers() {
		return _numAnswers;
	}

	public int compareTo(Interview o) {
		return sIntName.hashCode() - o.sIntName.hashCode();
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	public String getNotes() {
		return notes;
	}

	public String toString() {
		return sIntName;
	}


        /*Returns a matrix with all the alters of every alter question prompt.*/
        public String[][] getAlterQuestionPromptAnswers(){
            return _alterQuestionPromptList;
        }

        public void setAlterQuestionPromptAnswers(String[] s, int questionIndex){
            _alterQuestionPromptList[questionIndex] = s;
        }

        public void setAlterQuestionPromptAnswers(String[][] list)
        {
            _alterQuestionPromptList = list;
        }


        /* Returns the number of the current alter question prompt. If the current
         * question is not an alter question prompt, returns -1.
         */
        public int getCurrentAlterQuestionPrompt(){

            int egoQuestions = _study.getQuestionOrder(QuestionType.EGO).size();
            int currentQuestion = getQuestionIndex()-egoQuestions;

            if (currentQuestion < 0)
            {
                return -1;
            }
            else
            {
                return currentQuestion;
            }
        }

	public String dump() {
		StringBuilder sb = new StringBuilder();

/*		private Answer[] ;
		private String[]  = new String[0]; // so alter pair is at least stateable
		*/

		sb.append("Name: " + sIntName + "\n");
		sb.append("_statisticsAvailable: " + _statisticsAvailable + "\n");
		sb.append("_complete: " + _complete + "\n");
		sb.append("_qIndex: " + _qIndex + "\n");
		sb.append("_numAlterPairs: " + _numAlterPairs + "\n");
		sb.append("followup: " + followup + "\n");
		sb.append("_numAnswers: " + _numAnswers + "\n");
		sb.append("notes: " + notes + "\n");
		sb.append("_study: " + _study + "\n");
		sb.append("_alterList: " + Arrays.toString(_alterList) + "\n");

		for(int i = 0; i < _answers.length; i++) {
			Answer a = _answers[i];

			sb.append("Answer "+i+", instance "+a.hashCode()+": " + a.getString() + "\n");
		}

		return sb.toString();
	}


        /*
         * Returns an array containing the union of the alters of all alter question prompt.
         * Alters will appear just once in this array.
         */
        public String[] getUnifiedAlterList()
        {

            ArrayList <String> tempList = new ArrayList <String>();

            for (int i = 0; i < _alterQuestionPromptList.length; i++)
            {
                for ( int j = 0; j < _alterQuestionPromptList[i].length; j++)
                {
                    if(!tempList.contains(_alterQuestionPromptList[i][j]))
                    {
                        tempList.add(_alterQuestionPromptList[i][j]);
                    }
                }
            }
            String[] unifiedList = new String[tempList.size()];
            unifiedList = tempList.toArray(unifiedList);

            return unifiedList;
        }

        /*
         * Passed an array of strings, returns an array containing the union of
         * the alters in the given array, and the global alter list.
         */
        public String[] getUnifiedAlterList(String[] s)
        {

            ArrayList <String> tempList = new ArrayList <String>();
            List <String> knownAlters = Arrays.asList(_alterList);
            tempList.addAll(knownAlters);

            for (int i = 0; i < s.length; i++)
            {
                if(!knownAlters.contains(s[i]))
                {
                    tempList.add(s[i]);
                }
            }
            String[] unknownAlterList = new String[tempList.size()];
            unknownAlterList = tempList.toArray(unknownAlterList);

            return unknownAlterList;
        }

        //Remove an alter or a collection of alters from the globl alter list.
        public void removeAlters(ArrayList <String> list)
        {
            ArrayList <String> alterList = new ArrayList<String>(Arrays.asList(_alterList));
            alterList.removeAll(list);
            String[] newAlterList = new String[alterList.size()];
            newAlterList = alterList.toArray(newAlterList);
            setAlterList(newAlterList);
        }

        //Returns a hashmap containing for every alter, how many times appears.
        public HashMap <String, Integer> getAlterHashmap()
        {
            HashMap<String, Integer> alterCounter = new HashMap <String, Integer>();
            ArrayList <String> alterList = new ArrayList<String>(Arrays.asList(_alterList));

            for(int i = 0; i < _alterQuestionPromptList.length; i++)
            {
                for(int j = 0; j <_alterQuestionPromptList[i].length; j++)
                {
                    if(alterList.contains(_alterQuestionPromptList[i][j]))
                    {
                        int counter;

                        if(alterCounter.get(_alterQuestionPromptList[i][j]) == null)
                        {
                            counter = 1;
                        }else
                        {
                            counter = alterCounter.get(_alterQuestionPromptList[i][j])+1;
                        }
                        alterCounter.put(_alterQuestionPromptList[i][j], counter);
                    }
                }
            }


            return alterCounter;
        }

        //Generates a matrix containing the relation/appearence between every alter prompt question
        //and all the alters.
        public int[][] generateAlterByAlterPromptMatrix()
        {

            int numPrompts = _study.getQuestionOrder(QuestionType.ALTER_PROMPT).size();
            int matrix [][] = new int[_alterList.length][numPrompts];

             for (int i = 0; i < _alterList.length; i++) {

                for (int j = 0; j < numPrompts; j++){

                      if (Arrays.asList(_alterQuestionPromptList[j]).contains(_alterList[i]) ){

                          matrix[i][j] = 1;

                      } else
                      {
                          matrix[i][j] = 0;
                      }
                }
            }
             return matrix;
        }

}