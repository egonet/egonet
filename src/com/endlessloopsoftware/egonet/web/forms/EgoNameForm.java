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
import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.util.ModuleException;

import com.endlessloopsoftware.egonet.util.StudyDataValue;
import com.endlessloopsoftware.egonet.web.WebShared;

public class EgoNameForm extends ELSValidatorForm
{
	//Logger logger = Logger.getLogger(this.getClass().getName());
	
	String 					firstName;
	String 					lastName;
	
	StudyDataValue study;
	
	public EgoNameForm()
	{
		logger.debug("instantiate");
		
	}
	
	/**
	 * @return Returns the firstName.
	 */
	public String getFirstName()
	{
		return firstName;
	}
	/**
	 * @param firstName The firstName to set.
	 */
	public void setFirstName(String firstName)
	{
		logger.debug("Setting First Name");
		this.firstName = firstName;
	}
	
	/**
	 * @return Returns the lastName.
	 */
	public String getLastName()
	{
		return lastName;
	}
	/**
	 * @param lastName The lastName to set.
	 */
	public void setLastName(String lastName)
	{
		logger.debug("Setting Last Name");
		this.lastName = lastName;
	}
	
	
	/**
	 * @return Returns the study.
	 */
	public StudyDataValue getStudy()
	{
		return study;
	}
	
	/* (non-Javadoc)
	 * @see org.apache.struts.action.ActionForm#reset(org.apache.struts.action.ActionMapping, javax.servlet.http.HttpServletRequest)
	 */
	public void reset(ActionMapping mapping, HttpServletRequest request)
	{
		logger.debug("WelcomeForm::reset");
		this.firstName = "";
		this.lastName = "";
		
		try 
		{
			study = WebShared.retrieveStudyDataValue(servlet);
		} 
		catch (ModuleException e) 
		{
			e.printStackTrace();
			// @TODO Set global error here
		}
	}
	
	/* (non-Javadoc)
	 * @see org.apache.struts.action.ActionForm#validate(org.apache.struts.action.ActionMapping, javax.servlet.http.HttpServletRequest)
	 */
	public ActionErrors validate(ActionMapping arg0, HttpServletRequest arg1)
	{
		logger.debug("Validating Form");
		ActionErrors errors = new ActionErrors();
		
//		if ("Fred".equals(firstName))
//		{
//			errors.add("firstName", new ActionError("Hi Fred"));
//		}
//		
		
		return errors;
	}
}

/**
 * $Log: EgoNameForm.java,v $
 * Revision 1.6  2004/04/05 01:16:44  admin
 * Modifying to use new Applet Linking Interface
 *
 * Revision 1.5  2004/03/28 17:30:07  admin
 * Display applet in complete screen
 * Mark interviews complete
 * Only return names of complete interviews to client
 *
 * Revision 1.4  2004/03/18 15:23:40  admin
 * Recovers previous interview state
 *
 * Revision 1.3  2004/02/15 14:59:02  admin
 * Fixing Header Tags
 *
 * Revision 1.2  2004/02/10 16:31:24  admin
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
