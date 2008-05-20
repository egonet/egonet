/**
 * <p>Title: Egocentric Networks Client Program</p>
 * <p>Description: Subject Interview Client</p>
 * <p>Copyright: Copyright (c) 2002 - 2004 </p>
 * <p>Company: Endless Loop Software</p>
 * @author Peter Schoaff
 *
 * $Id: AlterPairAppletAction.java,v 1.5 2004/05/17 00:05:24 admin Exp $
 */
package com.endlessloopsoftware.egonet.web.actions;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.util.ModuleException;
import org.jboss.logging.Logger;

import com.endlessloopsoftware.egonet.AlterPair;
import com.endlessloopsoftware.egonet.InterviewPosition;
import com.endlessloopsoftware.egonet.Shared;
import com.endlessloopsoftware.egonet.interfaces.StudyEJBLocal;
import com.endlessloopsoftware.egonet.util.AnswerDataValue;
import com.endlessloopsoftware.egonet.util.InterviewDataValue;
import com.endlessloopsoftware.egonet.web.WebShared;
import com.endlessloopsoftware.egonet.web.forms.AppletLinkForm;

/**
 * @author admin
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public final class AlterPairAppletAction extends ELSAction
{
	/* (non-Javadoc)
	 * @see org.apache.struts.action.Action#perform(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	public ActionForward perform(	ActionMapping mapping, ActionForm pform, 
									HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException
	{
		Logger          logger   = Logger.getLogger(this.getClass());
      AppletLinkForm  form     = (AppletLinkForm) pform;
      
      logger.debug("SUBMIT");
            
      // Lookup Interview
      try 
      {
         StudyEJBLocal        study             = WebShared.retrieveStudy(servlet);
         InterviewDataValue   interviewData     = WebShared.retrieveInterviewDataValue(request);
         InterviewPosition    position          = WebShared.retrieveInterviewPosition(request);
         List                 previousAnswers   = Arrays.asList(interviewData.getAnswerDataValues());
         
         AnswerDataValue answer = new AnswerDataValue(null, new AlterPair(position.getPrimaryAlter()),
                                                      Shared.LINK_TO_NONE, new String[0], false, -1, -1, "", false,
                                                      study, form.getAnswer().getQuestion());
         
         // Check if this was already answered
         int index = Collections.binarySearch(previousAnswers, answer);

         // This is the simple case, just mark single question as answered
         if (index >= 0)
         {
            AnswerDataValue previousAnswer = (AnswerDataValue) previousAnswers.get(index);
            previousAnswer.setAnswerString(answer.getAnswerString());
            previousAnswer.fillAppletLinkAnswer();
            logger.debug("Found previous matching answer " + answer);
            interviewData.updateAnswerDataValue(previousAnswer);
         }
         else
         {
            answer.setAnswerString(answer.getAnswerString());
            answer.fillAppletLinkAnswer();
            logger.debug("Storing New Answer " + answer);
            interviewData.addAnswerDataValue(answer);
         }
         
         // Store new answer
         WebShared.retrieveInterview(request).setInterviewDataValue(interviewData);
      } 
      catch (ModuleException e)
      {
         e.printStackTrace();
         throw new IOException(e.getMessage());
      }
		
		return (mapping.findForward(WebShared.FORWARD_SUCCESS));
	}
}


/**
 * $Log: AlterPairAppletAction.java,v $
 * Revision 1.5  2004/05/17 00:05:24  admin
 * Node coloring in alter question screens
 *
 * Revision 1.4  2004/04/10 23:09:22  admin
 * Implemented Three Step Elicitation
 *   Added Position Forward Action
 *   Store current prompt type as part of answer and interview position
 *
 * Using custom version of FR Layout to try to keep labels from overlapping
 * borders
 *
 * Revision 1.3  2004/04/06 14:42:14  admin
 * Completed Applet Linking
 *
 * Revision 1.2  2004/04/05 21:23:57  admin
 * Can't cache InterviewDataValue since matrix is set from another session.
 * Applet Linking now works.
 *
 * Revision 1.1  2004/04/05 20:32:21  admin
 * Modifying workflow to use applet for linking
 *
 */