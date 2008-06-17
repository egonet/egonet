/**
 * <p>Title: Egocentric Networks Client Program</p>
 * <p>Description: Subject Interview Client</p>
 * <p>Copyright: Copyright (c) 2002 - 2004 </p>
 * <p>Company: Endless Loop Software</p>
 * @author Peter Schoaff
 *
 * $Id: EgoQuestionSubmitAction.java,v 1.9 2004/04/10 23:09:22 admin Exp $
 */
package com.endlessloopsoftware.egonet.web.actions;

import java.io.IOException;
import java.util.Iterator;
import java.util.Vector;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.jboss.logging.Logger;

import com.endlessloopsoftware.egonet.interfaces.InterviewEJBLocal;
import com.endlessloopsoftware.egonet.interfaces.InterviewSBLocal;
import com.endlessloopsoftware.egonet.util.AnswerDataValue;
import com.endlessloopsoftware.egonet.util.InterviewDataValue;
import com.endlessloopsoftware.egonet.web.WebShared;
import com.endlessloopsoftware.egonet.web.forms.QuestionListForm;

/**
 * @author admin
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public final class EgoQuestionSubmitAction extends ELSAction
{
	InterviewSBLocal _interviewSB;
	
	/* (non-Javadoc)
	 * @see org.apache.struts.action.Action#perform(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	public ActionForward perform(	ActionMapping mapping, ActionForm pform, 
											HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException
	{
		Logger 					 logger 			= Logger.getLogger(this.getClass());
		QuestionListForm      form 				= (QuestionListForm) pform;
		InterviewEJBLocal     interview;
		InterviewDataValue    interviewData;
		AnswerDataValue[]     previousAnswers;
		
		logger.debug("EgoQuestionSubmitAction::perform");
				
		// Lookup Interview
		try 
		{
			// Get Interview from session context
			interview         = WebShared.retrieveInterview(request);
			interviewData     = WebShared.retrieveInterviewDataValue(request);
			previousAnswers   = interviewData.getAnswerDataValues();
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
			throw new IOException(e.getMessage());
		}
		
		Vector answers = form.getAnswers();
		for (Iterator it = answers.iterator(); it.hasNext();)
		{
			AnswerDataValue 	answer 	= (AnswerDataValue) it.next();
			boolean 				found		= false;

			for (int i = 0; i < previousAnswers.length; ++i)
			{
				if (previousAnswers[i].equals(answer))
				{
					previousAnswers[i].setAnswerString(answer.getAnswerString());
					previousAnswers[i].fillAnswer();
					interviewData.updateAnswerDataValue(previousAnswers[i]);
					found = true;
				}
			}
			
			if (!found)
			{
				answer.fillAnswer();
				interviewData.addAnswerDataValue(answer);
			}
			
			logger.debug(answer.getAnswerString());
		}
		
		try
		{
			interview.setInterviewDataValue(interviewData);
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
			throw new IOException(e.getMessage());
		}
		
		return (mapping.findForward(WebShared.FORWARD_SUCCESS));
	}
	
}


/**
 * $Log: EgoQuestionSubmitAction.java,v $
 * Revision 1.9  2004/04/10 23:09:22  admin
 * Implemented Three Step Elicitation
 *   Added Position Forward Action
 *   Store current prompt type as part of answer and interview position
 *
 * Using custom version of FR Layout to try to keep labels from overlapping
 * borders
 *
 * Revision 1.8  2004/04/05 21:23:58  admin
 * Can't cache InterviewDataValue since matrix is set from another session.
 * Applet Linking now works.
 *
 * Revision 1.7  2004/03/18 15:23:40  admin
 * Recovers previous interview state
 *
 * Revision 1.6  2004/03/12 18:05:28  admin
 * Applet now works under Windows IE.
 * Fixed layout issues related to struts-layout converting spaces to nbsp
 * Using Servlet for applet/server communications
 *
 * Revision 1.5  2004/02/15 14:59:02  admin
 * Fixing Header Tags
 *
 * Revision 1.4  2004/02/15 14:37:39  admin
 * Displaying network graph on web pages
 *
 * Revision 1.3  2004/02/10 16:31:24  admin
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