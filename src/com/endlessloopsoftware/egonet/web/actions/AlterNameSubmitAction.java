/**
 * <p>Title: Egocentric Networks Client Program</p>
 * <p>Description: Subject Interview Client</p>
 * <p>Copyright: Copyright (c) 2002 - 2004 </p>
 * <p>Company: Endless Loop Software</p>
 * @author Peter Schoaff
 *
 * $Id: AlterNameSubmitAction.java,v 1.9 2004/04/10 23:09:22 admin Exp $
 */
package com.endlessloopsoftware.egonet.web.actions;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.actions.DispatchAction;
import org.apache.struts.util.ModuleException;
import org.jboss.logging.Logger;

import com.endlessloopsoftware.egonet.AlterPair;
import com.endlessloopsoftware.egonet.InterviewPosition;
import com.endlessloopsoftware.egonet.Shared;
import com.endlessloopsoftware.egonet.interfaces.InterviewSBLocal;
import com.endlessloopsoftware.egonet.interfaces.InterviewSBLocalHome;
import com.endlessloopsoftware.egonet.interfaces.InterviewSBUtil;
import com.endlessloopsoftware.egonet.interfaces.QuestionEJBLocal;
import com.endlessloopsoftware.egonet.interfaces.StudyEJBLocal;
import com.endlessloopsoftware.egonet.util.AnswerDataValue;
import com.endlessloopsoftware.egonet.util.InterviewDataValue;
import com.endlessloopsoftware.egonet.web.WebShared;
import com.endlessloopsoftware.egonet.web.forms.AlterNameForm;

/**
 * @author admin
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public final class AlterNameSubmitAction 
   extends DispatchAction
{
   Logger            logger = Logger.getLogger(this.getClass().getName());
	InterviewSBLocal  _interviewSB;
	
   /*****
    * Marks PairsComplete for the current interview
    * 
    **/
   public ActionForward complete(ActionMapping mapping, ActionForm pform, HttpServletRequest request,
                                 HttpServletResponse response) 
      throws IOException, ServletException
   {
      logger.debug("AlterPromptSubmitAction::complete");
      AlterNameForm form = (AlterNameForm) pform;
      
      try
      {
         if (form.getPromptType().equals(Shared.LINK_PAIR))
         {
            // Just mark pairs complete
            InterviewPosition position = WebShared.retrieveInterviewPosition(request);
            position.setAlterPromptType(Shared.LINK_TO_NONE);
         }
         else if (form.getPromptType().equals(Shared.LINK_TO_NEXT))
         {
            // Just change type of prompt
            InterviewPosition position = WebShared.retrieveInterviewPosition(request);
            position.setAlterPromptType(Shared.LINK_TO_NONE);
         }
         else if (form.getPromptType().equals(Shared.LINK_TO_PRIOR))
         {
            // Just change type of prompt
            InterviewPosition position = WebShared.retrieveInterviewPosition(request);
            position.setAlterPromptType(Shared.LINK_TO_NEXT);
         }
      }
      catch (Exception e)
      {
         e.printStackTrace();
         throw new IOException(e.getMessage());
      }

      return (mapping.findForward(WebShared.FORWARD_POSITION));
   }
   
	/*
    * (non-Javadoc)
    * 
    * @see org.apache.struts.action.Action#perform(org.apache.struts.action.ActionMapping,
    *      org.apache.struts.action.ActionForm,
    *      javax.servlet.http.HttpServletRequest,
    *      javax.servlet.http.HttpServletResponse)
    */
	public ActionForward submit(	ActionMapping mapping, ActionForm pform, 
									HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException
	{
		AlterNameForm form = (AlterNameForm) pform;
		
		logger.debug("AlterPromptSubmitAction::submit");
				
		// Lookup Interview
		try 
		{
			// Get Interview from session context
         StudyEJBLocal        study             = WebShared.retrieveStudy(servlet);
			InterviewDataValue 	interviewData 		= WebShared.retrieveInterviewDataValue(request);
			InterviewPosition		position				= WebShared.retrieveInterviewPosition(request);
			List						previousAnswers	   = Arrays.asList(interviewData.getAnswerDataValues());
         String[]             alters            = interviewData.getAlters();
         InterviewPosition[]  template          = WebShared.retrieveTemplate(servlet);
         Map                  questionMap       = WebShared.retrieveQuestionMap(servlet);
         Long[][]             orders            = study.getQuestionOrder();
         QuestionEJBLocal     promptQuestion    = (QuestionEJBLocal) questionMap.get(orders[Shared.ALTER_PROMPT][0]);
         QuestionEJBLocal     pairQuestion      = (QuestionEJBLocal) questionMap.get(orders[Shared.ALTER_PAIR_QUESTION][0]);
         
         
			// Add answer to database 
			AnswerDataValue answer  = form.getAnswer();
         answer.setAnswerString(form.getNameOne());

         if (form.getPromptType().equals(Shared.LINK_PAIR))
         {
            // Pair Linking UI, must skip over next alter prompt and both alter pair questions
            answer = new AnswerDataValue(null, new AlterPair(position.getPrimaryAlter()), Shared.LINK_PAIR,
                                         new String[0], false, -1, -1, form.getNameOne(), false, study, promptQuestion);
            storeCompletedAnswer(study, interviewData, previousAnswers, answer);

            answer = new AnswerDataValue(null, new AlterPair(position.getPrimaryAlter()), Shared.NOT_ALTER_PROMPT,
                                         new String[0], false, -1, -1, "", false, study, pairQuestion);
            storeCompletedAnswer(study, interviewData, previousAnswers, answer);

            answer = new AnswerDataValue(null, new AlterPair(position.getPrimaryAlter() + 1), Shared.LINK_PAIR,
                                         new String[0], false, -1, -1, form.getNameTwo(), false, study, promptQuestion);
            storeCompletedAnswer(study, interviewData, previousAnswers, answer);

            answer = new AnswerDataValue(null, new AlterPair(position.getPrimaryAlter() + 1),
                                         Shared.NOT_ALTER_PROMPT, new String[0], false, -1, -1, "", false, study,
                                         pairQuestion);
            storeCompletedAnswer(study, interviewData, previousAnswers, answer);
            
            // Add alter to alters list
            alters[position.getPrimaryAlter()]     = form.getNameOne();
            alters[position.getPrimaryAlter() + 1] = form.getNameTwo();
            
            // Mark Adjacency
            int[][] matrix = interviewData.getAdjacencyMatrix();
            matrix[position.getPrimaryAlter()][position.getPrimaryAlter() + 1] = 1;
            matrix[position.getPrimaryAlter() + 1][position.getPrimaryAlter()] = 1;

            logger.debug("Skipping over questions...");
            logger.debug(template[position.getGlobalPosition() + 0]);
            logger.debug(template[position.getGlobalPosition() + 1]);
            logger.debug(template[position.getGlobalPosition() + 2]);
            logger.debug(template[position.getGlobalPosition() + 3]);

            position = template[position.getGlobalPosition() + 3];
         }
         else if (form.getPromptType().equals(Shared.LINK_TO_NEXT))
         {
            // Used for Three question elicitations. Skip over alter pair question
            storeCompletedAnswer(study, interviewData, previousAnswers, answer);
            alters[position.getPrimaryAlter()] = form.getNameOne();
 
            answer = new AnswerDataValue(null, new AlterPair(position.getPrimaryAlter()), Shared.LINK_TO_NEXT,
                                         new String[] {form.getNameOne()}, false, -1, -1, "", false, study, pairQuestion);
            storeCompletedAnswer(study, interviewData, previousAnswers, answer);
            
            // Update position to skip over alter pair
            position = template[position.getGlobalPosition() + 1];
            position.setAlterPromptType(Shared.LINK_TO_NEXT);
         }
         else if (form.getPromptType().equals(Shared.LINK_TO_PRIOR))
         {
            // Used for Three question elicitations. Skip over alter pair question
            storeCompletedAnswer(study, interviewData, previousAnswers, answer);
            alters[position.getPrimaryAlter()] = form.getNameOne();
 
            answer = new AnswerDataValue(null, new AlterPair(position.getPrimaryAlter()), Shared.LINK_TO_PRIOR,
                                         new String[] {form.getNameOne()}, false, -1, -1, "", false, study, pairQuestion);
            storeCompletedAnswer(study, interviewData, previousAnswers, answer);
            
            // Mark Adjacency
            int[][] matrix = interviewData.getAdjacencyMatrix();
            matrix[position.getPrimaryAlter()][position.getPrimaryAlter() - 1] = 1;
            matrix[position.getPrimaryAlter() - 1][position.getPrimaryAlter()] = 1;

            // Update position to skip over alter pair question
            position = template[position.getGlobalPosition() + 1];
            position.setAlterPromptType(Shared.LINK_TO_PRIOR);
         }
         else
         {
            storeCompletedAnswer(study, interviewData, previousAnswers, answer);
            alters[position.getPrimaryAlter()] = form.getNameOne();
         }

			// Update session variables
			logger.debug("Saving Names in Interview +> " + form.getNameOne() + ", " + form.getNameTwo());
			WebShared.retrieveInterview(request).setInterviewDataValue(interviewData);
         
         try
         {
            logger.debug("Storing updated position => " + position);
            WebShared.storeInterviewPosition(request, position);
         }
         catch (ModuleException e)
         {
            e.printStackTrace();
            throw new ServletException(e.getMessage());
         }
			
			StringBuffer str = new StringBuffer();
			for (int i = 0; i < alters.length; ++i)
			{
				str.append(i + ": " + alters[i] + ", ");
			}
			logger.debug(str.toString());

		} 
		catch (Exception e) 
		{
			e.printStackTrace();
			throw new IOException(e.getMessage());
		}
			
		return (mapping.findForward(WebShared.FORWARD_SUCCESS));
	}
	
	/**
    * @param interviewData
    * @param previousAnswers
    * @param answer
    */
   private void storeCompletedAnswer(StudyEJBLocal study, InterviewDataValue interviewData, List previousAnswers, AnswerDataValue answer)
   {
      int index = Collections.binarySearch(previousAnswers, answer);

      // This is the simple case, just mark single question as answered
      	if (index >= 0)
      	{
      		AnswerDataValue previousAnswer = (AnswerDataValue) previousAnswers.get(index);
      		previousAnswer.setAnswerString(answer.getAnswerString());
      		fillAnswer(study, previousAnswer);
      		interviewData.updateAnswerDataValue(previousAnswer);
      	}
      	else
      	{
      		answer.setAnswerString(answer.getAnswerString());
         fillAnswer(study, answer);
      		interviewData.addAnswerDataValue(answer);
      	}
   }
   
   private void fillAnswer(StudyEJBLocal study, AnswerDataValue answer)
   {
      if (study.isAppletUI() && (answer.getQuestion().getQuestionType() == Shared.ALTER_PAIR_QUESTION))
      {
         answer.fillAppletLinkAnswer();
      }
      else
      {
         answer.fillAnswer();
      }
   }

   public InterviewSBLocal getInterviewSB()
	{
		if (_interviewSB == null)
		{	
			try
			{
				InterviewSBLocalHome interviewSBHome = InterviewSBUtil.getLocalHome();
				_interviewSB = interviewSBHome.create();
			} 
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		
		return _interviewSB;
   }


   protected Map getKeyMethodMap() 
   {
      logger.debug("Get Key Map");
      Map map = new HashMap();
      map.put("alterprompt.button.submit",   "submit");
      map.put("alterprompt.button.complete", "complete");
      return map;
   }
}


/**
 * $Log: AlterNameSubmitAction.java,v $
 * Revision 1.9  2004/04/10 23:09:22  admin
 * Implemented Three Step Elicitation
 *   Added Position Forward Action
 *   Store current prompt type as part of answer and interview position
 *
 * Using custom version of FR Layout to try to keep labels from overlapping
 * borders
 *
 * Revision 1.8  2004/04/06 14:42:13  admin
 * Completed Applet Linking
 *
 * Revision 1.7  2004/04/05 21:23:57  admin
 * Can't cache InterviewDataValue since matrix is set from another session.
 * Applet Linking now works.
 *
 * Revision 1.6  2004/04/05 20:32:21  admin
 * Modifying workflow to use applet for linking
 *
 * Revision 1.5  2004/04/05 01:16:43  admin
 * Modifying to use new Applet Linking Interface
 *
 * Revision 1.4  2004/03/18 15:23:40  admin
 * Recovers previous interview state
 *
 * Revision 1.3  2004/02/15 14:59:01  admin
 * Fixing Header Tags
 *
 * Revision 1.2  2004/02/15 14:37:38  admin
 * Displaying network graph on web pages
 *
 * Revision 1.1  2004/02/10 16:31:23  admin
 * Alter Prompt Completed
 *
 * Revision 1.2  2004/02/08 13:40:09  admin
 * Ego Questions Implemented.
 * Full workflow path complete in struts-config.xml
 * Took control of DataValue Objects from XDoclet
 *
 * Revision 1.1  2004/02/07 04:33:26  admin
 * First egoquestions page
 *
 * Revision 1.3  2004/01/30 23:31:09  admin
 * Using struts-layout
 *
 * Revision 1.6  2004/01/23 13:36:07  admin
 * Updating Libraries
 * Allowing upload to web server
 *
 */