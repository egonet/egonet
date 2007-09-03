/****
 * 
 * Copyright (c) 2007, Endless Loop Software, Inc.
 * 
 *  This file is part of EgoNet.
 *
 *    EgoNet is free software; you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as published by
 *    the Free Software Foundation; either version 3 of the License, or
 *    (at your option) any later version.
 *
 *    EgoNet is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU General Public License for more details.
 *
 *    You should have received a copy of the GNU General Public License
 *    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.endlessloopsoftware.ego.client.statistics;


import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.Stack;

import com.endlessloopsoftware.ego.Answer;
import com.endlessloopsoftware.ego.Question;
import com.endlessloopsoftware.ego.Study;
import com.endlessloopsoftware.ego.client.Interview;
import com.endlessloopsoftware.ego.exceptions.MissingPairException;
import com.endlessloopsoftware.elsutils.files.FileHelpers;

import electric.xml.Element;

public class Statistics
{
   private final Study     _study;
   private final Interview _interview;

   public int[][]          adjacencyMatrix         = new int[0][];
   public int[][]          weightedAdjacencyMatrix = new int[0][];
   public int[][]          proximityMatrix         = new int[0][];
   public float[]          betweennessArray        = new float[0];
   public float[]          closenessArray          = new float[0];
   public int[]            farnessArray            = new int[0];
   public int[]            degreeArray             = new int[0];
   public Set              cliqueSet               = new HashSet();
   public Set              allSet                  = new HashSet();
   public Set              componentSet            = new HashSet();
   public AlterStats[]     alterStatArray          = new AlterStats[0];
   public Integer[][]      alterSummary            = new Integer[0][];

   public String[]         alterList               = new String[0];

   public int              isolates;
   public int              dyads;

   public int              mostCentralDegreeAlterValue;
   public float            meanCentralDegreeValue;
   public int              mostCentralDegreeAlterIndex;
   public String           mostCentralDegreeAlterName;
   public float            degreeNC;

   public float            mostCentralBetweenAlterValue;
   public float            meanCentralBetweenAlterValue;
   public int              mostCentralBetweenAlterIndex;
   public String           mostCentralBetweenAlterName;
   public float            betweenNC;

   public float            mostCentralClosenessAlterValue;
   public float            meanCentralClosenessValue;
   public int              mostCentralClosenessAlterIndex;
   public String           mostCentralClosenessAlterName;
   public float            closenessNC;

   public static final int MINIMUM_CLIQUE          = 3;

	/* Instantiable class */
	private Statistics(Interview interview)
	{
      _interview  = interview;
      _study      = interview.getStudy();
	}

	public static Statistics generateInterviewStatistics(Interview interview, Question q)
	{
		Statistics stats = new Statistics(interview);

      int      numAlters = stats.getInterview().getNumAlters();
      int      alterindex;
      float    maxfloat, meanfloat;
      int      maxint, meanint;
      float    sizefloat;

		try
		{
			if (stats.getInterview().getAnswerSubset(q.UniqueId).size() == 0)
			{
				stats.adjacencyMatrix            = new int[0][];
            stats.weightedAdjacencyMatrix    = new int[0][];
            stats.proximityMatrix            = new int[0][];
            stats.betweennessArray           = new float[0];
            stats.closenessArray             = new float[0];
            stats.farnessArray               = new int[0];
            stats.allSet                     = new HashSet(0);
            stats.cliqueSet                  = new HashSet(0);
            stats.componentSet               = new HashSet(0);
            stats.degreeArray                = new int[0];
            stats.alterStatArray             = new AlterStats[0];
            stats.alterSummary               = new Integer[0][];
         }
         else
         {
            stats.adjacencyMatrix            = interview.generateAdjacencyMatrix(q, false);
            stats.weightedAdjacencyMatrix    = interview.generateAdjacencyMatrix(q, true);
            stats.alterList                  = interview.getAlterList();
            stats.allSet                     = new HashSet(0);
            stats.cliqueSet                  = new HashSet(0);
            stats.componentSet               = new HashSet(0);

            stats.allSet                     = stats.identifyAllConnections();
            stats.cliqueSet                  = stats.identifyCliques();
            stats.componentSet               = stats.identifyComponents();
            stats.isolates                   = stats.countComponentSize(1);
            stats.dyads                      = stats.countComponentSize(2);

            stats.clearIdentity(stats.adjacencyMatrix);
            stats.clearIdentity(stats.weightedAdjacencyMatrix);

            stats.proximityMatrix            = stats.generateProximityMatrix();
            stats.betweennessArray           = stats.generateBetweennessArray();
            stats.degreeArray                = stats.generateDegreeArray();

				stats.generateAlterStatArray();

				/* find most between alter */
				maxfloat   = -1;
				alterindex = -1;
				float tot  = 0;
				for (int i = 0; i < numAlters; ++i)
				{
					stats.betweennessArray[i] = (float)(Math.rint(1000*(stats.betweennessArray[i]/2)))/1000;
					tot = tot + stats.betweennessArray[i];
					if (stats.betweennessArray[i] > maxfloat)
					{
						alterindex = i;
						maxfloat = stats.betweennessArray[i];
					}
				}

				stats.mostCentralBetweenAlterValue	= maxfloat;
				stats.mostCentralBetweenAlterIndex 	= alterindex;
				stats.mostCentralBetweenAlterName 	= stats.alterList[alterindex];
				stats.meanCentralBetweenAlterValue   = (float)(Math.rint(1000*tot/numAlters))/1000;

				//compute betweenness network centralization
				meanfloat = 0;
				for (int i = 0; i < numAlters; ++i)
            {
               meanfloat = meanfloat + (maxfloat - stats.betweennessArray[i]);
            }

				stats.betweenNC = (float)(Math.rint(1000*((2*meanfloat*100)/((numAlters-1)*(numAlters-1)*(numAlters-2)))))/1000;

				//System.out.println("Mean betweenness value is "+ stats.meanCentralBetweenAlterValue);
				/* find most central degree alter */
				maxint = -1;
				for (int i = 0; i < numAlters; ++i)
				{
					if (stats.degreeArray[i] > maxint)
					{
						alterindex = i;
						maxint = stats.degreeArray[i];
					}
				}

				meanfloat = 0;
				for (int i = 0; i < numAlters; ++i)
				{
					meanfloat = meanfloat + stats.degreeArray[i];
				}

				//System.out.println("Mean degree centrality is "+ meanint/size);
				stats.mostCentralDegreeAlterValue 	= maxint;

				//System.out.println("Most central degree value is "+ maxint);
				stats.mostCentralDegreeAlterIndex  = alterindex;
				stats.mostCentralDegreeAlterName   = stats.alterList[alterindex];
				stats.meanCentralDegreeValue       = (float)(Math.rint(1000*meanfloat/numAlters))/1000;

				//compute network centralization
				meanfloat = 0;
				for(int i = 0; i < numAlters; ++i){
					meanfloat = meanfloat + (maxint-stats.degreeArray[i]);
				}
				stats.degreeNC = (float)(Math.rint(1000*((meanfloat*100)/((numAlters*numAlters)-(3*numAlters)+2))))/1000;
				//System.out.println("summation is " + meanfloat + "NC is " + stats.degreeNC);

				/* find most central closeness alter */
				stats.closenessArray = new float[stats.alterList.length];
            stats.farnessArray   = new int[stats.alterList.length];
            maxint = -1;
            maxfloat = -1;

				int count = 0;
            boolean conn = true;
            for (int i = 0; i < numAlters; ++i)
            {
               int total = 0;
               count = 0;
               for (int j = 0; j < numAlters; ++j)
               {
                  //For farness add maximum value if unconnected
                  if (stats.proximityMatrix[i][j] == 0)
                  {
                     total += numAlters;
                     ++count;
                  }
                  else
                  {
                     total += stats.proximityMatrix[i][j];
                  }
               }

               if (count > 1)
               {
                  conn  = false;
                  count = 0;
               }

               //Subtract the maximum value added for ego
               total = total - numAlters;
               stats.farnessArray[i] = total; //(total == 0) ? 0 : ((float) 1 /
               // total);
               if (stats.farnessArray[i] > 0)
               {
                  sizefloat = (float) numAlters;
                  stats.closenessArray[i] = (float) (Math.rint(1000 * ((100 * (sizefloat - 1)) / stats.farnessArray[i]))) / 1000;
                  if (stats.closenessArray[i] > maxfloat)
                  {
                     alterindex = i;
                     maxfloat = stats.closenessArray[i];
                  }
               }
               else
               {
                  stats.closenessArray[i] = 0;
               }
            }

				meanfloat = 0;
				for (int i = 0; i < numAlters; ++i)
				{
					meanfloat = meanfloat + stats.closenessArray[i];
				}
				//System.out.println("Mean closeness centrality is "+ meanfloat/size);

				stats.mostCentralClosenessAlterValue 	= maxfloat;
				stats.mostCentralClosenessAlterIndex 	= alterindex;
				stats.mostCentralClosenessAlterName 	= stats.alterList[alterindex];
				stats.meanCentralClosenessValue 		= (float)(Math.rint(1000*meanfloat/numAlters))/1000;

				//compute network centralization
            if (conn)
            {
               meanfloat = 0;
               for (int i = 0; i < numAlters; ++i)
               {
                  meanfloat = meanfloat + (maxfloat - stats.closenessArray[i]);
               }

               stats.closenessNC = (float) (Math.rint(1000 * (meanfloat / (((numAlters * numAlters) - (3 * numAlters) + 2) / ((2 * numAlters) - 3))))) / 1000;
            }
            else
            {
               //System.out.println("Unconnected nw");
               stats.closenessNC = -1;
            }
			}
		}
		catch (MissingPairException ex)
		{
			ex.printStackTrace();
		}

		/*********
		 * test code
		 */
		/*
		long l = System.currentTimeMillis();
		int[][] testmatrix = new int[80][80];
		for (int i = 0; i < 80; ++i)
		{
			for (int j = i; j < 80; ++j)
			{
				int v = (int)(Math.random() * 2);
				testmatrix[i][j] = v;
				testmatrix[j][i] = v;
			}

			testmatrix[i][i] = 1;
		}
		generateProximityMatrix(testmatrix);
		generateBetweennessArray(testmatrix);
		Set testCliqueSet = identifyCliques(testmatrix);
		identifyComponents(testCliqueSet);
		generateDegreeArray(testmatrix);
		l = System.currentTimeMillis() - l;
		System.out.println(l);
		*/

		return stats;
	}

	private void clearIdentity(int[][] matrix)
	{
		for (int i = 0; i < this.adjacencyMatrix.length; ++i)
		{
			matrix[i][i] = 0;
		}
	}

	private void setIdentity(int[][] matrix)
	{
		for (int i = 0; i < this.adjacencyMatrix.length; ++i)
		{
			matrix[i][i] = 1;
		}
	}

	/********
	 * For a non-directional adjacency graph represented by the parameter matrix, determines
	 * shortest path length between each pair of alters
	 * @return Matrix of shortest path lengths
	 */
	private int[][] generateProximityMatrix()
	{
		int size = this.adjacencyMatrix.length;
		int[][] outMatrix = new int[size][];
		int s, i, j, k;

		/* Clear */
		for (i = 0; i < size; ++i)
		{
			outMatrix[i] = (int[]) this.adjacencyMatrix[i].clone();
			outMatrix[i][i] = 0;
		}

		for (i = 0; i < size; ++i)
		{
			for (j = 0; j < size; ++j)
			{
				if (outMatrix[j][i] > 0)
				{
					for (k = 0; k < size; ++k)
					{
						if (outMatrix[i][k] > 0)
						{
							s = outMatrix[j][i] + outMatrix[i][k];
							if ((outMatrix[j][k] == 0) || (s < outMatrix[j][k]))
							{
								outMatrix[j][k] = s;
							}
						}
					}
				}
			}
		}

		/* Clear */
		for (i = 0; i < size; ++i)
		{
			outMatrix[i][i] = 0;
		}

		return (outMatrix);
	}

	/********
	 * For a non-directional adjacency graph represented by the parameter matrix,
	 * the Degree Centrality value for each alter. This is simply the number of
	 * people the alter knows
	 * @return array of C(D) for each alter
	 */
	private int[] generateDegreeArray()
	{
		int[] d = new int[this.adjacencyMatrix.length];

		for (int i = 0; i < this.adjacencyMatrix.length; ++i)
		{
			for (int j = 0; j < this.adjacencyMatrix.length; ++j)
			{
				if (this.adjacencyMatrix[i][j] > 0)
				{
					d[i]++;
				}
			}
		}

		return d;
	}

	/********
	 * For a non-directional adjacency graph represented by the parameter matrix,
	 * the "Betweenness" value for each alter. The betweenness is the percentage of
	 * all shortests paths which pass through the alter.
	 * Based on an algorithm by Ulrik Brandes (2001)
	 * @return array of C(B) for each alter
	 */
	private float[] generateBetweennessArray()
	{
		int size = this.adjacencyMatrix.length;
		float[] Cb = new float[size];
		int s;

		for (s = 0; s < size; ++s)
		{
			Stack S = new Stack();
			List<Integer>[] P = new List<Integer>[size];
			LinkedList Q = new LinkedList();
			int[] spaths = new int[size];
			int[] distance = new int[size];

			for (int w = 0; w < size; ++w)
			{
				P[w] = new LinkedList();
				distance[w] = -1;
			}

			spaths[s] = 1;
			distance[s] = 0;
			Q.addLast(new Integer(s));

			while (!Q.isEmpty())
			{
				Integer V = (Integer) Q.removeFirst();
				int v = V.intValue();

				S.push(V);

				for (int w = 0; w < size; ++w)
				{
					if ((w != v) && (adjacencyMatrix[w][v] > 0))
					{
						// w found for first time?
						if (distance[w] < 0)
						{
							Q.addLast(new Integer(w));
							distance[w] = distance[v] + 1;
						}

						// shortest path to w via v?
						if (distance[w] == (distance[v] + 1))
						{
							spaths[w] += spaths[v];
							P[w].add(new Integer(v));
						}
					}
				}
			}

			// S returns vertices in order of non-increasing distance from s
			float[] dependency = new float[size];
			while (!S.empty())
			{
				int w = ((Integer) S.pop()).intValue();

            for (Integer o : P[w])
            {
               int v = o;
               dependency[v] += (spaths[v] + (spaths[v] * dependency[w])) / spaths[w];
            }

            if (w != s)
				{
					Cb[w] += dependency[w];
				}
			}
		}

		return (Cb);
	}

	/********
	 * For a non-directional adjacency graph represented by the parameter matrix,
	 * counts the number of cliques in the graph
	 * Based on Bron Kerbosch [73]
	 * @return Set of Sets. Each Set represents one clique
	 */
	private Set identifyCliques()
	{
      int[][] matrix     = (int[][]) adjacencyMatrix.clone();
      int size           = matrix.length;
		int[]  all         = new int[size];
		Stack  compsub     = new Stack();
		Set    cliqueSet   = new HashSet();

		for (int c = 0; c < size; ++c)
		{
			all[c] = c;
		}

      // Set identity
      for (int i = 0; i < size; ++i)
         matrix[i][i] = 1;

		extendVersion2(matrix, compsub, cliqueSet, all, 0, size, 0);

		return (cliqueSet);
	}

	/********
	 * For a non-directional adjacency graph represented by the parameter matrix,
	 * counts the number of all fully connected groups in the graph
	 * * @return Set of Sets. Each Set represents one fully connected graph
	 */
	private Set identifyAllConnections()
	{
      int[][] matrix  = (int[][]) adjacencyMatrix.clone();
		int size        = matrix.length;
		int[] all       = new int[size];
		Stack compsub   = new Stack();
		Set allSet      = new HashSet();

		for (int c = 0; c < size; ++c)
		{
			all[c] = c;
		}

      // Set identity
      for (int i = 0; i < size; ++i)
         matrix[i][i] = 1;

		extendVersion2(matrix, compsub, allSet, all, 0, size, 1);

		return (allSet);
	}

	//Returns the number of components of size n in the network
	private int countComponentSize(int n)
	{
		int count = 0;
		Iterator it = this.componentSet.iterator();

		while (it.hasNext())
      {
         Set s = (Set) it.next();
         if (s.size() == n)
         {
            ++count;
         }
      }
		return count;
	}

	private void extendVersion2(int[][] pMatrix, Stack compsub, Set cliqueSet, int[] old, int ne, int ce,
                               int cliqueOrAll)
   {
      int[] newarray    = new int[ce];
      int   nod, fixp   = 0;
      int   db          = -1;
      int   newne, newce, i, j, count, pos = 0, p, s = 0, sel, minnod;

      minnod   = ce;
      nod      = 0;

      // Determine each counter value and look for minimum
      for (i = 0; (i < ce) && (minnod != 0); ++i)
      {
         p = old[i];
         count = 0;

         // Count Disconnections
         for (j = ne; (j < ce) && (count < minnod); ++j)
         {
            if (pMatrix[p][old[j]] == 0)
            {
               ++count;

               // Save position of potential candidate
               pos = j;
            }
         }

         // Test new minimum
         if (count < minnod)
         {
            fixp = p;
            minnod = count;

            if (i < ne)
            {
               s = pos;
               db = 0;
            }
            else
            {
               s     = i;
               db    = 1;
               nod   = 1;
            }
         }
      }

		/***
		 * If fixed point initially chosen from candidates then number of
		 * disconnections will be preincreased by one
		 */
		for (nod += minnod; nod >= 1; --nod)
		{
			/* Interchange */
			p        = old[s];
			old[s]   = old[ne];
			old[ne]  = p;
			sel      = p;

			/* Fill new set "not" */
			newne = 0;
			for (i = 0; i < ne; ++i)
			{
				if (pMatrix[sel][old[i]] > 0)
				{
					newarray[newne++] = old[i];
				}
			}

			newce = newne;

			/* Fill new set "cand" */
			for (i = ne + 1; i < ce; ++i)
			{
				if (pMatrix[sel][old[i]] > 0)
				{
					newarray[newce++] = old[i];
				}
			}

			/* Add to compsub */
			compsub.push(new Integer(sel));

			if (newce == 0)
			{
            if (cliqueOrAll == 1)
            {
               /* Add if all the disconnections are needed */
               /* Return the fully connected graph */
               if (compsub.size() >= 1)
               {
                  cliqueSet.add(compsub.clone());
               }
            }
            else
            {
               /* Add if only the cliques are needed */
               /* Return Clique */
               if (compsub.size() >= MINIMUM_CLIQUE)
               {
                  cliqueSet.add(compsub.clone());
               }
            }
			}
			else if (newne < newce)
			{
				extendVersion2(pMatrix, compsub, cliqueSet, newarray, newne, newce, cliqueOrAll);
			}

			/* Remove from compsub */
			compsub.pop();
			ne++;

			if (nod > 1)
			{
				for (s = ne; pMatrix[fixp][old[s]] > 0; ++s);
			}
		}
	}

	/********
	 * For a non-directional adjacency graph represented by the parameter matrix,
	 * identifies all unconnected components of the graph
	 * @param cliqueSet Set of stacks returned by identifyCliques
	 * @return Set of Sets. Each Set represents one component
	 */
	private Set identifyComponents()
	{
		Set          s     = new HashSet();
		LinkedList   list  = new LinkedList();
		Iterator     it;
		boolean      merged;

		/* clone stacks so this is non-destructive */

		for (it = this.allSet.iterator(); it.hasNext();)
		{
			list.add(new HashSet((Stack) it.next()));
		}

		while (list.size() > 0)
		{
			merged = false;
			it = list.iterator();
			Set component = (Set) it.next();

			while (it.hasNext())
			{
				Set intersection = new HashSet(component);
				Set compareClique = (Set) it.next();

				intersection.retainAll(compareClique);

				if (intersection.size() > 0)
				{
					component.addAll(compareClique);
					it.remove();
					merged = true;
				}
			}

			if (merged == false)
			{
				s.add(component);
				list.remove(0);
			}
		}

		return s;
	}

	/********
	 * Calculates compositional measures of alter questions
	 * Generates percentage summary for categorical, and average for numerical
	 */
	private void generateAlterStatArray()
	{
		List qList = _study.getQuestionOrder(Question.ALTER_QUESTION);
		Iterator qIt = qList.iterator();
		int index = 0;

		alterSummary = new Integer[alterList.length][];

		/* Count qList Questions which are categorical or numerical */
		while (qIt.hasNext())
		{
			Question q = _study.getQuestions().getQuestion((Long) qIt.next());

			if ((q.answerType == Question.CATEGORICAL) || (q.answerType == Question.NUMERICAL))
			{
				index++;
			}
		}

		alterStatArray = new AlterStats[index];
		for (int i = 0; i < index; ++i)
		{
			alterStatArray[i] = new AlterStats();
		}

		for (int i = 0; i < alterList.length; ++i)
		{
			alterSummary[i] = new Integer[index];
		}

		index = 0;
		qIt = qList.iterator();
		while (qIt.hasNext())
		{
			Long qId = (Long) qIt.next();
			Question q = _study.getQuestions().getQuestion(qId);

			if ((q.answerType == Question.CATEGORICAL) || (q.answerType == Question.NUMERICAL))
			{
				Set answerSet = _interview.getAnswerSubset(qId);
				Iterator aIt = answerSet.iterator();

				alterStatArray[index].questionId = qId;
				alterStatArray[index].qTitle = q.title;
				alterStatArray[index].answerType = q.answerType;

				if (q.answerType == Question.NUMERICAL)
				{
					alterStatArray[index].answerText = new String[] { "Mean" };
					alterStatArray[index].answerTotals = new int[1];
				}
				else if (q.answerType == Question.CATEGORICAL)
				{
					alterStatArray[index].answerTotals = new int[q.selections.length];
					alterStatArray[index].answerText = new String[q.selections.length];

					for (int i = 0; i < q.selections.length; ++i)
					{
						alterStatArray[index].answerText[i] = q.selections[q.selections.length - (i + 1)].string;
					}
				}

				while (aIt.hasNext())
				{
					Answer a = (Answer) aIt.next();

					if (a.answered)
					{
						alterSummary[a.alters[0]][index] = new Integer(a.value);

						if (q.answerType == Question.NUMERICAL)
						{
							if (a.value != -1)
							{
								alterStatArray[index].answerTotals[0] += a.value;
								alterStatArray[index].answerCount++;
							}
						}
						else if (q.answerType == Question.CATEGORICAL)
						{
							alterStatArray[index].answerTotals[a.value] += 1;
							alterStatArray[index].answerCount++;
						}
					}
					else
					{
						alterSummary[a.alters[0]][index] = new Integer(-1);
					}
				}

				index++;
			}
		}
	}

	/********
	 * Add interview information to an xml structure for output to a file
	 * @param e XML Element, parent of interview tree
	 */
	public void writeStructuralStatistics(Element e)
	{
		String[] egoName = _interview.getName();
		Element egoNameElem = e.addElement("EgoName");
		egoNameElem.addElement("First").setString(egoName[0]);
		egoNameElem.addElement("Last").setString(egoName[1]);

		e.addElement("DegreeValue").setInt(mostCentralDegreeAlterValue);
		e.addElement("DegreeName").setString(mostCentralDegreeAlterName);
		e.addElement("DegreeMean").setFloat(meanCentralDegreeValue);
		e.addElement("DegreeNC").setFloat(degreeNC);

		e.addElement("BetweenValue").setFloat(mostCentralBetweenAlterValue);
		e.addElement("BetweenName").setString(mostCentralBetweenAlterName);
		e.addElement("BetweenMean").setFloat(meanCentralBetweenAlterValue);
		e.addElement("BetweenNC").setFloat(betweenNC);

		e.addElement("ClosenessValue").setFloat(mostCentralClosenessAlterValue);
		e.addElement("ClosenessName").setString(mostCentralClosenessAlterName);
		e.addElement("ClosenessMean").setFloat(meanCentralClosenessValue);
		e.addElement("ClosenessNC").setFloat(closenessNC);

		e.addElement("NumCliques").setInt(cliqueSet.size());
		e.addElement("NumComponents").setInt(componentSet.size()-isolates-dyads);
		e.addElement("NumIsolates").setInt(isolates);
		e.addElement("NumDyads").setInt(dyads);
	}

	/********
	 * Add interview information to an xml structure for output to a file
	 * @param e XML Element, parent of interview tree
	 */
	public void writeCompositionalStatistics(Element e)
	{
		Element aqList = e.addElement("AlterQuestionSummaries");

		for (int i = 0; i < alterStatArray.length; ++i)
		{
			Element aqElement = aqList.addElement("AlterQuestionSummary");

			aqElement.addElement("Title").setString(alterStatArray[i].qTitle);
			aqElement.addElement("Count").setInt(alterStatArray[i].answerCount);

			Element aList = aqElement.addElement("Answers");
			for (int j = 0; j < alterStatArray[i].answerText.length; ++j)
			{
				Element aElement = aList.addElement("Answer");
				aElement.addElement("Text").setString(alterStatArray[i].answerText[j]);
				aElement.addElement("Total").setInt(alterStatArray[i].answerTotals[j]);
				aElement.addElement("AnswerIndex").setInt(j);
			}
		}
	}

	/********
	 * Write proximity array for current question to a printwriter
	 * @param name      Name of Ego
	 * @param w         PrintWriter
	 * @param weighted  use weighted proximity array
	 */
	public void writeAdjacencyArray(String name, PrintWriter w, boolean weighted)
	{
		// Write column names
		w.print(FileHelpers.formatForCSV(name));

		for (int i = 0; i < alterList.length; ++i)
		{
			w.print(", " + FileHelpers.formatForCSV(alterList[i]));
		}
		w.println();

		for (int i = 0; i < alterList.length; ++i)
		{
			w.print(FileHelpers.formatForCSV(alterList[i]));
			if (weighted)
			{
				for (int j = 0; j < alterList.length; ++j)
				{
					w.print(", " + weightedAdjacencyMatrix[i][j]);
				}
			}
			else
			{
				for (int j = 0; j < alterList.length; ++j)
				{
					w.print(", " + adjacencyMatrix[i][j]);
				}
			}
			w.println();
		}
	}

	/********
	 * Write alters answer summary to a printwriter
	 * @param w         PrintWriter
	 */
	public void writeAlterArray(PrintWriter w)
	{
		// Write column names
		w.write("Alter_Name");
		for (int i = 0; i < alterStatArray.length; ++i)
		{
			w.write(", " + FileHelpers.formatForCSV(alterStatArray[i].qTitle));
		}
		w.println(", Degree, Closeness, Betweenness");

		for (int i = 0; i < alterList.length; ++i)
		{
			w.print(FileHelpers.formatForCSV(alterList[i]));

			for (int j = 0; j < alterSummary[0].length; ++j)
			{
				try
				{
					w.print(", " + alterSummary[i][j]);
				}
				catch (Exception ex)
				{
					ex.printStackTrace();
				}
			}
			w.println(", " + degreeArray[i] + ", " + closenessArray[i] + ", " + betweennessArray[i]);
		}
	}

	/********
	 * Calculates compositional measures of alter questions
	 * Generates percentage summary for categorical, and average for numerical
	 */
	public void writeTextAnswers(PrintWriter w)
	{
		List qList;
		Iterator qIt;
		int parsePtr;
		String s;

		qList = _interview.getEgoAnswers();
		qIt = qList.iterator();
		while (qIt.hasNext())
		{
			Answer answer = (Answer) qIt.next();
			Question q = _study.getQuestions().getQuestion(answer.questionId);

			if (q.answerType == Question.TEXT)
			{
				if (answer.answered)
				{
					w.println("Ego Question: " + q.title);
					w.println("Text: " + q.text);
					w.println(answer.string);
				}
			}
		}

		qList = _study.getQuestionOrder(Question.ALTER_QUESTION);
		qIt = qList.iterator();
		while (qIt.hasNext())
		{
			Long qId = (Long) qIt.next();
			Question q = _study.getQuestions().getQuestion(qId);

			if (q.answerType == Question.TEXT)
			{
				w.println("Alter Question: " + q.title);
				w.println("Text: " + q.text);
				Set answerSet = _interview.getAnswerSubset(qId);
				Iterator aIt = answerSet.iterator();

				while (aIt.hasNext())
				{
					Answer a = (Answer) aIt.next();

					if (a.answered)
					{
						w.println(alterList[a.alters[0]] + ": " + a.string);
					}
				}
			}
		}
	}

   /**
    * @return Returns the interview.
    */
   public Interview getInterview()
   {
      return _interview;
   }

   /**
    * @return Returns the study.
    */
   public Study getStudy()
   {
      return _study;
   }
}

/**
 * $Log$
 * Revision 1.1  2007/09/03 13:51:24  schoaff
 * Initial Checkin
 *
 * Revision 1.14  2004/04/08 15:06:07  admin
 * EgoClient now creates study summaries from Server
 * EgoAuthor now sets active study on server
 *
 * Revision 1.13  2004/04/06 20:29:22  admin
 * First pass as supporting interactive applet linking interviews
 *
 * Revision 1.12  2004/04/06 15:46:11  admin
 * cvs tags in headers
 *
 * revision 1.11
 * Moving matrix generation into interview to support Applet Linking UI.
 * An interview generated with applet linking will have no meaningful alter pair
 * questions. The adjacency matrix will be returned in an Athenian manner from
 * the server.
 *
 * revision 1.10
 * Work to integrate with Applet Linking UI
 *
 * revision 1.9
 * Fixed the components: It was looking at cliques before.Now it will look
 * at all the different components in the graph.
 * Added display of Components and Cliques
 *
 * revision 1.8
 * Adding client library
 * cleaning up code
 *
 * revision 1.7
 * Added some structural measures
 *
 * revision 1.6
 * Version 2.0 beta 3
 *
 * revision 1.5
 * Making sure all libraries are available
 *
 * revision 1.4
 * Fixing bug reading in adjacency selections
 * Clearing identity diagonal of Weighted Adjacency Matrix
 *
 * revision 1.3
 * Modified to generate matrix files on survey completion or summarization
 * Extracted statistics models
 *
 * revision 1.2
 * Extracting Study
 *
 * revision 1.1
 * Merging EgoNet and EgoClient projects so that they can share some
 * common classes more easily.
 */