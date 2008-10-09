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
import com.endlessloopsoftware.ego.Study;

public class EgoFrame extends JFrame implements Observer {
	int lastTab = 0;
	int curTab = 0;

	private final EgoNet egoNet;
	private JPanel contentPane;

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

	private final StudyPanel study_panel;

	private final EgoQPanel[] questionPanel;
	
	// Construct the frame
	public EgoFrame(EgoNet egoNet) throws Exception
	{
		this.egoNet = egoNet;
		study_panel = new StudyPanel(egoNet);
		
		questionPanel = new EgoQPanel[] {
					null,
					(EgoQPanel) new AuthoringQuestionPanel(egoNet, Question.EGO_QUESTION),
					(EgoQPanel) new PromptPanel(egoNet, Question.ALTER_PROMPT),
					(EgoQPanel) new AuthoringQuestionPanel(egoNet, Question.ALTER_QUESTION),
					(EgoQPanel) new AuthoringQuestionPanel(egoNet, Question.ALTER_PAIR_QUESTION), };

		
		enableEvents(AWTEvent.WINDOW_EVENT_MASK);
		jbInit();
	}

	// Component initialization
	private void jbInit() throws Exception {
		// Listen for window closing
		this.addWindowListener(new CloseListener());
		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);

		contentPane = (JPanel) this.getContentPane();
		contentPane.setLayout(borderLayout1);
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		this.setSize(screenSize);
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
		egoNet.setStudy(new Study());
		fillCurrentPanel();
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
			jMenuFileSave.setEnabled(egoNet.getStudy().isCompatible()
					&& egoNet.getStudy().isModified());
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
	 */
	private void jMenuFileOpen_actionPerformed(ActionEvent e) {
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

	private void jMenuFileClose_actionPerformed(ActionEvent e) {
		boolean ok = closeStudyFile();

		if (ok) {
			egoNet.getStorage().setStudyFile(null);
			egoNet.setStudy(new Study());
			fillCurrentPanel();
			egoNet.getStudy().addObserver(this);
			egoNet.getStudy().setModified(false);
		}
	}

	private void jMenuFileImport_actionPerformed(ActionEvent e) {
		egoNet.getStorage().importQuestions();
		fillCurrentPanel();
	}

	private void jMenuFileExport_actionPerformed(ActionEvent e) {
		egoNet.getStorage().exportQuestions();
	}

	private void jMenuFileUpload_actionPerformed(ActionEvent e) {
		JDialog storeDialog = new StoreStudyDialog(this, egoNet);
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
		if (egoNet.getStorage().getStudyFile() == null) {
			jMenuFileSaveAs_actionPerformed(e);
		} else {
			egoNet.getStorage().saveStudyFile();
			egoNet.getStudy().setModified(false);
		}
	}

	private void jMenuFileSaveAs_actionPerformed(ActionEvent e) {
		egoNet.getStorage().saveAsStudyFile();
		fillStudyPanel();
		egoNet.getStudy().addObserver(this);
		egoNet.getStudy().setModified(false);
		egoNet.getStudy().setCompatible(true);
	}

	// File | Exit action performed
	private void jMenuFileExit_actionPerformed(ActionEvent e) {
		boolean exit = closeStudyFile();

		if (exit) {
			dispose();
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

	public void fillCurrentPanel() {
		boolean sd = egoNet.getStudy().isModified();
		boolean sc = egoNet.getStudy().isCompatible();

		if (curTab == Question.STUDY_CONFIG) {
			study_panel.fillPanel();
		} else if ((curTab >= Question.MIN_QUESTION_TYPE)
				&& (curTab <= Question.MAX_QUESTION_TYPE)) {
			questionPanel[curTab].fillPanel();
		}

		egoNet.getStudy().setModified(sd);
		egoNet.getStudy().setCompatible(sc);
	}

	public void fillStudyPanel() {
		boolean sd = egoNet.getStudy().isModified();

		if (curTab == Question.STUDY_CONFIG) {
			study_panel.fillPanel();
		}

		egoNet.getStudy().setModified(sd);
	}

	private void jTabbedPane_stateChanged(ChangeEvent e)
	{
		lastTab = curTab;
		curTab = jTabbedPane.getSelectedIndex();

		if ((lastTab == Question.STUDY_CONFIG) && (curTab != lastTab)) {
			egoNet.getStudy().validateQuestions();
		}

		if ((curTab >= Question.MIN_QUESTION_TYPE)
				&& (curTab <= Question.MAX_QUESTION_TYPE)) {
			questionPanel[curTab].fillPanel();
		} else if (curTab == Question.STUDY_CONFIG) {
			study_panel.fillPanel();
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