package org.egonet.io;


import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.egonet.exceptions.DuplicateQuestionException;
import org.egonet.exceptions.EgonetException;
import org.egonet.exceptions.MalformedQuestionException;
import org.egonet.model.Answer;
import org.egonet.model.Question;
import org.egonet.model.Selection;
import org.egonet.model.Study;
import org.egonet.model.Shared.AlterNameModel;
import org.egonet.model.Shared.AlterSamplingModel;
import org.egonet.model.Shared.AnswerType;
import org.egonet.model.Shared.QuestionType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import electric.xml.Document;
import electric.xml.Element;
import electric.xml.Elements;
import electric.xml.ParseException;

public class StudyReader {

	final private static Logger logger = LoggerFactory.getLogger(StudyReader.class);

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

		Element documentRoot = document.getRoot();
		try {
			study.setStudyId(documentRoot.getAttributeValue("Id"));
			logger.info("Loaded study ID " + study.getStudyId());
		}
		catch (NumberFormatException nfe) {
			throw new EgonetException("Unable to parse the unique ID for this study", nfe);
		}

		Element studyRoot = documentRoot.getElement("Study");
		if(studyRoot == null) {
			throw new EgonetException("Couldn't find a study element in this file, may not be a study file.");
		}

		if (studyRoot.getElement("name") != null) {
			study.setStudyName(studyRoot.getTextString("name"));
			logger.info("Loaded study name " + study.getStudyName());
		}

        //Retrocompatibility for old versions of Egonet. Some studies had
        //alternumberfixed meaning limited mode, and numalters as the min/max alters.
        //If alternumberfixed were false meant that there weren't max number of alters.
        if(studyRoot.getElement("alternumberfixed") != null)
        {
            boolean limitedMode = studyRoot.getBoolean("alternumberfixed");
            study.setUnlimitedMode(!limitedMode);
            logger.info("Loaded study unlimited mode " + study.isUnlimitedAlterMode());

            if(studyRoot.getElement("numalters") != null) {
                if(limitedMode) {
                    study.setMaximumNumberOfAlters(studyRoot.getInt("numalters"));
                }
                study.setMinimumNumberOfAlters(studyRoot.getInt("numalters"));
            }
        }

		// if either new XML alters element is missing, default back to numalters
		if(studyRoot.getElement("minalters") == null || studyRoot.getElement("maxalters") == null) {
			if (studyRoot.getElement("numalters") != null) {
				int i = studyRoot.getInt("numalters");
				study.setMinimumNumberOfAlters(i);
				study.setMaximumNumberOfAlters(i);
			}
		}


        if (studyRoot.getElement("altermodeunlimited") != null) {
                boolean b = studyRoot.getBoolean("altermodeunlimited");
                study.setUnlimitedMode(b);
        }

		if (studyRoot.getElement("minalters") != null) {
			int i = studyRoot.getInt("minalters");
			study.setMinimumNumberOfAlters(i);
		}

		if (studyRoot.getElement("maxalters") != null) {
			int i = studyRoot.getInt("maxalters");
			study.setMaximumNumberOfAlters(i);
		}
		logger.info("Loaded study min/max: " + study.getMaximumNumberOfAlters() + "/" + study.getMinimumNumberOfAlters());

		if(studyRoot.getElement("altersamplingmodel") != null) {
			study.setAlterSamplingModel(AlterSamplingModel.values()[studyRoot.getInt("altersamplingmodel")]);
		}

		if(studyRoot.getElement("alternamemodel") != null) {
			int mod = studyRoot.getInt("alternamemodel");
			study.setAlterNameModel(AlterNameModel.values()[mod]);
			//logger.info(mod + " : " + study.getAlterNameModel());
		}

		if(studyRoot.getElement("altersamplingparameter") != null) {
			study.setAlterSamplingParameter(studyRoot.getInt("altersamplingparameter"));
		}

		if(studyRoot.getElement("allowskipquestions") != null) {
			//System.out.println("FOUND ALLOW SKIPS");
			study.setAllowSkipQuestions(studyRoot.getBoolean("allowskipquestions"));
		}

		Elements elements = studyRoot.getElements("questionorder");
		long orderct = 0;
		while (elements.hasMoreElements()) {
			Element element = elements.next();
			String questionType = element.getAttribute("questiontype");
			QuestionType qType = questionTypeFromString(questionType);
			List<Long> questionOrder = study.getQuestionOrder(qType);

			Elements ids = element.getElements("id");
			while (ids.hasMoreElements()) {
				questionOrder.add(new Long(ids.next().getLong()));
				orderct++;
			}
		}
		logger.info("Loaded question order instruction count " + orderct);

		List<Question> questions = getQuestions(documentRoot);
		for(Question q : questions) {
			study.addQuestion(q);
		}

		logger.info("Loaded question count: " + questions.size());

		return study;
	}

	private static List<Question> getQuestions(Element root) throws MalformedQuestionException, DuplicateQuestionException, EgonetException
	{
		if(root == null) {
			throw new IllegalArgumentException("Cannot parse XML document with null root element");
		}

		Element questionListElement = root.getElement("QuestionList");
		if(questionListElement == null) {
			throw new IllegalArgumentException("Cannot find QuestionList element");
		}

		Elements questions = questionListElement.getElements("Question");
		if(questions == null || !questions.hasMoreElements()) {
			throw new IllegalArgumentException("Cannot find Question elements in QuestionList");
		}

		List<Question> questionList = new ArrayList<Question>();
		while (questions.hasMoreElements()) {
			Question q = readQuestion(questions.next());
			questionList.add(q);
			logger.info("Loaded question " + q.getString());
		}

		return questionList;
	}

	/**
	 * Import questions from a file (not used for reading real studies)
	 */
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

	@SuppressWarnings({"deprecation"})
	public static Question readQuestion(Element question) throws MalformedQuestionException
	{
		Question q = new Question();

		String questionType = question.getString("QuestionType");
		q.questionType = questionTypeFromString(questionType);

		String answerType = question.getString("AnswerType");
		q.answerType = answerTypeFromString(answerType);

		// force alter prompt to be a text answer?
		if (q.questionType == QuestionType.ALTER_PROMPT) {
			q.answerType = AnswerType.TEXT;
		}

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

		if(question.hasElement("FollowUpOnly")) {
			boolean foo = question.getBoolean("FollowUpOnly");
			q.followupOnly = foo;
		}

		q.UniqueId = new Long(question.getLong("Id"));

		if (question.getAttribute("CentralityMarker") != null) {
			boolean centrality = question.getAttribute("CentralityMarker").equals("true");

			if (centrality && (!(q.questionType == QuestionType.ALTER_PAIR))) {
				//logger.info("ID:" + q.UniqueId + " title:"+ q.title);
				throw (new MalformedQuestionException("Centrality marker on non-alter pair question"));
			}
		}

		Element link = question.getElement("Link");
		if (link != null) {
			Answer answer = new Answer();
			answer.setQuestionId(link.getLong("Id"));
			q.link.setAnswer(answer);
			q.link.getAnswer().setValue(link.getInt("value"));

			/* Only support questions with single answers for link */
			q.link.getAnswer().string = link.getTextString("string");
		}


		if (q.answerType.equals(AnswerType.CATEGORICAL)) {
			Element answerList = question.getElement("Answers");

			if (answerList != null) {
				Elements selections = answerList.getElements("AnswerText");

				if (selections.size() == 0) {
					throw (new MalformedQuestionException("zero selections, impossible for a categorical question!"));
				}

				/*
				 * temp vars for determining statable, a question must have at
				 * least one of each selection type to be statable
				 */
				boolean adjacent = false;
				boolean nonadjacent = false;

				q.setSelections(new ArrayList<Selection>(selections.size()));

				while (selections.hasMoreElements()) {

					Element selection = selections.next();
					int index = Integer.parseInt(selection.getAttributeValue("index"));

					Selection ptr = new Selection();
					try {

						ptr.setString(selection.getTextString());
						ptr.setValue(Integer.parseInt(selection.getAttributeValue("value")));

						ptr.setAdjacent(Boolean.valueOf(selection.getAttributeValue("adjacent")).booleanValue());
						ptr.setIndex(index);
					} catch (NumberFormatException ex) {
						//logger.info("Throwing exception");
						ptr.setValue(selections.size() - (index + 1));
						ptr.setAdjacent(false);
					}

					if (ptr.isAdjacent())
						adjacent = true;
					else
						nonadjacent = true;

					q.getSelections().add(index, ptr);
				}

				/*
				 * a question must have at least one of each selection type to
				 * be statable
				 */
				q.setStatable(adjacent && nonadjacent);
				if(!q.isStatable()) {
					// THIS MAY BE OK IF IT ISN'T AN ALTER PAIR QUESTION
					String str = "Study readQuestion found that there wasn't a valid adjacency map, marking alter pair question NON statable; q: " + q.getString();
					logger.error(str);
					//throw new RuntimeException(str);
				}
				else {
					logger.info("Successfully determined that this study is statable");
				}

				/* Check to make sure all answers are contiguous */
				for (int i = 0; i < selections.size(); i++) {
					if (q.getSelections().get(i) == null) {
						throw (new MalformedQuestionException("saved a null as a selection"));
					}
				}
			}
		}

		return q;
	}


	public static QuestionType questionTypeFromString(String s) {
		for(QuestionType t : QuestionType.values()) {
			if(s.equals(t.ordinal()+"")) {
				return t;
			}
			else if(t.name().equals(s)) {
				return t;
			}
		}
		throw new MalformedQuestionException("Question's QuestionType "+s+" did not contain canonical name or integer");
	}

	public static AnswerType answerTypeFromString(String s) {
		for(AnswerType t : AnswerType.values()) {
			if(s.equals(t.ordinal()+"")) {
				return t;
			}
			else if(t.name().equals(s)) {
				return t;
			}
		}
		throw new MalformedQuestionException("Question's AnswerType "+s+" did not contain canonical name or integer");
	}
}
