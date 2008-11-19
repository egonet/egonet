package org.egonet.util;

public class Pair<A extends Comparable<A>> {
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
		try {
			Pair<A> p = (Pair<A>) o;
			return p.first().equals(first()) && p.second().equals(second());
		} catch(ClassCastException ex) {
			return false;
		}
	}
	@Override
	public String toString() {
		return "Pair("+first()+","+second()+")";
	}
}

