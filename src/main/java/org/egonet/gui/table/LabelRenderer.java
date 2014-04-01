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

import javax.swing.*;
import javax.swing.table.*;

import java.awt.Component;

import org.egonet.model.question.Selection;

/**
 * To Renderer the cell displaying selection 
 * @author sonam
 *
 */
public class LabelRenderer extends DefaultTableCellRenderer {

	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {
		
		if(value instanceof Selection)
		{
			Selection s = (Selection) value;
			value = s.getString();
		//} else {
		//	logger.info("Asked to render a non-selection of type "+value.getClass()+": " + value);
		}
		
		setHorizontalAlignment(CENTER);
		return super.getTableCellRendererComponent(table,value,isSelected,hasFocus,row,column);
	}
}
