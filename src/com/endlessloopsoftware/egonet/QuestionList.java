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
package com.endlessloopsoftware.egonet;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;

public class QuestionList extends Observable
		implements Observer
{
	private final Map<Long,Question> questionMap = new HashMap<Long,Question>();

	public QuestionList()
	{
	}

	/****
	 * Notifies observers that a field in the study has changed
	 */
	public void notifyObservers()
	{
		setChanged();
		super.notifyObservers(this);
	}

	/****
	 * Returns Map containing all questions
	 * @return questionList Map of questions
	 */
	public Map<Long, Question> getQuestionMap()
	{
		return(questionMap);
	}

	/****
	 * Deletes all questions from map
	 */
	public void removeAll()
	{
		questionMap.clear();
	}

	/****
	 * Removes a single question from the map
	 * @param q question to add
	 */
	public void remove(Question q)
	{
		questionMap.remove(q.UniqueId);
	}

	/****
	 * Adds question to the map
	 * @param q question to add
	 */
	public void addQuestion(Question q)
	{
		questionMap.put(q.UniqueId, q);
	}

	/****
	 * Returns question from map identified by its UniqueId
	 * @param l UniqueId of question
	 * @return q question in list
	 */
	public Question getQuestion(Long l)
	{
		return((Question) questionMap.get((Object) l));
	}

	/****
	 * Returns a Collection of all the values in the map
	 * usually used to iterate over questions
	 * @return collection collection of questions
	 */
	public Collection<Question> values()
	{
		return (questionMap.values());
	}

	/****
	 * Determines if question is already in map
	 * @param q question to seek
	 * @return bool true iff question in map
	 */
	public boolean contains(Question q)
	{
		return(questionMap.containsValue(q));
	}

	/****
	 * Determines if question with a given UniqueId is already in map
	 * @param l UniqueId of question to seek
	 * @return bool true iff question in map
	 */
	public boolean contains(Long l)
	{
		return(questionMap.containsKey(l));
	}

	/****
	 * Function called when observables updated
	 * @param o Observable
	 * @param arg param from Observable
	 */
	public void update(Observable o, Object arg)
	{
		/**@todo Implement this java.util.Observer method*/
		throw new java.lang.UnsupportedOperationException("Method update() not yet implemented.");
	}

   public int size()
   {
      return questionMap.size();
   }
   
   public String dump()
   {
      StringBuffer buffer = new StringBuffer();
      
      Set keys = questionMap.keySet();
      for (Iterator it = keys.iterator(); it.hasNext();)
      {
         Object key = it.next();
         buffer.append(key + " : " + questionMap.get(key) + "\n");
      }
      
      return buffer.toString();
   }
}