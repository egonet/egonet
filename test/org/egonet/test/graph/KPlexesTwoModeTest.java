package org.egonet.test.graph;

import static org.junit.Assert.*;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import net.sf.functionalj.tuple.Pair;

import org.egonet.graph.KPlexesTwoMode;
import org.junit.*;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import junit.framework.JUnit4TestAdapter;

public class KPlexesTwoModeTest {

	protected Random rand = new Random();
	private KPlexesTwoMode<Integer> kp = new KPlexesTwoMode<Integer>();
	
	private Set<Integer> intSet(Integer... members) {
		return Sets.newHashSet(members);
	}
	
	private Pair<Integer,Set<Integer>> intGraphItem(Integer member, Integer... connections) {
		return new Pair<Integer,Set<Integer>>(member,intSet(connections));
	}
	
	private Map<Integer,Set<Integer>> intGraph(Pair<Integer,Set<Integer>>... members) {
		Map<Integer,Set<Integer>> result = Maps.newHashMap();
		for(Pair<Integer,Set<Integer>> member : members) {
			result.put(member.getFirst(), member.getSecond());
		}
		return result;
	}
	
	@SuppressWarnings("unchecked")
	private Map<Integer,Set<Integer>> exampleGraph() {
		intGraphItem(1,2,3);
		return intGraph(
				intGraphItem(1,  7,8,9,10),
				intGraphItem(2,  10),
				intGraphItem(3,  7,9,10),
				intGraphItem(4,  6,7,10),
				intGraphItem(5,  6),
				intGraphItem(6,  4,5),
				intGraphItem(7,  1,3,4),
				intGraphItem(8,  1),
				intGraphItem(9,  1,3),
				intGraphItem(10, 1,2,3,4));
	}
	private Set<Integer> exampleMode1() {
		return intSet(1,2,3,4,5);
	}
	private List<Set<Integer>> exampleLargestCliques() {
		List<Set<Integer>> results = Lists.newArrayList();
		results.add(intSet(1,3,4,7,10));
		results.add(intSet(1,3,7,9,10));
		return results;
	}

	@Test
	public void testConnectionsByNode() {
		Map<Integer,Integer> calculations = 
			kp.connectionsByNode(exampleGraph());
		Integer[] answers = new Integer[]{4,1,3,3,1,2,3,1,2,4};
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
			kp.connectednessByNode(exampleGraph());
		Integer[] answers = new Integer[]{2,1,2,2,1,1,3,1,2,3};
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
			kp.connectionsWithinSubgroup(exampleGraph(), intSet(1,4,7,10));
		Integer[] answers = new Integer[]{2,1,2,2,0,1,2,1,1,2};
		for(Integer i = 0; i < answers.length; i++) {
			Integer node = i+1;
			Integer answer = answers[i];
			Integer calculated = cons.get(node);
			assertEquals(node+" is connected to "+answer+" members of {1,4,7,10}",
					answer,calculated);
		}
	}
	
	@Test
	public void testCriticalNodes() {
		assertEquals("All nodes are critical in a 1-plex.",
				intSet(7),
				kp.criticalNodesInKPlex(
						exampleGraph(),
						exampleMode1(),
						intSet(7),
						1));
	}
	
	@Test
	public void testSubgraphBoundingFinalKPlex() {
		Set<Integer> finalKPlex = intSet(1,4,7,10);
		Map<Integer,Set<Integer>> subgraph = 
			kp.subgraphBoundingFinalKPlex(exampleGraph(), exampleMode1(), intSet(1), 1, 2);
		Set<Integer> boundsKPlex = subgraph.keySet();
		assertTrue(boundsKPlex+" should bound "+finalKPlex+" (full graph is "+subgraph+")",
				boundsKPlex.containsAll(finalKPlex));
	}
	
	@Test
	public void testGrowClique() {
		Set<Integer> clique = kp.growKPlex(exampleGraph(), exampleMode1(), intSet(4), 1, 2);
		assertTrue("Expect {7} to grow into one of the two largest cliques.",
				exampleLargestCliques().contains(clique));
	}
	
	@Test
	public void testCliqueSearch() {
		Set<Integer> clique = kp.findLargeKPlex(exampleGraph(), exampleMode1(), 1);
		assertTrue("Find one of the largest cliques.",
				exampleLargestCliques().contains(clique));
	}
	
	@Test
	public void testKPlexSearch() {
		assertEquals("Looks like the largest 2-plex is {1,3,4,7,9,10}.",
				intSet(1,3,4,7,9,10),kp.findLargeKPlex(exampleGraph(), exampleMode1(), 2));
	}

	public static junit.framework.Test suite() {
      return new JUnit4TestAdapter(KPlexesTwoModeTest.class);
	}
}
