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

import java.util.Iterator;
import java.util.Set;

import javax.swing.JTable;

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
		Iterator<Set<Integer>> it = stats.componentSet.iterator();
		int index = 0;
		int maxCount = 0;

		componentArray = new Integer[stats.componentSet.size()][];

		while (it.hasNext())
		{
			Set<Integer> s = it.next();

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
