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

import javax.swing.JCheckBoxMenuItem;
import java.awt.AWTEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;

import javax.swing.JInternalFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JFileChooser;
import javax.swing.ProgressMonitor;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.InternalFrameListener;
import javax.swing.filechooser.FileFilter;

import org.egonet.gui.EgoStore;
import org.egonet.gui.MDIChildFrame;
import org.egonet.io.EdgeListWriter;
import org.egonet.mdi.MDIContext;
import org.egonet.util.CatchingAction;
import org.egonet.util.ExtensionFileFilter;
import org.egonet.util.FileHelpers;
import org.egonet.util.ImageFilter;
import org.egonet.util.Name;
import org.egonet.util.SwingWorker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.endlessloopsoftware.ego.client.graph.*;
import com.endlessloopsoftware.ego.client.statistics.StatRecord;
import com.endlessloopsoftware.egonet.Interview;
import com.endlessloopsoftware.egonet.Question;
import com.endlessloopsoftware.egonet.Shared;
import com.endlessloopsoftware.egonet.Study;


// implement MdiChildFrame and molest the public menu!
public class ClientFrame extends MDIChildFrame implements InternalFrameListener {

	final private static Logger logger = LoggerFactory.getLogger(ClientFrame.class);
	
	/**
	 * Used to create drop down menus of different "modes"
	 */
	public static final int		SELECT				= 0;
	public static final int		DO_INTERVIEW		= 1;
	public static final int		VIEW_INTERVIEW		= 2;
	public static final int		VIEW_SUMMARY		= 3;
	
	private final JMenuBar jMenuBar1 = new JMenuBar();

	private final JMenu jMenuFile = new JMenu("File");

	private final JMenu jMenuGraph = new JMenu("Graph");

	private final JMenuItem graphProperties = new JMenuItem("Graph Properties");

	private final JMenuItem nodeProperties = new JMenuItem("Node Properties");

	private final JMenuItem edgeProperties = new JMenuItem("Edge Properties");

	private final JMenuItem saveStudySummary = new JMenuItem("Save Study Summary");

	private final JMenuItem exportInterview = new JMenuItem("Export interview as...");
	
	private final JMenuItem exit = new JMenuItem("Exit");

	public final JMenuItem saveAlterSummary = new JMenuItem("Save Alter Summary");

	public final JMenuItem saveTextSummary = new JMenuItem("Save Text Answer Summary");

	public final JMenuItem saveAdjacencyMatrix = new JMenuItem("Save Adjacency Matrix");

	public final JMenuItem saveWeightedAdjacencyMatrix = new JMenuItem("Save Weighted Adjacency Matrix");


	public final JMenuItem saveGraphSettings = new JMenuItem("Save graph settings");
	public final JMenuItem applyGraphSettings = new JMenuItem("Load/Apply graph settings");
	public final JCheckBoxMenuItem detailedTooltips = new JCheckBoxMenuItem("Show extended node tooltips");
	
	
	public final JMenuItem saveInterview = new JMenuItem("Save Interview");
	public final JMenuItem saveGraph = new JMenuItem("Save Graph as image");
	public final JMenuItem saveGraphCoordinates = new JMenuItem("Save Graph coordinates");
	public final JMenuItem saveEdgeList = new JMenuItem("Save Edgelist");
	
	

	public final JMenuItem recalculateStatistics = new JMenuItem("Recalculate Statistics");

	public final JMenuItem close = new JMenuItem("Return to Main Menu");

	public final JMenuItem saveInterviewStatistics = new JMenuItem("Save Interview Statistics");

	private final EgoClient egoClient;
	// Construct the frame
	public ClientFrame(EgoClient egoClient) {
		enableEvents(AWTEvent.WINDOW_EVENT_MASK);
		this.egoClient = egoClient;
		jbInit();
	}

	// Component initialization
	private void jbInit() {
		//setSize(new Dimension(700, 600));
		setTitle("Interviewing and Analysis Tool");

		createMenuBar(ClientFrame.SELECT);

		setContentPane(new JPanel());
		
		pack();
		
		setMaximizable(true);
		setIconifiable(true);
		setResizable(true);
		setClosable(true);

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
		
		saveGraphCoordinates.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(ActionEvent e) {
				saveGraphCoordinates_actionPerformed(e);
			}
		});
		
		saveEdgeList.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(ActionEvent e) {
				saveEdgeList_actionPerformed(e);
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
					egoClient.getInterview().completeInterview(egoClient.getStorage());
				} catch (Exception ex) {
					throw new RuntimeException(ex);
				}
			}
		});
		
		exportInterview.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					egoClient.getStorage().exportStudy(true);
					egoClient.getInterview();
				} catch (Exception ex) {
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
		detailedTooltips
		.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					GraphRenderer.getGraphSettings().setDetailedTooltips(detailedTooltips.isSelected());
				} catch (Exception ex) {
					throw new RuntimeException(ex);
				}
			}
		});
		
		
		recalculateStatistics.addActionListener(new CatchingAction("recalculateStatistics") {
			public void safeActionPerformed(ActionEvent e) throws Exception {
						EgoStore storage = egoClient.getStorage();
						Interview interview = storage.readInterview(storage.getInterviewFile());
						
						if (interview != null)
						    gotoViewInterviewPanel();
					}
				});

		close.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(ActionEvent e) {
				gotoSourceSelectPanel();
			}
		});
		
		this.addInternalFrameListener(this);
	}

	public void flood() {
		invalidate();
		pack();
		setMinimumSize(getPreferredSize());
	}

	// File | Exit action performed
	public void jMenuFileExit_actionPerformed(ActionEvent e) {
		if (egoClient.getInterview() != null) {
			try {
				egoClient.getStorage().writeCurrentInterview();
				if(egoClient.getInterview().isComplete()) {
					egoClient.getInterview().exit();
				}
			} catch (Exception e1) {
				throw new RuntimeException(e1);
			}
		}
		dispose();
	}

	// Overridden so we can exit when window is closed
	protected void processWindowEvent(WindowEvent e) {
		//TODO super.processWindowEvent(e);
		if (e.getID() == WindowEvent.WINDOW_CLOSING) {
			try { jMenuFileExit_actionPerformed(null); } catch (Exception ex) { throw new RuntimeException(ex); }
		}
	}

	public void createMenuBar(int mode) {
		jMenuBar1.removeAll();
		jMenuFile.removeAll();
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
			jMenuFile.add(saveGraphCoordinates);
			jMenuFile.add(saveEdgeList);
			jMenuFile.add(detailedTooltips);

			jMenuFile.addSeparator();
			jMenuFile.add(saveGraph);
			jMenuFile.add(saveInterview);
			jMenuFile.add(exportInterview);
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

		this.setJMenuBar(jMenuBar1);
	}

	void saveStudySummary_actionPerformed(ActionEvent e) {
		String name = 
			FileHelpers.formatForCSV(egoClient.getStudy().getStudyName())
			.replaceAll("\"", "");
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
		File currentDirectory = new File(egoClient.getStorage().getStudyFile()
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
				logger.info(imageFile.getName());
				GraphData.writeImage(imageFile, fmt);
				break;
			} else {
				JOptionPane
						.showMessageDialog(this,
								"I don't recognize that image format. Please try again.");
			}
		}

	}
	
	void saveEdgeList_actionPerformed(ActionEvent e) {
		String fileName;
		fileName = egoClient.getInterview().getName() + "_edgelist";
		File currentDirectory = new File(egoClient.getStorage().getStudyFile()
				.getParent()
				+ "/Graphs");
		currentDirectory.mkdir();

		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setCurrentDirectory(currentDirectory);
		fileChooser.setSelectedFile(new File(fileName + ".csv"));
		fileChooser.setDialogTitle("Save Graph EdgeList");
		fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);

		int returnValue = JFileChooser.APPROVE_OPTION;
		while (returnValue == JFileChooser.APPROVE_OPTION) {
			returnValue = fileChooser.showSaveDialog(this);
			File dataFile = fileChooser.getSelectedFile();
			try {
				
				EdgeListWriter fw = new EdgeListWriter(dataFile);
				
				Interview interview = egoClient.getInterview();
				Study study = egoClient.getStudy();
				
				String [] thisInterviewAlterlist = interview.getAlterList();

				Iterator<Long> questions = study.getQuestionOrder(Shared.QuestionType.ALTER_PAIR).iterator();
				while (questions.hasNext()) {
					Question q = study.getQuestion((Long) questions.next());
					int[][] adj = interview.generateAdjacencyMatrix(q, true);

					// loop through adj
					// if adj[i][j] == 1, thisInterviewAlters[i] && thisInterviewAlters[j] are adjacent in final matrix

					fw.writeEdgelist(thisInterviewAlterlist, adj);
				}
				
				fw.close();
			} catch (Exception e1) {
				throw new RuntimeException(e1);
			}
			break;
		}
	}
	
	void saveGraphCoordinates_actionPerformed(ActionEvent e) {
		String fileName;
		fileName = egoClient.getInterview().getName() + "_graph_coordinates";
		File currentDirectory = new File(egoClient.getStorage().getStudyFile()
				.getParent()
				+ "/Graphs");
		currentDirectory.mkdir();

		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setCurrentDirectory(currentDirectory);
		fileChooser.setSelectedFile(new File(fileName + ".csv"));
		fileChooser.setDialogTitle("Save Graph Coordinates");
		fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);

		int returnValue = JFileChooser.APPROVE_OPTION;
		while (returnValue == JFileChooser.APPROVE_OPTION) {
			returnValue = fileChooser.showSaveDialog(this);
			File dataFile = fileChooser.getSelectedFile();
			try {
				GraphData.writeCoordinates(dataFile);
			} catch (IOException e1) {
				throw new RuntimeException(e1);
			}
			break;
		}
	}
	
	void saveGraphSettings_actionPerformed(ActionEvent e) {
		String[] name = egoClient.getInterview().getName();
		String fileName = "/" + new Name(name[0],name[1]).toString("_") + ".xml";

		final File currentDirectory = new File(egoClient.getStorage()
				.getStudyFile().getParent(), "Graphs");
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

		final File currentDirectory = new File(egoClient.getStorage().getStudyFile().getParent(), "Graphs");
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
			} catch (Throwable ex) {
				logger.error("Problem loading graph settings",ex);
			}
		}
	}
	
	public void gotoSourceSelectPanel() {
		/* Return to first screen */
		setVisible(false);
		ClientPanel panel = new ClientPanel(egoClient);

		setContentPane(panel);
		createMenuBar(ClientFrame.SELECT);
		flood();

		setVisible(true);
	}
	
	public void gotoViewInterviewPanel()
	   {
	      final ProgressMonitor progressMonitor = new ProgressMonitor(egoClient.getFrame(), "Calculating Statistics", "", 0, 100);
	        final SwingWorker worker = new SwingWorker() 
	        {
	            public Object construct() 
	            {
	                // Build Screen
	                setVisible(false);
	                //com.endlessloopsoftware.egonet.Shared.setWaitCursor(egoClient.getFrame(), true);
	                progressMonitor.setProgress(5);
	                setContentPane(new ViewInterviewPanel(egoClient, progressMonitor));
	                progressMonitor.setProgress(95);
	                createMenuBar(ClientFrame.VIEW_INTERVIEW);
	                pack();
	                // setSize(640, 530);

	                return this;
	          }
	          
	          public void finished()
	            {
	            //com.endlessloopsoftware.egonet.Shared.setWaitCursor(egoClient.getFrame(), false);
	             progressMonitor.close();
	             setVisible(true);
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
       	setVisible(false);
        setContentPane(new ClientQuestionPanel(egoClient));
        pack();
        setVisible(true);
    }
    
    public void gotoSummaryPanel(StatRecord[] stats)
    {
       // Build Screen
       setVisible(false);
       //com.endlessloopsoftware.egonet.Shared.setWaitCursor(egoClient.getFrame(), true);
       setContentPane(new SummaryPanel(egoClient, stats));
       createMenuBar(ClientFrame.VIEW_SUMMARY);
       pack();
      // 
       //com.endlessloopsoftware.egonet.Shared.setWaitCursor(egoClient.getFrame(), false);
       setVisible(true);
    }
    
    public void gotoStartPanel() throws Exception
    {
        /* Return to first screen */
        setVisible(false);
        StartPanel sp = new StartPanel(egoClient);
        setContentPane(sp);
        pack();
        setSize(350, 350);
        /* Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension frameSize = getSize();
        if (frameSize.height > screenSize.height)
        {
            frameSize.height = screenSize.height;
        }
        if (frameSize.width > screenSize.width)
        {
            frameSize.width = screenSize.width;
        }
        setLocation((screenSize.width - frameSize.width) / 2, (screenSize.height - frameSize.height) / 2); */
        setVisible(true);
        sp.doFocus();
    }
    		
    public void quickSaveSummary() {
        final ProgressMonitor progressMonitor = new ProgressMonitor(egoClient.getFrame(), "Calculating Statistics", "", 0, 100);
        final SwingWorker worker = new SwingWorker() 
        {
        	public Object construct()
        	{
                setVisible(false);
        		SummaryPanel summaryPanel = new SummaryPanel(egoClient, progressMonitor);
        		return summaryPanel;
        	}
           
        	public void finished() {
        		if (! progressMonitor.isCanceled()) {
        			String name = 
        				FileHelpers.formatForCSV(egoClient.getStudy().getStudyName())
        				.replaceAll("\"", "");
        			String filename = name + "_Summary";
        			PrintWriter w = egoClient.getStorage().newStatisticsPrintWriter(
        					"Study Summary", "csv", filename);

        			if (w != null) {
        				try {
        					((SummaryPanel) this.getValue()).writeStudySummary(w);
        				} finally {
        					w.close();
        				}
        			}
        		}
        		progressMonitor.close();
                setVisible(true);
        	}
        };

        progressMonitor.setProgress(0);
        progressMonitor.setMillisToDecideToPopup(0);
        progressMonitor.setMillisToPopup(0);

        worker.start();
    }
    
    public void gotoSummaryPanel()
    {
       final ProgressMonitor progressMonitor = new ProgressMonitor(egoClient.getFrame(), "Calculating Statistics", "", 0, 100);
       final SwingWorker worker = new SwingWorker() 
       {
          public Object construct()
          {
             // Build Screen
             setVisible(false);
             //com.endlessloopsoftware.egonet.Shared.setWaitCursor(egoClient.getFrame(), true);
             setContentPane(new SummaryPanel(egoClient, progressMonitor));
             createMenuBar(ClientFrame.VIEW_SUMMARY);
             pack();
             return egoClient.getFrame();
          }

          public void finished()
          {
             //com.endlessloopsoftware.egonet.Shared.setWaitCursor(egoClient.getFrame(), false);
             setVisible(true);

             if (progressMonitor.isCanceled()) {
                gotoSourceSelectPanel();
             }
             progressMonitor.close();
          }
       };

       progressMonitor.setProgress(0);
       progressMonitor.setMillisToDecideToPopup(0);
       progressMonitor.setMillisToPopup(0);

       worker.start();
    }

	public void focusActivated() {
		logger.info(this.getTitle() + " activated");
		
	}

	public void focusDeactivated() {
		logger.info(this.getTitle() + " deactivated");
		
	}

	public JInternalFrame getInternalFrame() {
		return this;
	}

	public void setMdiContext(MDIContext context) {
		// TODO Auto-generated method stub
	}

	public void internalFrameActivated(InternalFrameEvent e) {
		// TODO Auto-generated method stub
		
	}

	public void internalFrameClosed(InternalFrameEvent e) {
		// TODO Auto-generated method stub
		
	}

	public void internalFrameClosing(InternalFrameEvent e) {
		jMenuFileExit_actionPerformed(null);
		
	}

	public void internalFrameDeactivated(InternalFrameEvent e) {
		// TODO Auto-generated method stub
		
	}

	public void internalFrameDeiconified(InternalFrameEvent e) {
		// TODO Auto-generated method stub
		
	}

	public void internalFrameIconified(InternalFrameEvent e) {
		// TODO Auto-generated method stub
		
	}

	public void internalFrameOpened(InternalFrameEvent e) {
		// TODO Auto-generated method stub
		
	}
}