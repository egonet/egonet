package org.egonet.model;

import java.util.HashSet;
import java.util.Set;

public class AlterPair {
	private Alter first, second;

	public AlterPair(Alter first, Alter second) {

		// ensure these come sorted correctly
		if(first.compareTo(second) < 0) {
			this.first = first;
			this.second = second;
		}
		else {
			this.first = second;
			this.second = first;
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((first == null) ? 0 : first.hashCode());
		result = prime * result + ((second == null) ? 0 : second.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AlterPair other = (AlterPair) obj;
		if (first == null) {
			if (other.first != null)
				return false;
		} else if (!first.equals(other.first))
			return false;
		if (second == null) {
			if (other.second != null)
				return false;
		} else if (!second.equals(other.second))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "AlterPair [first=" + first + ", second=" + second + "]";
	}


	public Set<Alter> getAlters() {
		// separate unordered list (force that with set)
		HashSet<Alter> r = new HashSet<Alter>();
		r.add(first); r.add(second);

		return r;
	}
}
