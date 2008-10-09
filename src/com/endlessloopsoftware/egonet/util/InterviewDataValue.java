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
package com.endlessloopsoftware.egonet.util;

import java.util.Arrays;
import java.util.Set;

import com.endlessloopsoftware.egonet.interfaces.InterviewEJBPK;
import com.endlessloopsoftware.egonet.interfaces.QuestionEJBLocal;
import com.endlessloopsoftware.egonet.interfaces.StudyEJBLocal;
import com.endlessloopsoftware.egonet.interfaces.StudyEJBPK;

/**
 * Value object for InterviewEJB.
 * 
 * @copyright 2004 Endless Loop Software Inc.
 */
public class InterviewDataValue 
	implements java.io.Serializable
{

	private Long           id;
   private boolean        idHasBeenSet              = false;
   private String         firstName;
   private boolean        firstNameHasBeenSet       = false;
   private String         lastName;
   private boolean        lastNameHasBeenSet        = false;
   private Boolean        complete;
   private boolean        completeHasBeenSet        = false;
   private String[]       alters;
   private boolean        altersHasBeenSet          = false;
   private int[][]        adjacencyMatrix           = new int[0][0];
   private boolean        adjacencyMatrixHasBeenSet = false;

   private Set<AnswerDataValue>            AnswerDataValues          = new java.util.TreeSet<AnswerDataValue>();
   private InterviewEJBPK pk;
   private StudyEJBLocal  study;
   private Long           studyId;

	public InterviewDataValue()
	{
		pk = new com.endlessloopsoftware.egonet.interfaces.InterviewEJBPK();
	}

	public InterviewDataValue(Long id, StudyEJBLocal study, QuestionEJBLocal question, String firstName,
			String lastName, Boolean complete, Boolean pairsComplete, String[] alters, int[][] adjacencyMatrix)
	{
		this.id = id;
		idHasBeenSet = true;
		this.firstName = firstName;
		firstNameHasBeenSet = true;
		this.lastName = lastName;
		lastNameHasBeenSet = true;
		this.complete = complete;
		completeHasBeenSet = true;

		this.alters = alters;
		altersHasBeenSet = true;
		this.adjacencyMatrix = adjacencyMatrix;
		this.adjacencyMatrixHasBeenSet = true;
		
		pk = new InterviewEJBPK(this.getId());

		this.study = study;
	}

	public InterviewDataValue(InterviewDataValue otherValue)
	{
		this.id                 = otherValue.id;
		idHasBeenSet 			   = true;
		this.firstName 		      = otherValue.firstName;
		firstNameHasBeenSet     = true;
		this.lastName           = otherValue.lastName;
		lastNameHasBeenSet 	   = true;
		this.complete 			   = otherValue.complete;
		completeHasBeenSet 	   = true;
		this.alters 			      = otherValue.alters;
		altersHasBeenSet 		   = true;
      
		this.adjacencyMatrix        = otherValue.adjacencyMatrix;
		adjacencyMatrixHasBeenSet   = true;
		
		this.AnswerDataValues = otherValue.AnswerDataValues;

		pk = new com.endlessloopsoftware.egonet.interfaces.InterviewEJBPK(this.getId());
		this.study = otherValue.study;
	}

	public InterviewEJBPK getPrimaryKey()
	{
		return pk;
	}

	public void setPrimaryKey(InterviewEJBPK pk)
	{
		// it's also nice to update PK object - just in case
		// somebody would ask for it later...
		this.pk = pk;
		setId(pk.id);
	}

	public Long getId()
	{
		return this.id;
	}

	/**
	 * @return Returns the study.
	 */
	public StudyEJBLocal getStudy()
	{
		return study;
	}

   /**
    * @return Returns the study.
    */
   public Long getStudyId()
   {
      return studyId;
   }

	/**
	 * @param study
	 *           The study to set.
	 */
	public void setStudy(StudyEJBLocal study)
	{
		this.study = study;
		this.studyId = ((StudyEJBPK) study.getPrimaryKey()).getId();
	}

	public void setId(Long id)
	{
		this.id = id;
		idHasBeenSet = true;
		pk.setId(id);
	}

	public boolean idHasBeenSet()
	{
		return idHasBeenSet;
	}

	public String getFirstName()
	{
		return this.firstName;
	}

	public void setFirstName(String firstName)
	{
		this.firstName = firstName;
		firstNameHasBeenSet = true;
	}

	public boolean firstNameHasBeenSet()
	{
		return firstNameHasBeenSet;
	}

	public String getLastName()
	{
		return this.lastName;
	}

	public void setLastName(String lastName)
	{
		this.lastName = lastName;
		lastNameHasBeenSet = true;
	}

	public boolean lastNameHasBeenSet()
	{
		return lastNameHasBeenSet;
	}

	public Boolean getComplete()
	{
		return this.complete;
	}

	public void setComplete(Boolean complete)
	{
		this.complete = complete;
		completeHasBeenSet = true;
	}

	public boolean completeHasBeenSet()
	{
		return completeHasBeenSet;
	}

	public String[] getAlters()
	{
		return this.alters;
	}

	public void setAlters(String[] alters)
	{
		this.alters = alters;
		altersHasBeenSet = true;
	}

	public boolean altersHasBeenSet()
	{
		return altersHasBeenSet;
	}

	public int[][] getAdjacencyMatrix()
	{
		return this.adjacencyMatrix;
	}

	public void setAdjacencyMatrix(int[][] adjacencyMatrix)
	{
		this.adjacencyMatrix = adjacencyMatrix;
		adjacencyMatrixHasBeenSet = true;
	}

	public boolean adjacencyMatrixHasBeenSet()
	{
		return adjacencyMatrixHasBeenSet;
	}

	protected Set<AnswerDataValue>	addedAnswerDataValues		= new java.util.HashSet<AnswerDataValue>();

	protected Set<AnswerDataValue>	onceAddedAnswerDataValues	= new java.util.HashSet<AnswerDataValue>();

	protected Set<AnswerDataValue>	removedAnswerDataValues		= new java.util.HashSet<AnswerDataValue>();

	protected Set<AnswerDataValue>	updatedAnswerDataValues		= new java.util.HashSet<AnswerDataValue>();

	public Set<AnswerDataValue> getAddedAnswerDataValues()
	{
		return addedAnswerDataValues;
	}

	public Set<AnswerDataValue> getOnceAddedAnswerDataValues()
	{
		return onceAddedAnswerDataValues;
	}

	public Set<AnswerDataValue> getRemovedAnswerDataValues()
	{
		return removedAnswerDataValues;
	}

	public Set<AnswerDataValue> getUpdatedAnswerDataValues()
	{
		return updatedAnswerDataValues;
	}

	public void setAddedAnswerDataValues(Set<AnswerDataValue> addedAnswerDataValues)
	{
		this.addedAnswerDataValues.clear();
		this.addedAnswerDataValues.addAll(addedAnswerDataValues);
	}

	public void setOnceAddedAnswerDataValues(Set<AnswerDataValue> onceAddedAnswerDataValues)
	{
		this.onceAddedAnswerDataValues.clear();
		this.onceAddedAnswerDataValues.addAll(onceAddedAnswerDataValues);
	}

	public void setRemovedAnswerDataValues(Set<AnswerDataValue> removedAnswerDataValues)
	{
		this.removedAnswerDataValues.clear();
		this.removedAnswerDataValues.addAll(removedAnswerDataValues);
	}

	public void setUpdatedAnswerDataValues(Set<AnswerDataValue> updatedAnswerDataValues)
	{
		this.updatedAnswerDataValues.clear();
		this.updatedAnswerDataValues.addAll(updatedAnswerDataValues);
	}

	public com.endlessloopsoftware.egonet.util.AnswerDataValue[] getAnswerDataValues()
	{
		return this.AnswerDataValues
				.toArray(new com.endlessloopsoftware.egonet.util.AnswerDataValue[AnswerDataValues.size()]);
	}

	public void setAnswerDataValues(com.endlessloopsoftware.egonet.util.AnswerDataValue[] AnswerDataValues)
	{
		this.AnswerDataValues.clear();
		for (int i = 0; i < AnswerDataValues.length; i++)
			this.AnswerDataValues.add(AnswerDataValues[i]);
	}

	public void clearAnswerDataValues()
	{
		this.AnswerDataValues.clear();
	}

	public void addAnswerDataValue(com.endlessloopsoftware.egonet.util.AnswerDataValue added)
	{
		this.AnswerDataValues.add(added);
		if (this.removedAnswerDataValues.contains(added))
		{
			this.removedAnswerDataValues.remove(added);
			if (this.onceAddedAnswerDataValues.contains(added))
			{
				if (!this.addedAnswerDataValues.contains(added))
					this.addedAnswerDataValues.add(added);
			}
			else if (!this.updatedAnswerDataValues.contains(added))
			{
				this.updatedAnswerDataValues.add(added);
			}
		}
		else
		{
			if (!this.onceAddedAnswerDataValues.contains(added))
				this.onceAddedAnswerDataValues.add(added);
			if (!this.addedAnswerDataValues.contains(added))
				this.addedAnswerDataValues.add(added);
		}
	}

	public void removeAnswerDataValue(com.endlessloopsoftware.egonet.util.AnswerDataValue removed)
	{
		this.AnswerDataValues.remove(removed);
		if (this.addedAnswerDataValues.contains(removed))
			this.addedAnswerDataValues.remove(removed);
		else if (!this.removedAnswerDataValues.contains(removed))
			this.removedAnswerDataValues.add(removed);
		if (this.updatedAnswerDataValues.contains(removed))
			this.updatedAnswerDataValues.remove(removed);
	}

	public void updateAnswerDataValue(com.endlessloopsoftware.egonet.util.AnswerDataValue updated)
	{
		if (!this.updatedAnswerDataValues.contains(updated) && !this.addedAnswerDataValues.contains(updated))
			this.updatedAnswerDataValues.add(updated);
		if (this.removedAnswerDataValues.contains(updated))
			this.removedAnswerDataValues.remove(updated);
	}

	public void cleanAnswerDataValue()
	{
		this.addedAnswerDataValues = new java.util.HashSet<AnswerDataValue>();
		this.onceAddedAnswerDataValues = new java.util.HashSet<AnswerDataValue>();
		this.removedAnswerDataValues = new java.util.HashSet<AnswerDataValue>();
		this.updatedAnswerDataValues = new java.util.HashSet<AnswerDataValue>();
	}

	public void copyAnswerDataValuesFrom(InterviewDataValue from)
	{
		// TODO Clone the List ????
		this.AnswerDataValues = from.AnswerDataValues;
	}

	public String toString()
	{
		StringBuffer str = new StringBuffer("{");
		str.append("id=" + getId() + " " + "firstName=" + getFirstName() + " " + "lastName=" + getLastName() + " "
				+ "complete=" + getComplete() + " " + "alters=" + getAlters() + " adjacencyMatrix=" + getAdjacencyMatrix());
		str.append('}');
		return (str.toString());
	}

	/**
	 * A Value Object has an identity if the attributes making its Primary Key
	 * have all been set. An object without identity is never equal to any other
	 * object.
	 * 
	 * @return true if this instance has an identity.
	 */
	protected boolean hasIdentity()
	{
		boolean ret = true;
		ret = ret && idHasBeenSet;
		return ret;
	}

	public boolean equals(Object other)
	{
		if (this == other)
			return true;
		if (!hasIdentity())
			return false;
		if (other instanceof InterviewDataValue)
		{
			InterviewDataValue that = (InterviewDataValue) other;
			if (!that.hasIdentity())
				return false;
			boolean lEquals = true;
			if (this.id == null)
			{
				lEquals = lEquals && (that.id == null);
			}
			else
			{
				lEquals = lEquals && this.id.equals(that.id);
			}
			lEquals = lEquals && isIdentical(that);
			return lEquals;
		}
		else
		{
			return false;
		}
	}

	public boolean isIdentical(Object other)
	{
		if (other instanceof InterviewDataValue)
		{
			InterviewDataValue that = (InterviewDataValue) other;
			boolean lEquals = true;
			if (this.firstName == null)
			{
				lEquals = lEquals && (that.firstName == null);
			}
			else
			{
				lEquals = lEquals && this.firstName.equals(that.firstName);
			}
			
			if (this.lastName == null)
			{
				lEquals = lEquals && (that.lastName == null);
			}
			else
			{
				lEquals = lEquals && this.lastName.equals(that.lastName);
			}
			
			if (this.complete == null)
			{
				lEquals = lEquals && (that.complete == null);
			}
			else
			{
				lEquals = lEquals && this.complete.equals(that.complete);
			}
			
			if (this.alters == null)
			{
				lEquals = lEquals && (that.alters == null);
			}
			else
			{
				lEquals = lEquals && this.alters.equals(that.alters);
			}
			
			if (this.adjacencyMatrix == null)
			{
				lEquals = lEquals && (that.adjacencyMatrix == null);
			}
			else
			{
				lEquals = lEquals && this.adjacencyMatrix.equals(that.adjacencyMatrix);
			}
			
			if (this.getAnswerDataValues() == null)
			{
				lEquals = lEquals && (that.getAnswerDataValues() == null);
			}
			else
			{
				lEquals = lEquals && Arrays.equals(this.getAnswerDataValues(), that.getAnswerDataValues());
			}
			
			return lEquals;
		}
		else
		{
			return false;
		}
	}

	public int hashCode()
	{
		int result = 17;
		result = 37 * result + ((this.id != null) ? this.id.hashCode() : 0);
		result = 37 * result + ((this.firstName != null) ? this.firstName.hashCode() : 0);
		result = 37 * result + ((this.lastName != null) ? this.lastName.hashCode() : 0);
		result = 37 * result + ((this.complete != null) ? this.complete.hashCode() : 0);
		result = 37 * result + ((this.alters != null) ? this.alters.hashCode() : 0);
		result = 37 * result + ((this.adjacencyMatrix != null) ? this.adjacencyMatrix.hashCode() : 0);
		result = 37 * result + ((this.getAnswerDataValues() != null) ? this.getAnswerDataValues().hashCode() : 0);
		return result;
	}
}
