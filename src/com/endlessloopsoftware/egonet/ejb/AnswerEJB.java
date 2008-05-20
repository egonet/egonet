/**
 * <p>Copyright: Copyright (c) 2002 - 2004 </p>
 * <p>Company: Endless Loop Software</p>
 * @author Peter Schoaff
 *
 * $Id: AnswerEJB.java,v 1.13 2004/05/14 15:06:07 admin Exp $
 */
package com.endlessloopsoftware.egonet.ejb;

import java.rmi.server.UID;

import javax.ejb.CreateException;
import javax.ejb.EJBException;
import javax.ejb.EntityBean;
import javax.ejb.EntityContext;
import javax.ejb.RemoveException;

import com.endlessloopsoftware.egonet.AlterPair;
import com.endlessloopsoftware.egonet.Shared.AlterPromptType;
import com.endlessloopsoftware.egonet.interfaces.AnswerEJBPK;
import com.endlessloopsoftware.egonet.interfaces.InterviewEJBLocal;
import com.endlessloopsoftware.egonet.interfaces.QuestionEJBLocal;
import com.endlessloopsoftware.egonet.interfaces.StudyEJBLocal;
import com.endlessloopsoftware.egonet.util.AnswerDataValue;

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
 *   @ejb.bean 
 *    name                 = "AnswerEJB"
 *      type               = "CMP"
 *      cmp-version        = "2.x"
 *      display-name       = "AnswerEJB"
 *      description        = "Answer EJB"
 *      view-type          = "both"
 *      jndi-name          = "AnswerEJBHome"
 *      local-jndi-name    = "AnswerEJBLocalHome"
 *
 *   @ejb.util
 *      generate           = "physical"
 * 
 *   @ejb.persistence
 *      table-name         = "AnswerEJB"
 * 
 *   @ejb.transaction
 *      type               = "Required"
 * 
 *   @ejb.pk
 *      class              = "com.endlessloopsoftware.egonet.interfaces.AnswerEJBPK"
 * 
 *   @ejb.finder 
 *      signature          = "com.endlessloopsoftware.egonet.interfaces.AnswerEJBLocal findByPrimaryKey(com.endlessloopsoftware.egonet.interfaces.AnswerEJBPK pk)"
 *       description       = "Find all instances of this type"
 *       query             = "SELECT OBJECT(a) FROM AnswerEJB AS a WHERE a.id=?"
 */
public abstract class AnswerEJB
   implements EntityBean
{
   private AnswerDataValue AnswerDataValue = null;

   /**
    *
    * @throws CreateException Thrown if the instance could not perform 
    * the function requested by the container because of an system-level error.
    * 
    * @ejb.create-method
    */
   public AnswerEJBPK ejbCreate(AnswerDataValue data) throws CreateException
   {
      UID id = new UID();
      this.setId(id.toString());
      this.setTimestamp(System.currentTimeMillis());
      return null;
   }

   /**
    *
    * @throws CreateException Thrown if the instance could not perform 
    * the function requested by the container because of an system-level error.
    */
   public void ejbPostCreate(AnswerDataValue data) throws CreateException
   {
      this.setAnswerDataValue(data);
   }

   public void ejbActivate() throws EJBException
   {
   }

   public void ejbPassivate() throws EJBException
   {
   }

   public void ejbLoad() throws EJBException
   {
   }

   public void ejbStore() throws EJBException
   {
   }

   public void ejbRemove() throws EJBException, RemoveException
   {
   }

   public void setEntityContext(EntityContext newContext) throws EJBException
   {
   }

   public void unsetEntityContext() throws EJBException
   {
   }

   /*********************************************************
    * Persistence Methods
    */

   /**
    * @ejb.persistence 
    *    column-name = "ID"
    * 
    *    @ejb.value-object 
    *       match = "data"
    * 
    * @ejb.pk-field 
    */
   public abstract String getId();

   public abstract void setId(String i);

   /**
    * @ejb.persistence 
    *    column-name = "TIMESTAMP"
    * 
    *    @ejb.value-object 
    *       match = "data"
    */
   public abstract long getTimestamp();

   public abstract void setTimestamp(long i);

   /**
    *    @ejb.persistence 
    *       column-name = "ALTERS"
    * 
    *    @ejb.value-object 
    *       match = "data"
    */
   public abstract AlterPair getAlters();

   public abstract void setAlters(AlterPair i);

   /**
    * @ejb.persistence 
    *    column-name = "ALTER_PROMPT_STATE"
    * 
    * @ejb.value-object 
    *    match = "data"
    */
   public abstract AlterPromptType getAlterPromptState();

   public abstract void setAlterPromptState(AlterPromptType i);

   /**
    * @ejb.persistence 
    *    column-name = "ANSWERED"
    * 
    *    @ejb.value-object 
    *       match = "data"
    */
   public abstract boolean getAnswered();

   public abstract void setAnswered(boolean i);

   /**
    * @ejb.persistence 
    *    column-name = "ANSWER_INDEX"
    * 
    *    @ejb.value-object 
    *       match = "data"
    */
   public abstract int getAnswerIndex();

   public abstract void setAnswerIndex(int i);

   /**
    * @ejb.persistence 
    *    column-name = "ANSWER_VALUE"
    * 
    *    @ejb.value-object 
    *       match = "data"
    */
   public abstract int getAnswerValue();

   public abstract void setAnswerValue(int i);

   /**
    * @ejb.persistence 
    *    column-name = "ANSWER_STRING"
    * 
    *    @ejb.value-object 
    *       match = "data"
    */
   public abstract String getAnswerString();

   public abstract void setAnswerString(String i);

   /**
    * @ejb.persistence 
    *    column-name = "ANSWER_ADJACENT"
    * 
    *    @ejb.value-object 
    *       match = "data"
    */
   public abstract boolean getAnswerAdjacent();

   public abstract void setAnswerAdjacent(boolean i);

   /*********************************************************
    * Data Object Methods
    */

   /**
    *   @ejb.interface-method 
    *       view-type = "local"
    */
   public AnswerDataValue getAnswerDataValue(StudyEJBLocal study)
   {
      AnswerDataValue = new AnswerDataValue(getId(), getAlters(), getAlterPromptState(), new String[0], getAnswered(),
                                            getAnswerIndex(), getAnswerValue(), getAnswerString(), getAnswerAdjacent(),
                                            study, getQuestion());

      return AnswerDataValue;
   }

   /**
    *   @ejb.interface-method 
    *       view-type = "local"
    */
   public void setAnswerDataValue(AnswerDataValue valueHolder)
   {

      try
      {
         setAlters(valueHolder.getAlters());
         setAlterPromptState(valueHolder.getAlterPromptState());
         setAnswered(valueHolder.getAnswered());
         setAnswerIndex(valueHolder.getAnswerIndex());
         setAnswerValue(valueHolder.getAnswerValue());
         setAnswerString(valueHolder.getAnswerString());
         setAnswerAdjacent(valueHolder.getAnswerAdjacent());
         setQuestion(valueHolder.getQuestion());
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
    *    @ejb.interface-method 
    * 
    *   @ejb.relation 
    *       name                = "Question-Has-Answers"
    *     role-name          = "Answers-To-Question"
    * 
    *   @jboss.relation
    *       fk-column          = "QUESTION"
    *       related-pk-field   = "id"
    * 
    *   @jboss.relation
    *       fk-column          = "STUDY_ID"
    *       related-pk-field   = "studyId"
    */
   public abstract QuestionEJBLocal getQuestion();

   /**
    *    @ejb.interface-method 
    */
   public abstract void setQuestion(QuestionEJBLocal question);

   /**
    *    @ejb.interface-method 
    * 
    *   @ejb.relation 
    *       name                = "Interview-Has-Answers"
    *     role-name          = "Answers-To-Interview"
    * 
    *   @jboss.relation
    *       fk-column          = "INTERVIEW"
    *       related-pk-field   = "id"
    */
   public abstract InterviewEJBLocal getInterview();

   /**
    *    @ejb.interface-method 
    */
   public abstract void setInterview(InterviewEJBLocal interview);
}

/**
 * $Log: AnswerEJB.java,v $
 * Revision 1.13  2004/05/14 15:06:07  admin
 * Added Correction mode to Applet
 *
 * Revision 1.12  2004/04/10 23:09:21  admin
 * Implemented Three Step Elicitation
 *   Added Position Forward Action
 *   Store current prompt type as part of answer and interview position
 *
 * Using custom version of FR Layout to try to keep labels from overlapping
 * borders
 *
 * Revision 1.11  2004/04/08 15:03:05  admin
 * Changing some primary keys to UID strings so as I was getting duplicate keys
 * on the Survey Center Server
 *
 * Created Configuration Bean to hold various info. Right now it stores the
 * current active survey.
 *
 * Revision 1.10  2004/03/28 17:30:06  admin
 * Display applet in complete screen
 * Mark interviews complete
 * Only return names of complete interviews to client
 *
 * Revision 1.9  2004/03/18 15:23:40  admin
 * Recovers previous interview state
 *
 * Revision 1.8  2004/03/12 18:05:27  admin
 * Applet now works under Windows IE.
 * Fixed layout issues related to struts-layout converting spaces to nbsp
 * Using Servlet for applet/server communications
 *
 * Revision 1.7  2004/02/15 14:59:00  admin
 * Fixing Header Tags
 *
 * Revision 1.6  2004/02/10 16:31:23  admin
 * Alter Prompt Completed
 *
 * Revision 1.5  2004/02/08 13:40:09  admin
 * Ego Questions Implemented.
 * Full workflow path complete in struts-config.xml
 * Took control of DataValue Objects from XDoclet
 *
 * Revision 1.4  2004/02/07 04:33:25  admin
 * First egoquestions page
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
