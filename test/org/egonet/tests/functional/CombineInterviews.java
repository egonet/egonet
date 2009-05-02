package org.egonet.tests.functional;

import java.awt.BorderLayout;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.swing.JFrame;
import javax.swing.JPanel;

import org.egonet.gui.EgoStore;
import org.egonet.io.InterviewFileFilter;
import org.egonet.io.InterviewReader;
import org.egonet.io.StudyReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.endlessloopsoftware.ego.client.graph.ELSFRLayout2;
import com.endlessloopsoftware.ego.client.graph.Edge;
import com.endlessloopsoftware.ego.client.graph.Vertex;
import com.endlessloopsoftware.egonet.Interview;
import com.endlessloopsoftware.egonet.Question;
import com.endlessloopsoftware.egonet.Shared;
import com.endlessloopsoftware.egonet.Study;

import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.graph.SparseGraph;
import edu.uci.ics.jung.visualization.VisualizationViewer;

public class CombineInterviews
{
	final private static Logger logger = LoggerFactory.getLogger(CombineInterviews.class);
	
	public void doCombineInterviews() throws Exception
	{
		/* Read new study */
		EgoStore store = new EgoStore(null);
		File studyFile = store.selectStudy(new File("."));
		
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

		Set<Edge> allPairs = new HashSet<Edge>();
		Set<Vertex> pairedAlters = new HashSet<Vertex>();
		
		
		
		for (String s: fileList){

			File f = new File(currentDirectory.toString() + "/" + s);			
			if(!filter.accept(f) || !f.canRead())
				throw new IOException("Couldn't read file or file not associated with selected study.");

			InterviewReader interviewReader = new InterviewReader(study, f);
			Interview interview = interviewReader.getInterview();
			if(!interview.isComplete())
			{
				logger.info("*** SKIPPED because interview isn't complete: " + f.getName());
				continue;
			}
			
			logger.info("** Reading next file " + f.getName());
			
			

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
							
							allPairs.add(new Edge(alter1, alter2));
							
							pairedAlters.add(new Vertex(alter1));
							pairedAlters.add(new Vertex(alter2));
							
							// mark those as adjacent in the new big matrix
							//logger.info(p +  " are adjacent");
						}
					}
				}
			}

		}

		Set<Vertex> vertices = new HashSet<Vertex>();
		for(Edge pair : allPairs)
		{
			Vertex v1 = new Vertex(pair.pair.getFirst());
			Vertex v2 = new Vertex(pair.pair.getSecond());
			vertices.add(v1);
			vertices.add(v2);
		}
		
		for(String isolate : alterList)
		{
			Vertex v = new Vertex(isolate);
			vertices.add(v);
		}
		
		SparseGraph<Vertex,Edge> graph = new SparseGraph<Vertex,Edge>();
		for(Edge pair : allPairs)
		{
			Vertex v1 = new Vertex(pair.pair.getFirst());
			Vertex v2 = new Vertex(pair.pair.getSecond());
			
			if(!graph.getVertices().contains(v1))
				graph.addVertex(v1);
			if(!graph.getVertices().contains(v2))
				graph.addVertex(v2);
			
			graph.addEdge(pair, Arrays.asList(v1,v2));
		}
		
		for(String isolate : alterList)
		{
			Vertex v = new Vertex(isolate);
			if(!graph.getVertices().contains(v))
				graph.addVertex(v);
		}
		
        Layout<Vertex,Edge> layout = new ELSFRLayout2<Vertex,Edge>(graph);
        VisualizationViewer<Vertex,Edge> vv = new VisualizationViewer<Vertex,Edge>(layout);
        
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(vv, BorderLayout.CENTER);
        
        frame.setContentPane(panel);
        frame.pack();

        frame.setVisible(true);

		// TODO: how do isolates exist from combining personal networks into a whole network?
		logger.info("Pairs: " + allPairs);
		alterList.removeAll(pairedAlters);
		logger.info("Single alters: " + alterList);
		
		// TODO: write to file using save dialog
	}

	public static void main(String[] args) throws Exception
	{
		new CombineInterviews().doCombineInterviews();
	}
}
