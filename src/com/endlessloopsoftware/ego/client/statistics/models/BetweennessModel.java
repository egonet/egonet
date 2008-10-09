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
    	  throw new RuntimeException(ex);
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