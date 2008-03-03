/**
 * <p>Title: Egocentric Network Researcher</p>
 * <p>Description: Configuration Utilities for an Egocentric network study</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: Endless Loop Software</p>
 * @author Peter C. Schoaff
 * @version 1.0
 *
 * $Id: ClosenessModel.java,v 1.1 2005/08/02 19:36:11 samag Exp $
 *
 * $Log: ClosenessModel.java,v $
 * Revision 1.1  2005/08/02 19:36:11  samag
 * Initial checkin
 *
 * Revision 1.3  2004/04/11 00:24:49  admin
 * Fixing headers
 *
 * Revision 1.2  2004/03/10 14:32:39  admin
 * Adding client library
 * cleaning up code
 *
 * Revision 1.1  2003/12/08 15:57:51  admin
 * Modified to generate matrix files on survey completion or summarization
 * Extracted statistics models
 *
 */
package com.endlessloopsoftware.ego.client.statistics.models;

import javax.swing.JTable;

import com.endlessloopsoftware.ego.client.statistics.Statistics;

public class ClosenessModel extends StatTableModel
{
	public ClosenessModel(Statistics stats) 
	{
		super(stats);
	}
	
	public int getColumnCount()
   {
      return (stats.proximityMatrix.length + 3);
   }

   public int getRowCount()
   {
      return (stats.proximityMatrix.length);
   }

   public Object getValueAt(int rowIndex, int columnIndex)
   {
      try
      {
         if (columnIndex == 0)
         {
            return (stats.alterList[rowIndex]);
         }
         else if (columnIndex == (getColumnCount() - 2))
         {
            return (new Integer(stats.farnessArray[rowIndex]));
         }
         else if (columnIndex == (getColumnCount() - 1))
         {
            return (new Float(stats.closenessArray[rowIndex]));
         }
         else
         {
            return (new Integer(stats.proximityMatrix[rowIndex][columnIndex - 1]));
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
         return (" ");
      }
      else if (column == (getColumnCount() - 2))
      {
         return ("Farness");
      }
      else if (column == (getColumnCount() - 1))
      {
         return ("nCloseness");
      }
      else
      {
         return stats.alterList[column - 1];
      }
   }

   public int getResizeMode()
   {
      return JTable.AUTO_RESIZE_OFF;
   }
}
