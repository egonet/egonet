package org.egonet.io;

import java.io.File;
import java.io.IOException;

import org.egonet.model.Interview;
import org.egonet.model.Study;
import org.egonet.model.answer.*;
import org.egonet.model.question.Question;

import electric.xml.Document;
import electric.xml.Element;

public class InterviewWriter {

	private Study study;
	private File interviewFile;
	
	public InterviewWriter(Study study, File interviewFile)
	{
		this.study = study;
		this.interviewFile = interviewFile;
	}	

	
	public void setInterview(Interview interview) throws IOException {
		Document document = new Document();

		if (interviewFile != null) {
			
			document.setEncoding("UTF-8");
			document.setVersion("1.0");
			Element interviewDocument = document.setRoot("Interview");

			interviewDocument.setAttribute("StudyId", study.getStudyId());
			interviewDocument.setAttribute("StudyName", study.getStudyName());
			
			interviewDocument.setAttribute("Creator", org.egonet.model.Shared.version);

			Element alterListElem = interviewDocument.addElement("AlterList");
			Element answerListElem = interviewDocument.addElement("AnswerList");

			interviewDocument.addElement("Complete").setBoolean(interview.isComplete());
			interviewDocument.addElement("FollowUpProtocol").setBoolean(interview.isFollowup());
			
			String[][] _alterLists = interview.getAlterQuestionPromptAnswers();
                        
			for (int i = 0; i < _alterLists.length; i++) 
                        {
				Element questionPrompt = alterListElem.addElement("QuestionPrompt");
                                
                                for(int j = 0; j < _alterLists[i].length; j++)
                                {
                                    questionPrompt.addElement("Name").setText(_alterLists[i][j]);
                                }
			}
			
			Answer[] _answers = interview.get_answers();
			for (int i = 0; i < _answers.length; i++) {
				writeAnswer(answerListElem, interview.getQuestion(i), interview, _answers[i]);
			}
			
			interviewDocument.addElement("notes").setString(interview.getNotes());

			
			document.write(interviewFile);
		}
	}
	
	public void writeAnswer(Element e, Question question, Interview interview, Answer answer) {
        Element answerElement = new Element("Answer");

        
        answerElement.addComment("Answer element: " + answer.getString());
        if(question != null)
            answerElement.addComment("Corresp question: " + question.getString());
        
        
        answerElement.addElement("QuestionId").setLong(answer.getQuestionId().longValue());
        answerElement.addElement("Answered").setBoolean(answer.isAnswered());

        if (answer.isAnswered()) {
            if(answer.getQuestionId().equals(1205185478364L)) System.err.println("Printed a value into the XML file that was zero: " + answer.getString());
            answerElement.addElement("Value").setInt(answer.getValue());
            answerElement.addElement("Index").setInt(answer.getIndex());
            answerElement.addElement("Adjacent").setBoolean(answer.adjacent);
            answerElement.addElement("String").setText(answer.string);
            answerElement.addElement("TimeStamp").setText(answer.timestamp);
        }

        if (answer.getAlters().size() > 0) {
            String[] alterList = interview.getAlterList();
            Element altersElement = answerElement.addElement("Alters");
            for (int i = 0; i < answer.getAlters().size(); i++) {
                int alterNumber = answer.getAlters().get(i);
                
                // alter may not have a name yet
                String alterName = alterList.length > alterNumber ? alterList[alterNumber] : "Undefined alter name (#"+alterNumber+")";
                
                Element thisAlterElement = altersElement.addElement("Index");
                thisAlterElement.setInt(alterNumber);
                
                // handy extra attribute
                thisAlterElement.setAttribute("name", alterName);
            }
        }

        e.addElement(answerElement);
    }
}
