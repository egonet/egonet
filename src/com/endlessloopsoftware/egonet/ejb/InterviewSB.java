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
package com.endlessloopsoftware.egonet.ejb;
import java.rmi.RemoteException;

import javax.ejb.CreateException;
import javax.ejb.FinderException;
import javax.ejb.RemoveException;
import javax.naming.NamingException;

import org.jboss.logging.Logger;

import com.endlessloopsoftware.egonet.interfaces.*;
import com.endlessloopsoftware.egonet.util.InterviewDataValue;


/**
 * Session facade for InterviewEJB. 
 * @copyright 2004 Endless Loop Software Inc.
 * 
 *	@ejb.bean 
 * 		name               = "InterviewSB" 
 * 		type               = "Stateless" 
 * 		view-type          = "both"
 * 		jndi-name	          = "ejb/InterviewSB" 
 * 		local-jndi-name	    = "InterviewSBLocal"
 * 
 *	@ejb.ejb-ref 
 *		ejb-name           = "InterviewEJB" 
 *		view-type          = "local" 
 *
 *	@ejb.util
 * 		generate           = "physical" 
 * 
 * 
 */
public class InterviewSB extends EgoSB
{
	private final transient Logger logger = Logger.getLogger(this.getClass());
	
	private InterviewEJBLocalHome getLocalHome()
				throws javax.naming.NamingException
	{
		return InterviewEJBUtil.getLocalHome();
	}

	/**
	 * @ejb.interface-method
    *    view-type = "local"
	 *  
	 * @param study		study object to match
	 * @param firstName	part of name to match
	 * @param lastName	part of name to match
	 * @param create		if interview not found, should I create it.
	 */
	public InterviewEJBLocal findUserInterview(StudyEJBLocal study, String firstName, String lastName, boolean create)
		throws CreateException, NamingException, FinderException
	{
		InterviewEJBLocal selected;
		
		try
		{
			selected = getLocalHome().findByStudyAndEgo(study, firstName, lastName);
			logger.debug("Found Interview");
		} 
		catch (FinderException e)
		{
			if (create)
			{
				// Create a new Interview
				InterviewDataValue data = new InterviewDataValue();
				data.setFirstName(firstName);
				data.setLastName(lastName);
				data.setAlters(new String[study.getNumAlters()]);
            data.setAdjacencyMatrix(new int[study.getNumAlters()][study.getNumAlters()]);
				data.setComplete(new Boolean(false));
	
				logger.debug("Creating Interview: " + data);
				selected = getLocalHome().create(data);
				selected.setStudy(study);
			}
			else
			{
				throw e;
			}
		} 
		
		//InterviewDataValue retval = selected.getInterviewDataValue();
		return selected;
	}
	
   /**
    * @ejb.interface-method 
    *    view-type = "remote"
    */
   public InterviewDataValue fetchUserInterviewData(String studyName, String firstName, String lastName, String password)
      throws FinderException, NamingException, CreateException, RemoteException
   {
      ConfigurationSBLocal configurationSession = ConfigurationSBUtil.getLocalHome().create();
      
      if (configurationSession.checkPassword(password))
      {
         return fetchUserInterviewData(studyName, firstName, lastName);
      }
      else
      {
         throw new RemoteException("Invalid Password");
      }
   }

	/***********
	 * Finds interview matching study and name, returns data object
	 * 
	 * @param studyName
	 * @param firstName
	 * @param lastName
	 * @return
	 *
	 * @ejb.interface-method
    *    view-type = "local"
	 */
	public InterviewDataValue fetchUserInterviewData(String studyName, String firstName, String lastName)
		throws NamingException, FinderException
	{
		/*******
		 * Get Study
		 */
		StudyEJBLocal study = StudyEJBUtil.getLocalHome().findByStudyName(studyName);
		
		InterviewEJBLocal 	interview		= null;
		InterviewDataValue	interviewData	= null;
		try
		{
			interview 		= findUserInterview(study, firstName, lastName, false);
			interviewData	= interview.getInterviewDataValue();
		}
		catch (CreateException e)
		{
			// Should never happen with create set to false
			e.printStackTrace();
		}

		return (interviewData);
	}
	
	/**
	 * @ejb.interface-method
    *    view-type = "local"
	 */
	public InterviewDataValue findByPrimaryKey(InterviewEJBPK pk)
				throws FinderException, NamingException
	{
		InterviewEJBLocal selected = getLocalHome().findByPrimaryKey(pk);
		InterviewDataValue retval = selected.getInterviewDataValue();
		return retval;
	}

	/**
	 * @ejb.interface-method
    *    view-type = "local"
	 */
	public InterviewDataValue createEntity(InterviewDataValue data)
				throws CreateException, NamingException
	{
		return getLocalHome().create(data).getInterviewDataValue();
	}

	/**
	 * @ejb.interface-method
    *    view-type = "local"
	 */
	public void removeEntity(InterviewDataValue data)
				throws RemoveException, FinderException, NamingException
	{
		getLocalHome().findByPrimaryKey(data.getPrimaryKey()).remove();
	}
	
	/**
	 * Used by applet to retrieve adjacency matrix
	 * 
	 * @ejb.interface-method
    *    view-type = "local"
	 */
	public String[] getAlters(InterviewEJBPK pk)
	{
		InterviewEJBLocal interview;
		try
		{
			interview = getLocalHome().findByPrimaryKey(pk);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return new String[0];
		}

		return interview.getAlters();
	}
	
	/**
	 * Used by applet to retrieve adjacency matrix
	 * 
	 * @ejb.interface-method
    *    view-type = "local"
	 */
	public int[][] getAdjacencyMatrix(InterviewEJBPK pk)
	{
		InterviewEJBLocal interview;
		try
		{
			interview = getLocalHome().findByPrimaryKey(pk);
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return new int[0][0];
		}

		return interview.getAdjacencyMatrix();
	}
	
	/**
	 * Used by applet to retrieve adjacency matrix
	 * 
	 * @ejb.interface-method
    *    view-type = "local"
	 */
	public void setAdjacencyMatrix(InterviewEJBPK pk, int[][] matrix)
	{
		InterviewEJBLocal interview;
		try
		{
			interview = getLocalHome().findByPrimaryKey(pk);
			interview.setAdjacencyMatrix(matrix);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	/**
	 * 	@throws javax.ejb.CreateException
	 * 
	 * 	@ejb.create-method
	 * 
	 * 	@ejb.permission 
	 * 		unchecked="true"
	 */
	public void ejbCreate() throws javax.ejb.CreateException
	{
	}
}
