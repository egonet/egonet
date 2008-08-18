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
package com.endlessloopsoftware.elsutils.listbuilder;
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