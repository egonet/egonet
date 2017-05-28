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
import java.util.Stack;

import javax.swing.JTable;

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
				throw new RuntimeException(ex);
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
