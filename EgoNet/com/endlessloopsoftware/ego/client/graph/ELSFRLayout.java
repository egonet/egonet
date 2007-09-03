/*
 * Copyright (c) 2003, the JUNG Project and the Regents of the University of
 * California All rights reserved.
 * 
 * This software is open-source under the BSD license; see either "license.txt"
 * or http://jung.sourceforge.net/license.txt for a description.
 */
package com.endlessloopsoftware.ego.client.graph;

import java.util.Iterator;

import cern.colt.matrix.DoubleMatrix1D;
import cern.colt.matrix.impl.DenseDoubleMatrix1D;
import edu.uci.ics.jung.exceptions.FatalException;
import edu.uci.ics.jung.graph.Edge;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.Vertex;
import edu.uci.ics.jung.utils.Pair;
import edu.uci.ics.jung.utils.UserData;
import edu.uci.ics.jung.visualization.AbstractLayout;
import edu.uci.ics.jung.visualization.Coordinates;
import edu.uci.ics.jung.visualization.LayoutMutable;

/**
 * Implements the Fruchterman-Reingold algorithm for node layout.
 * 
 * @author Scott White, Yan-Biao Boey, Danyel Fisher
 */
public class ELSFRLayout
   extends AbstractLayout
   implements LayoutMutable
{

   private static final Object FR_KEY         = "edu.uci.ics.jung.FR_Visualization_Key";
   private double              forceConstant;
   private double              temperature;
   private int                 currentIteration;
   private String              status         = null;
   private int                 mMaxIterations = 700;

   public ELSFRLayout(Graph g)
   {
      super(g);
      //		currentIteration = 0;
   }

   /*
    * new function for handling updates and changes to the graph
    */
   public void update()
   {
      for (Iterator iter = getGraph().getVertices().iterator(); iter.hasNext();)
      {
         Vertex v = (Vertex) iter.next();
         Coordinates coord = (Coordinates) v.getUserDatum(getBaseKey());
         if (coord == null)
         {
            coord = new Coordinates();
            v.addUserDatum(getBaseKey(), coord, UserData.REMOVE);
            initializeLocation(v, coord, getCurrentSize());
            initialize_local_vertex(v);
         }
      }
   }

   /**
    * Returns the current temperature and number of iterations elapsed, as a
    * string.
    */
   public String getStatus()
   {
      return status;
   }

   public void forceMove(Vertex picked, int x, int y)
   {
      super.forceMove(picked, x, y);
   }

   protected void initialize_local()
   {
      currentIteration = 0;
      temperature = getCurrentSize().getWidth() / 10;
      forceConstant = 0.75 * Math.sqrt(getCurrentSize().getHeight() * getCurrentSize().getWidth()
                                       / getVisibleGraph().numVertices());
   }

   private Object key     = null;

   private double EPSILON = 0.000001D;

   /**
    * Returns a visualization-specific key (that is, specific both to this
    * instance and <tt>AbstractLayout</tt>) that can be used to access
    * UserData related to the <tt>AbstractLayout</tt>.
    */
   public Object getKey()
   {
      if (key == null)
         key = new Pair(this, FR_KEY);
      return key;
   }

   protected void initialize_local_vertex(Vertex v)
   {
      if (v.getUserDatum(getKey()) == null)
      {
         v.addUserDatum(getKey(), new FRVertexData(), UserData.REMOVE);
      }
   }

   /**
    * Moves the iteration forward one notch, calculation attraction and
    * repulsion between vertices and edges and cooling the temperature.
    */
   public void advancePositions()
   {
      currentIteration++;
      status = "VV: " + getVisibleVertices().size() + " IT: " + currentIteration + " temp: " + temperature;
      /**
       * Calculate repulsion
       */
      for (Iterator iter = getVisibleVertices().iterator(); iter.hasNext();)
      {
         Vertex v1 = (Vertex) iter.next();
         if (dontMove(v1))
            continue;
         calcRepulsion(v1);
      }

      /**
       * Calculate attraction
       */
      for (Iterator iter = getVisibleEdges().iterator(); iter.hasNext();)
      {
         Edge e = (Edge) iter.next();

         calcAttraction(e);
      }

      double cumulativeChange = 0;

      for (Iterator iter = getVisibleVertices().iterator(); iter.hasNext();)
      {
         Vertex v = (Vertex) iter.next();
         if (dontMove(v))
            continue;
         calcPositions(v);
      }

      cool();
   }

   public void calcPositions(Vertex v)
   {
      FRVertexData fvd = getFRData(v);
      Coordinates xyd = getCoordinates(v);
      double deltaLength = Math.max(EPSILON, Math.sqrt(fvd.disp.zDotProduct(fvd.disp)));

      double newXDisp = fvd.getXDisp() / deltaLength * Math.min(deltaLength, temperature);

      if (Double.isNaN(newXDisp)) { throw new FatalException("Unexpected mathematical result"); }

      double newYDisp = fvd.getYDisp() / deltaLength * Math.min(deltaLength, temperature);
      xyd.addX(newXDisp);
      xyd.addY(newYDisp);

      double borderWidth = getCurrentSize().getWidth() / 50.0;
      double maxborder   = getCurrentSize().getWidth() / 8;
      borderWidth = Math.max(borderWidth, 30);
      borderWidth = Math.min(borderWidth, maxborder);
      
      double newXPos = xyd.getX();
      if (newXPos < borderWidth)
      {
         newXPos = borderWidth + Math.random() * borderWidth * 2.0;
      }
      else if (newXPos > (getCurrentSize().getWidth() - borderWidth))
      {
         newXPos = getCurrentSize().getWidth() - borderWidth - Math.random() * borderWidth * 2.0;
      }
      //double newXPos = Math.min(getCurrentSize().getWidth() - 20.0,
      // Math.max(20.0, xyd.getX()));

      double newYPos = xyd.getY();
      if (newYPos < borderWidth)
      {
         newYPos = borderWidth + Math.random() * borderWidth * 2.0;
      }
      else if (newYPos > (getCurrentSize().getHeight() - borderWidth))
      {
         newYPos = getCurrentSize().getHeight() - borderWidth - Math.random() * borderWidth * 2.0;
      }
      //double newYPos = Math.min(getCurrentSize().getHeight() - 20.0,
      // Math.max(20.0, xyd.getY()));

      xyd.setX(newXPos);
      xyd.setY(newYPos);
   }

   public void calcAttraction(Edge e)
   {
      Vertex v1 = (Vertex) e.getIncidentVertices().iterator().next();
      Vertex v2 = e.getOpposite(v1);

      double xDelta = getX(v1) - getX(v2);
      double yDelta = getY(v1) - getY(v2);

      double deltaLength = Math.max(EPSILON, Math.sqrt((xDelta * xDelta) + (yDelta * yDelta)));

      double force = (deltaLength * deltaLength) / forceConstant;

      if (Double.isNaN(force)) { throw new FatalException("Unexpected mathematical result"); }

      FRVertexData fvd1 = getFRData(v1);
      FRVertexData fvd2 = getFRData(v2);

      fvd1.decrementDisp((xDelta / deltaLength) * force, (yDelta / deltaLength) * force);
      fvd2.incrementDisp((xDelta / deltaLength) * force, (yDelta / deltaLength) * force);
   }

   public void calcRepulsion(Vertex v1)
   {
      FRVertexData fvd1 = getFRData(v1);
      fvd1.setDisp(0, 0);

      for (Iterator iter2 = getVisibleVertices().iterator(); iter2.hasNext();)
      {
         Vertex v2 = (Vertex) iter2.next();
         if (dontMove(v2))
            continue;
         if (v1 != v2)
         {
            double xDelta = getX(v1) - getX(v2);
            double yDelta = getY(v1) - getY(v2);

            double deltaLength = Math.max(EPSILON, Math.sqrt((xDelta * xDelta) + (yDelta * yDelta)));

            double force = (forceConstant * forceConstant) / deltaLength;

            if (Double.isNaN(force)) { throw new FatalException("Unexpected mathematical result"); }

            fvd1.incrementDisp((xDelta / deltaLength) * force, (yDelta / deltaLength) * force);
         }
      }
   }

   private void cool()
   {
      temperature *= (1.0 - currentIteration / (double) mMaxIterations);
   }

   public void setMaxIterations(int maxIterations)
   {
      mMaxIterations = maxIterations;
   }

   public FRVertexData getFRData(Vertex v)
   {
      return (FRVertexData) (v.getUserDatum(getKey()));
   }

   /**
    * This one is an incremental visualization.
    */
   public boolean isIncremental()
   {
      return true;
   }

   /**
    * Returns true once the current iteration has passed the maximum count, <tt>MAX_ITERATIONS</tt>.
    */
   public boolean incrementsAreDone()
   {
      if (currentIteration > mMaxIterations) { return true; }
      return false;
   }

   public static class FRVertexData
   {
      private DoubleMatrix1D disp;

      public FRVertexData()
      {
         initialize();
      }

      public void initialize()
      {
         disp = new DenseDoubleMatrix1D(2);
      }

      public double getXDisp()
      {
         return disp.get(0);
      }

      public double getYDisp()
      {
         return disp.get(1);
      }

      public void setDisp(double x, double y)
      {
         disp.set(0, x);
         disp.set(1, y);
      }

      public void incrementDisp(double x, double y)
      {
         disp.set(0, disp.get(0) + x);
         disp.set(1, disp.get(1) + y);
      }

      public void decrementDisp(double x, double y)
      {
         disp.set(0, disp.get(0) - x);
         disp.set(1, disp.get(1) - y);
      }
   }
}