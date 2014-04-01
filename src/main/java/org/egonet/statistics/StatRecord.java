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
package org.egonet.statistics;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.egonet.util.Name;


public class StatRecord
{
	private String  name           = "";
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

   public StatRecord()
   {
	   
   }
 
  public List getEgoAnswers()
   {
	   return egoAnswers;
   }
   public List getAlterAnswers()
   {
	   return alterAnswers;
   }
   
   public StatRecord(Statistics stats)
   {
	   setName(new Name(stats.getInterview().getIntName()).toString());
      
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

   public void setName(String name) {
	this.name = name;
}

public String getName() {
	return name;
}

public static class EgoAnswer
   {
      public final String title;
      public final String answer;
      public final int    index;

      public EgoAnswer(String title, String answer, int index)
      {
         this.title  = title;
         this.answer = answer;
         this.index  = index;
      }
   }

   public static class AlterAnswer
   {
	  public String   title;
      public int      count;
      public String[] selections;
      public int[]    totals;
      //code added
      public int[] 	   AnswerIndex;
      //end of add
      
      public AlterAnswer(String title, int count, String[] selections,
			int[] totals, int[] answerIndex) {
		super();
		this.title = title;
		this.count = count;
		this.selections = selections;
		this.totals = totals;
		AnswerIndex = answerIndex;
	}

	public AlterAnswer(AlterStats alterStats)
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