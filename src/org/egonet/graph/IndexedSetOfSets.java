package org.egonet.graph;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

public class IndexedSetOfSets<N> implements Set<Set<N>> {

	private Set<Set<N>> set;
	private Map<N,Set<Set<N>>> index;
	
	public Set<Set<N>> findByIndex(N item) {
		Set<Set<N>> result = index.get(item);
		return result == null ? new HashSet<Set<N>>() : result;
	}
	
	public IndexedSetOfSets() {
		clear();
	}
	
	public boolean add(Set<N> item) {
		if(set.contains(item)) {
			return false;
		} else {
			set.add(item);
			for(N n : item) {
				Set<Set<N>> nIndex = index.get(n);
				if(nIndex == null) {
					nIndex = Sets.newHashSet();
					index.put(n, nIndex);
				}
				nIndex.add(item);
			}
			return true;
		}
	}

	public boolean addAll(Collection<? extends Set<N>> items) {
		boolean modified = false;
		for(Set<N> item : items) {
			if(add(item)) {
				modified = true;
			}
		}
		return modified;
	}

	public void clear() {
		set = Sets.newHashSet();
		index = Maps.newHashMap();
	}

	public boolean contains(Object item) {
		return set.contains(item);
	}

	public boolean containsAll(Collection<?> items) {
		for(Object item : items) {
			if(! contains(item)) {
				return false;
			}
		}
		return true;
	}

	public boolean isEmpty() {
		return set.isEmpty();
	}

	private class IndexedIterator implements Iterator<Set<N>> {

		private List<Set<N>> items;
		int i = -1;
		
		public IndexedIterator() {
			items = Lists.newArrayList(set);
		}
		
		public boolean hasNext() {
			return items.size() > i+1;
		}

		public Set<N> next() {
			i++;
			return items.get(i);
		}

		public void remove() {
			IndexedSetOfSets.this.remove(items.get(i));
		}
	}
	
	public Iterator<Set<N>> iterator() {
		return new IndexedIterator();
	}

	@SuppressWarnings("unchecked")
	public boolean remove(Object obj) {
		Set<N> item = (Set<N>) obj;
		boolean modified = set.remove(item);
		if(modified) {
			for(N n : item) {
				index.get(n).remove(item);
			}
		}
		return modified;
	}

	public boolean removeAll(Collection<?> items) {
		boolean modified = false;
		for(Object item : items) {
			if(remove(item)) {
				modified = true;
			}
		}
		return modified;
	}

	@SuppressWarnings("unchecked")
	public boolean retainAll(Collection<?> items) {
		Set<Set<N>> keepers = Sets.newHashSet();
		keepers.addAll((Collection<Set<N>>) items);
		boolean modified = false;
		Iterator<Set<N>> iter = iterator();
		while(iter.hasNext()) {
			Set<N> item = iter.next();
			if(! keepers.contains(item)) {
				remove(item);
			}
		}
		return modified;
	}

	public int size() {
		return set.size();
	}

	public Object[] toArray() {
		return set.toArray();
	}

	public <T> T[] toArray(T[] t) {
		return set.toArray(t);
	}

	public boolean equals(Object obj) {
		return set.equals(obj);
	}
	public int hashCode() {
		return set.hashCode();
	}
}
