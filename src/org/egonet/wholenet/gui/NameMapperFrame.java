package org.egonet.wholenet.gui;

import java.io.File;
import java.util.List;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;

public class NameMapperFrame extends JFrame {

	private final List<File> mappableFiles;
	public NameMapperFrame(List<File> mappableFiles) {
		this.mappableFiles = mappableFiles;
	}

	public class MapperTableModel extends AbstractTableModel {

		public int getColumnCount() {
			// TODO Auto-generated method stub
			return 0;
		}

		public int getRowCount() {
			// TODO Auto-generated method stub
			return 0;
		}

		public Object getValueAt(int rowIndex, int columnIndex) {
			// TODO Auto-generated method stub
			return null;
		}
		
	}
}
