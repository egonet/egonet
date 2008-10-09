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
package com.endlessloopsoftware.ego.client;

import java.awt.AWTEvent;
import java.awt.Dimension;
import java.awt.Toolkit;
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
import javax.swing.JTabbedPane;
import javax.swing.ProgressMonitor;
import javax.swing.filechooser.FileFilter;

import com.endlessloopsoftware.elsutils.SwingWorker;
import com.endlessloopsoftware.elsutils.files.ExtensionFileFilter;
import com.endlessloopsoftware.elsutils.files.FileCreateException;
import com.endlessloopsoftware.ego.Shared;
import com.endlessloopsoftware.ego.client.graph.*;
import com.endlessloopsoftware.elsutils.files.FileHelpers;
import com.endlessloopsoftware.ego.client.graph.GraphData;


public class ClientFrame extends JFrame {
	
	/**
	 * Used to create drop down menus of different "modes"
	 */
	public static final int		SELECT				= 0;
	public static final int		DO_INTERVIEW		= 1;
	public static final int		VIEW_INTERVIEW		= 2;
	public static final int		VIEW_SUMMARY		= 3;
	
	private final JMenuBar jMenuBar1 = new JMenuBar();

	private final JMenu jMenuFile = new JMenu("File");

	private final JMenu jMenuHelp = new JMenu("Help");

	private final JMenu jMenuGraph = new JMenu("Graph");

	private final JMenuItem graphProperties = new JMenuItem("Graph Properties");

	private final JMenuItem nodeProperties = new JMenuItem("Node Properties");

	private final JMenuItem edgeProperties = new JMenuItem("Edge Properties");

	private final JMenuItem jMenuHelpAbout = new JMenuItem("About");

	private final JMenuItem saveStudySummary = new JMenuItem("Save Study Summary");

	private final JMenuItem exit = new JMenuItem("Exit");

	public final JMenuItem saveAlterSummary = new JMenuItem("Save Alter Summary");

	public final JMenuItem saveTextSummary = new JMenuItem("Save Text Answer Summary");

	public final JMenuItem saveAdjacencyMatrix = new JMenuItem("Save Adjacency Matrix");

	public final JMenuItem saveWeightedAdjacencyMatrix = new JMenuItem("Save Weighted Adjacency Matrix");


	public final JMenuItem saveGraphSettings = new JMenuItem("Save graph settings");
	public final JMenuItem applyGraphSettings = new JMenuItem("Load/Apply graph settings");
	
	public final JMenuItem saveInterview = new JMenuItem("Save Interview");
	public final JMenuItem saveGraph = new JMenuItem("Save Graph as image");

	public final JMenuItem recalculateStatistics = new JMenuItem(
			"Recalculate Statistics");

	public final JMenuItem close = new JMenuItem("Return to Main Menu");

	public final JMenuItem saveInterviewStatistics = new JMenuItem(
			"Save Interview Statistics");

	private final EgoClient egoClient;
	// Construct the frame
	public ClientFrame(EgoClient egoClient) {
		enableEvents(AWTEvent.WINDOW_EVENT_MASK);
		this.egoClient = egoClient;
		
			jbInit();
	}

	// Component initialization
	private void jbInit() {
		this.setSize(new Dimension(700, 600));
		this.setExtendedState(this.getExtendedState() | JFrame.MAXIMIZED_BOTH);
		this.setTitle("Egocentric Networks Study Tool");

		createMenuBar(ClientFrame.SELECT);

		this.setContentPane(new JPanel());

		jMenuHelpAbout.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				jMenuHelpAbout_actionPerformed(e);
			}
		});

		exit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			    try {
				jMenuFileExit_actionPerformed(e);
			    } catch (Exception ex) { throw new RuntimeException(ex); }
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

		saveGraphSettings
				.addActionListener(new java.awt.event.ActionListener() {
					public void actionPerformed(ActionEvent e) {
						saveGraphSettings_actionPerformed(e);
					}
				});

		saveInterview.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					egoClient.getInterview().completeInterview();
				} catch (FileCreateException ex) {
					throw new RuntimeException(ex);
				}
			}
		});

		applyGraphSettings
				.addActionListener(new java.awt.event.ActionListener() {
					public void actionPerformed(ActionEvent e) {
						try {
							applyGraphSettings_actionPerformed(e);
						} catch (Exception ex) {
							throw new RuntimeException(ex);
						}
					}
				});

		recalculateStatistics
				.addActionListener(new java.awt.event.ActionListener() {
					public void actionPerformed(ActionEvent e) {
						egoClient.setInterview(egoClient.getStorage().readInterview());
						if (egoClient.getInterview() != null)
						    gotoViewInterviewPanel();
					}
				});

		close.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(ActionEvent e) {
				gotoSourceSelectPanel(false);
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
	public void jMenuFileExit_actionPerformed(ActionEvent e) throws Exception{
		if (egoClient.getInterview() != null) {
			egoClient.getInterview().exit();
		}

		System.exit(0);
	}

	// Help | About action performed
	public void jMenuHelpAbout_actionPerformed(ActionEvent e) {
		JOptionPane
				.showMessageDialog(
						this,
						"Egonet is an egocentric network study tool."
								+ "\n\nThanks to: Dr. Chris McCarty, University of Florida",
						"About Egonet", JOptionPane.PLAIN_MESSAGE);
	}

	// Overridden so we can exit when window is closed
	protected void processWindowEvent(WindowEvent e) {
		super.processWindowEvent(e);
		if (e.getID() == WindowEvent.WINDOW_CLOSING) {
			try { jMenuFileExit_actionPerformed(null); } catch (Exception ex) { throw new RuntimeException(ex); }
		}
	}

	public void createMenuBar(int mode) {
		jMenuBar1.removeAll();
		jMenuFile.removeAll();
		jMenuHelp.removeAll();
		jMenuGraph.removeAll();

		// File Menu
		if (mode == ClientFrame.VIEW_SUMMARY) {
			jMenuFile.add(saveStudySummary);
			jMenuFile.add(close);
			jMenuFile.addSeparator();
			jMenuFile.add(exit);
		} else if (mode == ClientFrame.VIEW_INTERVIEW) {
			/*******************************************************************
			 * Create Menu Bar
			 ******************************************************************/
			jMenuFile.add(saveAlterSummary);
			jMenuFile.add(saveTextSummary);
			jMenuFile.add(saveAdjacencyMatrix);
			jMenuFile.add(saveWeightedAdjacencyMatrix);

			jMenuFile.addSeparator();
			jMenuFile.add(saveGraphSettings);
			jMenuFile.add(applyGraphSettings);

			jMenuFile.addSeparator();
			jMenuFile.add(saveGraph);
			jMenuFile.add(saveInterview);
			jMenuFile.add(recalculateStatistics);
			
			jMenuFile.addSeparator();
			jMenuFile.add(close);

			jMenuGraph.add(graphProperties);
			jMenuGraph.add(nodeProperties);
			jMenuGraph.add(edgeProperties);

		} else {
			jMenuFile.add(exit);
		}
		jMenuBar1.add(jMenuFile);
		jMenuHelp.add(jMenuHelpAbout);
		jMenuBar1.add(jMenuHelp);

		this.setJMenuBar(jMenuBar1);
	}

	void saveStudySummary_actionPerformed(ActionEvent e) {
		String name = FileHelpers.formatForCSV(egoClient.getStudy().getStudyName());
		String filename = name + "_Summary";
		PrintWriter w = egoClient.getStorage().newStatisticsPrintWriter(
				"Study Summary", "csv", filename);

		if (w != null) {
			try {
				((SummaryPanel) egoClient.getFrame().getContentPane())
						.writeStudySummary(w);
			} finally {
				w.close();
			}
		}
	}

	void saveGraph_actionPerformed(ActionEvent e) {
		String fileName;
		fileName = egoClient.getInterview().getName() + "_graph";
		File currentDirectory = new File(egoClient.getStorage().getPackageFile()
				.getParent()
				+ "/Graphs");
		currentDirectory.mkdir();

		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setCurrentDirectory(currentDirectory);
		fileChooser.setSelectedFile(new File(fileName + ".jpg"));
		fileChooser.setDialogTitle("Save Graph");
		fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);

		FileFilter imageFilter = new ImageFilter();
		fileChooser.addChoosableFileFilter(imageFilter);

		int returnValue = JFileChooser.APPROVE_OPTION;
		while (returnValue == JFileChooser.APPROVE_OPTION) {
			returnValue = fileChooser.showSaveDialog(this);
			File imageFile = fileChooser.getSelectedFile();

			String fmt = ImageFilter.getExtension(imageFile);
			if (fmt != null && imageFilter.accept(imageFile)) {
				System.out.println(imageFile.getName());
				GraphData.writeImage(imageFile, fmt);
				break;
			} else {
				JOptionPane
						.showMessageDialog(this,
								"I don't recognize that image format. Please try again.");
			}
		}

	}

	void saveGraphSettings_actionPerformed(ActionEvent e) {
		String[] name = egoClient.getInterview().getName();
		String fileName = "/" + name[0] + "_" + name[1] + ".xml";

		final File currentDirectory = new File(egoClient.getStorage()
				.getPackageFile().getParent(), "Graphs");
		currentDirectory.mkdir();
		File file = new File(currentDirectory.getAbsolutePath() + fileName);

		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setCurrentDirectory(currentDirectory);
		fileChooser.setSelectedFile(new File(fileName));
		fileChooser.setDialogTitle("Save Graph Settings");
		fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		fileChooser.setSelectedFile(file);

		ExtensionFileFilter filter = new ExtensionFileFilter("Graph Settings","xml");
		fileChooser.addChoosableFileFilter(filter);

		int returnValue = fileChooser.showSaveDialog(this);
		if (returnValue == JFileChooser.APPROVE_OPTION) {
			File settingsFile = fileChooser.getSelectedFile();

			GraphRenderer.getGraphSettings().saveSettingsFile(settingsFile);
		}
	}
	
	protected void applyGraphSettings_actionPerformed(ActionEvent e) {
		String[] name = egoClient.getInterview().getName();
		String fileName = "/" + name[0] + "_" + name[1] + ".xml";

		final File currentDirectory = new File(egoClient.getStorage().getPackageFile().getParent(), "Graphs");
		currentDirectory.mkdir();
		File file = new File(currentDirectory.getAbsolutePath() + fileName);

		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setCurrentDirectory(currentDirectory);
		fileChooser.setSelectedFile(new File(fileName));
		fileChooser.setDialogTitle("Load Graph Settings");
		fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		fileChooser.setSelectedFile(file);

		ExtensionFileFilter filter = new ExtensionFileFilter("Graph Settings","xml");
		fileChooser.addChoosableFileFilter(filter);

		int returnValue = fileChooser.showOpenDialog(this);
		if (returnValue == JFileChooser.APPROVE_OPTION) {
			File settingsFile = fileChooser.getSelectedFile();

			try {
				GraphRenderer.getGraphSettings().loadSettingsFile(settingsFile);
			} catch (Throwable cause) {
				cause.printStackTrace();
			}
		}
	}
	
	public void gotoSourceSelectPanel(boolean center)
	   {
	      /* Return to first screen */
//	      egoClient.getFrame().setVisible(false);
	    JTabbedPane tabbedPane = new JTabbedPane();
	    tabbedPane.addTab("Local Files", new ClientPanel(egoClient));
	    // this.addTab("Remote Server", new ServerInterviewChooser());
	    
	      egoClient.getFrame().setContentPane(tabbedPane);
	      egoClient.getFrame().createMenuBar(ClientFrame.SELECT);
	      egoClient.getFrame().pack();
	      //egoClient.getFrame().setSize(600, 500);
	      egoClient.getFrame().setExtendedState(egoClient.getFrame().getExtendedState()|JFrame.MAXIMIZED_BOTH);
	      
	      if (center)
	      {
	        //Center the window
	        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
	        Dimension frameSize = egoClient.getFrame().getSize();
	        if (frameSize.height > screenSize.height)
	        {
	            frameSize.height = screenSize.height;
	        }
	        if (frameSize.width > screenSize.width)
	        {
	            frameSize.width = screenSize.width;
	        }
	        egoClient.getFrame().setLocation((screenSize.width - frameSize.width) / 2, (screenSize.height - frameSize.height) / 2);
	        
	      }
	     
	     egoClient.getFrame().setVisible(true);
	   }
	
	public void gotoViewInterviewPanel()
	   {
	      final ProgressMonitor progressMonitor = new ProgressMonitor(egoClient.getFrame(), "Calculating Statistics", "", 0, 100);
	        final SwingWorker worker = new SwingWorker() 
	        {
	            public Object construct() 
	            {
	                // Build Screen
	                egoClient.getFrame().setVisible(false);
	                Shared.setWaitCursor(egoClient.getFrame(), true);
	                progressMonitor.setProgress(5);
	                egoClient.getFrame().setContentPane(new ViewInterviewPanel(egoClient, progressMonitor));
	                progressMonitor.setProgress(95);
	                egoClient.getFrame().createMenuBar(ClientFrame.VIEW_INTERVIEW);
	                egoClient.getFrame().pack();
	                // egoClient.getFrame().setSize(640, 530);
	                egoClient.getFrame().setExtendedState(egoClient.getFrame().getExtendedState()|JFrame.MAXIMIZED_BOTH);

	                return egoClient.getFrame();
	          }
	          
	          public void finished()
	            {
	            Shared.setWaitCursor(egoClient.getFrame(), false);
	             progressMonitor.close();
	                egoClient.getFrame().setVisible(true);
	          }
	      };
	      
	     progressMonitor.setProgress(0);
	     progressMonitor.setMillisToDecideToPopup(0);
	     progressMonitor.setMillisToPopup(0);
	    
	     worker.start();
	   }
	
	   /**
     * Hides the static frame egoClient.getFrame() and initializes it with an
     * entirely new QuestionPanel
     */
    public void gotoClientQuestionPanel() {
        /* Return to first screen */
        egoClient.getFrame().setVisible(false);
        egoClient.getFrame().setContentPane(new ClientQuestionPanel(egoClient));
        egoClient.getFrame().pack();

        if (egoClient.getUiPath() == ClientFrame.DO_INTERVIEW) {
            // egoClient.getFrame().setSize(600, 530);
            egoClient.getFrame().setExtendedState(egoClient.getFrame().getExtendedState()
                    | JFrame.MAXIMIZED_BOTH);
        } else {
            // egoClient.getFrame().setSize(640, 530);
            egoClient.getFrame().setExtendedState(egoClient.getFrame().getExtendedState()
                    | JFrame.MAXIMIZED_BOTH);
        }

        egoClient.getFrame().setVisible(true);
    }
    
    public void gotoSummaryPanel(StatRecord[] stats)
    {
       // Build Screen
       egoClient.getFrame().setVisible(false);
       Shared.setWaitCursor(egoClient.getFrame(), true);
       egoClient.getFrame().setContentPane(new SummaryPanel(egoClient, stats));
       egoClient.getFrame().createMenuBar(ClientFrame.VIEW_SUMMARY);
       egoClient.getFrame().pack();
      // egoClient.getFrame().setSize(640, 530);
       egoClient.getFrame().setExtendedState(egoClient.getFrame().getExtendedState()|JFrame.MAXIMIZED_BOTH);
       Shared.setWaitCursor(egoClient.getFrame(), false);
       egoClient.getFrame().setVisible(true);
    }
    
    public void gotoStartPanel()
    {
        /* Return to first screen */
        egoClient.getFrame().setVisible(false);
        egoClient.getFrame().setContentPane(new StartPanel(egoClient));
        egoClient.getFrame().pack();
        egoClient.getFrame().setSize(350, 350);
        //egoClient.getFrame().setExtendedState(egoClient.getFrame().getExtendedState()|JFrame.MAXIMIZED_BOTH);
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension frameSize = egoClient.getFrame().getSize();
        if (frameSize.height > screenSize.height)
        {
            frameSize.height = screenSize.height;
        }
        if (frameSize.width > screenSize.width)
        {
            frameSize.width = screenSize.width;
        }
        egoClient.getFrame().setLocation((screenSize.width - frameSize.width) / 2, (screenSize.height - frameSize.height) / 2);
        egoClient.getFrame().setVisible(true);
    }

    public void gotoSummaryPanel()
    {
       final ProgressMonitor progressMonitor = new ProgressMonitor(egoClient.getFrame(), "Calculating Statistics", "", 0, 100);
       final SwingWorker worker = new SwingWorker() 
       {
          public Object construct()
          {
             // Build Screen
             egoClient.getFrame().setVisible(false);
             Shared.setWaitCursor(egoClient.getFrame(), true);
             egoClient.getFrame().setContentPane(new SummaryPanel(progressMonitor));
             egoClient.getFrame().createMenuBar(ClientFrame.VIEW_SUMMARY);
             egoClient.getFrame().pack();
             //egoClient.getFrame().setSize(640, 530);
             egoClient.getFrame().setExtendedState(egoClient.getFrame().getExtendedState()|JFrame.MAXIMIZED_BOTH);
             return egoClient.getFrame();
          }

          public void finished()
          {
             Shared.setWaitCursor(egoClient.getFrame(), false);
             egoClient.getFrame().setVisible(true);

             if (progressMonitor.isCanceled())
             {
                gotoSourceSelectPanel(false);
             }
             progressMonitor.close();
          }
       };

       progressMonitor.setProgress(0);
       progressMonitor.setMillisToDecideToPopup(0);
       progressMonitor.setMillisToPopup(0);

       worker.start();
    }
}