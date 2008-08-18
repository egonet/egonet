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

import org.jboss.util.id.UID;

import com.endlessloopsoftware.egonet.interfaces.SelectionEJBPK;
import com.endlessloopsoftware.egonet.util.SelectionDataValue;

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
 * 	@ejb.bean name 						= "SelectionEJB"
 *           type 						= "CMP"
 *           cmp-version 			= "2.x"
 *           display-name 			= "SelectionEJB"
 *           description 			= "Selection EJB"
 *           view-type 				= "both"
 *           jndi-name 				= "SelectionEJBHome"
 *           local-jndi-name 		= "SelectionEJBLocalHome"
 *
 * 	@ejb.util
 *      generate						= "physical"
 * 
 * 	@ejb.persistence
 * 		table-name 						= "SelectionEJB"
 * 
 * 	@ejb.transaction
 * 		type								= "Required"
 * 
 * 	@ejb.pk
 * 		class 								= "com.endlessloopsoftware.egonet.interfaces.SelectionEJBPK"
 * 
 * 	@ejb.finder 
 *  	signature 						= "com.endlessloopsoftware.egonet.interfaces.SelectionEJBLocal findByPrimaryKey(com.endlessloopsoftware.egonet.interfaces.SelectionEJBPK pk)"
 * 		description 						= "Find all instances of this type"
 * 		query 								= "SELECT OBJECT(a) FROM SelectionEJB AS a WHERE a.id=?"
 * 		
 */
public abstract class SelectionEJB implements EntityBean
{
	private SelectionDataValue SelectionDataValue = null;

	/**
	 *
	 * @throws CreateException Thrown if the instance could not perform 
	 * the function requested by the container because of an system-level error.
	 * 
	 * @ejb.create-method
	 */
	public SelectionEJBPK ejbCreate(SelectionDataValue data) throws CreateException
	{
      UID id = new UID();
		this.setId(id.toString());
		return null;
	}

	/**
	 *
	 * @throws CreateException Thrown if the instance could not perform 
	 * the function requested by the container because of an system-level error.
	 */
	public void ejbPostCreate(SelectionDataValue data) throws CreateException
	{
		this.setSelectionDataValue(data);
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
	 * 	column-name = "ID"
	 * 
	 * @ejb.pk-field 
	 */
	public abstract String getId();
	public abstract void setId(String i);
	
	/**
	 * @ejb.persistence 
	 * 	column-name = "SELECTION_TEXT"
	 */
	public abstract String getText();
	public abstract void setText(String i);
	
	/**
	 * @ejb.persistence 
	 * 	column-name = "SELECTION_VALUE"
	 */
	public abstract int getValue();
	public abstract void setValue(int i);
	
	/**
	 * @ejb.persistence 
	 * 	column-name = "SELECTION_INDEX"
	 */
	public abstract int getIndex();
	public abstract void setIndex(int i);
	
	/**
	 * @ejb.persistence 
	 * 	column-name = "SELECTION_ADJACENT"
	 */
	public abstract boolean getAdjacent();
	public abstract void setAdjacent(boolean i);
	
	
	
	/*********************************************************
	 * Data Object Methods
	 */

	/**
	 *	@ejb.interface-method 
	 * 		view-type = "local"
	 */
	public SelectionDataValue getSelectionDataValue()
	{
		SelectionDataValue = new SelectionDataValue();
		try
		{
			SelectionDataValue.setId( getId() );
			SelectionDataValue.setText( getText() );
			SelectionDataValue.setValue( getValue() );
			SelectionDataValue.setIndex( getIndex() );
			SelectionDataValue.setAdjacent( getAdjacent() );

		}
		catch (Exception e)
		{
			throw new javax.ejb.EJBException(e);
		}

		return SelectionDataValue;
	}

	/**
	 *	@ejb.interface-method 
	 * 		view-type = "local"
	 */
	public void setSelectionDataValue( SelectionDataValue valueHolder )
	{

		try
		{
			setText( valueHolder.getText() );
			setValue( valueHolder.getValue() );
			setIndex( valueHolder.getIndex() );
			setAdjacent( valueHolder.getAdjacent() );

		}
		catch (Exception e)
		{
			throw new javax.ejb.EJBException(e);
		}
	}

	/* Value Objects END */
	
	/*********************************************************
	 * Relationship Methods
	 */
	
	/**
	 * 	@ejb.interface-method 
	 * 
	 *	@ejb.relation 
	 * 		name 						= "Question-Has-Selections"
	 *  	role-name 				= "Selection-To-Question"
	 * 		target-ejb 				= "QuestionEJB"
	 * 		target-role-name 		= "Question-From-Selection"
	 * 		target-multiple 		= "true"
	 * 
	 *	@jboss.relation
	 * 		fk-column 			= "QUESTION"
	 * 		related-pk-field	= "id"
	 * 
	 *	@jboss.relation
	 * 		fk-column 			= "STUDY_ID"
	 * 		related-pk-field	= "studyId"
	 */
	public abstract com.endlessloopsoftware.egonet.interfaces.QuestionEJBLocal getQuestion();

	/**
	 * 	@ejb.interface-method 
	 */
	public abstract void setQuestion(com.endlessloopsoftware.egonet.interfaces.QuestionEJBLocal question);
}


/**
 * $Log: SelectionEJB.java,v $
 * Revision 1.8  2004/04/08 15:03:06  admin
 * Changing some primary keys to UID strings so as I was getting duplicate keys
 * on the Survey Center Server
 *
 * Created Configuration Bean to hold various info. Right now it stores the
 * current active survey.
 *
 * Revision 1.7  2004/03/28 17:30:06  admin
 * Display applet in complete screen
 * Mark interviews complete
 * Only return names of complete interviews to client
 *
 * Revision 1.6  2004/02/15 14:59:01  admin
 * Fixing Header Tags
 *
 * Revision 1.5  2004/02/10 16:31:23  admin
 * Alter Prompt Completed
 *
 * Revision 1.4  2004/02/08 13:40:09  admin
 * Ego Questions Implemented.
 * Full workflow path complete in struts-config.xml
 * Took control of DataValue Objects from XDoclet
 *
 * Revision 1.3  2004/01/30 23:31:08  admin
 * Using struts-layout
 *
 * Revision 1.2  2004/01/08 14:03:28  admin
 * Adding Web Tier
 *
 * Revision 1.1  2004/01/03 15:44:20  admin
 * Adding new EJB files.
 * More relationships and use of value objects.
 *
 * Revision 1.1.1.1  2003/12/18 21:17:18  admin
 * Imported sources
 *
 * Revision 1.3  2003/12/10 20:05:42  admin
 * fixing header
 *
 * Revision 1.2  2003/12/10 20:02:58  admin
 * removing generated files
 *
 */
