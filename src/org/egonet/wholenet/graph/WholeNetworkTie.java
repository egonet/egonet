package org.egonet.wholenet.graph;

import java.util.ArrayList;
import java.util.List;


import net.sf.functionalj.tuple.Pair;
import net.sf.functionalj.tuple.Triple;

import org.egonet.wholenet.gui.NameMapperFrame.NameMapping;

import com.endlessloopsoftware.egonet.Question;

public class WholeNetworkTie {

	private final WholeNetworkAlter a;
	private final WholeNetworkAlter b;
	private final List<Triple<NameMapping,NameMapping,Question>> ties;
	
	public WholeNetworkTie(Pair<WholeNetworkAlter,WholeNetworkAlter> tie) {
		this(tie.getFirst(), tie.getSecond());
	}
	
	public WholeNetworkTie(WholeNetworkAlter a, WholeNetworkAlter b) {
		super();
		this.a = a.compareTo(b) < 0 ? a : b;
		this.b = a.compareTo(b) < 0 ? b : a;
		ties = new ArrayList<Triple<NameMapping,NameMapping,Question>>();
	}
	
	public void addTie(NameMapping x,NameMapping y,Question q) {
		if(x.compareTo(y) < 0)
			addTie(new Triple<NameMapping,NameMapping,Question>(x,y,q));
		else
			addTie(new Triple<NameMapping,NameMapping,Question>(y,x,q));
	}
	
	private void addTie(Triple<NameMapping,NameMapping,Question> o) {
		if(!ties.contains(o))
			ties.add(o);
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
