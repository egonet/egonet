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
package com.endlessloopsoftware.ego.client.graph;

import java.awt.*;
public class EdgeProperty extends GraphProperty{

	public static enum EdgeShape {Line, QuadCurve, CubicCurve}
	EdgeShape shape;
	
	public static enum EdgePropertyType {
		Color, Shape, Size, Label
	}
	
	private EdgePropertyType property = null;
	private boolean visible = false;
	
	public EdgeProperty()
	{
		this.size= 1;
		this.shape = EdgeShape.Line;
		this.color = Color.BLACK;
	}

	public EdgeProperty(String label, Color color, EdgeShape shape, int size) {
		this.label = label;
		this.color = color;
		this.shape = shape;
		this.size = size;
	}
	
	public EdgeProperty(Color color, EdgeShape shape, int size) {
		this.color = color;
		this.shape = shape;
		this.size = size;
	}
	
	public EdgeShape getShape() {
		return this.shape;
	}

	public void setShape(EdgeShape shape) {
		this.shape = shape;
	}
	
	public void setShapeFromString(String nodeShape){
		if(nodeShape.equalsIgnoreCase("Line")){
			this.shape = EdgeShape.Line;
		}else if(nodeShape.equalsIgnoreCase("QuadCurve")){
			this.shape = EdgeShape.QuadCurve;
		}else {
			this.shape = EdgeShape.CubicCurve;
		}
	}

	public String toString()
	{
		return "[edge property,type="+property.name()+",shape="+shape+",visible="+visible+"]";
	}
	
	public EdgePropertyType getProperty() {
		return property;
	}

	public void setProperty(EdgePropertyType property) {
		this.property = property;
	}

	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}
	
}
