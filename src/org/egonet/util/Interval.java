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
package org.egonet.util;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;



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