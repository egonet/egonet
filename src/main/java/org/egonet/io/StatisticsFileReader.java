package org.egonet.io;

import java.io.File;

import org.egonet.statistics.StatRecord;
import org.egonet.statistics.StatRecord.AlterAnswer;
import org.egonet.statistics.StatRecord.EgoAnswer;
import org.egonet.util.Name;

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
			sr.setName(new Name(nameElem.getString("First"),nameElem.getString("Last")).toString());
		}

		sr.degreeName     = e.getString("DegreeName");
		sr.degreeValue    = Integer.valueOf(e.getInt("DegreeValue"));
		sr.degreeMean     = Float.valueOf(e.getFloat("DegreeMean"));
		sr.degreeNC       = Float.valueOf(e.getFloat("DegreeNC"));

		sr.closenessName  = e.getString("ClosenessName");
		sr.closenessValue = Float.valueOf(e.getFloat("ClosenessValue"));
		sr.closenessMean  = Float.valueOf(e.getFloat("ClosenessMean"));
		sr.closenessNC    = Float.valueOf(e.getFloat("ClosenessNC"));

		sr.betweenName    = e.getString("BetweenName");
		sr.betweenValue   = Float.valueOf(e.getFloat("BetweenValue"));
		sr.betweenMean    = Float.valueOf(e.getFloat("BetweenMean"));
		sr.betweenNC      = Float.valueOf(e.getFloat("BetweenNC"));

		sr.numCliques     = Integer.valueOf(e.getInt("NumCliques"));
		sr.numComponents  = Integer.valueOf(e.getInt("NumComponents"));
		sr.numIsolates    = Integer.valueOf(e.getInt("NumIsolates"));
		sr.numDyads       = Integer.valueOf(e.getInt("NumDyads"));

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
