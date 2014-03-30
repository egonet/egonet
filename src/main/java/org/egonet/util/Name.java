package org.egonet.util;

public class Name {
	private String first,last;
	public Name(String first, String last) {
		this.first = first;
		this.last = last;
	}
	public Name(String only) {
		this.first = only;
		this.last = null;
	}
	public String toString(String separator) {
		return
			(first == null ? "" : first) +
			(first == null || last == null ? "" : separator) +
			(last == null ? "" : last);
			
	}
	public String toString() {
		return toString(" ");
	}
}
