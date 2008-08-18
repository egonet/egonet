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
package com.endlessloopsoftware.egonet.web;

import java.util.Map;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;

import org.apache.struts.util.ModuleException;
import org.jboss.logging.Logger;

import com.endlessloopsoftware.egonet.InterviewPosition;
import com.endlessloopsoftware.egonet.interfaces.InterviewEJBLocal;
import com.endlessloopsoftware.egonet.interfaces.StudyEJBLocal;
import com.endlessloopsoftware.egonet.util.InterviewDataValue;
import com.endlessloopsoftware.egonet.util.StudyDataValue;

/**
 * @author admin
 *
 */
public class WebShared
{
   final static Logger        logger                       = Logger.getLogger("WebShared");

   /* Forwards */
   public final static String FORWARD_SUCCESS              = "Success";
   public final static String FORWARD                      = "Forward";
   public final static String FORWARD_ERROR                = "Error";
   public final static String FORWARD_TUTORIAL             = "Tutorial";
   public final static String FORWARD_EGO_QUESTIONS        = "Ego Questions";
   public final static String FORWARD_NEXT_ALTER           = "Next Alter";
   public final static String FORWARD_ALTER_PROMPT         = "Alter Prompt";
   public final static String FORWARD_ALTER_QUESTIONS      = "Alter Questions";
   public final static String FORWARD_ALTER_PAIR_QUESTIONS = "Alter Pair Questions";
   public final static String FORWARD_ALTER_PAIR_APPLET    = "Alter Pair Applet";
   public final static String FORWARD_PAIRS_COMPLETE       = "PairsComplete";
   public final static String FORWARD_POSITION             = "Position Forward";
   public final static String FORWARD_COMPLETE             = "Complete";
   public final static String FORWARD_CORRECTION           = "Correction";

   /* Application Objects */
   public final static String STUDY_ID_KEY                 = "study_id";
   public final static String STUDY_KEY                    = "study_key";
   public final static String STUDY_DV_KEY                 = "study_data_value_key";
   public final static String QUESTION_DV_MAP_KEY          = "interview_key";
   public final static String QUESTION_MAP_KEY             = "interview_key";
   public final static String TEMPLATE_KEY                 = "template_key";
   public final static String UI_TYPE_KEY                  = "ui_type";

   /* Session Objects */
   public final static String INTERVIEW_KEY                = "interview_key";
   public final static String INTERVIEW_DV_KEY             = "interview_data_value_key";
   public final static String POSITION_KEY                 = "position_key";
   public final static String MATRIX_KEY                   = "matrix_key";

   /* Applet Modes */
   public final static String VIEW_MODE                    = "View";
   public final static String LINK_MODE                    = "Link";
   public final static String CORRECT_MODE                 = "Correct";
   public final static String ALTER_QUESTION_MODE          = "AlterQuestion";
	
   
	/*************************
	 * Fetch Objects Stored in Session or Application Context
	 * 
	 */
	
	/*
	 *	Factory for returning a shared instance of the study
	 */
	public static StudyDataValue retrieveStudyDataValue(HttpServlet servlet)
	throws ModuleException
	{
		StudyDataValue study = (StudyDataValue) servlet.getServletContext().getAttribute(WebShared.STUDY_DV_KEY);
		
		if (study == null)
		{	
			throw new ModuleException("Study not found in application context");
		}

		return (study);
	}
	
	/*
    * Factory for returning a shared instance of the study
    */
   public static StudyEJBLocal retrieveStudy(HttpServlet servlet) 
      throws ModuleException
   {
      StudyEJBLocal study = (StudyEJBLocal) servlet.getServletContext().getAttribute(WebShared.STUDY_KEY);

      if (study == null) { throw new ModuleException("Study not found in application context"); }

      return (study);
   }
	
	/*
	 *	Factory for returning a shared instance of the study
	 */
	public static InterviewPosition[] retrieveTemplate(HttpServlet servlet)
	   throws ModuleException
	{
		InterviewPosition[] template = (InterviewPosition[]) servlet.getServletContext().getAttribute(WebShared.TEMPLATE_KEY);
		
		if (template == null)
		{	
			throw new ModuleException("Template not found in application context");
		}
		
		return (template);
	}
	
	/*
	 *	Factory for returning a shared instance of the study
	 */
	public static Map retrieveQuestionMap(HttpServlet servlet)
	throws ModuleException
	{
		Map questionMap = (Map) servlet.getServletContext().getAttribute(WebShared.QUESTION_MAP_KEY);
		
		if (questionMap == null)
		{	
			throw new ModuleException("QuestionMap not found in application context");
		}
		
		return (questionMap);
	}
	
	/*************************************
	 * Retrieve objects stored in Session Context
	 */
	/*
	 *	Factory for returning an individual instance of the interview position
	 */
	public static InterviewPosition retrieveInterviewPosition(HttpServletRequest request)
		throws ModuleException
	{
		InterviewPosition ip = (InterviewPosition) request.getSession().getAttribute(WebShared.POSITION_KEY);
		
		if (ip == null)
		{	
			throw new ModuleException("Interview Position not found in session context");
		}
		
		return (ip);
	}
	
	/*
	 *	Method for storing an individual instance of the interview position
	 */
	public static void storeInterviewPosition(HttpServletRequest request, InterviewPosition ip)
		throws ModuleException
	{
      logger.debug("Storing updated position => " + ip);
		request.getSession().setAttribute(WebShared.POSITION_KEY, ip);
	}
	
	/*
	 *	Method for storing an individual instance of the interview template
	 */
	public static void storeTemplate(HttpServlet servlet, InterviewPosition[] template)
	{
		servlet.getServletContext().setAttribute(WebShared.TEMPLATE_KEY, template);
	}
	
	/*
	 *	Factory for returning an individual instance of the interview data value
	 */
	public static InterviewDataValue retrieveInterviewDataValue(HttpServletRequest request)
		throws ModuleException
	{
		InterviewDataValue idv = retrieveInterview(request).getInterviewDataValue();
		
		if (idv == null)
		{	
			throw new ModuleException("Interview not found in session context");
		}
		
		return (idv);
	}
	
	/*
	 *	Factory for returning an individual instance of the interview position
	 */
//	public static void storeInterviewDataValue(HttpServletRequest request, InterviewDataValue interview)
//		throws ModuleException
//	{
//		request.getSession().setAttribute(WebShared.INTERVIEW_DV_KEY, interview);
//	}
	
   /*
    * Factory for returning an individual instance of the interview position
    */
   public static void setInterviewComplete(HttpServletRequest request)
      throws ModuleException
   {
      InterviewEJBLocal interview = retrieveInterview(request);
      interview.setComplete(Boolean.TRUE);
   }
   
	/*
	 *	Factory for returning an individual instance of the interview position
	 */
	public static InterviewEJBLocal retrieveInterview(HttpServletRequest request)
	   throws ModuleException
	{
		InterviewEJBLocal idv = (InterviewEJBLocal) request.getSession().getAttribute(WebShared.INTERVIEW_KEY);
		
		if (idv == null)
		{	
			throw new ModuleException("Interview not found in session context");
		}
		
		return (idv);
	}
	
	/*
	 *	Factory for returning an individual instance of the interview position
	 */
	public static void storeInterview(HttpServletRequest request, InterviewEJBLocal interview)
	   throws ModuleException
	{
		request.getSession().setAttribute(WebShared.INTERVIEW_KEY, interview);
	}
}
