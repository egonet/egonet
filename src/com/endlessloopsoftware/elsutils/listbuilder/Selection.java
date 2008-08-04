package com.endlessloopsoftware.elsutils.listbuilder;

/**
 * Endless Loop Software Utilities
 * Copyright (c) 2003, Endless Loop Software, Inc.
 *
 *  @author     $Author: schoaff $
 *  @date      	$Date: 2006-03-09 09:42:46 -0500 (Thu, 09 Mar 2006) $
 *  @version    $Id: Selection.java 2 2006-03-09 14:42:46Z schoaff $
 *
 */

public class Selection
{
	public String 	 string;
	public int		 index;
	public int		 value;
	public boolean  adjacent;

	public Selection()
	{
		string 	    	= "";
		value	       	= 0;
		index				= 0;
		adjacent     	= false;
	}

	public Selection(String string, int value, int index, boolean adjacent)
	{
		this.string     = string;
		this.value	    = value;
		this.index		 = index;
		this.adjacent   = false;
	}

	public String toString()
	{
		return (string);
		//return (string + ", " + value + ", " + adjacent);
	}

	public boolean equals(Object o)
	{
		try
		{
			return string.equals(((Selection) o).string);
		}
		catch (Exception e)
		{
			return false;
		}
	}
}