package com.endlessloopsoftware.ego.client.graph;

import java.awt.*;

public class EdgeProperty extends GraphProperty{

	public static enum EdgeShape {Line, QuadCurve, CubicCurve}
	EdgeShape shape;
	
	public EdgeProperty()
	{
		this.size= 1;
		this.shape = EdgeShape.Line;
		this.color = Color.BLACK;
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
	
	
}
