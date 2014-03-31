package org.egonet.tests.graph
import java.util.Random

class GraphTest extends EgonetSpec {

  val rand = new Random();
  
  	"testRandom" should "have correct size and density" in {
  	  
  	  for(i <- Range(0, 60)) {
			var expectedDensity = rand.nextDouble();
			
			var graph = Graph.random(i, expectedDensity);
			assert(i.intValue() == graph.nodes().size());
			
			var calculatedDensity = graph.density()
			if(i > 50) {
				assert(round(100*expectedDensity)/100 == round(100*calculatedDensity)/100);
			}
  	    
  	  }
	}
  
}