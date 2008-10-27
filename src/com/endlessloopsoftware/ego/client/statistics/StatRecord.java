/***
 * Copyright (c) 2008, Endless Loop Software, Inc.
 * 
 * This file is part of EgoNet.
 * 
 * EgoNet is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 * 
 * EgoNet is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.endlessloopsoftware.ego.client.statistics;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


import electric.xml.Element;
import electric.xml.Elements;

public class StatRecord
{
	public String  name           = "";
	public String  degreeName     = "";
	public Integer degreeValue    = new Integer(0);
	public Float   degreeMean     = new Float(0);
	public Float   degreeNC       = new Float(0);

	public String  betweenName    = "";
	public Float   betweenValue   = new Float(0);
	public Float   betweenMean    = new Float(0);
	public Float   betweenNC      = new Float(0);

	public String  closenessName  = "";
	public Float   closenessValue = new Float(0);
	public Float   closenessMean  = new Float(0);
	public Float   closenessNC    = new Float(0);

	public Integer numCliques     = new Integer(0);
	public Integer numComponents  = new Integer(0);
	public Integer numIsolates    = new Integer(0);
	public Integer numDyads       = new Integer(0);
   
   public List<EgoAnswer>    egoAnswers     = new ArrayList<EgoAnswer>();
   public List<AlterAnswer>    alterAnswers   = new ArrayList<AlterAnswer>();

 
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
      public final String title;
      public final String answer;
      public final int    index;

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
	  public String   title;
      public int      count;
      public String[] selections;
      public int[]    totals;
      //code added
      public int[] 	   AnswerIndex;
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