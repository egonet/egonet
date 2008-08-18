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
package com.endlessloopsoftware.ego.client.statistics;
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
