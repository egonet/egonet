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

import org.apache.commons.collections15.functors.ConstantTransformer;

import edu.uci.ics.jung.visualization.util.VertexShapeFactory;

@SuppressWarnings("unchecked")
public class EllipseVertexShapeFunction extends VertexShapeFactory<Vertex>
{
	public EllipseVertexShapeFunction() 
    {
    	super(new ConstantTransformer(15), new ConstantTransformer(1.0f));
    }
    public Shape getShape(Vertex v, int size)
    {
    	vsf = new ConstantTransformer(size);
    	return getShape(v);
    }
    public Shape getShape(Vertex v)
    {
        return getEllipse(v);
    }
    public Shape getShape(Vertex v, NodeProperty.NodeShape type, int size){
    	vsf = new ConstantTransformer(size);
    	if(type == NodeProperty.NodeShape.Star) {
    		return getRegularStar(v, 5);
    	}
    	else {
    		return getRoundRectangle(v);
    	}
    }
}