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
package org.egonet.statistics;
import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.PrintWriter;
import java.util.Iterator;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import org.egonet.gui.interview.EgoClient;
import org.egonet.model.Question;
import org.egonet.model.Shared.QuestionType;
import org.egonet.util.CatchingAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;



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

	private EgoClient egoClient;

	final private static Logger logger = LoggerFactory.getLogger(StatisticsFrame.class);

	public StatisticsFrame(EgoClient egoClient) {
		this.egoClient = egoClient;
			jbInit();
	}

	private void jbInit() {
		boolean studyStatable = false;

		/***********************************************************************
		 * Fill in alter pair question selection menu
		 **********************************************************************/
		Iterator questions = egoClient.getStudy().getQuestionOrder(QuestionType.ALTER_PAIR).iterator();
		while (questions.hasNext()) {
			Question q = egoClient.getStudy().getQuestion((Long) questions.next());

			if (q.isStatable()) {
				//alterQuestionMenu.addItem(q);
				studyStatable = true;
				stats = egoClient.getInterview().generateStatistics(q);

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

				removeAllActionListeners(egoClient.getFrame().saveAlterSummary);
				egoClient.getFrame().saveAlterSummary
						.addActionListener(new java.awt.event.ActionListener() {
							public void actionPerformed(ActionEvent e) {
								saveAlterSummary_actionPerformed(e);
							}
						});

				removeAllActionListeners(egoClient.getFrame().saveTextSummary);
				egoClient.getFrame().saveTextSummary
						.addActionListener(new java.awt.event.ActionListener() {
							public void actionPerformed(ActionEvent e) {
								saveTextSummary_actionPerformed(e);
							}
						});

				removeAllActionListeners(egoClient.getFrame().saveWeightedAdjacencyMatrix);
				egoClient.getFrame().saveWeightedAdjacencyMatrix
						.addActionListener(new CatchingAction("saveWeightedAdjacencyMatrix") {
						    public void safeActionPerformed(ActionEvent e) throws Exception {
							    saveAdjacencyMatrix_actionPerformed(e, true);
							}
						});


				removeAllActionListeners(egoClient.getFrame().saveAlterByAlterPromptMatrix);
				egoClient.getFrame().saveAlterByAlterPromptMatrix
						.addActionListener(new CatchingAction("saveAlterByAlterPromptMatrix") {
						    public void safeActionPerformed(ActionEvent e) throws Exception {
							    saveAlterByAlterPromptMatrix_actionPerformed(e);
							}
						});

				removeAllActionListeners(egoClient.getFrame().saveAdjacencyMatrix);
				egoClient.getFrame().saveAdjacencyMatrix
						.addActionListener(new CatchingAction("saveAdjacencyMatrix") {
							public void safeActionPerformed(ActionEvent e) throws Exception {
							    saveAdjacencyMatrix_actionPerformed(e, false);
							}
						});

				removeAllActionListeners(egoClient.getFrame().saveInterviewStatistics);
				egoClient.getFrame().saveInterviewStatistics
						.addActionListener(new CatchingAction("saveInterviewStatistics") {
						    public void safeActionPerformed(ActionEvent e) throws Exception {
								saveInterviewStatistics_actionPerformed(e);
							}
						});


				updateAll();
			}
			else {
				logger.error("No stateable ALTER_PAIR question, bailing on lots of stuff");
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

		egoClient.getFrame().close
				.addActionListener(new java.awt.event.ActionListener() {
					public void actionPerformed(ActionEvent e) {
						close_actionPerformed(e);
					}
				});
	}

	private static void removeAllActionListeners(JMenuItem saveAlterSummary) {
		ActionListener[] listeners = saveAlterSummary.getActionListeners();
		for(ActionListener l : listeners)
			saveAlterSummary.removeActionListener(l);
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
			stats = egoClient.getInterview()
					.generateStatistics((Question) alterQuestionMenu
							.getSelectedItem());

			updateAll();
		}
	}*/

	void saveAlterSummary_actionPerformed(ActionEvent e) {
		String name = egoClient.getInterview().getIntName();
		String filename = name + "_alter_summary";
		PrintWriter w = egoClient.getStorage().newStatisticsPrintWriter("Alter Summary", "csv", filename);

		try {
			stats.writeAlterArray(w);
		} finally {
			w.close();
		}
	}

	void saveTextSummary_actionPerformed(ActionEvent e) {
		String name = egoClient.getInterview().getIntName();
		String filename = name + "_Text_Summary";
		PrintWriter w = egoClient.getStorage().newStatisticsPrintWriter(
				"Text Summary", "txt", filename);

		if (w != null) {
			try {
				stats.writeTextAnswers(w);
			} finally {
				w.close();
			}
		}
	}

	void saveAdjacencyMatrix_actionPerformed(ActionEvent e, boolean weighted) throws IOException {
	    String filename = egoClient.getStorage().getInterviewFile().getName() + (weighted ? "_Weighted" : "") +  "_Adjacency_Matrix";

	    PrintWriter w = egoClient.getStorage().newStatisticsPrintWriter(filename, "csv", filename);

	    stats.writeAdjacencyFile(w, egoClient.getStorage().getInterviewFile().getName().replace(".int", ""), weighted);
	}

        void saveAlterByAlterPromptMatrix_actionPerformed(ActionEvent e) throws IOException
        {
            String filename = egoClient.getStorage().getInterviewFile().getName() + ("_alter_by_alter_prompt");

            PrintWriter w = egoClient.getStorage().newStatisticsPrintWriter(filename, "csv", filename);

            stats.writeAlterByPromptFile(w, filename);
        }

	void close_actionPerformed(ActionEvent e) {
		//logger.info("Return");
		egoClient.getFrame().gotoSourceSelectPanel();
	}

	void saveInterviewStatistics_actionPerformed(ActionEvent e) throws IOException {
		/***********************************************************************
		 * Generate statistics for the first statable question
		 */
		Question q = egoClient.getStudy().getFirstStatableQuestion();

		if (q != null) {
			egoClient.getStorage().writeStatisticsFiles(stats);
		} else {
		    throw new IOException("No statable questions");
		}

	}
}
