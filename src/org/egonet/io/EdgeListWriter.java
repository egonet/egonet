package org.egonet.io;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileWriter;
import java.io.IOException;

public class EdgeListWriter extends FileWriter {

	public EdgeListWriter(File file, boolean append) throws IOException {
		super(file, append);
	}

	public EdgeListWriter(File file) throws IOException {
		super(file);
	}

	public EdgeListWriter(FileDescriptor fd) {
		super(fd);
	}

	public EdgeListWriter(String fileName, boolean append) throws IOException {
		super(fileName, append);
	}

	public EdgeListWriter(String fileName) throws IOException {
		super(fileName);
	}

	public void writeEdgelist(String [] thisInterviewAlterlist, int [][] adj) throws IOException {
		for(int i = 0; i < adj.length; i++)
		{
			for(int j = i+1; j < adj[i].length; j++)
			{
				// adj[i][j] != 0 && 
				if(i != j)
				{
					
					String alter1 = thisInterviewAlterlist[i];
					String alter2 = thisInterviewAlterlist[j];
					
					// mark those as adjacent in the new big matrix
					String line = ("\"" + alter1 + "\",\"" + alter2 + "\","+adj[i][j] +"\n");
					write(line);
				}
			}
		}
	}
	
}
