package org.egonet.wholenet.graph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import net.sf.functionalj.Function2;
import net.sf.functionalj.tuple.Pair;
import net.sf.functionalj.tuple.PairUni;

import org.egonet.exceptions.MissingPairException;
import org.egonet.wholenet.graph.WholeNetworkTie.DiscrepancyStrategy;
import org.egonet.wholenet.gui.NameMapperFrame.NameMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.endlessloopsoftware.egonet.Interview;
import com.endlessloopsoftware.egonet.Question;
import com.endlessloopsoftware.egonet.Shared;
import com.endlessloopsoftware.egonet.Study;
import com.google.common.collect.Sets;

/**
 * This class encapsulates everything necessary to compile and retain data from
 * a set of Egonet interviews into a whole network, including alter mappings.
 * 
 * @author Martin
 * 
 */
public class WholeNetwork {

	final private static Logger logger = LoggerFactory.getLogger(WholeNetwork.class);
	
	final private Study study;
	final private List<Interview> interviews;
	final private List<NameMapping> nameMap;
	
	// maps for fast access
	private Map<Integer,WholeNetworkAlter> wholeNetworkAlters;
	private Map<Pair<WholeNetworkAlter,WholeNetworkAlter>,WholeNetworkTie> wholeNetworkTies;

	private Settings settings;
	
	public WholeNetwork(Study study, List<Interview> interviews,
			List<NameMapping> nameMap, Settings settings, 
			Function2<Map<String,String>,Interview,Integer> getAlterAttributes) 
	{
		super();
		this.study = study;
		this.interviews = interviews;
		this.nameMap = nameMap;
		this.settings = settings;
		build(getAlterAttributes);
	}

	public static class Settings {
		public Integer inclusionThreshold = 1;
		public Boolean alwaysIncludeEgo = true;
		public DiscrepancyStrategy discrepancyStrategy = DiscrepancyStrategy.Majority;
	}
	
	public void build(Function2<Map<String,String>,Interview,Integer> getAlterAttributes) {
		wholeNetworkAlters = new HashMap<Integer,WholeNetworkAlter>();
		wholeNetworkTies = new HashMap<Pair<WholeNetworkAlter,WholeNetworkAlter>,WholeNetworkTie>();
		
		// add all alters
		for(NameMapping mapping : nameMap) {
			int group = mapping.getGroup();
			if(!wholeNetworkAlters.containsKey(group)) {
				wholeNetworkAlters.put(group, new WholeNetworkAlter(group));
			}
			
			WholeNetworkAlter alter = wholeNetworkAlters.get(group);
			alter.addOccurence(mapping);
		}
		
		// remove WholeNetworkAlters that are not mentioned in enough interviews
		Map<Integer,WholeNetworkAlter> remainingAlters = new HashMap<Integer,WholeNetworkAlter>();
		for(Entry<Integer,WholeNetworkAlter> entry : wholeNetworkAlters.entrySet()) {
			if(entry.getValue().getOccurences().size() < settings.inclusionThreshold) {
				if(settings.alwaysIncludeEgo) {
					boolean isEgo = false;
					for(NameMapping occurrence : entry.getValue().getOccurences()) {
						if(occurrence.getAlterNumber().equals(-1)) {
							isEgo = true;
						}
					}
					if(isEgo) {
						remainingAlters.put(entry.getKey(), entry.getValue());
					}
				}
			} else {
				// Include alter only if mentioned in enough interviews.
				remainingAlters.put(entry.getKey(), entry.getValue());
			}
		}
		wholeNetworkAlters = remainingAlters;
		
		// Set attributes for remaining alters
		for(WholeNetworkAlter wholeNetworkAlter : wholeNetworkAlters.values()) {
			for(NameMapping mapping : wholeNetworkAlter.getOccurences()) {
				wholeNetworkAlter.addAttributes(
						getAlterAttributes.call(
								mapping.getInterview(), 
								mapping.getAlterNumber()));
			}
		}
		
		for(Interview interview : interviews) {
			
			String [] thisInterviewAlterlist = interview.getAlterList();
			
			// tie the ego to all alters
			Pair<WholeNetworkAlter,NameMapping> ego = findAlter(interview, -1);
			if(ego != null) {
				for(int i = 0; i < interview.getAlterList().length; i++) {
					Pair<WholeNetworkAlter,NameMapping> alter = findAlter(interview, i);
					if(alter != null) {
						tie(ego, alter, interview.getName(), true, true);
					}
				}
			}

			// tie adjacent alters together
			Iterator<Long> questions = study.getQuestionOrder(Shared.QuestionType.ALTER_PAIR).iterator();
			while (questions.hasNext()) {
				Question q = study.getQuestion((Long) questions.next());
				if(q.determinesAdjacency()) {
					try {
						int [][] adj = interview.generateAdjacencyMatrix(q, false);
						//int [][] adjWeight = interview.generateAdjacencyMatrix(q, true);
						
						// loop through adj
						// if adj[i][j] == 1, thisInterviewAlters[i] && thisInterviewAlters[j] are adjacent in final matrix
		
						int alters = Math.min(adj.length,thisInterviewAlterlist.length);
						for(int i = 0; i < alters; i++) {
							for(int j = i+1; j < alters; j++) {
								boolean adjacent = adj[i][j] == 1;
								String alter1 = thisInterviewAlterlist[i];
								String alter2 = thisInterviewAlterlist[j];
								logger.debug(alter1 + "("+i+") and " + alter2 + "("+j+") are" +
										(adjacent ? " " : " not ")+"adjacent");
	
								// find whole network alters
								Pair<WholeNetworkAlter,NameMapping> wholeAlter1 = findAlter(interview, i);
								Pair<WholeNetworkAlter,NameMapping> wholeAlter2 = findAlter(interview, j);
	
								if(wholeAlter1 != null && wholeAlter2 != null) {
									if(wholeAlter1.getFirst().compareTo(wholeAlter2.getFirst()) > 0) {
										Pair<WholeNetworkAlter,NameMapping> swap = wholeAlter1;
										wholeAlter1 = wholeAlter2;
										wholeAlter2 = swap;
									}
	
									// TODO: strength of tie, even if not adjacent
									tie(wholeAlter1, wholeAlter2, interview.getName(), adjacent,false);
								}
							}
						}
					} catch (MissingPairException ex) {
						logger.error("Couldn't create adjacency matrix for question " + q, ex);
					}
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
		return null;
		//throw new IllegalArgumentException("Alter did not exist -- it must have been derived from somewhere, so we *must* find it");
	}

	public Map<Integer, WholeNetworkAlter> getWholeNetworkAlters() {
		return wholeNetworkAlters;
	}

	public Set<WholeNetworkTie> getWholeNetworkTies() {
		Set<WholeNetworkTie> ties = Sets.newHashSet();
		for(WholeNetworkTie tie : wholeNetworkTies.values()) {
			if(tie.isTied(settings.discrepancyStrategy)) {
				ties.add(tie);
			}
		}
		return ties;
	}
	
	public WholeNetworkTie getTie(WholeNetworkAlter alter1, WholeNetworkAlter alter2) {
		WholeNetworkTie tie1 = 
			wholeNetworkTies.get(new PairUni<WholeNetworkAlter>(alter1,alter2));
		if(tie1 != null) {
			return tie1;
		}
		WholeNetworkTie tie2 = 
			wholeNetworkTies.get(new PairUni<WholeNetworkAlter>(alter2,alter1));
		if(tie2 != null) {
			return tie2;
		}
		return null;
	}
	
	private void tie(Pair<WholeNetworkAlter,NameMapping> wholeAlter1, Pair<WholeNetworkAlter,NameMapping> wholeAlter2,
			String[] reporter, boolean isTied, boolean egoReportingOnSelf) 
	{
		
		Pair<WholeNetworkAlter, WholeNetworkAlter> tieKey = new Pair<WholeNetworkAlter,WholeNetworkAlter>(wholeAlter1.getFirst(), wholeAlter2.getFirst());
			
		if(!wholeNetworkTies.containsKey(tieKey)) {
			wholeNetworkTies.put(tieKey, new WholeNetworkTie(tieKey));
			logger.info("Saw new tie for first time: " + tieKey);
		}
		
		WholeNetworkTie tieEntry = wholeNetworkTies.get(tieKey);
		if(isTied && egoReportingOnSelf) {
			tieEntry.addEvidenceEgoSaysTied(reporter);
		} else {
			tieEntry.addEvidence(reporter, isTied);
		}
	}
	
	public Pair<String[],int[][]> getAdjacencyMatrix() {
		
		List<WholeNetworkAlter> alterList = 
			new ArrayList<WholeNetworkAlter>(wholeNetworkAlters.values());
		
		
		int size = alterList.size();
		
		String [] names = new String[size];
		for(int i = 0; i < names.length; i++) {
			names[i] = alterList.get(i).getOccurences().get(0).toString();
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
					adj[x][y] = tie.isTied(settings.discrepancyStrategy) ? 1 : 0;
				}
				else {
					adj[x][y] = 0;
				}
			}
		}
		
		return new Pair<String[],int[][]>(names,adj);
	}
}
