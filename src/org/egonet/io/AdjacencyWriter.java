package org.egonet.io;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileWriter;
import java.io.IOException;

public class AdjacencyWriter extends FileWriter {

	public AdjacencyWriter(File file, boolean append) throws IOException {
		super(file, append);
	}

	public AdjacencyWriter(File file) throws IOException {
		super(file);
	}

	public AdjacencyWriter(FileDescriptor fd) {
		super(fd);
	}

	public AdjacencyWriter(String fileName, boolean append) throws IOException {
		super(fileName, append);
	}

	public AdjacencyWriter(String fileName) throws IOException {
		super(fileName);
	}

	public void writeAdjacency(int [][] adj) throws IOException {
		write("Adjacency Matrix");
		write("\n");
		for(int i = 0; i < adj.length; i++)
		{
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
