package org.egonet.wholenet.io;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Set;

import org.egonet.wholenet.graph.WholeNetwork;
import org.egonet.wholenet.graph.WholeNetworkAlter;
import org.egonet.wholenet.graph.WholeNetworkTie;

import au.com.bytecode.opencsv.CSVWriter;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

public class ConsensusDataWriter {
	WholeNetwork net;
	public ConsensusDataWriter(WholeNetwork net) {
		this.net = net;
	}
	public void writeToFile(File file) throws IOException {
		Set<WholeNetworkAlter> alterSet = 
			Sets.newHashSet(net.getWholeNetworkAlters().values());
		Set<String> eligibleReporters = null;
		for(WholeNetworkAlter alter1 : alterSet) {
			for(WholeNetworkAlter alter2 : alterSet) {
				if(! alter1.equals(alter2)) {
					WholeNetworkTie tie = net.getTie(alter1, alter2);
					if(tie == null) {
						// no one reported on this tie? Then there are no eligible reporters.
						eligibleReporters = Sets.newHashSet();
					} else {
						Set<String> tieReporters =
							Sets.union(tie.tiedNo(), tie.tiedYes());
						if(eligibleReporters == null) {
							eligibleReporters = tieReporters;
						} else {
							eligibleReporters = 
								Sets.intersection(eligibleReporters, tieReporters);
						}
					}
				}
			}
		}
		List<WholeNetworkAlter> alterList = Lists.newArrayList(alterSet);
		if(eligibleReporters == null) {
			eligibleReporters = Sets.newHashSet();
		}
		List<String> reporterList = Lists.newArrayList(eligibleReporters);
		
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
