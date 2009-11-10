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
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.ProgressMonitor;

import org.egonet.exceptions.CorruptedInterviewException;
import org.egonet.gui.EgoStore;
import org.egonet.io.InterviewReader;
import org.egonet.io.StatisticsFileReader;
import org.egonet.util.DirList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.com.bytecode.opencsv.CSVWriter;

import com.endlessloopsoftware.ego.client.statistics.StatRecord;
import com.endlessloopsoftware.ego.client.statistics.StatisticsArrayPanel;
import com.endlessloopsoftware.ego.client.statistics.models.StatTableModel;

public class SummaryPanel extends JPanel
{
	
	final private static Logger logger = LoggerFactory.getLogger(SummaryPanel.class);
	
	private final    JButton 		_finishedButton = new JButton("Finished");
	private          JPanel 			_summaryPanel;

	private          StatRecord[] 	_stats          = new StatRecord[0];
	private          int 				_recordCount    = 0;
	private EgoClient egoClient;

	
   public SummaryPanel(EgoClient egoClient, StatRecord[] stats)
   {
	   this.egoClient = egoClient;
      setLayout(new GridBagLayout());

      /* Get data to display */
      _stats         = stats;
      _recordCount   = stats.length;

      /* Load table */
      _summaryPanel = new StatisticsArrayPanel(new SummaryModel(this));

      add(_summaryPanel,
         new GridBagConstraints( 0, 0, 1, 1, 1.0, 1.0,
                                 GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                                 new Insets(0, 0, 0, 0), 0, 0));
      add(_finishedButton,
         new GridBagConstraints( 0, 1, 1, 1, 0.0, 0.0,
                                 GridBagConstraints.EAST, GridBagConstraints.NONE,
                                 new Insets(5, 5, 5, 5), 0, 0));

      _finishedButton.addActionListener(new java.awt.event.ActionListener()
      {
         public void actionPerformed(ActionEvent e)
         {
            finishedButton_actionPerformed(e);
         }
      });
   }

	public SummaryPanel(EgoClient egoClient, ProgressMonitor progress)
	{
		this.egoClient = egoClient;
		setLayout(new GridBagLayout());

		/* Get data to display */
		loadInterviewArray(progress);

		/* Load table */
		_summaryPanel = new StatisticsArrayPanel(new SummaryModel(this));

		add(_summaryPanel,
			new GridBagConstraints(	0, 0, 1, 1, 1.0, 1.0,
											GridBagConstraints.CENTER, GridBagConstraints.BOTH,
											new Insets(0, 0, 0, 0), 0, 0));
		add(_finishedButton,
			new GridBagConstraints( 0, 1, 1, 1, 0.0, 0.0,
											GridBagConstraints.EAST, GridBagConstraints.NONE,
											new Insets(5, 5, 5, 5), 0, 0));

		_finishedButton.addActionListener(new java.awt.event.ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				finishedButton_actionPerformed(e);
			}
		});
	}

	private void finishedButton_actionPerformed(ActionEvent e)
	{
	    egoClient.getFrame().gotoSourceSelectPanel();
	}

	private void loadInterviewArray(ProgressMonitor progress)
	{
		File intPath = new File(egoClient.getStorage().getStudyFile().getParent(), "/Interviews/");
		File istPath = new File(egoClient.getStorage().getStudyFile().getParent(), "/Statistics/");
		String[] intFiles = DirList.getDirList(intPath, "int");
		Set<File> istFileSet = new HashSet<File>();
		int i = 0, p = 0;

		istPath.mkdir();
		
		progress.setMinimum(0);
		progress.setMaximum(intFiles.length * 2);

		logger.info("Files available for loading in "+intPath.getAbsolutePath()+": " + intFiles.length);
		for (i = 0; (i < intFiles.length) && !progress.isCanceled(); i++)
		{
			
			progress.setProgress(++p);

			String thisIntFileName = intFiles[i];
			logger.info("Loading file " + thisIntFileName);
			
			File intFile = new File(intPath, thisIntFileName);

			try {
				// Check that this has the correct Study Id
				String istPathString = istPath.getCanonicalPath();
	
				EgoStore store = egoClient.getStorage();
				File thisIstFile = store.interviewStatisticsFile(istPathString, intFiles[i]);
				
				InterviewReader ir = new InterviewReader(egoClient.getStudy(), intFile);
				ir.getInterview();
				store.generateStatisticsFile(intFile);
				istFileSet.add(thisIstFile);
			} catch (CorruptedInterviewException ex)
			{
				// no match, eat silently
			} catch (IOException ex)
			{
				logger.info("Failed to read " + thisIntFileName,ex);
			}
		
			
		}

		_stats = new StatRecord[istFileSet.size()];

		progress.setMaximum(intFiles.length + istFileSet.size());
		for (Iterator<File> it = istFileSet.iterator(); it.hasNext() && !progress.isCanceled();)
		{
			progress.setProgress(++p);

			try
			{
				StatisticsFileReader sfr = new StatisticsFileReader(it.next());
				StatRecord rec = sfr.readStatRecord();

				if (rec != null)
				{
					_stats[_recordCount++] = rec;
				}
			}
			catch (Exception ignored)
			{
				ignored.printStackTrace();
			}
		}
	}

	public void writeStudySummary(CSVWriter w)
	{
		Iterator it;
		StatRecord stat = _stats[0];
		DecimalFormat percentFormatter = new DecimalFormat("#.##");

		if (_stats.length == 0)
		{
			return;
		}

		/*******
		 * Column Headers
		 */
		List<String> header = new ArrayList<String>();
		header.add("Respondant_Name");
		it = stat.egoAnswers.iterator();
		while (it.hasNext())
		{
			header.add(((StatRecord.EgoAnswer) it.next()).title);
		}

		it = stat.alterAnswers.iterator();
		while (it.hasNext())
		{
			StatRecord.AlterAnswer answer = (StatRecord.AlterAnswer) it.next();
			
			if (answer.selections.length == 1)
			{
				header.add(answer.title + "_mn");
			}
			else
			{
				
				//Code commented and changed by sonam 08/24/07 
				/*for (int i = 0; i < answer.selections.length; i++)
				{
					w.print(", " + title + i + "N");
					w.print(", " + title + i + "P");
				}*/
				
				for (int i = 0; i < answer.selections.length; i++)
				{
					header.add(answer.title + "|Answer:" + answer.selections[i] + 
							"|Value:" + answer.AnswerIndex[i] + "|Count");
					header.add(answer.title + "|Answer:" + answer.selections[i] + 
							"|Value:" + answer.AnswerIndex[i] + "|Percentage");
				}
				//end of code modify
			}
		}
		String[] statHeaders = 
			("Max_Deg_Name,Max_Deg_Value,Max_Close_Name,Max_Close_Value,"
			+ "Max_Between_Name,Max_Between_Value,N_Cliques,N_Components,Degree_Mean,"
			+ "Closeness_Mean,Between_Mean,DegreeNC,ClosenessNC,BetweenNC,"
			+ "N_Isolates,N_Dyads").split(",");
		for(String statHeader : statHeaders) {
			header.add(statHeader);
		}
		w.writeNext(header.toArray(new String[]{}));
		
		/*******
		 * Data Lines
		 */
		for (int i = 0; i < _recordCount; i++)
		{
			List<String> row = new ArrayList<String>();
			
			stat = _stats[i];

			row.add(stat.getName());

			it = stat.egoAnswers.iterator();
			while (it.hasNext())
			{
				row.add(((StatRecord.EgoAnswer) it.next()).index+"");
			}

			it = stat.alterAnswers.iterator();
			while (it.hasNext())
			{
				StatRecord.AlterAnswer answer = (StatRecord.AlterAnswer) it.next();

				if (answer.selections.length == 1)
				{
					if ((answer.count == 0) || (answer.totals[0] == 0))
					{
						row.add(0+"");
					}
					else
					{
						row.add(((float) answer.totals[0] / answer.count)+"");
					}
				}
				else
				{
					for (int j = 0; j < answer.selections.length; j++)
					{
						row.add(answer.totals[j]+"");

						if ((answer.count == 0) || (answer.totals[j] == 0))
						{
							row.add(0+"");
						}
						else
						{
							row.add(percentFormatter.format((double) answer.totals[j] / answer.count));
						}
					}
				}
			}

			row.add(stat.degreeName);
			row.add(stat.degreeValue+"");
			row.add(stat.closenessName);
			row.add(stat.closenessValue+"");
			row.add(stat.betweenName);
			row.add(stat.betweenValue+"");
			row.add(stat.numCliques+"");
			row.add(stat.numComponents+"");
			row.add(stat.degreeMean+"");
			row.add(stat.closenessMean.floatValue()== -1 ? "." : stat.closenessMean.toString());
			row.add(stat.betweenMean+"");
			row.add(stat.degreeNC+"");
			row.add(stat.closenessNC+"");
			row.add(stat.betweenNC+"");
			row.add(stat.numIsolates+"");
			row.add(stat.numDyads+"");
			w.writeNext(row.toArray(new String[]{}));
		}
	}

   class SummaryModel extends StatTableModel
   {
   	private final SummaryPanel summaryPanel;
   	SummaryModel(SummaryPanel parent)
   	{
   		summaryPanel = parent;
   	}
   
   	public int getColumnCount()
   	{
   		if (summaryPanel._recordCount > 0)
   		{
   			return 17;
   		}
   		else
   		{
   			return 1;
   		}
   	}
   
   	public Object getValueAt(int rowIndex, int columnIndex)
   	{
   		if (rowIndex < summaryPanel._recordCount)
   		{
   			try
   			{
   				switch (columnIndex)
   				{
   					case 0 :
   						return (summaryPanel._stats[rowIndex].getName()); /* Name */
   					case 1 :
   						return (summaryPanel._stats[rowIndex].degreeName); /* Max Degree Name */
   					case 2 :
   						return (summaryPanel._stats[rowIndex].degreeValue);
   					case 3 :
   						return (summaryPanel._stats[rowIndex].closenessName); /* Max Closeness Name */
   					case 4 :
   						return (summaryPanel._stats[rowIndex].closenessValue);
   					case 5 :
   						return (summaryPanel._stats[rowIndex].betweenName); /* Max Betweenness Name */
   					case 6 :
   						return (summaryPanel._stats[rowIndex].betweenValue);
   					case 7 :
   						return (summaryPanel._stats[rowIndex].numCliques); /* # Cliques */
   					case 8 :
   						return (summaryPanel._stats[rowIndex].numComponents); /* # Components */
   					case 9 :
   						return (summaryPanel._stats[rowIndex].degreeMean);
   					case 10 :
   						return (summaryPanel._stats[rowIndex].closenessMean);
   					case 11 :
   						return (summaryPanel._stats[rowIndex].betweenMean);
   					case 12 :
   						return (summaryPanel._stats[rowIndex].degreeNC);
   					case 13 :
   						return (summaryPanel._stats[rowIndex].closenessNC);
   					case 14 :
   						return (summaryPanel._stats[rowIndex].betweenNC);
   					case 15 :
   						return (summaryPanel._stats[rowIndex].numIsolates); /* Components size 1 */
   					case 16 :
   						return (summaryPanel._stats[rowIndex].numDyads); /* Components size 2*/
   					default :
   						return (null);
   				}
   			}
   			catch (Exception ex)
   			{
   				throw new RuntimeException(ex);
   			}
   		}
   		else
   		{
   			return (null);
   		}
   	}
   
   	public int getRowCount()
   	{
   		if (summaryPanel._recordCount > 0)
   		{
   			return summaryPanel._recordCount;
   		}
   		else
   		{
   			return 0;
   		}
   	}
   
   	public String getColumnName(int column)
   	{
   		if (summaryPanel._recordCount > 0)
   		{
   			switch (column)
   			{
   				case 0 :
   					return ("Name");
   				case 1 :
   					return ("Degree Max");
   				case 3 :
   					return ("Closeness Max");
   				case 5 :
   					return ("Betweenness Max");
   				case 7 :
   					return ("# Cliques");
   				case 8 :
   					return ("# Components");
   				case 9 :
   					return ("Degree Mean");
   				case 10:
   					return ("Closeness Mean");
   				case 11:
   					return ("Betweenness Mean");
   				case 12:
   					return ("Degree NC");
   				case 13:
   					return ("Closeness NC");
   				case 14:
   					return ("Betweenness NC");
   				case 15:
   					return ("# Isolates");
   				case 16:
   					return ("# Dyads");
   				default :
   					return (null);
   			}
   		}
   		else
   		{
   			return ("No matching interviews found");
   		}
   	}
   
   	public int getResizeMode()
   	{
   		return JTable.AUTO_RESIZE_OFF;
   	}
   }

}