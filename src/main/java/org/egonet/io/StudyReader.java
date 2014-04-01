package org.egonet.io;


import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.egonet.exceptions.DuplicateQuestionException;
import org.egonet.exceptions.EgonetException;
import org.egonet.exceptions.MalformedQuestionException;
import org.egonet.model.Study;
import org.egonet.model.Shared.AlterNameModel;
import org.egonet.model.Shared.AlterSamplingModel;
import org.egonet.model.answer.*;
import org.egonet.model.question.*;
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
		
		Element root = document.getRoot();
		try {
			study.setStudyId(root.getAttributeValue("Id"));
		}
		catch (NumberFormatException nfe) {
			throw new EgonetException("Unable to parse the unique ID for this study", nfe);
		}
	
		root = root.getElement("Study");
		if(root == null) {
			throw new EgonetException("Couldn't find a study element in this file, may not be a study file.");
		}
	
		if (root.getElement("name") != null) {
			study.setStudyName(root.getTextString("name"));
		}
	
                //Retrocompatibility for old versions of Egonet. Some studies had 
                //alternumberfixed meaning limited mode, and numalters as the min/max alters.
                //If alternumberfixed were false meant that there weren't max number of alters.
                if(root.getElement("alternumberfixed") != null)
                {
                    boolean limitedMode = root.getBoolean("alternumberfixed");
                   
                    study.setUnlimitedMode(!limitedMode);                    
                    
                    if(root.getElement("numalters") != null)
                    {
                        if(limitedMode)
                        {
                          
                            study.setMaximumNumberOfAlters(root.getInt("numalters"));
                        }
                        
                        study.setMinimumNumberOfAlters(root.getInt("numalters"));
                    }
                }
                
                
                
		// if either new XML alters element is missing, default back to numalters
		if(root.getElement("minalters") == null || root.getElement("maxalters") == null) {
			if (root.getElement("numalters") != null) {
				int i = root.getInt("numalters");
				study.setMinimumNumberOfAlters(i);
				study.setMaximumNumberOfAlters(i);
			}
		}
		
                if (root.getElement("altermodeunlimited") != null)
                {
                        boolean b = root.getBoolean("altermodeunlimited");
                        study.setUnlimitedMode(b); 
                }
                               
		if (root.getElement("minalters") != null) {
			int i = root.getInt("minalters");
			study.setMinimumNumberOfAlters(i);
		}
		
		if (root.getElement("maxalters") != null) {
			int i = root.getInt("maxalters");
			study.setMaximumNumberOfAlters(i);
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
			//System.out.println("FOUND ALLOW SKIPS");
			study.setAllowSkipQuestions(root.getBoolean("allowskipquestions"));
		}
		
		Elements elements = root.getElements("questionorder");
		while (elements.hasMoreElements()) {
			Element element = elements.next();
			String questionType = element.getAttribute("questiontype");
			Class<? extends Question> qType = Question.asSubclass(questionType);
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

	@Deprecated
	private enum QuestionType {

		STUDY_CONFIG(StudyQuestion.class.getCanonicalName()),
		EGO(EgoQuestion.class.getCanonicalName()),
		ALTER_PROMPT(AlterPromptQuestion.class.getCanonicalName()),
		ALTER(AlterQuestion.class.getCanonicalName()),
		ALTER_PAIR(AlterPairQuestion.class.getCanonicalName())
		;
	    public final String className;
	    QuestionType(String className) {
	        this.className = className;
	    }
	}
	
	@Deprecated
	private enum AnswerType {

		CATEGORICAL(CategoricalAnswer.class.getCanonicalName()),
	    NUMERICAL(NumericalAnswer.class.getCanonicalName()),
	    TEXT(TextAnswer.class.getCanonicalName()),
	    INFORMATIONAL(InformationalAnswer.class.getCanonicalName()),
		
		;
	    public final String className;
	    AnswerType(String className) {
	        this.className = className;
	    }
	}
	
	
	@SuppressWarnings({"deprecation"})
	public static Question readQuestion(Element question) throws MalformedQuestionException
	{
		Question q;
		
		String questionType = question.getString("QuestionType");
		if(!questionType.toLowerCase().contains(".class".toLowerCase())) {
			int intQuestiontype = -1; 
			try {
				intQuestiontype = Integer.parseInt(questionType);
			}
			catch (Exception ex) {
				throw new MalformedQuestionException("QuestionType did not contain canonical name, but I couldn't parse an integer");
			}
			
			String clazz = null;
			QuestionType [] types = { 
					QuestionType.STUDY_CONFIG,
					QuestionType.EGO,
					QuestionType.ALTER_PROMPT,
					QuestionType.ALTER,
					QuestionType.ALTER_PAIR
			};
			
			for(QuestionType t : types) {
				if(t.ordinal() == intQuestiontype) {
					clazz = t.className;
				}
			}
			if(clazz == null) {
				throw new MalformedQuestionException("QuestionType did not contain canonical name or integer");
			}
			questionType = clazz;
		}
		q = Question.newInstance(questionType);	
		
		String answerType = question.getString("AnswerType");
		if(!answerType.toLowerCase().contains(".class".toLowerCase())) {
			int intAnswerType = -1; 
			try {
				intAnswerType = Integer.parseInt(answerType);
			}
			catch (Exception ex) {
				throw new MalformedQuestionException("Question's AnswerType did not contain canonical name, but I couldn't parse an integer");
			}
			
			String clazz = null;
			AnswerType [] types = {
					AnswerType.CATEGORICAL,
					AnswerType.NUMERICAL,
					AnswerType.TEXT,
					AnswerType.INFORMATIONAL
			};
			
			for(AnswerType t : types) {
				if(t.ordinal() == intAnswerType) {
					clazz = t.className;
				}
			}
			if(clazz == null) {
				throw new MalformedQuestionException("Question's AnswerType did not contain canonical name or integer");
			}
			answerType = clazz;
		}
		
		q.answerType = Answer.asSubclass(answerType);
		// force alter prompt to be a text answer?
		if (q instanceof AlterPromptQuestion) {
			q.answerType = TextAnswer.class;
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

			if (centrality && (!(q instanceof AlterPairQuestion))) {
				//logger.info("ID:" + q.UniqueId + " title:"+ q.title);
				throw (new MalformedQuestionException("Centrality marker on non-alter pair question"));
			}
		}

		Element link = question.getElement("Link");
		if (link != null) {
			Answer answer = Answer.newInstance(q.answerType);
			answer.setQuestionId(link.getLong("Id"));
			q.link.setAnswer(answer);
			q.link.getAnswer().setValue(link.getInt("value"));

			/* Only support questions with single answers for link */
			q.link.getAnswer().string = link.getTextString("string");
		}
		

		if (q.answerType.equals(CategoricalAnswer.class)) {
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

				q.setSelections(new Selection[selections.size()]);

				while (selections.hasMoreElements()) {

					Element selection = selections.next();
					int index = Integer.parseInt(selection.getAttributeValue("index"));

					try {
						q.getSelections()[index] = new Selection();
						q.getSelections()[index].setString(selection.getTextString());
						q.getSelections()[index]
								.setValue(Integer.parseInt(selection.getAttributeValue("value")));

						q.getSelections()[index].setAdjacent(Boolean.valueOf(selection.getAttributeValue("adjacent")).booleanValue());
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
					if (q.getSelections()[i] == null) {
						throw (new MalformedQuestionException());
					}
				}
			}
		}
		
		return q;
	}


}
