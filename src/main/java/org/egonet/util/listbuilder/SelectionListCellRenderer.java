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
package org.egonet.util.listbuilder;

import java.awt.Component;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

import org.egonet.model.question.Selection;

public class SelectionListCellRenderer implements ListCellRenderer<Selection>
{
	protected final DefaultListCellRenderer renderer = new DefaultListCellRenderer();
	
	public Component getListCellRendererComponent(
	        JList list,
	        Selection value,
	        int index,
	        boolean isSelected,
	        boolean cellHasFocus)
	{
		if(value.getClass().equals(Selection.class))
		{
			Selection selection = (Selection)value;
			String strValue = selection.getString() + " (value = " + selection.getValue()+")";
			
			Component listCellRendererComponent = renderer.getListCellRendererComponent(list, strValue, index, isSelected, cellHasFocus);
			if(selection.isAdjacent())
				listCellRendererComponent.setForeground(java.awt.Color.RED);
			return listCellRendererComponent; 
		}
		else
		{
			return renderer.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
		}
	}
}
