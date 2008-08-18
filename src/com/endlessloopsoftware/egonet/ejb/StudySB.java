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
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

import javax.ejb.CreateException;
import javax.ejb.FinderException;
import javax.ejb.RemoveException;
import javax.naming.NamingException;

import com.endlessloopsoftware.egonet.interfaces.ConfigurationSBLocal;
import com.endlessloopsoftware.egonet.interfaces.ConfigurationSBUtil;
import com.endlessloopsoftware.egonet.interfaces.InterviewEJBLocal;
import com.endlessloopsoftware.egonet.interfaces.StudyEJBLocal;
import com.endlessloopsoftware.egonet.interfaces.StudyEJBLocalHome;
import com.endlessloopsoftware.egonet.interfaces.StudyEJBPK;
import com.endlessloopsoftware.egonet.interfaces.StudyEJBUtil;
import com.endlessloopsoftware.egonet.util.InterviewIdentifier;
import com.endlessloopsoftware.egonet.util.StudyAndInterviewTransfer;
import com.endlessloopsoftware.egonet.util.StudyDataValue;


/**
 * Session facade for StudyEJB. 
 * @copyright 2004 Endless Loop Software Inc.
 * 
 *	@ejb.bean 
 * 		name					= "StudySB" 
 * 		type					= "Stateless" 
 * 		view-type				= "both"
 * 		jndi-name				= "ejb/StudySB" 
 * 		local-jndi-name		= "StudySBLocal"
 * 
 *	@ejb.ejb-ref 
 *    ejb-name          = "StudyEJB" 
 *    view-type         = "local" 
 *
 *	@ejb.util
 * 		generate          = "physical" 
 * 
 * 
 */
public class StudySB extends EgoSB
{
	private StudyEJBLocalHome getLocalHome()
			throws javax.naming.NamingException
	{
		return StudyEJBUtil.getLocalHome();
	}

	/**
	 */
	private StudyEJBLocal findByPrimaryKey(StudyEJBPK pk)
			throws FinderException, NamingException
	{
		StudyEJBLocal selected = getLocalHome().findByPrimaryKey(pk);
		return selected;
	}

   /**
    * @ejb.interface-method 
    *    view-type = "remote"
    */
   public StudyDataValue fetchDataByStudyName(String name, String password)
      throws FinderException, NamingException, CreateException, RemoteException
   {
      ConfigurationSBLocal configurationSession = ConfigurationSBUtil.getLocalHome().create();
      
      if (configurationSession.checkPassword(password))
      {
         return fetchDataByStudyName(name);
      }
      else
      {
         throw new RemoteException("Invalid Password");
      }
   }

	/**
    * Returns Data value for a given study
    * 
    * @ejb.interface-method 
    *    view-type = "local"
	 */
	public StudyDataValue fetchDataByStudyName(String name)
			throws FinderException, NamingException
	{
		StudyEJBLocal selected = getLocalHome().findByStudyName(name);
		System.out.println("Returning Data Value for " + selected.getStudyName());
		return selected.getStudyDataValue();
	}

   /**
    * Returns Data value for a given study
    * 
    * @ejb.interface-method
    *    view-type = "local"
    */
   public StudyEJBLocal fetchByStudyName(String name)
         throws FinderException, NamingException
   {
      StudyEJBLocal selected = getLocalHome().findByStudyName(name);
      System.out.println("Returning Data Value for " + selected.getStudyName());
      return selected;
   }

	/**
	 * @ejb.interface-method 
    *    view-type = "local"
	 */
	public void createStudy(StudyDataValue data)
			throws CreateException, NamingException
	{
		try
		{
			// Make sure this study name is unique
			StudyEJBLocal study = getLocalHome().findByStudyName(data.getStudyName());
			
			throw new CreateException("There is already a study with this name " +study +". Please choose a different Name");
		}
		catch (NamingException e)
		{
			e.printStackTrace();
			throw new CreateException("Internal Server Error");
		}		
		catch (FinderException e){ /* We want this to occur */}

		getLocalHome().create(data);
	}

   /**
    * @ejb.interface-method 
    *    view-type = "remote"
    */
   public void createStudy(StudyDataValue data, String password)
         throws CreateException, NamingException, RemoteException
   {
      ConfigurationSBLocal configurationSession = ConfigurationSBUtil.getLocalHome().create();
      
      if (configurationSession.checkPassword(password))
      {
         createStudy(data);
      }
      else
      {
         throw new RemoteException("Invalid Password");
      }
   }

	/**
	 * @ejb.interface-method
    *    view-type = "local"
	 */
	public void removeEntity(StudyDataValue data)
				throws RemoveException, FinderException, NamingException
	{
		getLocalHome().findByPrimaryKey(data.getPrimaryKey()).remove();
	}
	
	/**
	 * @ejb.interface-method
    *    view-type = "local"
	 */
	public StudyDataValue getStudyDataValue(Long id)
	{
		try
		{
			StudyEJBLocal study = findByPrimaryKey(new StudyEJBPK(id));
			return study.getStudyDataValue();
		}
		catch (Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}
	
   /**
    * @ejb.interface-method
    *    view-type = "local"
    */
   public Vector getDisplayableAlterQuestions(Long id)
   {
      try
      {
         StudyEJBLocal study = findByPrimaryKey(new StudyEJBPK(id));
         return study.getDisplayableAlterQuestions();
      }
      catch (Exception e)
      {
         e.printStackTrace();
         return null;
      }
   }
   
	/**
	 * @ejb.interface-method
    *    view-type = "local"
	 */
	public StudyEJBLocal getStudy(Long id)
	{
		StudyEJBLocal study = null;
		try
		{
			study = findByPrimaryKey(new StudyEJBPK(id));
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
		return study;
	}
	
	
   /**
    * Returns name to use for a selection list
    * 
    * @ejb.interface-method 
    *    view-type = "remote"
    */
   public Set getStudyNames()
   {
      Set names;
      try
      {
         names = getLocalHome().getStudyNames();
      }
      catch (NamingException e)
      {
         e.printStackTrace();
         names = new HashSet(0);
      }
      return names;
   }

   /**
    * Returns name to use for a selection list
    * 
    * @ejb.interface-method 
    *    view-type = "remote"
    */
   public Set getStudyAndInterviewNames()
   {
      Set studyNames = new HashSet();

      try
      {
         Collection studies = getLocalHome().findAll();

         System.out.println("Found " + studies.size() + " studies.");

         for (Iterator it = studies.iterator(); it.hasNext();)
         {
            StudyEJBLocal study = (StudyEJBLocal) it.next();
            StudyAndInterviewTransfer xfer = new StudyAndInterviewTransfer(study.getStudyName());

            System.out.println("Found " + study.getInterviews().size() + " interviews.");
            for (Iterator ints = study.getInterviews().iterator(); ints.hasNext();)
            {
               InterviewEJBLocal interview = (InterviewEJBLocal) ints.next();

               if (interview.getComplete().booleanValue())
                  xfer.addInterview(new InterviewIdentifier(interview.getId(), interview.getFirstName(),
                                                            interview.getLastName()));
            }

            studyNames.add(xfer);
         }

      }
      catch (Exception e)
      {
         e.printStackTrace();
      }

      return studyNames;
   }


	/**
	 * 	@ejb.create-method
	 * 
	 * 	@ejb.permission 
	 * 		unchecked="true"
	 */
	public void ejbCreate() throws javax.ejb.CreateException
	{
	}
}
