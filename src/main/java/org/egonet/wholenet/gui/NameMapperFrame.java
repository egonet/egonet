package org.egonet.wholenet.gui;

import java.awt.Component;
import java.awt.Cursor;
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
import org.egonet.model.answer.*;
import org.egonet.model.question.AlterQuestion;
import org.egonet.model.question.Question;
import org.egonet.util.CatchingAction;
import org.egonet.util.ExtensionFileFilter;
import org.egonet.util.Name;
import org.egonet.util.SwingWorker;
import org.egonet.wholenet.graph.WholeNetwork;
import org.egonet.wholenet.graph.WholeNetwork.Settings;
import org.egonet.wholenet.graph.WholeNetworkTie.DiscrepancyStrategy;
import org.egonet.wholenet.io.NameMappingReader;
import org.egonet.wholenet.io.NameMappingWriter;
import org.jdesktop.swingx.JXTable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.ac.shef.wit.simmetrics.similaritymetrics.AbstractStringMetric;
import uk.ac.shef.wit.simmetrics.similaritymetrics.Levenshtein;
import net.miginfocom.swing.MigLayout;
import net.sf.functionalj.Function2;
import net.sf.functionalj.Function2Impl;
import net.sf.functionalj.Functions;
import net.sf.functionalj.tuple.Pair;
import net.sf.functionalj.tuple.Triple;

import com.endlessloopsoftware.egonet.Interview;
import com.endlessloopsoftware.egonet.Study;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class NameMapperFrame extends JFrame {

	final private static Logger logger = LoggerFactory.getLogger(NameMapperFrame.class);
	
	private final Study study;
	private final File studyFile;
	
	private final List<Pair<File, Interview>> interviewMap;
	
	private MapperTableModel tableModel;
	
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
		public NameMapping(Study study, Interview interview, String iName, Integer group) {
			super();
			this.interview = interview;
			this.study = study;
			this.alterNumber = -1;
			this.alterName = new Name(iName).toString();
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

		public final String [] columns = {"Alter", "Ego", "Mapping Group"};
		private final List<NameMapping> mappings;
		
		private final List<Question> alterQuestions;
		private final Set<Long> alterQuestionIds;
		private final Map<Triple<Long,String,Integer>,Answer> questionInterviewAlterToAnswer;
		
		public Map<String,String> 
		attributesForInterviewAndAlterId(Interview interview,Integer alterId) {
			Map<String,String> attributes = Maps.newTreeMap();
			String interviewName = interview.toString();
			for(Question question : alterQuestions) {
				Answer answer = 
					questionInterviewAlterToAnswer.get(
							new Triple<Long,String,Integer>(
									question.UniqueId,interviewName,alterId));
				attributes.put(question.title, showAnswer(answer));
			}
			return attributes;
		}
		
		public MapperTableModel() {
			mappings = new ArrayList<NameMapping>();
			alterQuestions = new ArrayList<Question>();
			alterQuestionIds = new TreeSet<Long>();
			questionInterviewAlterToAnswer = new TreeMap<Triple<Long,String,Integer>,Answer>();
			
			for(Question question : study.getQuestions().values()) {
				if(question instanceof AlterQuestion) {
					alterQuestions.add(question);
					alterQuestionIds.add(question.UniqueId);
				}
			}
			
			int group = 1;
			
			for(Pair<File, Interview> entry : interviewMap) {
				Interview interview = entry.getSecond();

				NameMapping egoMapping = new NameMapping(study, interview, entry.getFirst().getName(), group++);
				mappings.add(egoMapping);
				
				String [] alterList = interview.getAlterList();
				for(int i = 0; i < alterList.length; i++) {
					NameMapping mapping = new NameMapping(study, interview, i, group++);
					mappings.add(mapping);
					
				}
				for(Answer answer : interview.get_answers()) {
					if(alterQuestionIds.contains(answer.getQuestionId())) {
						questionInterviewAlterToAnswer.put(
								new Triple<Long,String,Integer>(
										answer.getQuestionId(),
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
				return row.alterName;
			} else if(columnIndex == 1) {
				String egoName = row.getInterview().getIntName();
				return new Name(egoName).toString();
			} else if(columnIndex == 2) {
				return row.group;
			}
			else { // Answer to alter question
				Question question = questionForColumn(columnIndex);
				Triple<Long,String,Integer> key =
					new Triple<Long,String,Integer>(
							question.UniqueId,
							row.getInterview().toString(),
							row.alterNumber);
				return showAnswer(questionInterviewAlterToAnswer.get(key));
			}
		}
		
		private String showAnswer(Answer answer) {
			String result = answer+"";
			return result.equals("-1") || result.equals("null") ? "" : result;
		}
		
		@Override
		public boolean isCellEditable(int rowIndex, int columnIndex) {
			if(columnIndex == 2)
				return true;
			return false;
		}
		
		@Override
	    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
			if(columnIndex == 2 && aValue instanceof String) {
				
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
			String ego = new Name(intv.getIntName()).toString();
			String alter = mapping.alterName;
			
			return alter + " (" + ego + ")";
		}
	}
	
	private Settings settings = new Settings();
	
	private void editSettings(final Settings settings) {
		JPanel panel = new JPanel(new MigLayout());

		panel.add(new JLabel("In how many interviews must an alter be mentioned"),"span,grow");
		panel.add(new JLabel("in order to be included in the whole network?"),"span,grow");
		final JTextField inclusionField = new JTextField(5);
		inclusionField.setText(settings.inclusionThreshold+"");
		panel.add(inclusionField,"wrap");
		
		panel.add(new JSeparator(),"span,grow");
		
		final JCheckBox egoAlwaysIncludedField = 
			new JCheckBox(
					"Always include ego (otherwise ego not mentioned often " +
					"enough is filtered as above)", 
					settings.alwaysIncludeEgo);
		panel.add(egoAlwaysIncludedField,"span,grow");
		
		panel.add(new JSeparator(),"span,grow");
		
		panel.add(new JLabel("Alter tie discrepancies"),"wrap");
		final ButtonGroup group = new ButtonGroup();
		for(DiscrepancyStrategy strategy : DiscrepancyStrategy.values()) {
			JRadioButton button = new JRadioButton(strategy.name()+" - "+strategy.getDescription());
			button.setActionCommand(strategy.name());
			group.add(button);
			panel.add(button,"span,grow");
			if(settings.discrepancyStrategy.equals(strategy)) {
				button.setSelected(true);
			}
		}
		final JCheckBox egoOverrideField =
			new JCheckBox(
					"Ego always connected to own alters " +
					"(even if all other respondents disagree)",
					settings.egoAlwaysTiedToOwnAlters);
		panel.add(egoOverrideField,"span,grow");
		
		panel.add(new JSeparator(),"span,grow");
		
		final JFrame frame = new JFrame("Whole Network Analysis");
		
		panel.add(new JButton(new CatchingAction("Save") {
			public void safeActionPerformed(ActionEvent e) throws Exception {
				try {
					settings.inclusionThreshold = Integer.parseInt(inclusionField.getText());
				} catch(Exception ex) {
					
				}
				settings.alwaysIncludeEgo = egoAlwaysIncludedField.isSelected();
				settings.discrepancyStrategy = 
					DiscrepancyStrategy.valueOf(group.getSelection().getActionCommand());
				settings.egoAlwaysTiedToOwnAlters = egoOverrideField.isSelected();
				frame.dispose();
			}
		}));
		
		frame.setContentPane(panel);
		frame.pack();
		frame.setVisible(true);
	}
	
    private void build() {
		tableModel = new MapperTableModel();
		final JXTable table = new JXTable(tableModel);
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
				setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR)); // Hourglass cursor
				try {
					doDefaultSimilarity(tableModel, 0.8f);
					table.repaint();
				} finally {
					setCursor(Cursor.getDefaultCursor()); // Finished - back to normal cursor
				}
			}
		};
		JButton automatchButton = new JButton(automatchAction);
		add(automatchButton,  "split, growx");

		add(
				new JButton(new CatchingAction("Save") {
					public void safeActionPerformed(ActionEvent e)
							throws Exception {
						File studyFile = NameMapperFrame.this.studyFile;
						File suggestedOutputFile = 
							new File(
									studyFile.getParent(),
									NameMapperFrame.this.study.getStudyName()+".mapping");
						JFileChooser fc = new JFileChooser(suggestedOutputFile);
						fc.setSelectedFile(suggestedOutputFile);
						fc.addChoosableFileFilter(new ExtensionFileFilter("Name Mapping Files","mapping"));
						fc.setDialogTitle("Save name mappings");
						if(fc.showSaveDialog(NameMapperFrame.this) == 
							JFileChooser.APPROVE_OPTION) 
						{
							new NameMappingWriter(tableModel.getMappings())
							.writeToFile(fc.getSelectedFile());
						}
					}
				}), 
				"split, growx");
		add(
				new JButton(new CatchingAction("Load") {
					public void safeActionPerformed(ActionEvent e)
							throws Exception {
						File studyFile = NameMapperFrame.this.studyFile;
						File suggestedOutputFile = 
							new File(studyFile.getParent());
						JFileChooser fc = new JFileChooser(suggestedOutputFile);
						fc.setSelectedFile(suggestedOutputFile);
						fc.addChoosableFileFilter(new ExtensionFileFilter("Name Mapping Files","mapping"));
						fc.setDialogTitle("Load name mappings from file");
						if(fc.showOpenDialog(NameMapperFrame.this) == 
							JFileChooser.APPROVE_OPTION) 
						{
							new NameMappingReader(fc.getSelectedFile())
							.applyTo(tableModel.getMappings());
						}
					}
				}), 
				"split, growx");
		add(new JButton(
				new CatchingAction("Settings") {
					@Override
					public void safeActionPerformed(ActionEvent e) throws Exception {
						editSettings(settings);
					}
				}),
				"split, growx");
		
		Action continueAction = new CatchingAction("Continue") {
			@Override
			public void safeActionPerformed(ActionEvent e) throws Exception {
				
				SwingWorker sw = new SwingWorker() {

					WholeNetworkViewer viewer;
					
					@Override
					public Object construct() {
						
						List<NameMapping> mappings = tableModel.getMappings();
						
						List<Interview> interviews = Lists.newArrayList(
							Functions.map(new Pair<File,Interview>().second, interviewMap));
						
						Function2<Map<String,String>,Interview,Integer> getAlterAttributes = new Function2Impl<Map<String,String>,Interview,Integer>(){
							public Map<String, String> call(
									Interview interview, Integer alterId) {
								return tableModel.attributesForInterviewAndAlterId(interview, alterId);
							}
						};
						
						// do the whole network combination, and export/show it!
						WholeNetwork net = 
							new WholeNetwork(study, interviews, mappings, settings, getAlterAttributes);
						
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
