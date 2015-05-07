package org.egonet.graph;

import static org.junit.Assert.*;

import java.util.Random;

import org.egonet.graph.Graph;
import org.junit.*;
import junit.framework.JUnit4TestAdapter;

public class GraphTest {

	private Random rand = new Random();
	
	@Test
	public void testRandom() {
		for(Integer i = 0; i < 60; i++) {
			Double expectedDensity = rand.nextDouble();
			Graph graph = Graph.random(i, expectedDensity);
			
			assertEquals("Random graph of "+i+" nodes actually has "+i+" nodes.",
					i.intValue(),graph.nodes().size());
			Double calculatedDensity = graph.density();
			if(i > 50) {
				assertEquals("Random graph with "+i+" nodes has requested density.",
						expectedDensity,calculatedDensity,0.1);
			}
		}
	}

	public static junit.framework.Test suite() {
      return new JUnit4TestAdapter(GraphTest.class);
	}
}
