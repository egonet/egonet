package org.egonet.util.table;
import org.egonet.util.listbuilder.Selection;
import javax.swing.table.AbstractTableModel;

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

	public Class getColumnClass(int c) {
		return getValueAt(0, c).getClass();
	}

	public String getColumnName(int col) {
		return columnNames[col];
	}
}
