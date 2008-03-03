/**
 * <p>Copyright: Copyright (c) 2002 - 2004 </p>
 * <p>Company: Endless Loop Software</p>
 * @author Peter Schoaff
 *
 * $Id: StatRecord.java,v 1.1 2005/08/02 19:36:00 samag Exp $
 */
package com.endlessloopsoftware.ego.client;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.endlessloopsoftware.ego.client.statistics.AlterStats;
import com.endlessloopsoftware.ego.client.statistics.Statistics;

import electric.xml.Element;
import electric.xml.Elements;

public class StatRecord
{
   String  name           = "";
   String  degreeName     = "";
   Integer degreeValue    = new Integer(0);
   Float   degreeMean     = new Float(0);
   Float   degreeNC       = new Float(0);

   String  betweenName    = "";
   Float   betweenValue   = new Float(0);
   Float   betweenMean    = new Float(0);
   Float   betweenNC      = new Float(0);

   String  closenessName  = "";
   Float   closenessValue = new Float(0);
   Float   closenessMean  = new Float(0);
   Float   closenessNC    = new Float(0);

   Integer numCliques     = new Integer(0);
   Integer numComponents  = new Integer(0);
   Integer numIsolates    = new Integer(0);
   Integer numDyads       = new Integer(0);
   
  List    egoAnswers     = new ArrayList();
  List    alterAnswers   = new ArrayList();

 
  public List getEgoAnswers()
   {
	   return egoAnswers;
   }
   public List getAlterAnswers()
   {
	   return alterAnswers;
   }
   
   public StatRecord(Element e)
   {
      Element nameElem = e.getElement("EgoName");
      if (nameElem != null)
      {
         name = nameElem.getString("First") + " " + nameElem.getString("Last");
      }

      degreeName     = e.getString("DegreeName");
      degreeValue    = new Integer(e.getInt("DegreeValue"));
      degreeMean     = new Float(e.getFloat("DegreeMean"));
      degreeNC       = new Float(e.getFloat("DegreeNC"));
      
      closenessName  = e.getString("ClosenessName");
      closenessValue = new Float(e.getFloat("ClosenessValue"));
      closenessMean  = new Float(e.getFloat("ClosenessMean"));
      closenessNC    = new Float(e.getFloat("ClosenessNC"));
      
      betweenName    = e.getString("BetweenName");
      betweenValue   = new Float(e.getFloat("BetweenValue"));
      betweenMean    = new Float(e.getFloat("BetweenMean"));
      betweenNC      = new Float(e.getFloat("BetweenNC"));
         
      numCliques     = new Integer(e.getInt("NumCliques"));
      numComponents  = new Integer(e.getInt("NumComponents"));
      numIsolates    = new Integer(e.getInt("NumIsolates"));
      numDyads       = new Integer(e.getInt("NumDyads"));
      
      Elements egoList = e.getElement("EgoAnswers").getElements("EgoAnswer");
      while (egoList.hasMoreElements())
      {
         egoAnswers.add(new EgoAnswer(egoList.next()));
      }

      Elements alterList = e.getElement("AlterQuestionSummaries").getElements("AlterQuestionSummary");
      while (alterList.hasMoreElements())
      {
         alterAnswers.add(new AlterAnswer(alterList.next()));
      }
   }
   
   public StatRecord(Statistics stats)
   {
      name = stats.getInterview().getName()[0] + " " + stats.getInterview().getName()[1];
      
      degreeName  = stats.mostCentralDegreeAlterName;
      degreeValue = new Integer(stats.mostCentralDegreeAlterValue);
      degreeMean  = new Float(stats.meanCentralDegreeValue);
      degreeNC    = new Float(stats.degreeNC);

      closenessName  = stats.mostCentralClosenessAlterName;
      closenessValue = new Float(stats.mostCentralClosenessAlterValue);
      closenessMean  = new Float(stats.meanCentralClosenessValue);
      closenessNC    = new Float(stats.closenessNC);
      
      betweenName    = stats.mostCentralBetweenAlterName;
      betweenValue   = new Float(stats.mostCentralBetweenAlterValue);
      betweenMean    = new Float(stats.meanCentralBetweenAlterValue);
      betweenNC      = new Float(stats.betweenNC);
      
      numCliques     = new Integer(stats.cliqueSet.size());
      numComponents  = new Integer(stats.componentSet.size() - stats.isolates - stats.dyads);
      numIsolates    = new Integer(stats.isolates);
      numDyads       = new Integer(stats.dyads);

      egoAnswers = Arrays.asList(stats.getInterview().getEgoAnswerArray(this));
      
      for (int i = 0; i < stats.alterStatArray.length; ++i)
      {
         alterAnswers.add(new AlterAnswer(stats.alterStatArray[i]));
      }
   }

   public class EgoAnswer
   {
      String title;
      String answer;
      int    index;

      protected EgoAnswer(Element e)
      {
         title   = e.getString("Title");
         answer  = e.getString("Answer");
         index   = e.getInt("AnswerIndex");
      }
      
      public EgoAnswer(String title, String answer, int index)
      {
         this.title  = title;
         this.answer = answer;
         this.index  = index;
      }
   }

   public class AlterAnswer
   {
      String   title;
      int      count;
      String[] selections;
      int[]    totals;
      //code added
      int[] 	   AnswerIndex;
      //end of add
      
      protected AlterAnswer(Element e)
      {
         int index=0;

         title                = e.getString("Title");
         count                = e.getInt("Count");
         
     
         Elements answerList = e.getElement("Answers").getElements("Answer");
         selections          = new String[answerList.size()];
         totals              = new int[answerList.size()];
         //added by sonam 08/24/07
         AnswerIndex 		 = new int[answerList.size()];
         //end
         while (answerList.hasMoreElements())
         {
            Element a          = answerList.next();
            
            index              = a.getInt("AnswerIndex");
            selections[index]  = a.getString("Text");
            totals[index]      = a.getInt("Total");
            //added by sonam 08/24/07
            AnswerIndex[index] = a.getInt("AnswerIndex");
            //end
         }
      }
      
      protected AlterAnswer(AlterStats alterStats)
      {
         title       = alterStats.qTitle;
         count       = alterStats.answerCount;
         selections  = (String[]) alterStats.answerText.clone();
         totals      = (int[]) alterStats.answerTotals.clone();
      }

	public String[] getSelections() {
		return selections;
	}

	public String getTitle() {
		return title;
	}

   }
}

/**
 * $Log: StatRecord.java,v $
 * Revision 1.1  2005/08/02 19:36:00  samag
 * Initial checkin
 *
 * Revision 1.1  2004/04/08 15:06:07  admin
 * EgoClient now creates study summaries from Server
 * EgoAuthor now sets active study on server
 *
 */