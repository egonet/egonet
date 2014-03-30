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
    	  throw new RuntimeException(ex);
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
