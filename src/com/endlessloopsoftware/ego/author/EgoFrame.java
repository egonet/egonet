package com.endlessloopsoftware.ego.author;

import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Observable;
import java.util.Observer;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.text.DefaultEditorKit;

import com.endlessloopsoftware.ego.Question;
import com.endlessloopsoftware.ego.Shared;
import com.endlessloopsoftware.ego.Study;
import com.endlessloopsoftware.elsutils.AboutBox;

/**
 * <p>
 * Title: Egocentric Network Researcher
 * </p>
 * <p>
 * Description: Configuration Utilities for an Egocentric network study
 * </p>
 * <p>
 * Copyright: Copyright (c) 2002
 * </p>
 * <p>
 * Company: Endless Loop Software
 * </p>
 * 
 * @author Peter C. Schoaff
 * @version 1.0
 * 
 * $Id: EgoFrame.java,v 1.1 2005/08/02 19:36:04 samag Exp $
 */

public class EgoFrame extends JFrame implements Observer {
	int lastTab = 0;
	int curTab = 0;

	private JPanel contentPane;
	private boolean waitCursor = false;

	private final JMenuBar jEgonetMenuBar = new JMenuBar();
	private final JMenu jMenuFile = new JMenu("File");
	private final JMenuItem jMenuFileNew = new JMenuItem("New Study");
	private final JMenuItem jMenuFileOpen = new JMenuItem("Open Study");
	private final JMenuItem jMenuFileClose = new JMenuItem("Close Study");
	private final JMenuItem jMenuFileImport = new JMenuItem(
			"Import Questions...");
	private final JMenuItem jMenuFileExport = new JMenuItem(
			"Export Questions...");
	private final JMenuItem jMenuFileSaveAs = new JMenuItem("Save Study As...");
	private final JMenuItem jMenuFileSave = new JMenuItem("Save Study");
	private final JMenuItem jMenuFileUpload = new JMenuItem("Upload Study");
	private final JMenuItem jMenuFileSelectStudy = new JMenuItem(
			"Select Active Study");
	private final JMenuItem jMenuFileExit = new JMenuItem("Quit");
	private final JMenu jMenuEdit = new JMenu("Edit");
	private final JMenuItem jMenuEditCut = new JMenuItem(
			new DefaultEditorKit.CutAction());
	private final JMenuItem jMenuEditCopy = new JMenuItem(
			new DefaultEditorKit.CopyAction());
	private final JMenuItem jMenuEditPaste = new JMenuItem(
			new DefaultEditorKit.PasteAction());
	private final JMenu jMenuHelp = new JMenu("Help");
	private final JMenuItem jMenuHelpAbout = new JMenuItem("About");

	private final JTabbedPane jTabbedPane = new JTabbedPane();
	private final BorderLayout borderLayout1 = new BorderLayout();

	private final StudyPanel study_panel = new StudyPanel(this);

	private final EgoQPanel[] questionPanel = {
			null,
			(EgoQPanel) new AuthoringQuestionPanel(Question.EGO_QUESTION),
			(EgoQPanel) new PromptPanel(Question.ALTER_PROMPT),
			(EgoQPanel) new AuthoringQuestionPanel(Question.ALTER_QUESTION),
			(EgoQPanel) new AuthoringQuestionPanel(Question.ALTER_PAIR_QUESTION), };

	// Construct the frame
	public EgoFrame() {
		enableEvents(AWTEvent.WINDOW_EVENT_MASK);
		try {
			jbInit();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// Component initialization
	private void jbInit() throws Exception {
		// Listen for window closing
		this.addWindowListener(new CloseListener());
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);

		contentPane = (JPanel) this.getContentPane();
		contentPane.setLayout(borderLayout1);
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		this.setSize(screenSize);
		this.setExtendedState(this.getExtendedState()|JFrame.MAXIMIZED_BOTH);
		this.setTitle("Egocentric Network Study");

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
		jMenuFile.addSeparator();
		jMenuFile.add(jMenuFileSave);
		jMenuFile.add(jMenuFileSaveAs);
		jMenuFile.addSeparator();
		jMenuFile.add(jMenuFileUpload);
		jMenuFile.add(jMenuFileSelectStudy);
		jMenuFile.addSeparator();
		jMenuFile.add(jMenuFileExit);

		jMenuEdit.add(jMenuEditCut);
		jMenuEdit.add(jMenuEditCopy);
		jMenuEdit.add(jMenuEditPaste);
		jMenuHelp.add(jMenuHelpAbout);
		jEgonetMenuBar.add(jMenuFile);
		jEgonetMenuBar.add(jMenuEdit);
		jEgonetMenuBar.add(jMenuHelp);
		this.setJMenuBar(jEgonetMenuBar);

		jTabbedPane.setTabPlacement(JTabbedPane.TOP);
		jTabbedPane.add(study_panel, "Study");

		for (int i = Question.MIN_QUESTION_TYPE; i <= Question.MAX_QUESTION_TYPE; i++) {
			jTabbedPane.add(questionPanel[i], Question.questionTypeString(i));
		}
		contentPane.add(jTabbedPane);

		/***********************************************************************
		 * Action Listeners for Menu Events
		 */
		jMenuFileNew.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				jMenuFileNew_actionPerformed(e);
			}
		});

		jMenuFileOpen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				jMenuFileOpen_actionPerformed(e);
			}
		});

		jMenuFileClose.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				jMenuFileClose_actionPerformed(e);
			}
		});

		jMenuFileSave.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				jMenuFileSave_actionPerformed(e);
			}
		});

		jMenuFileSaveAs.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				jMenuFileSaveAs_actionPerformed(e);
			}
		});

		jMenuFileUpload.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				jMenuFileUpload_actionPerformed(e);
			}
		});

		jMenuFileSelectStudy.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				jMenuFileSelectStudy_actionPerformed(e);
			}
		});

		jMenuFileImport.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				jMenuFileImport_actionPerformed(e);
			}
		});

		jMenuFileExport.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				jMenuFileExport_actionPerformed(e);
			}
		});

		jMenuFileExit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				jMenuFileExit_actionPerformed(e);
			}
		});

		jMenuHelpAbout.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				jMenuHelpAbout_actionPerformed(e);
			}
		});

		/***********************************************************************
		 * Change Listener for tabs
		 */
		jTabbedPane.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				jTabbedPane_stateChanged(e);
			}
		});

		/* Fill panel, initialize frame */
		EgoNet.study = new Study();
		fillCurrentPanel();
		EgoNet.study.setModified(false);
		updateMenus();
	}

	/***************************************************************************
	 * Updates menus to take dirty question and study into account
	 */
	public void updateMenus() {
		if (EgoNet.storage.getStudyFile() == null) {
			jMenuFileImport.setEnabled(false);
			jMenuFileClose.setEnabled(false);
			jMenuFileSave.setEnabled(false);
			jMenuFileSaveAs.setEnabled(false);
			jMenuFileUpload.setEnabled(false);
			jMenuFileSelectStudy.setEnabled(true);
			jMenuFileExport.setEnabled(false);
			jTabbedPane.setEnabledAt(1, false);
			jTabbedPane.setEnabledAt(2, false);
			jTabbedPane.setEnabledAt(3, false);
			jTabbedPane.setEnabledAt(4, false);
			jTabbedPane.setSelectedIndex(0);
		} else {
			jMenuFileImport.setEnabled(true);
			jMenuFileClose.setEnabled(true);
			jMenuFileSave.setEnabled(EgoNet.study.isCompatible()
					&& EgoNet.study.isModified());
			jMenuFileSaveAs.setEnabled(true);
			jMenuFileUpload.setEnabled(true);
			jMenuFileSelectStudy.setEnabled(true);
			jMenuFileExport.setEnabled(true);
			jTabbedPane.setEnabledAt(1, true);
			jTabbedPane.setEnabledAt(2, true);
			jTabbedPane.setEnabledAt(3, true);
			jTabbedPane.setEnabledAt(4, true);
		}
	}

	/**
	 * New Study menu handler
	 * 
	 * @param e
	 *            Menu UI Event
	 */
	private void jMenuFileNew_actionPerformed(ActionEvent e) {
		boolean ok = closeStudyFile();

		if (ok) {
			EgoNet.storage.newStudyFiles();
			fillCurrentPanel();
			EgoNet.study.setModified(false);
			EgoNet.study.setCompatible(true);
			EgoNet.study.addObserver(this);
			updateMenus();
		}
	}

	/***************************************************************************
	 * Open Study menu handler
	 * 
	 * @param e
	 *            Menu UI Event
	 */
	private void jMenuFileOpen_actionPerformed(ActionEvent e) {
		boolean ok = closeStudyFile();

		if (ok) {
			EgoNet.storage.selectStudy();
			fillCurrentPanel();
			EgoNet.study.setModified(false);
			EgoNet.study.setCompatible(true);
			EgoNet.study.addObserver(this);
			updateMenus();
		}
	}

	private void jMenuFileClose_actionPerformed(ActionEvent e) {
		boolean ok = closeStudyFile();

		if (ok) {
			EgoNet.storage.setStudyFile(null);
			EgoNet.study = new Study();
			fillCurrentPanel();
			EgoNet.study.addObserver(this);
			EgoNet.study.setModified(false);
		}
	}

	private void jMenuFileImport_actionPerformed(ActionEvent e) {
		EgoNet.storage.importQuestions();
		fillCurrentPanel();
	}

	private void jMenuFileExport_actionPerformed(ActionEvent e) {
		EgoNet.storage.exportQuestions();
	}

	private void jMenuFileUpload_actionPerformed(ActionEvent e) {
		JDialog storeDialog = new StoreStudyDialog(this);
		storeDialog.pack();
		/*
		 * this.show(); Code above deprecated. Code change done Changed by sonam
		 * on 08/20/2007
		 */
		storeDialog.setVisible(true);
	}

	private void jMenuFileSelectStudy_actionPerformed(ActionEvent e) {
		JDialog storeDialog = new SetActiveStudyDialog(this);
		storeDialog.pack();
		/*
		 * this.show(); Code above deprecated. Code change done Changed by sonam
		 * on 08/20/2007
		 */
		storeDialog.setVisible(true);
	}

	private void jMenuFileSave_actionPerformed(ActionEvent e) {
		if (EgoNet.storage.getStudyFile() == null) {
			jMenuFileSaveAs_actionPerformed(e);
		} else {
			EgoNet.storage.saveStudyFile();
			EgoNet.study.setModified(false);
		}
	}

	private void jMenuFileSaveAs_actionPerformed(ActionEvent e) {
		EgoNet.storage.saveAsStudyFile();
		fillStudyPanel();
		EgoNet.study.addObserver(this);
		EgoNet.study.setModified(false);
		EgoNet.study.setCompatible(true);
	}

	// File | Exit action performed
	private void jMenuFileExit_actionPerformed(ActionEvent e) {
		boolean exit = closeStudyFile();

		if (exit) {
			System.exit(0);
		}
	}

	// Help | About action performed
	public void jMenuHelpAbout_actionPerformed(ActionEvent e) {

		JOptionPane.showMessageDialog(this,
				"Egonet is an egocentric network study tool." +
				"\n\nThanks to: Dr. Chris McCarty, University of Florida",
				"About Egonet", JOptionPane.PLAIN_MESSAGE);

	}

	/**
	 * Closes question file. If changes made gives user the option of saving.
	 * 
	 * @return False iff user cancels save, True otherwise
	 */
	public boolean closeStudyFile() {
		boolean exit = true;

		if (EgoNet.study.isModified()) {
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

	public void fillCurrentPanel() {
		boolean sd = EgoNet.study.isModified();
		boolean sc = EgoNet.study.isCompatible();

		if (curTab == Question.STUDY_CONFIG) {
			study_panel.fillPanel();
		} else if ((curTab >= Question.MIN_QUESTION_TYPE)
				&& (curTab <= Question.MAX_QUESTION_TYPE)) {
			questionPanel[curTab].fillPanel();
		}

		EgoNet.study.setModified(sd);
		EgoNet.study.setCompatible(sc);
	}

	public void fillStudyPanel() {
		boolean sd = EgoNet.study.isModified();

		if (curTab == Question.STUDY_CONFIG) {
			study_panel.fillPanel();
		}

		EgoNet.study.setModified(sd);
	}

	private void jTabbedPane_stateChanged(ChangeEvent e) {
		lastTab = curTab;
		curTab = jTabbedPane.getSelectedIndex();

		if ((lastTab == Question.STUDY_CONFIG) && (curTab != lastTab)) {
			EgoNet.study.validateQuestions();
		}

		if ((curTab >= Question.MIN_QUESTION_TYPE)
				&& (curTab <= Question.MAX_QUESTION_TYPE)) {
			questionPanel[curTab].fillPanel();
		} else if (curTab == Question.STUDY_CONFIG) {
			study_panel.fillPanel();
		}
	}

	protected void setWaitCursor(boolean waitCursor) {
		this.waitCursor = waitCursor;

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
			jMenuFileExit_actionPerformed(null);
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
}

/*******************************************************************************
 * $Id: EgoFrame.java,v 1.1 2005/08/02 19:36:04 samag Exp $
 * 
 * $Log: EgoFrame.java,v $ Revision 1.1 2005/08/02 19:36:04 samag Initial
 * checkin
 * 
 * Revision 1.12 2004/04/11 00:24:48 admin Fixing headers
 * 
 * Revision 1.11 2004/04/08 15:06:06 admin EgoClient now creates study summaries
 * from Server EgoAuthor now sets active study on server
 * 
 * Revision 1.10 2004/03/28 17:31:31 admin More error handling when uploading
 * study to server Server URL selection dialog for upload
 * 
 * Revision 1.9 2004/03/23 14:58:48 admin Update UI Study creation now occurs in
 * instantiators
 * 
 * Revision 1.8 2004/03/21 14:00:38 admin Cleaned up Question Panel Layout using
 * FOAM
 * 
 * Revision 1.7 2004/03/10 14:32:39 admin Adding client library cleaning up code
 * 
 * Revision 1.6 2004/02/10 20:10:42 admin Version 2.0 beta 3
 * 
 * Revision 1.5 2004/01/23 13:36:07 admin Updating Libraries Allowing upload to
 * web server
 * 
 * Revision 1.4 2003/12/05 19:15:43 admin Extracting Study
 * 
 * Revision 1.3 2003/12/04 15:14:08 admin Merging EgoNet and EgoClient projects
 * so that they can share some common classes more easily.
 * 
 * Revision 1.2 2003/11/25 19:25:43 admin Warn before closing window
 * 
 * Revision 1.1.1.1 2003/06/08 15:09:40 admin Egocentric Network Survey
 * Authoring Module
 * 
 * Revision 1.13 2002/08/30 16:50:27 admin Using Selections
 * 
 * Revision 1.12 2002/08/11 22:26:05 admin Final Statistics window, new file
 * handling
 * 
 * Revision 1.11 2002/08/08 17:07:24 admin Preparing to change file system
 * 
 * Revision 1.10 2002/07/24 14:17:08 admin xml files, links
 * 
 * Revision 1.9 2002/07/18 14:43:05 admin New Alter Prompt Panel, packages
 * 
 * Revision 1.8 2002/06/26 15:43:42 admin More selection dialog work File
 * loading fixes
 * 
 * Revision 1.7 2002/06/25 15:41:01 admin Lots of UI work
 * 
 * Revision 1.6 2002/06/21 22:47:12 admin question lists working again
 * 
 * Revision 1.5 2002/06/21 21:52:50 admin Many changes to event handling, file
 * handling
 * 
 * Revision 1.4 2002/06/19 01:57:04 admin Much UI work done
 * 
 * Revision 1.3 2002/06/16 17:53:10 admin Working with files
 * 
 * Revision 1.2 2002/06/15 14:19:50 admin Initial Checkin of question and survey
 * General file system work
 * 
 */
