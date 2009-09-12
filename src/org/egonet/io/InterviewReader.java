package org.egonet.io;

import java.io.File;

import org.egonet.exceptions.CorruptedInterviewException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.endlessloopsoftware.egonet.Answer;
import com.endlessloopsoftware.egonet.Interview;
import com.endlessloopsoftware.egonet.Question;
import com.endlessloopsoftware.egonet.Study;

import electric.xml.Document;
import electric.xml.Element;
import electric.xml.Elements;
import electric.xml.ParseException;

public class InterviewReader {

	final private static Logger logger = LoggerFactory.getLogger(InterviewReader.class);
	
	private Study study;
	private File interviewFile;
	
	public InterviewReader(Study study, File interviewFile)	{
		this.study = study;
		this.interviewFile = interviewFile;
	}	

	public Interview getInterview() throws CorruptedInterviewException {
		try {
			Document document = new Document(interviewFile);
			long studyId = Long.parseLong(document.getRoot().getAttribute("StudyId"));
			if (studyId != study.getStudyId())
				throw (new CorruptedInterviewException("study ID in study doesn't match study ID in interview file"));
			Interview interview = readInterview(study, document.getRoot());
			return interview;
		} catch (ParseException ex) {
			throw new CorruptedInterviewException(ex);
		}
		
	}
	
	private static Interview readInterview(Study study, Element e) throws CorruptedInterviewException{
			Element alterListElem = e.getElement("AlterList");
			Element answerListElem = e.getElement("AnswerList");
			
			/* Read alter list so we can size interview record */
			String[] lAlterList = readAlters(alterListElem);
			Interview interview = new Interview(study);
			interview.setAlterList(lAlterList);

			/* Read answers */
			
			if (e.getElement("numalters") != null)
			{
				int numAlters = e.getInt("numalters");
				if(numAlters != study.getNetworkSize())
					throw new CorruptedInterviewException("Study expected " + study.getNetworkSize() + " but interview file had " + numAlters + " alters");
				
			}
			
			interview.setComplete(e.getBoolean("Complete"));

			/* Read interviewee name */
			Element egoNameElem = e.getElement("EgoName");

			if (egoNameElem != null) {
				interview.setName(egoNameElem.getString("First"), egoNameElem.getString("Last"));
			}
			
			readAnswers(study, interview, answerListElem);

			Element notes = e.getElement("notes");
			if(notes != null) {
				interview.setNotes(notes.getString());
			}
			
			return (interview);
	}

	public static boolean checkForCompleteness(Interview interview) {
		boolean all = true;
		
		Answer [] answers = interview.get_answers();
		for(int i = 0 ; i < answers.length ; i++) {
			//logger.info("\n---------------------------------------------------------------");
			
			Answer answer = answers[i];
			//logger.info("Found answer " + answer.getString());

			// can't correctly find the linked question???
			Question question = interview.getStudy().getQuestion(answer.questionId);
			//logger.info("\tFound question by answer " + question.getString());
			
			if(!answer.answered && question.link.isActive()) { // if there's a real possibility this is linked
				Answer linkedAnswer = answers[question.link.getAnswer().getIndex()];
				if(!linkedAnswer.answered) {
					//logger.info("\t!answer.answered && question.link.isActive()");
					all = false;
				}
			} 
			else if(!answer.answered && !question.link.isActive()) {
				//logger.info("\t!answer.answered && !question.link.isActive()");
				all = false;
			}
			
			//logger.info("---------------------------------------------------------------\n");
		}
		
		logger.info("Checking interview for completeness bug - (" + all + ") "+ interview.toString());
		return all;
	}

	private static String[] readAlters(Element alterListElem) throws CorruptedInterviewException{
		Elements alterIter = alterListElem.getElements("Name");
		String[] lAlterList;
		int lNumAlters;
		int index = 0;

		lNumAlters = alterIter.size();
		lAlterList = new String[lNumAlters];

		while (alterIter.hasMoreElements()) {
			lAlterList[index++] = alterIter.next().getTextString();
		}

		return (lAlterList);
	}		
	
	private static void readAnswers(Study study, Interview interview, Element e) throws CorruptedInterviewException {

		Elements answerIter = e.getElements("Answer");
		if (interview.get_numAnswers() != answerIter.size()) {
			String err = "This interview file had " + answerIter.size() + " answered questions. I was expecting " + interview.get_numAnswers() + "!";
			throw (new CorruptedInterviewException(err));
		}
	
		for(int index = 0; answerIter.hasMoreElements(); ) {
			    Element answerElement = answerIter.next();
				Answer oldAnswer = interview.get_answerElement(index);
				Answer newAnswer = readAnswer(study, answerElement);
				
				if (oldAnswer.questionId.equals(newAnswer.questionId)) {
					interview.set_answerElement(index++, newAnswer);
				} else {
					throw (new CorruptedInterviewException("mismatch question and answer id in datafile"));
				}
		}
	}
	
    private static Answer readAnswer(Study study, Element e) {
        int qAlters[] = null;
        Long qId = new Long(e.getLong("QuestionId"));
        Question q = (Question) study.getQuestions().getQuestion(qId);
        Element alterElem = e.getElement("Alters");

        if (alterElem != null) {
            Elements alterElems = alterElem.getElements("Index");
            qAlters = new int[alterElems.size()];

            for (int i = 0; i < alterElems.size(); i++) {
                qAlters[i] = alterElems.next().getInt();
            }
        }

        Answer r = new Answer(qId, qAlters);

        r.answered = e.getBoolean("Answered");

        if (r.answered) {
            r.string = e.getString("String");
            r.setValue(e.getInt("Value"));
            r.setIndex(e.getInt("Index"));
            r.adjacent = q.selectionAdjacent(r.getValue());
        } else {
            r.string = null;
        }

        //logger.info("Read answer: " + r.getString());
        return r;
    }
}
