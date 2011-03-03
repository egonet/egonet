package org.egonet.wholenet.graph;

import net.sf.functionalj.tuple.Pair;

public class WholeNetworkTie {

	private final WholeNetworkAlter a;
	private final WholeNetworkAlter b;
	
	public WholeNetworkTie(Pair<WholeNetworkAlter,WholeNetworkAlter> tie) {
		this(tie.getFirst(), tie.getSecond());
	}
	
	public WholeNetworkTie(WholeNetworkAlter a, WholeNetworkAlter b) {
		super();
		this.a = a.compareTo(b) < 0 ? a : b;
		this.b = a.compareTo(b) < 0 ? b : a;
	}
	
	private int tiedYes = 0;
	private int tiedNo = 0;
	
	public void addEvidence(boolean isTied) {
		if(isTied) {
			tiedYes++;
		} else {
			tiedNo++;
		}
	}
	
	private boolean egoSaysTied = false;
	
	public void addEvidenceEgoSaysTied() {
		egoSaysTied = true;
		tiedYes++;
	}
	
	public boolean isTied(DiscrepancyStrategy strategy) {
		if(strategy.equals(DiscrepancyStrategy.Maximum)) {
			return tiedYes > 0;
		} else if(strategy.equals(DiscrepancyStrategy.Majority)) {
			return egoSaysTied || tiedYes > tiedNo;
		} else if(strategy.equals(DiscrepancyStrategy.Minimum)) {
			return tiedYes > 0 && tiedNo < 1;
		} else if(strategy.equals(DiscrepancyStrategy.EgoAlterTiesOnly)) {
			return egoSaysTied;
		} else {
			throw new RuntimeException("Unrecognized DiscrepancyStrategy: "+strategy);
		}
	}
	
	public static enum DiscrepancyStrategy {
		Maximum("tie if any interviews say to tie"),
		Majority("tie if more interviews say to tie than not to tie, ego vote overrides"),
		Minimum("tie only when an interview says to tie and no interviews say not to tie"),
		EgoAlterTiesOnly("ignore alter pair tie question");
		
		private final String description;
		DiscrepancyStrategy(String description) {
			this.description = description;
		}
		public String getDescription() {
			return description;
		}
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((a == null) ? 0 : a.hashCode());
		result = prime * result + ((b == null) ? 0 : b.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof WholeNetworkTie))
			return false;
		WholeNetworkTie other = (WholeNetworkTie) obj;
		if (a == null) {
			if (other.a != null)
				return false;
		} else if (!a.equals(other.a))
			return false;
		if (b == null) {
			if (other.b != null)
				return false;
		} else if (!b.equals(other.b))
			return false;
		return true;
	}

	public WholeNetworkAlter getA() {
		return a;
	}

	public WholeNetworkAlter getB() {
		return b;
	}
}
