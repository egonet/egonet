package org.egonet.test.graph;

import static org.junit.Assert.*;

import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.egonet.graph.KPlexes;
import org.junit.*;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import junit.framework.JUnit4TestAdapter;

public class KPlexesTest {

	protected Random rand = new Random();
	private KPlexes<Integer> kp = new KPlexes<Integer>();
	
	private Map<Integer,Set<Integer>> graphWithThreeCliques() {
		Map<Integer,Set<Integer>> res = Maps.newHashMap();
		Set<Integer> one = Sets.newHashSet(2,3,8);
		Set<Integer> two = Sets.newHashSet(1,3,4);
		Set<Integer> three = Sets.newHashSet(1,2,4,5);
		Set<Integer> four = Sets.newHashSet(2,3);
		Set<Integer> five = Sets.newHashSet(3,6,7);
		Set<Integer> six = Sets.newHashSet(5,7);
		Set<Integer> seven = Sets.newHashSet(5,6);
		Set<Integer> eight = Sets.newHashSet(1,9);
		Set<Integer> nine = Sets.newHashSet(8);
		res.put(1,one);
		res.put(2,two);
		res.put(3,three);
		res.put(4,four);
		res.put(5,five);
		res.put(6,six);
		res.put(7,seven);
		res.put(8,eight);
		res.put(9,nine);
		return res;
	}
	
	private Set<Integer> intSet(Integer... members) {
		return Sets.newHashSet(members);
	}

	@Test
	public void testConnectionsByNode() {
		Map<Integer,Integer> calculations = 
			kp.connectionsByNode(graphWithThreeCliques());
		Integer[] answers = new Integer[]{3,3,4,2,3,2,2,2,1};
		for(Integer i = 0; i < answers.length; i++) {
			Integer node = i+1;
			Integer answer = answers[i];
			Integer calculated = calculations.get(node);
			assertEquals(node+" has "+answer+" connections",
					answer,calculated);
		}
	}
	
	@Test
	public void testConnectednessByNode() {
		Map<Integer,Integer> calculations = 
			kp.connectednessByNode(graphWithThreeCliques());
		Integer[] answers = new Integer[]{2,2,3,2,2,2,2,1,1};
		for(Integer i = 0; i < answers.length; i++) {
			Integer node = i+1;
			Integer answer = answers[i];
			Integer calculated = calculations.get(node);
			assertEquals(node+" has connectedness of "+answer,
					answer,calculated);
		}
	}
	
	@Test
	public void testConnectionsWithinSubgroup() {
		Map<Integer,Integer> cons = 
			kp.connectionsWithinSubgroup(graphWithThreeCliques(), intSet(1,2,3,4));
		Integer[] answers = new Integer[]{2,3,3,2,1,0,0,1,0};
		for(Integer i = 0; i < answers.length; i++) {
			Integer node = i+1;
			Integer answer = answers[i];
			Integer calculated = cons.get(node);
			assertEquals(node+" is connected to "+answer+" members of {1,2,3,4}",
					answer,calculated);
		}
	}
	
	@Test
	public void testCriticalNodes() {
		assertEquals("All nodes are critical in a 1-plex.",
				intSet(7),
				kp.criticalNodesInKPlex(
						graphWithThreeCliques(),
						intSet(7),
						1));
	}
	
	@Test
	public void testSubgraphBoundingFinalKPlex() {
		Set<Integer> finalKPlex = intSet(5,6,7);
		Map<Integer,Set<Integer>> subgraph = 
			kp.subgraphBoundingFinalKPlex(graphWithThreeCliques(), intSet(7), 1, 3);
		Set<Integer> boundsKPlex = subgraph.keySet();
		assertTrue(boundsKPlex+" should bound "+finalKPlex+" (full graph is "+subgraph+")",
				boundsKPlex.containsAll(finalKPlex));
	}
	
	@Test
	public void testGrowClique() {
		Set<Integer> clique = kp.growKPlex(graphWithThreeCliques(), intSet(7), 1, 3);
		assertEquals("Expect {7} to grow into 1-plex of {5,6,7}.",
				intSet(5,6,7),clique);
	}
	
	@Test
	public void testCliqueSearch() {
		Set<Integer> clique = kp.findLargeKPlex(graphWithThreeCliques(), 1);
		assertEquals("Find a clique of size three, because all three cliques have size three.",
				3,clique.size());
	}
	
	@Test
	public void testKPlexSearch() {
		assertEquals("Find a 2-plex of size 4.",
				intSet(1,2,3,4),kp.findLargeKPlex(graphWithThreeCliques(), 2));
	}

	public static junit.framework.Test suite() {
      return new JUnit4TestAdapter(KPlexesTest.class);
	}
}
