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

import java.util.Set;

import javax.ejb.CreateException;
import javax.ejb.EJBException;
import javax.ejb.EntityBean;
import javax.ejb.EntityContext;
import javax.ejb.RemoveException;

import com.endlessloopsoftware.egonet.interfaces.*;
import com.endlessloopsoftware.egonet.util.AnswerDataValue;
import com.endlessloopsoftware.egonet.util.QuestionDataValue;
import com.endlessloopsoftware.egonet.util.SelectionDataValue;

/**
 * XDoclet-based CMP entity bean. This class must be declared
 * <code>public abstract</code> because the concrete class will be implemented
 * by the CMP provider's tooling.<br>
 * 
 * To generate code: <br>
 * <ul>
 * <li> Add Standard EJB module to XDoclet project properties
 * <li> Customize XDoclet configuration
 * <li> Run XDoclet
 * </ul>
 * <br>
 * Please see the included XDoclet Overview and the XDoclet Reference in the
 * help system for details
 * 
 * @ejb.bean name = "QuestionEJB" type = "CMP" cmp-version = "2.x" display-name =
 *           "QuestionEJB" description = "Question EJB" view-type = "both"
 *           reentrant = "true" jndi-name = "QuestionEJBHome" local-jndi-name =
 *           "QuestionEJBLocalHome"
 * 
 * @ejb.util generate = "physical"
 * 
 * @ejb.persistence table-name = "QuestionEJB"
 * 
 * @ejb.transaction type = "Required"
 * 
 * @ejb.pk class = "com.endlessloopsoftware.egonet.interfaces.QuestionEJBPK"
 * 
 * @ejb.finder signature =
 *             "com.endlessloopsoftware.egonet.interfaces.QuestionEJBLocal
 *             findByPrimaryKey(com.endlessloopsoftware.egonet.interfaces.QuestionEJBPK
 *             pk)" description = "Find all instances of this type" query =
 *             "SELECT OBJECT(a) FROM QuestionEJB AS a WHERE a.id=?"
 * 
 */
@SuppressWarnings({"unchecked"})
public abstract class QuestionEJB implements EntityBean {
	private QuestionDataValue QuestionDataValue = null;

	/**
	 * 
	 * @throws CreateException
	 *             Thrown if the instance could not perform the function
	 *             requested by the container because of an system-level error.
	 * 
	 * @ejb.create-method
	 */
	public QuestionEJBPK ejbCreate(QuestionDataValue data) throws CreateException {
		this.setId(data.getId());
		this.setStudyId(data.getStudyId());
		return null;
	}

	/**
	 * 
	 * @throws CreateException
	 *             Thrown if the instance could not perform the function
	 *             requested by the container because of an system-level error.
	 */
	public void ejbPostCreate(QuestionDataValue data) throws CreateException {
		this.setQuestionDataValue(data);
	}

	public void ejbActivate() throws EJBException {
	}

	public void ejbPassivate() throws EJBException {
	}

	public void ejbLoad() throws EJBException {
	}

	public void ejbStore() throws EJBException {
	}

	public void ejbRemove() throws EJBException, RemoveException {
	}

	public void setEntityContext(EntityContext newContext) throws EJBException {
	}

	public void unsetEntityContext() throws EJBException {
	}

	/***************************************************************************
	 * Persistence Methods
	 */

	/**
	 * @ejb.persistence column-name = "ID"
	 * 
	 * @ejb.interface-method
	 * 
	 * @ejb.pk-field
	 */
	public abstract Long getId();

	public abstract void setId(Long i);

	/**
	 * @ejb.persistence column-name = "STUDY_ID"
	 * 
	 * @ejb.pk-field
	 */
	public abstract Long getStudyId();

	public abstract void setStudyId(Long i);

	/**
	 * @ejb.persistence column-name = "CENTRAL_MARKER"
	 */
	public abstract boolean getCentralMarker();

	public abstract void setCentralMarker(boolean i);

	/**
	 * @ejb.persistence column-name = "QUESTION_TYPE"
	 * 
	 * @ejb.interface-method view-type = "local"
	 */
	public abstract int getQuestionType();

	public abstract void setQuestionType(int i);

	/**
	 * @ejb.persistence column-name = "ANSWER_TYPE"
	 * 
	 * @ejb.interface-method view-type = "local"
	 */
	public abstract int getAnswerType();

	public abstract void setAnswerType(int i);

	/**
	 * @ejb.persistence column-name = "TITLE"
	 * 
	 * @ejb.interface-method view-type = "local"
	 */
	public abstract String getTitle();

	public abstract void setTitle(String i);

	/**
	 * @ejb.persistence column-name = "TEXT" jdbc-type = "VARCHAR" sql-type =
	 *                  "TEXT"
	 * 
	 * @ejb.interface-method view-type = "local"
	 */
	public abstract String getText();

	public abstract void setText(String i);

	/**
	 * @ejb.persistence column-name = "CITATION"
	 */
	public abstract String getCitation();

	public abstract void setCitation(String i);

	/***************************************************************************
	 * Data Object Methods
	 */

	/* Value Objects BEGIN */

	/**
	 * @ejb.interface-method view-type = "local"
	 */
	public void addAnswer(AnswerDataValue added) throws javax.ejb.FinderException {
		try {
			AnswerEJBPK pk = new AnswerEJBPK(added.getId());

			AnswerEJBLocalHome home = AnswerEJBUtil.getLocalHome();

			AnswerEJBLocal relation = home.findByPrimaryKey(pk);
			getAnswers().add(relation);
		} catch (Exception e) {
			if (e instanceof javax.ejb.FinderException)
				throw (javax.ejb.FinderException) e;
			else
				throw new javax.ejb.EJBException(e);
		}
	}

	/**
	 * @ejb.interface-method view-type = "local"
	 */
	public void removeAnswer(AnswerDataValue removed) throws javax.ejb.RemoveException {
		try {
			AnswerEJBPK pk = new AnswerEJBPK(removed.getId());

			AnswerEJBLocalHome home = AnswerEJBUtil.getLocalHome();

			AnswerEJBLocal relation = home.findByPrimaryKey(pk);
			getAnswers().remove(relation);
		} catch (Exception e) {
			if (e instanceof javax.ejb.RemoveException)
				throw (javax.ejb.RemoveException) e;
			else
				throw new javax.ejb.EJBException(e);
		}
	}

	/**
	 * @ejb.interface-method view-type = "local"
	 */
	public void addSelection(SelectionDataValue added) throws javax.ejb.CreateException {
		try {

			SelectionEJBLocalHome home = SelectionEJBUtil.getLocalHome();

			SelectionEJBLocal relation = home.create(added);
			getSelections().add(relation);
		} catch (Exception e) {
			if (e instanceof javax.ejb.CreateException)
				throw (javax.ejb.CreateException) e;
			else
				throw new javax.ejb.EJBException(e);
		}
	}

	/**
	 * @ejb.interface-method view-type = "local"
	 */
	public void removeSelection(SelectionDataValue removed) throws javax.ejb.RemoveException {
		try {
			SelectionEJBPK pk = new SelectionEJBPK(removed.getId());

			SelectionEJBLocalHome home = SelectionEJBUtil.getLocalHome();

			SelectionEJBLocal relation = home.findByPrimaryKey(pk);
			getSelections().remove(relation);
			relation.remove();
		} catch (Exception e) {
			if (e instanceof javax.ejb.RemoveException)
				throw (javax.ejb.RemoveException) e;
			else
				throw new javax.ejb.EJBException(e);
		}
	}

	/**
	 * @ejb.interface-method view-type = "local"
	 */
	public void updateSelection(SelectionDataValue updated) throws javax.ejb.FinderException {
		try {
			SelectionEJBPK pk = new SelectionEJBPK(updated.getId());

			SelectionEJBLocalHome home = SelectionEJBUtil.getLocalHome();

			SelectionEJBLocal relation = home.findByPrimaryKey(pk);
			relation.setSelectionDataValue(updated);
		} catch (Exception e) {
			if (e instanceof javax.ejb.FinderException)
				throw (javax.ejb.FinderException) e;
			else
				throw new javax.ejb.EJBException(e);
		}
	}

	/**
	 * @ejb.interface-method view-type = "local"
	 */
	public QuestionDataValue getQuestionDataValue() {
		QuestionEJBPK pk = new QuestionEJBPK(getId(), getStudyId());
		QuestionDataValue = new QuestionDataValue(pk);
		try {
			QuestionDataValue.setId(getId());
			QuestionDataValue.setCentralMarker(getCentralMarker());
			QuestionDataValue.setQuestionType(getQuestionType());
			QuestionDataValue.setAnswerType(getAnswerType());
			QuestionDataValue.setTitle(getTitle());
			QuestionDataValue.setText(getText());
			QuestionDataValue.setCitation(getCitation());
			QuestionDataValue.clearAnswerDataValues();
			java.util.Iterator iAnswerDataValue = getAnswers().iterator();

			while (iAnswerDataValue.hasNext()) {
				QuestionDataValue.addAnswerDataValue(((AnswerEJBLocal) iAnswerDataValue.next())
						.getAnswerDataValue(getStudy()));
			}

			QuestionDataValue.cleanAnswerDataValue();

			if (getQuestionLink() != null)
				QuestionDataValue.setQuestionLinkDataValue(getQuestionLink()
						.getQuestionLinkDataValue());
			QuestionDataValue.clearSelectionDataValues();
			java.util.Iterator iSelectionDataValue = getSelections().iterator();

			while (iSelectionDataValue.hasNext()) {
				QuestionDataValue.addSelectionDataValue(((SelectionEJBLocal) iSelectionDataValue
						.next()).getSelectionDataValue());
			}
			QuestionDataValue.cleanSelectionDataValue();

		} catch (Exception e) {
			throw new javax.ejb.EJBException(e);
		}

		return QuestionDataValue;
	}

	/**
	 * @ejb.interface-method view-type = "local"
	 */
	public void setQuestionDataValue(QuestionDataValue valueHolder) {
		try {
			setCentralMarker(valueHolder.getCentralMarker());
			setQuestionType(valueHolder.getQuestionType());
			setAnswerType(valueHolder.getAnswerType());
			setTitle(valueHolder.getTitle());
			setText(valueHolder.getText());
			setCitation(valueHolder.getCitation());

			{

				java.util.Iterator iAnswerDataValue = valueHolder.getAddedAnswerDataValues()
						.iterator();
				while (iAnswerDataValue.hasNext()) {
					AnswerDataValue o = (AnswerDataValue) iAnswerDataValue.next();
					addAnswer(o);
				}
				iAnswerDataValue = valueHolder.getRemovedAnswerDataValues().iterator();
				while (iAnswerDataValue.hasNext()) {
					AnswerDataValue o = (AnswerDataValue) iAnswerDataValue.next();
					removeAnswer(o);
				}
			}
			{
				// Checks for null aggregate
				if (valueHolder.getQuestionLinkDataValue() != null) {
					QuestionLinkEJBPK pk = new QuestionLinkEJBPK(valueHolder
							.getQuestionLinkDataValue().getId());

					QuestionLinkEJBLocalHome home = QuestionLinkEJBUtil.getLocalHome();

					QuestionLinkEJBLocal relation = home.findByPrimaryKey(pk);
					setQuestionLink(relation);
				} else {
					setQuestionLink(null);
				}
			}
			// Anonymous block to allow variable declations without conflicts
			{

				java.util.Iterator iSelectionDataValue = valueHolder
						.getUpdatedSelectionDataValues().iterator();
				while (iSelectionDataValue.hasNext()) {
					SelectionDataValue o = (SelectionDataValue) iSelectionDataValue.next();
					updateSelection(o);
				}
				iSelectionDataValue = valueHolder.getAddedSelectionDataValues().iterator();
				while (iSelectionDataValue.hasNext()) {
					SelectionDataValue o = (SelectionDataValue) iSelectionDataValue.next();
					addSelection(o);
				}
				iSelectionDataValue = valueHolder.getRemovedSelectionDataValues().iterator();
				while (iSelectionDataValue.hasNext()) {
					SelectionDataValue o = (SelectionDataValue) iSelectionDataValue.next();
					removeSelection(o);
				}
			}
		} catch (Exception e) {
			throw new javax.ejb.EJBException(e);
		}
	}

	/* Value Objects END */

	/***************************************************************************
	 * Relationship Methods
	 */

	/**
	 * @ejb.interface-method
	 * 
	 * @ejb.relation name = "Question-Has-Selections" role-name =
	 *               "Question-To-Selections"
	 * 
	 * 
	 * @ejb.value-object compose =
	 *                   "com.endlessloopsoftware.egonet.util.SelectionDataValue"
	 *                   compose-name = "SelectionDataValue" members =
	 *                   "com.endlessloopsoftware.egonet.interfaces.SelectionEJBLocal"
	 *                   members-name = "Selection" relation = "external" type =
	 *                   "Set"
	 */
	public abstract Set getSelections();

	/**
	 * @ejb.interface-method
	 */
	public abstract void setSelections(Set selections);

	/**
	 * @ejb.interface-method
	 * 
	 * @ejb.relation name = "Question-Has-Answers" role-name =
	 *               "Question-To-Answers"
	 * 
	 * 
	 * @ejb.value-object aggregate =
	 *                   "com.endlessloopsoftware.egonet.util.AnswerDataValue"
	 *                   aggregate-name = "AnswerDataValue" members =
	 *                   "com.endlessloopsoftware.egonet.interfaces.AnswerEJBLocal"
	 *                   members-name = "Answer" relation = "external" type =
	 *                   "Set"
	 */
	public abstract Set getAnswers();

	/**
	 * @ejb.interface-method
	 */
	public abstract void setAnswers(Set answers);

	/**
	 * @ejb.interface-method
	 * 
	 * @ejb.relation name = "Study-Has-Questions" role-name =
	 *               "Question-To-Study" cascade-delete = "yes"
	 * 
	 * @jboss.relation fk-column = "STUDY" related-pk-field = "id"
	 * 
	 */
	public abstract StudyEJBLocal getStudy();

	/**
	 * @ejb.interface-method
	 */
	public abstract void setStudy(StudyEJBLocal study);

	/**
	 * @ejb.interface-method
	 * 
	 * @ejb.relation name = "Question-Has-QuestionLink" role-name =
	 *               "Question-To-QuestionLink"
	 * 
	 * @jboss.relation fk-column = "QUESTION_LINK" related-pk-field = "id"
	 * 
	 * @ejb.value-object aggregate =
	 *                   "com.endlessloopsoftware.egonet.data.QuestionLinkDataValue"
	 *                   aggregate-name = "QuestionLinkDataValue" members =
	 *                   "com.endlessloopsoftware.egonet.interfaces.QuestionLinkEJBLocal"
	 *                   members-name = "QuestionLink" relation = "external"
	 */
	public abstract QuestionLinkEJBLocal getQuestionLink();

	/**
	 * @ejb.interface-method
	 */
	public abstract void setQuestionLink(QuestionLinkEJBLocal questionLink);

}

/**
 * $Log: QuestionEJB.java,v $ Revision 1.12 2004/05/14 15:06:07 admin Added
 * Correction mode to Applet
 * 
 * Revision 1.11 2004/04/05 01:16:43 admin Modifying to use new Applet Linking
 * Interface
 * 
 * Revision 1.10 2004/04/01 15:10:56 admin Preparing to tag default UI version
 * 
 * Revision 1.9 2004/03/28 17:30:06 admin Display applet in complete screen Mark
 * interviews complete Only return names of complete interviews to client
 * 
 * Revision 1.8 2004/03/12 18:05:28 admin Applet now works under Windows IE.
 * Fixed layout issues related to struts-layout converting spaces to nbsp Using
 * Servlet for applet/server communications
 * 
 * Revision 1.7 2004/02/15 14:59:00 admin Fixing Header Tags
 * 
 * Revision 1.6 2004/02/10 16:31:23 admin Alter Prompt Completed
 * 
 * Revision 1.5 2004/02/08 13:40:09 admin Ego Questions Implemented. Full
 * workflow path complete in struts-config.xml Took control of DataValue Objects
 * from XDoclet
 * 
 * Revision 1.4 2004/02/07 04:33:25 admin First egoquestions page
 * 
 * Revision 1.3 2004/01/08 14:03:27 admin Adding Web Tier
 * 
 * Revision 1.2 2004/01/03 15:44:20 admin Adding new EJB files. More
 * relationships and use of value objects.
 * 
 * Revision 1.1.1.1 2003/12/18 21:17:18 admin Imported sources
 * 
 * Revision 1.3 2003/12/10 20:05:42 admin fixing header
 * 
 * Revision 1.2 2003/12/10 20:02:58 admin removing generated files
 * 
 */
