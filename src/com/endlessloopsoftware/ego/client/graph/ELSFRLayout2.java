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

import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.Vertex;
import edu.uci.ics.jung.visualization.Coordinates;
import edu.uci.ics.jung.visualization.FRLayout;

public class ELSFRLayout2 extends FRLayout {
    public ELSFRLayout2(Graph g) {
        super(g);
    }
    
    private int xIsolate=25,yIsolate=0;
	protected void initializeLocation(Vertex v, Coordinates coord, Dimension d) {
		super.initializeLocation(v, coord, d);
		if (v.getIncidentEdges().size() == 0) {
			//System.out.println("BEFORE: v="+v+",coord="+coord+",d="+d+",x="+xIsolate+",y="+yIsolate);
			lockVertex(v);
		    
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
		    
		    coord.setLocation(xIsolate,yIsolate);
		}
	}
}
