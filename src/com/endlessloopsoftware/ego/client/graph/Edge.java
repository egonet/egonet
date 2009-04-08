package com.endlessloopsoftware.ego.client.graph;

import edu.uci.ics.jung.graph.util.Pair;

public class Edge {

	public final Pair<String> pair;

	public Edge(String a, String b) {
		super();
		this.pair = buildEdge(a,b);
	}
	
	public Edge(Vertex a, Vertex b) {
		super();
		this.pair = buildEdge(a.name,b.name);
	}
	
	// keep them ordered for edge identity
	public static Pair<String> buildEdge(String a, String b) {
		return a.compareTo(b) > 0 ? new Pair<String>(b,a) : new Pair<String>(a,b); 
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((pair == null) ? 0 : pair.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof Edge))
			return false;
		Edge other = (Edge) obj;
		if (pair == null) {
			if (other.pair != null)
				return false;
		} else if (!pair.equals(other.pair))
			return false;
		return true;
	}
	
	public Pair<String> getEndpoints() {
		return pair;
	}

}
