/****
 * 
 * Copyright (c) 2007, Endless Loop Software, Inc.
 * 
 *  This file is part of EgoNet.
 *
 *    EgoNet is free software; you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as published by
 *    the Free Software Foundation; either version 3 of the License, or
 *    (at your option) any later version.
 *
 *    EgoNet is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU General Public License for more details.
 *
 *    You should have received a copy of the GNU General Public License
 *    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.endlessloopsoftware.ego.client.statistics.models;


import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;

import com.endlessloopsoftware.ego.client.statistics.Statistics;

public abstract class StatTableModel extends AbstractTableModel
{
	public Statistics stats;
	
	public StatTableModel(Statistics stats)
	{
		this.stats = stats;
	}
	
	public StatTableModel() {}
	
	public int getResizeMode()
	{
		return JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS;
	}

	public void update()
	{
		this.fireTableStructureChanged();
		fireTableDataChanged();
	}
	
	/**
	 * @param stats The stats to set.
	 */
	public void setStats(Statistics stats)
	{
		this.stats = stats;
	}
}
