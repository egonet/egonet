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
package com.endlessloopsoftware.ego.client.statistics.models;

import javax.swing.JTable;

import com.endlessloopsoftware.ego.client.statistics.Statistics;
import com.endlessloopsoftware.egonet.Question;

public class QSummaryModel extends StatTableModel
{
	private int answerDepth;

	public QSummaryModel(Statistics stats)
	{
		super(stats);
		initModel();
	}

	private void initModel()
	{
		int maxCount = 0;
		for (int i = 0; i < stats.alterStatArray.length; i++)
		{
			if (stats.alterStatArray[i].answerTotals.length > maxCount)
			{
				maxCount = stats.alterStatArray[i].answerTotals.length;
			}
		}

		answerDepth = maxCount;
	}

	public void update()
	{
		initModel();
		this.fireTableStructureChanged();
		fireTableDataChanged();
	}

	public int getColumnCount()
	{
		return (answerDepth + 1);
	}

	public int getRowCount()
	{
		return (stats.alterStatArray.length);
	}

	public Object getValueAt(int rowIndex, int columnIndex)
	{
		if (columnIndex == 0)
		{
			return (stats.alterStatArray[rowIndex].qTitle);
		}
		else if (columnIndex <= stats.alterStatArray[rowIndex].answerTotals.length)
		{
			if ((stats.alterStatArray[rowIndex].answerType == Question.NUMERICAL) && (columnIndex == 1))
			{
				return (
					"Average: "
						+ stats.alterStatArray[rowIndex].answerTotals[0] / stats.alterStatArray[rowIndex].answerCount);
			}
			else if (stats.alterStatArray[rowIndex].answerType == Question.CATEGORICAL)
			{
				String s = null;
				try
				{
					if (stats.alterStatArray[rowIndex].answerCount == 0)
					{
						s = stats.alterStatArray[rowIndex].answerText[columnIndex - 1] + ": n/a";
					}
					else
					{
						s =
							stats.alterStatArray[rowIndex].answerText[columnIndex
								- 1]
								+ ": "
								+ ((stats.alterStatArray[rowIndex].answerTotals[columnIndex - 1] * 100)
									/ stats.alterStatArray[rowIndex].answerCount)
								+ "%";
					}
				}
				catch (Exception ex)
				{
					s = "Statistics Generation Error";
					System.err.println("Error in StatTableModel::getValueAt; " + ex);
				}

				return s;
			}
			else
			{
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
		if (column == 0)
		{
			return ("Question");
		}
		else
		{
			return ("Answer " + column);
		}
	}

	public int getResizeMode()
	{
		return JTable.AUTO_RESIZE_OFF;
	}
}
