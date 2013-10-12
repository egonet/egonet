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
package com.endlessloopsoftware.ego.author;

import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.InternalFrameListener;
import javax.swing.text.DefaultEditorKit;

import org.egonet.exceptions.CorruptedInterviewException;
import org.egonet.gui.MDIChildFrame;
import org.egonet.mdi.MDIContext;
import org.egonet.util.CatchingAction;
import org.egonet.util.EgonetAnalytics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.endlessloopsoftware.egonet.Shared;
import com.endlessloopsoftware.egonet.Shared.QuestionType;

public class EgoFrame extends MDIChildFrame implements Observer, InternalFrameListener {
	
	final private static Logger logger = LoggerFactory.getLogger(EgoFrame.class);

    Shared.QuestionType lastTab = Shared.QuestionType.STUDY_CONFIG;
    /**
     * Changes based on jTabbedPane_stateChanged, which is activated when tabs are clicked
     */
	Shared.QuestionType curTab = Shared.QuestionType.STUDY_CONFIG;

	private final EgoNet egoNet;
	private JPanel contentPane;

	private final JMenuBar jEgonetMenuBar = new JMenuBar();
	private final JMenu jMenuFile = new JMenu("File");
	private final JMenuItem jMenuFileNew = new JMenuItem("New Study");
	private final JMenuItem jMenuFileOpen = new JMenuItem("Open Study");
	private final JMenuItem jMenuFileClose = new JMenuItem("Close Study");
	private final JMenuItem jMenuFileImport = new JMenuItem("Import Questions...");
	private final JMenuItem jMenuFileExport = new JMenuItem("Export Questions...");
	private final JMenuItem jMenuFileExportStudy = new JMenuItem("Export Study As...");
	
	private final JMenuItem jMenuFileSaveAs = new JMenuItem("Save Study As...");
	private final JMenuItem jMenuFileSave = new JMenuItem("Save Study");
	
	
	
	private final JMenuItem jMenuFileExit = new JMenuItem("Quit");
	private final JMenu jMenuEdit = new JMenu("Edit");
	private final JMenuItem jMenuEditCut = new JMenuItem(new DefaultEditorKit.CutAction());
	private final JMenuItem jMenuEditCopy = new JMenuItem(new DefaultEditorKit.CopyAction());
	private final JMenuItem jMenuEditPaste = new JMenuItem(new DefaultEditorKit.PasteAction());

	private final JTabbedPane jTabbedPane = new JTabbedPane();
	private final BorderLayout borderLayout1 = new BorderLayout();

	private final StudyPanel study_panel;

	private final Map<QuestionType,EgoQPanel> questionPanel;
	
	// Construct the frame
	public EgoFrame(EgoNet egoNet)
	{
		try {
			this.egoNet = egoNet;
			study_panel = new StudyPanel(egoNet);
			
			questionPanel = new HashMap<QuestionType,EgoQPanel>();
			questionPanel.put(QuestionType.EGO, new AuthoringQuestionPanel(egoNet, Shared.QuestionType.EGO));
			questionPanel.put(QuestionType.ALTER_PROMPT, new PromptPanel(egoNet, Shared.QuestionType.ALTER_PROMPT));
			questionPanel.put(Shared.QuestionType.ALTER, new AuthoringQuestionPanel(egoNet, Shared.QuestionType.ALTER));
			questionPanel.put(Shared.QuestionType.ALTER_PAIR, new AuthoringQuestionPanel(egoNet, Shared.QuestionType.ALTER_PAIR));
			
			enableEvents(AWTEvent.WINDOW_EVENT_MASK);
			jbInit();
		}
		catch (Exception ex) {
			throw new RuntimeException(ex);
		}
		
	}

	// Component initialization
	private void jbInit() throws Exception {
		// Listen for window closing
		//this.addWindowListener(new CloseListener());
		this.addInternalFrameListener(this);
		
		//fixme
		
		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setResizable(true);

		contentPane = new JPanel();
		contentPane.setLayout(borderLayout1);
		setContentPane(contentPane);
		setTitle("Study Design Tool");

		jMenuFileExit.setAccelerator(javax.swing.KeyStroke.getKeyStroke(
				KeyEvent.VK_Q, Toolkit.getDefaultToolkit()
						.getMenuShortcutKeyMask()));
		jMenuFileNew.setAccelerator(javax.swing.KeyStroke.getKeyStroke(
				KeyEvent.VK_N, Toolkit.getDefaultToolkit()
						.getMenuShortcutKeyMask()));
		jMenuFileOpen.setAccelerator(javax.swing.KeyStroke.getKeyStroke(
				KeyEvent.VK_O, Toolkit.getDefaultToolkit()
						.getMenuShortcutKeyMask()));
		jMenuFileClose.setAccelerator(javax.swing.KeyStroke.getKeyStroke(
				KeyEvent.VK_W, Toolkit.getDefaultToolkit()
						.getMenuShortcutKeyMask()));
		jMenuEditCopy.setAccelerator(javax.swing.KeyStroke.getKeyStroke(
				KeyEvent.VK_C, Toolkit.getDefaultToolkit()
						.getMenuShortcutKeyMask()));
		jMenuEditCut.setAccelerator(javax.swing.KeyStroke.getKeyStroke(
				KeyEvent.VK_X, Toolkit.getDefaultToolkit()
						.getMenuShortcutKeyMask()));
		jMenuEditPaste.setAccelerator(javax.swing.KeyStroke.getKeyStroke(
				KeyEvent.VK_V, Toolkit.getDefaultToolkit()
						.getMenuShortcutKeyMask()));

		jMenuEditCopy.setText("Copy");
		jMenuEditCut.setText("Cut");
		jMenuEditPaste.setText("Paste");

		jMenuFile.add(jMenuFileNew);
		jMenuFile.add(jMenuFileOpen);
		jMenuFile.add(jMenuFileClose);
		jMenuFile.addSeparator();
		jMenuFile.add(jMenuFileImport);
		jMenuFile.add(jMenuFileExport);
		jMenuFile.add(jMenuFileExportStudy);
		jMenuFile.addSeparator();
		jMenuFile.add(jMenuFileSave);
		jMenuFile.add(jMenuFileSaveAs);
		jMenuFile.addSeparator();
		jMenuFile.add(jMenuFileExit);

		jMenuEdit.add(jMenuEditCut);
		jMenuEdit.add(jMenuEditCopy);
		jMenuEdit.add(jMenuEditPaste);
		jEgonetMenuBar.add(jMenuFile);
		jEgonetMenuBar.add(jMenuEdit);
		this.setJMenuBar(jEgonetMenuBar);

		jTabbedPane.setTabPlacement(JTabbedPane.TOP);
		jTabbedPane.add(study_panel, "Study");

		for (QuestionType qT : QuestionType.values()) {
		    if(qT.equals(QuestionType.STUDY_CONFIG))
		        continue;
			jTabbedPane.add(questionPanel.get(qT), qT.niceName);
		}
		contentPane.add(jTabbedPane);

		/***********************************************************************
		 * Action Listeners for Menu Events
		 */
		jMenuFileNew.addActionListener(new CatchingAction("jMenuFileNew") {
			public void safeActionPerformed(ActionEvent e) throws Exception {
				jMenuFileNew_actionPerformed(e);
			}
		});

		jMenuFileOpen.addActionListener(new CatchingAction("jMenuFileOpen") {
			public void safeActionPerformed(ActionEvent e) throws Exception {
				jMenuFileOpen_actionPerformed(e);
			}
		});

		jMenuFileClose.addActionListener(new CatchingAction("jMenuFileClose") {
			public void safeActionPerformed(ActionEvent e) throws Exception {
				jMenuFileClose_actionPerformed(e);
			}
		});

		jMenuFileSave.addActionListener(new CatchingAction("jMenuFileSave") {
			public void safeActionPerformed(ActionEvent e) throws Exception {
				jMenuFileSave_actionPerformed(e);
			}
		});
		
		jMenuFileExportStudy.addActionListener(new CatchingAction("jMenuFileExportStudy") {
			public void safeActionPerformed(ActionEvent e) throws Exception {
				jMenuFileExportStudy_actionPerformed(e);
			}
		});

		jMenuFileSaveAs.addActionListener(new CatchingAction("jMenuFileSaveAs") {
			public void safeActionPerformed(ActionEvent e) throws Exception {
				jMenuFileSaveAs_actionPerformed(e);
			}
		});

		jMenuFileImport.addActionListener(new CatchingAction("jMenuFileImport") {
			public void safeActionPerformed(ActionEvent e) throws Exception {
				jMenuFileImport_actionPerformed(e);
			}
		});

		jMenuFileExport.addActionListener(new CatchingAction("jMenuFileExport") {
			public void safeActionPerformed(ActionEvent e) throws Exception {
				jMenuFileExport_actionPerformed(e);
			}
		});

		jMenuFileExit.addActionListener(new CatchingAction("jMenuFileExit") {
			public void safeActionPerformed(ActionEvent e) throws Exception {
				jMenuFileExit_actionPerformed(e);
			}
		});

		/***********************************************************************
		 * Change Listener for tabs
		 */
		jTabbedPane.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				try {
					jTabbedPane_stateChanged(e);
				} catch (IOException ex)
				{
					throw new RuntimeException(ex);
				}
			}
		});

		/* Fill panel, initialize frame */
		egoNet.getStorage().createNewStudy();
		fillCurrentPanel();
		
		pack();
		setMinimumSize(getPreferredSize());
		
		setMaximizable(true);
		setIconifiable(true);
		setClosable(true);

		egoNet.getStudy().setModified(false);
		updateMenus();
	}

	/***************************************************************************
	 * Updates menus to take dirty question and study into account
	 */
	public void updateMenus() {
		if (egoNet.getStorage().getStudyFile() == null) {
			jMenuFileImport.setEnabled(false);
			jMenuFileClose.setEnabled(false);
			jMenuFileSave.setEnabled(false);
			jMenuFileExportStudy.setEnabled(false);
			jMenuFileSaveAs.setEnabled(false);
			jMenuFileExport.setEnabled(false);
			jTabbedPane.setEnabledAt(1, false);
			jTabbedPane.setEnabledAt(2, false);
			jTabbedPane.setEnabledAt(3, false);
			jTabbedPane.setEnabledAt(4, false);
			jTabbedPane.setSelectedIndex(0);
		} else {
			jMenuFileImport.setEnabled(true);
			jMenuFileClose.setEnabled(true);
			jMenuFileSave.setEnabled(egoNet.getStudy().isCompatible()
					&& egoNet.getStudy().isModified());
			jMenuFileSaveAs.setEnabled(true);
			jMenuFileExportStudy.setEnabled(true);
			jMenuFileExport.setEnabled(true);
			jTabbedPane.setEnabledAt(1, true);
			jTabbedPane.setEnabledAt(2, true);
			jTabbedPane.setEnabledAt(3, true);
			jTabbedPane.setEnabledAt(4, true);
		}
	}

	// split the file menu functionality out into a class of functionality
	// and a class of the UI
	/**
	 * New Study menu handler
	 * 
	 * @param e
	 *            Menu UI Event
	 * @throws IOException 
	 */
	private void jMenuFileNew_actionPerformed(ActionEvent e) throws IOException {
		boolean ok = closeStudyFile();

		if (ok) {
			egoNet.getStorage().newStudyFiles();
			fillCurrentPanel();
			egoNet.getStudy().setModified(false);
			egoNet.getStudy().setCompatible(true);
			egoNet.getStudy().addObserver(this);
			updateMenus();
		}
	}

	/***************************************************************************
	 * Open Study menu handler
	 * 
	 * @param e
	 *            Menu UI Event
	 * @throws IOException 
	 */
	private void jMenuFileOpen_actionPerformed(ActionEvent e) throws IOException {
		boolean ok = closeStudyFile();

		if (ok) {
			egoNet.getStorage().selectStudy();
			fillCurrentPanel();
			egoNet.getStudy().setModified(false);
			egoNet.getStudy().setCompatible(true);
			egoNet.getStudy().addObserver(this);
			updateMenus();
		}
	}

	private void jMenuFileClose_actionPerformed(ActionEvent e) throws IOException {
		boolean ok = closeStudyFile();

		if (ok) {
			egoNet.getStorage().createNewStudy();
			
			fillCurrentPanel();
			egoNet.getStudy().addObserver(this);
			egoNet.getStudy().setModified(false);
		}
	}

	private void jMenuFileImport_actionPerformed(ActionEvent e) throws Exception {
		egoNet.getStorage().importQuestions();
		fillCurrentPanel();
	}

	private void jMenuFileExport_actionPerformed(ActionEvent e) {
		egoNet.getStorage().exportQuestions();
	}

	private void jMenuFileSave_actionPerformed(ActionEvent e) throws IOException {
		EgonetAnalytics.track("save study"); // track!
		if (egoNet.getStorage().getStudyFile() == null) {
			jMenuFileSaveAs_actionPerformed(e);
		} else {
			egoNet.getStorage().saveStudyFile();
			egoNet.getStudy().setModified(false);
		}
	}
	
	private void jMenuFileExportStudy_actionPerformed(ActionEvent e) throws IOException, CorruptedInterviewException {
		egoNet.getStorage().exportStudy(false);
	}
	
	private void jMenuFileSaveAs_actionPerformed(ActionEvent e) throws IOException {
		egoNet.getStorage().saveAsStudyFile();
		fillStudyPanel();
		egoNet.getStudy().addObserver(this);
		egoNet.getStudy().setModified(false);
		egoNet.getStudy().setCompatible(true);
	}

	// File | Exit action performed
	public void jMenuFileExit_actionPerformed(ActionEvent e) {
		try { 
		boolean exit = closeStudyFile();

		if (exit) {
			dispose();
		}
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}
	}

	/**
	 * Closes question file. If changes made gives user the option of saving.
	 * 
	 * @return False iff user cancels save, True otherwise
	 * @throws IOException 
	 */
	public boolean closeStudyFile() throws IOException {
		boolean exit = true;

		if (egoNet.getStudy().isModified()) {
			int confirm = JOptionPane
					.showConfirmDialog(
							this,
							"There are unsaved changes to the study. Would you like to save the study now?",
							"Save Study Changes",
							JOptionPane.YES_NO_CANCEL_OPTION);

			if (confirm == JOptionPane.YES_OPTION) {
				jMenuFileSave_actionPerformed(null);
			} else if (confirm == JOptionPane.CANCEL_OPTION) {
				exit = false;
			}
		}

		return exit;
	}

	public void fillCurrentPanel() throws IOException {
		boolean sd = egoNet.getStudy().isModified();
		boolean sc = egoNet.getStudy().isCompatible();

		if (curTab == Shared.QuestionType.STUDY_CONFIG) {
			study_panel.fillPanel();
		} else {
			questionPanel.get(curTab).fillPanel();
		}

		egoNet.getStudy().setModified(sd);
		egoNet.getStudy().setCompatible(sc);
	}

	public void fillStudyPanel() throws IOException {
		boolean sd = egoNet.getStudy().isModified();

		if (curTab == Shared.QuestionType.STUDY_CONFIG) {
			study_panel.fillPanel();
		}

		egoNet.getStudy().setModified(sd);
	}

	private void jTabbedPane_stateChanged(ChangeEvent e) throws IOException
	{
		lastTab = curTab;
		Component selectedTab = jTabbedPane.getSelectedComponent();
		if(selectedTab instanceof StudyPanel)
		{
		    curTab = Shared.QuestionType.STUDY_CONFIG;
		} else {
		    curTab = ((EgoQPanel)selectedTab).questionType;
		}

		if ((lastTab == Shared.QuestionType.STUDY_CONFIG) && (curTab != lastTab)) {
			egoNet.getStudy().validateQuestions();
		}

		if (curTab == Shared.QuestionType.STUDY_CONFIG) {
            study_panel.fillPanel();
        } else {
			questionPanel.get(curTab).fillPanel();
		} 
	}

	protected void setWaitCursor(boolean waitCursor) {

		if (waitCursor) {
			this.getGlassPane().setVisible(true);
			this.getGlassPane().setCursor(
					Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		} else {
			this.getGlassPane().setCursor(
					Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			this.getGlassPane().setVisible(false);
		}
	}

	class CloseListener extends WindowAdapter {
		/*
		 * (non-Javadoc)
		 * 
		 * @see java.awt.event.WindowListener#windowClosed(java.awt.event.WindowEvent)
		 */
		public void windowClosing(WindowEvent arg0) {
			logger.info("Window close event received");
			try {
				jMenuFileExit_actionPerformed(null);
			} catch (Throwable cause)
			{
				throw new RuntimeException(cause);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
	 */
	public void update(Observable o, Object arg) {
		updateMenus();
	}
	
	public void focusActivated() {
		//logger.info(this.getTitle() + " activated");
		
	}

	public void focusDeactivated() {
		//logger.info(this.getTitle() + " deactivated");
		
	}

	public JInternalFrame getInternalFrame() {
		return this;
	}

	public void setMdiContext(MDIContext context) {
		
	}

	public void internalFrameActivated(InternalFrameEvent e) {
		
		
	}

	public void internalFrameClosed(InternalFrameEvent e) {
		
		
	}

	public void internalFrameClosing(InternalFrameEvent e) {
		jMenuFileExit_actionPerformed(null);
	}

	public void internalFrameDeactivated(InternalFrameEvent e) {
		
		
	}

	public void internalFrameDeiconified(InternalFrameEvent e) {
		
		
	}

	public void internalFrameIconified(InternalFrameEvent e) {
		
		
	}

	public void internalFrameOpened(InternalFrameEvent e) {
		
		
	}
}