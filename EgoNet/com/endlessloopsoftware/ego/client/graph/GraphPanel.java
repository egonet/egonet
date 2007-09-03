package com.endlessloopsoftware.ego.client.graph;

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


import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import sun.awt.image.codec.JPEGImageEncoderImpl;

import com.endlessloopsoftware.ego.client.EgoClient;
import com.endlessloopsoftware.ego.client.statistics.Statistics;
import com.endlessloopsoftware.ego.client.statistics.StatisticsFrame;

import edu.uci.ics.jung.algorithms.importance.BetweennessCentrality;
import edu.uci.ics.jung.algorithms.importance.Ranking;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.Vertex;
import edu.uci.ics.jung.graph.decorators.StringLabeller;
import edu.uci.ics.jung.graph.decorators.StringLabeller.UniqueLabelException;
import edu.uci.ics.jung.graph.impl.SparseVertex;
import edu.uci.ics.jung.graph.impl.UndirectedSparseEdge;
import edu.uci.ics.jung.graph.impl.UndirectedSparseGraph;
import edu.uci.ics.jung.visualization.GraphDraw;
import edu.uci.ics.jung.visualization.Layout;

public class GraphPanel 
   extends JPanel
   implements ComponentListener
{
	private static Graph          _graph;
   private static Layout         _layout;
   private static ELSRenderer    _renderer;
   private static GraphDraw      _graphDraw;
   private static Vertex[]       _vertexArray = null;
   private static BufferedImage  _image;
   private static JPanel         _panel;

	public static final Dimension	size	= new Dimension(200, 200);

	public GraphPanel()
	{
      this.setLayout(new GridLayout());
      this.addComponentListener(this);
	}

	public void init(StatisticsFrame frame)
	{
		this.addNotify();
		this.setOpaque(true);
		this.setDoubleBuffered(false);

		Graph           g                 = new UndirectedSparseGraph();
		StringLabeller  undirectedLabeler = StringLabeller.getLabeller(g);
		Statistics      stats             = EgoClient.interview.getStats();

		int[][]         adjacencyMatrix   = stats.adjacencyMatrix;
		String[]        alterList         = stats.alterList;
		_vertexArray                      = new Vertex[alterList.length];

		for (int i = 0; i < alterList.length; ++i)
		{
			try
			{
				_vertexArray[i] = new SparseVertex();
				g.addVertex(_vertexArray[i]);
				undirectedLabeler.setLabel(_vertexArray[i], alterList[i]);
			}
			catch (UniqueLabelException e1)
			{
				e1.printStackTrace();
			}
		}

		for (int i = 0; i < adjacencyMatrix.length; ++i)
		{
			for (int j = i + 1; j < adjacencyMatrix[i].length; ++j)
			{
				if (adjacencyMatrix[i][j] > 0)
				{
					g.addEdge(new UndirectedSparseEdge(_vertexArray[i], _vertexArray[j]));
				}
			}
		}

		displayGraph(g, frame);
	}

	protected void displayGraph(Graph graph, StatisticsFrame frame)
	{
      if (_graphDraw != null)
      {
         remove(_graphDraw);
      }
      
		_graph = graph;

		//mVisualizer = new SpringLayout(mCurrentGraph);
		//mVisualizer = new CircleLayout(mCurrentGraph);
		_layout = new ELSFRLayout(_graph);
//		_layout = (Layout) new KKLayout();
//		_layout = (Layout) new ISOMLayout(_graph);
		
		//      mVisualizer = new ISOMLayout(mCurrentGraph);
		//((FRLayout) mVisualizer).setMaxIterations(400);

		_layout.initialize(size);
		_layout.resize(size);

		_renderer = new ELSRenderer();
		_renderer.setLabel("LABEL");
		_renderer.setSizeKey(BetweennessCentrality.CENTRALITY);

		//		if (mVizViewer != null)
		//		{
		//			//getContentPane().remove(mVizViewer);
		//			remove(mVizViewer);
		//		}
		//		mVizViewer = new VisualizationViewer(mVisualizer, mRenderer);
		//		mVizViewer.setSize(size);
		//		mVizViewer.setBackground(Color.WHITE);

		_graphDraw = new GraphDraw(_graph);
		_graphDraw.setGraphLayout(_layout);
		_graphDraw.setRenderer(_renderer);
		_graphDraw.hideStatus();

		BetweennessCentrality bc = new BetweennessCentrality(graph, true);
		bc.setRemoveRankScoresOnFinalize(false);
		bc.evaluate();
		List rankingList = bc.getRankings();

		if (rankingList.size() > 0)
		{
			Ranking betwennessMax = (Ranking) rankingList.get(0);
			_renderer.setMaxDegreeRank(betwennessMax.rankScore);
		}
		else
		{
			_renderer.setMaxDegreeRank(1);
		}

		//      mNodeAcceptBetweennessSlider.setMaximum((int)
		// Math.ceil(betwennessMax.rankScore / 4.0));
		//      mNodeAcceptBetweennessSlider.setMinimum(1);

		//getContentPane().add(mVizViewer);

		//		mVizViewer.setSize(size);
		//		mVizViewer.validate();
		//		add(mVizViewer);
		//		mVizViewer.revalidate();
		//		mVizViewer.repaint();

		add(_graphDraw);

		//writeGifThread.start();
	}

	static void writeGif()
	{
		if ((_vertexArray != null) && (_vertexArray.length > 0))
		{
			try
			{
				// Save as jpeg
				File file = new File("/Users/admin/dev/docs/graph.jpg");
				file.delete();
				file.createNewFile();
				OutputStream stream = new BufferedOutputStream(new FileOutputStream(file));

				JPEGImageEncoderImpl jpegEncoder = new JPEGImageEncoderImpl(stream);
				jpegEncoder.encode(_image);

				stream.flush();
				stream.close();

				//mImage.getGraphics().dispose();
			}
			catch (FileNotFoundException e)
			{
				e.printStackTrace();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
	}

	public void bufferGif()
	{
		//		mImage = new BufferedImage(mVizViewer.getWidth(),
		// mVizViewer.getHeight(), BufferedImage.TYPE_INT_RGB);
		//		mVizViewer.paintAll(mImage.getGraphics());

		_image = new BufferedImage(_graphDraw.getWidth(), _graphDraw.getHeight(), BufferedImage.TYPE_INT_RGB);
		_graphDraw.paintAll(_image.getGraphics());

		//   	JFrame fred = new JFrame();
		//   	fred.getContentPane().add(new ImageLabel(mImage));
		//   	fred.pack();
		//   	fred.validate();
		//   	fred.show();
	}

	static final Runnable	doDrawGif	 = new Runnable() 
   {
	   public void run()
	   {
	      System.out.println(_vertexArray);
	      System.out.println(_vertexArray.length);
	      
	      _panel = new JPanel();
	      _panel.addNotify();
	      _panel.setOpaque(true);
	      _panel.setDoubleBuffered(false); // for
	      // better
	      // performance
	      //			mPanel.add(mVizViewer);
	      _panel.add(_graphDraw);
	      _panel.validate(); // this
	      // might not
	      // be
	      // necessary
	      
	      //   		while
	      // (!mVisualizer.incrementsAreDone())
	      //   		{
	      //   			mVisualizer.advancePositions();
	      //   			System.out.println(mVisualizer.getStatus());
	      //   		}
	   }
   };

	Thread writeGifThread = new Thread() 
   {
	   public void run()
	   {
	      try
	      {
	         SwingUtilities.invokeAndWait(doDrawGif);
	         bufferGif();
	         writeGif();
	      }
	      catch (Exception e)
	      {
	         e.printStackTrace();
	      }
	      System.out.println("Finished on " + Thread.currentThread());
	   }
   };

	public static void main(String[] args)
	{

		JFrame vizApp = new JFrame("Endless Loop Software Network Viewer");
		vizApp.setSize(700, 500);
		vizApp.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		vizApp.getContentPane().add(new GraphPanel());
		vizApp.pack();
		vizApp.show();
	}

   /* (non-Javadoc)
    * @see java.awt.event.ComponentListener#componentResized(java.awt.event.ComponentEvent)
    */
   public void componentResized(ComponentEvent e)
   {
      _graphDraw.getVisualizationViewer().suspend();
      ((ELSFRLayout) _graphDraw.getGraphLayout()).update();
      _graphDraw.getVisualizationViewer().restart();
   }

   public void componentMoved(ComponentEvent e){}
   public void componentShown(ComponentEvent e){}
   public void componentHidden(ComponentEvent e) {}
}

/**
 * $Log$
 * Revision 1.1  2007/09/03 13:51:25  schoaff
 * Initial Checkin
 *
 * Revision 1.6  2004/04/11 00:17:13  admin
 * Improving display of Alter Prompt questions from Applet UI Interviews
 *
 * Revision 1.5  2004/04/06 20:29:22  admin
 * First pass as supporting interactive applet linking interviews
 *
 * Revision 1.4  2004/03/19 20:28:45  admin
 * Converted statistics frome to a panel. Incorporated in a tabbed panel
 * as part of main frame.
 *
 * Revision 1.4  2004/02/15 14:59:01  admin
 * Fixing Header Tags
 *
 * Revision 1.3  2004/02/15 14:44:15  admin
 * fixing headers
 *
 * Revision 1.2  2004/02/15 14:41:56  admin
 * Answer Data Value taking a couple of new parameters to aid display of
 * Alter Names
 *
 * Revision 1.1  2004/02/15 14:37:38  admin
 * Displaying network graph on web pages
 *
 */