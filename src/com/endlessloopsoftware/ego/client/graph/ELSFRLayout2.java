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
