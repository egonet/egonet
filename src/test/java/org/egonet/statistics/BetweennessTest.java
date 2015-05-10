package org.egonet.statistics;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.junit.*;

import edu.uci.ics.jung.algorithms.importance.BetweennessCentrality;
import edu.uci.ics.jung.graph.UndirectedSparseGraph;

public class BetweennessTest {

	//private List<String> vertices = Arrays.asList(new String[] {"A","B","C","D","E","F","G"});
	//private List<String> edgeList = Arrays.asList(new String[] {"AC","BC","CD","DE","EF","EG", "FG"});

	private List<String> vertices = Arrays.asList(new String[] {"A","B","C","D","E"});
	private List<String> edgeList = Arrays.asList(new String[] {"AB", "BC", "CD", "DE"});
	
	private int[][] adjacencyMatrix;
	UndirectedSparseGraph<String,String> graph = new UndirectedSparseGraph<String,String>();

	@Before
    public void setUp() {
		for(String v : vertices) {
			graph.addVertex(v);
		}
		
    	adjacencyMatrix = new int[vertices.size()][vertices.size()];
    	for(int i = 0; i < vertices.size(); i++) {
    		adjacencyMatrix[i] = new int[vertices.size()];
    		for(int j = 0; j < vertices.size(); j++) {
    			String v1 = i < j ? vertices.get(i) : vertices.get(j);
    			String v2 = i < j ? vertices.get(j) : vertices.get(i);
    			
    			if(i == j || containsEdge(v1, v2)) {
    				adjacencyMatrix[i][j] = 1;
    				graph.addEdge(v1+","+v2, v1, v2);
    				graph.addEdge(v2+","+v1, v2, v1);
    			} else {
    				adjacencyMatrix[i][j] = 0;
    			}
    		}
    	}
    	
    	//for(int i = 0; i < adjacencyMatrix[0].length; i++)
    	//	System.out.println(adjacencyMatrix[0][i]);
    	//System.out.println(doubleArrayToString(adjacencyMatrix));
    	System.out.println("\n");
    }
	
	private boolean containsEdge(String v1, String v2) {
		return edgeList.contains(v1+v2) || edgeList.contains(v2+v1);
	}
 
    @After
    public void tearDown() {
    	adjacencyMatrix = null;
        //System.out.println("@After - tearDown");
    }
    
    @Test
    public void testEgonetBetweenness() {
    	float[] bt = Statistics.generateBetweennessArray(adjacencyMatrix);
    	
		int n = bt.length;
		double divisor = (n-1.0)*(n-2.0)/2.0;
    	
    	for(int i = 0; i < bt.length; i++) {
    		System.out.println("Egonet\t" + vertices.get(i) + "\t" + bt[i]);
    	}
    	System.out.println("\n");
    }
    
    @Test
    public void testJungBetweenness() {
		BetweennessCentrality<String,String> ranker = new BetweennessCentrality<String,String>(graph);
		ranker.setRemoveRankScoresOnFinalize(false);
		ranker.setNormalizeRankings(false);
		ranker.evaluate();
		
		int n = graph.getVertexCount();
		double divisor = (n-1.0)*(n-2.0)/2.0;
		
		for(String v : vertices) {
    		System.out.println("Jung\t" + (v) + "\t" + ranker.getVertexRankScore(v));
    	}
		System.out.println("\n");
    }
    
    private static String doubleArrayToString(int [][] matrix) {
    	StringBuilder sb = new StringBuilder();
    	for(int i = 0; i < matrix.length; i++) {
    		sb.append("[");
    		for(int j = 0; j < matrix[i].length; j++) {
    			sb.append(matrix[i][j] + ",");
    		}
    		sb.append("]\n");
    	}
    	return sb.toString();
    }
}
