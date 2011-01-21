package org.egonet.io;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.egonet.util.FileHelpers;

public class AdjacencyWriter extends FileWriter {

	public AdjacencyWriter(File file) throws IOException {
		super(file);
	}
	
	public void writeAdjacency(String[] labels, int [][] adj) throws IOException {
		// indicate that this is an adjacency matrix. Is this really necessary?
		write("Adjacency Matrix");
		write("\n");
		
		// Check that the number of labels matches the size of the adjacency matrix.
		assert(adj.length == labels.length);
		
		// Labels across the top
		write("        ");
		for(int i = 0; i < adj.length; i++) {
			write(","+FileHelpers.formatForCSV(labels[i]));
		}
		write("\n");
		
		for(int i = 0; i < adj.length; i++)
		{
			write(FileHelpers.formatForCSV(labels[i])+",");
			for(int j = 0; j < adj.length; j++)
			{
				if(i == j)
					write(""+1);
				else
					write(""+adj[i][j]);
				
				if(j < adj.length-1)
					write(",");
				else
					write("\n");
			}
		}
	}
	
}
