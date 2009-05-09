package org.egonet.wholenet.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;


import javax.swing.*;
import java.io.*;
import java.util.*;

import net.miginfocom.swing.MigLayout;

import org.egonet.io.InterviewFileFilter;
import org.egonet.io.InterviewReader;
import org.egonet.util.CatchingAction;
import org.egonet.util.SwingWorker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.endlessloopsoftware.egonet.Interview;
import com.endlessloopsoftware.egonet.Study;

public class InterviewFileSelectionFrame extends JFrame {

	final private static Logger logger = LoggerFactory.getLogger(InterviewFileSelectionFrame.class);
	
	private final Study study;
	private final File studyFile;
	
	private JList interviewList;
	
	final InterviewFileFilter filter;
	
	public InterviewFileSelectionFrame(final File studyFile, final Study study) throws HeadlessException {
		this(studyFile, study, "Select interviews to include in whole network analysis");
	}

	public InterviewFileSelectionFrame(final File studyFile, final Study study, String title) throws HeadlessException {
		super(title);
		this.studyFile = studyFile;
		this.study = study;
		
		this.filter = new InterviewFileFilter(study, "Interview Files", "int");
		
		build();
	}


	private void build() {
		BorderLayout layout = new BorderLayout();
		setLayout(layout);
		
		interviewList = new JList();
		DefaultListCellRenderer cellRenderer = new DefaultListCellRenderer() {
			@Override
			public Component getListCellRendererComponent(JList list,
					Object value, int index, boolean isSelected,
					boolean cellHasFocus) {
				
				if(value instanceof File) {
					File fValue = (File)value;
					value = fValue.toString().replace(studyFile.getParent().toString(), "");
				}
				
				return super.getListCellRendererComponent(list, value, index, isSelected,
						cellHasFocus);
			}
		};
		interviewList.setCellRenderer(cellRenderer);
		
		JScrollPane interviewScroller = new JScrollPane(interviewList);
		add(interviewScroller, BorderLayout.CENTER);
		
		JPanel fileButtonPanel = new JPanel();
		MigLayout vertical = new MigLayout("flowy,fill");
		fileButtonPanel.setLayout(vertical);
		
		Action addFileAction = new CatchingAction("Add interview file") {
			@Override
			public void safeActionPerformed(ActionEvent e) throws Exception {
				addFileAction();
			}
		};
		fileButtonPanel.add(new JButton(addFileAction), "growx, sg 1");
		
		Action addDirectoryAction = new CatchingAction("Add directory") {
			@Override
			public void safeActionPerformed(ActionEvent e) throws Exception {
				addDirectoryAction();
			}
		};
		fileButtonPanel.add(new JButton(addDirectoryAction), "growx, sg 1");
		
		Action removeFileAction = new CatchingAction("Remove selected") {
			@Override
			public void safeActionPerformed(ActionEvent e) throws Exception {
				removeFileAction();
			}
		};
		fileButtonPanel.add(new JButton(removeFileAction), "growx, sg 1");
		
		add(fileButtonPanel, BorderLayout.WEST);

		List<File> files = getInitialFiles(studyFile.getParentFile());
		interviewList.setModel(new InterviewFileListModel(files));

		JPanel cancelNextButtonPanel = new JPanel();
		MigLayout horiz = new MigLayout("flowx,fill","center");
		cancelNextButtonPanel.setLayout(horiz);
		
		Action cancelAction = new CatchingAction("Cancel") {
			@Override
			public void safeActionPerformed(ActionEvent e) throws Exception {
				dispose();
			}
		};
		cancelNextButtonPanel.add(new JButton(cancelAction), "growx, sg 2");
		
		Action nextAction = new CatchingAction("Continue") {
			@Override
			public void safeActionPerformed(ActionEvent e) throws Exception {
				final InterviewFileListModel model = (InterviewFileListModel)interviewList.getModel();
				

				SwingWorker sw = new SwingWorker() {
					
					NameMapperFrame frame;
					
					@Override
					public Object construct() {
						List<File> mappableFiles = new ArrayList<File>();
						for(Object f : model.toArray())
							mappableFiles.add((File)f);
						
						frame = new NameMapperFrame(study, studyFile, mappableFiles);
						return frame;
					}

					@Override
					public void finished() {
						frame.setVisible(true);
					}
					
				};
				
				sw.start();
				dispose();
			}
		};
		cancelNextButtonPanel.add(new JButton(nextAction), "growx, sg 2");
		
		add(cancelNextButtonPanel, BorderLayout.SOUTH);
		
		pack();
	}

	protected void removeFileAction() {
		int[] selected = interviewList.getSelectedIndices();
		
		if(selected.length > 0) {
			InterviewFileListModel newModel = new InterviewFileListModel();
			
			ListModel model = interviewList.getModel();
			for(int i = 0; i < model.getSize(); i++) {
				Object item = model.getElementAt(i);
				
				boolean remove = false;
				for(int j = 0; j < selected.length; j++) {
					if(selected[j] == i)
						remove = true;
				}
					
				if(!remove)
					newModel.addElement(item);
			}
		
			interviewList.setModel(newModel);
		}
	}

	protected void addDirectoryAction() {
		JFileChooser jfc = new JFileChooser();
		jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		jfc.setMultiSelectionEnabled(false);
		
		int c = jfc.showOpenDialog(this);
		if(c == JFileChooser.CANCEL_OPTION)
			return;
		

		InterviewFileListModel model = (InterviewFileListModel)interviewList.getModel();
		
		File selected = jfc.getSelectedFile();
		if(selected != null && selected.isDirectory()) {
			List<File> results = getInitialFiles(selected);
			for(File result : results) {
				logger.info("Possible new file " + result.getName());
				
				if(!model.contains(result))
					model.addElement(result);
			}
		}
	}

	protected void addFileAction() {
		JFileChooser jfc = new JFileChooser();
		jfc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		jfc.setFileFilter(filter);
		jfc.setMultiSelectionEnabled(true);
		
		int c = jfc.showOpenDialog(this);
		if(c == JFileChooser.CANCEL_OPTION)
			return;
		
		
		InterviewFileListModel model = (InterviewFileListModel)interviewList.getModel();
		
		File [] selected = jfc.getSelectedFiles();
		for(File result : selected) {
			if(!model.contains(result))
				model.addElement(result);
		}
	}
	
	private boolean validFile(File candidate) {
		boolean shouldRead = candidate.isFile() && candidate.canRead() && filter.accept(candidate);
		if(!shouldRead) {
			// it didn't match for some reason
			//logger.info("Couldn't read or wasn't accepted by filter: " + candidate.getName());
			return false;
		}
	
		try {
			InterviewReader reader = new InterviewReader(study, candidate);
			Interview interview = reader.getInterview();

			if(!interview.isComplete()) {
				//logger.info("Interview wasn't completed: " + candidate.getName());
				return false;
			}
		
		} catch (Exception ex) {
			//logger.info("Exception reading: " + candidate.getName());
			return false;
		}

		return true;
	}

	private List<File> getInitialFiles(File directory) {
		
		List<File> output = new ArrayList<File>();
		if(!directory.isDirectory())
			return output;
		
		
		List<File> files = new ArrayList<File>();
		files.add(directory);
		
		while(files.size() > 0) {
			File candidate = files.remove(0);
			if(candidate.isDirectory()) {
				for(File subfile : candidate.listFiles()) {
					files.add(subfile);
				}

				continue;
			}

			if(validFile(candidate)) {
				output.add(candidate);
			    //logger.info("I'd use " + candidate.getName());
			}
		}

		return output;
		
	}

	public Study getStudy() {
		return study;
	}

	public File getStudyFile() {
		return studyFile;
	}

	public class InterviewFileListModel extends DefaultListModel {
		public InterviewFileListModel() {
			super();
		}
		
		public InterviewFileListModel(List<File> files) {
			super();
			for(File f : files)
				addElement(f);
		}
	}
}
