package com.endlessloopsoftware.ego.client.graph;

import java.awt.Shape;
import edu.uci.ics.jung.graph.Vertex;
import edu.uci.ics.jung.graph.decorators.*;

public class PolygonVertexShapeFunction extends AbstractVertexShapeFunction
{
	private int numberOfEdges;
    public PolygonVertexShapeFunction() 
    {
    	this.setSizeFunction(new ConstantVertexSizeFunction(15));
    }
 
    public Shape getShape(Vertex v, int size, int numberOfEdges)
    {
    	this.numberOfEdges= numberOfEdges;
    	this.setSizeFunction(new ConstantVertexSizeFunction(size));
    	return getShape(v);
    }
    
    public Shape getShape(Vertex v)
    {
    	return factory.getRegularPolygon(v,numberOfEdges);
    }
}
