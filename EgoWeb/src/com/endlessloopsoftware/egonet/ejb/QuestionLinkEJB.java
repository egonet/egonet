/**
 * <p>Copyright: Copyright (c) 2002 - 2004 </p>
 * <p>Company: Endless Loop Software</p>
 * @author Peter Schoaff
 *
 * $Id: QuestionLinkEJB.java,v 1.9 2004/04/08 15:03:06 admin Exp $
 */
package com.endlessloopsoftware.egonet.ejb;

import java.rmi.server.UID;

import javax.ejb.CreateException;
import javax.ejb.EJBException;
import javax.ejb.EntityBean;
import javax.ejb.EntityContext;
import javax.ejb.RemoveException;

import com.endlessloopsoftware.egonet.data.QuestionLinkDataValue;
import com.endlessloopsoftware.egonet.interfaces.QuestionEJBLocal;
import com.endlessloopsoftware.egonet.interfaces.QuestionEJBLocalHome;
import com.endlessloopsoftware.egonet.interfaces.QuestionEJBUtil;
import com.endlessloopsoftware.egonet.interfaces.QuestionLinkEJBPK;

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
 * 	@ejb.bean name 					= "QuestionLinkEJB"
 *           type 					= "CMP"
 *           cmp-version 		= "2.x"
 *           display-name 		= "QuestionLinkEJB"
 *           description 		= "QuestionLink EJB"
 *           view-type 			= "both"
 *           jndi-name 			= "QuestionLinkEJBHome"
 *           local-jndi-name 	= "QuestionLinkEJBLocalHome"
 *
 * 	@ejb.util
 *      generate					= "physical"
 * 
 * 	@ejb.persistence
 * 		table-name 					= "QuestionLinkEJB"
 * 
 * 	@ejb.transaction
 * 		type							= "Required"
 * 
 * 	@ejb.value-object 
 * 		name 							= "QuestionLinkData"
 * 		match 							= "*"
 * 
 * 	@ejb.pk
 * 		class 							= "com.endlessloopsoftware.egonet.interfaces.QuestionLinkEJBPK"
 * 
 * 	@ejb.finder 
 *  	signature 					= "com.endlessloopsoftware.egonet.interfaces.QuestionLinkEJBLocal findByPrimaryKey(com.endlessloopsoftware.egonet.interfaces.QuestionLinkEJBPK pk)"
 * 		description 					= "Find all instances of this type"
 * 		query 							= "SELECT OBJECT(a) FROM QuestionLinkEJB AS a WHERE a.id=?"
 * 		
 */
public abstract class QuestionLinkEJB implements EntityBean
{
	/**
	 * There are zero or more ejbCreate<METHOD>(...) methods, whose signatures match
	 * the signatures of the create<METHOD>(...) methods of the entity bean�s home interface.
	 * The container invokes an ejbCreate<METHOD>(...) method on an entity bean instance
	 * when a client invokes a matching create<METHOD>(...) method on the entity bean�s
	 * home interface.<br>
	 * 
	 * The entity bean provider�s responsibility is to initialize the instance in the ejbCreate<
	 * METHOD>(...) methods from the input arguments, using the get and set accessor
	 * methods, such that when the ejbCreate<METHOD>(...) method returns, the persistent
	 * representation of the instance can be created. <br>
	 * 
	 * The entity bean provider must not attempt to modify the values of cmr-fields in an ejbCreate<
	 * METHOD(...) method; this should be done in the ejbPostCreate<METHOD(...) method instead.<br>
	 * 
	 * The entity object created by the ejbCreate<METHOD> method must have a unique primary
	 * key. This means that the primary key must be different from the primary keys of all the existing
	 * entity objects within the same home. However, it is legal to reuse the primary key of a previously
	 * removed entity object. The implementation of the bean provider�s ejbCreate<
	 * METHOD>(...) methods should be coded to return a null.<br>
	 * 
	 * An ejbCreate<METHOD>(...) method executes in the transaction context determined by
	 * the transaction attribute of the matching create<METHOD>(...) method. 
	 * The database insert operations are performed by the container within the same
	 * transaction context after the Bean Provider�s ejbCreate<METHOD>(...) method completes.    
	 *
	 * @throws CreateException Thrown if the instance could not perform 
	 * the function requested by the container because of an system-level error.
	 * 
	 * @ejb.create-method
	 */
	public QuestionLinkEJBPK ejbCreate(QuestionLinkDataValue data, QuestionEJBLocal question) throws CreateException
	{
		this.setId((new UID()).toString());
		return null;
	}

	/**
	 * For each ejbCreate<METHOD>(...) method, there is a matching ejbPostCreate<
	 * METHOD>(...) method that has the same input parameters but whose return type is
	 * void. The container invokes the matching ejbPostCreate<METHOD>(...) method on
	 * an instance after it invokes the ejbCreate<METHOD>(...) method with the same arguments.
	 * The instance can discover the primary key by calling getPrimaryKey() on its
	 * entity context object. <br>
	 * 
	 * The entity object identity is available during the ejbPostCreate<METHOD>(...)
	 * method. The instance may, for example, obtain the component interface of the associated entity
	 * object and pass it to another enterprise bean as a method argument.<br>
	 * 
	 * The entity Bean Provider may use the ejbPostCreate<METHOD>(...) to set the values
	 * of cmr-fields to complete the initialization of the entity bean instance.
	 * An ejbPostCreate<METHOD>(...) method executes in the same transaction context as
	 * the previous ejbCreate<METHOD>(...) method.
	 *
	 * @throws CreateException Thrown if the instance could not perform 
	 * the function requested by the container because of an system-level error.
	 */
	public void ejbPostCreate(QuestionLinkDataValue data, QuestionEJBLocal question) throws CreateException
	{
		this.setQuestionLinkDataValue(data);
		
		try
		{
			QuestionEJBLocalHome questionHome = QuestionEJBUtil.getLocalHome();
//			QuestionEJBLocal question = questionHome.findByPrimaryKey(new QuestionEJBPK(data.getQuestionId(), studyId));
			this.setQuestion(question);
		} 
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new CreateException(e.getMessage());
		} 
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
	 * 	column-name = "QUESTION_ID"
	 */
	public abstract Long getQuestionId();
	public abstract void setQuestionId(Long i);
	
	/**
	 * @ejb.persistence 
	 * 	column-name = "ANSWER_VALUE"
	 * 
	 */
	public abstract int getAnswerValue();
	public abstract void setAnswerValue(int i);
	
	/**
	 * @ejb.persistence 
	 * 	column-name = "ANSWER_STRING"
	 * 
	 */
	public abstract String getAnswerString();
	public abstract void setAnswerString(String i);
	
	/**
	 * @ejb.persistence 
	 * 	column-name = "ACTIVE"
	 */
	public abstract boolean getActive();
	public abstract void setActive(boolean i);
	
	/*********************************************************
	 * Data Object Methods
	 */

	/**
	 * @ejb.interface-method 
	 */
	public abstract void setQuestionLinkDataValue(QuestionLinkDataValue data);
	
	/**
	 * @ejb.interface-method 
	 */
	public abstract QuestionLinkDataValue getQuestionLinkDataValue();
	
	/*********************************************************
	 * Relationship Methods
	 */
	
	/**
	 * 	@ejb.interface-method 
	 * 
	 *	@ejb.relation 
	 * 		name 				= "Question-Has-QuestionLink"
	 *  	role-name 		= "QuestionLink-To-Question"
	 */
	public abstract QuestionEJBLocal getQuestion();
	
	/**
	 * 	@ejb.interface-method 
	 */
	public abstract void setQuestion(QuestionEJBLocal question);
}


/**
 * $Log: QuestionLinkEJB.java,v $
 * Revision 1.9  2004/04/08 15:03:06  admin
 * Changing some primary keys to UID strings so as I was getting duplicate keys
 * on the Survey Center Server
 *
 * Created Configuration Bean to hold various info. Right now it stores the
 * current active survey.
 *
 * Revision 1.8  2004/03/28 17:30:06  admin
 * Display applet in complete screen
 * Mark interviews complete
 * Only return names of complete interviews to client
 *
 * Revision 1.7  2004/02/15 14:59:00  admin
 * Fixing Header Tags
 *
 * Revision 1.6  2004/02/10 16:31:23  admin
 * Alter Prompt Completed
 *
 * Revision 1.5  2004/02/07 04:33:25  admin
 * First egoquestions page
 *
 * Revision 1.4  2004/01/30 23:31:08  admin
 * Using struts-layout
 *
 * Revision 1.3  2004/01/08 14:03:27  admin
 * Adding Web Tier
 *
 * Revision 1.2  2004/01/03 15:44:20  admin
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
