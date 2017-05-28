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
package org.egonet.gui.util;

import javax.swing.table.AbstractTableModel;

import org.egonet.model.Selection;

public class ChoosablePropertyTableModel extends AbstractTableModel{

		private String[] columnNames = { "Show Edges", "Responses", "Property"};

		private Object[][] rowData;

		public ChoosablePropertyTableModel(Object[][] data) {
			super();
			this.rowData = new Selection[data.length][data[0].length];
			this.rowData = data;
		}

		public ChoosablePropertyTableModel() {
			super();
		}

		public int getColumnCount() {
			return columnNames.length;
		}

		public int getRowCount() {
			return rowData.length;
		}

		public Object getValueAt(int rowIndex, int columnIndex) {
			return rowData[rowIndex][columnIndex];
		}

		public void setValueAt(Object value, int rowIndex, int colIndex) {
			rowData[rowIndex][colIndex] = value;
			fireTableCellUpdated(rowIndex, colIndex);
		}

		public Class<?> getColumnClass(int c) {
			return getValueAt(0, c).getClass();
		}

		public String getColumnName(int col) {
			return columnNames[col];
		}
		
		public boolean isCellEditable(int row, int col) {
			return !(col == 1);
		}


}
