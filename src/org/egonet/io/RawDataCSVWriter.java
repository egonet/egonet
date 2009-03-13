package org.egonet.io;

import com.endlessloopsoftware.egonet.Shared;
import com.endlessloopsoftware.egonet.Study;
import com.endlessloopsoftware.egonet.Interview;
import com.endlessloopsoftware.egonet.Question;
import com.endlessloopsoftware.egonet.Answer;

import static com.endlessloopsoftware.egonet.Shared.QuestionType.*;

import java.util.Iterator;
import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.io.FileWriter;

import java.util.ArrayList;
import java.util.TreeMap;

import net.sf.functionalj.tuple.Pair;
import net.sf.functionalj.tuple.Triple;

import org.egonet.exceptions.CorruptedInterviewException;
import org.egonet.util.DirList;

import au.com.bytecode.opencsv.CSVWriter;

public class RawDataCSVWriter {
	
	private Study study;
	
	// obtain study with EgoClient.getStudy()
	public RawDataCSVWriter(Study study) {
		this.study = study;
	}
	
	// Writes raw data about interviews found in interview
	// directory to file outputCSV in CSV format
	public void writeFromInterviewDirectoryToFile(
			File interviewDirectory, File outputCSV)
		throws IOException
	{
		FileWriter fw = new FileWriter(outputCSV);
		writeInterviewsAsCSV(
				interviewsInDirectory(interviewDirectory),
				fw);
		fw.close();
	}
	
	private void writeInterviewsAsCSV(
			Iterable<Interview> interviews, Writer writer) 
		throws IOException
	{
		
		CSVWriter csv = new CSVWriter(writer);
		
		TreeMap<Long,Question> linkQuestions =
			new TreeMap<Long,Question>();
		TreeMap<Long,Question> egoQuestions =
			new TreeMap<Long,Question>();
		TreeMap<Long,Question> alterQuestions =
			new TreeMap<Long,Question>();
		for(Question question : study.getQuestions().values()) {
			Shared.QuestionType type = question.questionType;
			TreeMap<Long,Question> qIdToTitle;
			if(type.equals(ALTER_PAIR)) {
				qIdToTitle = linkQuestions;
			} else if(type.equals(EGO)) {
				qIdToTitle = egoQuestions;
			} else if(type.equals(ALTER)) {
				qIdToTitle = alterQuestions;
			} else {
				qIdToTitle = null;
			}
			if(qIdToTitle != null) {
				qIdToTitle.put(
						question.UniqueId,
						question);
			}
		}

		// Create two tables in CSV file: one for nodes and one for edges
		writeNodes(csv,interviews,egoQuestions,alterQuestions);
		csv.writeNext(new String[]{}); // blank line between tables
		writeEdges(csv,interviews,linkQuestions);
		
		csv.flush();
	}
	
	// Like Answer.toString, except expresses answer 
	// as number if possible
	private String answerValue(Question question, Answer answer) {
		return question.answerType.equals(Shared.AnswerType.TEXT) ? 
				answer.toString() : answer.getValue()+"";
	}
	
	// Creates a table in which each row represents an alter
	// and contains answers to each alter question. Answers to
	// ego questions for each alter's ego are also included, 
	// which means that information about any ego is repeated
	// for each of that ego's alters.
	private void writeNodes(
			CSVWriter csv, 
			Iterable<Interview> interviews,
			TreeMap<Long,Question> egoQuestions, 
			TreeMap<Long,Question> alterQuestions) 
	{
		ArrayList<String> headings = new ArrayList<String>();
		headings.add("EgoID");
		headings.add("EgoName");
		headings.add("AlterID");
		headings.add("AlterName");

		for(Question question : egoQuestions.values()) {
			headings.add(question.title);
		}
		for(Question question : alterQuestions.values()) {
			headings.add(question.title);
		}
		
		csv.writeNext(headings.toArray(new String[]{}));
		
		Integer egoID = 0;
		for(Interview interview : interviews) {
			egoID++;
			String egoName = 
				interview.getName()[0]+" "+interview.getName()[1];
			
			TreeMap<Long,String> egoQuestionToAnswer =
				new TreeMap<Long,String>();
			TreeMap<Pair<Long,Integer>,String> alterQuestionToAnswer =
				new TreeMap<Pair<Long,Integer>,String>();
			for(Answer answer : interview.get_answers()) {
				Long qId = answer.questionId;
				if(null != egoQuestions.get(answer.questionId)) { // answer to ego question
					egoQuestionToAnswer.put(
							qId, 
							answerValue(
									egoQuestions.get(qId),
									answer));
				} else if(null != alterQuestions.get(answer.questionId)) { // answer to alter question
					alterQuestionToAnswer.put(
							new Pair<Long,Integer>(
									qId,
									answer.firstAlter()), 
							answerValue(
									alterQuestions.get(qId),
									answer));
				}
			}
			
			for(Integer alterID = 0; alterID < interview.getNumAlters(); alterID++) {
				ArrayList<String> rowData = new ArrayList<String>();
				rowData.add(egoID.toString());
				rowData.add(egoName);
				rowData.add(alterID.toString());
				rowData.add(interview.getAlterList()[alterID]); // alterName

				for(Long qId : egoQuestions.keySet()) {
					rowData.add(egoQuestionToAnswer.get(qId));
				}
				for(Long qId : alterQuestions.keySet()) {
					rowData.add(alterQuestionToAnswer.get(
							new Pair<Long,Integer>(
									qId,alterID)));
				}

				csv.writeNext(rowData.toArray(new String[]{}));
			}
		}
	}
	
	// Creates a table in which each row represents a pair of alters 
	// and contains answers to each of the alter pair questions.
	private void writeEdges(
			CSVWriter csv, 
			Iterable<Interview> interviews,
			TreeMap<Long,Question> linkQuestions) 
	{
		ArrayList<String> headings = new ArrayList<String>();
		
		headings.add("EgoID");
		headings.add("EgoName");
		headings.add("AlterID1");
		headings.add("AlterName1");
		headings.add("AlterID2");
		headings.add("AlterName2");

		for(Question question : linkQuestions.values()) {
			headings.add(question.title);
		}

		csv.writeNext(headings.toArray(new String[]{}));
		
		Integer egoId = 0;
		for(Interview interview : interviews) {
			egoId++;
			String egoName = 
				interview.getName()[0]+" "+interview.getName()[1];

			TreeMap<Triple<Long,Integer,Integer>,String> linkQuestionToAnswer =
				new TreeMap<Triple<Long,Integer,Integer>,String>();
			for(Answer answer : interview.get_answers()) {
				if(null != linkQuestions.get(answer.questionId)) { // answer to link question
					Long qId = answer.questionId;
					linkQuestionToAnswer.put(
							new Triple<Long,Integer,Integer>(
									qId,
									answer.firstAlter(),
									answer.secondAlter()), 
							answerValue(
									linkQuestions.get(qId),
									answer));
				}
			}
			
			for(Integer alterId1 = 0; alterId1 < interview.getNumAlters(); alterId1++) {
				String alterName1 = interview.getAlterList()[alterId1];
				for(Integer alterId2 = alterId1 + 1; alterId2 < interview.getNumAlters(); alterId2++) {
					String alterName2 = interview.getAlterList()[alterId2];
					ArrayList<String> rowData = new ArrayList<String>();
					rowData.add(egoId.toString());
					rowData.add(egoName);
					rowData.add(alterId1.toString());
					rowData.add(alterName1);
					rowData.add(alterId2.toString());
					rowData.add(alterName2);
					
					for(Long qId : linkQuestions.keySet()) {
						rowData.add(linkQuestionToAnswer.get(
								new Triple<Long,Integer,Integer>(
										qId,alterId1,alterId2)));
					}
					
					csv.writeNext(rowData.toArray(new String[]{}));
				}
			}
		}
	}
	
	// Get this directory with:
	// File intPath = 
	//     new File(egoClient.getStorage().getPackageFile().getParent(), 
	//              "/Interviews/");
	private Iterable<Interview> interviewsInDirectory(final File interviewDirectory) {
		return new InterviewIterable(study,interviewDirectory);
	}
	
	private class InterviewIterable implements Iterable<Interview> {
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
				public boolean hasNext() {
					return i < interviewFilenames.length;
				}
				public void remove() {
					throw new UnsupportedOperationException();
				}
				public Interview next() {
					if(hasNext()) {
						String intFileName = interviewFilenames[i++];
						File intFile = new File(interviewDirectory, intFileName);
						InterviewReader intReader = new InterviewReader(study, intFile);
						try {
							return intReader.getInterview();
						} catch(CorruptedInterviewException ex) {
							throw new RuntimeException(
									"Unable to read interview file: "
										+ intFile.getName(),
									ex);
						}
					}
					throw new java.util.NoSuchElementException();
				}
			};
		}
	}
}
