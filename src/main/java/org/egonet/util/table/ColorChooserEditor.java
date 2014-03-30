package org.egonet.util.table;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.AbstractCellEditor;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JTable;
import javax.swing.table.TableCellEditor;

public class ColorChooserEditor extends AbstractCellEditor implements TableCellEditor {

	  private JButton delegate = new JButton();

	  Color savedColor;

	  public ColorChooserEditor() {
	   
		  ActionListener actionListener = new ActionListener() {
	      public void actionPerformed(ActionEvent actionEvent) {
	        Color color = JColorChooser.showDialog(delegate,
	            "Color Chooser", savedColor);
	        ColorChooserEditor.this.changeColor(color);
	      }
	    };
	    
	    delegate.addActionListener(actionListener);
	  }

	  public Object getCellEditorValue() {
	    return savedColor;
	  }

	  private void changeColor(Color color) {
	    if (color != null) {
	      savedColor = color;
	      delegate.setIcon(new DiamondIcon(color));
	    }
	  }

	  public Component getTableCellEditorComponent(JTable table, Object value,
	      boolean isSelected, int row, int column) {
	    changeColor((Color) value);
	    return delegate;
	  }
	}
