package com.endlessloopsoftware.egonet.web.forms;

/**
 * <p>Title: Egocentric Networks Client Program</p>
 * <p>Description: Subject Interview Client</p>
 * <p>Copyright: Copyright (c) 2002 - 2004 </p>
 * <p>Company: Endless Loop Software</p>
 * @author Peter Schoaff
 *
 * $Id: AlterNameForm.java,v 1.12 2004/05/26 12:35:54 admin Exp $
 */
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.util.ModuleException;
import org.jboss.logging.Logger;

import com.endlessloopsoftware.egonet.AlterPair;
import com.endlessloopsoftware.egonet.InterviewPosition;
import com.endlessloopsoftware.egonet.Shared;
import com.endlessloopsoftware.egonet.Shared.AlterPromptType;
import com.endlessloopsoftware.egonet.interfaces.InterviewEJBLocal;
import com.endlessloopsoftware.egonet.interfaces.InterviewEJBPK;
import com.endlessloopsoftware.egonet.interfaces.QuestionEJBLocal;
import com.endlessloopsoftware.egonet.interfaces.StudyEJBLocal;
import com.endlessloopsoftware.egonet.util.AnswerDataValue;
import com.endlessloopsoftware.egonet.util.InterviewDataValue;
import com.endlessloopsoftware.egonet.web.WebShared;

public class AlterNameForm extends ELSValidatorForm
{
	Logger logger = Logger.getLogger(this.getClass().getName());
   
   private static final String[] NO_ALTER_STRINGS = new String[0];

   private String            _nameOne;
   private String            _nameTwo;
   private AnswerDataValue   _answer;
   private java.lang.Integer _position;
   private java.lang.Long    _interviewId;
   private AlterPromptType   _promptType;

   private boolean           _nameTwoRequired = false;
	
	public AlterNameForm()
	{
		logger.debug("instantiate");
		
	}
	
	/**
	 * @return Returns the firstName.
	 */
	public String getNameOne()
	{
		return _nameOne;
	}
	/**
	 * @param firstName The firstName to set.
	 */
	public void setNameOne(String firstName)
	{
		logger.debug("Setting First Name");
		_nameOne = firstName;
	}
		
   /**
    * @return Returns the firstName.
    */
   public String getNameTwo()
   {
      return _nameTwo;
   }
   /**
    * @param firstName The firstName to set.
    */
   public void setNameTwo(String name)
   {
      logger.debug("Setting First Name");
      _nameTwo = name;
   }
      
	/**
	 * @return Returns the _answer.
	 */
	public AnswerDataValue getAnswer() {
		return _answer;
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
   public AlterPromptType getPromptType()
   {
      return _promptType;
   }

	/* (non-Javadoc)
	 * @see org.apache.struts.action.ActionForm#reset(org.apache.struts.action.ActionMapping, javax.servlet.http.HttpServletRequest)
	 */
	public void reset(ActionMapping mapping, HttpServletRequest request)
	{
		logger.debug(this.getClass().getName() + "::reset");

		try
		{
			StudyEJBLocal 			study 				= WebShared.retrieveStudy(servlet);
			Map 						questionMap		= WebShared.retrieveQuestionMap(servlet);
			InterviewPosition		position 			= WebShared.retrieveInterviewPosition(request);
         InterviewEJBLocal    interview      = WebShared.retrieveInterview(request);
         Long[][]             orders         = study.getQuestionOrder();
         String[]             alterStrings   = NO_ALTER_STRINGS;
			
         QuestionEJBLocal     question;
         
         _promptType = position.getAlterPromptType();
         if (_promptType.equals(Shared.LINK_TO_NEXT))
         {
            question = (QuestionEJBLocal) questionMap.get(orders[Shared.ALTER_PROMPT][0]);
            _nameTwoRequired = false;
         }
         else if (_promptType.equals(Shared.LINK_TO_PRIOR))
         {
            logger.debug("Link To Prior");
            alterStrings = new String[] {interview.getAlters()[position.getPrimaryAlter()], 
                                         interview.getAlters()[position.getPrimaryAlter() - 1]};

            question = (QuestionEJBLocal) questionMap.get(orders[Shared.ALTER_PROMPT][1]);
            _nameTwoRequired = false;
         }
         else if (_promptType.equals(Shared.LINK_PAIR))
         {
            question = (QuestionEJBLocal) questionMap.get(orders[Shared.ALTER_PROMPT][0]);
            _nameTwoRequired = true;
         }
         else
         {
            // This is the generic name generator. It will always be the last question in the array
            int index = orders[Shared.ALTER_PROMPT].length - 1;
            question = (QuestionEJBLocal) questionMap.get(orders[Shared.ALTER_PROMPT][index]);
            _nameTwoRequired = false;
         }
         
         {
            InterviewPosition[]  template = WebShared.retrieveTemplate(servlet);
            logger.debug("Question Id " + question.getId());
            logger.debug("Question " + questionMap.get(template[position.getGlobalPosition()].getQuestionId()));
            
            if (alterStrings.length > 0)
               logger.debug("Alter[0] = " + alterStrings[0]);
            if (alterStrings.length > 1)
               logger.debug("Alter[1] = " + alterStrings[1]);
         }
         
			_answer = new AnswerDataValue(null, new AlterPair(position.getPrimaryAlter()), Shared.LINK_TO_NONE,
                                       alterStrings, false, -1, -1, "", false, study, question);
			
			_position 		= new Integer(position.getPrimaryAlter() - 1);
			_interviewId 	= ((InterviewEJBPK) WebShared.retrieveInterview(request).getPrimaryKey()).getId();
		} 
		catch (ModuleException e)
		{
			e.printStackTrace();
		}
	}
	
	/* (non-Javadoc)
	 * @see org.apache.struts.action.ActionForm#validate(org.apache.struts.action.ActionMapping, javax.servlet.http.HttpServletRequest)
	 */
	public ActionErrors validate(ActionMapping arg0, HttpServletRequest request)
	{
      ActionErrors   errors = new ActionErrors();
      String         action = request.getParameter("submit");
      
      logger.debug("Validating Form with " + action);
      
      if ("submit".equals(action))
      {
         InterviewDataValue interview;
         InterviewPosition position;

         try
         {
            interview = WebShared.retrieveInterviewDataValue(request);
            position = WebShared.retrieveInterviewPosition(request);
         }
         catch (ModuleException e)
         {
            e.printStackTrace();
            errors.add(ActionErrors.GLOBAL_ERROR, new ActionError("error.resource"));
            return errors;
         }

         if ((getNameOne() == null) || "".equals(getNameOne().trim()))
         {
            errors.add("nameOne", new ActionError("error.answer.required"));
         }

         if (_nameTwoRequired && ((getNameTwo() == null) || "".equals(getNameTwo().trim())))
         {
            errors.add("nameTwo", new ActionError("error.answer.required"));
         }

         if (position.getPrimaryAlter() > 0)
         {
            String[] alters = interview.getAlters();
            for (int i = 0; i < position.getPrimaryAlter(); ++i)
            {
               if (alters[i].equals(getNameOne()))
               {
                  errors.add("nameOne", new ActionError("error.alter.duplicate", getNameOne()));
                  break;
               }
               else if (alters[i].equals(getNameTwo()))
               {
                  errors.add("nameTwo", new ActionError("error.alter.duplicate", getNameTwo()));
                  break;
               }
            }
         }
      }
		
		return errors;
	}
}

/**
 * $Log: AlterNameForm.java,v $
 * Revision 1.12  2004/05/26 12:35:54  admin
 * Adding tutorial
 *
 * Revision 1.11  2004/04/10 23:09:22  admin
 * Implemented Three Step Elicitation
 *   Added Position Forward Action
 *   Store current prompt type as part of answer and interview position
 *
 * Using custom version of FR Layout to try to keep labels from overlapping
 * borders
 *
 * Revision 1.10  2004/04/07 00:29:05  admin
 * fixing error checking workflows
 *
 * Revision 1.9  2004/04/06 23:09:43  admin
 * Fixing workflows, error checking for AlterNames
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
