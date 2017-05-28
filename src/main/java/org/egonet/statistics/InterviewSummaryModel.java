/***
 * Copyright (c) 2008, Endless Loop Software, Inc.
 * 
 * This file is part of EgoNet.
 * 
 * EgoNet is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 * 
 * EgoNet is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.egonet.statistics;

import javax.swing.JTable;

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
						return ("Closeness Centrality Maximum");
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
