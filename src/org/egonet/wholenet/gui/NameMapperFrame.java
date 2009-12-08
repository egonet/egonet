package org.egonet.wholenet.gui;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import org.egonet.io.InterviewReader;
import org.egonet.util.CatchingAction;
import org.egonet.util.Name;
import org.egonet.util.SwingWorker;
import org.egonet.wholenet.graph.WholeNetwork;
import org.jdesktop.swingx.JXTable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.ac.shef.wit.simmetrics.similaritymetrics.AbstractStringMetric;
import uk.ac.shef.wit.simmetrics.similaritymetrics.Levenshtein;

import net.miginfocom.swing.MigLayout;
import net.sf.functionalj.tuple.Pair;
import net.sf.functionalj.tuple.Triple;

import com.endlessloopsoftware.egonet.Answer;
import com.endlessloopsoftware.egonet.Interview;
import com.endlessloopsoftware.egonet.Question;
import com.endlessloopsoftware.egonet.Study;
import com.endlessloopsoftware.egonet.Shared.QuestionType;

public class NameMapperFrame extends JFrame {

	final private static Logger logger = LoggerFactory.getLogger(NameMapperFrame.class);
	
	private final Study study;
	private final File studyFile;
	
	private final List<Pair<File, Interview>> interviewMap;
	public NameMapperFrame(Study study, File studyFile, List<File> mappableFiles) {
		super("Whole Network - Alter Name Mapping Editor");
		this.study = study;
		this.studyFile = studyFile;
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
				logger.info("Couldn't map " + intFile.getName());
				logger.error(ex.toString());
			}
		}
		
		build();
	}

	/**
	 * This class is a data member which will keep track of exactly what should
	 * mapping happen for every alter in every interview. It may point to
	 * another alter that should be treated as the same thing, or it may be set
	 * to never map to anything.
	 */
	public class NameMapping implements Comparable<NameMapping> {
		final Study study;
		final Interview interview;
		final Integer alterNumber;
		final String alterName;
		
		private Integer group;
		
		public NameMapping(Study study, Interview interview, Integer alterNumber, Integer group) {
			super();
			this.interview = interview;
			this.study = study;
			this.alterNumber = alterNumber;
			this.alterName = interview.getAlterList()[alterNumber];
			this.group = group;
		}
		
		// ego constructor
		public NameMapping(Study study, Interview interview, Integer group) {
			super();
			this.interview = interview;
			this.study = study;
			this.alterNumber = -1;
			this.alterName = new Name(interview.getName()[0],interview.getName()[1]).toString();
			this.group = group;
		}

		public Integer getGroup() {
			return group;
		}

		public void setGroup(Integer group) {
			this.group = group;
		}

		public Study getStudy() {
			return study;
		}

		public Interview getInterview() {
			return interview;
		}

		public Integer getAlterNumber() {
			return alterNumber;
		}
		
		public String toString() {
			return alterName;
		}

		public int compareTo(NameMapping o) {
			return alterNumber.compareTo(o.alterNumber);
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + getOuterType().hashCode();
			result = prime * result
					+ ((alterNumber == null) ? 0 : alterNumber.hashCode());
			result = prime * result
					+ ((interview == null) ? 0 : interview.hashCode());
			result = prime * result + ((study == null) ? 0 : study.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (!(obj instanceof NameMapping))
				return false;
			NameMapping other = (NameMapping) obj;
			if (!getOuterType().equals(other.getOuterType()))
				return false;
			if (alterNumber == null) {
				if (other.alterNumber != null)
					return false;
			} else if (!alterNumber.equals(other.alterNumber))
				return false;
			if (interview == null) {
				if (other.interview != null)
					return false;
			} else if (!interview.equals(other.interview))
				return false;
			if (study == null) {
				if (other.study != null)
					return false;
			} else if (!study.equals(other.study))
				return false;
			return true;
		}

		private NameMapperFrame getOuterType() {
			return NameMapperFrame.this;
		}
	}
	
	public class MapperTableModel extends AbstractTableModel {

		public final String [] columns = {"Alter Name (Source Ego)", "Mapping Group"};
		private final List<NameMapping> mappings;
		
		private final List<Question> alterQuestions;
		private final Set<Long> alterQuestionIds;
		private final Map<Triple<Long,String,Integer>,Answer> questionInterviewAlterToAnswer;
		
		public MapperTableModel() {
			mappings = new ArrayList<NameMapping>();
			alterQuestions = new ArrayList<Question>();
			alterQuestionIds = new TreeSet<Long>();
			questionInterviewAlterToAnswer = new TreeMap<Triple<Long,String,Integer>,Answer>();
			
			for(Question question : study.getQuestions().values()) {
				if(question.questionType.equals(QuestionType.ALTER)) {
					alterQuestions.add(question);
					alterQuestionIds.add(question.UniqueId);
				}
			}
			
			int group = 1;
			
			for(Pair<File, Interview> entry : interviewMap) {
				Interview interview = entry.getSecond();

				NameMapping egoMapping = new NameMapping(study, interview, group++);
				mappings.add(egoMapping);
				
				String [] alterList = interview.getAlterList();
				for(int i = 0; i < alterList.length; i++) {
					NameMapping mapping = new NameMapping(study, interview, i, group++);
					mappings.add(mapping);
					
				}
				for(Answer answer : interview.get_answers()) {
					if(alterQuestionIds.contains(answer.questionId)) {
						questionInterviewAlterToAnswer.put(
								new Triple<Long,String,Integer>(
										answer.questionId,
										interview.toString(),
										answer.firstAlter()),
								answer);
					}
				}
			}
		}
		
		public int getColumnCount() {
			return columns.length + alterQuestions.size();
		}

	    public String getColumnName(int column) {
	    	return column < columns.length ? columns[column] : 
	    		questionForColumn(column).title;
	    }
		
	    private Question questionForColumn(int column) {
	    	return alterQuestions.get(column - columns.length);
	    }
	    
		public int getRowCount() {
			return mappings.size();
		}

		public Object getValueAt(int rowIndex, int columnIndex) {
			NameMapping row = mappings.get(rowIndex);
			if(columnIndex == 0) {
				return row;
			}
			else if(columnIndex == 1) {
				return row.group;
			}
			else { // Answer to alter question
				Question question = questionForColumn(columnIndex);
				Triple<Long,String,Integer> key =
					new Triple<Long,String,Integer>(
							question.UniqueId,
							row.getInterview().toString(),
							row.alterNumber);
				String result = questionInterviewAlterToAnswer.get(key)+"";
				return result.equals("-1") || result.equals("null") ? "" : result;
			}
		}
		
		@Override
		public boolean isCellEditable(int rowIndex, int columnIndex) {
			if(columnIndex == 1)
				return true;
			return false;
		}
		
		@Override
	    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
			if(columnIndex == 1 && aValue instanceof String) {
				
				Integer i = null;
				try {
					i = Integer.parseInt(aValue.toString());
				} 
				catch(NumberFormatException ex) {
					return;
				}
				
				NameMapping row = mappings.get(rowIndex);
				row.setGroup(i);
			}
	    }

		public List<NameMapping> getMappings() {
			return mappings;
		}

	}
	
	class MappingRenderer implements ListCellRenderer, TableCellRenderer, Serializable {
		
		private final DefaultTableCellRenderer tableDelegate = new DefaultTableCellRenderer();
		private final DefaultListCellRenderer listDelegate = new DefaultListCellRenderer();
		
		public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
			
			if(value instanceof NameMapping) {
				value = convert((NameMapping)value);
			}
			
			return tableDelegate.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
		}

		public Component getListCellRendererComponent(JList list, Object value,
				int index, boolean isSelected, boolean cellHasFocus) {
			
			if(value instanceof NameMapping) {
				value = convert((NameMapping)value);
			}
			
			return listDelegate.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
		}
		
		private String convert(NameMapping mapping) {
			Interview intv = mapping.interview;
			String ego = new Name(intv.getName()[0],intv.getName()[1]).toString();
			String alter = mapping.alterName;
			
			return alter + " (" + ego + ")";
		}
	}
	
    private void build() {
		final MapperTableModel model = new MapperTableModel();
		final JXTable table = new JXTable(model);
		table.setSortable(true);
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		
		// column 0 - source ego/alter
		table.getColumnModel().getColumn(0).setCellRenderer(new MappingRenderer());
		
		// column 1 - mapping group
		; // no changes
		
    	MigLayout layout = new MigLayout("fill", "[grow]");
		setLayout(layout);
		
		JScrollPane scrollPane = 
			new JScrollPane(table,
					ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
					ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);		
		add(new JLabel("Please perform alter/ego name mappings"),  "growx, wrap");
		add(scrollPane, "grow, span, wrap");

		Action cancelAction = new CatchingAction("Cancel") {
			@Override
			public void safeActionPerformed(ActionEvent e) throws Exception {
				dispose();
			}
		};
		JButton cancelButton = new JButton(cancelAction);
		add(cancelButton,  "split, growx");
		
		Action automatchAction = new CatchingAction("Automatch") {
			@Override
			public void safeActionPerformed(ActionEvent e) throws Exception {
				doDefaultSimilarity(model, 0.8f);
				table.repaint();
			}
		};
		JButton automatchButton = new JButton(automatchAction);
		add(automatchButton,  "split, growx");
		
		Action continueAction = new CatchingAction("Continue") {
			@Override
			public void safeActionPerformed(ActionEvent e) throws Exception {
				
				SwingWorker sw = new SwingWorker() {

					WholeNetworkViewer viewer;
					
					@Override
					public Object construct() {
						
						List<NameMapping> mappings = model.getMappings();
						
						// do the whole network combination, and export/show it!
						WholeNetwork net = new WholeNetwork(study, interviewMap, mappings);
						net.recompile();
						
						viewer = new WholeNetworkViewer(study, studyFile, net);
						return viewer;
					}
					
					public void finished() { 
						viewer.setVisible(true);
					}
					
				};
				
				sw.start();
				dispose();
				
			}
		};
		JButton continueButton = new JButton(continueAction);
		add(continueButton,  "growx");
		
		
		
		pack();
	}
    
    private void doDefaultSimilarity(MapperTableModel model, float cutoff) {
    	AbstractStringMetric metric = new Levenshtein();
    	
    	Map<Integer,List<NameMapping>> groupings = new HashMap<Integer,List<NameMapping>>();
    	int groupCounter = 1;
    	
    	ArrayList<NameMapping> names = new ArrayList<NameMapping>(model.getMappings());
    	while(names.size() > 0) {
    		NameMapping current = names.remove(0);
    		
    		if(current == null || current.alterName == null)
    			continue;
    		
    		float highest = 0.0f; Integer highestGroup = null;
    		for(Map.Entry<Integer,List<NameMapping>> entry : groupings.entrySet()) {
    			float averageScore = 0; int elementCount = 0;
    			for(NameMapping entryMapping : entry.getValue()) {
    				
    				if(entryMapping.alterName == null)
    					continue;
    				
    				// when figuring a true score, make everything lowercase and trimmed
    				float thisScore = metric.getSimilarity(current.alterName.toLowerCase().trim(), entryMapping.alterName.toLowerCase().trim());
    				if(thisScore >= cutoff)
    					logger.info(current.alterName + " + " + entryMapping.alterName + " = " + thisScore);
    				
    				averageScore += thisScore;
    				elementCount++;
    			}
    			averageScore /= elementCount;
    			if(averageScore >= cutoff && averageScore > highest) {
    				highest = averageScore;
    				highestGroup = entry.getKey();
    			}
    		}

    		// pick an existing group
    		if(highestGroup != null) {
    			List<NameMapping> destGroup = groupings.get(highestGroup);
    			destGroup.add(current);
    		}
    		// create a new group
    		else {
    			List<NameMapping> destGroup = new ArrayList<NameMapping>();
    			destGroup.add(current);
    			groupings.put(groupCounter++, destGroup);
    		}
    	}
    	
    	for(NameMapping map : model.getMappings()) {
    		for(Integer group : groupings.keySet()) {
    			if(groupings.get(group).contains(map)) {
    				map.setGroup(group);
    				break;
    			}
    		}
    	}
    }
}
