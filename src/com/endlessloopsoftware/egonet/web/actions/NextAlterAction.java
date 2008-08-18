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

import javax.servlet.ServletException;
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
import com.endlessloopsoftware.egonet.interfaces.StudyEJBLocal;
import com.endlessloopsoftware.egonet.web.WebShared;

/**
 * @author admin
 * 
 * To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Generation - Code and Comments
 */
public final class NextAlterAction
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
      StudyEJBLocal        study;

      logger.debug(this.getClass().getName() + "::PERFORM");

      try
      {
         position = WebShared.retrieveInterviewPosition(request);
         study = getStudy();

         /* Update Interview if were down to 1 alter needed */
         if (study.getUIType().equals(Shared.PAIR_ELICITATION))
         {
             int altersNeeded = study.getNumAlters() - (position.getPrimaryAlter() + 1);
             if (position.getAlterPromptType().equals(Shared.LINK_PAIR) && (altersNeeded < 2))
             {
                position.setAlterPromptType(Shared.LINK_TO_NONE);
             }
         }
      }
      catch (ModuleException e)
      {
         e.printStackTrace();
         throw new ServletException(e.getMessage());
      }

      logger.debug("Current => " + position);

      if (study.getUIType().equals(Shared.TRADITIONAL_QUESTIONS))
      {
         position = getNextTraditionalQuestion(position, study);
      }
      else
      {
         position = getNextLinkingAppletQuestion(position, study);
      }

      /***
       * Save derived position
       */
      try
      {
         WebShared.storeInterviewPosition(request, position);
      }
      catch (ModuleException e)
      {
         e.printStackTrace();
         throw new ServletException(e.getMessage());
      }

      return (mapping.findForward(WebShared.FORWARD_POSITION));
   }

   /**
    * @param position
    * @param study
    * @param hasAlterQuestions
    * @param hasAlterPairQuestions
    * @param needMoreAlters
    * @return
    */
   private InterviewPosition getNextLinkingAppletQuestion(InterviewPosition position, StudyEJBLocal study)
   {
      boolean hasAlterQuestions     = (study.getQuestionOrder()[Shared.ALTER_QUESTION].length > 0);
      boolean hasAlterPairQuestions = (study.getQuestionOrder()[Shared.ALTER_PAIR_QUESTION].length > 0);
      boolean needMoreAlters        = (study.getNumAlters() > (position.getPrimaryAlter() + 1));

      logger.debug("hAQ = " + hasAlterQuestions + "; hAPQ = " + hasAlterPairQuestions + "; nMA = " + needMoreAlters);

      // Linking Applet Version
      switch (position.getQuestionType())
      {
         case Shared.EGO_QUESTION:
            {
               // Jump right to first Alter Name
               position = new InterviewPosition(Shared.ALTER_PROMPT, 
                                           Shared.getNextPromptType(position.getAlterPromptType(), study.getUIType()), 
                                           position.getGlobalPosition(), 0, position.getGlobalPageNumber() + 1);
               break;
            }

         case Shared.ALTER_PROMPT:
            {
               if (hasAlterPairQuestions)
               {
                  // Jump right to first Alter Pair Question, passes along
                  // prompt type
                  position = new InterviewPosition(Shared.ALTER_PAIR_QUESTION, position.getAlterPromptType(),
                                                   position.getGlobalPosition(), position.getPrimaryAlter(),
                                                   position.getGlobalPageNumber() + 1);
               }
               else if (needMoreAlters)
               {
                  // Get Next Alter Name
                  position = new InterviewPosition(Shared.ALTER_PROMPT,
                                                   Shared.getNextPromptType(position.getAlterPromptType(),
                                                                            study.getUIType()),
                                                   position.getGlobalPosition(), position.getPrimaryAlter() + 1,
                                                   position.getGlobalPageNumber() + 1);
               }
               else if (hasAlterQuestions)
               {
                  // Jump right to Beginning of Alter Questions
                  position = new InterviewPosition(Shared.ALTER_QUESTION, Shared.NOT_ALTER_PROMPT,
                                                   position.getGlobalPosition(), 0, position.getGlobalPageNumber() + 1);
               }
               else
               {
                  position = new InterviewPosition(Shared.COMPLETE);
               }

               break;
            }

         case Shared.ALTER_QUESTION:
            {
               if (needMoreAlters)
               {
                  // Get Next Alter Name
                  position = new InterviewPosition(Shared.ALTER_QUESTION, Shared.NOT_ALTER_PROMPT,
                                                   position.getGlobalPosition(), position.getPrimaryAlter() + 1,
                                                   position.getGlobalPageNumber() + 1);
               }
               else
               {
                  position = new InterviewPosition(Shared.COMPLETE);
               }

               break;
            }

         case Shared.ALTER_PAIR_QUESTION:
            {
               if (needMoreAlters)
               {
                  // Get Next Alter Name
                  position = new InterviewPosition(Shared.ALTER_PROMPT,
                                                   Shared.getNextPromptType(position.getAlterPromptType(),
                                                                            study.getUIType()),
                                                   position.getGlobalPosition(), position.getPrimaryAlter() + 1,
                                                   position.getGlobalPageNumber() + 1);
               }
               else
               {
                  // Jump right to Alter Questions
                  position = new InterviewPosition(Shared.CORRECTION, Shared.NOT_ALTER_PROMPT,
                                                   position.getGlobalPosition(), Shared.NO_ALTER, 
                                                   position.getGlobalPageNumber() + 1);
               }

               break;
            }
         
         case Shared.CORRECTION:
            {
               if (hasAlterQuestions)
               {
                  // Jump right to Alter Questions
                  position = new InterviewPosition(Shared.ALTER_QUESTION, Shared.NOT_ALTER_PROMPT,
                                                   position.getGlobalPosition(), 0,
                                                   position.getGlobalPageNumber() + 1);
               }
               else
               {
                  position = new InterviewPosition(Shared.COMPLETE);
               }
               
               break;
            }

         default:
            {
               position = new InterviewPosition(Shared.ERROR);
            }
      }
      
      return position;
   }

   /**
    * @param position
    * @param study
    * @param hasAlterQuestions
    * @param hasAlterPairQuestions
    * @param needMoreAlters
    * @return
    */
   private InterviewPosition getNextTraditionalQuestion(InterviewPosition position, StudyEJBLocal study)
   {
      boolean hasAlterQuestions = (study.getQuestionOrder()[Shared.ALTER_QUESTION].length > 0);
      boolean hasAlterPairQuestions = (study.getQuestionOrder()[Shared.ALTER_PAIR_QUESTION].length > 0);
      boolean needMoreAlters = (study.getNumAlters() > (position.getPrimaryAlter() + 1));

      logger.debug("hAQ = " + hasAlterQuestions + "; hAPQ = " + hasAlterPairQuestions + "; nMA = " + needMoreAlters);

      switch (position.getQuestionType())
      {
         case Shared.EGO_QUESTION:
            {
               // Jump right to first Alter Name
               position = new InterviewPosition(Shared.ALTER_PROMPT, Shared.LINK_TO_NONE, position.getGlobalPosition(),
                                                0, position.getGlobalPageNumber() + 1);
               break;
            }

         case Shared.ALTER_PROMPT:
            {
               if (hasAlterQuestions)
               {
                  // Jump right to Alter Questions
                  position = new InterviewPosition(Shared.ALTER_QUESTION, Shared.LINK_TO_NONE,
                                                   position.getGlobalPosition(), position.getPrimaryAlter(),
                                                   position.getGlobalPageNumber() + 1);
               }
               else if (hasAlterPairQuestions)
               {
                  // Jump right to first Alter Pair Question
                  position = new InterviewPosition(Shared.ALTER_PAIR_QUESTION, Shared.LINK_TO_NONE,
                                                   position.getGlobalPosition(), position.getPrimaryAlter(),
                                                   position.getGlobalPageNumber() + 1);
               }
               else if (needMoreAlters)
               {
                  // Get Next Alter Name
                  position = new InterviewPosition(Shared.ALTER_PROMPT, Shared.LINK_TO_NONE,
                                                   position.getGlobalPosition(), position.getPrimaryAlter() + 1,
                                                   position.getGlobalPageNumber() + 1);
               }
               else
               {
                  position = new InterviewPosition(Shared.COMPLETE);
               }

               break;
            }

         case Shared.ALTER_QUESTION:
            {
               // After asking an alter question, ask all appropriate alter
               // pair questions
               if ((position.getPrimaryAlter() > 0) && hasAlterPairQuestions)
               {
                  position = new InterviewPosition(Shared.ALTER_PAIR_QUESTION, Shared.LINK_TO_NONE,
                                                   position.getGlobalPosition(), position.getPrimaryAlter(),
                                                   position.getGlobalPageNumber() + 1);
               }
               else if (needMoreAlters)
               {
                  // Get Next Alter Name
                  position = new InterviewPosition(Shared.ALTER_PROMPT, Shared.LINK_TO_NONE,
                                                   position.getGlobalPosition(), position.getPrimaryAlter() + 1,
                                                   position.getGlobalPageNumber() + 1);
               }
               else
               {
                  position = new InterviewPosition(Shared.COMPLETE);
               }

               break;
            }

         case Shared.ALTER_PAIR_QUESTION:
            {
               if (study.getQuestionOrder()[Shared.ALTER_PAIR_QUESTION].length > (position.getTypePageNumber() + 1))
               {
                  position = new InterviewPosition(Shared.ALTER_PAIR_QUESTION, Shared.NOT_ALTER_PROMPT,
                                                   position.getPrimaryAlter(), null, -1,
                                                   position.getGlobalPageNumber() + 1,
                                                   position.getTypePageNumber() + 1, 0);
               }
               else if (needMoreAlters)
               {
                  // Get Next Alter Name
                  position = new InterviewPosition(Shared.ALTER_PROMPT, Shared.LINK_TO_NONE,
                                                   position.getGlobalPosition(), position.getPrimaryAlter() + 1,
                                                   position.getGlobalPageNumber() + 1);
               }
               else
               {
                  position = new InterviewPosition(Shared.COMPLETE);
               }

               break;
            }

         default:
            {
               position = new InterviewPosition(Shared.ERROR);
            }
      }
      return position;
   }
}

/**
 * $Log: NextAlterAction.java,v $
 * Revision 1.11  2004/05/17 00:05:24  admin
 * Node coloring in alter question screens
 *
 * Revision 1.10  2004/04/10 23:09:22  admin
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