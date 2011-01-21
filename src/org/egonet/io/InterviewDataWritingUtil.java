package org.egonet.io;

import static com.endlessloopsoftware.egonet.Shared.QuestionType.ALTER;
import static com.endlessloopsoftware.egonet.Shared.QuestionType.ALTER_PAIR;
import static com.endlessloopsoftware.egonet.Shared.QuestionType.EGO;

import java.io.File;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import net.sf.functionalj.tuple.Pair;
import net.sf.functionalj.tuple.Triple;

import org.egonet.exceptions.CorruptedInterviewException;
import org.egonet.util.DirList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.endlessloopsoftware.egonet.Answer;
import com.endlessloopsoftware.egonet.Interview;
import com.endlessloopsoftware.egonet.Question;
import com.endlessloopsoftware.egonet.Shared;
import com.endlessloopsoftware.egonet.Study;
import com.google.common.collect.Maps;

public class InterviewDataWritingUtil {

	final private static Logger logger = LoggerFactory.getLogger(InterviewDataWritingUtil.class);
	
	public static boolean showableAsText(Question question) {
		return question.answerType.equals(Shared.AnswerType.TEXT)
			|| question.answerType.equals(Shared.AnswerType.CATEGORICAL);
	}
	
	public static boolean showableAsNumber(Question question) {
		return question.answerType.equals(Shared.AnswerType.NUMERICAL)
			|| question.answerType.equals(Shared.AnswerType.CATEGORICAL);
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
			Long qId = answer.questionId;
			if(null != studyQuestions.egoQuestions.get(answer.questionId)) { // answer to ego question
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
			Shared.QuestionType type = question.questionType;
			TreeMap<Long,Question> qIdToTitle;
			if(type.equals(ALTER_PAIR)) {
				qIdToTitle = result.linkQuestions;
			} else if(type.equals(EGO)) {
				qIdToTitle = result.egoQuestions;
			} else if(type.equals(ALTER)) {
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

			final String[] interviewFilenames = 
				DirList.getDirList(interviewDirectory, "int");
			return new Iterator<Interview>() {
				private int i=0;
				private Map<String,String> egonameToFilename;
				public boolean hasNext() {
					return i < interviewFilenames.length;
				}
				public void remove() {
					throw new UnsupportedOperationException();
				}
				public Interview next() {
					if(hasNext()) {
						if(egonameToFilename == null) {
							egonameToFilename = Maps.newTreeMap();
						}
						String intFileName = interviewFilenames[i++];
						File intFile = new File(interviewDirectory, intFileName);
						InterviewReader intReader = new InterviewReader(study, intFile);
						try {
							Interview interview = intReader.getInterview();
							String egoname = interview.getName()[0]+" "+interview.getName()[1];
							if(egonameToFilename.get(egoname) != null) {
								logger.warn("Two interview files have the same ego name: name="+
										egoname+" file1="+egonameToFilename.get(egoname)+" file2="+
										intFileName);
							}
							egonameToFilename.put(egoname, intFileName);
							return interview;
						} catch(CorruptedInterviewException ex) {
							String err = "Unable to read interview file "+ intFile.getName();
							if(ex.getMessage() != null)
								err += ": " + ex.getMessage();

							throw new RuntimeException(err,ex);
						}
					}
					throw new java.util.NoSuchElementException();
				}
			};
		}
	}
}
