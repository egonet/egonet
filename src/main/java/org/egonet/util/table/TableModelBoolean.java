package org.egonet.util.table;

import javax.swing.table.AbstractTableModel;

/**
 * Custom table model for Graph Data
 * @author sonam
 *
 */
public class TableModelBoolean extends AbstractTableModel {

	private String[] columnNames = { "ShowEdges", "Choice", "Color", "Shape", "Size" };

	private Object[][] rowData;

	public TableModelBoolean(Object[][] data) {
		super();
		this.rowData = new Object[data.length][data[0].length];
		this.rowData = data;
	}

	public TableModelBoolean() {
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

	public Class<?> getColumnClass(int c) {
		return getValueAt(0, c).getClass();
	}

	public boolean isCellEditable(int row, int col) {
		return !(col == 1);
	}

	public void setValueAt(Object value, int row, int col) {
		rowData[row][col] = value;
		fireTableCellUpdated(row, col);
	}

	 public String getColumnName(int col) 
	 { return columnNames[col]; }
}
