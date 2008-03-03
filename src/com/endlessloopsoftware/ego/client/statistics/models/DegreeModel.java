/**
 * <p>Title: Egocentric Network Researcher</p>
 * <p>Description: Configuration Utilities for an Egocentric network study</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: Endless Loop Software</p>
 * @author Peter C. Schoaff
 * @version 1.0
 *
 * $Id: DegreeModel.java,v 1.1 2005/08/02 19:36:12 samag Exp $
 *
 * $Log: DegreeModel.java,v $
 * Revision 1.1  2005/08/02 19:36:12  samag
 * Initial checkin
 *
 * Revision 1.2  2004/04/11 00:24:49  admin
 * Fixing headers
 *
 * Revision 1.1  2003/12/08 15:57:51  admin
 * Modified to generate matrix files on survey completion or summarization
 * Extracted statistics models
 *
 */
package com.endlessloopsoftware.ego.client.statistics.models;

import javax.swing.JTable;

import com.endlessloopsoftware.ego.client.statistics.Statistics;

public class DegreeModel extends StatTableModel
{
	public DegreeModel(Statistics stats)
	{
		super(stats);
	}

	public int getColumnCount()
	{
		return (3);
	}

	public int getRowCount()
	{
		return (stats.degreeArray.length);
	}

	public Object getValueAt(int rowIndex, int columnIndex)
	{
		try
		{
			if (columnIndex == 0)
			{
				return (stats.alterList[rowIndex]);
			}
			else if (columnIndex == 1)
			{
				return (new Integer(stats.degreeArray[rowIndex]));
			}
			else
			{
				return (new Float(stats.degreeArray[rowIndex] / ((float) (stats.proximityMatrix.length - 1))));
			}
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			return null;
		}
	}

	public String getColumnName(int column)
	{
		if (column == 0)
		{
			return ("Alter");
		}
		else if (column == 1)
		{
			return ("Raw");
		}
		else
		{
			return ("Normalized");
		}
	}

	public int getResizeMode()
	{
		return JTable.AUTO_RESIZE_ALL_COLUMNS;
	}
}
