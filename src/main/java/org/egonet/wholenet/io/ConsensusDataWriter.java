package org.egonet.wholenet.io;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.egonet.graph.KPlexesTwoMode;
import org.egonet.wholenet.graph.WholeNetwork;
import org.egonet.wholenet.graph.WholeNetworkAlter;
import org.egonet.wholenet.graph.WholeNetworkTie;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.com.bytecode.opencsv.CSVWriter;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

public class ConsensusDataWriter {
	
	final private static Logger logger = LoggerFactory.getLogger(ConsensusDataWriter.class);
	
	WholeNetwork net;
	Integer allowedMissingEdgesPerNode;
	public ConsensusDataWriter(WholeNetwork net, Integer allowedMissingEdgesPerNode) {
		this.net = net;
		this.allowedMissingEdgesPerNode = allowedMissingEdgesPerNode;
	}
	@SuppressWarnings("unchecked")
	public String writeToFile(File file) throws IOException {
		Set<WholeNetworkAlter> allAlters = 
			Sets.newHashSet(net.getWholeNetworkAlters().values());
		
		Integer numTies = 0;
		Integer numMixedTies = 0;
		Integer numYesOnlyTies = 0;
		Integer numNoOnlyTies = 0;
		Integer numUntied = 0;
		Integer numTiedWithoutVotes = 0;
		
		Map<WholeNetworkAlter,Set<Integer>> 
		reportersByAlter = Maps.newHashMap();
		Set<Integer> allReporters = Sets.newHashSet();
		for(WholeNetworkAlter alter1 : allAlters) {
			Set<Integer> alter1reporters = Sets.newHashSet();
			for(WholeNetworkAlter alter2 : allAlters) {
				if(! alter1.equals(alter2)) {
					WholeNetworkTie tie = net.getTie(alter1, alter2);
					if(tie == null) {
						numUntied++;
					} else {
						numTies++;
						Set<Integer> tieReporters = Sets.union(tie.tiedNo(), tie.tiedYes());
						alter1reporters.addAll(tieReporters);
						if(tieReporters.isEmpty()) {
							numTiedWithoutVotes++;
						} else if(tie.tiedNo().isEmpty()) {
							numYesOnlyTies++;
						} else if(tie.tiedYes().isEmpty()) {
							numNoOnlyTies++;
						} else {
							numMixedTies++;
						}
					}
				}
			}
			reportersByAlter.put(alter1, alter1reporters);
			allReporters.addAll(alter1reporters);
		}
		logger.info("NumTies: "+numTies+", NumMixed: "+numMixedTies+
				", NumYesOnly: "+numYesOnlyTies+", NumNoOnly: "+numNoOnlyTies+
				", NumUntied: "+numUntied+", NumTiedWithoutVotes: "+numTiedWithoutVotes);
		
		Map<Integer,Set<WholeNetworkAlter>>
		altersByReporter = Maps.newHashMap();
		for(Integer reporter : allReporters) {
			Set<WholeNetworkAlter> reporterAlters = Sets.newHashSet();
			for(WholeNetworkAlter alter : allAlters) {
				if(reportersByAlter.get(alter).contains(reporter)) {
					reporterAlters.add(alter);
				}
			}
			altersByReporter.put(reporter, reporterAlters);
		}
		
		// Construct two mode network of alters and reporters and look for cliques,
		// which are subnetworks in which all reporters can report on all alters.
		// Add connections for 100% density within modes so that I can use a
		// one mode clique finding algorithm.
		Map<Object,Set> alterAndReporterTwoMode = Maps.newHashMap();
		for(Integer reporter : allReporters) {
			Set<Object> reporterConnections = Sets.newHashSet();
			reporterConnections.addAll(altersByReporter.get(reporter));
			alterAndReporterTwoMode.put(reporter, reporterConnections);
		}
		for(WholeNetworkAlter alter : allAlters) {
			Set<Object> alterConnections = Sets.newHashSet();
			alterConnections.addAll(reportersByAlter.get(alter));
			alterAndReporterTwoMode.put(alter, alterConnections);
		}
		Set<Object> kplex = new KPlexesTwoMode().findLargeKPlex(alterAndReporterTwoMode, allReporters, allowedMissingEdgesPerNode);
		Set<Integer> kplexReporters = Sets.intersection(allReporters, kplex);
		Set<WholeNetworkAlter> kplexAlters = Sets.intersection(allAlters, kplex);
		
		List<WholeNetworkAlter> alterList = Lists.newArrayList(kplexAlters);
		List<Integer> reporterList = Lists.newArrayList(kplexReporters);
		
		FileWriter fw = new FileWriter(file);
		CSVWriter csv = new CSVWriter(fw);
		
		List<String> header = Lists.newArrayList("");
		for(int i = 0; i < alterList.size(); i++) {
			WholeNetworkAlter alter1 = alterList.get(i);
			for(int j = i+1; j < alterList.size(); j++) {
				WholeNetworkAlter alter2 = alterList.get(j);
				if(! alter1.equals(alter2)) {
					header.add(alter1+" - "+alter2);
				}
			}
		}
		csv.writeNext(header.toArray(new String[]{}));
		
		Integer dataYes = 0, dataNo = 0, guessYes = 0, guessNo = 0;
		Integer guessWithKnownAlters = 0;
		Integer guessWithUnknownAlters = 0;
		Random rand = new Random();
		
		for(Integer reporter : reporterList) {
			List<String> line = Lists.newArrayList(reporter+"");
			for(int i = 0; i < alterList.size(); i++) {
				WholeNetworkAlter alter1 = alterList.get(i);
				for(int j = i+1; j < alterList.size(); j++) {
					WholeNetworkAlter alter2 = alterList.get(j);
					WholeNetworkTie tie = net.getTie(alter1, alter2);
					if(alterAndReporterTwoMode.get(reporter).contains(alter1) &&
							alterAndReporterTwoMode.get(reporter).contains(alter2))
					{
						if(alter1.getId().equals(reporter) ||
								alter2.getId().equals(reporter))
						{ // Tie between ego and alter
							line.add("1");
							dataYes++;
						} else if(tie != null && tie.tiedYes().contains(reporter)) {
							line.add("1");
							dataYes++;
						} else if(tie != null && tie.tiedNo().contains(reporter)) {
							line.add("0");
							dataNo++;
						} else {
							guessWithKnownAlters++;
							System.out.println(reporter+" knows "+alter1+" and "+alter2+
									" but didn't say whether they are linked.");
						}
					} else {
						if(rand.nextInt(2) > 0) {
							line.add("1");
							guessYes++;
						} else {
							line.add("0");
							guessNo++;
						}
						guessWithUnknownAlters++;
					}
				}
			}
			csv.writeNext(line.toArray(new String[]{}));
		}
		Integer reportedAnswers = dataYes+dataNo;
		Integer imputedAnswers = guessYes+guessNo;
		Double portionImputed = imputedAnswers*1.0/(reportedAnswers+imputedAnswers);
		String msg = 
			"Allowed max missing per node: "+allowedMissingEdgesPerNode+
			"\nActual max missing per node: "+
			new KPlexesTwoMode().maxMissingEdgesPerNodeInSubgroup(alterAndReporterTwoMode, allReporters, kplex)+
			"\nReporters: "+reporterList.size()+
			"\nAlters: "+alterList.size()+
			"\nReported answers: "+reportedAnswers+
			"\nImputed answers: "+imputedAnswers+
			"\nPercent imputed: "+((int) (portionImputed*100))+"%"+
			"\nReporter didn't know one or both alters: "+guessWithUnknownAlters+
			"\nReporter knew alters, but I couldn't find answer: "+guessWithKnownAlters;
		System.out.println(msg);

		csv.flush();
		fw.close();
		return msg;
	}
}
