package org.egonet.util.table;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.Component;

import org.egonet.util.listbuilder.Selection;

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
		//	System.out.println("Asked to render a non-selection of type "+value.getClass()+": " + value);
		}
		
		setHorizontalAlignment(CENTER);
		return super.getTableCellRendererComponent(table,value,isSelected,hasFocus,row,column);
	}
}
