package org.egonet.io;

import java.io.File;
import java.util.Arrays;

import org.egonet.exceptions.CorruptedInterviewException;
import org.egonet.exceptions.StudyIdMismatchException;
import org.egonet.model.Interview;
import org.egonet.model.Study;
import org.egonet.model.answer.*;
import org.egonet.model.question.AlterPromptQuestion;
import org.egonet.model.question.Question;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
	
	public static File getNewInterviewPath(File studyPath, String [] name) {
		if(studyPath.isFile()) // someone passed us the study file
			throw new IllegalArgumentException("studyPath should be a path, not a file");
		
		int i = 0;
		
		boolean foundUniqueFile = false;
		File ptr = null;
		
		while(!foundUniqueFile) {
			i++;
			ptr = new File(studyPath, name[0].toLowerCase() + "_" + name[1].toLowerCase() + ".int");
			if(!ptr.exists()) {
				foundUniqueFile = true;
			}
			ptr = new File(studyPath, name[0].toLowerCase() + "_" + name[1].toLowerCase() + "-"+new java.util.Date().getTime()+".int");
			if(!ptr.exists()) {
				foundUniqueFile = true;
			}
			
			if(i > 20) {
				throw new RuntimeException("Couldn't get a new, unique filename for new interview with " + Arrays.asList(name));
			}
		}
		
		
		return ptr;
	}
	
	public Interview getInterview() throws CorruptedInterviewException {
		return getInterview(false);
	}
	
	public Interview getInterview(boolean ignoreStudyId) throws CorruptedInterviewException {
		String studyId = "";
		try {
			Document document = new Document(interviewFile);
			studyId = document.getRoot().getAttribute("StudyId");
			 
			if (!studyId.equals(study.getStudyId())) {
				if(ignoreStudyId)
					document.getRoot().setAttribute(study.getStudyId(), studyId);
				else
					throw (new StudyIdMismatchException("study ID in study doesn't match study ID in interview file"));
			}
			Interview interview = readInterview(study, document.getRoot(), interviewFile.getName().replace(".int", ""));
			logger.info("Completely parsed interview with study ID " + studyId + " and interview " + interview);
			return interview;
		} catch (NumberFormatException ex) {
			throw new CorruptedInterviewException("Trouble parsing the study ID in this file: id=[" + studyId+"]",ex);
		} catch (ParseException ex) {
			throw new CorruptedInterviewException(ex);
		}
		
	}
	
	private static Interview readInterview(Study study, Element e, String name) throws CorruptedInterviewException{
			Element alterListElem = e.getElement("AlterList");
			Element answerListElem = e.getElement("AnswerList");
			
                        /* Read the multiple alter list */
			String[][] multipleAlterList = readAlters(alterListElem, study);
			Interview interview = new Interview(study, name);
                       
                        interview.setAlterQuestionPromptAnswers(multipleAlterList);
                        
                        /* Get a list with no alter repetitions (repetitions 
                           come from the apperance of alters in diferent questions)*/
                        String[] alterList = interview.getUnifiedAlterList();
                        interview.setAlterList(alterList);
                        
			/* Read answers */
			if((alterList.length < study.getMinimumNumberOfAlters() || alterList.length > study.getMaximumNumberOfAlters())
                            && !study.isUnlimitedAlterMode())
				logger.warn("Study expected between " + study.getMinimumNumberOfAlters() + " and " +study.getMaximumNumberOfAlters() + " but interview file had " + alterList.length + " alters");
			
			interview.setComplete(e.getBoolean("Complete"));
			try {
				interview.setFollowup(e.getBoolean("FollowUpProtocol"));
			} catch (Exception ex) {
				// no followup was found, set false
				interview.setFollowup(false);
			}
			
			// TODO: Read new 'original alters' field
			
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
			Question question = interview.getStudy().getQuestion(answer.getQuestionId());
			//logger.info("\tFound question by answer " + question.getString());
			
			if(!answer.isAnswered() && question.link.isActive()) { // if there's a real possibility this is linked
				Answer linkedAnswer = answers[question.link.getAnswer().getIndex()];
				if(!linkedAnswer.isAnswered()) {
					//logger.info("\t!answer.answered && question.link.isActive()");
					all = false;
				}
			} 
			else if(!answer.isAnswered() && !question.link.isActive()) {
				//logger.info("\t!answer.answered && !question.link.isActive()");
				all = false;
			}
			
			//logger.info("---------------------------------------------------------------\n");
		}
		
		logger.info("Checking interview for completeness bug - (" + all + ") "+ interview.toString());
		return all;
	}

	private static String[][] readAlters(Element alterListElem, Study study) throws CorruptedInterviewException{
            
                Elements alterPromptIter = alterListElem.getElements("QuestionPrompt");    
                String[][] lAlterList = null;
                
                //New Egonet interviews, with multiple prompt questions
                if(alterPromptIter.size() != 0)
                {
                    int lNumPrompt;
                    int questionIndex = 0;
                    int alterIndex = 0;

                    lNumPrompt = study.getQuestionOrder(AlterPromptQuestion.class).size();
                    
                    lAlterList = new String[lNumPrompt][];
                    
                    while (alterPromptIter.hasMoreElements()) {

                            Elements alterNames = alterPromptIter.next().getElements("Name");
                            int sizePromptQuestion = alterNames.size();
                            lAlterList[questionIndex] = new String[sizePromptQuestion];
                            alterIndex = 0;

                            while(alterNames.hasMoreElements()){
                                lAlterList[questionIndex][alterIndex] = alterNames.next().getTextString(); 
                                alterIndex++;
                            }  
                            questionIndex++;
                    }
                }
                //Old Egonet inteviews format, with only one alter question prompt.
                else
                {
                    
                    Elements alterNames = alterListElem.getElements("Name");
                    lAlterList = new String[1][alterNames.size()];
                    int index = 0;
                    
                    while(alterNames.hasMoreElements()){
                        lAlterList[0][index] = alterNames.next().getTextString();
                        index++;
                    }
                    
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
				
				if (oldAnswer.getQuestionId().equals(newAnswer.getQuestionId())) {
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
        
        Answer r = Answer.newInstance(q.answerType);
        r.setQuestionId(qId); r.setAlters(qAlters);

        r.setAnswered(e.getBoolean("Answered"));

        if (r.isAnswered()) {
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
