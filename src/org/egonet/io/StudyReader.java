package org.egonet.io;

import com.endlessloopsoftware.egonet.Shared.AlterNameModel;
import com.endlessloopsoftware.egonet.Shared.AlterSamplingModel;
import com.endlessloopsoftware.egonet.Shared.AnswerType;
import com.endlessloopsoftware.egonet.Shared.QuestionType;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.egonet.exceptions.DuplicateQuestionException;
import org.egonet.exceptions.EgonetException;
import org.egonet.exceptions.MalformedQuestionException;
import org.egonet.util.listbuilder.Selection;

import com.endlessloopsoftware.egonet.Answer;
import com.endlessloopsoftware.egonet.Question;
import com.endlessloopsoftware.egonet.Shared;
import com.endlessloopsoftware.egonet.Study;

import electric.xml.Document;
import electric.xml.Element;
import electric.xml.Elements;
import electric.xml.ParseException;

public class StudyReader {

	private File studyFile;
	public StudyReader(File studyFile)
	{
		this.studyFile = studyFile;
	}
	
	
	public Study getStudy() throws EgonetException {
		try {
			Study readStudy = readPackageStudy(new Document(studyFile));
			readStudy.verifyStudy();
			
			return readStudy;
			
		} catch (ParseException ex) {
			throw new EgonetException("Could not read study file", ex);
		}
	}
	
	private Study readPackageStudy(Document document) throws MalformedQuestionException, DuplicateQuestionException, EgonetException
	{
		Study study = new Study();
		
		Element root = document.getRoot();
		study.setStudyId(Long.parseLong(root.getAttributeValue("Id")));
	
		root = root.getElement("Study");
	
		if (root.getElement("name") != null) {
			study.setStudyName(root.getTextString("name"));
		}
	
		if (root.getElement("numalters") != null) {
			study.setNetworkSize(root.getInt("numalters"));
		}
	
		if(root.getElement("altersamplingmodel") != null) {
			study.setAlterSamplingModel(AlterSamplingModel.values()[root.getInt("altersamplingmodel")]);
		}
		
		if(root.getElement("alternamemodel") != null) {
			int mod = root.getInt("alternamemodel");
			study.setAlterNameModel(AlterNameModel.values()[mod]);
			//logger.info(mod + " : " + study.getAlterNameModel());
		}
	
		if(root.getElement("altersamplingparameter") != null) {
			study.setAlterSamplingParameter(root.getInt("altersamplingparameter"));
		}

		if(root.getElement("allowskipquestions") != null) {
			System.out.println("FOUND ALLOW SKIPS");
			study.setAllowSkipQuestions(root.getBoolean("allowskipquestions"));
		}
		
		Elements elements = root.getElements("questionorder");
		while (elements.hasMoreElements())
		{
			Element element = elements.next();
			int qOrderId = Integer.parseInt(element.getAttribute("questiontype"));
			QuestionType qType = QuestionType.values()[qOrderId];
			List<Long> questionOrder = study.getQuestionOrder(qType);
	
			Elements ids = element.getElements("id");
			while (ids.hasMoreElements())
			{
				questionOrder.add(new Long(ids.next().getLong()));
			}
		}
		
		List<Question> questions = getQuestions();
		for(Question q : questions)
			study.addQuestion(q);
		
		return study;
	}
	
	private static List<Question> getQuestions(Element root) throws MalformedQuestionException, DuplicateQuestionException, EgonetException
	{
		root = root.getElement("QuestionList");
		Elements questions = root.getElements("Question");
	
		List<Question> questionList = new ArrayList<Question>();
		while (questions.hasMoreElements())
		{
			Question q = readQuestion(questions.next());
			questionList.add(q);
		}
		
		return questionList;
	}
	
	public static List<Question> getQuestions(File questionFile) throws MalformedQuestionException, DuplicateQuestionException, EgonetException
	{
		try {
			Document document = new Document(questionFile);
			 Element root = document.getRoot();
			 return getQuestions(root);
		} catch (ParseException e) {
			throw new EgonetException(e);
		}
	}

	public List<Question> getQuestions() throws MalformedQuestionException, DuplicateQuestionException, EgonetException
	{
		Document document;
		try {
			document = new Document(studyFile);
		} catch (ParseException e) {
			throw new EgonetException(e);
		}
		Element root = document.getRoot();
		return getQuestions(root);
	}


	public boolean isStudyInUse() throws EgonetException {
		Document document;
		try {
			document = new Document(studyFile);
		} catch (ParseException e) {
			throw new EgonetException(e);
		}
        Element root = document.getRoot();
        String inUse = root.getAttribute("InUse");

        return ((inUse != null) && inUse.equals("Y"));
    }
	
	public static Question readQuestion(Element question) throws MalformedQuestionException
	{
		Question q = new Question();
		
		if(question.getElement("QuestionTitle") == null) {
			q.title = "";
		} else if(question.getElement("QuestionText") == null) {
			q.text = "";
		} 
		
		q.title = question.getTextString("QuestionTitle");
		q.title = (q.title == null) ? "" : q.title;

		q.text = question.getTextString("QuestionText");
		q.text = (q.text == null) ? "" : q.text;

		q.citation = question.getTextString("Citation");
		q.citation = (q.citation == null) ? "" : q.citation;

		q.UniqueId = new Long(question.getLong("Id"));
		q.questionType = QuestionType.values()[question.getInt("QuestionType")];
		q.answerType = AnswerType.values()[question.getInt("AnswerType")];

		if (q.questionType == QuestionType.ALTER_PROMPT) {
			q.answerType = Shared.AnswerType.TEXT;
		}

		if (question.getAttribute("CentralityMarker") != null) {
			boolean centrality = question.getAttribute("CentralityMarker")
					.equals("true");

			if (centrality
					&& (q.questionType != Shared.QuestionType.ALTER_PAIR)) {
				//logger.info("ID:" + q.UniqueId + " title:"+ q.title);
				throw (new MalformedQuestionException());
			}
		}

		Element link = question.getElement("Link");
		if (link != null) {
			q.link.setAnswer(new Answer(new Long(link.getLong("Id"))));
			q.link.getAnswer().setValue(link.getInt("value"));

			/* Only support questions with single answers for link */
			q.link.getAnswer().string = link.getTextString("string");
		}

		if (q.answerType == Shared.AnswerType.CATEGORICAL) {
			Element answerList = question.getElement("Answers");

			if (answerList != null) {
				Elements selections = answerList.getElements("AnswerText");

				if (selections.size() == 0) {
					throw (new MalformedQuestionException());
				}

				/*
				 * temp vars for determining statable, a question must have at
				 * least one of each selection type to be statable
				 */
				boolean adjacent = false;
				boolean nonadjacent = false;

				q.setSelections(new Selection[selections.size()]);

				while (selections.hasMoreElements()) {

					Element selection = selections.next();
					int index = Integer.parseInt(selection
							.getAttributeValue("index"));

					try {
						q.getSelections()[index] = new Selection();
						q.getSelections()[index].setString(selection
								.getTextString());
						q.getSelections()[index]
								.setValue(Integer.parseInt(selection
										.getAttributeValue("value")));

						q.getSelections()[index].setAdjacent(Boolean.valueOf(
								selection.getAttributeValue("adjacent"))
								.booleanValue());
						q.getSelections()[index].setIndex(index);

					} catch (NumberFormatException ex) {
						//logger.info("Throwing exception");
						q.getSelections()[index].setValue(selections.size()
								- (index + 1));
						q.getSelections()[index].setAdjacent(false);
					}

					if (q.getSelections()[index].isAdjacent())
						adjacent = true;
					else
						nonadjacent = true;
				}

				/*
				 * a question must have at least one of each selection type to
				 * be statable
				 */
				q.statable = adjacent && nonadjacent;

				/* Check to make sure all answers are contiguous */
				for (int i = 0; i < selections.size(); i++) {
					if (q.getSelections()[i] == null) {
						throw (new MalformedQuestionException());
					}
				}
			}
		}
		
		return q;
	}


}
