package com.endlessloopsoftware.ego.client;

import java.awt.AWTEvent;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.PrintWriter;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;
import com.endlessloopsoftware.elsutils.files.ExtensionFileFilter;
import com.endlessloopsoftware.elsutils.files.FileCreateException;

import com.endlessloopsoftware.ego.Shared;
import com.endlessloopsoftware.elsutils.AboutBox;
import com.endlessloopsoftware.elsutils.files.FileHelpers;
import com.endlessloopsoftware.ego.client.graph.GraphData;

/**
 * <p>
 * Title: Egocentric Network Researcher
 * </p>
 * <p>
 * Description: Configuration Utilities for an Egocentric network study
 * </p>
 * <p>
 * Copyright: Copyright (c) 2004
 * </p>
 * <p>
 * Company: Endless Loop Software
 * </p>
 * 
 * @author Peter C. Schoaff
 * @version 2.1
 */

public class ClientFrame extends JFrame {
	private final JMenuBar jMenuBar1 = new JMenuBar();

	private final JMenu jMenuFile = new JMenu("File");

	private final JMenu jMenuHelp = new JMenu("Help");

	private final JMenuItem jMenuHelpAbout = new JMenuItem("About");

	private final JMenuItem saveStudySummary = new JMenuItem(
			"Save Study Summary");

	private final JMenuItem exit = new JMenuItem("Exit");

	public final JMenuItem saveAlterSummary = new JMenuItem(
			"Save Alter Summary");

	public final JMenuItem saveTextSummary = new JMenuItem(
			"Save Text Answer Summary");

	public final JMenuItem saveAdjacencyMatrix = new JMenuItem(
			"Save Adjacency Matrix");

	public final JMenuItem saveWeightedAdjacencyMatrix = new JMenuItem(
			"Save Weighted Adjacency Matrix");

	public final JMenuItem saveGraph = new JMenuItem("Save Graph as JPEG image");
	
	public final JMenuItem saveGraphSettings = new JMenuItem("Save graph settings");

	public final JMenuItem saveInterview = new JMenuItem("Save Interview");
	
	public final JMenuItem recalculateStatistics = new JMenuItem("Recalculate Statistics");

	public final JMenuItem close = new JMenuItem("Return to Main Menu");

	public final JMenuItem saveInterviewStatistics = new JMenuItem(
			"Save Interview Statistics");

	// Construct the frame
	public ClientFrame() {
		enableEvents(AWTEvent.WINDOW_EVENT_MASK);

		try {
			jbInit();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// Component initialization
	private void jbInit() throws Exception {
		this.setSize(new Dimension(700, 600));
		this.setTitle("Egocentric Networks Study Tool");

		createMenuBar(EgoClient.SELECT);

		this.setContentPane(new JPanel());

		jMenuHelpAbout.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				jMenuHelpAbout_actionPerformed(e);
			}
		});

		exit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				jMenuFileExit_actionPerformed(e);
			}
		});

		saveStudySummary.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(ActionEvent e) {
				saveStudySummary_actionPerformed(e);
			}
		});

		saveGraph.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(ActionEvent e) {
				saveGraph_actionPerformed(e);
			}
		});

		saveGraphSettings.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(ActionEvent e) {
				saveGraphSettings_actionPerformed(e);
			}
		});
		
		saveInterview.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					EgoClient.interview.completeInterview();
				} catch (FileCreateException ex) {
					ex.printStackTrace();
				}
			}
		});
		
		recalculateStatistics.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(ActionEvent e) {
				EgoClient.interview = EgoClient.storage.readInterview();
				if(EgoClient.interview != null)
					ViewInterviewPanel.gotoPanel();
			}
		});
		
		close
		.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(ActionEvent e) {
				SourceSelectPanel.gotoPanel(false);
			}
		});
	}

	public void flood() {
		Dimension size = this.getSize();
		this.pack();
		this.setSize(size);
		this.validate();
	}

	// File | Exit action performed
	public void jMenuFileExit_actionPerformed(ActionEvent e) {
		if (EgoClient.interview != null) {
			EgoClient.interview.exit();
		}

		System.exit(0);
	}

	// Help | About action performed
	public void jMenuHelpAbout_actionPerformed(ActionEvent e) {
		JOptionPane.showMessageDialog(this,
				"Egonet is an egocentric network study tool." +
				"\n\nThanks to: Dr. Chris McCarty, University of Florida",
				"About Egonet", JOptionPane.PLAIN_MESSAGE);
	}

	// Overridden so we can exit when window is closed
	protected void processWindowEvent(WindowEvent e) {
		super.processWindowEvent(e);
		if (e.getID() == WindowEvent.WINDOW_CLOSING) {
			jMenuFileExit_actionPerformed(null);
		}
	}

	public void createMenuBar(int mode) {
		jMenuBar1.removeAll();
		jMenuFile.removeAll();
		jMenuHelp.removeAll();

		// File Menu
		if (mode == EgoClient.VIEW_SUMMARY) {
			jMenuFile.add(saveStudySummary);
			jMenuFile.add(close);
			jMenuFile.addSeparator();
			jMenuFile.add(exit);
		} else if (mode == EgoClient.VIEW_INTERVIEW) {
			/*******************************************************************
			 * Create Menu Bar
			 ******************************************************************/
			jMenuFile.add(saveAlterSummary);
			jMenuFile.add(saveTextSummary);
			jMenuFile.add(saveAdjacencyMatrix);
			jMenuFile.add(saveWeightedAdjacencyMatrix);
			jMenuFile.add(saveGraph);
			jMenuFile.add(saveGraphSettings);
			jMenuFile.add(saveInterview);
			jMenuFile.add(recalculateStatistics);
			jMenuFile.addSeparator();
			jMenuFile.add(close);
		} else {
			jMenuFile.add(exit);
		}
		jMenuBar1.add(jMenuFile);

		// Help Menu
		jMenuHelp.add(jMenuHelpAbout);
		jMenuBar1.add(jMenuHelp);

		this.setJMenuBar(jMenuBar1);
	}

	void saveStudySummary_actionPerformed(ActionEvent e) {
		String name = FileHelpers.formatForCSV(EgoClient.study.getStudyName());
		String filename = name + "_Summary";
		PrintWriter w = EgoClient.storage.newStatisticsPrintWriter(
				"Study Summary", "csv", filename);

		if (w != null) {
			try {
				((SummaryPanel) EgoClient.frame.getContentPane())
						.writeStudySummary(w);
			} finally {
				w.close();
			}
		}
	}

	void saveGraph_actionPerformed(ActionEvent e) {
		String fileName;
		fileName = EgoClient.interview.getName() + "_graph";
		File currentDirectory = new File(EgoClient.storage.getPackageFile()
				.getParent()
				+ "/Graphs");
		currentDirectory.mkdir();

		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setCurrentDirectory(currentDirectory);
		fileChooser.setSelectedFile(new File(fileName + ".jpeg"));
		fileChooser.setDialogTitle("Save Graph");
		fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);

		// ExtensionFileFilter jpegFilter = new ExtensionFileFilter("JPEG
		// Files",".jpeg");
		FileFilter imageFilter = new ImageFilter();
		fileChooser.addChoosableFileFilter(imageFilter);

		int returnValue = fileChooser.showSaveDialog(this);
		if (returnValue == JFileChooser.APPROVE_OPTION) {
			File imageFile = fileChooser.getSelectedFile();
			System.out.println(imageFile.getName());
			GraphData.writeJPEGImage(imageFile);
		}

	}
	
	void saveGraphSettings_actionPerformed(ActionEvent e) {
		String fileName;
		fileName = EgoClient.interview.getName() + "_graphSettings";
		File currentDirectory = new File(EgoClient.storage.getPackageFile()
				.getParent()
				+ "/Graphs");
		currentDirectory.mkdir();

		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setCurrentDirectory(currentDirectory);
		fileChooser.setSelectedFile(new File(fileName + ".settings"));
		fileChooser.setDialogTitle("Save Graph Settings");
		fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);

		// ExtensionFileFilter jpegFilter = new ExtensionFileFilter("JPEG
		// Files",".jpeg");
		ExtensionFileFilter filter = new ExtensionFileFilter("Graph Settings", "settings");
		fileChooser.addChoosableFileFilter(filter);

		int returnValue = fileChooser.showSaveDialog(this);
		if (returnValue == JFileChooser.APPROVE_OPTION) {
			File settingsFile = fileChooser.getSelectedFile();
			System.out.println(settingsFile.getName());
			EgoClient.storage.writeGraphSettings(settingsFile);
		}

	}
}