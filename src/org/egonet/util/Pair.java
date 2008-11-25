package org.egonet.util;

public class Pair<A extends Comparable<A>> extends Object {
	private final A a;
	private final A b;
	public Pair(A a, A b) {
		
		boolean reversed = a.compareTo(b) > 0;
		this.a = reversed ? b : a;
		this.b = reversed ? a : b;
	}
	public A first() {
		return a;
	}
	public A second() {
		return b;
	}
	@Override
	public int hashCode() {
		return 13*a.hashCode() + 27*b.hashCode();
	}
	@Override
	public boolean equals(Object o) {
		// we don't really need generics in this implementation, since we delegate to A#equals which
		// always exists because the type A is always somehow a subtype of Object which has #equals.
		
		if(!(o instanceof Pair))
			return false;
		
		@SuppressWarnings("unchecked")
		Pair p = (Pair)o;
		
		return p.first().equals(first()) && p.second().equals(second());
	}
	@Override
	public String toString() {
		return "Pair("+first()+","+second()+")";
	}
}

