package com.endlessloopsoftware.ego;
/****
 * 
 * Copyright (c) 2007, Endless Loop Software, Inc.
 * 
 *  This file is part of EgoNet.
 *
 *    EgoNet is free software; you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as published by
 *    the Free Software Foundation; either version 3 of the License, or
 *    (at your option) any later version.
 *
 *    EgoNet is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU General Public License for more details.
 *
 *    You should have received a copy of the GNU General Public License
 *    along with this program.  If not, see <http://www.gnu.org/licenses/>.
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