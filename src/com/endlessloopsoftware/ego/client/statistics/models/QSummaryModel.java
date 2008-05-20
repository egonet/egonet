/**
 * <p>Title: Egocentric Network Researcher</p>
 * <p>Description: Configuration Utilities for an Egocentric network study</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: Endless Loop Software</p>
 * @author Peter C. Schoaff
 * @version 1.0
 *
 * $Id: QSummaryModel.java,v 1.1 2005/08/02 19:36:12 samag Exp $
 *
 * $Log: QSummaryModel.java,v $
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

import com.endlessloopsoftware.ego.Question;
import com.endlessloopsoftware.ego.client.statistics.Statistics;

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
