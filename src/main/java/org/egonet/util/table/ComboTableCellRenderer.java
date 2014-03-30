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
package org.egonet.util.table;

import java.awt.Color;
import java.awt.Component;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JTable;
import javax.swing.ListCellRenderer;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;

public class ComboTableCellRenderer implements ListCellRenderer, TableCellRenderer {
	  DefaultListCellRenderer listRenderer = new DefaultListCellRenderer();

	  DefaultTableCellRenderer tableRenderer = new DefaultTableCellRenderer();

	  private void configureRenderer(JLabel renderer, Object value) {
	    if ((value != null) && (value instanceof Color)) {
	      renderer.setIcon(new DiamondIcon((Color) value));
	      renderer.setText("");
	    } else {
	      renderer.setIcon(null);
	      renderer.setText((String) value);
	    }
	  }

	  public Component getListCellRendererComponent(JList list, Object value,
	      int index, boolean isSelected, boolean cellHasFocus) {
	    listRenderer = (DefaultListCellRenderer) listRenderer
	        .getListCellRendererComponent(list, value, index, isSelected,
	            cellHasFocus);
	    configureRenderer(listRenderer, value);
	    return listRenderer;
	  }

	  public Component getTableCellRendererComponent(JTable table, Object value,
	      boolean isSelected, boolean hasFocus, int row, int column) {
	    tableRenderer = (DefaultTableCellRenderer) tableRenderer
	        .getTableCellRendererComponent(table, value, isSelected,
	            hasFocus, row, column);
	    configureRenderer(tableRenderer, value);
	    return tableRenderer;
	  }
	}

