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
package com.endlessloopsoftware.egonet.web.servlet;

import java.awt.Dimension;
import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.swing.JComponent;

import org.apache.struts.util.ModuleException;
import org.jboss.logging.Logger;

import com.endlessloopsoftware.egonet.InterviewPosition;
import com.endlessloopsoftware.egonet.util.InterviewDataValue;
import com.endlessloopsoftware.egonet.web.WebShared;

import edu.uci.ics.jung.algorithms.importance.BetweennessCentrality;
import edu.uci.ics.jung.algorithms.importance.Ranking;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.Vertex;
import edu.uci.ics.jung.graph.decorators.StringLabeller;
import edu.uci.ics.jung.graph.decorators.StringLabeller.UniqueLabelException;
import edu.uci.ics.jung.graph.impl.SparseVertex;
import edu.uci.ics.jung.graph.impl.UndirectedSparseEdge;
import edu.uci.ics.jung.graph.impl.UndirectedSparseGraph;
import edu.uci.ics.jung.visualization.FRLayout;
import edu.uci.ics.jung.visualization.GraphDraw;
import edu.uci.ics.jung.visualization.Layout;

/**
 * @author admin
 *  
 */
public class NetworkServlet extends SwingServlet
{

	public static final Dimension	size		= new Dimension(200, 200);
	public static final Logger		logger	= Logger.getLogger("Network Servlet");

	/**
	 * Override me!
	 */
	protected JComponent createComponent(HttpServletRequest req) throws ServletException, IOException
	{
		logger.debug("create component");
		try
		{
			return getComponent(req);
		}
		catch (ModuleException e)
		{
			e.printStackTrace();
			throw new ServletException(e.getMessage());
		}
	}

	public JComponent getComponent(HttpServletRequest req) throws ModuleException
	{
		Graph                g                 = new UndirectedSparseGraph();
      StringLabeller       undirectedLabeler = StringLabeller.getLabeller(g);
      InterviewDataValue   interviewData     = WebShared.retrieveInterviewDataValue(req);
      int[][]              adjacencyMatrix   = interviewData.getAdjacencyMatrix();
      String[]             alterList         = interviewData.getAlters();
      InterviewPosition    position          = WebShared.retrieveInterviewPosition(req);
      Vertex[]             vertexList        = new Vertex[alterList.length];

		// Add me
		Vertex me = new SparseVertex();
		g.addVertex(me);
		try
		{
			undirectedLabeler.setLabel(me, interviewData.getFirstName() + " " + interviewData.getLastName().substring(0, 1));
		}
		catch (UniqueLabelException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if (position.getPrimaryAlter() > 0)
		{
			for (int i = 0; i < position.getPrimaryAlter(); ++i)
			{
				try
				{
					vertexList[i] = new SparseVertex();
					g.addVertex(vertexList[i]);
					undirectedLabeler.setLabel(vertexList[i], alterList[i]);
				}
				catch (UniqueLabelException e1)
				{
					e1.printStackTrace();
				}
			}

			for (int i = 0; i < (position.getPrimaryAlter() - 1); ++i)
			{
				g.addEdge(new UndirectedSparseEdge(vertexList[i], me));
				
				for (int j = i + 1; j < position.getPrimaryAlter(); ++j)
				{
					if (adjacencyMatrix[i][j] > 0)
					{
						g.addEdge(new UndirectedSparseEdge(vertexList[i], vertexList[j]));
					}
				}
			}
		}

		Layout layout = new FRLayout(g);

		layout.initialize(size);
		layout.resize(size);

		ELSRenderer renderer = new ELSRenderer();
		renderer.setLabel("LABEL");
		renderer.setSizeKey(BetweennessCentrality.CENTRALITY);

		GraphDraw graphDraw = new GraphDraw(g);
		graphDraw.setGraphLayout(layout);
		graphDraw.setRenderer(renderer);

		BetweennessCentrality bc = new BetweennessCentrality(g, true);
		bc.setRemoveRankScoresOnFinalize(false);
		bc.evaluate();
		List rankingList = bc.getRankings();

		if (rankingList.size() > 0)
		{
			Ranking betwennessMax = (Ranking) rankingList.get(0);
			renderer.setMaxDegreeRank(betwennessMax.rankScore);
		}
		else
		{
			renderer.setMaxDegreeRank(1);
		}

		return graphDraw;
	}
}
