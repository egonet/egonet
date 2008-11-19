package org.egonet.tests.functional;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.egonet.exceptions.CorruptedInterviewException;
import org.egonet.util.Pair;

import com.endlessloopsoftware.ego.client.ClientFrame;
import com.endlessloopsoftware.ego.client.EgoClient;
import com.endlessloopsoftware.ego.client.EgoStore;
import com.endlessloopsoftware.ego.client.EgoStore.VersionFileFilter;
import com.endlessloopsoftware.ego.client.graph.EdgeProperty;
import com.endlessloopsoftware.ego.client.graph.GraphData;
import com.endlessloopsoftware.ego.client.graph.GraphRenderer;
import com.endlessloopsoftware.ego.client.graph.GraphSettingsEntry;
import com.endlessloopsoftware.ego.client.graph.NodeProperty;
import com.endlessloopsoftware.ego.client.graph.EdgeProperty.EdgeShape;
import com.endlessloopsoftware.ego.client.graph.NodeProperty.NodeShape;
import com.endlessloopsoftware.egonet.Interview;
import com.endlessloopsoftware.egonet.Question;
import com.endlessloopsoftware.egonet.Shared;
import com.endlessloopsoftware.egonet.Study;

import edu.uci.ics.jung.graph.ArchetypeVertex;
import edu.uci.ics.jung.graph.Edge;
import edu.uci.ics.jung.graph.impl.UndirectedSparseEdge;
import electric.xml.Document;

public class CombineInterviews
{
	// end up doing this for studies and interviews, readers and writers
	public interface InterviewReader
	{
		public Interview getInterview() throws CorruptedInterviewException;
	}
	
	public abstract class DefaultInterviewReader implements InterviewReader
	{
		protected Study study;
		public DefaultInterviewReader(Study study)
		{
			// some generic functionality related to studies when you're reading interview data
			this.study = study;
		}
	}
	
	public class InterviewFileReader extends DefaultInterviewReader
	{
		private File interviewFile;
		public InterviewFileReader(Study study, File interviewFile)
		{
			super(study);
			this.interviewFile = interviewFile;
		}
		

		public Interview getInterview() throws CorruptedInterviewException {
			// TODO move all of the XML format reading here from EgoStore#readInterview and Interview#readInterview
			Interview interview = EgoStore.readInterview(study, interviewFile);
			return interview;
		}
		
	}
	
	private Map<ArchetypeVertex, NodeProperty> nodeSettingsMap = Collections
	.synchronizedMap(new HashMap<ArchetypeVertex, NodeProperty>());

	private Map<Edge, EdgeProperty> edgeSettingsMap = Collections
	.synchronizedMap(new HashMap<Edge, EdgeProperty>());

	private java.util.List<GraphSettingsEntry> QAsettings = Collections
	.synchronizedList(new ArrayList<GraphSettingsEntry>());

	GraphRenderer renderer;
	private EgoClient egoClient;

	public void doCombineInterviews() throws Exception
	{
		/* Read new study */
		File studyFile = EgoStore.selectStudy(null, new File("."));
		Document packageDocument = new Document(studyFile);
		Study study = new Study(packageDocument);

		//Find the interview files associated with this study
		File parentFile = studyFile.getParentFile();
		File interviewFile = new File(parentFile, "/Interviews/");

		File guessLocation = new File(".");
		if(parentFile.exists() && parentFile.isDirectory() && parentFile.canRead())
			guessLocation = parentFile;

		if(interviewFile.exists() && interviewFile.isDirectory() && interviewFile.canRead())
			guessLocation = interviewFile;

		final File currentDirectory = guessLocation;

		String[] fileList = currentDirectory.list();	
		VersionFileFilter filter = new VersionFileFilter(study.getStudyId(), "Interview Files", "int");
		ArrayList<String> alterList = new ArrayList<String>();
		Interview interview = null;
		int[][] adj = null;

		Set<Pair<String>> allPairs = new HashSet<Pair<String>>();
		Set<String> pairedAlters = new HashSet<String>();
		
		for (String s: fileList){

			File f = new File(currentDirectory.toString() + "/" + s);			
			if(!filter.accept(f) || !f.canRead())
				throw new IOException("Couldn't read file or file not associated with selected study.");

			Document document = new Document(f);
			interview = Interview.readInterview(study, document.getRoot());
			if(!interview.isComplete())
			{
				System.out.println("*** SKIPPED because interview isn't complete: " + f.getName());
				continue;
			}
			
			System.out.println("** Reading next file " + f.getName());
			
			

			String [] thisInterviewAlterlist = interview.getAlterList();
			alterList.addAll(Arrays.asList(interview.getAlterList()));

			Iterator<Long> questions = study.getQuestionOrder(Shared.QuestionType.ALTER_PAIR).iterator();
			while (questions.hasNext()) {
				Question q = study.getQuestion((Long) questions.next());
				adj = interview.generateAdjacencyMatrix(q, false);

				// loop through adj
				// if adj[i][j] == 1, thisInterviewAlters[i] && thisInterviewAlters[j] are adjacent in final matrix

				for(int i = 0; i < adj.length; i++)
				{
					for(int j = 0; j < adj[i].length; j++)
					{
						if(adj[i][j] == 1 && i != j)
						{
							
							String alter1 = thisInterviewAlterlist[i];
							String alter2 = thisInterviewAlterlist[j];
							
							Pair<String> p = new Pair<String>(alter1, alter2);
							allPairs.add(p);
							
							pairedAlters.add(alter1);
							pairedAlters.add(alter2);
							
							// mark those as adjacent in the new big matrix
							//System.out.println(p +  " are adjacent");
						}
					}
				}
			}

		}

		System.out.println("Pairs: " + allPairs);
		alterList.removeAll(pairedAlters);
		
		System.out.println("Single alters: " + alterList);
		
		if(true)
			return;
		
//----Tried to see if I could manipulate EgoClient to do my bidding----		
		String[] bleh = new String[alterList.size()];
		for (int i = 0; i < alterList.size(); i++){
			bleh[i] = alterList.get(i);
		}
		interview.setAlterList(bleh);
		egoClient.setStudy(study);
		egoClient.setStorage(new EgoStore(egoClient));
		egoClient.setFrame(new ClientFrame(egoClient));
		egoClient.setInterview(interview); 
		renderer = new GraphRenderer(egoClient);
//----So far unsuccessful----
		
//----Kinda stuck here...don't know how to get around not using EgoClient----		
		int noOfAlters = interview.getNumAlters();
		// initialize nodes with default settings
		for (int i = 0; i < noOfAlters; i++) {
			String alterName = interview.getAlterList()[i];
			Color color = Color.RED;
			int size = 1;
			NodeShape shape = NodeShape.Circle;
			NodeProperty nodeProperty = new NodeProperty(alterName, color,
					shape, size);
//			String toolTipText = getAlterInfo(i);
//			nodeProperty.setToolTipText(toolTipText);
			nodeSettingsMap.put(renderer.getvertexArray()[i], nodeProperty);
		}
		// initialize edges with default settings
		GraphRenderer.getGraph().removeAllEdges();
//		GraphData graphData = new GraphData(egoClient);
		int[][] adjacencyMatrix = adj;
		for (int i = 0; i < adjacencyMatrix.length; ++i) {
			for (int j = i + 1; j < adjacencyMatrix[i].length; ++j) {
				if (adjacencyMatrix[i][j] > 0) {
					UndirectedSparseEdge edge = new UndirectedSparseEdge(
							renderer.getvertexArray()[i], renderer
									.getvertexArray()[j]);
					GraphRenderer.getGraph().addEdge(edge);
					String label = ((Integer) interview.getStats().proximityMatrix[i][j])
							.toString();
					EdgeProperty edgeProperty = new EdgeProperty(label,
							Color.BLACK, EdgeShape.Line, 1);
					edgeProperty.setVisible(true);
					edgeSettingsMap.put(edge, edgeProperty);
				}
			}
		}
//----End----
	}

	public static void main(String[] args) throws Exception
	{
		new CombineInterviews().doCombineInterviews();
	}
}
