package org.egonet.tests.functional;

import java.awt.BorderLayout;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.swing.JFrame;
import javax.swing.JPanel;

import org.egonet.io.InterviewReader;
import org.egonet.io.StudyReader;
import org.egonet.util.Pair;

import samples.graph.BasicRenderer;

import com.endlessloopsoftware.ego.client.EgoStore;
import com.endlessloopsoftware.ego.client.EgoStore.InterviewFileFilter;
import com.endlessloopsoftware.ego.client.graph.ELSFRLayout;
import com.endlessloopsoftware.egonet.Interview;
import com.endlessloopsoftware.egonet.Question;
import com.endlessloopsoftware.egonet.Shared;
import com.endlessloopsoftware.egonet.Study;

import edu.uci.ics.jung.graph.Vertex;
import edu.uci.ics.jung.graph.impl.SparseGraph;
import edu.uci.ics.jung.graph.impl.SparseVertex;
import edu.uci.ics.jung.graph.impl.UndirectedSparseEdge;
import edu.uci.ics.jung.visualization.Layout;
import edu.uci.ics.jung.visualization.VisualizationViewer;

public class CombineInterviews
{
	public void doCombineInterviews() throws Exception
	{
		/* Read new study */
		File studyFile = EgoStore.selectStudy(null, new File("."));
		
		StudyReader sr = new StudyReader(studyFile);
		Study study = sr.getStudy();

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
		InterviewFileFilter filter = new InterviewFileFilter(study, "Interview Files", "int");
		ArrayList<String> alterList = new ArrayList<String>();
		int[][] adj = null;

		Set<Pair<String>> allPairs = new HashSet<Pair<String>>();
		Set<String> pairedAlters = new HashSet<String>();
		
		
		
		for (String s: fileList){

			File f = new File(currentDirectory.toString() + "/" + s);			
			if(!filter.accept(f) || !f.canRead())
				throw new IOException("Couldn't read file or file not associated with selected study.");

			InterviewReader interviewReader = new InterviewReader(study, f);
			Interview interview = interviewReader.getInterview();
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

		Map<String,Vertex> vertices = new HashMap<String,Vertex>();
		for(Pair<String> pair : allPairs)
		{
			if(!vertices.containsKey(pair.first()))
					vertices.put(pair.first(), new SparseVertex());
			if(!vertices.containsKey(pair.second()))
				vertices.put(pair.second(), new SparseVertex());
		}
		
		for(String isolate : alterList)
		{
			if(!vertices.containsKey(isolate))
			{
				vertices.put(isolate, new SparseVertex());
			}
		}
		
		SparseGraph graph = new SparseGraph();
		for(Pair<String> pair : allPairs)
		{
			if(!graph.getVertices().contains(vertices.get(pair.first())))
				graph.addVertex(vertices.get(pair.first()));
			if(!graph.getVertices().contains(vertices.get(pair.second())))
				graph.addVertex(vertices.get(pair.second()));
			
			graph.addEdge(new UndirectedSparseEdge(vertices.get(pair.first()), vertices.get(pair.second())));
		}
		
		for(String isolate : alterList)
		{
				Vertex v = vertices.get(isolate);
				if(!graph.getVertices().contains(v))
					graph.addVertex(v);
		}
		
        Layout layout = new ELSFRLayout(graph);
        VisualizationViewer vv = new VisualizationViewer(layout, new BasicRenderer());
        
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(vv, BorderLayout.CENTER);
        
        frame.setContentPane(panel);
        frame.pack();

        frame.setVisible(true);

		// TODO: how do isolates exist from combining personal networks into a whole network?
		System.out.println("Pairs: " + allPairs);
		alterList.removeAll(pairedAlters);
		System.out.println("Single alters: " + alterList);
		
		// TODO: write to file using save dialog
	}

	public static void main(String[] args) throws Exception
	{
		new CombineInterviews().doCombineInterviews();
	}
}
