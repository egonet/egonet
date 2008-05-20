package com.endlessloopsoftware.ego;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;


/**
 * <p>Title: Egocentric Network Researcher</p>
 * <p>Description: Configuration Utilities for an Egocentric network study</p>
 * <p>Copyright: Copyright (c) 2002 - 2004</p>
 * <p>Company: Endless Loop Software</p>
 * @author Peter C. Schoaff
 *
 * $Id: QuestionList.java,v 1.1 2005/08/02 19:36:02 samag Exp $
 */

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
	public Map getQuestionMap()
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

   /* (non-Javadoc)
    * @see java.util.List#size()
    */
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

/**
 * $Log: QuestionList.java,v $
 * Revision 1.1  2005/08/02 19:36:02  samag
 * Initial checkin
 *
 * Revision 1.6  2004/04/11 00:24:48  admin
 * Fixing headers
 *
 * Revision 1.5  2004/03/29 00:35:09  admin
 * Downloading Interviews
 * Fixing some bugs creating Interviews from Data Objects
 *
 * Revision 1.4  2004/03/21 14:00:38  admin
 * Cleaned up Question Panel Layout using FOAM
 *
 * Revision 1.3  2003/12/18 19:30:05  admin
 * Small mods to support EJB persistence
 *
 * Revision 1.2  2003/12/05 19:15:43  admin
 * Extracting Study
 *
 * Revision 1.1  2003/12/04 15:14:08  admin
 * Merging EgoNet and EgoClient projects so that they can share some
 * common classes more easily.
 *
 * Revision 1.2  2003/11/25 19:25:43  admin
 * Warn before closing window
 *
 * Revision 1.1.1.1  2003/06/08 15:09:40  admin
 * Egocentric Network Survey Authoring Module
 *
 * Revision 1.6  2002/08/08 17:07:25  admin
 * Preparing to change file system
 *
 * Revision 1.5  2002/07/24 14:17:09  admin
 * xml files, links
 *
 * Revision 1.4  2002/06/30 15:59:17  admin
 * Moving questions in lists, between lists
 * Better category input
 *
 * Revision 1.3  2002/06/25 15:41:01  admin
 * Lots of UI work
 *
 * Revision 1.2  2002/06/21 22:47:12  admin
 * question lists working again
 *
 * Revision 1.1  2002/06/21 21:53:29  admin
 * new files
 *
 */
