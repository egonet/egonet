package org.egonet.tests.functional;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.egonet.exceptions.CorruptedInterviewException;
import org.egonet.util.Pair;

import com.endlessloopsoftware.ego.client.EgoStore;
import com.endlessloopsoftware.ego.client.EgoStore.VersionFileFilter;
import com.endlessloopsoftware.egonet.Answer;
import com.endlessloopsoftware.egonet.Interview;
import com.endlessloopsoftware.egonet.Question;
import com.endlessloopsoftware.egonet.Shared;
import com.endlessloopsoftware.egonet.Study;

import electric.xml.Document;
import electric.xml.Element;
import electric.xml.Elements;
import electric.xml.ParseException;

public class CombineInterviews
{
	// end up doing this for studies and interviews, readers and writers
	public interface InterviewReader
	{
		public Interview getInterview() throws CorruptedInterviewException;
	}
	
	public abstract class DefaultInterviewReader implements InterviewReader
	{
		protected Study study;
		public DefaultInterviewReader(Study study)
		{
			// some generic functionality related to studies when you're reading interview data
			this.study = study;
		}
	}
	
	public class InterviewFileReader extends DefaultInterviewReader
	{
		private File interviewFile;
		public InterviewFileReader(Study study, File interviewFile)
		{
			super(study);
			this.interviewFile = interviewFile;
		}	

		public Interview getInterview() throws CorruptedInterviewException {
			// TODO move all of the XML format reading here from EgoStore#readInterview and Interview#readInterview
			Interview interview = null;
			long studyId;
			
			try {
				Document document = new Document(interviewFile);
				studyId = Long.parseLong(document.getRoot().getAttribute("StudyId"));
				if (studyId != study.getStudyId()) {
					interview = null;
					throw (new CorruptedInterviewException("study ID in study doesn't match study ID in interview file"));
				}
				interview = parseInterviewFile(study, document.getRoot());				
			} catch (ParseException ex) {
				interview = null;

				throw (new CorruptedInterviewException(ex));
			}
			return interview;
		}
		
		public Interview parseInterviewFile(Study study, Element e) throws CorruptedInterviewException{
			Interview interview;
			String[] lAlterList;
			Element alterListElem = e.getElement("AlterList");
			Element answerListElem = e.getElement("AnswerList");

			try {
				/* Read alter list so we can size interview record */
				lAlterList = readAlters(alterListElem);
				interview = new Interview(study);
				interview.setAlterList(lAlterList);

				/* Read answers */
				study.readInterviewStudy(e);
				interview.setComplete(e.getBoolean("Complete"));

				/* Read interviewee name */
				Element egoNameElem = e.getElement("EgoName");

				if (egoNameElem != null) {
					interview.setName(egoNameElem.getString("First"), egoNameElem.getString("Last"));
				}
				readAnswers(study, interview, answerListElem);
			} catch (CorruptedInterviewException ex) {
				interview = null;
				throw (ex);
			} catch (Exception ex) {
				interview = null;
				throw new RuntimeException(ex);
			}

			return (interview);
		}

		private String[] readAlters(Element alterListElem) throws CorruptedInterviewException{
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
		
		private void readAnswers(Study study, Interview interview, Element e)
		throws CorruptedInterviewException {
	
			Elements answerIter = e.getElements("Answer");
			if (interview.get_numAnswers() != answerIter.size()) {
				String err = "This interview file had " + answerIter.size() + " answered questions. I was expecting " + interview.get_numAnswers()
					+ "!";
				System.err.println(err);
				throw (new CorruptedInterviewException(err));
			}
		
			int index = 0;
			while(answerIter.hasMoreElements()) {
				try {
				    Element answerElement = answerIter.next();
					Answer oldAnswer = interview.get_answerElement(index);
					Answer newAnswer = Answer.readAnswer(study, answerElement);
		
					if (oldAnswer.questionId.equals(newAnswer.questionId)) {
						interview.set_answerElement(index++, newAnswer);
					} else {
						throw (new CorruptedInterviewException());
					}
					
				} catch (Exception ex) {
					System.err.println("Answer::readAnswer failed in Interview::readAnswers; " + ex);
				}
			}
		}
	}

	public void doCombineInterviews() throws Exception
	{
		/* Read new study */
		File studyFile = EgoStore.selectStudy(null, new File("."));
		Document packageDocument = new Document(studyFile);
		Study study = new Study(packageDocument);

		//Find the interview files associated with this study
		File parentFile = studyFile.getParentFile();
		File interviewFile = new File(parentFile, "/Interviews/");

		File guessLocation = new File(".");
		if(parentFile.exists() && parentFile.isDirectory() && parentFile.canRead())
			guessLocation = parentFile;

		if(interviewFile.exists() && interviewFile.isDirectory() && interviewFile.canRead())
			guessLocation = interviewFile;

		final File currentDirectory = guessLocation;

		String[] fileList = currentDirectory.list();	
		VersionFileFilter filter = new VersionFileFilter(study.getStudyId(), "Interview Files", "int");
		ArrayList<String> alterList = new ArrayList<String>();
		Interview interview = null;
		int[][] adj = null;

		Set<Pair<String>> allPairs = new HashSet<Pair<String>>();
		Set<String> pairedAlters = new HashSet<String>();
		
		for (String s: fileList){

			File f = new File(currentDirectory.toString() + "/" + s);			
			if(!filter.accept(f) || !f.canRead())
				throw new IOException("Couldn't read file or file not associated with selected study.");

			Document document = new Document(f);
			interview = Interview.readInterview(study, document.getRoot());
			if(!interview.isComplete())
			{
				System.out.println("*** SKIPPED because interview isn't complete: " + f.getName());
				continue;
			}
			
			System.out.println("** Reading next file " + f.getName());
			
			

			String [] thisInterviewAlterlist = interview.getAlterList();
			alterList.addAll(Arrays.asList(interview.getAlterList()));

			Iterator<Long> questions = study.getQuestionOrder(Shared.QuestionType.ALTER_PAIR).iterator();
			while (questions.hasNext()) {
				Question q = study.getQuestion((Long) questions.next());
				adj = interview.generateAdjacencyMatrix(q, false);

				// loop through adj
				// if adj[i][j] == 1, thisInterviewAlters[i] && thisInterviewAlters[j] are adjacent in final matrix

				for(int i = 0; i < adj.length; i++)
				{
					for(int j = 0; j < adj[i].length; j++)
					{
						if(adj[i][j] == 1 && i != j)
						{
							
							String alter1 = thisInterviewAlterlist[i];
							String alter2 = thisInterviewAlterlist[j];
							
							Pair<String> p = new Pair<String>(alter1, alter2);
							allPairs.add(p);
							
							pairedAlters.add(alter1);
							pairedAlters.add(alter2);
							
							// mark those as adjacent in the new big matrix
							//System.out.println(p +  " are adjacent");
						}
					}
				}
			}

		}

		System.out.println("Pairs: " + allPairs);
		alterList.removeAll(pairedAlters);
		
		System.out.println("Single alters: " + alterList);
		
		if(true)
			return;
	}

	public static void main(String[] args) throws Exception
	{
		new CombineInterviews().doCombineInterviews();
	}
}
