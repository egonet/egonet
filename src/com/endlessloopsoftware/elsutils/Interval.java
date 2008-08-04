package com.endlessloopsoftware.elsutils;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * Endless Loop Software Utilities
 * Copyright (c) 2003, Endless Loop Software, Inc.
 *
 *  @author     $Author: schoaff $
 *  @date      	$Date: 2006-03-09 09:42:46 -0500 (Thu, 09 Mar 2006) $
 *  @version    $Id: Interval.java 2 2006-03-09 14:42:46Z schoaff $
 *
 *
 * <p>This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * <p>This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * <p>You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 * @author Peter Schoaff
 * @version 1.4
 */


public class Interval implements Serializable
{
	private int years;
	private int months;
	private int days;

	/**
	 * <p>Instantiates an empty interval representing no time</p>
	 * @return Interval for no time
	 */
	Interval()
	{
		years   = 0;
		months  = 0;
		days    = 0;
	}

	/**
	 * <p>Returns string representation of this interval</p>
	 * @return interval string
	 */
	public String toString()
	{
		return ("Days: " + days + "; Months: " + months + "; Years: " + years);
	}

	private void writeObject(ObjectOutputStream oos) throws IOException
	{
		oos.defaultWriteObject();
	}
	private void readObject(ObjectInputStream ois) throws ClassNotFoundException, IOException
	{
		ois.defaultReadObject();
	}

	public int getDays()
	{
		return days;
	}
	public int getMonths()
	{
		return months;
	}
	public int getYears()
	{
		return years;
	}
	public void setDays(int days)
	{
		this.days = days;
	}
	public void setMonths(int months)
	{
		this.months = months;
	}
	public void setYears(int years)
	{
		this.years = years;
	}
}