package org.egonet.model.alter;

import java.util.*;

/**
 * Integer values for a pair of alters. This class can be used to map any pair of alters to an integer.
 * @author Martin
 *
 */
public class AlterMatrix<T extends Comparable<T>> {
	
	private final Map<AlterPair,T> _matrix;
	
	public AlterMatrix() { 
		 this(10);
	}
	public AlterMatrix(int size) { 
		_matrix = new HashMap<AlterPair,T>(size);
	}
	public AlterMatrix(AlterMatrix<T> arg) { 
		this(arg.getAllAlterPairs().size());
		
		for(AlterPair ap : arg.getAllAlterPairs()) {
			T val = arg.get(ap);
			set(ap, val);
		}
	}

	
	public void set(Alter first, Alter second, T i) {
		_matrix.put(new AlterPair(first, second), i);
	}

	public void set(AlterPair pair, T i) {
		_matrix.put(pair, i);
	}
	
	public T get(AlterPair pair) {
		return _matrix.get(pair);
	}
	
	public T get(Alter first, Alter second) {
		return _matrix.get(new AlterPair(first,second));
	}
	
	public List<AlterPair> getAllAlterPairs() {
		return new ArrayList<AlterPair>(_matrix.keySet());
	}
	
	public Set<Alter> getAllAlters() {
		Set<AlterPair> keys = _matrix.keySet();
		HashSet<Alter> s = new HashSet<Alter>(keys.size()*2);
		
		for(AlterPair p : keys) {
			s.addAll(p.getAlters());
		}
		
		
		return s;
	}
	
	@Deprecated
	public boolean contains(AlterPair pair) {
		return _matrix.containsKey(pair);
	}
	
	@Deprecated
	public boolean contains(Alter first, Alter second) {
		return _matrix.containsKey(new AlterPair(first, second));
	}
	
	public void clear() {
		_matrix.clear();
	}
}
