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
package com.endlessloopsoftware.ego.client.graph;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

import edu.uci.ics.jung.graph.Edge;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.Vertex;
import edu.uci.ics.jung.graph.decorators.StringLabeller;
import edu.uci.ics.jung.visualization.AbstractRenderer;

/**
 * @author Scott White
 */
public class ELSRenderer extends AbstractRenderer
{
   private String mSizeKey;

   private int mDefaultNodeSize;
   private double maxRank = -1;
   private double minRank = -1;

   public ELSRenderer()
   {
      mDefaultNodeSize = 8;
      maxRank = 0;
   }

   public void paintEdge(Graphics g, Edge e, int x1, int y1, int x2, int y2)
   {
      Color c = g.getColor();
      g.setColor(Color.LIGHT_GRAY);
      g.drawLine(x1, y1, x2, y2);
      g.setColor(c);
    }

   public void paintVertex(Graphics g, Vertex v, int x, int y)
   {

      String label = null;
      if (getLabel() != null)
      {
         //       label = (String) v.getUserDatum(getLabel());
         label = StringLabeller.getLabeller((Graph) v.getGraph()).getLabel(v);
      }

      if (label == null)
      {
         label = v.toString();
      }

      if (label.length() > 15)
      {
         label = label.substring(0, 14);
      }

      int nodeSize = mDefaultNodeSize;
      if (mSizeKey != null)
      {
      	//         for (Iterator it = v.getUserDatumKeyIterator(); it.hasNext();)
      	//            System.out.println(it.next());
      	
      	try
      	{
      		Number decoratedNodeSize  = (Number) v.getUserDatum(mSizeKey);
      		int   red                 = 0;
      		
      		if (decoratedNodeSize.doubleValue() > 0)
      		{
      			//				  red = (int) Math.ceil((double) 255 * (Math.log(decoratedNodeSize.doubleValue()) / 
      			//                                                      Math.log(getMaxDegreeRank())));
      			red = (int) Math.ceil((double) 255 * (decoratedNodeSize.doubleValue() / 
      					getMaxDegreeRank()));
      		}
      		
      		//            System.out.println(decoratedNodeSize.doubleValue());
      		//            System.out.println(Math.log(decoratedNodeSize.doubleValue()));
      		//            System.out.println(getMaxDegreeRank());
      		//            System.out.println(Math.log(getMaxDegreeRank()));
      		//				System.out.println(red);
      		
      		Color c = new Color(255, 255 - red, 64 - (red / 4));
      		g.setColor(c);
      	}
      	catch (Exception e)
      	{
      		e.printStackTrace();
      		System.exit(-1);
      	}
      }

      int labelSize = g.getFontMetrics().stringWidth(label);
      nodeSize = Math.max(nodeSize, 10);
      nodeSize = Math.min(nodeSize, 150);

      g.fillOval(x - nodeSize / 2, y - nodeSize / 2, nodeSize, nodeSize);
      g.setColor(Color.GRAY);
      g.drawOval(x - nodeSize / 2, y - nodeSize / 2, nodeSize, nodeSize);
      g.setColor(Color.BLACK);
      Font font = new Font("Arial", Font.PLAIN, 12);
      Font f = g.getFont();
      g.setFont(font);
      if (nodeSize > labelSize)
      {
         g.drawString(label, x - labelSize / 2, y + 4);
      }
      else
      {
         g.drawString(label, x - labelSize / 2 + 20, y + 15);

      }
      g.setFont(f);
   }

   public String getSizeKey()
   {
      return mSizeKey;
   }

   public void setSizeKey(String decorationKey)
   {
      this.mSizeKey = decorationKey;
   }

   String mLabel;

   public String getLabel()
   {
      return mLabel;
   }

   public void setLabel(String label)
   {
      this.mLabel = label;
   }

   /**
    * @return
    */
   public double getMaxDegreeRank()
   {
      return maxRank;
   }

   /**
    * @param i
    */
   public void setMaxDegreeRank(double d)
   {
      maxRank = d;
   }

   /**
    * @return
    */
   public double getMinRank()
   {
      return minRank;
   }

   /**
    * @param d
    */
   public void setMinRank(double d)
   {
      minRank = d;
   }

}
