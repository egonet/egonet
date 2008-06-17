/**
 * <p>Title: Egocentric Networks Client Program</p>
 * <p>Description: Subject Interview Client</p>
 * <p>Copyright: Copyright (c) 2002 - 2004 </p>
 * <p>Company: Endless Loop Software</p>
 * @author Peter Schoaff
 *
 * $Id: QuestionSB.java,v 1.7 2004/05/14 15:06:08 admin Exp $
 */


package com.endlessloopsoftware.egonet.ejb;
import javax.ejb.CreateException;
import javax.ejb.FinderException;
import javax.ejb.RemoveException;
import javax.naming.NamingException;

import com.endlessloopsoftware.egonet.interfaces.QuestionEJBLocal;
import com.endlessloopsoftware.egonet.interfaces.QuestionEJBLocalHome;
import com.endlessloopsoftware.egonet.interfaces.QuestionEJBPK;
import com.endlessloopsoftware.egonet.interfaces.QuestionEJBUtil;
import com.endlessloopsoftware.egonet.util.QuestionDataValue;


/**
 * Session facade for QuestionEJB. 
 * @copyright 2004 Endless Loop Software Inc.
 * 
 *	@ejb.bean 
 * 		name						= "QuestionSB" 
 * 		type						= "Stateless" 
 * 		view-type				   = "both"
 * 		jndi-name	            = "QuestionSB" 
 * 		local-jndi-name      = "QuestionSBLocal"
 * 
 *	@ejb.ejb-ref 
 *		ejb-name="QuestionEJB" 
 *		view-type="local" 
 *
 *	@ejb.util
 * 		generate="physical" 
 * 
 * 
 */
public class QuestionSB extends EgoSB
{
	private QuestionEJBLocalHome getLocalHome()
				throws javax.naming.NamingException
	{
		return QuestionEJBUtil.getLocalHome();
	}

	/**
	 * @ejb.interface-method
	 *  
	 */
	public QuestionEJBLocal findByPrimaryKey(QuestionEJBPK pk)
				throws FinderException, NamingException
	{
		QuestionEJBLocal selected = (QuestionEJBLocal) getLocalHome().findByPrimaryKey(pk);
		return selected;
	}

   /**
    * @ejb.interface-method
    *  
    */
   public QuestionDataValue findDataByPrimaryKey(QuestionEJBPK pk)
            throws FinderException, NamingException
   {
      QuestionEJBLocal selected = (QuestionEJBLocal) getLocalHome().findByPrimaryKey(pk);
      return selected.getQuestionDataValue();
   }

	/**
	 * @ejb.interface-method 
	 */
	public QuestionDataValue createEntity(QuestionDataValue data, Long studyId)
				throws CreateException, NamingException
	{
		return getLocalHome().create(data).getQuestionDataValue();
	}

	/**
	 * @ejb.interface-method
	 *  
	 */
	public void removeEntity(QuestionDataValue data)
				throws RemoveException, FinderException, NamingException
	{
		getLocalHome().findByPrimaryKey(data.getPrimaryKey()).remove();
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
