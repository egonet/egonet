/**
 * <p>Title: Egocentric Networks Client Program</p>
 * <p>Description: Subject Interview Client</p>
 * <p>Copyright: Copyright (c) 2002 - 2004 </p>
 * <p>Company: Endless Loop Software</p>
 * @author Peter Schoaff
 *
 * $Id: EgoNameAction.java,v 1.11 2004/05/26 12:35:54 admin Exp $
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
import com.endlessloopsoftware.egonet.interfaces.InterviewEJBLocal;
import com.endlessloopsoftware.egonet.interfaces.InterviewSBLocal;
import com.endlessloopsoftware.egonet.interfaces.InterviewSBLocalHome;
import com.endlessloopsoftware.egonet.interfaces.InterviewSBUtil;
import com.endlessloopsoftware.egonet.web.WebShared;
import com.endlessloopsoftware.egonet.web.forms.EgoNameForm;

/**
 * @author admin
 * 
 * To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Generation - Code and Comments
 */
public final class EgoNameAction
   extends ELSAction
{
   InterviewSBLocal _interviewSB;

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
      EgoNameForm form = (EgoNameForm) pform;
      InterviewPosition position = null;

      logger.debug("EgoNameAction::perform");
      logger.debug(form.getFirstName() + " " + form.getLastName());

      // Lookup Interview
      try
      {
         InterviewSBLocal interviewSB = getInterviewSB();
         InterviewEJBLocal interview = interviewSB.findUserInterview(getStudy(), form.getFirstName(),
                                                                     form.getLastName(), true);
         logger.debug("Interview is now: " + interview.getInterviewDataValue());
         WebShared.storeInterview(request, interview);

         position = interview.calculateInterviewPosition(WebShared.retrieveTemplate(servlet));
         logger.debug("Restarting from position => " + position);
         WebShared.storeInterviewPosition(request, position);
      }
      catch (Exception e)
      {
         e.printStackTrace();
         throw new IOException(e.getMessage());
      }

      if (position.getGlobalPosition() == 0)
         return (mapping.findForward(WebShared.FORWARD_TUTORIAL));
      else
         return (mapping.findForward(WebShared.FORWARD_POSITION));
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

}

/**
 * $Log: EgoNameAction.java,v $
 * Revision 1.11  2004/05/26 12:35:54  admin
 * Adding tutorial
 * Revision 1.10 2004/04/10 23:09:22 admin
 * Implemented Three Step Elicitation Added Position Forward Action Store
 * current prompt type as part of answer and interview position
 * 
 * Using custom version of FR Layout to try to keep labels from overlapping
 * borders
 * 
 * Revision 1.9 2004/04/05 21:23:58 admin Can't cache InterviewDataValue since
 * matrix is set from another session. Applet Linking now works.
 * 
 * Revision 1.8 2004/04/05 20:32:21 admin Modifying workflow to use applet for
 * linking
 * 
 * Revision 1.7 2004/04/05 01:16:44 admin Modifying to use new Applet Linking
 * Interface
 * 
 * Revision 1.6 2004/03/28 17:30:07 admin Display applet in complete screen Mark
 * interviews complete Only return names of complete interviews to client
 * 
 * Revision 1.5 2004/03/18 15:23:40 admin Recovers previous interview state
 * 
 * Revision 1.4 2004/02/15 14:59:02 admin Fixing Header Tags
 * 
 * Revision 1.3 2004/02/15 14:37:38 admin Displaying network graph on web pages
 * 
 * Revision 1.2 2004/02/10 16:31:24 admin Alter Prompt Completed
 * 
 * Revision 1.1 2004/02/07 04:33:26 admin First egoquestions page
 * 
 * Revision 1.3 2004/01/30 23:31:09 admin Using struts-layout
 * 
 * Revision 1.6 2004/01/23 13:36:07 admin Updating Libraries Allowing upload to
 * web server
 *  
 */