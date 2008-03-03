/**
 * <p>Title: Egocentric Network Researcher</p>
 * <p>Description: Configuration Utilities for an Egocentric network study</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: Endless Loop Software</p>
 * @author Peter C. Schoaff
 *
 * $Id: InterviewSummaryModel.java,v 1.1 2005/08/02 19:36:12 samag Exp $
 *
 * $Log: InterviewSummaryModel.java,v $
 * Revision 1.1  2005/08/02 19:36:12  samag
 * Initial checkin
 *
 * Revision 1.3  2004/04/11 00:24:49  admin
 * Fixing headers
 *
 * Revision 1.2  2004/03/10 14:32:40  admin
 * Adding client library
 * cleaning up code
 *
 * Revision 1.1  2003/12/08 15:57:51  admin
 * Modified to generate matrix files on survey completion or summarization
 * Extracted statistics models
 *
 */
package com.endlessloopsoftware.ego.client.statistics.models;

import javax.swing.JTable;

import com.endlessloopsoftware.ego.client.statistics.Statistics;

public class InterviewSummaryModel extends StatTableModel
{
	public InterviewSummaryModel(Statistics stats) 
	{
		super(stats);
	}
	
	public int getColumnCount()
	{
		if (stats.adjacencyMatrix.length > 0)
		{
			return 3;
		}
		else
		{
			return 1;
		}
	}

	public Object getValueAt(int rowIndex, int columnIndex)
	{
		switch (columnIndex)
		{
			case 0 :
				switch (rowIndex)
				{
					case 0 :
						return ("Degree Centrality Maximum");
					case 1 :
						return ("Closeness Centrality Minimum");
					case 2 :
						return ("Betweenness Centrality Maximum");
					case 3 :
						return ("Number of Cliques");
					case 4 :
						return ("Number of Components");
					default :
						return (null);
				}

			case 1 :
				switch (rowIndex)
				{
					case 0 :
						return (stats.mostCentralDegreeAlterName);
					case 1 :
						return (stats.mostCentralClosenessAlterName);
					case 2 :
						return (stats.mostCentralBetweenAlterName);
					case 3 :
						return (new Integer(stats.cliqueSet.size()));
					case 4 :
						return (new Integer(stats.componentSet.size()));
					default :
						return (null);
				}

			case 2 :
				switch (rowIndex)
				{
					case 0 :
						return (new Integer(stats.mostCentralDegreeAlterValue));
					case 1 :
						return (new Float(stats.mostCentralClosenessAlterValue));
					case 2 :
						return (new Float(stats.mostCentralBetweenAlterValue));
					default :
						return (null);
				}

			default :
				return (null);
		}
	}

	public int getRowCount()
	{
		if (stats.adjacencyMatrix.length > 0)
		{
			return 5;
		}
		else
		{
			return 0;
		}
	}

	public String getColumnName(int column)
	{
		if (stats.adjacencyMatrix.length > 0)
		{
			return null;
		}
		else
		{
			return ("No Structural Measures question specified in study");
		}
	}

	public int getResizeMode()
	{
		return JTable.AUTO_RESIZE_ALL_COLUMNS;
	}
}
