/*
* Copyright (c) 2003, the JUNG Project and the Regents of the University 
* of California
* All rights reserved.
*
* This software is open-source under the BSD license; see either
* "license.txt" or
* http://jung.sourceforge.net/license.txt for a description.
*/
package com.endlessloopsoftware.egonet.web.applet;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

import com.endlessloopsoftware.egonet.web.WebShared;

import edu.uci.ics.jung.graph.Edge;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.Vertex;
import edu.uci.ics.jung.graph.decorators.StringLabeller;
import edu.uci.ics.jung.visualization.AbstractRenderer;

/**
 * @author Scott White
 */
public class ELSAppletRenderer extends AbstractRenderer
{
   private String        mSizeKey;

   private String        mode;
   private int           mDefaultNodeSize;
   private double        maxRank = -1;
   private double        minRank = -1;
   
   private static final Color[] COLORS = {Color.RED, Color.YELLOW, Color.GREEN, Color.BLUE, new Color(0x80, 0, 0x80)};

   public ELSAppletRenderer(String mode)
   {
      this.mode = mode;
      mDefaultNodeSize = 8;
      maxRank = 0;
   }

   public void paintEdge(Graphics g, Edge e, int x1, int y1, int x2, int y2)
   {
      Color c = g.getColor();
      g.setColor(Color.gray);
      g.drawLine(x1, y1, x2, y2);
      g.setColor(c);
   }

   public void paintVertex(Graphics g, Vertex v, int x, int y)
   {
      String label = null;
      if (getLabel() != null)
      {
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
      
      String type = (String) v.getUserDatum(EgoGraph.TYPE);
      if (WebShared.VIEW_MODE.equals(mode))
      {
         if (EgoGraph.EGO.equals(type))
         {
            g.setColor(Color.RED);
         }
         else
         {
            oldPaintVertex(g, v, x, y);
         }
      }
      else if (WebShared.ALTER_QUESTION_MODE.equals(mode))
      {
         Integer answer = (Integer) v.getUserDatum(EgoGraph.ANSWER);
         if (EgoGraph.NONE.equals(answer))
         {
            g.setColor(Color.BLACK);
         }
         else
         {
            g.setColor(COLORS[answer.intValue()]);
         }
      }
      else /* Linking/Correct Mode */
      {
         if (EgoGraph.BASE.equals(type))
         {
            g.setColor(Color.BLUE);
         }
         else if (EgoGraph.LINKED.equals(type))
         {
            g.setColor(Color.GREEN);
         }
         else if (EgoGraph.EGO.equals(type))
         {
            g.setColor(Color.RED);
         }
         else
         {
            g.setColor(Color.BLACK);
         }
      }

      drawVertex(g, x, y, label);
   }
   
   /**
    * @param g
    * @param x
    * @param y
    * @param label
    */
   private void drawVertex(Graphics g, int x, int y, String label)
   {
      int nodeSize = mDefaultNodeSize;
      int labelSize = g.getFontMetrics().stringWidth(label);
      nodeSize = Math.max(nodeSize, 10);
      nodeSize = Math.min(nodeSize, 150);

      g.fillOval(x - nodeSize / 2, y - nodeSize / 2, nodeSize, nodeSize);
      g.setColor(Color.gray);
      g.drawOval(x - nodeSize / 2, y - nodeSize / 2, nodeSize, nodeSize);
      g.setColor(Color.black);
      Font font = new Font("Arial", Font.PLAIN, 10);
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

   public void oldPaintVertex(Graphics g, Vertex v, int x, int y)
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

      if (mSizeKey != null)
      {
      	   try
         {
            Number decoratedNodeSize = (Number) v.getUserDatum(mSizeKey);
            int red = 0;

            if (decoratedNodeSize.doubleValue() > 0)
            {
               //				  red = (int) Math.ceil((double) 255 *
               // (Math.log(decoratedNodeSize.doubleValue()) /
               //                                                      Math.log(getMaxDegreeRank())));
               red = (int) Math.ceil((double) 255 * (decoratedNodeSize.doubleValue() / getMaxDegreeRank()));
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
