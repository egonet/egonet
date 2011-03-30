package org.egonet.graph;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

public class Graph<N> {
	
	private Map<N,Set<N>> connections;
	
	public Graph() {
		this(new HashMap<N,Set<N>>());
	}
	public Graph(Map<N,Set<N>> connections) {
		this.connections = new HashMap<N,Set<N>>();
		for(N node : connections.keySet()) {
			Set<N> set = Sets.newHashSet();
			set.addAll(connections.get(node));
			this.connections.put(node, set);
		}
	}
	public static Graph<Integer> random(Integer numNodes, Double density) {
		Random rand = new Random();
		Map<Integer,Set<Integer>> connections = Maps.newHashMap();
		List<Integer> nodes = Lists.newArrayList();
		for(Integer i = 0; i < numNodes; i++) {
			nodes.add(i);
		}
		for(Integer i = 0; i < numNodes; i++) {
			Collections.shuffle(nodes,rand);
			Integer expectedConnections = (int) Math.round(density * (numNodes - 1));
			Integer spread = Math.min(expectedConnections, numNodes-1-expectedConnections);
			Integer actual = rand.nextInt(2*spread+1) + expectedConnections - spread;
			Set<Integer> connectionsForThisNode = Sets.newHashSet();
			for(Integer j = 0; j < actual && j < nodes.size(); j++) {
				if(nodes.get(j).equals(j)) {
					actual++;
				} else {
					connectionsForThisNode.add(nodes.get(j));
				}
			}
			connections.put(i, connectionsForThisNode);
		}
		return new Graph<Integer>(connections);
	}
	public Double density() {
		Integer numNodes = connections.keySet().size();
		if(numNodes < 2) {
			return 1.0;
		}
		Integer maxConnectionsTimesTwo = numNodes * (numNodes-1);
		Integer actualConnectionsTimesTwo = 0;
		for(N node : connections.keySet()) {
			actualConnectionsTimesTwo += connections.get(node).size();
		}
		return actualConnectionsTimesTwo < 1 ? 0.0 :
			(actualConnectionsTimesTwo * 1.0 / maxConnectionsTimesTwo);
	}
	public Set<N> nodes() {
		return connections.keySet();
	}
	public boolean connected(N n1, N n2) {
		return connections.get(n1).contains(n2);
	}
	// TODO: Double distance(N n1, N n2)
	public boolean equals(Object o) {
		if(o == null || ! (o instanceof Graph)) {
			return false;
		}
		return connections.equals(((Graph) o).connections);
	}
	public int hashCode() {
		return connections.hashCode();
	}
}
