package org.egonet.wholenet.graph;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import net.sf.functionalj.tuple.Pair;

import org.egonet.exceptions.MissingPairException;
import org.egonet.wholenet.gui.NameMapperFrame.NameMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.endlessloopsoftware.egonet.Interview;
import com.endlessloopsoftware.egonet.Question;
import com.endlessloopsoftware.egonet.Shared;
import com.endlessloopsoftware.egonet.Study;

/**
 * This class encapsulates everything necessary to compile and retain data from
 * a set of Egonet interviews into a whole network, including alter mappings.
 * 
 * @author Martin
 * 
 */
public class WholeNetwork {

	final private static Logger logger = LoggerFactory.getLogger(WholeNetwork.class);
	
	final Study study;
	final List<Pair<File, Interview>> interviewMap;
	final List<NameMapping> nameMap;

	// maps for fast access
	final Map<Integer,WholeNetworkAlter> wholeNetworkAlters;
	final Map<Pair<WholeNetworkAlter,WholeNetworkAlter>,WholeNetworkTie> wholeNetworkTies;

	
	public WholeNetwork(Study study, List<Pair<File, Interview>> interviewMap,
			List<NameMapping> nameMap) {
		super();
		this.study = study;
		this.interviewMap = interviewMap;
		this.nameMap = nameMap;
	
		wholeNetworkAlters = new HashMap<Integer,WholeNetworkAlter>();
		wholeNetworkTies = new HashMap<Pair<WholeNetworkAlter,WholeNetworkAlter>,WholeNetworkTie>();
	}

	public void recompile() {
		wholeNetworkAlters.clear();
		wholeNetworkTies.clear();
		
		// add all alters
		for(NameMapping mapping : nameMap) {
			int group = mapping.getGroup();
			if(!wholeNetworkAlters.containsKey(group)) {
				wholeNetworkAlters.put(group, new WholeNetworkAlter(group));
			}
			
			WholeNetworkAlter alter = wholeNetworkAlters.get(group);
			alter.addOccurence(mapping);
		}
		
		// build all ties
		for(Pair<File, Interview> pair : interviewMap) {
			
			Interview interview = pair.getSecond();
			String [] thisInterviewAlterlist = interview.getAlterList();
			
			// tie the ego to all alters
			Pair<WholeNetworkAlter,NameMapping> ego = findAlter(interview, -1);
			for(int i = 0; i < interview.getAlterList().length; i++) {
				Pair<WholeNetworkAlter,NameMapping> alter = findAlter(interview, i);
				tie(ego, alter, null);
			}
			

			// tie adjacent alters together
			Iterator<Long> questions = study.getQuestionOrder(Shared.QuestionType.ALTER_PAIR).iterator();
			while (questions.hasNext()) {
				Question q = study.getQuestion((Long) questions.next());
				try {
					int [][] adj = interview.generateAdjacencyMatrix(q, false);
	
					// loop through adj
					// if adj[i][j] == 1, thisInterviewAlters[i] && thisInterviewAlters[j] are adjacent in final matrix
	
					for(int i = 0; i < adj.length; i++)
					{
						for(int j = 0; j < adj[i].length; j++)
						{
							if(adj[i][j] == 1 && i != j && 
									i < thisInterviewAlterlist.length && j < thisInterviewAlterlist.length)
							{
	
								String alter1 = thisInterviewAlterlist[i];
								String alter2 = thisInterviewAlterlist[j];
								logger.debug(alter1 + "("+i+") and " + alter2 + "("+j+") are adjacent");
								
								// find whole network alters, tie them!
								Pair<WholeNetworkAlter,NameMapping> wholeAlter1 = findAlter(interview, i);
								Pair<WholeNetworkAlter,NameMapping> wholeAlter2 = findAlter(interview, j);
								
								
								if(wholeAlter1.getFirst().compareTo(wholeAlter2.getFirst()) > 0) {
									Pair<WholeNetworkAlter,NameMapping> swap = wholeAlter1;
									wholeAlter1 = wholeAlter2;
									wholeAlter2 = swap;
								}
								
								tie(wholeAlter1, wholeAlter2, q);
								
							}
						}
					}
				}
				catch (MissingPairException ex) {
					logger.error("Couldn't create adjacency matrix for question " + q, ex);
				}
			}
		}

		logger.info("# Alters: " + wholeNetworkAlters.size() + ", # Ties: " + wholeNetworkTies.size());
	}
	
	private Pair<WholeNetworkAlter,NameMapping> findAlter(Interview interview, Integer alterNumber) {
		
		for(WholeNetworkAlter alter : wholeNetworkAlters.values()) {
			for(NameMapping mapping : alter.getOccurences()) {
				if(mapping.getInterview().equals(interview) && mapping.getAlterNumber().equals(alterNumber))
					return new Pair<WholeNetworkAlter,NameMapping>(alter,mapping);
			}
		}
		
		throw new IllegalArgumentException("Alter did not exist -- it must have been derived from somewhere, so we *must* find it");
	}

	public Map<Integer, WholeNetworkAlter> getWholeNetworkAlters() {
		return wholeNetworkAlters;
	}

	public Map<Pair<WholeNetworkAlter, WholeNetworkAlter>, WholeNetworkTie> getWholeNetworkTies() {
		return wholeNetworkTies;
	}
	
	private void tie(Pair<WholeNetworkAlter,NameMapping> wholeAlter1, Pair<WholeNetworkAlter,NameMapping> wholeAlter2, Question q) {
		
		Pair<WholeNetworkAlter, WholeNetworkAlter> tieKey = new Pair<WholeNetworkAlter,WholeNetworkAlter>(wholeAlter1.getFirst(), wholeAlter2.getFirst());
			
		if(!wholeNetworkTies.containsKey(tieKey)) {
			wholeNetworkTies.put(tieKey, new WholeNetworkTie(tieKey));
			logger.info("Saw new tie for first time: " + tieKey);
		}
		
		WholeNetworkTie tieEntry = wholeNetworkTies.get(tieKey);
		tieEntry.addTie(wholeAlter1.getSecond(), wholeAlter1.getSecond(), q);
	}
	
	public Pair<String[],int[][]> getAdjacencyMatrix() {
		
		List<WholeNetworkAlter> alterList = 
			new ArrayList<WholeNetworkAlter>(wholeNetworkAlters.values());
		
		
		int size = alterList.size();
		
		String [] names = new String[size];
		for(int i = 0; i < names.length; i++) {
			names[i] = alterList.get(i).toString();
		}
		
		int [][] adj = new int[size][size];
		for(int x = 0; x < size; x++) {
			for(int y = 0; y < size; y++) {
				WholeNetworkAlter wholeAlter1 = alterList.get(x);
				WholeNetworkAlter wholeAlter2 = alterList.get(y);
				
				if(wholeAlter1.compareTo(wholeAlter2) > 0) {
					WholeNetworkAlter swap = wholeAlter1;
					wholeAlter1 = wholeAlter2;
					wholeAlter2 = swap;
				}
				
				Pair<WholeNetworkAlter, WholeNetworkAlter> tieKey = new Pair<WholeNetworkAlter,WholeNetworkAlter>(wholeAlter1, wholeAlter2);
				if(wholeNetworkTies.containsKey(tieKey)) {
					WholeNetworkTie tie = wholeNetworkTies.get(tieKey);
					adj[x][y] = tie.numberOfTies();
				}
				else {
					adj[x][y] = 0;
				}
			}
		}
		
		
		return new Pair<String[],int[][]>(names,adj);
	}
}
