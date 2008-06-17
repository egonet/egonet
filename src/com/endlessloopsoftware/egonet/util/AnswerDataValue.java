/**
 * <p>Title: Egocentric Networks Client Program</p>
 * <p>Description: Subject Interview Client</p>
 * <p>Copyright: Copyright (c) 2002 - 2004 </p>
 * <p>Company: Endless Loop Software</p>
 * @author Peter Schoaff
 *
 * $Id: AnswerDataValue.java,v 1.16 2004/05/26 12:35:53 admin Exp $
 */

package com.endlessloopsoftware.egonet.util;

import org.jboss.logging.Logger;

import com.endlessloopsoftware.egonet.AlterPair;
import com.endlessloopsoftware.egonet.Shared;
import com.endlessloopsoftware.egonet.Shared.AlterPromptType;
import com.endlessloopsoftware.egonet.interfaces.AnswerEJBPK;
import com.endlessloopsoftware.egonet.interfaces.QuestionEJBLocal;
import com.endlessloopsoftware.egonet.interfaces.StudyEJBLocal;
import com.endlessloopsoftware.egonet.interfaces.StudyEJBPK;

/**
 * Value object for AnswerEJB.
 *
 * @xdoclet-generated at ${TODAY}
 * @copyright 2004 Endless Loop Software
 *  Inc.
 */
public class AnswerDataValue
	extends java.lang.Object
	implements java.io.Serializable, Comparable
{
	Logger                   logger                   = Logger.getLogger(this.getClass());
	
	private java.lang.String id;
   private boolean          idHasBeenSet               = false;
   private AlterPair        alters;
   private boolean          altersHasBeenSet           = false;
   private AlterPromptType  alterPromptState;
   private boolean          alterPromptStateHasBeenSet = false;
   private boolean          answered;
   private boolean          answeredHasBeenSet         = false;
   private int              answerIndex;
   private boolean          answerIndexHasBeenSet      = false;
   private int              answerValue;
   private boolean          answerValueHasBeenSet      = false;
   private java.lang.String answerString;
   private boolean          answerStringHasBeenSet     = false;
   private boolean          answerAdjacent;
   private boolean          answerAdjacentHasBeenSet   = false;

   private AnswerEJBPK      pk;
   private StudyEJBLocal    _study;
   private Long             _studyId;
   private QuestionEJBLocal _question;
   private int              _questionType;
   private Long             _questionId;
   private String           _questionText;
   private String           _questionTitle;
   private String[]         _alterStrings              = {};
	
	private AnswerDataValue()
	{
		pk = new com.endlessloopsoftware.egonet.interfaces.AnswerEJBPK();
	}
	
	public AnswerDataValue(java.lang.String id, AlterPair alters, AlterPromptType alterPromptState, String[] alterStrings,
                          boolean answered, int answerIndex, int answerValue, java.lang.String answerString,
                          boolean answerAdjacent, StudyEJBLocal study, QuestionEJBLocal question)
	{
		this.id = id;
		idHasBeenSet = true;
		this.alters = alters;
		altersHasBeenSet = true;
      this.alterPromptState = alterPromptState;
      this.alterPromptStateHasBeenSet = true;
		this.answered = answered;
		answeredHasBeenSet = true;
		this.answerIndex = answerIndex;
		answerIndexHasBeenSet = true;
		this.answerValue = answerValue;
		answerValueHasBeenSet = true;
		this.answerString = answerString;
		answerStringHasBeenSet = true;
		this.answerAdjacent = answerAdjacent;
		answerAdjacentHasBeenSet = true;
		pk = new com.endlessloopsoftware.egonet.interfaces.AnswerEJBPK(this.getId());
		
		// Private fields for internal use
		this.setStudy(study);
		this.setQuestion(question);
		_alterStrings = alterStrings;
		
		if ((getStudy() == null) || (getQuestion() == null))
		{
			logger.error("Study or Question is null in AnswerDataValue instance");
		}
	}
	
	//TODO Cloneable is better than this !
	public AnswerDataValue( AnswerDataValue otherValue )
	{
		this.id = otherValue.id;
		idHasBeenSet = true;
		this.alters = otherValue.alters;
		altersHasBeenSet = true;
      this.alterPromptState = otherValue.alterPromptState;
      alterPromptStateHasBeenSet = true;
		this.answered = otherValue.answered;
		answeredHasBeenSet = true;
		this.answerIndex = otherValue.answerIndex;
		answerIndexHasBeenSet = true;
		this.answerValue = otherValue.answerValue;
		answerValueHasBeenSet = true;
		this.answerString = otherValue.answerString;
		answerStringHasBeenSet = true;
		this.answerAdjacent = otherValue.answerAdjacent;
		answerAdjacentHasBeenSet = true;
		
		pk = new AnswerEJBPK(this.getId());
		this.setStudy(otherValue.getStudy());
		this.setQuestion(otherValue.getQuestion());
		
		if ((getStudy() == null) || (getQuestion() == null))
		{
			logger.error("Study or Question is null in AnswerDataValue instance");
		}
	}
	
	public com.endlessloopsoftware.egonet.interfaces.AnswerEJBPK getPrimaryKey()
	{
		return pk;
	}
	
	public void setPrimaryKey( com.endlessloopsoftware.egonet.interfaces.AnswerEJBPK pk )
	{
		// it's also nice to update PK object - just in case
		// somebody would ask for it later...
		this.pk = pk;
		setId( pk.id );
	}
	
	public java.lang.String getId()
	{
		return this.id;
	}
	
	public void setId( java.lang.String id )
	{
		this.id = id;
		idHasBeenSet = true;
		
		pk.setId(id);
	}
	
	public boolean idHasBeenSet()
	{
		return idHasBeenSet;
	}
	
	public AlterPair getAlters()
	{
		return this.alters;
	}
	
	public void setAlters( AlterPair alters )
	{
		this.alters = alters;
		altersHasBeenSet = true;
		
	}
	
	public boolean altersHasBeenSet()
	{
		return altersHasBeenSet;
	}
	
   public AlterPromptType getAlterPromptState()
   {
      return this.alterPromptState;
   }
   
   public void setAlters( AlterPromptType state )
   {
      this.alterPromptState = state;
      alterPromptStateHasBeenSet = true;
   }
   
   public boolean alterPromptStateHasBeenSet()
   {
      return alterPromptStateHasBeenSet;
   }
   
	public boolean getAnswered()
	{
		return this.answered;
	}
	
	public void setAnswered( boolean answered )
	{
		this.answered = answered;
		answeredHasBeenSet = true;
		
	}
	
	public boolean answeredHasBeenSet()
	{
		return answeredHasBeenSet;
	}

	public int getAnswerIndex()
	{
		return this.answerIndex;
	}
	
	public void setAnswerIndex( int answerIndex )
	{
		this.answerIndex = answerIndex;
		answerIndexHasBeenSet = true;
		
	}
	
	public boolean answerIndexHasBeenSet()
	{
		return answerIndexHasBeenSet;
	}
	
	public int getAnswerValue()
	{
		return this.answerValue;
	}
	
	public void setAnswerValue( int answerValue )
	{
		this.answerValue = answerValue;
		answerValueHasBeenSet = true;
	}
	
	public boolean answerValueHasBeenSet()
	{
		return answerValueHasBeenSet;
	}
	
	public java.lang.String getAnswerString()
	{
		return this.answerString;
	}
	
	public void setAnswerString( java.lang.String answerString )
	{
		this.answerString = answerString;
		answerStringHasBeenSet = true;
		
	}
	
	public boolean answerStringHasBeenSet()
	{
		return answerStringHasBeenSet;
	}
	
	public boolean getAnswerAdjacent()
	{
		return this.answerAdjacent;
	}
	
	public void setAnswerAdjacent( boolean answerAdjacent )
	{
		this.answerAdjacent = answerAdjacent;
		answerAdjacentHasBeenSet = true;
		
	}
	
	public boolean answerAdjacentHasBeenSet()
	{
		return answerAdjacentHasBeenSet;
	}
	
	/**
	 * @return Returns the study.
	 */
	public StudyEJBLocal getStudy()
	{
		return _study;
	}
	
	/**
	 * @param study The study to set.
	 */
	public void setQuestion(QuestionEJBLocal question)
	{
		_question 			= question;
		_questionType 		= question.getQuestionType();
      _questionTitle    = question.getTitle();
		_questionId			= question.getId();
		_questionText		= question.getText();
//		logger.debug("Setting Question: " + _questionType + "/" + _questionId);
	}
	
	/**
	 * @return Returns the study.
	 */
	public QuestionEJBLocal getQuestion()
	{
		return _question;
	}
	
   /**
    * @return Returns the study.
    */
   public Long getQuestionId()
   {
      return _questionId;
   }
   
   /**
    * @return Returns the study.
    */
   public int getQuestionType()
   {
      return _questionType;
   }
   
   /**
    * @return Returns the study.
    */
   public String getQuestionTitle()
   {
      return _questionTitle;
   }
   
	/**
	 * @return Returns the study.
	 */
	public String getQuestionText()
   {
      String   s        = _questionText;
      String   oldS     = s;
      int      parsePtr;

      		logger.debug("Formatting Question: " + s);
      		logger.debug("Using alterStrings: " + _alterStrings + "; #" +  _alterStrings.length);
      try
      {
         for (parsePtr = s.indexOf("$$-1"); (parsePtr != -1); parsePtr = s.indexOf("$$-1"))
         {
            s = s.substring(0, parsePtr) + _alterStrings[1] + s.substring(parsePtr + 4);
         }

         for (parsePtr = s.indexOf("$$1"); (parsePtr != -1); parsePtr = s.indexOf("$$1"))
         {
            s = s.substring(0, parsePtr) + _alterStrings[0] + s.substring(parsePtr + 3);
         }

         for (parsePtr = s.indexOf("$$2"); (parsePtr != -1); parsePtr = s.indexOf("$$2"))
         {
            s = s.substring(0, parsePtr) + _alterStrings[1] + s.substring(parsePtr + 3);
         }

         for (parsePtr = s.indexOf("$$"); (parsePtr != -1); parsePtr = s.indexOf("$$"))
         {
            s = s.substring(0, parsePtr) + _alterStrings[0] + s.substring(parsePtr + 2);
         }
      }
      catch (Exception ex)
      {
         logger.debug("Error getting Question Text");
         ex.printStackTrace();
         s = oldS;
      }

      return s;
   }
	
	/**
	 * @param study The study to set.
	 */
	public void setStudy(StudyEJBLocal study)
	{
		_study = study;
		_studyId = ((StudyEJBPK) study.getPrimaryKey()).getId();
	}
	
	/**
	 * @return Returns a string of html options for the selections
	 */
	public String writeSelections()
	{
		SelectionDataValue[] selections = getQuestion().getQuestionDataValue().getSelectionDataValues();
		StringBuffer str = new StringBuffer();
		
		for (int i = 0; i < selections.length; i++)
		{
			str.append("<option value='" + selections[i].getValue() + "'>" + selections[i].getText() + "</option>");
		}
		
		return str.toString();
	}
	
   
   /**
    * Fill in an alter pair question when using applet linking
    *
    */
   public void fillAppletLinkAnswer()
   {
      setAnswerString("Applet Linking");
      setAnswerIndex(-1);
      setAnswerValue(-1);
      setAnswerAdjacent(true);
      setAnswered(true);   
   }
	
	/**
	 * Uses the answerString value set by struts to fill in the answerValue and answerIndex fields
	 */
	public void fillAnswer() 
	{
		String answerString 			= getAnswerString();
		QuestionEJBLocal question 	= getQuestion();

		switch (question.getAnswerType())
		{
			case Shared.TEXT:
			{
				setAnswerIndex(-1);
				setAnswerValue(answerString.length());
				setAnswerAdjacent(false);
				setAnswered(true);
				break;
			}

			case Shared.NUMERICAL:
			{
				setAnswerIndex(-1);
				setAnswerValue(Shared.toInt(answerString));
				setAnswerAdjacent(false);
				setAnswered(true);
				break;
			}
			
			case Shared.CATEGORICAL:
			{
				int value = Shared.toInt(answerString);
				
				SelectionDataValue[] selections = getQuestion().getQuestionDataValue().getSelectionDataValues();
				for (int i = 0; i < selections.length; i++)
				{
					if (selections[i].getValue() == value)
					{
						setAnswerIndex(selections[i].getIndex());
						setAnswerValue(value);
						setAnswerString(selections[i].getText());
						setAnswerAdjacent(selections[i].getAdjacent());
						setAnswered(true);
						
//						logger.debug("Found Selection Match: " + this.toString());
						break;
					}
				}
			}
		}
	}

	/*******
	 * Outputs HTML Code for entering this item
	 */
	public String toHTML(String name, Integer index)
	{
		QuestionDataValue qd	= getQuestion().getQuestionDataValue();
		StringBuffer str 				= new StringBuffer();
		String itemName			= name + "[" + index + "].answerString";

		if (getQuestion().getAnswerType() == Shared.TEXT)
		{	
			str.append("<tr><th valign='top' class='LABEL'><span class='LABEL'>");
			str.append(qd.getText());
			str.append("</span></th><td valign='top' class='LABEL' style=''>");
			str.append("<input type='text' name='" + itemName + "' maxlength='32' size='24'");
			str.append("value='" + getAnswerString() + "' onchange='checkValue(this, '" + itemName + "','TEXT',true);' class='LABEL'>");
			str.append("<img name='" + itemName + "required' src='/EgoWeb/images/clearpixel.gif'></td></tr>");
		}
		else if (getQuestion().getAnswerType() == Shared.NUMERICAL)
		{
			str.append("<tr><th valign='top' class='LABEL'><span class='LABEL'>");
			str.append(qd.getText());
			str.append("</span></th><td valign='top' class='LABEL' style=''>");
			str.append("<input type='text' name='" + itemName + "' maxlength='32' size='24'");
			str.append("value='" + getAnswerString() + "' onchange='checkValue(this, '" + itemName + "','NUMBER',true);' class='LABEL'>");
			str.append("<img name='" + itemName + "required' src='/EgoWeb/images/clearpixel.gif'></td></tr>");
		}
		else
		{
			str.append("<tr><th valign='top' class='LABEL'><span class='LABEL'>");
			str.append(qd.getText());
			str.append("</span></th><td valign='top' class='LABEL' style=''>");
			str.append("<select name='" + itemName + "'>");
			
			SelectionDataValue[] selections = qd.getSelectionDataValues();
			for (int i = 0; i < selections.length; i++)
			{
				str.append("<option value='" + selections[i].getValue() + "'>" + selections[i].getText() + "</option>");
			}
			str.append("</select>");
			str.append("<img name='" + itemName + "required' src='/EgoWeb/images/clearpixel.gif'></td></tr>");
		}
		
		//<img name='firstNamerequired' src='/EgoWeb/images/clearpixel.gif'></td></tr>		
		return str.toString();
	}
	
	public String toString()
	{
		StringBuffer str = new StringBuffer("{");
		
		str.append("id=" + getId() + " " + "alters=" + getAlters() + " " + "answered=" + getAnswered() + " " + "answerIndex=" + getAnswerIndex() + " " + "answerValue=" + getAnswerValue() + " " + "answerString=" + getAnswerString() + " " + "answerAdjacent=" + getAnswerAdjacent());
		str.append(" question=" + getQuestion() + " questionId=" + getQuestionId() + " questionType=" + _questionType);
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
	
	public boolean isIdentical(Object other)
	{
		if (other instanceof AnswerDataValue)
		{
			AnswerDataValue that = (AnswerDataValue) other;
			boolean lEquals = true;
			lEquals = lEquals && this.alters == that.alters;
			lEquals = lEquals && this.answered == that.answered;
			lEquals = lEquals && this.answerIndex == that.answerIndex;
			lEquals = lEquals && this.answerValue == that.answerValue;
			if( this.answerString == null )
			{
				lEquals = lEquals && ( that.answerString == null );
			}
			else
			{
				lEquals = lEquals && this.answerString.equals( that.answerString );
			}
			lEquals = lEquals && this.answerAdjacent == that.answerAdjacent;
			
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
		result = 37*result + ((this.id != null) ? this.id.hashCode() : 0);
      
      result = 37*result + ((this._studyId != null) ? this._studyId.hashCode() : 0);
      result = 37*result + ((this.getQuestionId() != null) ? this.getQuestionId().hashCode() : 0);
		
		result = 37*result + alters.hashCode();
		
		return result;
	}
	
	public boolean equals(Object other)
	{
		AnswerDataValue that = (AnswerDataValue) other;

		if (	this._studyId.equals(that._studyId) &&
				this.getQuestionId().equals(that.getQuestionId()) &&
				this.getAlters().equals(that.getAlters()))
		{
			return true;
		}
		
		return false;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(Object arg0) 
	{	
		AnswerDataValue that = (AnswerDataValue) arg0;
		
		int rval = this._studyId.compareTo(that._studyId);
		
		if (rval == 0)
		{	
			rval = this._questionType - that._questionType;
		}
		
		if (rval == 0)
		{
			rval = this.getAlters().compareTo(that.getAlters());
		}
		
		if (rval == 0)
		{
			rval = this.getQuestionId().compareTo(that.getQuestionId());
		}
		
		return rval;
	}
	
}
