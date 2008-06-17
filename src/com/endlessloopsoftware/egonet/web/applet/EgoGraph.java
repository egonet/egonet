/*
 * Copyright (c) 2003, the JUNG Project and the Regents of the University of
 * California All rights reserved.
 * 
 * This software is open-source under the BSD license; see either "license.txt"
 * or http://jung.sourceforge.net/license.txt for a description.
 */
package com.endlessloopsoftware.egonet.web.applet;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.Panel;
import java.awt.TextArea;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JApplet;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import com.endlessloopsoftware.egonet.util.QuestionDataValue;
import com.endlessloopsoftware.egonet.util.SelectionDataValue;
import com.endlessloopsoftware.egonet.web.WebShared;
import com.endlessloopsoftware.egonet.web.servlet.InterviewDataServlet;

import edu.uci.ics.jung.algorithms.importance.BetweennessCentrality;
import edu.uci.ics.jung.algorithms.importance.Ranking;
import edu.uci.ics.jung.graph.Edge;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.Vertex;
import edu.uci.ics.jung.graph.decorators.StringLabeller;
import edu.uci.ics.jung.graph.decorators.StringLabeller.UniqueLabelException;
import edu.uci.ics.jung.graph.impl.SparseVertex;
import edu.uci.ics.jung.graph.impl.UndirectedSparseEdge;
import edu.uci.ics.jung.graph.impl.UndirectedSparseGraph;
import edu.uci.ics.jung.utils.UserData;
import edu.uci.ics.jung.visualization.GraphDraw;
import edu.uci.ics.jung.visualization.GraphMouseListener;
import edu.uci.ics.jung.visualization.Layout;

public class EgoGraph 
   extends JApplet
   implements ActionListener
{
   private final static String[] COLORS = {"RED", "YELLOW", "GREEN", "BLUE", "PURPLE"};

   public static final String    INDEX       = "Index";
   public static final String    TYPE        = "Type";
   public static final String    ANSWER      = "Answer";
   public static final String    EGO         = "Ego";
   public static final String    BASE        = "Base";
   public static final String    DEFAULT     = "Default";
   public static final String    LINKED      = "Linked";
   public static final Integer   NONE        = new Integer(-1);
   
   private int[][]           _matrix;
   private String[]          _alters;
   private int               _baseIndex;
   private int               _numAlters;
   private String            _egoName;
   private GraphDraw         _graphDraw;
   private Graph             _graph;
   private Vertex            _ego;
   private Vertex            _base;
   private Map               _edges;
   private String            _mode              = WebShared.VIEW_MODE;

   // URL Stuff
   private String            _hostName          = "localhost";
   private int               _port              = 8080;
   private Long              _interviewId;

   private JPanel            _menuPanel;
   private JComboBox         _selectionMenu     = new JComboBox();
   
   private Panel             _statusBarPanel;
   private Label             _statusLabel;
   private String            _webServerStr      = null;
   private TextArea          _statusTextArea;
   
   // Alter Question Stuff
   private  Vector           _questions;
   private  Map              _answers;
   
   private  JLabel           _answerLabel = new JLabel("");

	public void init()
	{
      // Initialize locals
      _questions      = new Vector(0);
      
		// Setup status logger
		_statusBarPanel = new Panel();
		_statusBarPanel.setBackground(Color.white);
		_statusBarPanel.setLayout(new FlowLayout());

		_statusLabel = new Label("Status Messages");
		_statusBarPanel.add(_statusLabel);

		_statusTextArea = new TextArea(3, 50);
		_statusBarPanel.add(_statusTextArea);
      
      
      // Menu Panel
      _menuPanel = new JPanel();
      _menuPanel.setBorder(BorderFactory.createRaisedBevelBorder());
      _menuPanel.setBackground(Color.white);
      _menuPanel.setLayout(new GridLayout(3, 1));

      _selectionMenu.setBackground(Color.WHITE);
      _menuPanel.add(_selectionMenu);
      _menuPanel.add(_answerLabel);
      _menuPanel.add(new JLabel(""));


      
      _selectionMenu.addActionListener(this);
	}

	/**************
    * Only one control, so called whenever the menu selection changes
	 */
   public void actionPerformed(ActionEvent e)
   {
      log("Menu Action Performed");
      int selectIndex = _selectionMenu.getSelectedIndex();
      
      if (selectIndex >= 0)
      {
         log("Changing selection to " + selectIndex);
         _baseIndex = selectIndex;
         
         if (WebShared.ALTER_QUESTION_MODE.equals(_mode))
         {
            setAnswerLegend();
         }
         else
         {
            setVertexTypes();
         }
         _graphDraw.repaint();
         //buildAppletPanel();
      }
   }

	public void start()
	{
		log("Applet start");
      
      // Get display mode
      _mode          = this.getParameter("Mode");
      _interviewId   = Long.valueOf(this.getParameter("InterviewId"));
      
      _selectionMenu.removeActionListener(this);
      readInterviewData();
		buildAppletPanel();
      _selectionMenu.addActionListener(this);
	}
	
	/**
    * 
    */
   private void buildAppletPanel()
   {
      log("Building UI for mode " + _mode);
      this.getContentPane().removeAll();
		
		JPanel appletPanel = new JPanel();
		appletPanel.setLayout(new BorderLayout());
		
      _selectionMenu.removeAllItems();

     // Set up menu
      if ((_numAlters > 0) && WebShared.CORRECT_MODE.equals(_mode))
      {
         for (int i = 0; i < _numAlters; ++i)
         {
            System.out.println("Alters[" + i + "] = " + _alters[i]);
            if (_alters[i] == null)
               break;

            _selectionMenu.addItem(_alters[i]);
         }

         _selectionMenu.setSelectedIndex(_baseIndex);
         appletPanel.add(_menuPanel, BorderLayout.EAST);
      }
      else if ((_questions.size() > 0) && WebShared.ALTER_QUESTION_MODE.equals(_mode))
      {
         for (int i = 0; i < _questions.size(); ++i)
         {
            System.out.println("Question[" + i + "] = " + _questions.get(i));
            _selectionMenu.addItem(((QuestionDataValue) _questions.get(i)).getTitle());
         }

         _selectionMenu.setSelectedIndex(0);
         appletPanel.add(_menuPanel, BorderLayout.EAST);
      }

		// Setup Graph Panel
		_graphDraw = buildGraphDraw();
		_graphDraw.setBackground(Color.white);
		_graphDraw.hideStatus();
      
//		appletPanel.add(_statusBarPanel, BorderLayout.NORTH);
		appletPanel.add(_graphDraw, BorderLayout.CENTER);

		this.getContentPane().add(appletPanel);
   }

   public void stop()
	{
		try
		{
			log("Applet stop");
         if (WebShared.LINK_MODE.equals(_mode) || WebShared.CORRECT_MODE.equals(_mode))
         {
            log("Sending Matrix");
            this.writeAdjacencyMatrix();
         }
         
			this.getContentPane().removeAll();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * Simple logging method for status message in the text area.
	 */
	protected void log(String msg)
	{
		_statusTextArea.append(msg + "\n");
      System.out.println(msg);
	}

	public GraphDraw buildGraphDraw()
	{
		_graph = buildGraph();

		/*********
		 * Set up Layout
		 */
		//Layout layout = new FRLayout(_graph);
      Layout layout = new ELSFRLayout(_graph);
		ELSAppletRenderer renderer = new ELSAppletRenderer(_mode);
		renderer.setLabel("LABEL");
		renderer.setSizeKey(BetweennessCentrality.CENTRALITY);

		GraphDraw graphDraw = new GraphDraw(_graph);
		graphDraw.setGraphLayout(layout);
		graphDraw.setRenderer(renderer);

		BetweennessCentrality bc = new BetweennessCentrality(_graph, true);
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

      /*************
       * Set types for drawing in linking or correction mode
       */
      setVertexTypes();
      
      if (WebShared.ALTER_QUESTION_MODE.equals(_mode))
      {
         setAnswerLegend();
      }
      
      if (WebShared.LINK_MODE.equals(_mode) || WebShared.CORRECT_MODE.equals(_mode))
      {
         log ("Installing mouse listener");
         graphDraw.addGraphMouseListener(new myGraphMouseListener() );
      }
      
		return graphDraw;
	}
   
   
   
   /**
    * 
    */
   private Graph buildGraph()
   {
      log ("Build Graph with baseIndex = " + _baseIndex);
      
      Graph graph = new UndirectedSparseGraph();
      _edges = new HashMap(_alters.length);
		StringLabeller undirectedLabeler = StringLabeller.getLabeller(graph);
		Vertex[] vertexList = new Vertex[_alters.length];

		/*********
		 * Fill Graph with Nodes and Edges
		 */
		_ego = new SparseVertex();
      _ego.addUserDatum(INDEX, NONE, UserData.CLONE);
      _ego.addUserDatum(TYPE, EGO, UserData.REMOVE);

      graph.addVertex(_ego);
		try
		{
			undirectedLabeler.setLabel(_ego, _egoName);
		}
		catch (UniqueLabelException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		for (int i = 0; i < _numAlters; ++i)
		{
			try
			{
				vertexList[i] = new SparseVertex();
				vertexList[i].addUserDatum(INDEX, new Integer(i), UserData.CLONE);
				graph.addVertex(vertexList[i]);
				undirectedLabeler.setLabel(vertexList[i], _alters[i]);
				
				log ("Adding Edge from " + _alters[i] + " to me.");
            Edge edge = new UndirectedSparseEdge(vertexList[i], _ego);
				graph.addEdge(edge);
			}
			catch (UniqueLabelException e1)
			{
				e1.printStackTrace();
			}
		}
      
      for (int i = 0; i < _numAlters; ++i)
      {
         for (int j = i + 1; j < _numAlters; ++j)
         {
            if (_matrix[i][j] > 0)
            {
               log("Adding Edge from " + _alters[i] + " to " + _alters[j]);
               Edge edge = new UndirectedSparseEdge(vertexList[i], vertexList[j]);
               graph.addEdge(edge);
               _edges.put(new EdgeIdentifier(i, j), edge);
           }
         }
      }
      
      return graph;
   }
   
   /********
    * Sets Vertex type according to base index
    */
   public void setVertexTypes()
   {
      Set vertexes = _graph.getVertices();
      for (Iterator it = vertexes.iterator(); it.hasNext();)
      {
         SparseVertex vertex = (SparseVertex) it.next();
         int index = ((Integer) vertex.getUserDatum(INDEX)).intValue();
         
         if (index >= 0)
         {
            if (index == _baseIndex)
            {
               _base = vertex;
               vertex.setUserDatum(TYPE, BASE, UserData.REMOVE);
            }
            else
            {
               boolean connected = _matrix[index][_baseIndex] == 1;

               if (!connected)
               {
                  vertex.setUserDatum(TYPE, DEFAULT, UserData.REMOVE);
                  log("Updating unconnected vertex " + _alters[index]);
               }
               else
               {
                  vertex.setUserDatum(TYPE, LINKED, UserData.REMOVE);
                  log("Updating connected vertex " + _alters[index]);
               }
            }
         }
      }
   }
   
   /************
    * Set Answer Legend
    */
   public void setAnswerLegend()
   {
      if (WebShared.ALTER_QUESTION_MODE.equals(_mode))
      {
         log ("Found " + _questions.size() + " questions");
         QuestionDataValue question = (QuestionDataValue) _questions.get(_baseIndex);
         SelectionDataValue[] selections = question.getSelectionDataValues();
         StringBuffer str = new StringBuffer("<html>");
         
         for (int i = 0; i < selections.length; ++i)
         {
            str.append("<FONT COLOR=" + COLORS[i] + "><B>");
            str.append(selections[i].getText());
            str.append("</B></FONT><P>");
         }
         
         str.append("</html>");
         _answerLabel.setText(str.toString());
         
         // Set data in vertexes
         log ("Setting answer values for " + question.getTitle());
         Map answers = (Map) _answers.get(question.getId());
         log ("Found " + answers.size() + " answers");
         
         Set vertexes = _graph.getVertices();
         for (Iterator it = vertexes.iterator(); it.hasNext();)
         {
            SparseVertex vertex = (SparseVertex) it.next();
            Integer index = (Integer) vertex.getUserDatum(INDEX);
            Integer answer = (Integer) answers.get(index);
            
            if (answer != null)
            {
               log ("Found answer for " + _alters[index.intValue()]);
               vertex.setUserDatum(ANSWER, answer, UserData.REMOVE);
            }
            else
            {
               vertex.setUserDatum(ANSWER, NONE, UserData.REMOVE);
            }
         }
      }
   }
   
	/**************************************
	 * Adds vertexes to linked nodes selected by clicking
	 * @author admin
	 *
	 */
	public class myGraphMouseListener 
		implements GraphMouseListener
	{
		public myGraphMouseListener()
		{
		}
		
		public void graphClicked(Vertex v, java.awt.event.MouseEvent me)
		{
			log("graphClicked on " + StringLabeller.getLabeller((Graph) v.getGraph()).getLabel(v));
			
//			_graphDraw.getVisualizationViewer().suspend();

         int newIndex = ((Integer) v.getUserDatum(INDEX)).intValue();
         
         // If vertex is not ego or base
         if ((newIndex >= 0) && (newIndex != _baseIndex))
         {
            boolean connected = _matrix[newIndex][_baseIndex] == 1;

            if (!connected)
            {
               log("adding edge from " + v + " to " + _base);
               Edge edge = new UndirectedSparseEdge(v, _base);
               v.setUserDatum(TYPE, LINKED, UserData.REMOVE);
               _graph.addEdge(edge);
               
               log("Storing " + edge);
               _edges.put(new EdgeIdentifier(newIndex, _baseIndex), edge);
               _matrix[newIndex][_baseIndex] = 1;
               _matrix[_baseIndex][newIndex] = 1;
            }
            else
            {
               log("found " + _edges.size() + " edges");
               
               for (Iterator it = _edges.keySet().iterator(); it.hasNext();)
               {
                  EdgeIdentifier id = (EdgeIdentifier) it.next();
                  log("Edge " + id.i + ":" + id.j);
               }
               
               log("seeking " + newIndex + ":" + _baseIndex);
               log("contains " + _edges.containsKey(new EdgeIdentifier(newIndex, _baseIndex)));
               Edge edge = (Edge) _edges.get(new EdgeIdentifier(newIndex, _baseIndex));
               log("Edge " + edge);

               if (edge != null)
               {
                  log("removing edge from " + v + " to " + _base);
                  log ("edge = " + edge);
                  v.setUserDatum(TYPE, DEFAULT, UserData.REMOVE);
                  _graph.removeEdge(edge);
                  _matrix[newIndex][_baseIndex] = 0;
                  _matrix[_baseIndex][newIndex] = 0;
               }
            }

            _graphDraw.repaint();
         }
			
		} //graphClicked
		
		public void graphPressed(Vertex v, MouseEvent me)
		{
			log("graphPressed");
		}//graphPressed
		
		public void graphReleased(Vertex v, MouseEvent me)
		{
			log("graphReleased");
		}//graphReleased
		
	}//class myGraphMouseListener


   
   /****************************************
    * Edge Identifier Class
    */
   public class EdgeIdentifier
   {
      private final int i, j;
      
      public EdgeIdentifier(int i, int j)
      {
         this.i = Math.min(i, j);
         this.j = Math.max(i, j);
      }
      
       /* (non-Javadoc)
       * @see java.lang.Object#equals(java.lang.Object)
       */
      public boolean equals(Object obj)
      {
         EdgeIdentifier that = (EdgeIdentifier) obj;
         boolean rval = ((this.i == that.i) && (this.j == that.j));
         log ("compare " + this + " " + that + " = " + rval);
         return rval;
      }
      
      public String toString()
      {
         return "EI[" + i + ", " + j + "]";
      }
      
      /* (non-Javadoc)
       * @see java.lang.Object#hashCode()
       */
      public int hashCode()
      {
         int rval = (((17 * 37) + i) * 37) + j;
         log ("hash of " + this + " is " + rval);
         return rval;
      }
   }
   
   
   /**
    *  Routines which communicate with the Servlet to retrive/save data
    */
   private void readInterviewData()
   {
      log("Reading Interview Data");
      
      // get the host name and port of the applet's web server
      URL hostURL = getCodeBase();
      _hostName = hostURL.getHost();
      _port = hostURL.getPort();

      if (_port == -1)
      {
         _port = 80;
      }

      log("Web Server host name: " + _hostName);

      _webServerStr = "http://" + _hostName + ":" + _port + "/EgoWeb/interview";
      log("Web String full = " + _webServerStr);
      log("Interview Id = " + _interviewId);
      log("Base Index = " + this.getParameter("BaseIndex"));
      log("Mode = " + _mode);

      /* Everyone needs the matrix */
      readMatrix();
      
      if (WebShared.ALTER_QUESTION_MODE.equals(_mode))
      {
         readQuestions();
      }
   }

   /**
    * 
    */
   private void readMatrix()
   {
      try
      {
         //     connect to the servlet
         String servletGET = _webServerStr   + "?InterviewId=" + this.getParameter("InterviewId")
                                             + "&Query="       + InterviewDataServlet.MATRIX;

         // connect to the servlet
         log("Connecting...");
         log(servletGET);
         URL interviewServlet = new URL(servletGET);
         URLConnection servletConnection = interviewServlet.openConnection();

         //        Don't used a cached version of URL connection.
         servletConnection.setUseCaches(false);
         servletConnection.setDefaultUseCaches(false);

         //        Read the input from the servlet.
         //
         //        The servlet will return a serialized vector containing
         //        student entries.
         //
         ObjectInputStream inputFromServlet = new ObjectInputStream(servletConnection.getInputStream());
         _matrix     = (int[][]) inputFromServlet.readObject();
         _alters     = (String[]) inputFromServlet.readObject();
         _egoName       = (String) inputFromServlet.readObject();
         
         if (WebShared.LINK_MODE.equals(_mode))
         {
            _baseIndex = Integer.valueOf(this.getParameter(("BaseIndex"))).intValue();
         }
         else
         {
            _baseIndex = 0;
         }
         
         // Count alters
         for (int i = 0; i < _alters.length; ++i)
         {
            log("Alters[" + i + "] = " + _alters[i]);
            if (_alters[i] == null)
               break;

             _numAlters = i + 1;
         }
         log (_numAlters + " alters");

         
         {
            StringBuffer buffer = new StringBuffer();
            int[][] matrix = _matrix;
           // int adjacencies = 0;
            for (int i = 0; i < matrix.length; ++i)
            {
               for (int j = 0; j < i; ++j)
               {
                  buffer.append(matrix[i][j] + " ");
               }
               buffer.append("\n");
            }
            
           buffer.append("\n");
           for (int i = 0; i < _alters.length; ++i)
               buffer.append(_alters[i] + "\n");
            //log(buffer.toString());
         }

         {
            int[][] matrix = _matrix;
            int adjacencies = 0;
            for (int i = 0; i < matrix.length; ++i)
               for (int j = 0; j < i; ++j)
                  if (matrix[i][j] == 1)
                     ++adjacencies;
            log("Read matrix with " + adjacencies + " adjacencies");
         }

         log("Base Index = " + _baseIndex);
      }
      catch (Exception e)
      {
         // TODO Auto-generated catch block
         System.err.println(_webServerStr);
         e.printStackTrace();
      }
   }

   /**
    * 
    */
   private void readQuestions()
   {
      try
      {
         //     connect to the servlet
         String servletGET = _webServerStr   + "?InterviewId=" + this.getParameter("InterviewId")
                                             + "&Query="       + InterviewDataServlet.QUESTIONS;

         // connect to the servlet
         log("Reading Questions");
         log(servletGET);
         URL interviewServlet = new URL(servletGET);
         URLConnection servletConnection = interviewServlet.openConnection();

         //        Don't used a cached version of URL connection.
         servletConnection.setUseCaches(false);
         servletConnection.setDefaultUseCaches(false);

         //        Read the input from the servlet.
         //
         //        The servlet will return a serialized vector containing
         //        student entries.
         //
         ObjectInputStream inputFromServlet = new ObjectInputStream(servletConnection.getInputStream());
         _questions      = (Vector) inputFromServlet.readObject();
         _answers        = (Map) inputFromServlet.readObject();

         {
            log("Found " + _questions.size() + " questions");
            for (int i = 0; i < _questions.size(); ++i)
               log("Question[" + i + "] = " + ((QuestionDataValue) _questions.get(i)).getTitle());
         }

      }
      catch (Exception e)
      {
         // TODO Auto-generated catch block
         System.err.println(_webServerStr);
         e.printStackTrace();
      }
   }

   /**
    * @author admin
    * 
    * To change the template for this generated type comment go to Window -
    * Preferences - Java - Code Generation - Code and Comments
    */
   private void writeAdjacencyMatrix() 
      throws IOException
   {
      URL servlet = new URL(_webServerStr);
      URLConnection servletConnection = servlet.openConnection();
      log("Connected");

      // inform the connection that we will send output and accept input
      servletConnection.setDoInput(true);
      servletConnection.setDoOutput(true);

      // Don't used a cached version of URL connection.
      servletConnection.setUseCaches(false);

      // Specify the content type that we will send binary data
      servletConnection.setRequestProperty("Content-Type", "application/octet-stream");

      // send the student object to the servlet using serialization
      sendMatrixToServlet(servletConnection);

      // now, let's read the response from the servlet.
      // this is simply a confirmation string
      readServletResponse(servletConnection);
      log("Complete");

   }

   /**
    *  Sends an adjacency matrix to the servlet
    */
    private void sendMatrixToServlet(URLConnection servletConnection)
    {
        ObjectOutputStream outputToServlet = null;
        
        try
        {
           // send the student object to the servlet using serialization
           log("Sending the student to the servlet...");
           outputToServlet = new ObjectOutputStream(servletConnection.getOutputStream());
           
           // serialize the object
           outputToServlet.writeObject(_interviewId);
           outputToServlet.writeObject(_matrix);
           
           outputToServlet.flush();         
           outputToServlet.close();
           log("Complete.");
        }
        catch (IOException e)
        {
          log(e.toString());    
        }
    }

   /**
    *  Reads a response from the servlet.
    */
   protected void readServletResponse(URLConnection servletConnection)
   {
      BufferedReader inFromServlet = null;
      
      try
      {
         // now, let's read the response from the servlet.
         // this is simply a confirmation string
         inFromServlet = new BufferedReader(new InputStreamReader(servletConnection.getInputStream()));
         
         String str;
         while (null != ((str = inFromServlet.readLine())))
         {
            log("Reading servlet response: " + str);
         }
         
         inFromServlet.close();
      }
      catch (IOException e)
      {
         log(e.toString());    
      }
   }
 

}
