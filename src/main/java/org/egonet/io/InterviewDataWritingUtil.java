package org.egonet.io;


import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.TreeMap;

import net.sf.functionalj.tuple.Pair;
import net.sf.functionalj.tuple.Triple;

import org.egonet.exceptions.CorruptedInterviewException;
import org.egonet.model.Answer;
import org.egonet.model.Interview;
import org.egonet.model.Question;
import org.egonet.model.Shared.AnswerType;
import org.egonet.model.Shared.QuestionType;
import org.egonet.model.Study;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InterviewDataWritingUtil {

	public static boolean showableAsText(Question question) {
		return question.answerType.equals(AnswerType.TEXT)
			|| question.answerType.equals(AnswerType.CATEGORICAL);
	}

	public static boolean showableAsNumber(Question question) {
		return question.answerType.equals(AnswerType.NUMERICAL)
			|| question.answerType.equals(AnswerType.CATEGORICAL);
	}

	public static String showAsText(Answer answer) {
		return answer == null ? "" : answer.toString();
	}

	public static Integer showAsNumber(Answer answer) {
		return answer == null ? -1 : answer.getValue();
	}

	public static InterviewAnswers interviewAnswers(Study study, Interview interview) {
		InterviewAnswers result = new InterviewAnswers();
		StudyQuestionsByCategoryAndId studyQuestions = studyQuestionsByCategoryAndId(study);
		for(Answer answer : interview.get_answers()) {
			Long qId = answer.getQuestionId();
			if(null != studyQuestions.egoQuestions.get(answer.getQuestionId())) { // answer to ego question
				result.egoQuestionToAnswer.put(
						qId,
						answer);
			} else if(null != studyQuestions.alterQuestions.get(qId)) { // answer to alter question
				result.alterQuestionToAnswer.put(
						new Pair<Long,Integer>(
								qId,
								answer.firstAlter()),
						answer);
			} else if (null != studyQuestions.linkQuestions.get(qId)) { // answer to alter-pair question
				result.linkQuestionToAnswer.put(
						new Triple<Long,Integer,Integer>(
								qId,
								answer.firstAlter(),
								answer.secondAlter()),
						answer);
			}
		}
		return result;
	}

	public static class InterviewAnswers {
		public TreeMap<Long,Answer> egoQuestionToAnswer =
			new TreeMap<Long,Answer>();
		public TreeMap<Pair<Long,Integer>,Answer> alterQuestionToAnswer =
			new TreeMap<Pair<Long,Integer>,Answer>();
		public TreeMap<Triple<Long,Integer,Integer>,Answer> linkQuestionToAnswer =
			new TreeMap<Triple<Long,Integer,Integer>,Answer>();
	}

	public static StudyQuestionsByCategoryAndId studyQuestionsByCategoryAndId(Study study) {
		StudyQuestionsByCategoryAndId result = new StudyQuestionsByCategoryAndId();
		for(Question question : study.getQuestions().values()) {
			TreeMap<Long,Question> qIdToTitle;
			if(question.questionType == QuestionType.ALTER_PAIR) {
				qIdToTitle = result.linkQuestions;
			} else if(question.questionType == QuestionType.EGO) {
				qIdToTitle = result.egoQuestions;
			} else if(question.questionType == QuestionType.ALTER) {
				qIdToTitle = result.alterQuestions;
			} else {
				qIdToTitle = null;
			}
			if(qIdToTitle != null) {
				qIdToTitle.put(
						question.UniqueId,
						question);
			}
		}
		return result;
	}

	public static class StudyQuestionsByCategoryAndId {
		public TreeMap<Long,Question> linkQuestions =
			new TreeMap<Long,Question>();
		public TreeMap<Long,Question> egoQuestions =
			new TreeMap<Long,Question>();
		public TreeMap<Long,Question> alterQuestions =
			new TreeMap<Long,Question>();
	}

	// Get this directory with:
	// File intPath =
	//     new File(egoClient.getStorage().getPackageFile().getParent(),
	//              "/Interviews/");
	public static Iterable<Interview> interviewsInDirectory(Study study, File interviewDirectory) {
		return new InterviewIterable(study,interviewDirectory);
	}

	private static class InterviewIterable implements Iterable<Interview> {

		final private static Logger logger = LoggerFactory.getLogger(InterviewIterable.class);

		private final Study study;
		final File interviewDirectory;

		public InterviewIterable(
				final Study study,
				final File interviewDirectory)
		{
			this.study = study;
			this.interviewDirectory = interviewDirectory;
		}

		public Iterator<Interview> iterator() {
			List<Interview> foundInterviews = new ArrayList<Interview>();

			final String[] interviewFilenames =  interviewDirectory.list();

			for(int i = 0; i < interviewFilenames.length; i++) {
				String intFileName = interviewFilenames[i];
				File intFile = new File(interviewDirectory, intFileName);
				InterviewReader intReader = new InterviewReader(study, intFile);
				try {
					Interview interview = intReader.getInterview();
					foundInterviews.add(interview);
				} catch(CorruptedInterviewException ex) {
					String err = "Unable to read interview file, skipping in iterator";
					if(intFile != null)
						err += " "+ intFile.getName();

					if(ex.getMessage() != null)
					 err += ": " + ex.getMessage();
					logger.info(err, ex);
				}
			}

			return foundInterviews.iterator();
		}
	}
}
