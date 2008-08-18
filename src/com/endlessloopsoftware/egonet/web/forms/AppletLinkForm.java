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
package com.endlessloopsoftware.egonet.web.forms;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.util.ModuleException;
import org.jboss.logging.Logger;

import com.endlessloopsoftware.egonet.AlterPair;
import com.endlessloopsoftware.egonet.InterviewPosition;
import com.endlessloopsoftware.egonet.Shared;
import com.endlessloopsoftware.egonet.interfaces.InterviewEJBLocal;
import com.endlessloopsoftware.egonet.interfaces.InterviewEJBPK;
import com.endlessloopsoftware.egonet.interfaces.QuestionEJBLocal;
import com.endlessloopsoftware.egonet.interfaces.StudyEJBLocal;
import com.endlessloopsoftware.egonet.util.AnswerDataValue;
import com.endlessloopsoftware.egonet.web.WebShared;

public class AppletLinkForm extends ActionForm
{
	Logger logger = Logger.getLogger(this.getClass().getName());
	
	private AnswerDataValue   _answer;
   private java.lang.Integer _position;
   private java.lang.Long    _interviewId;
   private Boolean           _pairsComplete;
   
	public AppletLinkForm()
	{
		logger.debug("instantiate");
		
	}
	
	/**
	 * @return Returns the _answer.
	 */
	public AnswerDataValue getAnswer() {
		return _answer;
	}

	/* (non-Javadoc)
	 * @see org.apache.struts.action.ActionForm#reset(org.apache.struts.action.ActionMapping, javax.servlet.http.HttpServletRequest)
	 */
	public void reset(ActionMapping mapping, HttpServletRequest request)
	{
		logger.debug(this.getClass().getName() + "::reset");

		try
		{
			StudyEJBLocal     study          = WebShared.retrieveStudy(servlet);
         InterviewEJBLocal interview      = WebShared.retrieveInterview(request);
			Map               questionMap    = WebShared.retrieveQuestionMap(servlet);
			InterviewPosition position       = WebShared.retrieveInterviewPosition(request);
         Long[][]          orders         = study.getQuestionOrder();
			
         QuestionEJBLocal  question       = (QuestionEJBLocal) questionMap.get(orders[Shared.ALTER_PAIR_QUESTION][0]);
         AlterPair         alters         = new AlterPair(position.getPrimaryAlter());
         
         int               primary        = alters.getPrimaryAlter();
         String[]          alterStrings   = new String[0];
         
         if (primary != Shared.NO_ALTER)
         {
            if (position.getAlterPromptType() == Shared.LINK_TO_PRIOR)
            {
               alterStrings = new String[] {interview.getAlters()[alters.getPrimaryAlter()], 
                                            interview.getAlters()[alters.getPrimaryAlter() - 1]};
            }
            else
            {
               alterStrings = new String[] {interview.getAlters()[alters.getPrimaryAlter()]};
            }
         }
        

         _answer = new AnswerDataValue(null, alters, Shared.LINK_TO_NONE, alterStrings, false, -1, -1, "", false,
                                       study, question);
			_position 		= new Integer(position.getPrimaryAlter());
			_interviewId 	= ((InterviewEJBPK) WebShared.retrieveInterview(request).getPrimaryKey()).getId();
		} 
		catch (ModuleException e)
		{
			e.printStackTrace();
		}
	}
	
	/**
	 * @return Returns the _interviewId.
	 */
	public Long getInterviewId()
	{
		return _interviewId;
	}

   /**
	 * @return Returns the _position.
	 */
	public Integer getPosition()
	{
      logger.debug("Get Position");
		return _position;
	}
   
   /**
    * @return Returns the _position.
    */
   public Boolean getPairsComplete()
   {
      return _pairsComplete;
   }
}

/**
 * $Log: AppletLinkForm.java,v $
 * Revision 1.4  2004/05/17 00:05:24  admin
 * Node coloring in alter question screens
 *
 * Revision 1.3  2004/04/10 23:09:22  admin
 * Implemented Three Step Elicitation
 *   Added Position Forward Action
 *   Store current prompt type as part of answer and interview position
 *
 * Using custom version of FR Layout to try to keep labels from overlapping
 * borders
 *
 * Revision 1.2  2004/04/06 14:42:14  admin
 * Completed Applet Linking
 *
 * Revision 1.1  2004/04/05 20:32:21  admin
 * Modifying workflow to use applet for linking
 *
 * Revision 1.8  2004/04/05 01:16:44  admin
 * Modifying to use new Applet Linking Interface
 *
 * Revision 1.7  2004/03/28 17:30:07  admin
 * Display applet in complete screen
 * Mark interviews complete
 * Only return names of complete interviews to client
 *
 * Revision 1.6  2004/03/18 15:23:40  admin
 * Recovers previous interview state
 *
 * Revision 1.5  2004/03/12 18:05:28  admin
 * Applet now works under Windows IE.
 * Fixed layout issues related to struts-layout converting spaces to nbsp
 * Using Servlet for applet/server communications
 *
 * Revision 1.4  2004/02/18 14:22:20  admin
 * reworking directory structure
 *
 * Revision 1.3  2004/02/15 14:59:02  admin
 * Fixing Header Tags
 *
 * Revision 1.2  2004/02/15 14:37:39  admin
 * Displaying network graph on web pages
 *
 * Revision 1.1  2004/02/10 16:31:24  admin
 * Alter Prompt Completed
 *
 * Revision 1.1  2004/02/07 04:33:26  admin
 * First egoquestions page
 *
 * Revision 1.2  2004/01/30 23:31:09  admin
 * Using struts-layout
 *
 * Revision 1.6  2004/01/23 13:36:07  admin
 * Updating Libraries
 * Allowing upload to web server
 *
 */
