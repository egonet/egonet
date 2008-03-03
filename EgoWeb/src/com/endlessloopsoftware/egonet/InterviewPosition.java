/**
 * <p>Title: Egocentric Networks Client Program</p>
 * <p>Description: Subject Interview Client</p>
 * <p>Copyright: Copyright (c) 2002 - 2004 </p>
 * <p>Company: Endless Loop Software</p>
 * @author Peter Schoaff
 *
 * $Id: InterviewPosition.java,v 1.4 2004/04/10 23:09:21 admin Exp $
 */

package com.endlessloopsoftware.egonet;

import com.endlessloopsoftware.egonet.Shared.AlterPromptType;

/**
 * @author admin
 *  
 */
public class InterviewPosition
{
	private final int             _questionType;
   private final int             _primaryAlter;
   private final int             _globalPosition;
   private final int             _pagePosition;
   private final int             _globalPageNumber;
   private final int             _typePageNumber;
   private final Long            _questionId;

   private       AlterPromptType _alterPromptType;

   /****
    * Saves position within web based interview
    * 
    * @param questionType     Standard question type from Shared, e.g. Shared.ALTER_PROMPT
    * @param alterPromptType  If Alter prompt, prompt method, e.g. Shared.ONE_LINK
    * @param primaryAlter     For alter prompts, alter pairs, and alter questions, the first alter
    * @param questionId       Primary key of question to be asked
    * @param globalPosition   Overall question number within interview
    * @param globalPageNumber Number of interview page on which this question falls
    * @param typePageNumber   Page within this question type
    * @param pagePosition     Index of question within the page
    */
   public InterviewPosition(int questionType, AlterPromptType alterPromptType, int primaryAlter, Long questionId,
                            int globalPosition, int globalPageNumber, int typePageNumber, int pagePosition)
	{
		_questionType 		= questionType;
      _alterPromptType  = alterPromptType;
		_primaryAlter 		= primaryAlter;
		_globalPosition 	= globalPosition;
		_questionId 		   = questionId;
		_globalPageNumber = globalPageNumber;
		_typePageNumber 	= typePageNumber;
		_pagePosition 		= pagePosition;
	}

   public InterviewPosition(int type, AlterPromptType promptType, int position, int alter, int globalPage)
   {
      _questionType     = type;
      _alterPromptType  = promptType;
      _primaryAlter     = alter;
      _globalPosition   = position;
      _questionId       = null;
      _globalPageNumber = globalPage;
      _typePageNumber   = 0;
      _pagePosition     = 0;
   }

   public InterviewPosition(int type)
   {
      _questionType     = type;
      _alterPromptType  = Shared.NOT_ALTER_PROMPT;
      _primaryAlter     = -1;
      _globalPosition   = -1;
      _questionId       = null;
      _globalPageNumber = -1;
      _typePageNumber   = -1;
      _pagePosition     = -1;
   }

   /**
	 * @return Returns the index.
	 */
	public int getGlobalPosition()
	{
		return _globalPosition;
	}

	/**
	 * @return Returns the pageIndex.
	 */
	public int getPagePosition()
	{
		return _pagePosition;
	}

	/**
	 * @return Returns the primaryAlter.
	 */
	public int getPrimaryAlter()
	{
		return _primaryAlter;
	}

   /**
    * @return Returns the primaryAlter.
    */
   public AlterPromptType getAlterPromptType()
   {
      return _alterPromptType;
   }

   /**
    * @return Returns the primaryAlter.
    */
   public void setAlterPromptType(AlterPromptType type)
   {
      _alterPromptType = type;
   }

	/**
	 * @return Returns the questionType.
	 */
	public int getQuestionType()
	{
		return _questionType;
	}

	/**
	 * @return Returns the globalPage.
	 */
	public int getGlobalPageNumber()
	{
		return _globalPageNumber;
	}

	/**
	 * @return Returns the typePage.
	 */
	public int getTypePageNumber()
	{
		return _typePageNumber;
	}

	/**
	 * @return Returns the questionId.
	 */
	public Long getQuestionId()
	{
		return _questionId;
	}

	public AlterPair getAlterPair()
	{
		AlterPair alters;

		if (	(this.getQuestionType() == Shared.ALTER_QUESTION) ||
				(this.getQuestionType() == Shared.ALTER_PROMPT))
		{
			alters = new AlterPair(this.getPrimaryAlter());
		}
		else if (this.getQuestionType() == Shared.ALTER_PAIR_QUESTION)
		{
			alters = new AlterPair(this.getPrimaryAlter(), this.getPagePosition());
		}
		else
		{
			alters = new AlterPair();
		}

		return alters;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	public String toString()
	{
		StringBuffer str = new StringBuffer();
		str.append("InterviewPosition [");
		str.append(" QuestionType: " + Shared.getTypeName(_questionType));
      str.append("; AlterPromptType: " + _alterPromptType);
		str.append("; PrimaryAlter: " + _primaryAlter);
		str.append("; GlobalPosition: " + _globalPosition);
		str.append("; PagePosition: " + _pagePosition);
		str.append("; GlobalPageNumber: " + _globalPageNumber);
		str.append("; TypePageNumber: " + _typePageNumber);
		str.append("; QuestionId: " + _questionId);
		str.append(" ]");

		return str.toString();
	}

}
