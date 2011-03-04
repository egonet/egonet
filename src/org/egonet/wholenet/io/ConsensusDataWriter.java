package org.egonet.wholenet.io;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
	public ConsensusDataWriter(WholeNetwork net) {
		this.net = net;
	}
	public void writeToFile(File file) throws IOException {
		Set<WholeNetworkAlter> allAlters = 
			Sets.newHashSet(net.getWholeNetworkAlters().values());
		
		Integer numTies = 0;
		Integer numMixedTies = 0;
		Integer numYesOnlyTies = 0;
		Integer numNoOnlyTies = 0;
		Integer numUntied = 0;
		Integer numTiedWithoutVotes = 0;
		
		Map<WholeNetworkAlter,Set<String>> 
		reportersByAlter = Maps.newHashMap();
		Set<String> allReporters = Sets.newHashSet();
		for(WholeNetworkAlter alter1 : allAlters) {
			Set<String> alter1reporters = Sets.newHashSet();
			for(WholeNetworkAlter alter2 : allAlters) {
				if(! alter1.equals(alter2)) {
					WholeNetworkTie tie = net.getTie(alter1, alter2);
					if(tie == null) {
						numUntied++;
					} else {
						numTies++;
						Set<String> tieReporters = Sets.union(tie.tiedNo(), tie.tiedYes());
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
		
		Map<String,Set<WholeNetworkAlter>>
		altersByReporter = Maps.newHashMap();
		for(String reporter : allReporters) {
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
		for(String reporter : allReporters) {
			Set<Object> reporterConnections = Sets.newHashSet();
			for(String otherReporter : allReporters) {
				if(! reporter.equals(otherReporter)) {
					reporterConnections.add(otherReporter);
				}
			}
			reporterConnections.addAll(altersByReporter.get(reporter));
			alterAndReporterTwoMode.put(reporter, reporterConnections);
		}
		for(WholeNetworkAlter alter : allAlters) {
			Set<Object> alterConnections = Sets.newHashSet();
			for(WholeNetworkAlter otherAlter : allAlters) {
				if(! alter.equals(otherAlter)) {
					alterConnections.add(otherAlter);
				}
			}
			alterConnections.addAll(reportersByAlter.get(alter));
			alterAndReporterTwoMode.put(alter, alterConnections);
		}
		Set<Set<Object>> cliques = 
			(Set<Set<Object>>) new clj.graph.Core().cliques(alterAndReporterTwoMode);
		Set<String> bestReporters = Sets.newHashSet();
		Set<WholeNetworkAlter> bestAlters = Sets.newHashSet();
		Integer bestScore = 0;
		for(Set<Object> clique : cliques) {
			Set<String> reporters = Sets.newHashSet();
			Set<WholeNetworkAlter> alters = Sets.newHashSet();
			for(Object cliqueMember : clique) {
				if(cliqueMember instanceof String) {
					reporters.add((String) cliqueMember);
				} else if(cliqueMember instanceof WholeNetworkAlter) {
					alters.add((WholeNetworkAlter) cliqueMember);
				} else {
					throw new RuntimeException("This clique member is neither a reporter " +
							"nor an alter: "+cliqueMember+" ("+cliqueMember.getClass()+")");
				}
			}
			Integer numrep = reporters.size(), numalt = alters.size();
			Integer score = numrep*numalt*Math.min(numrep,numalt);
			if(score > bestScore) {
				logger.info("Found "+numrep+" reporters and "+numalt+" alters" +
						" for score of "+score);
				bestReporters = reporters;
				bestAlters = alters;
				bestScore = score;
			}
		}
		logger.info("Best score was "+bestScore);
		
		List<WholeNetworkAlter> alterList = Lists.newArrayList(bestAlters);
		List<String> reporterList = Lists.newArrayList(bestReporters);
		
		FileWriter fw = new FileWriter(file);
		CSVWriter csv = new CSVWriter(fw);
		
		List<String> header = Lists.newArrayList("");
		for(WholeNetworkAlter alter1 : alterList) {
			for(WholeNetworkAlter alter2 : alterList) {
				if(! alter1.equals(alter2)) {
					header.add(alter1+" - "+alter2);
				}
			}
		}
		csv.writeNext(header.toArray(new String[]{}));
		
		for(String reporter : reporterList) {
			List<String> line = Lists.newArrayList(reporter);
			for(WholeNetworkAlter alter1 : alterList) {
				for(WholeNetworkAlter alter2 : alterList) {
					if(! alter1.equals(alter2)) {
						line.add(
								net.getTie(alter1, alter2).tiedYes().contains(reporter)
									? "1" : "0");
					}
				}
			}
			csv.writeNext(line.toArray(new String[]{}));
		}

		csv.flush();
		fw.close();
	}
}
