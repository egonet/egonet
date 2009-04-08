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

import java.awt.Dimension;
import java.awt.geom.Point2D;
import java.util.Date;
import java.util.Random;

import org.apache.commons.collections15.Transformer;
import edu.uci.ics.jung.algorithms.layout.FRLayout;
import edu.uci.ics.jung.graph.Graph;


public class ELSFRLayout2 extends FRLayout<Vertex,Edge> {
    public ELSFRLayout2(Graph<Vertex,Edge> g) {
        super(g);
    }
    
	public class RandomLocationTransformer implements Transformer<Vertex,Point2D> {

		Dimension d;
		Random random;
		Graph<Vertex,?> graph; 
	    
	    public RandomLocationTransformer(final Graph<Vertex,?> graph, final Dimension d) {
	    	this(graph,d, new Date().getTime());
	    }
	    
	    public RandomLocationTransformer(final Graph<Vertex,?> graph, final Dimension d, long seed) {
	    	this.d = d;
	    	this.random = new Random(seed);
	    	this.graph=graph;
	    }
	    
	    private int xIsolate=25,yIsolate=0;
	    public Point2D transform(Vertex v) {
			if (graph.getIncidentEdges(v).size() == 0) {
			    lock(v,true);
				
				// can expand to the right
			    if(yIsolate+25 > d.height && xIsolate+25 <= d.width)
			    {
			    	// hit bottom, new column
			    	yIsolate=0;
			    	xIsolate+=25;
			    }
			    // can't expand down or to the right
			    else if(yIsolate+25 > d.height && xIsolate+25 > d.width)
			    {
			    	// reset entire thing
			    	yIsolate=0;
			    	xIsolate=0;
			    }
			    // just go downward first
			    else
			    {
			    	yIsolate+=25;
			    }
			    
			    return new Point2D.Double(xIsolate, yIsolate);
			}
			else {
				return new Point2D.Double(random.nextDouble() * d.width, random.nextDouble() * d.height);	
			}
	    }
	}
	
	@Override
	public void setSize(Dimension size) {
		if(initialized == false) {
			super.setSize(size);
			setInitializer(new RandomLocationTransformer(super.getGraph(), size));
		} 
		else {
			super.setSize(size);
		}
	}
}
