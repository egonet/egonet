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
