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

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.Stack;

import org.egonet.exceptions.MissingPairException;
import org.egonet.model.Interview;
import org.egonet.model.Study;
import org.egonet.model.answer.*;
import org.egonet.model.question.AlterPromptQuestion;
import org.egonet.model.question.AlterQuestion;
import org.egonet.model.question.Question;
import org.egonet.util.FileHelpers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.com.bytecode.opencsv.CSVWriter;

public class Statistics
{
	final private static Logger logger = LoggerFactory.getLogger(Statistics.class);
	
    private final Study     _study;
    private final Interview _interview;

    public int[][]          adjacencyMatrix         = new int[0][];
    public int[][]          weightedAdjacencyMatrix = new int[0][];
    public int[][]          proximityMatrix         = new int[0][];
    public int[][]          alter_alterPromptMatrix = new int[0][];
    public float[]          betweennessArray        = new float[0];
    public float[]          closenessArray          = new float[0];
    public int[]            farnessArray            = new int[0];
    public int[]            degreeArray             = new int[0];
    public Set<Stack<Integer>>       cliqueSet               = new HashSet<Stack<Integer>>();
    public Set<Stack<Integer>>              allSet                  = new HashSet<Stack<Integer>>();
    public Set<Set<Integer>>              componentSet            = new HashSet<Set<Integer>>();
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
        
        int      numAlters = stats.getInterview().getNumberAlters();
        //int numAlters = 2;
        int      alterindex;
        float    maxfloat, meanfloat;
        int      maxint;
        float    sizefloat;

        boolean noneYet = stats.getInterview().getAnswerSubset(q.UniqueId).size() == 0;
        logger.info("CREATING STATISTICS: Study " + (noneYet ? "has none yet": "already had some"));
        
        try
        {
            if (noneYet)
            {
                stats.adjacencyMatrix            = new int[0][];
                stats.weightedAdjacencyMatrix    = new int[0][];
                stats.alter_alterPromptMatrix    = new int[0][];
                stats.proximityMatrix            = new int[0][];
                stats.betweennessArray           = new float[0];
                stats.closenessArray             = new float[0];
                stats.farnessArray               = new int[0];
                stats.allSet                     = new HashSet<Stack<Integer>>(0);
                stats.cliqueSet                  = new HashSet<Stack<Integer>>(0);
                stats.componentSet               = new HashSet<Set<Integer>>(0);
                stats.degreeArray                = new int[0];
                stats.alterStatArray             = new AlterStats[0];
                stats.alterSummary               = new Integer[0][];
            }
            else
            {
                stats.adjacencyMatrix            = interview.generateAdjacencyMatrix(q, false);
                stats.weightedAdjacencyMatrix    = interview.generateAdjacencyMatrix(q, true);
                stats.alter_alterPromptMatrix    = interview.generateAlterByAlterPromptMatrix();
                stats.alterList                  = interview.getAlterList();
                stats.allSet                     = new HashSet<Stack<Integer>>(0);
                stats.cliqueSet                  = new HashSet<Stack<Integer>>(0);
                stats.componentSet               = new HashSet<Set<Integer>>(0);

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

                //logger.info("Mean betweenness value is "+ stats.meanCentralBetweenAlterValue);
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

                //logger.info("Mean degree centrality is "+ meanint/size);
                stats.mostCentralDegreeAlterValue 	= maxint;

                //logger.info("Most central degree value is "+ maxint);
                stats.mostCentralDegreeAlterIndex  = alterindex;
                stats.mostCentralDegreeAlterName   = stats.alterList[alterindex];
                stats.meanCentralDegreeValue       = (float)(Math.rint(1000*meanfloat/numAlters))/1000;

                //compute network centralization
                meanfloat = 0;
                for(int i = 0; i < numAlters; ++i){
                    meanfloat = meanfloat + (maxint-stats.degreeArray[i]);
                }
                stats.degreeNC = (float)(Math.rint(1000*((meanfloat*100)/((numAlters*numAlters)-(3*numAlters)+2))))/1000;
                //logger.info("summation is " + meanfloat + "NC is " + stats.degreeNC);

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
                //logger.info("Mean closeness centrality is "+ meanfloat/size);

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
                    //logger.info("Unconnected nw");
                    stats.closenessNC = -1;
                }
            }
        }
        catch (MissingPairException ex)
        {
            throw new RuntimeException(ex);
        }

        return stats;
    }

    private void clearIdentity(int[][] matrix)
    {
        for (int i = 0; i < this.adjacencyMatrix.length; ++i)
        {
            matrix[i][i] = 0;
        }
    }

    /********
     * For a non-directional adjacency graph represented by the parameter matrix, determines
     * shortest path length between each pair of alters
     * @param adjacencyMatrix representing non-directed graph of alters
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
     * @param adjacencyMatrix representing non-directed graph of alters
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

    private float[] generateBetweennessArray() {
    	return generateBetweennessArray(this.adjacencyMatrix);
    }
    
    /********
     * For a non-directional adjacency graph represented by the parameter matrix,
     * the "Betweenness" value for each alter. The betweenness is the percentage of
     * all shortests paths which pass through the alter.
     * Based on an algorithm by Ulrik Brandes (2001)
     * @return array of C(B) for each alter
     */
    public static float[] generateBetweennessArray(int [][] adjacencyMatrix)
    {
        int size = adjacencyMatrix.length;
        float[] Cb = new float[size];

        // for each vertex in graph
        for (int s = 0; s < size; ++s) {
            Stack<Integer> S = new Stack<Integer>();
            
            @SuppressWarnings({"unchecked"})
            java.util.List<Integer>[] P = new java.util.List[size];
            
            LinkedList<Integer> Q = new LinkedList<Integer>();
            int[] spaths = new int[size];
            int[] distance = new int[size];

            for (int w = 0; w < size; ++w) {
                P[w] = new LinkedList<Integer>();
                distance[w] = -1;
            }

            spaths[s] = 1;
            distance[s] = 0;
            Q.addLast(s);

            while (!Q.isEmpty()) {
                int v = Q.removeFirst();

                S.push(v);

                for (int w = 0; w < size; ++w) {
                    if ((w != v) && (adjacencyMatrix[w][v] > 0)) {
                        // w found for first time?
                        if (distance[w] < 0) {
                            Q.addLast(w);
                            distance[w] = distance[v] + 1;
                        }

                        // shortest path to w via v?
                        if (distance[w] == (distance[v] + 1)) {
                            spaths[w] += spaths[v];
                            P[w].add(v);
                        }
                    }
                }
            }

            // S returns vertices in order of non-increasing distance from s
            float[] dependency = new float[size];
            while (!S.empty()) {
                int w = S.pop();
                
                for(int v : P[w]) {
                    dependency[v] += (spaths[v] + (spaths[v] * dependency[w])) / spaths[w];
                }

                if (w != s) {
                    Cb[w] += dependency[w];
                }
            }
        }

        return Cb;
    }

    /********
     * For a non-directional adjacency graph represented by the parameter matrix,
     * counts the number of cliques in the graph
     * Based on Bron Kerbosch [73]
     * @return Set of Sets. Each Set represents one clique
     */
    private Set<Stack<Integer>> identifyCliques()
    {
        int[][] matrix     = (int[][]) adjacencyMatrix.clone();
        int size           = matrix.length;
        int[]  all         = new int[size];
        Stack<Integer>  compsub     = new Stack<Integer>();
        Set<Stack<Integer>>    cliqueSet   = new HashSet<Stack<Integer>>();

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
    private Set<Stack<Integer>> identifyAllConnections()
    {
        int[][] matrix  = (int[][]) adjacencyMatrix.clone();
        int size        = matrix.length;
        int[] all       = new int[size];
        Stack<Integer> compsub   = new Stack<Integer>();
        Set<Stack<Integer>> allSet      = new HashSet<Stack<Integer>>();

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

    @SuppressWarnings("unchecked")
    private void extendVersion2(int[][] pMatrix, Stack<Integer> compsub, Set<Stack<Integer>> cliqueSet, int[] old, int ne, int ce,
            int cliqueOrAll)
    {
        int[] newarray    = new int[ce];
        int   nod, fixp   = 0;
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
                }
                else
                {
                    s     = i;
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
            compsub.push(sel);

            if (newce == 0)
            {
                if (cliqueOrAll == 1)
                {
                    /* Add if all the disconnections are needed */
                    /* Return the fully connected graph */
                    if (compsub.size() >= 1)
                    {
                        cliqueSet.add((Stack<Integer>)compsub.clone());
                    }
                }
                else
                {
                    /* Add if only the cliques are needed */
                    /* Return Clique */
                    if (compsub.size() >= MINIMUM_CLIQUE)
                    {
                        cliqueSet.add((Stack<Integer>)compsub.clone());
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
    private Set<Set<Integer>> identifyComponents()
    {
        Set<Set<Integer>>          s     = new HashSet<Set<Integer>>();
        LinkedList<Set<Integer>>   list  = new LinkedList<Set<Integer>>();

        boolean      merged;

        /* clone stacks so this is non-destructive */

        for (Iterator<Stack<Integer>> it = this.allSet.iterator(); it.hasNext();)
        {
            list.add(
                    new HashSet<Integer>(it.next())
            );
        }

        while (list.size() > 0)
        {
            merged = false;
            Iterator<Set<Integer>> it = list.iterator();
            Set<Integer> component = it.next();

            while (it.hasNext())
            {
                Set<Integer> intersection = new HashSet<Integer>(component);
                Set<Integer> compareClique = it.next();

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
        List<Long> qList = _study.getQuestionOrder(AlterQuestion.class);
        Iterator<Long> qIt = qList.iterator();
        int index = 0;

        alterSummary = new Integer[alterList.length][];

        /* Count qList Questions which are categorical or numerical */
        while (qIt.hasNext())
        {
            Question q = _study.getQuestions().getQuestion((Long) qIt.next());

            if ((q.answerType.equals(CategoricalAnswer.class)) || (q.answerType.equals(NumericalAnswer.class)))
                index++;
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

            if ((q.answerType.equals(CategoricalAnswer.class)) || (q.answerType.equals(NumericalAnswer.class)))
            {
                Set<Answer> answerSet = _interview.getAnswerSubset(qId);
                Iterator<Answer> aIt = answerSet.iterator();

                alterStatArray[index].questionId = qId;
                alterStatArray[index].qTitle = q.title;
                alterStatArray[index].answerType = q.answerType;

                if (q.answerType.equals(NumericalAnswer.class))
                {
                    alterStatArray[index].answerText = new String[] { "Mean" };
                    alterStatArray[index].answerTotals = new int[1];
                }
                else if (q.answerType.equals(CategoricalAnswer.class))
                {
                    alterStatArray[index].answerTotals = new int[q.getSelections().size()];
                    alterStatArray[index].answerText = new String[q.getSelections().size()];

                    for (int i = 0; i < q.getSelections().size(); ++i)
                    {
                        //	alterStatArray[index].answerText[i] = q.selections[q.selections.length - (i + 1)].getString();
                        alterStatArray[index].answerText[i] = q.getSelections().get(i).getString();
                    }
                }

                while (aIt.hasNext())
                {
                    try {
                        Answer a = (Answer) aIt.next();

                        if (a.isAnswered())
                        {
                            alterSummary[a.firstAlter()][index] = new Integer(a.getValue());

                            if (q.answerType.equals(NumericalAnswer.class))
                            {
                                if (a.getValue() != -1)
                                {
                                    alterStatArray[index].answerTotals[0] += a.getValue();
                                    alterStatArray[index].answerCount++;
                                }
                            }
                            else if (q.answerType.equals(CategoricalAnswer.class))
                            {
                                alterStatArray[index].answerTotals[a.getIndex()] += 1;
                                alterStatArray[index].answerCount++;
                            }
                        }
                        else
                        {
                            alterSummary[a.firstAlter()][index] = new Integer(-1);
                        }

                    } catch (Exception ex) {
                        throw new RuntimeException(ex);
                    }

                }

                index++;
            }
        }
    }

    
        /******************************************************************************
        * Writes matrix alter-prompt to see which alters has appeared in which alter prompt
        * questions.
        * 
        * @param alterPromptWriter
        *              File to write data to
        * 
        * @throws IOException
        * 
        */
        public void writeAlterByPromptFile(PrintWriter alterPromptWriter, String name) throws IOException
        {
            logger.info("Writing Alter By Prompt matrix");
            CSVWriter alterByPromptCSVWriter = new CSVWriter(alterPromptWriter);
            writeAlterByPromptMatrix( name, alterByPromptCSVWriter );
            alterByPromptCSVWriter.close(); 
        }
    
        private void writeAlterByPromptMatrix(String name, CSVWriter w){
        
        List <String> columnNames = new ArrayList<String>();
        columnNames.add(name);
        int numPrompts = _study.getQuestionOrder(AlterPromptQuestion.class).size();
       
        for (int i = 0; i < numPrompts; ++i)
        {
                columnNames.add(FileHelpers.formatForCSV("Question "+i));
                
        }
        
        w.writeNext(columnNames.toArray(new String[]{}));
        
         for (int i = 0; i < alterList.length; i++)
         {
             List<String> row = new ArrayList<String>();  
             //_study.getQuestionIterator(AlterPromptQuestion.class);
             row.add(FileHelpers.formatForCSV(alterList[i]));
             
             for (int j = 0; j < numPrompts ; ++j)
             {
                 row.add(""+ alter_alterPromptMatrix[i][j]);
             }    
             w.writeNext(row.toArray(new String[]{}));
         }
    }
        
     /***************************************************************************
     * Writes all questions to a package file for later use
     * 
     * @param f
     *            File to write data to
     * @param stats
     *            Statistics Object
     * @throws IOException 
     * @throws IOException
     */
    public void writeAdjacencyFile(PrintWriter adjacencyWriter, String name, boolean weighted) throws IOException {
        logger.info("Writing "+(weighted ? "" : "non-")+"weighted adjacency file for " + name);
        CSVWriter adjacencyCSVWriter = new CSVWriter(adjacencyWriter);
        writeAdjacencyArray(name, adjacencyCSVWriter, weighted);
        adjacencyCSVWriter.close();
    }

    /********
     * Write proximity array for current question to a printwriter
     * @param name      Name of Ego
     * @param w         PrintWriter
     * @param weighted  use weighted proximity array
     */
    private void writeAdjacencyArray(String name, CSVWriter w, boolean weighted)
    {
        // Write column names
    	List<String> columnNames = new ArrayList<String>();
    	columnNames.add(name);
        for (int i = 0; i < alterList.length; ++i)
        {
        	columnNames.add(FileHelpers.formatForCSV(alterList[i]));
        }
        w.writeNext(columnNames.toArray(new String[]{}));

        // Write other rows
        for (int i = 0; i < alterList.length; ++i)
        {
        	List<String> row = new ArrayList<String>();
        	row.add(FileHelpers.formatForCSV(alterList[i]));
            for (int j = 0; j < alterList.length; ++j)
            {
                row.add(""+(weighted ? weightedAdjacencyMatrix[i][j] : adjacencyMatrix[i][j]));
            }
            w.writeNext(row.toArray(new String[]{}));
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
                w.print(", " + alterSummary[i][j]);
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

        qList = _interview.getEgoAnswers();
        qIt = qList.iterator();
        while (qIt.hasNext())
        {
            Answer answer = (Answer) qIt.next();
            Question q = _study.getQuestions().getQuestion(answer.getQuestionId());

            if (q.answerType.equals(TextAnswer.class))
            {
                if (answer.isAnswered())
                {
                    w.println("Ego Question: " + q.title);
                    w.println("Text: " + q.text);
                    w.println(answer.string);
                }
            }
        }

        qList = _study.getQuestionOrder(AlterQuestion.class);
        qIt = qList.iterator();
        while (qIt.hasNext())
        {
            Long qId = (Long) qIt.next();
            Question q = _study.getQuestions().getQuestion(qId);

            if (q.answerType.equals(TextAnswer.class))
            {
                w.println("Alter Question: " + q.title);
                w.println("Text: " + q.text);
                Set answerSet = _interview.getAnswerSubset(qId);
                Iterator aIt = answerSet.iterator();

                while (aIt.hasNext())
                {
                    Answer a = (Answer) aIt.next();

                    if (a.isAnswered())
                    {
                        w.println(alterList[a.firstAlter()] + ": " + a.string);
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