package org.egonet.tests.functional;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

import javax.swing.JOptionPane;
import com.endlessloopsoftware.ego.client.EgoStore;
import com.endlessloopsoftware.ego.client.EgoStore.VersionFileFilter;
import com.endlessloopsoftware.egonet.Answer;
import com.endlessloopsoftware.egonet.Interview;
import com.endlessloopsoftware.egonet.Question;
import com.endlessloopsoftware.egonet.Study;

import electric.xml.Document;

public class CombineInterviews
{

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
		
		for (String s: fileList){
			try{	
				File f = new File(currentDirectory.toString() + "/" + s);			
				if(!filter.accept(f) || !f.canRead())
					throw new IOException("Couldn't read file or file not associated with selected study.");
				
					
					Document document = new Document(f);
					Interview interview = Interview.readInterview(study, document.getRoot());
					
					//String [] thisInterviewAlters = interview.getAlterList();
					
					Iterator questions = study.getQuestionOrder(Question.ALTER_PAIR_QUESTION).iterator();
					while (questions.hasNext()) {
						Question q = study.getQuestion((Long) questions.next());
						Answer a = q.answer;
						int[][] adj = interview.generateAdjacencyMatrix(q, false);
						
						// loop through adj
						// if adj[i][j] == 1, thisInterviewAlters[i] && thisInterviewAlters[j] are adjacent in final matrix
						
						for(int i = 0; i < adj.length; i++)
						{
							for(int j = 0; j < adj[i].length; j++)
							{
								if(a.adjacent)
								{
									// alter1 = a.getAlters()[0]
									// alter2 = a.getAlters()[1]
									// mark those as adjacent in the new big matrix
									System.out.println(a.getAlters()[0] + " and " + a.getAlters()[1] + " are adjacent");
								}
							}
						}
					}
					
					//add alters to alterList
					alterList.addAll(Arrays.asList(interview.getAlterList()));
				
			}
			catch(Throwable e){
				e.printStackTrace();
				JOptionPane.showMessageDialog(null,
						"Unable to read interview file.", "File Error",
						JOptionPane.ERROR_MESSAGE);
			}
		
		}
		
		System.out.println(alterList);
	}

	public static void main(String[] args) throws Exception
	{
		new CombineInterviews().doCombineInterviews();
	}
}
