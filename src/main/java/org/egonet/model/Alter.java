package org.egonet.model;

import java.util.Comparator;
import java.util.UUID;

public class Alter implements Comparable<Alter> {

	/**
	 * Using UUID instead of a String allows us to rename alters without losing
	 * alter data. It also gives us a nice way to check equality and compare
	 * values without worry about comparing Strings. This leads to a
	 * counter-intuitive sort order, but we'll provide a comparator for that.
	 */
	private String name;
	private final UUID _id;

	public Alter(String name) {
		super();
		this.name = name;
		_id = UUID.randomUUID();
	}

	public Alter(String name, UUID id) {
		super();
		this.name = name;
		_id = id;
	}

	public Alter(String name, String id) {
		super();
		this.name = name;
		_id = UUID.fromString(id);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((_id == null) ? 0 : _id.hashCode());
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
		Alter other = (Alter) obj;

		return _id.equals(other._id);
	}

	@Override
	public String toString() {
		return "Alter [name=" + name + "]";
	}

	@Override
	public int compareTo(Alter arg0) {
		UUID first = this._id;
		UUID second = arg0._id;

		return first.compareTo(second);
	}

	public class AlterNameComparator implements Comparator<Alter> {

		@Override
		public int compare(Alter arg0, Alter arg1) {
			String first = arg0.name;
			String second = arg1.name;

			return first.compareTo(second);
		}

	}

	public String getName() {
		return name;
	}
}
