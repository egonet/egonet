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
	public Integer degreeValue    = Integer.valueOf(0);
	public Float   degreeMean     = Float.valueOf(0);
	public Float   degreeNC       = Float.valueOf(0);

	public String  betweenName    = "";
	public Float   betweenValue   = Float.valueOf(0);
	public Float   betweenMean    = Float.valueOf(0);
	public Float   betweenNC      = Float.valueOf(0);

	public String  closenessName  = "";
	public Float   closenessValue = Float.valueOf(0);
	public Float   closenessMean  = Float.valueOf(0);
	public Float   closenessNC    = Float.valueOf(0);

	public Integer numCliques     = Integer.valueOf(0);
	public Integer numComponents  = Integer.valueOf(0);
	public Integer numIsolates    = Integer.valueOf(0);
	public Integer numDyads       = Integer.valueOf(0);
   
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
      degreeValue = Integer.valueOf(stats.mostCentralDegreeAlterValue);
      degreeMean  = Float.valueOf(stats.meanCentralDegreeValue);
      degreeNC    = Float.valueOf(stats.degreeNC);

      closenessName  = stats.mostCentralClosenessAlterName;
      closenessValue = Float.valueOf(stats.mostCentralClosenessAlterValue);
      closenessMean  = Float.valueOf(stats.meanCentralClosenessValue);
      closenessNC    = Float.valueOf(stats.closenessNC);

      betweenName    = stats.mostCentralBetweenAlterName;
      betweenValue   = Float.valueOf(stats.mostCentralBetweenAlterValue);
      betweenMean    = Float.valueOf(stats.meanCentralBetweenAlterValue);
      betweenNC      = Float.valueOf(stats.betweenNC);

      numCliques     = Integer.valueOf(stats.cliqueSet.size());
      numComponents  = Integer.valueOf(stats.componentSet.size() - stats.isolates - stats.dyads);
      numIsolates    = Integer.valueOf(stats.isolates);
      numDyads       = Integer.valueOf(stats.dyads);

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