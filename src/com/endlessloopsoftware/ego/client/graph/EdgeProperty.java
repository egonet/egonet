package com.endlessloopsoftware.ego.client.graph;

import java.awt.*;

public class EdgeProperty extends GraphProperty{

	public static enum EdgeShape {Line, QuadCurve, CubicCurve}
	EdgeShape shape;
	
	public static enum Property {
		Color, Shape, Size, Label
	}
	
	private Property property = null;
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
	
	public String toString()
	{
		String str;
		str = this.shape.toString() + " " + this.color.toString() + " " + this.size;
		return str;
	}
	
	public Property getProperty() {
		return property;
	}

	public void setProperty(Property property) {
		this.property = property;
	}

	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}
	
}
