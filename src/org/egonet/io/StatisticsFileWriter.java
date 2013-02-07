package org.egonet.io;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import com.endlessloopsoftware.ego.client.statistics.Statistics;
import com.endlessloopsoftware.egonet.Answer;
import com.endlessloopsoftware.egonet.Interview;
import com.endlessloopsoftware.egonet.Study;

import electric.xml.Document;
import electric.xml.Element;

public class StatisticsFileWriter {

	private Study study;
	private Interview interview;
	private File statisticsFile;
	
	
	
	public StatisticsFileWriter(Study study, Interview interview, File statisticsFile) {
		super();
		this.study = study;
		this.interview = interview;
		this.statisticsFile = statisticsFile;
	}



	public void writeStatisticsFile(Statistics stats) throws IOException {
		Document document = new Document();

		document.setEncoding("UTF-8");
		document.setVersion("1.0");
		Element studyElement = document.setRoot("Statistics");

		studyElement.setAttribute("StudyId", Long.toString(study.getStudyId()));
		studyElement.setAttribute("Creator", com.endlessloopsoftware.egonet.Shared.version);

		writeStructuralStatistics(studyElement, interview, stats);
		writeEgoAnswers(studyElement, interview);
		writeCompositionalStatistics(studyElement, stats);

		File parent = statisticsFile.getParentFile();
		if(!parent.exists())
			parent.mkdirs();
		document.write(statisticsFile);
	}
	
	private void writeEgoAnswers(Element e, Interview interview) {
		Iterator egoAnswers = interview.getEgoAnswers().iterator();
		Element eqList = e.addElement("EgoAnswers");

		while (egoAnswers.hasNext()) {
			Answer answer = (Answer) egoAnswers.next();

			try {
				Element aElement = eqList.addElement("EgoAnswer");
				aElement.addElement("Title")
						.setString(
								study.getQuestions().getQuestion(
										answer.questionId).title);

				if (answer.isAnswered()) {
					aElement.addElement("Answer").setString(answer.string);
					aElement.addElement("AnswerIndex")
							.setInt(answer.getValue());
				} else {
					aElement.addElement("Answer").setString("N/A");
					aElement.addElement("AnswerIndex").setInt(Answer.NO_ANSWER);
				}
			} catch (Exception ex) {
				System.err.println("Failure in Interview::writeEgoAnswers; "
						+ ex);
			}
		}
	}



	/********
	 * Add interview information to an xml structure for output to a file
	 * @param e XML Element, parent of interview tree
	 */
	public void writeCompositionalStatistics(Element e, Statistics stats)
	{
	    Element aqList = e.addElement("AlterQuestionSummaries");
	
	    for (int i = 0; i < stats.alterStatArray.length; ++i)
	    {
	        Element aqElement = aqList.addElement("AlterQuestionSummary");
	
	        aqElement.addElement("Title").setString(stats.alterStatArray[i].qTitle);
	        aqElement.addElement("Count").setInt(stats.alterStatArray[i].answerCount);
	
	        Element aList = aqElement.addElement("Answers");
	        for (int j = 0; j < stats.alterStatArray[i].answerText.length; ++j)
	        {
	            Element aElement = aList.addElement("Answer");
	            aElement.addElement("Text").setString(stats.alterStatArray[i].answerText[j]);
	            aElement.addElement("Total").setInt(stats.alterStatArray[i].answerTotals[j]);
	            aElement.addElement("AnswerIndex").setInt(j);
	        }
	    }
	}



	/********
	 * Add interview information to an xml structure for output to a file
	 * @param e XML Element, parent of interview tree
	 */
	public void writeStructuralStatistics(Element e, Interview _interview, Statistics stats)
	{
	    String egoName = _interview.getIntName();
	    Element egoNameElem = e.addElement("EgoName");
	    egoNameElem.setString(egoName);
	
	    e.addElement("DegreeValue").setInt(stats.mostCentralDegreeAlterValue);
	    e.addElement("DegreeName").setString(stats.mostCentralDegreeAlterName);
	    e.addElement("DegreeMean").setFloat(stats.meanCentralDegreeValue);
	    e.addElement("DegreeNC").setFloat(stats.degreeNC);
	
	    e.addElement("BetweenValue").setFloat(stats.mostCentralBetweenAlterValue);
	    e.addElement("BetweenName").setString(stats.mostCentralBetweenAlterName);
	    e.addElement("BetweenMean").setFloat(stats.meanCentralBetweenAlterValue);
	    e.addElement("BetweenNC").setFloat(stats.betweenNC);
	
	    e.addElement("ClosenessValue").setFloat(stats.mostCentralClosenessAlterValue);
	    e.addElement("ClosenessName").setString(stats.mostCentralClosenessAlterName);
	    e.addElement("ClosenessMean").setFloat(stats.meanCentralClosenessValue);
	    e.addElement("ClosenessNC").setFloat(stats.closenessNC);
	
	    e.addElement("NumCliques").setInt(stats.cliqueSet.size());
	    e.addElement("NumComponents").setInt(stats.componentSet.size()-stats.isolates-stats.dyads);
	    e.addElement("NumIsolates").setInt(stats.isolates);
	    e.addElement("NumDyads").setInt(stats.dyads);
	}
	
}
