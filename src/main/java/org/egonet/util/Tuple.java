package org.egonet.util;

public class Tuple<A extends Comparable<A>, B extends Comparable<B>> extends Object {
	private final A a;
	private final B b;
	public Tuple(A a, B b) {
		this.a = a;
		this.b = b;
	}
	public A first() {
		return a;
	}
	public B second() {
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
		
		if(!(o instanceof Tuple))
			return false;
		
		//@SuppressWarnings("unchecked")
		Tuple p = (Tuple)o;
		
		return p.first().equals(first()) && p.second().equals(second());
	}
	@Override
	public String toString() {
		return "Tuple("+first()+","+second()+")";
	}
}

