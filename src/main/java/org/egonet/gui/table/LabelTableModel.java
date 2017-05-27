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
package org.egonet.gui.table;
import javax.swing.table.AbstractTableModel;

import org.egonet.model.Selection;

public class LabelTableModel extends AbstractTableModel {
	private String[] columnNames = { "Responses" };

	private Selection[] rowData;

	public LabelTableModel(Selection[] data) {
		super();
		this.rowData = new Selection[data.length];
		this.rowData = data;
	}

	public LabelTableModel() {
		super();
	}

	public int getColumnCount() {
		return columnNames.length;
	}

	public int getRowCount() {
		return rowData.length;
	}

	public Selection getValueAt(int rowIndex, int columnIndex) {
		return rowData[rowIndex];
	}

	public void setValueAt(Selection selection, int rowIndex) {
		rowData[rowIndex] = selection;
	}

	public Class<?> getColumnClass(int c) {
		return getValueAt(0, c).getClass();
	}

	public String getColumnName(int col) {
		return columnNames[col];
	}
}
