/**
 * <p>Title: Egocentric Networks Client Program</p>
 * <p>Description: Subject Interview Client</p>
 * <p>Copyright: Copyright (c) 2002 - 2004 </p>
 * <p>Company: Endless Loop Software</p>
 * @author Peter Schoaff
 *
 * $Id: PositionForward.java,v 1.2 2004/05/17 00:05:24 admin Exp $
 */
package com.endlessloopsoftware.egonet.web.actions;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.util.ModuleException;
import org.jboss.logging.Logger;

import com.endlessloopsoftware.egonet.InterviewPosition;
import com.endlessloopsoftware.egonet.Shared;
import com.endlessloopsoftware.egonet.interfaces.InterviewSBLocal;
import com.endlessloopsoftware.egonet.web.WebShared;

/**
 * @author admin
 * 
 * To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Generation - Code and Comments
 */
public final class PositionForward
   extends ELSAction
{
   public final    Logger           logger = Logger.getLogger(this.getClass());
                   InterviewSBLocal _interviewSB;

   /*
    * (non-Javadoc)
    * 
    * @see org.apache.struts.action.Action#perform(org.apache.struts.action.ActionMapping,
    *         org.apache.struts.action.ActionForm,
    *         javax.servlet.http.HttpServletRequest,
    *         javax.servlet.http.HttpServletResponse)
    */
   public ActionForward perform(ActionMapping mapping, ActionForm pform, HttpServletRequest request,
                                HttpServletResponse response) throws IOException, ServletException
   {
      InterviewPosition    position;
      ActionForward        forward = null;
      int                  start;

      logger.debug(this.getClass().getName() + "::PERFORM");

      try
      {
         position = WebShared.retrieveInterviewPosition(request);
         start    = position.getGlobalPosition();
      }
      catch (ModuleException e)
      {
         e.printStackTrace();
         throw new ServletException(e.getMessage());
      }

      logger.debug("Proposed => " + position);

      try
      {
         position = fillGlobalPosition(servlet, position, start);
      }
      catch (ModuleException e1)
      {
         e1.printStackTrace();
         throw new ServletException(e1.getMessage());
      }

      logger.debug("New => " + position);
      try
      {
         WebShared.storeInterviewPosition(request, position);
      }
      catch (ModuleException e)
      {
         e.printStackTrace();
         throw new ServletException(e.getMessage());
      }

      /*************************************************************************
       * Get Forward
       */
      switch (position.getQuestionType())
      {
         case Shared.COMPLETE:
            forward = mapping.findForward(WebShared.FORWARD_COMPLETE);
            break;

         case Shared.CORRECTION:
            forward = mapping.findForward(WebShared.FORWARD_CORRECTION);
            break;

         case Shared.EGO_QUESTION:
            forward = mapping.findForward(WebShared.FORWARD_EGO_QUESTIONS);
            break;

         case Shared.ALTER_PROMPT:
            forward = mapping.findForward(WebShared.FORWARD_ALTER_PROMPT);
            break;

         case Shared.ALTER_QUESTION:
            forward = mapping.findForward(WebShared.FORWARD_ALTER_QUESTIONS);
            break;

         case Shared.ALTER_PAIR_QUESTION:
            {
               if (getStudy().isAppletUI())
                  forward = mapping.findForward(WebShared.FORWARD_ALTER_PAIR_APPLET);
               else
                  forward = mapping.findForward(WebShared.FORWARD_ALTER_PAIR_QUESTIONS);
            }
            break;

         default:
            forward = mapping.findForward(WebShared.FORWARD_ERROR);
            break;
      }

      logger.debug(forward);
      logger.debug(mapping);
      logger.debug(mapping.findForward(WebShared.FORWARD_COMPLETE));
      if (forward.equals(mapping.findForward(WebShared.FORWARD_COMPLETE)))
      {
         try
         {
            WebShared.setInterviewComplete(request);
         }
         catch (ModuleException e)
         {
            e.printStackTrace();
            throw new ServletException(e.getMessage());
         }
      }

      return (forward);
   }

   /****************************************************************************
    * Compares proposed incomplete position against template. Fills in extra
    * fields
    * 
    * @param servlet
    *           For accessing template in servlet context
    * @param position
    *           Proposed Position
    * @param start
    *           Where to start in template (no need to test past questions)
    * @return Completed position structure from template
    * @throws ModuleException
    */
   public InterviewPosition fillGlobalPosition(HttpServlet servlet, InterviewPosition position, int start)
      throws ModuleException
   {
      // Handle special cases
      if ((position.getQuestionType() == Shared.COMPLETE) || (position.getQuestionType() == Shared.ERROR))
         return position;

      // Search template
      InterviewPosition[] template = WebShared.retrieveTemplate(servlet);
      InterviewPosition rval = null;

      for (int i = start; i < template.length; ++i)
      {
         							logger.debug("Checking => " + template[i]);
         if ((template[i].getGlobalPageNumber()    == position.getGlobalPageNumber())
             && (template[i].getQuestionType()     == position.getQuestionType())
             && (template[i].getPrimaryAlter()     == position.getPrimaryAlter())
             && (template[i].getTypePageNumber()   == position.getTypePageNumber()))
         {
            rval = template[i];
            rval.setAlterPromptType(position.getAlterPromptType());
            break;
         }
      }

      if (rval == null) { throw new ModuleException("Invalid Interview Position"); }

      return rval;
   }
}

/**
 * $Log: PositionForward.java,v $
 * Revision 1.2  2004/05/17 00:05:24  admin
 * Node coloring in alter question screens
 *
 * Revision 1.1  2004/04/10 23:09:22  admin
 * Implemented Three Step Elicitation
 *   Added Position Forward Action
 *   Store current prompt type as part of answer and interview position
 *
 * Using custom version of FR Layout to try to keep labels from overlapping
 * borders
 * Revision 1.9 2004/04/06 23:09:43 admin Fixing
 * workflows, error checking for AlterNames
 * 
 * Revision 1.8 2004/04/05 21:23:58 admin Can't cache InterviewDataValue since
 * matrix is set from another session. Applet Linking now works.
 * 
 * Revision 1.7 2004/04/05 20:32:21 admin Modifying workflow to use applet for
 * linking
 * 
 * Revision 1.6 2004/04/05 01:16:44 admin Modifying to use new Applet Linking
 * Interface
 * 
 * Revision 1.5 2004/03/28 17:30:07 admin Display applet in complete screen Mark
 * interviews complete Only return names of complete interviews to client
 * 
 * Revision 1.4 2004/03/18 15:23:40 admin Recovers previous interview state
 * Revision 1.3 2004/02/15 14:59:02 admin Fixing Header Tags
 * 
 * Revision 1.2 2004/02/15 14:37:39 admin Displaying network graph on web pages
 * 
 * Revision 1.1 2004/02/10 16:31:24 admin Alter Prompt Completed
 * 
 * Revision 1.2 2004/02/08 13:40:09 admin Ego Questions Implemented. Full
 * workflow path complete in struts-config.xml Took control of DataValue Objects
 * from XDoclet
 * 
 * Revision 1.1 2004/02/07 04:33:26 admin First egoquestions page
 * 
 * Revision 1.3 2004/01/30 23:31:09 admin Using struts-layout
 * 
 * Revision 1.6 2004/01/23 13:36:07 admin Updating Libraries Allowing upload to
 * web server
 *  
 */