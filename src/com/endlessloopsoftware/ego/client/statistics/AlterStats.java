package com.endlessloopsoftware.ego.client.statistics;

import java.util.Map;
import java.util.HashMap;

/**
 * <p>Title: Egocentric Network Researcher</p>
 * <p>Description: Configuration Utilities for an Egocentric network study</p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: Endless Loop Software</p>
 * @author Peter C. Schoaff
 * @version 1.0
 *
 * $Id: AlterStats.java,v 1.1 2005/08/02 19:36:05 samag Exp $
 *
 * $Log: AlterStats.java,v $
 * Revision 1.1  2005/08/02 19:36:05  samag
 * Initial checkin
 *
 * Revision 1.2  2004/02/26 21:19:17  admin
 * adding jardescs
 *
 * Revision 1.1  2003/12/08 15:57:50  admin
 * Modified to generate matrix files on survey completion or summarization
 * Extracted statistics models
 *
 */

public class AlterStats
{
	public String 	qTitle;
	public int 		answerType;
	public Long 	questionId;
	public int 		answerCount;
	public String[]	answerText;
	
	// Map<Index,Total count>
	//public Map<Integer,Integer> 	answerTotals = new HashMap<Integer,Integer>(0);
	public int answerTotals[];
}

