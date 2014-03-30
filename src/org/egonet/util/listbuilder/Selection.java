/***
 * Copyright (c) 2008, Endless Loop Software, Inc.
 * 
 * This file is part of EgoNet.
 * 
 * EgoNet is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 * 
 * EgoNet is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.egonet.util.listbuilder;

public class Selection {
	private String string;

	private int index;

	private int value;

	private boolean adjacent;

	public Selection() {
		this.string = "";
		this.value = -1;
		this.index = -1;
		this.adjacent = false;
	}
	
	public Selection(String str) {
		this.string = str;
		this.value = -1;
		this.index = -1;
		this.adjacent = false;
	}

	public Selection(String string, int value, int index, boolean adjacent) {
		this.string = string;
		this.value = value;
		this.index = index;
		this.adjacent = adjacent;
	}

	public String toString() {
		// return "[Selection \"" + string + "\", index = "+index+", value =
		// "+value+", adjacent = "+adjacent+"]";
		return string;
	}

	public boolean isAdjacent() {
		return adjacent;
	}

	public void setAdjacent(boolean adjacent) {
		this.adjacent = adjacent;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public String getString() {
		return string;
	}

	public void setString(String string) {
		this.string = string;
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}

	public boolean equals(Selection selection) {
		if (this.adjacent == selection.adjacent
				&& this.index == selection.index
				&& this.string.equals(selection.string)
				&& this.value == selection.value) {
			return true;
		}
		return false;
	}
	
	public int hashCode()
	{
		return this.index;
	}
}
