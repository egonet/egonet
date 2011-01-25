package org.egonet.io;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.ArrayList;

import net.sf.functionalj.tuple.Pair;
import net.sf.functionalj.tuple.Triple;

import org.egonet.io.InterviewDataWritingUtil.StudyQuestionsByCategoryAndId;

import com.endlessloopsoftware.egonet.Answer;
import com.endlessloopsoftware.egonet.Interview;
import com.endlessloopsoftware.egonet.Question;
import com.endlessloopsoftware.egonet.Study;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;

public class VnaInterviewWriter {
	
	// See com.endlessloopsoftware.ego.client.statistics.Statistics.writeAlterArray
	// and org.egonet.io.RawDataWriter
	// and http://netwiki.amath.unc.edu/DataFormats/NetDrawVna
	
	private Study study;
	private Interview interview;
	
	public VnaInterviewWriter(Study study, Interview interview) {
		this.study = study;
		this.interview = interview;
	}
	
	public void write(File file) throws IOException {
		FileWriter fw = new FileWriter(file);
		write(fw);
		fw.close();
	}
	
	public void write(Writer writer) {
		
		StudyQuestionsByCategoryAndId questions = 
			InterviewDataWritingUtil.studyQuestionsByCategoryAndId(study);
		InterviewDataWritingUtil.InterviewAnswers answers = 
			InterviewDataWritingUtil.interviewAnswers(study, interview);

		PrintWriter pw = new PrintWriter(writer);

		pw.println("*Node data");
		ArrayList<String> alterhead = Lists.newArrayList();
		ArrayList<Long> alterQIds = Lists.newArrayList(questions.alterQuestions.keySet());
		alterhead.add(sanitize("ID"));
		for(Long qid : alterQIds) {
			Question question = questions.alterQuestions.get(qid);
			if(InterviewDataWritingUtil.showableAsText(question)) {
				alterhead.add(sanitize(question.title+" text"));
			}
			if(InterviewDataWritingUtil.showableAsNumber(question)) {
				alterhead.add(sanitize(question.title+" value"));
			}
		}
		pw.println(Joiner.on(", ").join(alterhead));
		
		String[] alterList = interview.getAlterList();
		Integer numAlters = alterList.length;
		
		for(Integer alterID = 0; alterID < numAlters; alterID++) {
			ArrayList<String> rowData = Lists.newArrayList();
			rowData.add(sanitize(alterList[alterID]));
			for(Long qid : alterQIds) {
				Question question = questions.alterQuestions.get(qid);
				Answer answer = 
					answers.alterQuestionToAnswer.get(
							new Pair<Long,Integer>(
									qid,alterID));
				if(InterviewDataWritingUtil.showableAsText(question)) {
					rowData.add(sanitize(InterviewDataWritingUtil.showAsText(answer)));
				}
				if(InterviewDataWritingUtil.showableAsNumber(question)) {
					rowData.add(sanitize(InterviewDataWritingUtil.showAsNumber(answer)+""));
				}
			}
			pw.println(" "+Joiner.on(" ").join(rowData));
		}
		
		pw.println("*Tie data");
		ArrayList<String> aphead = Lists.newArrayList();
		ArrayList<Long> apQIds = Lists.newArrayList(questions.linkQuestions.keySet());
		aphead.add("FROM TO");
		for(Long qid : apQIds) {
			Question question = questions.linkQuestions.get(qid);
			if(InterviewDataWritingUtil.showableAsNumber(question)) {
				aphead.add(sanitize(question.title));
			}
		}
		pw.println(Joiner.on(" ").join(aphead));
		
		for(Integer alterID1 = 0; alterID1 < numAlters; alterID1++) {
			for(Integer alterID2 = alterID1+1; alterID2 < numAlters; alterID2++) {
				ArrayList<String> rowData = Lists.newArrayList();
				rowData.add(sanitize(alterList[alterID1]));
				rowData.add(sanitize(alterList[alterID2]));
				for(Long qid : apQIds) {
					Question question = questions.linkQuestions.get(qid);
					Answer answer =
						answers.linkQuestionToAnswer.get(
								new Triple<Long,Integer,Integer>(
										qid,alterID1,alterID2));
					if(InterviewDataWritingUtil.showableAsNumber(question)) {
						Integer number = InterviewDataWritingUtil.showAsNumber(answer);
						rowData.add((number == null ? 0 : number)+"");
					}
				}
				pw.println(Joiner.on(" ").join(rowData));
			}
		}
		
		pw.flush();
	}
	
	private String sanitize(String string) {
		if(string == null) {
			return "_";
		}
		return "\""+string.replaceAll("[^a-zA-Z_\\-0-9 ]","_")+"\"";
	}
}
