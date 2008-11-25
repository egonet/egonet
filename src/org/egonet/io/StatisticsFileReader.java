package org.egonet.io;

import java.io.File;

import com.endlessloopsoftware.ego.client.statistics.StatRecord;
import com.endlessloopsoftware.ego.client.statistics.StatRecord.AlterAnswer;
import com.endlessloopsoftware.ego.client.statistics.StatRecord.EgoAnswer;
import electric.xml.Document;
import electric.xml.Element;
import electric.xml.Elements;
import electric.xml.ParseException;

public class StatisticsFileReader {

	private File statisticsFile;
	public StatisticsFileReader(File statisticsFile) {
		super();
		this.statisticsFile = statisticsFile;
	}

	public StatRecord readStatRecord() throws ParseException
	{
		Document document = new Document(statisticsFile);
		Element e = document.getRoot();

		
		StatRecord sr = new StatRecord();

		Element nameElem = e.getElement("EgoName");
		if (nameElem != null)
		{
			sr.name = nameElem.getString("First") + " " + nameElem.getString("Last");
		}

		sr.degreeName     = e.getString("DegreeName");
		sr.degreeValue    = new Integer(e.getInt("DegreeValue"));
		sr.degreeMean     = new Float(e.getFloat("DegreeMean"));
		sr.degreeNC       = new Float(e.getFloat("DegreeNC"));

		sr.closenessName  = e.getString("ClosenessName");
		sr.closenessValue = new Float(e.getFloat("ClosenessValue"));
		sr.closenessMean  = new Float(e.getFloat("ClosenessMean"));
		sr.closenessNC    = new Float(e.getFloat("ClosenessNC"));

		sr.betweenName    = e.getString("BetweenName");
		sr.betweenValue   = new Float(e.getFloat("BetweenValue"));
		sr.betweenMean    = new Float(e.getFloat("BetweenMean"));
		sr.betweenNC      = new Float(e.getFloat("BetweenNC"));

		sr.numCliques     = new Integer(e.getInt("NumCliques"));
		sr.numComponents  = new Integer(e.getInt("NumComponents"));
		sr.numIsolates    = new Integer(e.getInt("NumIsolates"));
		sr.numDyads       = new Integer(e.getInt("NumDyads"));

		Elements egoList = e.getElement("EgoAnswers").getElements("EgoAnswer");
		while (egoList.hasMoreElements())
		{
			sr.egoAnswers.add(readEgoAnswer(egoList.next()));
		}

		Elements alterList = e.getElement("AlterQuestionSummaries").getElements("AlterQuestionSummary");
		while (alterList.hasMoreElements())
		{
			sr.alterAnswers.add(readAlterAnswer(alterList.next()));
		}

		return sr;
	}

	protected EgoAnswer readEgoAnswer(Element e)
	{
		EgoAnswer ea = new EgoAnswer(e.getString("Title"), e.getString("Answer"), e.getInt("AnswerIndex"));
		return ea;
	}

	protected AlterAnswer readAlterAnswer(Element e)
	{
		int index=0;

		Elements answerList = e.getElement("Answers").getElements("Answer");
		
		AlterAnswer aa = new AlterAnswer(
				e.getString("Title"),
				e.getInt("Count"),
				new String[answerList.size()],
				new int[answerList.size()],
				new int[answerList.size()]
				);
		
		
		while (answerList.hasMoreElements())
		{
			Element a          = answerList.next();

			index              = a.getInt("AnswerIndex");
			aa.selections[index]  = a.getString("Text");
			aa.totals[index]      = a.getInt("Total");
			//added by sonam 08/24/07
			aa.AnswerIndex[index] = a.getInt("AnswerIndex");
			//end
		}
		
		return aa;
	}
}
