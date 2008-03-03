package org.egonet.util.table;

import javax.swing.*;
import javax.swing.table.*;
import javax.swing.border.*;
import java.awt.Component;
import java.awt.Color;

/** 
 * Renderer on color cell with label - not used now
 * @author sonam
 *
 */
public class ColorRenderer extends JLabel implements TableCellRenderer {
	Border unselectedBorder = null;

	Border selectedBorder = null;

	boolean isBordered = true;

	public ColorRenderer(boolean isBordered) {
		this.isBordered = isBordered;
		setOpaque(true); // MUST do this for background to show up.
	}

	public Component getTableCellRendererComponent(JTable table, Object color,
			boolean isSelected, boolean hasFocus, int row, int column) {
		Color newColor = (Color) color;
		setBackground(newColor);
		if (isBordered) {
			if (isSelected) {
				if (selectedBorder == null) {
					selectedBorder = BorderFactory.createMatteBorder(2, 5, 2,
							5, table.getSelectionBackground());
				}
				setBorder(selectedBorder);
			} else {
				if (unselectedBorder == null) {
					unselectedBorder = BorderFactory.createMatteBorder(2, 5, 2,
							5, table.getBackground());
				}
				setBorder(unselectedBorder);
			}
		}

		setToolTipText("RGB value: " + newColor.getRed() + ", "
				+ newColor.getGreen() + ", " + newColor.getBlue());
		return this;
	}
}
