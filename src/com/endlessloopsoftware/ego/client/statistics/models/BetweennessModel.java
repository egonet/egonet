/**
 * <p>Title: Egocentric Network Researcher</p>
 * <p>Description: Configuration Utilities for an Egocentric network study</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: Endless Loop Software</p>
 * @author Peter C. Schoaff
 * @version 1.0
 *
 * $Id: BetweennessModel.java,v 1.1 2005/08/02 19:36:11 samag Exp $
 *
 * $Log: BetweennessModel.java,v $
 * Revision 1.1  2005/08/02 19:36:11  samag
 * Initial checkin
 *
 * Revision 1.2  2004/02/26 21:19:18  admin
 * adding jardescs
 *
 * Revision 1.1  2003/12/08 15:57:51  admin
 * Modified to generate matrix files on survey completion or summarization
 * Extracted statistics models
 *
 */
package com.endlessloopsoftware.ego.client.statistics.models;

import javax.swing.JTable;

import com.endlessloopsoftware.ego.client.statistics.Statistics;

public class BetweennessModel extends StatTableModel
{
	public BetweennessModel(Statistics stats) 
	{
		super(stats);
	}
	
	public int getColumnCount()
   {
      return (3);
   }

   public int getRowCount()
   {
      return (stats.degreeArray.length);
   }

   public Object getValueAt(int rowIndex, int columnIndex)
   {
      try
      {
         if (columnIndex == 0)
         {
            return (stats.alterList[rowIndex]);
         }
         else if (columnIndex == 1)
         {
            return (new Float(stats.betweennessArray[rowIndex]));
         }
         else
         {
            double big = stats.proximityMatrix.length - 1;
            big *= big;
            return (new Float(stats.betweennessArray[rowIndex] / big));
         }
      }
      catch (Exception ex)
      {
         ex.printStackTrace();
         return null;
      }
   }

   public String getColumnName(int column)
   {
      if (column == 0)
      {
         return ("Alters");
      }
      else if (column == 1)
      {
         return ("Raw");
      }
      else
      {
         return ("Normalized");
      }
   }

   public int getResizeMode()
   {
      return JTable.AUTO_RESIZE_ALL_COLUMNS;
   }
}