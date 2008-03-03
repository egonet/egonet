package com.endlessloopsoftware.ego.client.graph;

import java.awt.Shape;
import edu.uci.ics.jung.graph.Vertex;
import edu.uci.ics.jung.graph.decorators.*;

public class EllipseVertexShapeFunction extends AbstractVertexShapeFunction
{
	public EllipseVertexShapeFunction() 
    {
    	this.setSizeFunction(new ConstantVertexSizeFunction(15));
    }
    public EllipseVertexShapeFunction(VertexSizeFunction vsf, VertexAspectRatioFunction varf)
    {
        super(vsf, varf);
    }
    public Shape getShape(Vertex v, int size)
    {
    	this.setSizeFunction(new ConstantVertexSizeFunction(size));
    	return getShape(v);
    }
    public Shape getShape(Vertex v)
    {
        return factory.getEllipse(v);
    }
    public Shape getShape(Vertex v, NodeProperty.NodeShape type, int size){
    	this.setSizeFunction(new ConstantVertexSizeFunction(size));
    	if(type == NodeProperty.NodeShape.Star) {
    		return factory.getRegularStar(v, 5);
    	}
    	else {
    		return factory.getRoundRectangle(v);
    	}
    }
}