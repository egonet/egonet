/**
 * <p>Title: Egocentric Networks Client Program</p>
 * <p>Description: Subject Interview Client</p>
 * <p>Copyright: Copyright (c) 2002 - 2004 </p>
 * <p>Company: Endless Loop Software</p>
 * @author Peter Schoaff
 *
 * $Id: WelcomeAction.java,v 1.10 2004/04/05 01:16:44 admin Exp $
 */
package com.endlessloopsoftware.egonet.web.actions;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.jboss.logging.Logger;

import com.endlessloopsoftware.egonet.InterviewPosition;
import com.endlessloopsoftware.egonet.interfaces.StudyEJBLocal;
import com.endlessloopsoftware.egonet.web.WebShared;

/**
 * First Action in EgoNet Interview. Primary purpose is to fetch the study and
 * store it in the application context.
 */

public final class WelcomeAction extends ELSAction
{
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.struts.action.Action#perform(org.apache.struts.action.ActionMapping,
	 *      org.apache.struts.action.ActionForm,
	 *      javax.servlet.http.HttpServletRequest,
	 *      javax.servlet.http.HttpServletResponse)
	 */
	public ActionForward perform(ActionMapping mapping, ActionForm pform, HttpServletRequest request,
			HttpServletResponse response) throws IOException, ServletException
	{
		Logger logger = Logger.getLogger(this.getClass());

		/*************************************************************************
		 * Fetch Study information, store it in application context if it's not
		 * already there.
		 */
		StudyEJBLocal	studyl = this.getStudy();
		
		logger.debug(studyl.getStudyName());

		InterviewPosition[] template = studyl.getInterviewTemplate();

		logger.debug("Storing New Template with " + template.length + " questions");
		WebShared.storeTemplate(servlet, template);

		/*************************************************************************
		 * Forward to next action
		 */
		ActionForward forward = mapping.findForward(WebShared.FORWARD);
		logger.debug(forward);

		return (forward);
	}
}

/**
 * $Log: WelcomeAction.java,v $
 * Revision 1.10  2004/04/05 01:16:44  admin
 * Modifying to use new Applet Linking Interface
 *
 * Revision 1.9  2004/03/18 15:23:40  admin
 * Recovers previous interview state
 * Revision 1.8 2004/02/15 14:59:02 admin Fixing
 * Header Tags
 * 
 * Revision 1.7 2004/02/15 14:44:15 admin fixing headers
 * 
 * Revision 1.6 2004/02/15 14:37:39 admin Displaying network graph on web pages
 * 
 * Revision 1.5 2004/02/10 16:31:24 admin Alter Prompt Completed
 * 
 * Revision 1.4 2004/02/07 04:33:26 admin First egoquestions page
 * 
 * Revision 1.3 2004/01/30 23:31:09 admin Using struts-layout
 * 
 * Revision 1.6 2004/01/23 13:36:07 admin Updating Libraries Allowing upload to
 * web server
 *  
 */