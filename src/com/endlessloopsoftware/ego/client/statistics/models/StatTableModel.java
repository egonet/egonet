package com.endlessloopsoftware.ego.client.statistics.models;

/**
 * <p>Endless Loop Software Abstract Statistics Table Model</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: Endless Loop Software, Inc.</p>
 *
 * @author  Peter C. Schoaff
 * @version $Revision: 1.1 $
 * 
 * $Date: 2005/08/02 19:36:11 $
 * $Id: StatTableModel.java,v 1.1 2005/08/02 19:36:11 samag Exp $
 */

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
