package com.endlessloopsoftware.ego.client.statistics;

/**
 * <p>Title: Egocentric Network Researcher</p>
 * <p>Description: Configuration Utilities for an Egocentric network study</p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: Endless Loop Software</p>
 * @author Peter C. Schoaff
 * @version 1.0
 *
 * $Id: StatisticsFrame.java,v 1.1 2005/08/02 19:36:05 samag Exp $
 *
 */

import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.io.PrintWriter;
import java.util.Iterator;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import com.endlessloopsoftware.ego.Question;
import com.endlessloopsoftware.ego.client.EgoClient;
import com.endlessloopsoftware.ego.client.SourceSelectPanel;
import com.endlessloopsoftware.ego.client.statistics.models.BetweennessModel;
import com.endlessloopsoftware.ego.client.statistics.models.CliqueModel;
import com.endlessloopsoftware.ego.client.statistics.models.ClosenessModel;
import com.endlessloopsoftware.ego.client.statistics.models.CompositionalStatsModel;
import com.endlessloopsoftware.ego.client.statistics.models.DegreeModel;
import com.endlessloopsoftware.ego.client.statistics.models.InterviewSummaryModel;
import com.endlessloopsoftware.ego.client.statistics.models.QSummaryModel;
import com.endlessloopsoftware.elsutils.files.FileCreateException;
import com.endlessloopsoftware.ego.client.graph.GraphPanel;



public class StatisticsFrame extends JPanel {
	private Statistics stats = null;

	private JTabbedPane tabs = new JTabbedPane();

	private JPanel summaryPanel = null;

	private JPanel dcPanel = null;

	private JPanel ccPanel = null;

	private JPanel bcPanel = null;

	private JPanel cliquePanel = null;

	private JPanel componentPanel = null;

	private JPanel qSummaryPanel = null;

//	private JPanel graphPanel = new GraphPanel();

	//private JComboBox alterQuestionMenu = new JComboBox();

	public StatisticsFrame() {
		try {
			jbInit();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void jbInit() throws Exception {
		boolean studyStatable = false;

		/***********************************************************************
		 * Fill in alter pair question selection menu
		 **********************************************************************/
		//String s = alterQuestionMenu.getActionCommand();
		//alterQuestionMenu.setActionCommand("Initialization");

		Iterator questions = EgoClient.study.getQuestionOrder(
				Question.ALTER_PAIR_QUESTION).iterator();
		while (questions.hasNext()) {
			Question q = EgoClient.study.getQuestion((Long) questions.next());

			if (q.statable) {
				//alterQuestionMenu.addItem(q);
				studyStatable = true;
				stats = EgoClient.interview.generateStatistics(q);
				
				// Use stats to initialize panels
				summaryPanel = new StatisticsArrayPanel(new InterviewSummaryModel(
						stats));
				dcPanel = new StatisticsArrayPanel(new DegreeModel(stats));
				ccPanel = new StatisticsArrayPanel(new ClosenessModel(stats));
				bcPanel = new StatisticsArrayPanel(new BetweennessModel(stats));
				cliquePanel = new StatisticsArrayPanel(new CliqueModel(stats));
				componentPanel = new StatisticsArrayPanel(
						new CompositionalStatsModel(stats));
				qSummaryPanel = new StatisticsArrayPanel(new QSummaryModel(stats));

				/*******************************************************************
				 * Create UI
				 ******************************************************************/
				// setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
				// Container panel = this.getContentPane();
				Container panel = this;

				panel.setLayout(new GridBagLayout());
				tabs.addTab("Structural Measures", summaryPanel);
				tabs.addTab("Compositional Summary", qSummaryPanel);
				tabs.addTab("Degree Centrality", dcPanel);
				tabs.addTab("Closeness Centrality", ccPanel);
				tabs.addTab("Betweenness Centrality", bcPanel);
				tabs.addTab("Cliques", cliquePanel);
				tabs.addTab("Components", componentPanel);
			//	tabs.addTab("Graph", graphPanel);

				/*******************************************************************
				 * Layout
				 ******************************************************************/
				this.add(tabs);
				panel.add(tabs, new GridBagConstraints(0, 0, 1, 1, 1.0, 0.9,
						GridBagConstraints.CENTER, GridBagConstraints.BOTH,
						new Insets(0, 0, 0, 0), 0, 0));
				/*panel.add(alterQuestionMenu, new GridBagConstraints(0, 1, 1, 1,
						0.2, 0.1, GridBagConstraints.SOUTHEAST,
						GridBagConstraints.NONE, new Insets(10, 10, 10, 10), 6, 6));
						*/

				/*******************************************************************
				 * Event Handlers
				 ******************************************************************/
				/*alterQuestionMenu
						.addActionListener(new java.awt.event.ActionListener() {
							public void actionPerformed(ActionEvent e) {
								alterQuestionMenu_actionPerformed(e);
							}
						});*/

				EgoClient.frame.saveAlterSummary
						.addActionListener(new java.awt.event.ActionListener() {
							public void actionPerformed(ActionEvent e) {
								saveAlterSummary_actionPerformed(e);
							}
						});

				EgoClient.frame.saveTextSummary
						.addActionListener(new java.awt.event.ActionListener() {
							public void actionPerformed(ActionEvent e) {
								saveTextSummary_actionPerformed(e);
							}
						});

				EgoClient.frame.saveWeightedAdjacencyMatrix
						.addActionListener(new java.awt.event.ActionListener() {
							public void actionPerformed(ActionEvent e) {
								saveWeightedAdjacencyMatrix_actionPerformed(e);
							}
						});

				EgoClient.frame.saveAdjacencyMatrix
						.addActionListener(new java.awt.event.ActionListener() {
							public void actionPerformed(ActionEvent e) {
								saveAdjacencyMatrix_actionPerformed(e);
							}
						});

				EgoClient.frame.saveInterviewStatistics
						.addActionListener(new java.awt.event.ActionListener() {
							public void actionPerformed(ActionEvent e) {
								saveInterviewStatistics_actionPerformed(e);
							}
						});

				updateAll();
			}
		}
		//alterQuestionMenu.setActionCommand(s);

		/***********************************************************************
		 * Check that there is at least one statable question, if not abort this
		 */
		if (!studyStatable) {
			/*******************************************************************
			 * No Statable Questions
			 */
			this.setLayout(new GridLayout());
			this
					.add(new JLabel(
							"No questions with adjacent and non-adjacent selections found."));
		}

		EgoClient.frame.close
				.addActionListener(new java.awt.event.ActionListener() {
					public void actionPerformed(ActionEvent e) {
						close_actionPerformed(e);
					}
				});
	}

	void updateAll() {
		for (int i = 0; i < tabs.getTabCount(); i++) {
			JComponent component = (JComponent) tabs.getComponentAt(i);

			if (component instanceof StatisticsArrayPanel) {
				((StatisticsArrayPanel) component).getTableModel().setStats(
						stats);
				((StatisticsArrayPanel) component).getTableModel().update();
			} 
//			else if (component instanceof GraphPanel) {
//				((GraphPanel) component).init(this);
//			}
		}
	}

	//TODO: We need to do alter pair stats when a question is selected, since we got rid of the drop down list.
	/*void alterQuestionMenu_actionPerformed(ActionEvent e) {
		if (!e.getActionCommand().equals("Initialization")) {
			stats = EgoClient.interview
					.generateStatistics((Question) alterQuestionMenu
							.getSelectedItem());

			updateAll();
		}
	}*/

	void saveAlterSummary_actionPerformed(ActionEvent e) {
		String[] name = EgoClient.interview.getName();
		String filename = name[0] + "_" + name[1] + "_Alter_Summary";
		PrintWriter w = EgoClient.storage.newStatisticsPrintWriter(
				"Alter Summary", "csv", filename);

		if (w != null) {
			try {
				stats.writeAlterArray(w);
			} finally {
				w.close();
			}
		}
	}

	void saveTextSummary_actionPerformed(ActionEvent e) {
		String[] name = EgoClient.interview.getName();
		String filename = name[0] + "_" + name[1] + "_Text_Summary";
		PrintWriter w = EgoClient.storage.newStatisticsPrintWriter(
				"Text Summary", "txt", filename);

		if (w != null) {
			try {
				stats.writeTextAnswers(w);
			} finally {
				w.close();
			}
		}
	}

	void saveAdjacencyMatrix_actionPerformed(ActionEvent e) {
		String[] name = EgoClient.interview.getName();
		String filename = name[0] + "_" + name[1] + "_Adjacency_Matrix";
		PrintWriter w = EgoClient.storage.newStatisticsPrintWriter(
				"Adjaceny Matrix", "csv", filename);

		if (w != null) {
			try {
				stats.writeAdjacencyArray(name[0] + " " + name[1], w, false);
			} finally {
				w.close();
			}
		}
	}

	void saveWeightedAdjacencyMatrix_actionPerformed(ActionEvent e) {
		String[] name = EgoClient.interview.getName();
		String filename = name[0] + "_" + name[1]
				+ "_Weighted_Adjacency_Matrix";
		PrintWriter w = EgoClient.storage.newStatisticsPrintWriter(
				"Adjaceny Matrix", "csv", filename);

		if (w != null) {
			try {
				stats.writeAdjacencyArray(name[0] + " " + name[1], w, true);
			} finally {
				w.close();
			}
		}
	}

	void close_actionPerformed(ActionEvent e) {
		//System.out.println("Return");
		SourceSelectPanel.gotoPanel(false);
	}

	void saveInterviewStatistics_actionPerformed(ActionEvent e) {
		/***********************************************************************
		 * Generate statistics for the first statable question
		 */
		Question q = EgoClient.study.getFirstStatableQuestion();

		try {
			if (q != null) {
				EgoClient.storage.writeStatisticsFiles(stats,
						EgoClient.interview.getName());
			}
		} catch (FileCreateException ex) {
		}
	}
}

/**
 * $Log: StatisticsFrame.java,v $ Revision 1.1 2005/08/02 19:36:05 samag Initial
 * checkin
 * 
 * Revision 1.9 2004/04/11 00:24:48 admin Fixing headers
 * 
 * Revision 1.8 2004/04/06 20:29:22 admin First pass as supporting interactive
 * applet linking interviews
 * 
 * Revision 1.7 2004/04/01 15:11:16 admin Completing Original UI work
 * 
 */

