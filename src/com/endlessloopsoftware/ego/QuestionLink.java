package com.endlessloopsoftware.ego;


/**
 * <p>Title: Egocentric Networks Client Program</p>
 * <p>Description: Subject Interview Client</p>
 * <p>Copyright: Copyright (c) 2002</p>
 * <p>Company: Endless Loop Software</p>
 * @author Peter Schoaff
 * @version 1.0
 */

public class QuestionLink
		implements Cloneable
{
	public boolean	active 		= false;
	public Answer	answer		= null;

	public Object clone()
			throws CloneNotSupportedException
	{
		QuestionLink q;

		q = (QuestionLink) super.clone();

		if (active)
		{
			q.answer = (Answer) this.answer.clone();
		}

		return(q);
	}
}