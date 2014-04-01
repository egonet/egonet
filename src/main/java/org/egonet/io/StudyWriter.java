package org.egonet.io;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;

import javax.swing.JOptionPane;

import org.egonet.exceptions.EgonetException;
import org.egonet.model.QuestionList;
import org.egonet.model.Shared;
import org.egonet.model.Study;
import org.egonet.model.question.Question;
import org.egonet.model.question.StudyQuestion;
import org.egonet.util.DateUtils;

import electric.xml.Document;
import electric.xml.Element;
import electric.xml.ParseException;

public class StudyWriter {
	
	private File studyFile;
	public StudyWriter(File studyFile)
	{
		this.studyFile = studyFile;
	}
	
	public void setStudy(Study study) throws IOException {
		Document document = new Document();

	      document.setEncoding("UTF-8");
	      document.setVersion("1.0");
	      Element studyElement = document.setRoot("Package");
	      studyElement.setAttribute("Id", ""+study.getStudyId());
	      studyElement.setAttribute("InUse", study.isInUse() ? "Y" : "N");
	      studyElement.setAttribute("Creator", org.egonet.model.Shared.version);
	      studyElement.setAttribute("Updated", DateUtils.getDateString(Calendar.getInstance().getTime(), "dd/MM/yyyy hh:mm a"));
	      
	      writeStudyData(studyElement, study);
	      writeAllQuestionData(studyElement, study.getQuestions());

	      document.write(studyFile);
	}
	
	public void setStudyInUse(boolean inUse) throws IOException, EgonetException {
		Document packageDocument;
		try {
			packageDocument = new Document(studyFile);
		} catch (ParseException e) {
			throw new EgonetException(e);
		}
		Element root = packageDocument.getRoot();
		root.setAttribute("InUse", "Y");
		packageDocument.write(studyFile);
		//studyFile.setReadOnly();
	}
	
	private void writeAllQuestionData(Element document, QuestionList questionList) throws IOException
	{	
		Element element = document.addElement("QuestionList");
		for(Question q : questionList.values())
		{
			writeQuestion(element.addElement("Question"), q);
		}
	}
	
	public void writeAllQuestionData(QuestionList questionList) throws IOException
	{
		   Document document = new Document();

		      //document.addChild( new XMLDecl( "1.0", "UTF-8" ) );
		      document.setEncoding("UTF-8");
		      document.setVersion("1.0");
		      Element study = document.setRoot("QuestionFile");
		      study.setAttribute("Id", Long.toString(new Date().getTime()));

		      writeAllQuestionData(study, questionList);

		      document.write(studyFile);

	}
	
	private void writeStudyData(Element document, Study studyObject)
	{
		try
		{
			Element study = document.addElement("Study");

			study.addElement("name").setText(studyObject.getStudyName());
			study.addElement("altermodeunlimited").setBoolean(studyObject.isUnlimitedAlterMode());
                        
			study.addElement("minalters").setInt(studyObject.getMinimumNumberOfAlters());
                        
                        //Only adds max alters if study is in limited mode.
                        if(!studyObject.isUnlimitedAlterMode())
                        {
                            study.addElement("maxalters").setInt(studyObject.getMaximumNumberOfAlters());
                        }
                        
			study.addElement("altersamplingmodel").setInt(studyObject.getAlterSamplingModel().ordinal());
			study.addElement("altersamplingparameter").setInt(studyObject.getAlterSamplingParameter() == null ? 0 : studyObject.getAlterSamplingParameter());
			study.addElement("alternamemodel").setInt(studyObject.getAlterNameModel().ordinal());
			study.addElement("allowskipquestions").setBoolean(studyObject.getAllowSkipQuestions());
			
			for (Class<? extends Question> type : Shared.questionClasses)
			{
			    
			    if(type.equals(StudyQuestion.class))
			        continue;
			    
				Element qorder = new Element("questionorder");
				Iterator<Long> it = studyObject.getQuestionOrder(type).iterator();

				if (it.hasNext())
				{
					study.addElement(qorder).setAttribute("questiontype", type.getSimpleName());
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
	
	
	public void writeQuestion(Element e, Question q) {
	    
	    e.addComment(q.getString());
	    
		if (q.centralMarker) {
			e.setAttribute("CentralityMarker", "true");
		}

		e.addElement("Id").setLong(q.UniqueId.longValue());
		e.addElement("QuestionType").setString(q.getClass().getCanonicalName());
		e.addElement("AnswerType").setString(q.answerType.getCanonicalName());
		e.addElement("FollowUpOnly").setBoolean(q.followupOnly);

		if ((q.title != null) && (!q.title.equals(""))) {
			e.addElement("QuestionTitle").setText(q.title);
		}

		if ((q.text != null) && (!q.text.equals(""))) {
			e.addElement("QuestionText").setText(q.text);
		}

		if ((q.citation != null) && (!q.citation.equals(""))) {
			e.addElement("Citation").setText(q.citation);
		}

		if (q.getSelections().length > 0) {
			int size = q.getSelections().length;
			Element selections = e.addElement("Answers");

			for (int i = 0; i < size; i++) {
				Element answer = selections.addElement("AnswerText");
				answer.setText(q.getSelections()[i].getString());
				answer.setAttribute("index", Integer.toString(i));
				answer.setAttribute("value", Integer
						.toString(q.getSelections()[i].getValue()));
				answer.setAttribute("adjacent",
						q.getSelections()[i].isAdjacent() ? "true" : "false");
			}
		}

		if (q.link.isActive()) {
			Element link = e.addElement("Link");
			link.addElement("Id").setLong(q.link.getAnswer().getQuestionId());
			link.addElement("value").setInt(q.link.getAnswer().getValue());
			link.addElement("string").setText(q.link.getAnswer().string);
		}
	}
}
