/****
 * 
 * Copyright (c) 2007, Endless Loop Software, Inc.
 * 
 *  This file is part of EgoNet.
 *
 *    EgoNet is free software; you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as published by
 *    the Free Software Foundation; either version 3 of the License, or
 *    (at your option) any later version.
 *
 *    EgoNet is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU General Public License for more details.
 *
 *    You should have received a copy of the GNU General Public License
 *    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.endlessloopsoftware.ego.client;

import java.awt.AWTEvent;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.io.PrintWriter;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;

import com.endlessloopsoftware.ego.Shared;
import com.endlessloopsoftware.elsutils.AboutBox;
import com.endlessloopsoftware.elsutils.files.FileHelpers;


public class ClientFrame
   extends JFrame
{
   private final JMenuBar  jMenuBar1                   = new JMenuBar();
   private final JMenu     jMenuFile                   = new JMenu("File");
   private final JMenu     jMenuHelp                   = new JMenu("Help");
   private final JMenuItem jMenuHelpAbout              = new JMenuItem("About");
   private final JMenuItem saveStudySummary            = new JMenuItem("Save Study Summary");
   private final JMenuItem exit                        = new JMenuItem("Exit");
   public final JMenuItem  saveAlterSummary            = new JMenuItem("Save Alter Summary");
   public final JMenuItem  saveTextSummary             = new JMenuItem("Save Text Answer Summary");
   public final JMenuItem  saveAdjacencyMatrix         = new JMenuItem("Save Adjacency Matrix");
   public final JMenuItem  saveWeightedAdjacencyMatrix = new JMenuItem("Save Weighted Adjacency Matrix");
   public final JMenuItem  close                       = new JMenuItem("Return to Main Menu");
   public final JMenuItem  saveInterviewStatistics     = new JMenuItem("Save Interview Statistics");

   //Construct the frame
   public ClientFrame()
   {
      enableEvents(AWTEvent.WINDOW_EVENT_MASK);

      try
      {
         jbInit();
      }
      catch (Exception e)
      {
         e.printStackTrace();
      }
   }

   //Component initialization
   private void jbInit()
      throws Exception
   {
      this.setSize(new Dimension(700, 600));
      this.setTitle("Egocentric Networks Study Tool");

      createMenuBar(EgoClient.SELECT);

      this.setContentPane(new JPanel());

      jMenuHelpAbout.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e)
         {
            jMenuHelpAbout_actionPerformed(e);
         }
      });

      exit.addActionListener(new ActionListener() {
         public void actionPerformed(ActionEvent e)
         {
            jMenuFileExit_actionPerformed(e);
         }
      });

      saveStudySummary.addActionListener(new java.awt.event.ActionListener() {
         public void actionPerformed(ActionEvent e)
         {
            saveStudySummary_actionPerformed(e);
         }
      });
   }

   public void flood()
   {
      Dimension size = this.getSize();
      this.pack();
      this.setSize(size);
      this.validate();
   }

   //File | Exit action performed
   public void jMenuFileExit_actionPerformed(ActionEvent e)
   {
      if (EgoClient.interview != null)
      {
         EgoClient.interview.exit();
      }

      System.exit(0);
   }

   //Help | About action performed
   public void jMenuHelpAbout_actionPerformed(ActionEvent e)
   {
      AboutBox dlg = new AboutBox(this, "Egocentric Network Survey Suite", "Survey Client", Shared.version);
      dlg.showAboutBox();
   }

   //Overridden so we can exit when window is closed
   protected void processWindowEvent(WindowEvent e)
   {
      super.processWindowEvent(e);
      if (e.getID() == WindowEvent.WINDOW_CLOSING)
      {
         jMenuFileExit_actionPerformed(null);
      }
   }

   public void createMenuBar(int mode)
   {
      jMenuBar1.removeAll();
      jMenuFile.removeAll();
      jMenuHelp.removeAll();

      // File Menu
      if (mode == EgoClient.VIEW_SUMMARY)
      {
         jMenuFile.add(saveStudySummary);
         jMenuFile.addSeparator();
         jMenuFile.add(exit);
      }
      else if (mode == EgoClient.VIEW_INTERVIEW)
      {
         /******
          * Create Menu Bar
          ***/
         jMenuFile.add(saveAlterSummary);
         jMenuFile.add(saveTextSummary);
         jMenuFile.add(saveAdjacencyMatrix);
         jMenuFile.add(saveWeightedAdjacencyMatrix);
         //       fileMenu.add(saveInterviewStatistics);
         jMenuFile.addSeparator();
         jMenuFile.add(close);
      }
      else
      {
         jMenuFile.add(exit);
      }
      jMenuBar1.add(jMenuFile);

      // Help Menu
      jMenuHelp.add(jMenuHelpAbout);
      jMenuBar1.add(jMenuHelp);

      this.setJMenuBar(jMenuBar1);
   }

   void saveStudySummary_actionPerformed(ActionEvent e)
   {
      String name = FileHelpers.formatForCSV(EgoClient.study.getStudyName());
      String filename = name + "_Summary";
      PrintWriter w = EgoClient.storage.newStatisticsPrintWriter("Study Summary", "csv", filename);

      if (w != null)
      {
         try
         {
            ((SummaryPanel) EgoClient.frame.getContentPane()).writeStudySummary(w);
         }
         finally
         {
            w.close();
         }
      }
   }
}