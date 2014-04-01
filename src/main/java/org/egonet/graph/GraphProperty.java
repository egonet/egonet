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
package org.egonet.graph;

import java.awt.*;


public abstract class GraphProperty {
	
	Color color;
	int size;
	String label;
	boolean defaultSetting = true; 
	
	public GraphProperty()
	{
		color = Color.RED;
		size = 1;
		label = "";
	}
	
	public boolean isDefaultSetting() {
		return this.defaultSetting;
	}
	public void setDefaultSetting(boolean defaultSetting) {
		this.defaultSetting = defaultSetting;
	}
	public Color getColor() {
		return this.color;
	}
	public void setColor(Color color) {
		this.color = color;
	}
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	public int getSize() {
		return size;
	}
	public void setSize(int size) {
		this.size = size;
	}
	
	public String toString()
	{
		return "[color="+color+",size="+size+",label="+label+",default="+defaultSetting+"]";
	}
	
}
