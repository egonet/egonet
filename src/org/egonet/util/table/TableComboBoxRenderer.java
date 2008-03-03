package org.egonet.util.table;

import javax.swing.table.*;
import javax.swing.JTable;
import javax.swing.JComboBox;
import java.awt.Component;

/**
 * Renderer for combobox - used for shape and size
 * @author sonam
 *
 */
public class TableComboBoxRenderer extends JComboBox implements
		TableCellRenderer {
	public TableComboBoxRenderer(Object[] items) {
		super(items);
	}

	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {
		if (isSelected) {
			setForeground(table.getSelectionForeground());
			super.setBackground(table.getSelectionBackground());
		} else {
			setForeground(table.getForeground());
			setBackground(table.getBackground());
		}

		// Select the current value
		setSelectedItem(value);
		return this;
	}
}
