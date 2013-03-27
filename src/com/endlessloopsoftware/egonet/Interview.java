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
import org.egonet.util.ELSMath;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.endlessloopsoftware.ego.client.statistics.StatRecord;
import com.endlessloopsoftware.ego.client.statistics.Statistics;
import com.endlessloopsoftware.ego.client.statistics.StatRecord.EgoAnswer;

public class Interview implements Comparable<Interview> {

	final private static Logger logger = LoggerFactory.getLogger(Interview.class);
	
	//private final Answer[] _answers;
        private final ArrayList<Answer> _answers;

	private final Study _study;

	private int[][] _matrix;

	private Statistics _stats = null;

	private String[] _egoName = { "", "" };

	private String notes = "";
	
	private boolean _complete;

	private String[] _alterList = new String[0];
        private String[][] _alterListbyPrompt;
	
        private int _qIndex = 0;

	private final int _numAlterPairs = 0;

	private int _numAnswers;

	private int _numAlters;
	
	private boolean followup = false;
        
        private boolean alterQuestionsGenerated = false;

	public boolean isFollowup() {
		return followup;
	}

	public void setFollowup(boolean followup) {
		this.followup = followup;
	}
	
	public boolean _statisticsAvailable = false;

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
	public Interview(Study study) throws CorruptedInterviewException {
		/* Locals */
		//int j, k;
		Iterator questions;
                /* Initializes the alterListByPrompt at number of quesions prompts in the study.*/
                _alterListbyPrompt = new String[study.getQuestionOrder(Shared.QuestionType.ALTER_PROMPT).size()][];
		/* Calculate some interview values */
		_study = study;
		/*_numAlters = study.getNumAlters();
		 _numAlterPairs = ELSMath.summation(_numAlters - 1);
		 set_numAnswers(study.getQuestionOrder(Shared.QuestionType.EGO)
				 .size()
				 + study.getQuestionOrder(Shared.QuestionType.ALTER_PROMPT)
				 		.size()
				 + (_numAlters * study.getQuestionOrder(
				 		Shared.QuestionType.ALTER).size())
				 + (_numAlterPairs * study.getQuestionOrder(
				 		Shared.QuestionType.ALTER_PAIR).size()));*/
		_answers = new ArrayList<>(); 

		/* Generate answer instances */
                /*Since number of alters is
                 unlimited, at constructor we can't know how many Alter questions and
                 Alter Pair questions will have the interview.*/
		_qIndex = 0;

		/* Ego Questions */
		questions = study.getQuestionOrder(Shared.QuestionType.EGO)
				.iterator();
               
		while (questions.hasNext()) {
			Long questionId = (Long) questions.next();
			Question question = _study.getQuestions().getQuestion(questionId);

			if (question == null) {
				throw new CorruptedInterviewException();
			} else {
                                _answers.add(new Answer(question.UniqueId));
 
				//_answers[_qIndex++] = new Answer(question.UniqueId);
			}
		}
                 
		/* Alter Prompt Questions. */
		questions = study.getQuestionOrder(Shared.QuestionType.ALTER_PROMPT)
				.iterator();
		while (questions.hasNext()) {
			Long questionId = (Long) questions.next();
			Question question = _study.getQuestions().getQuestion(questionId);

			if (question == null) {
				throw new CorruptedInterviewException();
			} else {
         
                            _answers.add(new Answer(question.UniqueId));	 
                            //_answers[_qIndex++] = new Answer(question.UniqueId); 
                                
			}
		}
  
		/* Checking Adjacency Selections for Alter Pair questions... */ 
                questions = study.getQuestionOrder(
                Shared.QuestionType.ALTER_PAIR).iterator(); 
                while (questions.hasNext()) {
                        Question question = _study.getQuestions().getQuestion(
                                        (Long) questions.next());

                        if (question == null) {
                                throw new CorruptedInterviewException();
                        } else {
                                if (question.isStatable()) {
                                        _statisticsAvailable = true;
                                } 
                        }
                }
 
                _numAnswers = _answers.size(); 
		 
	}
        
        /* **************************************************************************** 
         * Called when the Ego has entered all alters into the alter list generators.
         * At this point, we'll know how many Alter Pair questions will have the interview. 
         */
        public void generateAlterPairQuestions( ) throws CorruptedInterviewException
        {
          
           for (int k = 0; k < getNumAlters(); k++) {
                for (int j = (k + 1); j < getNumAlters(); j++) {
     
                    Iterator questions = getStudy().getQuestionOrder(
                                    Shared.QuestionType.ALTER_PAIR).iterator();
                    int[] alters = { k, j };
                    while (questions.hasNext()) { 
                            Question question = _study.getQuestions().getQuestion(
                                            (Long) questions.next());

                            if (question == null) {
                                throw new CorruptedInterviewException();
                            } else {  
                               
                                    _answers.add( new Answer(question.UniqueId, alters)); 
                            }  
                    }
                }
            } 
           
            _numAnswers = _answers.size();
           
        }
        
        /* **************************************************************************** 
         * Called when the Ego has entered all alters into the alter list generators
         * At this point, we'll know how many Alter questions will have the interview. 
         */
        public void generateAlterQuestions( ) throws CorruptedInterviewException  
        {
 
           
            for (int j = 0; j < getNumAlters(); j++) {
                       
                        Iterator questions = getStudy().getQuestionOrder(
					Shared.QuestionType.ALTER).iterator();
			int[] alter = { j };
			while (questions.hasNext()) {
                          
				Long questionId = (Long) questions.next();
				Question question = _study.getQuestions().getQuestion(
						questionId);
				if (question == null) {
					throw new CorruptedInterviewException();
				} else { 
                              
                                        _answers.add(new Answer(question.UniqueId, alter)); 
				}
                 
			} 
            }
         
            _numAnswers = _answers.size();
            this.alterQuestionsGenerated = true;
        }
         /*****************************************************************************
          * Delete/Clear the previous questions generated by Alter prompt questions. 
          * ***************************************************************************/
        public void clearAlterQuestions(){
           
            int num_questions  = getStudy().getQuestionOrder(Shared.QuestionType.EGO).size() 
                                +getStudy().getQuestionOrder(Shared.QuestionType.ALTER_PROMPT) .size(); 
            _answers.subList(num_questions, _answers.size()).clear() ; 
        }    
         
        /******************************************************************************
         * Set and get the Alter list structured by prompt questions. 
         ******************************************************************************/
        public void setAlterListByIndexPrompt(int i, String[] alters ){
            this._alterListbyPrompt[i]= alters;  
        }
        
        public String[] getAlterListByIndexPrompt(int i ){
            try{
                return this._alterListbyPrompt[i];
            }catch (Exception e){
                return null;
            }
        }
        public void setAlterListByPrompt(String [][] alterList){
            this._alterListbyPrompt = alterList;
        }
        
        public String[][] getAlterListByPrompt(){
            return this._alterListbyPrompt;
        }
        
        /*************************************************************************** 
        * Method that returns an unified Alter List. It says, given a alter list by 
        * prompt, returns an unique dimension array with alter list. In alter list by 
        * prompt, its possible to be the same alter in different question prompts. In  
        * the unified list, this alter only will be once. On this way, there is no need
        * to refactor structures in other parts of application. 
        ****************************************************************************/
        public String[] getUnifiedAlterList(){
            
            ArrayList <String> tempUnifiedList = new ArrayList <String>();
            
            for(int i=0; i< getAlterListByPrompt().length;i++){
                if(getAlterListByPrompt()[i] != null){
                    for(int j= 0; j<getAlterListByPrompt()[i].length;j++){
                    
                        if (!tempUnifiedList.contains(getAlterListByPrompt()[i][j]))
                        {
                            tempUnifiedList.add(getAlterListByPrompt()[i][j]);
                        }
                    }  
                }
                
            }
            
            String [] unifiedList = tempUnifiedList.toArray(new String[tempUnifiedList.size()]);
            return unifiedList ;
        }
        
        
        
	/***************************************************************************
	 * Called when user shutting down program
	 */
	public void exit() throws Exception {
		if (!_complete) {
				
		}
	}

	/***************************************************************************
	 * Searches question list for all questions and places them in list
	 * 
	 * @param dlm
	 *            list model to use in inserting questions
	 */
	public void fillList(DefaultListModel<String> dlm) {
		dlm.removeAllElements();

		for (int i = 0; i < get_numAnswers(); i++) {
			// Question q = getQuestion(i);

			Question q = _study.getQuestions().getQuestion(_answers.get(i).questionId);

			if (q.questionType == Shared.QuestionType.ALTER_PAIR
					&& (_study.getUIType().equals(Shared.PAIR_ELICITATION) || _study
							.getUIType().equals(Shared.THREE_STEP_ELICITATION))) {
				/* Skip Alter Pair Questions for Interactive Linking Studies */
			} else {
				String s = q.toString();

				if (q.questionType == Shared.QuestionType.ALTER) {
					s = s + "; " + _answers.get(i).firstAlter();
				} else if (q.questionType == Shared.QuestionType.ALTER_PAIR) {
					s = s + "; "
							+ _answers.get(i).firstAlter() + " & "
							+ _answers.get(i).secondAlter();
				}

				s = q.questionType.niceName + ": " + s;

				dlm.addElement(s);
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
	 * Returns current list of all alters
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
	 */
	public void setAlterList(String[] s) {
		_alterList = s;
                _numAlters = getNumAlters(); 
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

		for (int i = 0; i < _answers.size(); i++) {
			if (_answers.get(i).questionId.equals(qId)) {
				s.add(_answers.get(i));
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
		Question q = _study.getQuestions().getQuestion(_answers.get(index).questionId);

		while (q.questionType == Shared.QuestionType.EGO) {
			l.add(_answers.get(index));
			q = _study.getQuestions().getQuestion(_answers.get(++index).questionId);
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
			if (q.questionType != Shared.QuestionType.ALTER)
				continue;

			l.add(q);
		}

		return (l);
	}

	/***************************************************************************
	 * Gets name of interview subject
	 * 
	 * @return String Array of first and last name
	 */
	public String[] getName() {
		String[] s = { _egoName[0], _egoName[1] };

		return s;
	}

	/***************************************************************************
	 * Sets name of interviewee
	 * 
	 * @param first
	 *            first name
	 * @param last
	 *            last name
	 */
	public void setName(String first, String last) {
		_egoName[0] = first;
		_egoName[1] = last;
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
			q.setAnswer(_answers.get(_qIndex));

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
                                       
			if (!_answers.get(i).isAnswered()) {
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
			if (_answers.get(i).questionId.equals(id))
				break;
		}

		if (i >= 0) {
			a = _answers.get(i);
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
			Answer a = getPriorQuestionInstance(index, q.link.getAnswer().questionId);

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
		q.setAnswer(_answers.get(_qIndex));
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
		q.setAnswer(_answers.get(_qIndex));
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
		
		b = (getQuestion(_qIndex).questionType == Shared.QuestionType.ALTER_PROMPT)
				&& hasNext()
				&& (getQuestion(_qIndex + 1).questionType != Shared.QuestionType.ALTER_PROMPT);

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
			s[0] = "";
			s[1] = "";
		}

		return s;
	}

	/**
	 * Get a question from the study using the answer's question ID
	 * @param index of an answer
	 * @return
	 */
	public Question getQuestion(int index) {
		return _study.getQuestion(_answers.get(index).questionId);
	}
	
	public List<Answer> getAnswersByUniqueId(long id) {
		List<Answer> list = new ArrayList<Answer>();
		for(Answer answer : _answers) {
			if(answer.questionId == id)
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
			storage.writeStatisticsFiles(stats, _egoName);
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
						.getQuestions().getQuestion(answer.questionId).title,
						answer.string, answer.getValue());
			} else {
				egoAnswers[index++] = new EgoAnswer(_study
						.getQuestions().getQuestion(answer.questionId).title,
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
	public int getNumAlters() {
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
	    
		if (_study.getUIType().equals(Shared.TRADITIONAL_QUESTIONS)) {
			int[][] m = new int[getNumAlters()][getNumAlters()];

			/*
			 * init to make sure we get all pairs, in the case of linked
			 * questions not all will be answered
			 */

			for (int i = 0; i < getNumAlters(); i++) {
				for (int j = 0; j < getNumAlters(); j++) {
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

        public int[][] generateAlterMatrixByPrompt(){
            
            String[] unifiedList = getUnifiedAlterList();
            int numPrompts = _study.getQuestionOrder(Shared.QuestionType.ALTER_PROMPT).size();
            int matrix [][] = new int[unifiedList.length][numPrompts];
             
             for (int i = 0; i < unifiedList.length; i++) {
               
                for (int j = 0; j < numPrompts; j++){
                       
                      if (Arrays.asList(getAlterListByIndexPrompt(j)).contains(unifiedList[i]) ){
                          
                          matrix[i][j] = 1;   
                          
                      } else
                      {
                          matrix[i][j] = 0; 
                      }
                }
            }
             return matrix;
        }
        
	public void rewind() {
		while (hasPrevious())
			previous();
	}

	public ArrayList <Answer> get_answers() {
		return _answers;
	}
	
	public Answer get_answerElement(int index){
		return _answers.get(index);
	}
	
	public void set_answerElement(int index, Answer value){
		//_answers[index] = value;
                
            try{
                _answers.set(index, value);
            } catch(Exception e){
                _answers.add(value);
            }
             
	}

	public void set_numAnswers(int _numAnswers) {
		this._numAnswers = _numAnswers;
	}

	public int get_numAnswers() {
		return _numAnswers;
	}

	public int compareTo(Interview o) {
		return _egoName.hashCode() - o._egoName.hashCode();
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	public String getNotes() {
		return notes;
	}
	
	public String toString() {
		String result = "";
		for(String namePart : getName()) {
			if(! result.isEmpty()) {
				result += " ";
			}
			result += namePart;
		}
		return result;
	}
        
        
        /*************************************************
         * Return the current prompt question. If current question is not
         * a prompt question, returns -1.
         * ************************************************/
        public int getIndexPromptQuestion(){
            
            int index = getQuestionIndex() -
                    _study.getQuestionOrder(Shared.QuestionType.EGO).size();
            
            if(index < 0 || index >= _study.getQuestionOrder(Shared.QuestionType.ALTER_PROMPT).size())
            {
                index = -1;
            }
            
            return index;
        }
        
        /************************************************
         * Returns a boolean specifing if alter and alter pair 
         * questions have been already generated.
         * ********************************************/
        public boolean isAlterQuestionsGenerated(){
            return alterQuestionsGenerated;
        }
}