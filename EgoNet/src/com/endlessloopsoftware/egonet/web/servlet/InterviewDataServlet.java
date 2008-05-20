/*
 * Created on Mar 5, 2004
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package com.endlessloopsoftware.egonet.web.servlet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jboss.logging.Logger;

import com.endlessloopsoftware.egonet.interfaces.InterviewEJBPK;
import com.endlessloopsoftware.egonet.interfaces.InterviewSBLocal;
import com.endlessloopsoftware.egonet.interfaces.InterviewSBLocalHome;
import com.endlessloopsoftware.egonet.interfaces.InterviewSBUtil;
import com.endlessloopsoftware.egonet.interfaces.StudySBLocal;
import com.endlessloopsoftware.egonet.interfaces.StudySBLocalHome;
import com.endlessloopsoftware.egonet.interfaces.StudySBUtil;
import com.endlessloopsoftware.egonet.util.AnswerDataValue;
import com.endlessloopsoftware.egonet.util.InterviewDataValue;
import com.endlessloopsoftware.egonet.util.QuestionDataValue;

/**
 * @author admin
 * 
 * To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Generation - Code and Comments
 */
public class InterviewDataServlet extends HttpServlet
{
	public final Logger	logger	= Logger.getLogger(this.getClass());
   
   public final static String  MATRIX      = "matrix";
   public final static String  QUESTIONS   = "questions";
   public final static String  SELECTIONS  = "selections";

	/**
	 * This method is used by HTML clients and applets.
	 * 
	 * Handles a request and reads information from HTML form data.
	 * 
	 * Figures out if the HTML form is sending a request to register or display
	 * the students. Also, handles a request from an applet to simply display
	 * the students.
	 */
	public void doGet(HttpServletRequest request, HttpServletResponse response) 
		throws ServletException, IOException
	{
		ObjectOutputStream outputToApplet;
		logger.debug("in doGet(" + request + ")");
      logger.debug("Query = " + request.getParameterValues("Query"));

      String interviewId   = request.getParameterValues("InterviewId")[0];
      String query         = request.getParameterValues("Query")[0];
      
      logger.debug("InterviewId == " + interviewId + "; Query == " + query);

      try
		{
	      InterviewSBLocalHome interviewSBHome  = InterviewSBUtil.getLocalHome();
			InterviewSBLocal     interviewSB      = interviewSBHome.create();
	
			InterviewEJBPK interviewPk            = new InterviewEJBPK(Long.valueOf(interviewId));
			InterviewDataValue interviewData      = interviewSB.findByPrimaryKey(interviewPk);

			outputToApplet = new ObjectOutputStream(response.getOutputStream());

         if (QUESTIONS.equals(query))
         {
            logger.debug("Returning Alter Questions");
            StudySBLocalHome  studySBHome = StudySBUtil.getLocalHome();
            StudySBLocal      studySB     = studySBHome.create();
            
            Vector questions = studySB.getDisplayableAlterQuestions(interviewData.getStudyId());
            
            AnswerDataValue[] answers     = interviewData.getAnswerDataValues();
            Map answerMap                 = new HashMap();
            
            for (int i = 0; i < questions.size(); ++i)
            {
               Map answerIndexes = new HashMap();
               Long id = ((QuestionDataValue) questions.get(i)).getId();
               
               for (int j = 0; j < answers.length; ++j)
               {
                  AnswerDataValue answer = answers[j];
                  if (answer.getQuestionId() == id)
                  {
                     answerIndexes.put(new Integer(answer.getAlters().getPrimaryAlter()), new Integer(answer.getAnswerIndex()));
                  }
               }
               
               logger.debug("Returning " + answerIndexes.size() + " answers for Question " + 
                            ((QuestionDataValue) questions.get(i)).getTitle());
               answerMap.put(id, answerIndexes);
            }
            
            logger.debug("Sending " + questions.size() + " questions");
            outputToApplet.writeObject(questions);
            outputToApplet.writeObject(answerMap);
         }
         else
         {
            logger.debug("Sending InterviewData to applet...");
            {
               int[][] matrix = interviewData.getAdjacencyMatrix();
               int adjacencies = 0;
               for (int i = 0; i < matrix.length; ++i)
                  for (int j = 0; j < i; ++j)
                     if (matrix[i][j] == 1)
                        ++adjacencies;
               logger.debug("Sending matrix of size " + matrix.length + " with " + adjacencies + " adjacencies");
            }            

            outputToApplet.writeObject(interviewData.getAdjacencyMatrix());
            outputToApplet.writeObject(interviewData.getAlters());
            outputToApplet.writeObject(interviewData.getFirstName()  + " " + interviewData.getLastName().substring(0, 1));
         }
					
			outputToApplet.flush();

			outputToApplet.close();
			logger.debug("Data transmission complete.");
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	/**
	 *  This method is used for applets.
	 *
	 *  Receives and sends the data using object serialization.
	 *
	 *  Gets an input stream from the applet and reads a student object.  Then
	 *  registers the student using our data accessor.  Finally, sends a confirmation
	 *  message back to the applet.
	 */
    public void doPost(HttpServletRequest request, HttpServletResponse response) 
       throws ServletException, IOException
   {
      ObjectInputStream inputFromApplet   = null;
      int[][]           matrix            = null;
      Long              interviewId;
      PrintWriter       out               = null;
      BufferedReader    inTest            = null;

      try
      {
         // get an input stream from the applet
         inputFromApplet = new ObjectInputStream(request.getInputStream());
         logger.debug("Connected");

         // read the serialized student data from applet        
         logger.debug("Reading data...");
         interviewId = (Long) inputFromApplet.readObject();
         matrix = (int[][]) inputFromApplet.readObject();
         logger.debug("Finished reading.");

         inputFromApplet.close();

         logger.debug("Complete.");

         // send back a confirmation message to the applet
         out = new PrintWriter(response.getOutputStream());
         response.setContentType("text/plain");
         out.println("confirmed");
         out.flush();
         out.close();

         logger.debug("Received adjacency data from applet...");
         {
            int adjacencies = 0;
            for (int i = 0; i < matrix.length; ++i)
               for (int j = 0; j < i; ++j)
                  if (matrix[i][j] == 1)
                     ++adjacencies;
                  
            logger.debug("Received matrix of size " + matrix.length + " with " + 
                         adjacencies + " adjacencies for interview " + interviewId);
         }
         
         InterviewSBLocalHome interviewSBHome = InterviewSBUtil.getLocalHome();
         InterviewSBLocal interviewSB = interviewSBHome.create();
   
         InterviewEJBPK interviewPk = new InterviewEJBPK(interviewId);
         interviewSB.setAdjacencyMatrix(interviewPk, matrix);
      }
      catch (Exception e)
      {
         e.printStackTrace();
      }
   }
    

}
