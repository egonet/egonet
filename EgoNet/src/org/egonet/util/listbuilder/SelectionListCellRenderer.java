package org.egonet.util.listbuilder;

import java.awt.Component;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

public class SelectionListCellRenderer implements ListCellRenderer
{
	protected final DefaultListCellRenderer renderer = new DefaultListCellRenderer();
	
	public Component getListCellRendererComponent(
	        JList list,
	        Object value,
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
