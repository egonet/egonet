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
package com.endlessloopsoftware.egonet.web.actions;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
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
public final class AlterQuestionsSubmitAction extends ELSAction
{
	InterviewSBLocal _interviewSB;
	
	/* (non-Javadoc)
	 * @see org.apache.struts.action.Action#perform(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	public ActionForward perform(	ActionMapping mapping, ActionForm pform, 
									HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException
	{
		Logger 					logger 			= Logger.getLogger(this.getClass());
		QuestionListForm 		form 				= (QuestionListForm) pform;
		InterviewEJBLocal 	interview;
		InterviewDataValue 	interviewData;
		List						previousAnswers;
		
		logger.debug("AlterQuestionSubmitAction::perform");
				
		// Lookup Interview
		try 
		{
			// Get Interview from session context
			interview 			= WebShared.retrieveInterview(request);
			interviewData 		= WebShared.retrieveInterviewDataValue(request);
			previousAnswers	= Arrays.asList(interviewData.getAnswerDataValues());
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
			int					index		= Collections.binarySearch(previousAnswers, answer);

			if (index >= 0)
			{
				AnswerDataValue previousAnswer = (AnswerDataValue) previousAnswers.get(index);
				previousAnswer.setAnswerString(answer.getAnswerString());
				previousAnswer.fillAnswer();
				interviewData.updateAnswerDataValue(previousAnswer);
			}
			else
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
 * $Log: AlterQuestionsSubmitAction.java,v $
 * Revision 1.5  2004/04/05 21:23:57  admin
 * Can't cache InterviewDataValue since matrix is set from another session.
 * Applet Linking now works.
 *
 * Revision 1.4  2004/03/18 15:23:40  admin
 * Recovers previous interview state
 *
 * Revision 1.3  2004/03/12 18:05:28  admin
 * Applet now works under Windows IE.
 * Fixed layout issues related to struts-layout converting spaces to nbsp
 * Using Servlet for applet/server communications
 *
 * Revision 1.2  2004/02/15 14:59:01  admin
 * Fixing Header Tags
 *
 * Revision 1.1  2004/02/15 14:37:38  admin
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