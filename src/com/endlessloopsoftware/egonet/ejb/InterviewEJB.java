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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.ejb.CreateException;
import javax.ejb.EJBException;
import javax.ejb.EntityBean;
import javax.ejb.EntityContext;
import javax.ejb.FinderException;
import javax.ejb.RemoveException;

import org.jboss.logging.Logger;

import com.endlessloopsoftware.egonet.AlterPair;
import com.endlessloopsoftware.egonet.InterviewPosition;
import com.endlessloopsoftware.egonet.Shared;
import com.endlessloopsoftware.egonet.Shared.AlterPromptType;
import com.endlessloopsoftware.egonet.interfaces.AnswerEJBLocal;
import com.endlessloopsoftware.egonet.interfaces.AnswerEJBLocalHome;
import com.endlessloopsoftware.egonet.interfaces.AnswerEJBPK;
import com.endlessloopsoftware.egonet.interfaces.AnswerEJBUtil;
import com.endlessloopsoftware.egonet.interfaces.InterviewEJBLocal;
import com.endlessloopsoftware.egonet.interfaces.InterviewEJBPK;
import com.endlessloopsoftware.egonet.interfaces.StudyEJBLocal;
import com.endlessloopsoftware.egonet.util.AnswerDataValue;
import com.endlessloopsoftware.egonet.util.InterviewDataValue;

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
 * 	@ejb.bean 
 *    name              = "InterviewEJB"
 *    type              = "CMP"
 *    cmp-version       = "2.x"
 *    display-name 		= "InterviewEJB"
 *    description       = "Interview EJB"
 *    view-type 			= "both"
 *    jndi-name 			= "InterviewEJBHome"
 *    local-jndi-name   = "InterviewEJBLocalHome"
 *
 * @ejb.util
 *    generate          = "physical"
 * 
 * @ejb.persistence
 *    table-name        = "InterviewEJB"
 * 
 * 	@ejb.transaction
 * 		type					= "Required"
 * 
 * 	@ejb.pk
 * 		class 					= "com.endlessloopsoftware.egonet.interfaces.InterviewEJBPK"
 * 
 * 	@ejb.finder 
 *    signature 			= "com.endlessloopsoftware.egonet.interfaces.InterviewEJBLocal findByPrimaryKey(com.endlessloopsoftware.egonet.interfaces.InterviewEJBPK pk)"
 * 		description 			= "Find single instance matching primary key"
 * 		query 					= "SELECT OBJECT(a) FROM InterviewEJB AS a WHERE a.id=?"
 * 		
 * 	@ejb.finder 
 *    signature         = "com.endlessloopsoftware.egonet.interfaces.InterviewEJBLocal findByStudyAndEgo(com.endlessloopsoftware.egonet.interfaces.StudyEJBLocal study, java.lang.String firstName, java.lang.String lastName)"
 * 		description 			= "Find interview for a given study and user"
 *    view-type         = "local"
 * 		query 					= "SELECT OBJECT(a) FROM InterviewEJB AS a WHERE a.study=?1 AND a.firstName=?2 AND a.lastName=?3"
 * 		
 */
@SuppressWarnings({"unchecked"})
public abstract class InterviewEJB
   implements EntityBean
{
   private final transient Logger logger              = Logger.getLogger(this.getClass().getName());
   private EntityContext          _context;
   private InterviewDataValue     _interviewDataValue = null;

   /**
    *	@param data	Initial Interview information
    * 	@throws CreateException Thrown if the instance could not perform 
    * 			the function requested by the container because of an system-level error.
    * 
    * @ejb.create-method
    */
   public InterviewEJBPK ejbCreate(InterviewDataValue data) throws CreateException
   {
      this.setId(new Long(System.currentTimeMillis()));
      return null;
   }

   /**
    * @param data
    *           Initial Interview information
    * @throws CreateException
    *            Thrown if the instance could not perform the function requested
    *            by the container because of an system-level error.
    */
   public void ejbPostCreate(InterviewDataValue data) throws CreateException
   {
      this.setInterviewDataValue(data);
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
      _context = newContext;
   }

   public void unsetEntityContext() throws EJBException
   {
      _context = null;
   }

   /****************************************************************************
    * Persistence Methods
    */

   /**
    * @ejb.persistence 
    *   column-name = "ID"
    * 
    * @ejb.pk-field 
    * 
    * @ejb.interface-method
    *    view = "local"
    */
   public abstract Long getId();

   public abstract void setId(Long i);

   /**
    * @ejb.persistence 
    *   column-name = "FIRST_NAME"
    * 
    * @ejb.interface-method
    *    view = "local"
    */
   public abstract String getFirstName();

   public abstract void setFirstName(String i);

   /**
    * @ejb.persistence 
    *   column-name = "LAST_NAME"
    * 
    * @ejb.interface-method
    *    view = "local"
    */
   public abstract String getLastName();

   public abstract void setLastName(String i);

   /**
    * @ejb.interface-method
    *    view = "local"
    * 
    * @ejb.persistence 
    *   column-name = "COMPLETE"
    */
   public abstract Boolean getComplete();

   /**
    * @ejb.interface-method
    *    view = "local"
    */
   public abstract void setComplete(Boolean i);

   /**
    * @ejb.interface-method
    * 
    * @ejb.persistence 
    *   column-name = "ALTERS"
    */
   public abstract String[] getAlters();

   public abstract void setAlters(String[] i);

   /**
    * @ejb.interface-method
    * 
    * @ejb.persistence 
    * 	column-name = "ADJACENCY_MATRIX"
    */
   public abstract int[][] getAdjacencyMatrix();

   /**
    * @ejb.interface-method
    */
   public abstract void setAdjacencyMatrix(int[][] i);

   /****************************************************************************
    * Relationship Methods
    */

   /**
    * 	@ejb.interface-method 
    * 
    *	@ejb.relation 
    * 		name            = "Study-Has-Interviews"
    *  	role-name       = "Interviews-To-Study"
    * 
    *	@jboss.relation
    * 		fk-column 			= "STUDY"
    * 		related-pk-field	= "id"
    */
   public abstract StudyEJBLocal getStudy();

   /**
    * 	@ejb.interface-method 
    */
   public abstract void setStudy(StudyEJBLocal study);

   /**
    * 	@ejb.interface-method 
    * 
    *	@ejb.relation 
    *   name 				= "Interview-Has-Answers"
    *   role-name 			= "Interview-To-Answers"
    * 
    * 
    *	@comment_ejb.value-object
    *   compose				=	"com.endlessloopsoftware.egonet.data.AnswerDataValue"
    *   compose-name		=	"AnswerDataValue"
    *   members				=	"com.endlessloopsoftware.egonet.interfaces.AnswerEJBLocal"
    *   members-name		=	"Answer"
    *   relation			=	"external"
    *   type					=	"Set"
    */
   public abstract Set getAnswers();

   /**
    * 	@ejb.interface-method 
    */
   public abstract void setAnswers(Set answers);

   /****************************************************************************
    * Utility Methods
    */

   /***************************
    * Get name in nice format
    * 
    * @ejb.interface-method
    * 	 view-type = "local"
    */
   public String[] getName()
   {
      return new String[] { getFirstName(), getLastName()};
   }

   /**
    *  Selects all document names from database
    *
    *  @ejb.select
    *      query       = "SELECT OBJECT(a) FROM AnswerEJB AS a WHERE a.interview=?1 AND a.answered=TRUE"
    */
   public abstract java.util.Set ejbSelectAnsweredAnswersByInterview(InterviewEJBLocal interview)
       throws FinderException;

   /**************************************
    * Find current position in interview by iterating through answers
    *
    * @ejb.interface-method 
    *    view-type = "local"
    *
    * @param   template    Array of positions defining individual questions in interview
    * @return  first unanswered question
    */
   public InterviewPosition calculateInterviewPosition(InterviewPosition[] template)
   {
      List              answerIds         = new ArrayList(0);
      InterviewPosition firstUnanswered   = null;
      Long              questionId;
      boolean           elicit3           = getStudy().getUIType().equals(Shared.THREE_STEP_ELICITATION);
      AlterPromptType   lastPromptType    = Shared.LINK_TO_NONE;

      logger.debug("Calculate Interview Position");
      try
      {
         boolean applet = getStudy().isAppletUI();

         Set pairs = ejbSelectAnsweredAnswersByInterview((InterviewEJBLocal) _context.getEJBLocalObject());

         answerIds = new ArrayList(pairs.size());

         logger.debug("Found " + pairs.size() + " completed answers");

         // Convert to AnswerIds
         for (Iterator it = pairs.iterator(); it.hasNext();)
         {
            AnswerDataValue answer = ((AnswerEJBLocal) it.next()).getAnswerDataValue(getStudy());
            
            logger.debug("Answer: " + answer);

            if (applet && (answer.getQuestion().getQuestionType() == Shared.ALTER_PROMPT))
               questionId = Shared.GENERIC_ALTER_PROMPT;
            else if (applet && (answer.getQuestion().getQuestionType() == Shared.ALTER_PAIR_QUESTION) && answer.getAlters().equals(Shared.NO_ALTERS))
               questionId = Shared.GENERIC_CORRECTION;
            else if (applet && (answer.getQuestion().getQuestionType() == Shared.CORRECTION))
               questionId = Shared.GENERIC_CORRECTION;
            else
               questionId = answer.getQuestion().getId();
            
            logger.debug("*)()*)() Modified Answer: " + answer);
            
            answerIds.add(new AnswerId(questionId, answer.getAlters(), answer.getAlterPromptState()));
         }

         Collections.sort(answerIds);
      }
      catch (FinderException e1)
      {
         // No Matching answers, not an error
      }

      int fromStart = 0;
      for (int i = 0; i < template.length; ++i)
      {
         InterviewPosition position = template[i];
         AnswerId id = new AnswerId(position.getQuestionId(), position.getAlterPair(), position.getAlterPromptType());

         int index = Collections.binarySearch(answerIds, id);
         if (index < 0)
         {
            logger.debug("Unmatched " + id);
            firstUnanswered = position;
            break;
         }
         else
         {
            logger.debug("Matched " + id);
            
            if (elicit3)
            {
               lastPromptType = ((AnswerId) answerIds.get(index))._promptType;
               logger.debug("AlterPromptType is " + lastPromptType);
            }
            ++fromStart;
         }
      }

      if (fromStart == 0)
      {
         // No matches, select first question
         firstUnanswered = template[0];
      }
      else if (firstUnanswered == null)
      {
         // Completed Interview
         firstUnanswered = new InterviewPosition(Shared.COMPLETE, Shared.NOT_ALTER_PROMPT, -1, null, 0, 0,
                                                 Shared.COMPLETE, 0);
      }
      else if (elicit3)
      {
         logger.debug("Next Question Type is " + Shared.getTypeName(firstUnanswered.getQuestionType()) +
                      "; Prompt Type is " + lastPromptType);
         if (firstUnanswered.getQuestionType() == Shared.ALTER_PAIR_QUESTION)
         {
            logger.debug("Alter Pair");
            firstUnanswered.setAlterPromptType(lastPromptType);
         }
         else if (firstUnanswered.getQuestionType() == Shared.ALTER_PROMPT)
         {
            AlterPromptType newtype = Shared.getNextPromptType(lastPromptType, getStudy().getUIType());
            logger.debug("Alter Prompt: " + newtype);
            firstUnanswered.setAlterPromptType(newtype);
         }
 
         logger.debug("Next Prompt Type is " + firstUnanswered.getAlterPromptType());
      }

      logger.debug("Found " + fromStart + " consecutive answers from start of interview");

      return firstUnanswered;
   }
   
   /********************
    * Answer Id Class used solely to calculate position in resumed interview
    * @author Peter Schoaff
    * @copyright (c) 2004 Endless Loop Software, Inc.
    *
    */
   private class AnswerId
      implements Comparable
   {
      private final Long            _questionId;
      private final AlterPair       _alters;
      private final AlterPromptType _promptType;

      public AnswerId(Long questionId, AlterPair alters, AlterPromptType promptType)
      {
         _questionId    = questionId;
         _alters        = alters;
         _promptType    = promptType;
      }

      public boolean match(Long questionId, AlterPair alters)
      {
         return (questionId.equals(_questionId) && alters.equals(_alters));
      }

      /*
       * (non-Javadoc)
       * 
       * @see java.lang.Comparable#compareTo(java.lang.Object)
       */
      public int compareTo(Object o)
      {
         AnswerId that = (AnswerId) o;

         			logger.debug("Comparing:");
         			logger.debug(" " + this);
         			logger.debug(" " + that);

         int rval = this._questionId.compareTo(that._questionId);

         if (rval == 0)
            rval = this._alters.compareTo(that._alters);

         return rval;
      }

      public String toString()
      {
         StringBuffer str = new StringBuffer("{");

         str.append("qid=" + _questionId + " " + "alters=" + _alters.getPrimaryAlter() + ":"
                    + _alters.getSecondaryAlter());
         str.append('}');

         return (str.toString());
      }
   }

   
   /****************************************************************************
    * Data Object Methods
    */

   /**
    * @ejb.interface-method
    *    view-type = "local"
    */
   public void addAnswer(AnswerDataValue added) throws javax.ejb.CreateException
   {
      try
      {
         AnswerEJBLocalHome home = AnswerEJBUtil.getLocalHome();
         AnswerEJBLocal relation = home.create(added);
         getAnswers().add(relation);
      }
      catch (Exception e)
      {
         if (e instanceof javax.ejb.CreateException)
            throw (javax.ejb.CreateException) e;
         else
            throw new javax.ejb.EJBException(e);
      }
   }

   /**
    * @ejb.interface-method
    *    view-type = "local"
    */
   public void removeAnswer(AnswerDataValue removed) throws javax.ejb.RemoveException
   {
      try
      {
         AnswerEJBPK pk = new AnswerEJBPK(removed.getId());
         AnswerEJBLocalHome home = AnswerEJBUtil.getLocalHome();
         AnswerEJBLocal relation = home.findByPrimaryKey(pk);
         getAnswers().remove(relation);
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

   /**
    *	@ejb.interface-method
    *		view-type = "local"
    */
   public void updateAnswer(AnswerDataValue updated) throws javax.ejb.FinderException
   {
      try
      {
         AnswerEJBPK pk = new AnswerEJBPK(updated.getId());
         AnswerEJBLocalHome home = AnswerEJBUtil.getLocalHome();
         AnswerEJBLocal relation = home.findByPrimaryKey(pk);
         relation.setAnswerDataValue(updated);
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
   public com.endlessloopsoftware.egonet.util.InterviewDataValue getInterviewDataValue()
   {
      _interviewDataValue = new com.endlessloopsoftware.egonet.util.InterviewDataValue();
      try
      {
         _interviewDataValue.setId(getId());
         _interviewDataValue.setFirstName(getFirstName());
         _interviewDataValue.setLastName(getLastName());
         _interviewDataValue.setComplete(getComplete());
         _interviewDataValue.setAlters(getAlters());
         _interviewDataValue.setStudy(getStudy());
         _interviewDataValue.setAdjacencyMatrix(getAdjacencyMatrix());

         // Do Answer Data Values
         _interviewDataValue.clearAnswerDataValues();

         java.util.Iterator iAnswerDataValue = getAnswers().iterator();

         while (iAnswerDataValue.hasNext())
         {
            AnswerDataValue value = ((AnswerEJBLocal) iAnswerDataValue.next()).getAnswerDataValue(getStudy());
            value.setStudy(getStudy());
            _interviewDataValue.addAnswerDataValue(value);
         }

         _interviewDataValue.cleanAnswerDataValue();
      }
      catch (Exception e)
      {
         throw new javax.ejb.EJBException(e);
      }

      return _interviewDataValue;
   }

   /**
    * @ejb.interface-method 
    */
   public void setInterviewDataValue(InterviewDataValue valueHolder)
   {
      try
      {
         setFirstName(valueHolder.getFirstName());
         setLastName(valueHolder.getLastName());
         setComplete(valueHolder.getComplete());
         setAlters(valueHolder.getAlters());
         setAdjacencyMatrix(valueHolder.getAdjacencyMatrix());

         // Anonymous block to allow variable declations without conflicts
         {
            java.util.Iterator iAnswerDataValue = valueHolder.getUpdatedAnswerDataValues().iterator();
            while (iAnswerDataValue.hasNext())
            {
               AnswerDataValue o = (AnswerDataValue) iAnswerDataValue.next();
               updateAnswer(o);
            }

            iAnswerDataValue = valueHolder.getAddedAnswerDataValues().iterator();
            while (iAnswerDataValue.hasNext())
            {
               AnswerDataValue o = (AnswerDataValue) iAnswerDataValue.next();
               addAnswer(o);
            }

            iAnswerDataValue = valueHolder.getRemovedAnswerDataValues().iterator();
            while (iAnswerDataValue.hasNext())
            {
               AnswerDataValue o = (AnswerDataValue) iAnswerDataValue.next();
               removeAnswer(o);
            }
         }
      }
      catch (Exception e)
      {
         throw new javax.ejb.EJBException(e);
      }
   }
	
}


/**
 * $Log: InterviewEJB.java,v $
 * Revision 1.17  2004/05/26 12:35:53  admin
 * Adding tutorial
 *
 * Revision 1.16  2004/05/17 00:05:24  admin
 * Node coloring in alter question screens
 *
 * Revision 1.15  2004/04/10 23:09:21  admin
 * Implemented Three Step Elicitation
 *   Added Position Forward Action
 *   Store current prompt type as part of answer and interview position
 *
 * Using custom version of FR Layout to try to keep labels from overlapping
 * borders
 *
 * Revision 1.14  2004/04/06 14:42:13  admin
 * Completed Applet Linking
 *
 * Revision 1.13  2004/04/05 21:23:57  admin
 * Can't cache InterviewDataValue since matrix is set from another session.
 * Applet Linking now works.
 *
 * Revision 1.12  2004/04/05 20:32:20  admin
 * Modifying workflow to use applet for linking
 *
 * Revision 1.11  2004/04/05 01:16:43  admin
 * Modifying to use new Applet Linking Interface
 *
 * Revision 1.10  2004/04/01 15:10:56  admin
 * Preparing to tag default UI version
 *
 * Revision 1.9  2004/03/28 17:30:06  admin
 * Display applet in complete screen
 * Mark interviews complete
 * Only return names of complete interviews to client
 *
 * Revision 1.8  2004/03/22 20:09:38  admin
 * Support for EgoClient selecting studies and interviews
 *
 * Revision 1.7  2004/03/18 18:21:47  admin
 * Fixed bugs with calculating resume position
 *
 * Revision 1.6  2004/03/18 15:23:40  admin
 * Recovers previous interview state
 *
 * Revision 1.5  2004/03/12 18:05:27  admin
 * Applet now works under Windows IE.
 * Fixed layout issues related to struts-layout converting spaces to nbsp
 * Using Servlet for applet/server communications
 *
 * Revision 1.4  2004/02/15 14:59:00  admin
 * Fixing Header Tags
 *
 * Revision 1.3  2004/02/10 16:31:23  admin
 * Alter Prompt Completed
 *
 * Revision 1.2  2004/02/07 04:33:25  admin
 * First egoquestions page
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
