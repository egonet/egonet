package org.egonet.util.table;

import org.egonet.util.listbuilder.Selection;
import javax.swing.table.AbstractTableModel;

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

		public Class getColumnClass(int c) {
			return getValueAt(0, c).getClass();
		}

		public String getColumnName(int col) {
			return columnNames[col];
		}
		
		public boolean isCellEditable(int row, int col) {
			return !(col == 1);
		}


}
