/**
 * <p>Title: Egocentric Networks Client Program</p>
 * <p>Base Class for all Struts Actions in System</p>
 * <p>Copyright: Copyright (c) 2002 - 2004 </p>
 * <p>Company: Endless Loop Software</p>
 * @author Peter Schoaff
 *
 * $Id: ELSAction.java,v 1.10 2004/04/10 23:09:22 admin Exp $
 */
package com.endlessloopsoftware.egonet.web.actions;

import java.util.HashMap;
import java.util.Map;

import javax.ejb.CreateException;
import javax.ejb.FinderException;
import javax.naming.NamingException;

import org.apache.struts.action.Action;
import org.jboss.logging.Logger;

import com.endlessloopsoftware.egonet.interfaces.ConfigurationSBLocal;
import com.endlessloopsoftware.egonet.interfaces.ConfigurationSBUtil;
import com.endlessloopsoftware.egonet.interfaces.QuestionEJBPK;
import com.endlessloopsoftware.egonet.interfaces.QuestionSBLocal;
import com.endlessloopsoftware.egonet.interfaces.QuestionSBLocalHome;
import com.endlessloopsoftware.egonet.interfaces.QuestionSBUtil;
import com.endlessloopsoftware.egonet.interfaces.StudyEJBLocal;
import com.endlessloopsoftware.egonet.util.QuestionDataValue;
import com.endlessloopsoftware.egonet.util.StudyDataValue;
import com.endlessloopsoftware.egonet.web.WebShared;

/**
 * <p>Title: Egocentric Networks Client Program</p>
 * <p>Description: Subject Interview Client</p>
 * <p>Copyright: Copyright (c) 2002 - 2004 </p>
 * <p>Company: Endless Loop Software</p>
 * @author Peter Schoaff
 *
 * $Id: ELSAction.java,v 1.10 2004/04/10 23:09:22 admin Exp $
 */

/**
 * First Action in EgoNet Interview. 
 * Primary purpose is to fetch the study and store it in the application context.
 */
public abstract class ELSAction extends Action
{
   private final Logger logger  = Logger.getLogger(this.getClass());
   
	/*
	 *	Factory for returning a shared instance of the study
	 */
	StudyDataValue getStudyDataValue()
	{
		StudyDataValue studyDataValue = (StudyDataValue) servlet.getServletContext().getAttribute(WebShared.STUDY_DV_KEY);
		
		if (studyDataValue == null)
		{	
			try
			{
				loadStudy();
				studyDataValue = getStudyDataValue();
			} 
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		
		return (studyDataValue);
	}
	
	/*
	 *	Factory for returning a shared instance of the study
	 */
	StudyEJBLocal getStudy()
	{
		StudyEJBLocal study = (StudyEJBLocal) servlet.getServletContext().getAttribute(WebShared.STUDY_KEY);
		
		if (study == null)
		{	
			try
			{
				loadStudy();
				study = getStudy();
			} 
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		
		return (study);
	}
	
	/**
	 * @return
	 * @throws NamingException
	 * @throws CreateException
	 */
	private void loadStudy() 
		throws FinderException, NamingException, CreateException 
	{
		logger.debug("loading Study");
      
      ConfigurationSBLocal configuration = ConfigurationSBUtil.getLocalHome().create();
      StudyEJBLocal study = configuration.getActiveStudy();
      StudyDataValue studyDataValue = study.getStudyDataValue();
      
      logger.debug("Found Study " + study);
            
      servlet.getServletContext().setAttribute(WebShared.UI_TYPE_KEY, studyDataValue.getUIType());
		servlet.getServletContext().setAttribute(WebShared.STUDY_DV_KEY, studyDataValue);
		servlet.getServletContext().setAttribute(WebShared.STUDY_KEY, study);
		
		servlet.getServletContext().setAttribute(WebShared.QUESTION_DV_MAP_KEY, getQuestionDataValueMap(studyDataValue));
		servlet.getServletContext().setAttribute(WebShared.QUESTION_MAP_KEY, getQuestionMap(studyDataValue));
	}

	public Map getQuestionDataValueMap(StudyDataValue study)
	{
		logger.debug("Filling QuestionDataValue Map");
		Map rmap = new HashMap();
		
		if (study != null)
		{
			QuestionDataValue[] questions = study.getQuestionDataValues();
			
			for (int i = 0; i < questions.length; i++)
			{
				rmap.put(questions[i].getId(), questions[i]);
			}
		}
		
		return rmap;
	}

	public Map getQuestionMap(StudyDataValue study)
		throws CreateException, NamingException
	{
		logger.debug("Filling Question Map");
		Map rmap = new HashMap();
		
		/**
		 * Prepare QuestionSBto lookup questions
		 */
		QuestionSBLocalHome questionSBHome = QuestionSBUtil.getLocalHome();
		QuestionSBLocal questionSB = questionSBHome.create();
		
		if (study != null)
		{
			QuestionDataValue[] questions = study.getQuestionDataValues();
			
			for (int i = 0; i < questions.length; i++)
			{
				try 
				{
					logger.debug("Inserting Question " + questions[i].getId());
					rmap.put(questions[i].getId(), questionSB.findByPrimaryKey(new QuestionEJBPK(questions[i].getId(), study.getId())));
				} 
				catch (FinderException e) 
				{
					System.err.println("Can't find question with Id: " + questions[i].getId());
					e.printStackTrace();
				} 
			}
		}
		
		return rmap;
	}
}


/**
 * $Log: ELSAction.java,v $
 * Revision 1.10  2004/04/10 23:09:22  admin
 * Implemented Three Step Elicitation
 *   Added Position Forward Action
 *   Store current prompt type as part of answer and interview position
 *
 * Using custom version of FR Layout to try to keep labels from overlapping
 * borders
 *
 * Revision 1.9  2004/04/08 15:03:06  admin
 * Changing some primary keys to UID strings so as I was getting duplicate keys
 * on the Survey Center Server
 *
 * Created Configuration Bean to hold various info. Right now it stores the
 * current active survey.
 *
 * Revision 1.8  2004/04/05 01:16:43  admin
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
 * Revision 1.5  2004/02/15 14:59:02  admin
 * Fixing Header Tags
 *
 * Revision 1.4  2004/02/15 14:37:38  admin
 * Displaying network graph on web pages
 *
 * Revision 1.3  2004/02/10 16:31:23  admin
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
 */