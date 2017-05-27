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
package org.egonet.model;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A map of question id (long) to question object
 * @author martins
 *
 */
public class QuestionList extends HashMap<Long,Question>
{
	final private static Logger logger = LoggerFactory.getLogger(QuestionList.class);
	
	public void addQuestion(Question q)
	{
	    logger.debug("Question added: " + q.getString());
		put(q.UniqueId, q);
	}

	/****
	 * Returns question from map identified by its UniqueId
	 * @param l UniqueId of question
	 * @return q question in list
	 */
	public Question getQuestion(Long l)
	{
		return get(l);
	}

	public String dump()
	{
		StringBuffer buffer = new StringBuffer();

		Set keys = keySet();
		for (Iterator it = keys.iterator(); it.hasNext();)
		{
			Object key = it.next();
			buffer.append("[" + key + "," + get(key) + "]\n");
		}

		return buffer.toString();
	}
	
	public boolean contains(long questionId)
	{
		return containsKey(questionId);
	}
	
	public boolean contains(Question question)
	{
		return containsValue(question);
	}
}