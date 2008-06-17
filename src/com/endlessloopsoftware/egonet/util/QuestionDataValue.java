/**
 * <p>Title: Egocentric Networks Web Client</p>
 * <p>Description: Subject Interview Client</p>
 * <p>Copyright: Copyright (c) 2002 - 2004 </p>
 * <p>Company: Endless Loop Software</p>
 * @author Peter Schoaff
 *
 * $Id: QuestionDataValue.java,v 1.5 2004/04/05 01:16:43 admin Exp $
 */
package com.endlessloopsoftware.egonet.util;

import java.util.Set;

import com.endlessloopsoftware.egonet.interfaces.QuestionEJBPK;

/**
 * Value object for QuestionEJB.
 *
 * @xdoclet-generated at ${TODAY}
 * @copyright 2004 Endless Loop Software
 *  Inc.
 */
public class QuestionDataValue
   extends java.lang.Object
   implements java.io.Serializable 
{
   private java.lang.Long id;
   private java.lang.Long studyId;
   private boolean idHasBeenSet = false;
   private boolean centralMarker;
   private boolean centralMarkerHasBeenSet = false;
   private int questionType;
   private boolean questionTypeHasBeenSet = false;
   private int answerType;
   private boolean answerTypeHasBeenSet = false;
   private java.lang.String title;
   private boolean titleHasBeenSet = false;
   private java.lang.String text;
   private boolean textHasBeenSet = false;
   private java.lang.String citation;
   private boolean citationHasBeenSet = false;
   private Set AnswerDataValues = new java.util.HashSet();
   private com.endlessloopsoftware.egonet.data.QuestionLinkDataValue QuestionLinkDataValue;
   private Set SelectionDataValues = new java.util.TreeSet();

   private com.endlessloopsoftware.egonet.interfaces.QuestionEJBPK pk;

   public QuestionDataValue(QuestionEJBPK pk)
   {
	  this.setPrimaryKey(pk);
   }

   public QuestionDataValue( QuestionEJBPK pk,boolean centralMarker,int questionType,int answerType,java.lang.String title,java.lang.String text,java.lang.String citation )
   {
	  this.id = pk.getId();
     this.studyId = pk.getStudyId();
	  idHasBeenSet = true;
	  this.centralMarker = centralMarker;
	  centralMarkerHasBeenSet = true;
	  this.questionType = questionType;
	  questionTypeHasBeenSet = true;
	  this.answerType = answerType;
	  answerTypeHasBeenSet = true;
	  this.title = title;
	  titleHasBeenSet = true;
	  this.text = text;
	  textHasBeenSet = true;
	  this.citation = citation;
	  citationHasBeenSet = true;
	  this.pk = pk;
   }

   //TODO Cloneable is better than this !
   public QuestionDataValue( QuestionDataValue otherValue )
   {
	  this.id = otherValue.id;
     this.studyId = otherValue.studyId;
	  idHasBeenSet = true;
	  this.centralMarker = otherValue.centralMarker;
	  centralMarkerHasBeenSet = true;
	  this.questionType = otherValue.questionType;
	  questionTypeHasBeenSet = true;
	  this.answerType = otherValue.answerType;
	  answerTypeHasBeenSet = true;
	  this.title = otherValue.title;
	  titleHasBeenSet = true;
	  this.text = otherValue.text;
	  textHasBeenSet = true;
	  this.citation = otherValue.citation;
	  citationHasBeenSet = true;
	// TODO Clone is better no ?
	  this.AnswerDataValues = otherValue.AnswerDataValues;
	// TODO Clone is better no ?
	  this.QuestionLinkDataValue = otherValue.QuestionLinkDataValue;
	// TODO Clone is better no ?
	  this.SelectionDataValues = otherValue.SelectionDataValues;

	  pk = otherValue.getPrimaryKey();
   }

   public com.endlessloopsoftware.egonet.interfaces.QuestionEJBPK getPrimaryKey()
   {
	  return pk;
   }

   public void setPrimaryKey( com.endlessloopsoftware.egonet.interfaces.QuestionEJBPK pk )
   {
      this.pk        = pk;
      this.studyId   = pk.studyId;
      this.id        = pk.id;
   }

   public java.lang.Long getStudyId()
   {
     return this.studyId;
   }

   public java.lang.Long getId()
   {
	  return this.id;
   }

   public void setId( java.lang.Long id )
   {
	  this.id = id;
	  idHasBeenSet = true;
	  pk.setId(id);
   }

   public boolean idHasBeenSet(){
	  return idHasBeenSet;
   }
   public boolean getCentralMarker()
   {
	  return this.centralMarker;
   }

   public void setCentralMarker( boolean centralMarker )
   {
	  this.centralMarker = centralMarker;
	  centralMarkerHasBeenSet = true;

   }

   public boolean centralMarkerHasBeenSet(){
	  return centralMarkerHasBeenSet;
   }
   public int getQuestionType()
   {
	  return this.questionType;
   }

   public void setQuestionType( int questionType )
   {
	  this.questionType = questionType;
	  questionTypeHasBeenSet = true;

   }

   public boolean questionTypeHasBeenSet(){
	  return questionTypeHasBeenSet;
   }
   public int getAnswerType()
   {
	  return this.answerType;
   }

   public void setAnswerType( int answerType )
   {
	  this.answerType = answerType;
	  answerTypeHasBeenSet = true;

   }

   public boolean answerTypeHasBeenSet(){
	  return answerTypeHasBeenSet;
   }
   public java.lang.String getTitle()
   {
	  return this.title;
   }

   public void setTitle( java.lang.String title )
   {
	  this.title = title;
	  titleHasBeenSet = true;

   }

   public boolean titleHasBeenSet(){
	  return titleHasBeenSet;
   }
   public java.lang.String getText()
   {
	  return this.text;
   }

   public void setText( java.lang.String text )
   {
	  this.text = text;
	  textHasBeenSet = true;

   }

   public boolean textHasBeenSet(){
	  return textHasBeenSet;
   }
   public java.lang.String getCitation()
   {
	  return this.citation;
   }

   public void setCitation( java.lang.String citation )
   {
	  this.citation = citation;
	  citationHasBeenSet = true;

   }

   public boolean citationHasBeenSet(){
	  return citationHasBeenSet;
   }

   protected Set addedAnswerDataValues = new java.util.HashSet();
   protected Set onceAddedAnswerDataValues = new java.util.HashSet();
   protected Set removedAnswerDataValues = new java.util.HashSet();
   protected Set updatedAnswerDataValues = new java.util.HashSet();

   public Set getAddedAnswerDataValues() { return addedAnswerDataValues; }
   public Set getOnceAddedAnswerDataValues() { return onceAddedAnswerDataValues; }
   public Set getRemovedAnswerDataValues() { return removedAnswerDataValues; }
   public Set getUpdatedAnswerDataValues() { return updatedAnswerDataValues; }

   public void setAddedAnswerDataValues(Set addedAnswerDataValues)
   {
      this.addedAnswerDataValues.clear();
      this.addedAnswerDataValues.addAll(addedAnswerDataValues);
   }

   public void setOnceAddedAnswerDataValues(Set onceAddedAnswerDataValues)
   {
      this.onceAddedAnswerDataValues.clear();
      this.onceAddedAnswerDataValues.addAll(onceAddedAnswerDataValues);
   }

   public void setRemovedAnswerDataValues(Set removedAnswerDataValues)
   {
      this.removedAnswerDataValues.clear();
      this.removedAnswerDataValues.addAll(removedAnswerDataValues);
   }

   public void setUpdatedAnswerDataValues(Set updatedAnswerDataValues)
   {
      this.updatedAnswerDataValues.clear();
      this.updatedAnswerDataValues.addAll(updatedAnswerDataValues);
   }

   public com.endlessloopsoftware.egonet.util.AnswerDataValue[] getAnswerDataValues()
   {
	  return (com.endlessloopsoftware.egonet.util.AnswerDataValue[])this.AnswerDataValues.toArray(new com.endlessloopsoftware.egonet.util.AnswerDataValue[AnswerDataValues.size()]);
   }

   public void setAnswerDataValues(com.endlessloopsoftware.egonet.util.AnswerDataValue[] AnswerDataValues)
   {
      this.AnswerDataValues.clear();
      for (int i=0; i < AnswerDataValues.length; i++)
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
          if (! this.addedAnswerDataValues.contains(added))
            this.addedAnswerDataValues.add(added);
        }
        else if (! this.updatedAnswerDataValues.contains(added))
        {
            this.updatedAnswerDataValues.add(added);
        }
      }
      else
      {
        if (! this.onceAddedAnswerDataValues.contains(added))
          this.onceAddedAnswerDataValues.add(added);
        if (! this.addedAnswerDataValues.contains(added))
          this.addedAnswerDataValues.add(added);
      }
   }

   public void removeAnswerDataValue(com.endlessloopsoftware.egonet.util.AnswerDataValue removed)
   {
	  this.AnswerDataValues.remove(removed);

      if (this.addedAnswerDataValues.contains(removed))
        this.addedAnswerDataValues.remove(removed);
      else if (! this.removedAnswerDataValues.contains(removed))
        this.removedAnswerDataValues.add(removed);

	  if (this.updatedAnswerDataValues.contains(removed))
		 this.updatedAnswerDataValues.remove(removed);
   }

   public void updateAnswerDataValue(com.endlessloopsoftware.egonet.util.AnswerDataValue updated)
   {
	  if ( !this.updatedAnswerDataValues.contains(updated) && !this.addedAnswerDataValues.contains(updated))
		 this.updatedAnswerDataValues.add(updated);
      if (this.removedAnswerDataValues.contains(updated))
         this.removedAnswerDataValues.remove(updated);
   }

   public void cleanAnswerDataValue(){
	  this.addedAnswerDataValues = new java.util.HashSet();
      this.onceAddedAnswerDataValues = new java.util.HashSet();
	  this.removedAnswerDataValues = new java.util.HashSet();
	  this.updatedAnswerDataValues = new java.util.HashSet();
   }

   public void copyAnswerDataValuesFrom(com.endlessloopsoftware.egonet.util.QuestionDataValue from)
   {
	  // TODO Clone the List ????
	  this.AnswerDataValues = from.AnswerDataValues;
   }
   public com.endlessloopsoftware.egonet.data.QuestionLinkDataValue getQuestionLinkDataValue()
   {
	  return this.QuestionLinkDataValue;
   }
   public void setQuestionLinkDataValue( com.endlessloopsoftware.egonet.data.QuestionLinkDataValue QuestionLinkDataValue )
   {
	  this.QuestionLinkDataValue = QuestionLinkDataValue;
   }
   protected Set addedSelectionDataValues = new java.util.HashSet();
   protected Set onceAddedSelectionDataValues = new java.util.HashSet();
   protected Set removedSelectionDataValues = new java.util.HashSet();
   protected Set updatedSelectionDataValues = new java.util.HashSet();

   public Set getAddedSelectionDataValues() { return addedSelectionDataValues; }
   public Set getOnceAddedSelectionDataValues() { return onceAddedSelectionDataValues; }
   public Set getRemovedSelectionDataValues() { return removedSelectionDataValues; }
   public Set getUpdatedSelectionDataValues() { return updatedSelectionDataValues; }

   public void setAddedSelectionDataValues(Set addedSelectionDataValues)
   {
      this.addedSelectionDataValues.clear();
      this.addedSelectionDataValues.addAll(addedSelectionDataValues);
   }

   public void setOnceAddedSelectionDataValues(Set onceAddedSelectionDataValues)
   {
      this.onceAddedSelectionDataValues.clear();
      this.onceAddedSelectionDataValues.addAll(onceAddedSelectionDataValues);
   }

   public void setRemovedSelectionDataValues(Set removedSelectionDataValues)
   {
      this.removedSelectionDataValues.clear();
      this.removedSelectionDataValues.addAll(removedSelectionDataValues);
   }

   public void setUpdatedSelectionDataValues(Set updatedSelectionDataValues)
   {
      this.updatedSelectionDataValues.clear();
      this.updatedSelectionDataValues.addAll(updatedSelectionDataValues);
   }

   public com.endlessloopsoftware.egonet.util.SelectionDataValue[] getSelectionDataValues()
   {
	  return (com.endlessloopsoftware.egonet.util.SelectionDataValue[])this.SelectionDataValues.toArray(new com.endlessloopsoftware.egonet.util.SelectionDataValue[SelectionDataValues.size()]);
   }

   public void setSelectionDataValues(com.endlessloopsoftware.egonet.util.SelectionDataValue[] SelectionDataValues)
   {
      this.SelectionDataValues.clear();
      for (int i=0; i < SelectionDataValues.length; i++)
      	this.SelectionDataValues.add(SelectionDataValues[i]);
   }

   public void clearSelectionDataValues()
   {
	  this.SelectionDataValues.clear();
   }

   public void addSelectionDataValue(com.endlessloopsoftware.egonet.util.SelectionDataValue added)
   {
	  this.SelectionDataValues.add(added);

      if (this.removedSelectionDataValues.contains(added))
      {
        this.removedSelectionDataValues.remove(added);
        if (this.onceAddedSelectionDataValues.contains(added))
        {
          if (! this.addedSelectionDataValues.contains(added))
            this.addedSelectionDataValues.add(added);
        }
        else if (! this.updatedSelectionDataValues.contains(added))
        {
            this.updatedSelectionDataValues.add(added);
        }
      }
      else
      {
        if (! this.onceAddedSelectionDataValues.contains(added))
          this.onceAddedSelectionDataValues.add(added);
        if (! this.addedSelectionDataValues.contains(added))
          this.addedSelectionDataValues.add(added);
      }
   }

   public void removeSelectionDataValue(com.endlessloopsoftware.egonet.util.SelectionDataValue removed)
   {
	  this.SelectionDataValues.remove(removed);

      if (this.addedSelectionDataValues.contains(removed))
        this.addedSelectionDataValues.remove(removed);
      else if (! this.removedSelectionDataValues.contains(removed))
        this.removedSelectionDataValues.add(removed);

	  if (this.updatedSelectionDataValues.contains(removed))
		 this.updatedSelectionDataValues.remove(removed);
   }

   public void updateSelectionDataValue(com.endlessloopsoftware.egonet.util.SelectionDataValue updated)
   {
	  if ( !this.updatedSelectionDataValues.contains(updated) && !this.addedSelectionDataValues.contains(updated))
		 this.updatedSelectionDataValues.add(updated);
      if (this.removedSelectionDataValues.contains(updated))
         this.removedSelectionDataValues.remove(updated);
   }

   public void cleanSelectionDataValue(){
	  this.addedSelectionDataValues = new java.util.HashSet();
      this.onceAddedSelectionDataValues = new java.util.HashSet();
	  this.removedSelectionDataValues = new java.util.HashSet();
	  this.updatedSelectionDataValues = new java.util.HashSet();
   }

   public void copySelectionDataValuesFrom(com.endlessloopsoftware.egonet.util.QuestionDataValue from)
   {
	  // TODO Clone the List ????
	  this.SelectionDataValues = from.SelectionDataValues;
   }

   public String toString()
   {
	  StringBuffer str = new StringBuffer("{");

	  str.append("id=" + getId() + " " + "centralMarker=" + getCentralMarker() + " " + "questionType=" + getQuestionType() + " " + "answerType=" + getAnswerType() + " " + "title=" + getTitle() + " " + "text=" + getText() + " " + "citation=" + getCitation());
	  str.append('}');

	  return(str.toString());
   }

   /**
    * A Value Object has an identity if the attributes making its Primary Key have all been set. An object without identity is never equal to any other object.
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
	  if ( ! hasIdentity() ) return false;
	  if (other instanceof QuestionDataValue)
	  {
		 QuestionDataValue that = (QuestionDataValue) other;
		 if ( ! that.hasIdentity() ) return false;
		 boolean lEquals = true;
		 if( this.id == null )
		 {
			lEquals = lEquals && ( that.id == null );
		 }
		 else
		 {
			lEquals = lEquals && this.id.equals( that.id );
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
	  if (other instanceof QuestionDataValue)
	  {
		 QuestionDataValue that = (QuestionDataValue) other;
		 boolean lEquals = true;
		 lEquals = lEquals && this.centralMarker == that.centralMarker;
		 lEquals = lEquals && this.questionType == that.questionType;
		 lEquals = lEquals && this.answerType == that.answerType;
		 if( this.title == null )
		 {
			lEquals = lEquals && ( that.title == null );
		 }
		 else
		 {
			lEquals = lEquals && this.title.equals( that.title );
		 }
		 if( this.text == null )
		 {
			lEquals = lEquals && ( that.text == null );
		 }
		 else
		 {
			lEquals = lEquals && this.text.equals( that.text );
		 }
		 if( this.citation == null )
		 {
			lEquals = lEquals && ( that.citation == null );
		 }
		 else
		 {
			lEquals = lEquals && this.citation.equals( that.citation );
		 }
		 if( this.getAnswerDataValues() == null )
		 {
			lEquals = lEquals && ( that.getAnswerDataValues() == null );
		 }
		 else
		 {
			lEquals = lEquals && java.util.Arrays.equals(this.getAnswerDataValues() , that.getAnswerDataValues()) ;
		 }
		 if( this.QuestionLinkDataValue == null )
		 {
			lEquals = lEquals && ( that.QuestionLinkDataValue == null );
		 }
		 else
		 {
			lEquals = lEquals && this.QuestionLinkDataValue.equals( that.QuestionLinkDataValue );
		 }
		 if( this.getSelectionDataValues() == null )
		 {
			lEquals = lEquals && ( that.getSelectionDataValues() == null );
		 }
		 else
		 {
			lEquals = lEquals && java.util.Arrays.equals(this.getSelectionDataValues() , that.getSelectionDataValues()) ;
		 }

		 return lEquals;
	  }
	  else
	  {
		 return false;
	  }
   }

   public int hashCode(){
	  int result = 17;
      result = 37*result + ((this.id != null) ? this.id.hashCode() : 0);

      result = 37*result + (centralMarker ? 0 : 1);

      result = 37*result + (int) questionType;

      result = 37*result + (int) answerType;

      result = 37*result + ((this.title != null) ? this.title.hashCode() : 0);

      result = 37*result + ((this.text != null) ? this.text.hashCode() : 0);

      result = 37*result + ((this.citation != null) ? this.citation.hashCode() : 0);

	  result = 37*result + ((this.getAnswerDataValues() != null) ? this.getAnswerDataValues().hashCode() : 0);
	  result = 37*result + ((this.QuestionLinkDataValue != null) ? this.QuestionLinkDataValue.hashCode() : 0);
	  result = 37*result + ((this.getSelectionDataValues() != null) ? this.getSelectionDataValues().hashCode() : 0);
	  return result;
   }

}
