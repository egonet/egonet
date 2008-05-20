/**
 * <p>Copyright: Copyright (c) 2002 - 2004 </p>
 * <p>Company: Endless Loop Software</p>
 * @author Peter Schoaff
 *
 * $Id: StudyAndInterviewTransfer.java,v 1.3 2004/04/01 15:10:56 admin Exp $
 */
package com.endlessloopsoftware.egonet.util;

import java.util.Set;
import java.util.TreeSet;

/**
 * Transfer object for Study and Interview Names.
 * 
 * @copyright 2004 Endless Loop Software Inc.
 */
public class StudyAndInterviewTransfer 
	implements java.io.Serializable
{
	public String 	studyName		         = null;
	public Set		interviewIdentifiers = new TreeSet();

	
	public StudyAndInterviewTransfer(String studyName)
	{
		this.studyName = studyName;
	}

	public void addInterview(InterviewIdentifier identifier)
	{
		interviewIdentifiers.add(identifier);
	}
}
