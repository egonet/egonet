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

import javax.ejb.CreateException;
import javax.ejb.EJBException;
import javax.ejb.EntityBean;
import javax.ejb.EntityContext;
import javax.ejb.RemoveException;

import com.endlessloopsoftware.egonet.interfaces.ConfigurationEJBPK;
import com.endlessloopsoftware.egonet.interfaces.StudyEJBLocal;
import com.endlessloopsoftware.elsutils.security.SymmetricKeyEncryption;

/**
 * XDoclet-based CMP entity bean.  This class must be declared
 * <code>public abstract</code> because the concrete class will
 * be implemented by the CMP provider's tooling.<br>
 * 
 * To generate code:
 * <br>
 * <ul>
 * <li> Add Standard EJB module to XDoclet project properties
 * <li> Customize XDoclet configuration
 * <li> Run XDoclet
 * </ul>
 * <br>
 * Please see the included XDoclet Overview 
 * and the XDoclet Reference in the help system for details
 * 
 * 	@ejb.bean name 					= "ConfigurationEJB"
 *           type 					= "CMP"
 *           cmp-version 		= "2.x"
 *           display-name 		= "ConfigurationEJB"
 *           description 		= "Configuration EJB"
 *           view-type 			= "both"
 *           jndi-name 			= "ConfigurationEJBHome"
 *           local-jndi-name 	= "ConfigurationEJBLocalHome"
 *
 * 	@ejb.util
 *      generate					= "physical"
 * 
 * 	@ejb.persistence
 * 		table-name 					= "ConfigurationEJB"
 * 
 * 	@ejb.transaction
 * 		type							= "Required"
 * 
 * 	@ejb.pk
 * 		class 							= "com.endlessloopsoftware.egonet.interfaces.ConfigurationEJBPK"
 * 
 * 	@ejb.finder 
 *  	signature 					= "com.endlessloopsoftware.egonet.interfaces.ConfigurationEJBLocal findByPrimaryKey(com.endlessloopsoftware.egonet.interfaces.ConfigurationEJBPK pk)"
 * 		description 					= "Find all instances of this type"
 * 		query 							= "SELECT OBJECT(a) FROM ConfigurationEJB AS a WHERE a.id=?"
 * 		
 */
public abstract class ConfigurationEJB implements EntityBean
{
   // There is only one instance of this bean
   private static final Byte ID = new Byte((byte) 1);
   public static final ConfigurationEJBPK pk = new ConfigurationEJBPK(ID);
   
	/**
	 *
	 * @throws CreateException Thrown if the instance could not perform 
	 * the function requested by the container because of an system-level error.
	 * 
	 * @ejb.create-method
	 */
	public ConfigurationEJBPK ejbCreate() throws CreateException
	{
		this.setId(ID);
		setServerPassword(SymmetricKeyEncryption.encrypt("chrism@bebr.ufl.edu"));
		return null;
	}

	/**
	 *
	 * @throws CreateException Thrown if the instance could not perform 
	 * the function requested by the container because of an system-level error.
	 */
	public void ejbPostCreate() throws CreateException
	{
	}


	public void ejbActivate() throws EJBException {}
	public void ejbPassivate() throws EJBException {}
	public void ejbLoad() throws EJBException {}
	public void ejbStore() throws EJBException {}
	public void ejbRemove() throws EJBException, RemoveException {}
	public void setEntityContext(EntityContext newContext) throws EJBException {}
	public void unsetEntityContext() throws EJBException {}
	
	
	/*********************************************************
	 * Persistence Methods
	 */

	/**
	 * @ejb.persistence 
	 * 	 column-name = "ID"
	 * 
	 * @ejb.pk-field 
	 */
	public abstract Byte getId();
	public abstract void setId(Byte i);
	
   /**
    * @ejb.persistence 
    *     column-name = "SERVER_PASSWORD"
    */
   public abstract String getServerPassword();
   public abstract void setServerPassword(String i);
   
   /**
    * @ejb.interface-method
    *    view-type = "local"
    */
   public boolean checkServerPassword(String encryptedPassword)
   {
      //log.debug (getServerPassword() + " : " + encryptedPassword);
      return (encryptedPassword.equals(getServerPassword()));
   }
	
	
	/*********************************************************
	 * Relationship Methods
	 */
	
   /**
    * @ejb.interface-method 
    * 
    * @ejb.relation 
    *       name              = "Configuration-Has-Study"
    *       role-name         = "Configuration-To-Study"
    *       target-role-name  = "Study-To-Configuration"
    *       target-ejb        = "StudyEJB"
    * 
    * @jboss.relation
    *       fk-column         = "STUDY"
    *       related-pk-field  = "id"
    * 
    */
   public abstract StudyEJBLocal getStudy();
   
   /**
    *    @ejb.interface-method 
    */
   public abstract void setStudy(StudyEJBLocal questionLink);
}


/**
 * $Log: ConfigurationEJB.java,v $
 * Revision 1.3  2004/05/26 12:35:53  admin
 * Adding tutorial
 *
 * Revision 1.2  2004/04/11 15:19:51  admin
 * Passwords for remote access to interviews
 *
 * Revision 1.1  2004/04/08 15:03:05  admin
 * Changing some primary keys to UID strings so as I was getting duplicate keys
 * on the Survey Center Server
 *
 * Created Configuration Bean to hold various info. Right now it stores the
 * current active survey.
 *
 */
