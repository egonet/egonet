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
import javax.naming.NamingException;

import org.jboss.logging.Logger;

import com.endlessloopsoftware.egonet.interfaces.ConfigurationEJBLocal;
import com.endlessloopsoftware.egonet.interfaces.ConfigurationEJBUtil;
import com.endlessloopsoftware.egonet.interfaces.StudyEJBLocal;
import com.endlessloopsoftware.egonet.interfaces.StudySBLocal;
import com.endlessloopsoftware.egonet.interfaces.StudySBLocalHome;
import com.endlessloopsoftware.egonet.interfaces.StudySBUtil;


/**
 * Session facade for ConfigurationEJB. 
 * @copyright 2004 Endless Loop Software Inc.
 * 
 *	@ejb.bean 
 * 		name            = "ConfigurationSB" 
 * 		type            = "Stateless" 
 * 		view-type       = "both"
 * 		jndi-name       = "ejb/ConfigurationSB" 
 * 		local-jndi-name = "ConfigurationSBLocal"
 * 
 *	@ejb.ejb-ref 
 *		ejb-name        = "StudyEJB" 
 *		view-type       = "local" 
 *
 * @ejb.ejb-ref 
 *    ejb-name        = "ConfigurationEJB" 
 *    view-type       = "local" 
 *
 *	@ejb.util
 * 		generate        = "physical" 
 * 
 * 
 */
public class ConfigurationSB extends EgoSB
{
	
	private static Logger log = Logger.getLogger(ConfigurationSB.class);
	
   private ConfigurationEJBLocal getConfiguration() throws CreateException, NamingException
   {
      ConfigurationEJBLocal configuration;
      
      try
      {
         configuration = ConfigurationEJBUtil.getLocalHome().findByPrimaryKey(ConfigurationEJB.pk);
      }
      catch (FinderException e)
      {
         configuration = ConfigurationEJBUtil.getLocalHome().create();
      }
       
      return configuration;
   }
   
	/**
    * Sets Active Study By Name
    * 
    * @ejb.interface-method
    *    view-type = "remote"
	 */
   public void setActiveStudy(String name, String password)
      throws RemoteException
   {
      try
      {
         StudySBLocalHome  studyHome   = StudySBUtil.getLocalHome();
         StudySBLocal      studySB     = studyHome.create();
         StudyEJBLocal     study       = studySB.fetchByStudyName(name);
         
         ConfigurationEJBLocal configuration = getConfiguration();
         
         if (configuration.checkServerPassword(password))
         {
            configuration.setStudy(study);
         }
         else
         {
            throw new RemoteException("Incorrect Password");
         }
      }
      catch (Exception e)
      {
         e.printStackTrace();
         throw new RemoteException(e.getMessage());
      }
   }

   /**
    * Gets Active Study
    * 
    * @ejb.interface-method
    *    view-type = "local"
    */
   public StudyEJBLocal getActiveStudy()
      throws FinderException
   {
      StudyEJBLocal study;
      try
      {
    	  log.info("Trying to get configuration");
          ConfigurationEJBLocal configuration = getConfiguration();
          log.info("Configuration found: " + configuration.getPrimaryKey() + ", trying to get a study");
          study = configuration.getStudy();
          if(study != null)
        	  log.info("Study found: " + study.getStudyName());
          else
        	  log.warn("Study was found to be NULL!!!");
      }
      catch (Exception ex)
      {
          log.error("Study fetch failure: " + ex.getMessage(), ex);
          throw new RuntimeException(ex.getMessage());
      }
      
      if(study == null)
    	  throw new FinderException("getActiveStudy() failed to find!");
      
      return study;
   }

   /**
    * Checks password against DB
    * 
    * @ejb.interface-method
    *    view-type = "local"
    */
   public boolean checkPassword(String password)
   {
      boolean valid = false;
      try
      {
         ConfigurationEJBLocal configuration = getConfiguration();
         valid = configuration.checkServerPassword(password);
      }
      catch (Exception e)
      {
         e.printStackTrace();
      }
      
      return valid;
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


/**
 * $Log: ConfigurationSB.java,v $
 * Revision 1.2  2004/04/11 15:19:51  admin
 * Passwords for remote access to interviews
 *
 * Revision 1.1  2004/04/08 15:03:06  admin
 * Changing some primary keys to UID strings so as I was getting duplicate keys
 * on the Survey Center Server
 *
 * Created Configuration Bean to hold various info. Right now it stores the
 * current active survey.
 *
 */
