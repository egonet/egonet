package org.egonet.io;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.TreeMap;

import net.sf.functionalj.tuple.Pair;
import net.sf.functionalj.tuple.Triple;

import org.egonet.io.InterviewDataWritingUtil.StudyQuestionsByCategoryAndId;
import org.egonet.model.Interview;
import org.egonet.model.Study;
import org.egonet.model.answer.*;
import org.egonet.model.question.Question;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.com.bytecode.opencsv.CSVWriter;

public class RawDataCSVWriter {
	
	final protected static Logger logger = LoggerFactory.getLogger(RawDataCSVWriter.class);
	private Study study;
	
            // obtain study with EgoClient.getStudy()
	public RawDataCSVWriter(Study study) {
		this.study = study;
	}
	
	// Writes raw data about interviews found in interview
	// directory to file outputCSV in CSV format
	public void writeFromInterviewDirectoryToFile(File interviewDirectory, File outputCSV) throws IOException {
		FileWriter fw = new FileWriter(outputCSV);
		writeInterviewsAsCSV(
				InterviewDataWritingUtil.interviewsInDirectory(study,interviewDirectory),
				fw);
		fw.close();
	}
	
	private void writeInterviewsAsCSV(
			Iterable<Interview> interviews, Writer writer) 
		throws IOException
	{
		
		CSVWriter csv = new CSVWriter(writer);
		
		StudyQuestionsByCategoryAndId questions = 
			InterviewDataWritingUtil.studyQuestionsByCategoryAndId(study);

		// Create two tables in CSV file: one for nodes and one for edges
		writeNodes(csv,interviews,questions.egoQuestions,questions.alterQuestions);
		csv.writeNext(new String[]{}); // blank line between tables
		writeEdges(csv,interviews,questions.linkQuestions);
		
		csv.flush();
	}
	
	// Like Answer.toString, except expresses answer 
	// as number if possible
	private String answerValue(Question question, Answer answer) {
		return InterviewDataWritingUtil.showableAsNumber(question) ?
				InterviewDataWritingUtil.showAsNumber(answer)+"" : 
				InterviewDataWritingUtil.showAsText(answer);
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
			if(interview.isComplete()) {
				egoID++;
				String egoName = interview.getIntName();
			
				InterviewDataWritingUtil.InterviewAnswers answers = InterviewDataWritingUtil.interviewAnswers(study, interview);

				String[] alterList = interview.getAlterList();
				Integer numAlters = alterList.length;
				for(Integer alterID = 0; alterID < numAlters; alterID++) {
					ArrayList<String> rowData = new ArrayList<String>();
					rowData.add(egoID.toString());
					rowData.add(egoName);
					rowData.add(alterID.toString());
					rowData.add(alterList[alterID]); // alterName

					for(Long qId : egoQuestions.keySet()) {
						rowData.add(
								answerValue(egoQuestions.get(qId),
										answers.egoQuestionToAnswer.get(qId)));
					}
					for(Long qId : alterQuestions.keySet()) {
						rowData.add(
								answerValue(alterQuestions.get(qId),
										answers.alterQuestionToAnswer.get(
												new Pair<Long,Integer>(
														qId,alterID))));
					}

					csv.writeNext(rowData.toArray(new String[]{}));
				}
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
			if(interview.isComplete()) {
				egoId++;
				String egoName = interview.getIntName();
				
				InterviewDataWritingUtil.InterviewAnswers answers = 
					InterviewDataWritingUtil.interviewAnswers(study, interview);
			
				String[] alterList = interview.getAlterList();
				Integer numAlters = alterList.length;
				for(Integer alterId1 = 0; alterId1 < numAlters; alterId1++) {
					String alterName1 = interview.getAlterList()[alterId1];
					for(Integer alterId2 = alterId1 + 1; alterId2 < numAlters; alterId2++) {
						String alterName2 = alterList[alterId2];
						ArrayList<String> rowData = new ArrayList<String>();
						rowData.add(egoId.toString());
						rowData.add(egoName);
						rowData.add(alterId1.toString());
						rowData.add(alterName1);
						rowData.add(alterId2.toString());
						rowData.add(alterName2);
					
						for(Long qId : linkQuestions.keySet()) {
							rowData.add(
									answerValue(
											linkQuestions.get(qId),
											answers.linkQuestionToAnswer.get(
													new Triple<Long,Integer,Integer>(
															qId,alterId1,alterId2))));
						}
					
						csv.writeNext(rowData.toArray(new String[]{}));
					}
				}
			}
		}
	}
	
}
