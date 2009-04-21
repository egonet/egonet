package org.egonet.wholenet.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;

import org.egonet.io.InterviewReader;

import net.sf.functionalj.tuple.Pair;

import com.endlessloopsoftware.egonet.Interview;
import com.endlessloopsoftware.egonet.Study;

public class NameMapperFrame extends JFrame {

	private final Study study;
	private final List<Pair<File, Interview>> interviewMap;
	public NameMapperFrame(Study study, List<File> mappableFiles) {
		super("Whole Network - Alter Name Mapping Editor");
		this.study = study;
		this.interviewMap = new ArrayList<Pair<File, Interview>>(mappableFiles.size());
		this.study.toString();
		
		for(File intFile : mappableFiles) {
			try {
				InterviewReader ir = new InterviewReader(study, intFile);
				Interview intV = ir.getInterview();
				Pair<File, Interview> p = new Pair<File, Interview>(intFile, intV);
				interviewMap.add(p);
			} 
			catch (Exception ex ) {
				System.out.println("Couldn't map " + intFile.getName());
				ex.printStackTrace();
			}
		}
		
		build();
	}

	
	enum MappingStyle { NONE, PRIMARY, OTHER_NAME };

	/**
	 * This class is a data member which will keep track of exactly what should
	 * mapping happen for every alter in every interview. It may point to
	 * another alter that should be treated as the same thing, or it may be set
	 * to never map to anything.
	 */
	public class NameMapping {
		final Study study;
		final Interview interview;
		final String alter;
		
		private Pair<Interview,Integer> destination;
		private MappingStyle style;
		
		public NameMapping(Study study, Interview interview, String alter) {
			super();
			this.interview = interview;
			this.study = study;
			this.style = MappingStyle.NONE;
			this.alter = alter;
		}

		public Pair<Interview, Integer> getDestination() {
			return destination;
		}

		public void setDestination(Pair<Interview, Integer> destination) {
			this.destination = destination;
		}

		public MappingStyle getStyle() {
			return style;
		}

		public void setStyle(MappingStyle style) {
			this.style = style;
		}
	}
	
	public class MapperTableModel extends AbstractTableModel {

		public final String [] columns = {"Ego", "Alter Name", "Mapping Type", "Destination"};
		private final List<NameMapping> mappings;
		
		public MapperTableModel() {
			mappings = new ArrayList<NameMapping>();
			for(Pair<File, Interview> entry : interviewMap) {
				Interview interview = entry.getSecond();

				NameMapping egoMapping = new NameMapping(study, interview, interview.getName()[0] + " " + interview.getName()[1]);
				mappings.add(egoMapping);
				
				for(String alter : interview.getAlterList()) {
					NameMapping mapping = new NameMapping(study, interview, alter);
					mappings.add(mapping);
					
				}
			}
			
			// destinations will probably be a list of all available mappings that are set to primary
		}
		
		public int getColumnCount() {
			return columns.length;
		}

	    public String getColumnName(int column) {
	    	return columns[column];
	    }
		
		public int getRowCount() {
			return mappings.size();
		}

		public Object getValueAt(int rowIndex, int columnIndex) {
			NameMapping row = mappings.get(rowIndex);
			if(columnIndex == 0) {
				return row.interview;
			}
			else if(columnIndex == 1) {
				return row.alter;
			}
			else if(columnIndex == 2) {
				return row.style;
			}
			else if(columnIndex == 3) {
				return row.destination;
			}
			else {
				return row;
			}
		}
		
		@Override
		public boolean isCellEditable(int rowIndex, int columnIndex) {
			if(columnIndex == 2)
				return true;
			
			if(columnIndex == 3 && mappings.get(rowIndex).style.equals(MappingStyle.OTHER_NAME))
				return true;
			
			return false;
		}
		
		@Override
	    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
			NameMapping row = mappings.get(rowIndex);
			if(columnIndex == 2 && aValue instanceof MappingStyle) {
				row.setStyle((MappingStyle)aValue);
			}
			else if(columnIndex == 3) {
				//row.setDestination(destination);
			}
	    }
	}
	
	class InterviewRenderer extends DefaultTableCellRenderer {
		public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
			
			if(value instanceof Interview) {
				Interview intv = (Interview)value;
				value = intv.getName()[0] + " " + intv.getName()[1];
			}
			
			return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
		}
	}
	
    public class MyComboBoxRenderer extends JComboBox implements TableCellRenderer {
        public MyComboBoxRenderer(Object [] items) {
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
    
    public class MyComboBoxEditor extends DefaultCellEditor {
        public MyComboBoxEditor(Object[] items) {
            super(new JComboBox(items));
        }
    }

	
	private void build() {
		BorderLayout layout = new BorderLayout();
		setLayout(layout);
		
		MapperTableModel model = new MapperTableModel();
		
		JTable table = new JTable();
		table.setModel(model);
		table.getColumnModel().getColumn(0).setCellRenderer(new InterviewRenderer());
		
		MyComboBoxRenderer mappingRenderer = new MyComboBoxRenderer(MappingStyle.values());
		MyComboBoxEditor mappingEditor = new MyComboBoxEditor(MappingStyle.values());
		table.getColumnModel().getColumn(2).setCellEditor(mappingEditor);
		table.getColumnModel().getColumn(2).setCellRenderer(mappingRenderer);
		
		JScrollPane scrollPane = new JScrollPane(table);
		add(scrollPane, BorderLayout.CENTER);
		
		pack();
	}
}
