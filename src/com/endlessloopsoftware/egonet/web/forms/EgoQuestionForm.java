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
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.util.ModuleException;
import org.jboss.logging.Logger;

import com.endlessloopsoftware.egonet.InterviewPosition;
import com.endlessloopsoftware.egonet.Shared;
import com.endlessloopsoftware.egonet.interfaces.QuestionEJBLocal;
import com.endlessloopsoftware.egonet.interfaces.StudyEJBLocal;
import com.endlessloopsoftware.egonet.util.AnswerDataValue;
import com.endlessloopsoftware.egonet.util.StudyDataValue;
import com.endlessloopsoftware.egonet.web.WebShared;

/**
 * @author admin
 *
 * To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public class EgoQuestionForm extends ELSValidatorForm
{
	private final Logger logger = Logger.getLogger(this.getClass().getName());
	
	private StudyDataValue _study;
	private Vector				_answers;
	
	public EgoQuestionForm()
	{
		logger.debug("instantiate");
		
	}
	
	/**
	 * @return Returns the study.
	 */
	public StudyDataValue getStudy()
	{
		return _study;
	}
	
	/**
	 * @return Returns the answers.
	 */
	public Vector getAnswers() 
	{
		return _answers;
	}

	/**
	 * @return Returns the answers.
	 */
	public void setAnswers(Vector answers) 
	{
		_answers = answers;
	}

	/* (non-Javadoc)
	 * @see org.apache.struts.action.ActionForm#reset(org.apache.struts.action.ActionMapping, javax.servlet.http.HttpServletRequest)
	 */
	public void reset(ActionMapping mapping, HttpServletRequest request)
	{
		logger.debug("WelcomeForm::reset");
		
		try
		{
			StudyEJBLocal 			study 		= WebShared.retrieveStudy(servlet);
			InterviewPosition[] 	template 	= WebShared.retrieveTemplate(servlet);
			Map 						questionMap	= WebShared.retrieveQuestionMap(servlet);
			InterviewPosition		position 	= WebShared.retrieveInterviewPosition(request);
			
			_answers = new Vector();
			
			for (int i = 0; (i < template.length) && (template[i].getGlobalPageNumber() == position.getGlobalPageNumber()); ++i)
			{	
				try 
				{
					AnswerDataValue answer = new AnswerDataValue("", Shared.NO_ALTERS, Shared.NOT_ALTER_PROMPT, new String[0],
                                                            false, 1, -1, "", false, study, 
                                                            (QuestionEJBLocal) questionMap.get(template[i].getQuestionId()));
										
					_answers.add(answer);
				} 
				catch (RuntimeException e1) 
				{
					System.err.println("Index " + i);
					System.err.println("Question Id " + template[i].getQuestionId());
					System.err.println("Question " + questionMap.get(template[i].getQuestionId()));
					e1.printStackTrace();
				}
			}
		} 
		catch (ModuleException e)
		{
			e.printStackTrace();
		}
	}
	
	/* (non-Javadoc)
	 * @see org.apache.struts.action.ActionForm#validate(org.apache.struts.action.ActionMapping, javax.servlet.http.HttpServletRequest)
	 */
	public ActionErrors validate(ActionMapping arg0, HttpServletRequest arg1)
	{
		logger.debug("Validating Form");
		ActionErrors errors = new ActionErrors();

		for (int i = 0; i < _answers.size(); ++i)
		{
			AnswerDataValue	answer 		   = (AnswerDataValue) _answers.get(i);
			int 					answerType	= answer.getQuestion().getAnswerType();
			String 				text			= answer.getAnswerString();
			
			// Head off degenerate case
			if (text == null)
			{
				errors.add("answers[" + i + "].answerString", new ActionError("error.answer.required"));
			}
			else
			{	
				// Trim return value
				text = text.trim();
				answer.setAnswerString(text);
				
				switch (answerType)
				{
					case Shared.TEXT:
					{
						if ("".equals(text))
						{
							errors.add("answers[" + i + "].answerString", new ActionError("error.answer.required"));
						}
						break;
					}
					
					case Shared.NUMERICAL:
					{
						if (("".equals(text)) || (Shared.toInt(text) == -1))
						{
							errors.add("answers[" + i + "].answerString", new ActionError("error.answer.numeric"));
						}
						break;
					}
				}
			}
		}
		
		return errors;
	}
}

/**
 * $Log: EgoQuestionForm.java,v $
 * Revision 1.12  2004/04/10 23:09:22  admin
 * Implemented Three Step Elicitation
 *   Added Position Forward Action
 *   Store current prompt type as part of answer and interview position
 *
 * Using custom version of FR Layout to try to keep labels from overlapping
 * borders
 *
 * Revision 1.11  2004/04/08 15:03:06  admin
 * Changing some primary keys to UID strings so as I was getting duplicate keys
 * on the Survey Center Server
 *
 * Created Configuration Bean to hold various info. Right now it stores the
 * current active survey.
 *
 * Revision 1.10  2004/04/06 23:09:43  admin
 * Fixing workflows, error checking for AlterNames
 *
 * Revision 1.9  2004/04/05 01:16:44  admin
 * Modifying to use new Applet Linking Interface
 *
 * Revision 1.8  2004/03/18 15:23:40  admin
 * Recovers previous interview state
 *
 * Revision 1.7  2004/03/12 18:05:28  admin
 * Applet now works under Windows IE.
 * Fixed layout issues related to struts-layout converting spaces to nbsp
 * Using Servlet for applet/server communications
 *
 * Revision 1.6  2004/02/15 14:59:02  admin
 * Fixing Header Tags
 *
 * Revision 1.5  2004/02/15 14:44:15  admin
 * fixing headers
 *
 * Revision 1.4  2004/02/15 14:41:56  admin
 * Answer Data Value taking a couple of new parameters to aid display of
 * Alter Names
 *
 * Revision 1.3  2004/02/10 16:31:24  admin
 * Alter Prompt Completed
 *
 * Revision 1.2  2004/02/08 13:40:10  admin
 * Ego Questions Implemented.
 * Full workflow path complete in struts-config.xml
 * Took control of DataValue Objects from XDoclet
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
