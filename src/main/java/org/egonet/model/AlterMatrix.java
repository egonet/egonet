package org.egonet.model;

import java.util.*;

/**
 * Integer values for a pair of alters. This class can be used to map any pair of alters to an integer.
 * @author Martin
 *
 */
public class AlterMatrix<T extends Comparable<T>> {

	private final Map<AlterPair,T> _edgelist;

	public AlterMatrix() {
		 this(10);
	}
	public AlterMatrix(int size) {
		_edgelist = new HashMap<AlterPair,T>(size);
	}
	public AlterMatrix(AlterMatrix<T> arg) {
		this(arg.getAllAlterPairs().size());

		for(AlterPair ap : arg.getAllAlterPairs()) {
			T val = arg.get(ap);
			set(ap, val);
		}
	}


	public void set(Alter first, Alter second, T i) {
		_edgelist.put(new AlterPair(first, second), i);
	}

	public void set(AlterPair pair, T i) {
		_edgelist.put(pair, i);
	}

	public T get(AlterPair pair) {
		return _edgelist.get(pair);
	}

	public T get(Alter first, Alter second) {
		return _edgelist.get(new AlterPair(first,second));
	}

	public List<AlterPair> getAllAlterPairs() {
		return new ArrayList<AlterPair>(_edgelist.keySet());
	}

	public Set<Alter> getAllAlters() {
		Set<AlterPair> keys = _edgelist.keySet();
		HashSet<Alter> s = new HashSet<Alter>(keys.size()*2);

		for(AlterPair p : keys) {
			s.addAll(p.getAlters());
		}


		return s;
	}

	@Deprecated
	public boolean contains(AlterPair pair) {
		return _edgelist.containsKey(pair);
	}

	@Deprecated
	public boolean contains(Alter first, Alter second) {
		return _edgelist.containsKey(new AlterPair(first, second));
	}

	public void clear() {
		_edgelist.clear();
	}
}
