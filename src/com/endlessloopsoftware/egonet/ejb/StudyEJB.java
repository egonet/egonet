/**
 * <p>Copyright: Copyright (c) 2002 - 2004 </p>
 * <p>Company: Endless Loop Software</p>
 * @author Peter Schoaff
 *
 * $Id: StudyEJB.java,v 1.16 2004/05/17 00:05:24 admin Exp $
 */
package com.endlessloopsoftware.egonet.ejb;

import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

import javax.ejb.CreateException;
import javax.ejb.EJBException;
import javax.ejb.EntityBean;
import javax.ejb.EntityContext;
import javax.ejb.FinderException;
import javax.ejb.RemoveException;

import org.jboss.logging.Logger;

import com.endlessloopsoftware.egonet.InterviewPosition;
import com.endlessloopsoftware.egonet.Shared;
import com.endlessloopsoftware.egonet.Shared.AlterPromptType;
import com.endlessloopsoftware.egonet.interfaces.*;
import com.endlessloopsoftware.egonet.util.InterviewDataValue;
import com.endlessloopsoftware.egonet.util.QuestionDataValue;
import com.endlessloopsoftware.egonet.util.StudyDataValue;
import com.endlessloopsoftware.elsutils.ELSMath;

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
 *	@ejb.bean name 				= "StudyEJB"
 *		type 						= "CMP"
 *		cmp-version 				= "2.x"
 *		display-name 			= "StudyEJB"
 *		description 				= "Study EJB"
 *		view-type 				= "both"
 *		reentrant					= "true"
 *		jndi-name 				= "StudyEJBHome"
 *		local-jndi-name 		= "StudyEJBLocalHome"
 *
 *	@ejb.util
 *		generate					= "physical"
 * 
 *	@ejb.persistence
 *		table-name 				= "StudyEJB"
 * 
 *	@ejb.transaction
 *		type						= "Required"
 * 
 *	@disabled.ejb.value-object 
 *		name 						= "StudyData"
 *		match 						= "*"
 * 
 *	@ejb.pk
 *		class 						= "com.endlessloopsoftware.egonet.interfaces.StudyEJBPK"
 * 
 *	@ejb.finder 
 *		signature 				= "com.endlessloopsoftware.egonet.interfaces.StudyEJBLocal findByPrimaryKey(com.endlessloopsoftware.egonet.interfaces.StudyEJBPK pk)"
 *		description 				= "Find specific instances of this type"
 *		query 						= "SELECT OBJECT(a) FROM StudyEJB AS a WHERE a.id=?"
 * 		
 *	@ejb.finder 
 *		signature 				= "java.util.Collection findAll()"
 *		description 				= "Find all instances of this type"
 *		query 						= "SELECT OBJECT(a) FROM StudyEJB AS a"
 * 		
 *	@ejb.finder
 *		signature 				= "com.endlessloopsoftware.egonet.interfaces.StudyEJBLocal findByStudyName(java.lang.String name)"
 *		description 				= "Find specific instances of this type with matching name"
 *		query 						= "SELECT OBJECT(a) FROM StudyEJB AS a WHERE a.studyName=?1"
 * 
 */
public abstract class StudyEJB implements EntityBean
{
	final Logger logger = Logger.getLogger(getClass().getName());
	
	/**
	 * @throws CreateException Thrown if the instance could not perform 
	 * the function requested by the container because of an system-level error.
	 * 
	 * @ejb.create-method
	 */
	public StudyEJBPK ejbCreate(StudyDataValue data) throws CreateException
	{
		this.setId(data.getId());
		return null;
	}

	/**
	 * @throws CreateException Thrown if the instance could not perform 
	 * the function requested by the container because of an system-level error.
	 */
	public void ejbPostCreate(StudyDataValue data) throws CreateException
	{
		this.setStudyDataValue(data);
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
	 * @ejb.interface-method 
	 * 	view-type 		= "local"
	 * 
	 * @ejb.pk-field 
	 */
	public abstract Long getId();
	public abstract void setId(Long i);
	
	/**
	 * @ejb.persistence 
	 * 	column-name = "NUM_ALTERS"
	 * 
	 * @ejb.interface-method 
	 * 	view-type 		= "local"
	 */
	public abstract int getNumAlters();
	public abstract void setNumAlters(int i);
	
	/**
	 * @ejb.persistence 
	 * 	column-name = "NAME"
	 * 
	 * @ejb.interface-method 
	 * 	view-type 	= "local"
	 */
	public abstract String getStudyName();
	public abstract void setStudyName(String i);
	
	/**
	 * @ejb.persistence 
	 * 	column-name = "QUESTION_ORDER"
	 * 
	 * @ejb.interface-method 
	 * 	view-type 		= "local"
	 */
	public abstract Long[][] getQuestionOrder();
	public abstract void setQuestionOrder(Long[][] i);
	
   /**
    * @ejb.persistence 
    *    column-name = "UI_TYPE"
    * 
    * @ejb.interface-method 
    *    view-type      = "local"
    */
   public abstract String getUIType();
   public abstract void setUIType(String i);
   

	/*********************************************************
	 * Relationship Methods
	 */
	
	/**
	 *	@ejb.interface-method 
	 * 
	 *	@ejb.relation 
	 *		name 					= "Study-Has-Interviews"
	 *  	role-name 			= "Study-To-Interviews"
	 * 
	 * 
	 *	@disabled.ejb.value-object
	 *		aggregate				=	"com.endlessloopsoftware.egonet.util.InterviewDataValue"
	 *		aggregate-name		=	"InterviewDataValue"
	 *   	members				=	"com.endlessloopsoftware.egonet.interfaces.InterviewEJBLocal"
	 *   	members-name			=	"Interview"
	 *   	relation				=	"external"
	 *   	type					=	"Set"
	 */
	public abstract Set getInterviews();
	
	/**
	 * 	@ejb.interface-method 
	 */
	public abstract void setInterviews(Set interviews);
	
	/**
	 *	@ejb.interface-method 
	 * 
	 *	@ejb.relation 
	 * 	name 						= "Study-Has-Questions"
	 *  	role-name 			= "Study-To-Questions"
	 * 
	 *	@disabled.ejb.value-object
	 *		compose				=	"com.endlessloopsoftware.egonet.util.QuestionDataValue"
	 *		compose-name			=	"QuestionDataValue"
	 *   	members				=	"com.endlessloopsoftware.egonet.interfaces.QuestionEJBLocal"
	 *   	members-name			=	"Question"
	 *   	relation				=	"external"
	 *   	type					=	"Set"
	 */
	public abstract Set getQuestions();
	
	/**
	 * 	@ejb.interface-method 
	 */
	public abstract void setQuestions(Set questions);
	
	
	/************************************************
	 * Utility Methods
	 */
	
	/**
    *  Selects all document names from database
    *
    *  @ejb.select
    *      query       = "SELECT s.studyName FROM StudyEJB AS s"
    */
   public abstract java.util.Set ejbSelectStudyNames()
       throws FinderException;

   /**
    *  Returns Names of all Documents in Database
    *
    *  @ejb.home-method
    */
   public java.util.Set ejbHomeGetStudyNames()
   {
       Set names = null;

       try
       {
           names = ejbSelectStudyNames();
       }
       catch (Exception ex)
       {
           ex.printStackTrace();
       }

       return (names == null) ? new HashSet(0) : names;
   }

   /**************
    * @ejb.interface-method 
    *    view-type = "local"
    */
   public boolean isAppletUI()
   {
	   return (getUIType().equals(Shared.PAIR_ELICITATION) || getUIType().equals(Shared.THREE_STEP_ELICITATION));
   }

   /**************
	 * @ejb.interface-method 
	 * 	 view-type 		= "local"
	 */
	public Integer getQuestionPosition(int questionType, Long questionId)
	{
/*		logger.debug("Get Question Position => Type: " + questionType + ", Id: " + questionId);*/
		Integer rval = new Integer(Integer.MAX_VALUE);
		Long[] questionOrder = this.getQuestionOrder()[questionType];
		
		for (int i = 0; i < questionOrder.length; ++i)
		{
			try
			{
				if (questionOrder[i].equals(questionId))
				{
					rval = new Integer(i);
					break;
				}
			}
			catch (Exception ex)
			{
				logger.debug("Exception on i = " + i + "; questionOrder[i] = " + questionOrder[i]);
				ex.printStackTrace();
			}
		}
		
		return rval;
	}
	
	/**************
	 * @ejb.interface-method 
	 * 	view-type 		= "local"
	 */
	public InterviewPosition[] getInterviewTemplate()
	{
      InterviewPosition[] template = null;
      
      if (isAppletUI())
      {
         template = getAppletTemplate();
      }
      else
      {
         template = getTraditionalTemplate();
      }
      
		return template;
	}
   
   /********************
    * @ejb.interface-method
    *    view-type = "local"
    */
   public Vector getDisplayableAlterQuestions()
   {
      Vector questions = new Vector();
      Long[] alterQuestionOrder = this.getQuestionOrder()[Shared.ALTER_QUESTION];

      try
      {
         QuestionSBLocalHome  questionSBHome  = QuestionSBUtil.getLocalHome();
         QuestionSBLocal      questionSB      = questionSBHome.create();

         for (int i = 0; i < alterQuestionOrder.length; ++i)
         {
            QuestionEJBPK        questionPk      = new QuestionEJBPK(alterQuestionOrder[i], this.getId());
            QuestionDataValue    questionData    = questionSB.findDataByPrimaryKey(questionPk);
            
            if ((questionData.getAnswerType() == Shared.CATEGORICAL) &&
                (questionData.getSelectionDataValues().length <= 5))
            {
               questions.add(questionData);
            }
         }
      }
      catch (Exception e)
      {
         e.printStackTrace();
      }
      
      return questions;
   }

   private InterviewPosition[] getTraditionalTemplate()
   {
      /*************************************************************************
       * Generate Interview Template
       */
      Long[][] orders = this.getQuestionOrder();
      int numAlters = this.getNumAlters();
      int index = 0;
      int page = 0;

      int numQuestions = orders[Shared.EGO_QUESTION].length;                                          // Ego Questions
      numQuestions += numAlters;                                                                      // Alter Prompts
      numQuestions += orders[Shared.ALTER_QUESTION].length * numAlters;                               // Alter Questions
      numQuestions += ELSMath.summation(numAlters - 1) * orders[Shared.ALTER_PAIR_QUESTION].length;   // Alter Pair Questions

      InterviewPosition[] template = new InterviewPosition[numQuestions];
      logger.debug("Num Questions: " + numQuestions);

      // Ego questions first
      //       public InterviewPosition(int questionType, int primaryAlter, Long
      // questionId,
      //                int globalIndex, int globalPage, int typePage, int pageIndex)
      for (int i = 0; i < orders[Shared.EGO_QUESTION].length; ++i, ++index)
      {
         template[index] = new InterviewPosition(Shared.EGO_QUESTION, Shared.NOT_ALTER_PROMPT, -1, orders[Shared.EGO_QUESTION][i], 
                                                 index, page, 0, i);
      }
      ++page;
      logger.debug("Ego Questions: " + index);

      // Alter Questions
      for (int primaryAlter = 0; primaryAlter < numAlters; ++primaryAlter)
      {
         // Alter Prompt -- For now we only support one alter prompt question
         template[index] = new InterviewPosition(Shared.ALTER_PROMPT, Shared.LINK_TO_NONE, primaryAlter, orders[Shared.ALTER_PROMPT][0],
                                                 index, page, 0, 0);
         ++index;
         ++page;

         // Alter
         for (int questionIndex = 0; questionIndex < orders[Shared.ALTER_QUESTION].length; ++questionIndex, ++index)
         {
            template[index] = new InterviewPosition(Shared.ALTER_QUESTION, Shared.NOT_ALTER_PROMPT, primaryAlter,
                                                    orders[Shared.ALTER_QUESTION][questionIndex], index, page, 0,
                                                    questionIndex);
         }
         ++page;

         // Alter Pair
         for (int questionIndex = 0; questionIndex < orders[Shared.ALTER_PAIR_QUESTION].length; ++questionIndex)
         {
            for (int secondaryAlter = 0; secondaryAlter < primaryAlter; ++secondaryAlter, ++index)
            {
               template[index] = new InterviewPosition(Shared.ALTER_PAIR_QUESTION, Shared.NOT_ALTER_PROMPT, primaryAlter,
                                                       orders[Shared.ALTER_PAIR_QUESTION][questionIndex], index, page,
                                                       questionIndex, secondaryAlter);
            }

            if (primaryAlter > 0)
            {
               ++page;
            }
         }
      }

      return template;
   }
   
   private InterviewPosition[] getAppletTemplate()
   {
      /*************************************************************************
       * Generate Interview Template
       * This version uses no alter pair questions, instead users click on
       * applet to create links
       */
      Long[][]          orders            = this.getQuestionOrder();
      int               numAlters         = this.getNumAlters();
      int               index             = 0;
      int               page              = 0;
      AlterPromptType   defaultLinkType   = getUIType().equals(Shared.PAIR_ELICITATION) ? Shared.LINK_PAIR
                                                                                        : Shared.LINK_TO_NEXT;
   
      int numQuestions = orders[Shared.EGO_QUESTION].length; // Ego Questions
      numQuestions += numAlters; // Alter Prompts
      numQuestions += numAlters; // Alter Links
      numQuestions++;            // Correction Screen
      numQuestions += orders[Shared.ALTER_QUESTION].length * numAlters; // Alter
                                                                        // Questions
   
      InterviewPosition[] template = new InterviewPosition[numQuestions];
      logger.debug("Num Questions: " + numQuestions);
   
      // Ego questions first
      for (int i = 0; i < orders[Shared.EGO_QUESTION].length; ++i, ++index)
      {
         template[index] = new InterviewPosition(Shared.EGO_QUESTION, Shared.NOT_ALTER_PROMPT, -1,
                                                 orders[Shared.EGO_QUESTION][i], index, page, 0, i);
      }
      ++page;
      logger.debug("Ego Questions: " + index);
      
      for (int primaryAlter = 0; primaryAlter < numAlters; ++primaryAlter)
      {   
         /**********************************************************************
          * Alter Prompt, Dynamically determines whether to ask for dyads for
          * single alters
          */
         template[index] = new InterviewPosition(Shared.ALTER_PROMPT, defaultLinkType, primaryAlter,
                                                 Shared.GENERIC_ALTER_PROMPT, index, page, 0, 0);
         ++index;
         ++page;

         /**********************************************************************
          * Gives user opportunity to use applet to create adjacency links
          */
         template[index] = new InterviewPosition(Shared.ALTER_PAIR_QUESTION, Shared.NOT_ALTER_PROMPT, primaryAlter,
                                                 orders[Shared.ALTER_PAIR_QUESTION][0], index, page, 0, Shared.NO_ALTER);
         ++index;
         ++page;
      }   
      
      template[index] = new InterviewPosition(Shared.CORRECTION, Shared.NOT_ALTER_PROMPT, Shared.NO_ALTER, 
                                              Shared.GENERIC_CORRECTION, index, page, 0, Shared.NO_ALTER);
      ++index;
      ++page;
     
      for (int primaryAlter = 0; primaryAlter < numAlters; ++primaryAlter)
      {   
         // Alter
         for (int questionIndex = 0; questionIndex < orders[Shared.ALTER_QUESTION].length; ++questionIndex, ++index)
         {
            template[index] = new InterviewPosition(Shared.ALTER_QUESTION, Shared.NOT_ALTER_PROMPT, primaryAlter,
                                                    orders[Shared.ALTER_QUESTION][questionIndex], index, page, 0,
                                                    questionIndex);
         }
         ++page;
      }
      
      return template;
   }

   
   /* Value Objects BEGIN */

   public void addInterview(InterviewDataValue added)
         throws javax.ejb.FinderException
   {
      try
      {
         InterviewEJBPK pk = new InterviewEJBPK(added.getId());
         InterviewEJBLocalHome home = InterviewEJBUtil.getLocalHome();
         InterviewEJBLocal relation = home.findByPrimaryKey(pk);
         getInterviews().add(relation);
      }
      catch (Exception e)
      {
         if (e instanceof javax.ejb.FinderException)
            throw (javax.ejb.FinderException) e;
         else
            throw new javax.ejb.EJBException(e);
      }
   }

   public void removeInterview(InterviewDataValue removed)
         throws javax.ejb.RemoveException
   {
      try
      {
         InterviewEJBPK pk = new InterviewEJBPK(removed.getId());
         InterviewEJBLocalHome home = InterviewEJBUtil.getLocalHome();
         InterviewEJBLocal relation = home.findByPrimaryKey(pk);
         getInterviews().remove(relation);
      }
      catch (Exception e)
      {
         if (e instanceof javax.ejb.RemoveException)
            throw (javax.ejb.RemoveException) e;
         else
            throw new javax.ejb.EJBException(e);
      }
   }

   public void addQuestion(QuestionDataValue added) throws javax.ejb.CreateException
   {
      try
      {
         QuestionEJBPK pk = new QuestionEJBPK(added.getId(), this.getId());
         added.setPrimaryKey(pk);
         QuestionEJBLocalHome home = QuestionEJBUtil.getLocalHome();
         QuestionEJBLocal relation = home.create(added);
         getQuestions().add(relation);
      }
      catch (Exception e)
      {
         if (e instanceof javax.ejb.CreateException)
            throw (javax.ejb.CreateException) e;
         else
            throw new javax.ejb.EJBException(e);
      }
   }

   public void removeQuestion(QuestionDataValue removed) throws javax.ejb.RemoveException
   {
      try
      {
         QuestionEJBPK pk = new QuestionEJBPK(removed.getId(), this.getId());
         removed.setPrimaryKey(pk);
         QuestionEJBLocalHome home = QuestionEJBUtil.getLocalHome();
         QuestionEJBLocal relation = home.findByPrimaryKey(pk);
         getQuestions().remove(relation);
         relation.remove();
      }
      catch (Exception e)
      {
         if (e instanceof javax.ejb.RemoveException)
            throw (javax.ejb.RemoveException) e;
         else
            throw new javax.ejb.EJBException(e);
      }
   }

   public void updateQuestion(QuestionDataValue updated) throws javax.ejb.FinderException
   {
      try
      {
         QuestionEJBPK pk = new QuestionEJBPK(updated.getId(), this.getId());
         updated.setPrimaryKey(pk);
         QuestionEJBLocalHome home = QuestionEJBUtil.getLocalHome();
         QuestionEJBLocal relation = home.findByPrimaryKey(pk);
         relation.setQuestionDataValue(updated);
      }
      catch (Exception e)
      {
         if (e instanceof javax.ejb.FinderException)
            throw (javax.ejb.FinderException) e;
         else
            throw new javax.ejb.EJBException(e);
      }
   }
   
   /**
    * @ejb.interface-method 
    */
   public StudyDataValue getStudyDataValue()
   {
      StudyDataValue StudyDataValue = new StudyDataValue();
      try
      {
         StudyDataValue.setId(getId());
         StudyDataValue.setNumAlters(getNumAlters());
         StudyDataValue.setStudyName(getStudyName());
         StudyDataValue.setQuestionOrder(getQuestionOrder());
         StudyDataValue.setUIType(getUIType());
         
         StudyDataValue.clearInterviewDataValues();
         
         java.util.Iterator iInterviewDataValue = getInterviews().iterator();
         while (iInterviewDataValue.hasNext())
         {
            StudyDataValue.addInterviewDataValue(((InterviewEJBLocal) iInterviewDataValue.next())
                  .getInterviewDataValue());
         }
         
         StudyDataValue.cleanInterviewDataValue();
         StudyDataValue.clearQuestionDataValues();
         
         java.util.Iterator iQuestionDataValue = getQuestions().iterator();
         while (iQuestionDataValue.hasNext())
         {
            StudyDataValue.addQuestionDataValue(((QuestionEJBLocal) iQuestionDataValue.next()).getQuestionDataValue());
         }
         
         StudyDataValue.cleanQuestionDataValue();

      }
      catch (Exception e)
      {
         throw new javax.ejb.EJBException(e);
      }

      return StudyDataValue;
   }

   /**
    * @ejb.interface-method 
    */
   public void setStudyDataValue(StudyDataValue valueHolder)
   {

      try
      {
         setNumAlters(valueHolder.getNumAlters());
         setStudyName(valueHolder.getStudyName());
         setQuestionOrder(valueHolder.getQuestionOrder());
         setUIType(valueHolder.getUIType());

         {

            java.util.Iterator iInterviewDataValue = valueHolder.getAddedInterviewDataValues().iterator();
            while (iInterviewDataValue.hasNext())
            {
               InterviewDataValue o = (InterviewDataValue) iInterviewDataValue.next();
               addInterview(o);
            }
            
            iInterviewDataValue = valueHolder.getRemovedInterviewDataValues().iterator();
            while (iInterviewDataValue.hasNext())
            {
               InterviewDataValue o = (InterviewDataValue) iInterviewDataValue.next();
               removeInterview(o);
            }
         }
         
         // Anonymous block to allow variable declations without conflicts
         {
            java.util.Iterator iQuestionDataValue = valueHolder.getUpdatedQuestionDataValues().iterator();
            while (iQuestionDataValue.hasNext())
            {
               QuestionDataValue o = (QuestionDataValue) iQuestionDataValue.next();
               updateQuestion(o);
            }
            
            iQuestionDataValue = valueHolder.getAddedQuestionDataValues().iterator();
            while (iQuestionDataValue.hasNext())
            {
               QuestionDataValue o = (QuestionDataValue) iQuestionDataValue.next();
               addQuestion(o);
            }
            
            iQuestionDataValue = valueHolder.getRemovedQuestionDataValues().iterator();
            while (iQuestionDataValue.hasNext())
            {
               QuestionDataValue o = (QuestionDataValue) iQuestionDataValue.next();
               removeQuestion(o);
            }
         }
      }
      catch (Exception e)
      {
         throw new javax.ejb.EJBException(e);
      }
   }

   /* Value Objects END */
}


/**
 * $Log: StudyEJB.java,v $
 * Revision 1.16  2004/05/17 00:05:24  admin
 * Node coloring in alter question screens
 *
 * Revision 1.15  2004/05/14 15:06:08  admin
 * Added Correction mode to Applet
 *
 * Revision 1.14  2004/04/10 23:09:21  admin
 * Implemented Three Step Elicitation
 *   Added Position Forward Action
 *   Store current prompt type as part of answer and interview position
 *
 * Using custom version of FR Layout to try to keep labels from overlapping
 * borders
 *
 * Revision 1.13  2004/04/06 14:42:13  admin
 * Completed Applet Linking
 *
 * Revision 1.12  2004/04/05 01:16:43  admin
 * Modifying to use new Applet Linking Interface
 * Revision 1.11 2004/03/28 17:30:07 admin Display
 * applet in complete screen Mark interviews complete Only return names of
 * complete interviews to client
 * 
 * Revision 1.10 2004/03/22 20:09:38 admin Support for EgoClient selecting
 * studies and interviews
 * 
 * Revision 1.9 2004/03/18 15:23:40 admin Recovers previous interview state
 * 
 * Revision 1.8 2004/03/12 18:05:28 admin Applet now works under Windows IE.
 * Fixed layout issues related to struts-layout converting spaces to nbsp Using
 * Servlet for applet/server communications
 * 
 * Revision 1.7 2004/02/15 14:59:01 admin Fixing Header Tags
 * 
 * Revision 1.6 2004/02/15 14:37:38 admin Displaying network graph on web pages
 * 
 * Revision 1.5 2004/02/10 16:31:23 admin Alter Prompt Completed
 * 
 * Revision 1.4 2004/02/08 13:40:09 admin Ego Questions Implemented. Full
 * workflow path complete in struts-config.xml Took control of DataValue Objects
 * from XDoclet
 * 
 * Revision 1.3 2004/02/07 04:33:25 admin First egoquestions page
 * 
 * Revision 1.2 2004/01/30 23:31:08 admin Using struts-layout
 * 
 * Revision 1.1 2004/01/03 15:44:20 admin Adding new EJB files. More
 * relationships and use of value objects.
 * 
 * Revision 1.1.1.1 2003/12/18 21:17:18 admin Imported sources
 * 
 * Revision 1.3 2003/12/10 20:05:42 admin fixing header
 * 
 * Revision 1.2 2003/12/10 20:02:58 admin removing generated files
 *  
 */
