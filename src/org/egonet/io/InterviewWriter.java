package org.egonet.io;

import java.io.File;
import java.io.IOException;

import com.endlessloopsoftware.egonet.Answer;
import com.endlessloopsoftware.egonet.Interview;
import com.endlessloopsoftware.egonet.Question;
import com.endlessloopsoftware.egonet.Study;

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

			interviewDocument.setAttribute("StudyId", Long.toString(study.getStudyId()));
			interviewDocument.setAttribute("StudyName", study.getStudyName());
			interviewDocument.setAttribute("NumAlters", Integer.toString(study.getNetworkSize()));
			interviewDocument.setAttribute("Creator", com.endlessloopsoftware.egonet.Shared.version);

			writeInterview(interviewDocument, interview);
			document.write(interviewFile);
		}
	}
	
	public void writeInterviewStudy(Element e)
	{
		e.setInt("numalters", study.getNetworkSize());
	}
	
	private void writeInterview(Element e, Interview interview) {
		Element alterListElem = e.addElement("AlterList");
		Element answerListElem = e.addElement("AnswerList");
		Element egoNameElem = e.addElement("EgoName");

		writeInterviewStudy(e);
		e.addElement("Complete").setBoolean(interview.isComplete());
		e.addElement("FollowUpProtocol").setBoolean(interview.isFollowup());
		
		String[] _egoName = interview.getName();
		
		egoNameElem.addElement("First").setString(_egoName[0]);
		egoNameElem.addElement("Last").setString(_egoName[1]);

		
		String[] _alterList = interview.getAlterList();
		for (int i = 0; i < _alterList.length; i++) {
			alterListElem.addElement("Name").setText(_alterList[i]);
		}
		
		Answer[] _answers = interview.get_answers();
		for (int i = 0; i < _answers.length; i++) {
			writeAnswer(answerListElem, interview.getQuestion(i), interview, _answers[i]);
		}
		
		e.addElement("notes").setString(interview.getNotes());
	}
	
	public void writeAnswer(Element e, Question question, Interview interview, Answer answer) {
        Element answerElement = new Element("Answer");

        
        answerElement.addComment("Answer element: " + answer.getString());
        if(question != null)
            answerElement.addComment("Corresp question: " + question.getString());
        
        
        answerElement.addElement("QuestionId").setLong(answer.questionId.longValue());
        answerElement.addElement("Answered").setBoolean(answer.isAnswered());

        if (answer.isAnswered()) {
            if(answer.questionId.equals(1205185478364L)) System.err.println("Printed a value into the XML file that was zero: " + answer.getString());
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
