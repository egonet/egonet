/**
 * <p>Title: Egocentric Network Researcher</p>
 * <p>Description: Configuration Utilities for an Egocentric network study</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: Endless Loop Software</p>
 * @author Peter C. Schoaff
 * @version 1.0
 *
 * $Id: CompositionalStatsModel.java,v 1.1 2005/08/02 19:36:12 samag Exp $
 *
 * $Log: CompositionalStatsModel.java,v $
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

import java.util.Iterator;
import java.util.Set;

import javax.swing.JTable;

import com.endlessloopsoftware.ego.client.statistics.Statistics;

public class CompositionalStatsModel extends StatTableModel
{
	private Integer[][] componentArray;
	private int componentDepth;

	public CompositionalStatsModel(Statistics stats)
	{
		super(stats);
		initModel();
	}

	private void initModel()
	{
		Iterator it = stats.componentSet.iterator();
		int index = 0;
		int maxCount = 0;

		componentArray = new Integer[stats.componentSet.size()][];

		while (it.hasNext())
		{
			Set s = (Set) it.next();

			componentArray[index] = new Integer[s.size()];
			s.toArray(componentArray[index]);

			if (s.size() > maxCount)
			{
				maxCount = s.size();
			}

			index++;
		}

		componentDepth = maxCount;
	}

	public void update()
	{
		initModel();
		this.fireTableStructureChanged();
		fireTableDataChanged();
	}

	public int getColumnCount()
	{
		return (componentArray.length);
	}

	public int getRowCount()
	{
		return (componentDepth);
	}

	public Object getValueAt(int rowIndex, int columnIndex)
	{
		if (rowIndex < componentArray[columnIndex].length)
		{
			return (stats.alterList[componentArray[columnIndex][rowIndex].intValue()]);
		}
		else
		{
			return null;
		}
	}

	public String getColumnName(int column)
	{
		return ("Component " + (column + 1));
	}

	public int getResizeMode()
	{
		return JTable.AUTO_RESIZE_OFF;
	}
}
