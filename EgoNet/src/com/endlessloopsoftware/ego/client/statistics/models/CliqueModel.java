/**
 * <p>Title: Egocentric Network Researcher</p>
 * <p>Description: Configuration Utilities for an Egocentric network study</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: Endless Loop Software</p>
 * @author Peter C. Schoaff
 * @version 1.0
 *
 * $Id: CliqueModel.java,v 1.1 2005/08/02 19:36:11 samag Exp $
 *
 * $Log: CliqueModel.java,v $
 * Revision 1.1  2005/08/02 19:36:11  samag
 * Initial checkin
 *
 * Revision 1.2  2004/02/26 21:19:18  admin
 * adding jardescs
 *
 * Revision 1.1  2003/12/08 15:57:51  admin
 * Modified to generate matrix files on survey completion or summarization
 * Extracted statistics models
 *
 */
package com.endlessloopsoftware.ego.client.statistics.models;

import java.util.Iterator;
import java.util.Stack;

import javax.swing.JTable;

import com.endlessloopsoftware.ego.client.statistics.Statistics;

public class CliqueModel extends StatTableModel
{
	private Stack[] cliqueArray;
	private int cliqueDepth;

	public CliqueModel(Statistics stats)
	{
		super(stats);
		initModel();
	}

	private void initModel()
	{
		cliqueArray = new Stack[stats.cliqueSet.size()];
		stats.cliqueSet.toArray(cliqueArray);

		/* Determine deepest clique */
		Iterator it = stats.cliqueSet.iterator();
		int maxCount = 0;

		while (it.hasNext())
		{
			Stack s = (Stack) it.next();

			if (s.size() > maxCount)
			{
				maxCount = s.size();
			}
		}

		cliqueDepth = maxCount;
	}

	public void update()
	{
		initModel();
		this.fireTableStructureChanged();
		fireTableDataChanged();
	}

	public int getColumnCount()
	{
		return (cliqueArray.length);
	}

	public int getRowCount()
	{
		return (cliqueDepth);
	}

	public Object getValueAt(int rowIndex, int columnIndex)
	{
		if (rowIndex < cliqueArray[columnIndex].size())
		{
			try
			{
				return (stats.alterList[((Integer) cliqueArray[columnIndex].get(rowIndex)).intValue()]);
			}
			catch (Exception ex)
			{
				ex.printStackTrace();
				return null;
			}
		}
		else
		{
			return null;
		}
	}

	public String getColumnName(int column)
	{
		return ("Clique " + (column + 1));
	}

	public int getResizeMode()
	{
		return JTable.AUTO_RESIZE_OFF;
	}
}
