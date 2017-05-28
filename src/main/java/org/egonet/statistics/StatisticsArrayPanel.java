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

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StatisticsArrayPanel extends JPanel
{
	final private static Logger logger = LoggerFactory.getLogger(StatisticsArrayPanel.class);
	
   StatTableModel data;
   private JTable dataTable;
   private JScrollPane dataScroll = new JScrollPane();
   private GridBagLayout gridBagLayout1 = new GridBagLayout();

   public StatisticsArrayPanel(StatTableModel data)
   {
      this.data = data;

      try
      {
         jbInit();
      }
      catch (Exception ex)
      {
         logger.error(ex.toString());
      }
   }

   private void jbInit() throws Exception
   {
      dataTable = new JTable(data);

      this.setLayout(gridBagLayout1);

      dataTable.setAutoResizeMode(data.getResizeMode());
      dataTable.setRowHeight(16);
      this.add(
         dataScroll,
         new GridBagConstraints(
            0,
            0,
            1,
            1,
            1.0,
            1.0,
            GridBagConstraints.CENTER,
            GridBagConstraints.BOTH,
            new Insets(5, 5, 5, 5),
            0,
            0));
      dataScroll.getViewport().add(dataTable, null);
   }

   public StatTableModel getTableModel()
   {
      return data;
   }
}