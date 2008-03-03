package com.endlessloopsoftware.ego.client.statistics;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import com.endlessloopsoftware.ego.client.statistics.models.StatTableModel;

/**
 * <p>Title: Egocentric Networks Client Program</p>
 * <p>Description: Subject Interview Client</p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: Endless Loop Software</p>
 * @author Peter Schoaff
 * @version 1.0
 */

public class StatisticsArrayPanel extends JPanel
{
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
      catch (Exception e)
      {
         e.printStackTrace();
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