package org.egonet.tests.graph

import java.util.Random

import org.egonet.tests.EgonetSpec
import org.egonet.graph.Graph
import scala.math.abs

class GraphTest extends EgonetSpec {

  val rand = new Random();
  
  	"testRandom" should "have correct size and density" in {
  	  
  	  for(i <- Range(0, 60)) {
			var expectedDensity = rand.nextDouble();
			
			var graph = Graph.random(i, expectedDensity);
			assert(i.intValue() == graph.nodes().size());
			
			var calculatedDensity = graph.density()
			if(i > 50) {
			  var diff = abs(calculatedDensity - expectedDensity)
			  
			  if(diff > 0.1)
			    assert(expectedDensity == calculatedDensity)
			}
  	    
  	  }
	}
  
}