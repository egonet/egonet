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
package com.endlessloopsoftware.elsutils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class DateUtils
{
	private DateUtils() {}

	private static final int days_in_month[] = {
		31, 28, 31, 30,
		31, 30, 31, 31,
		30, 31, 30, 31
	};

	// return true if leap year

	/**
	 * DateFormatSymbols returns an extra, empty value at the
	 * end of the array of months.  Remove it.
	 */
	static public String[] getMonthStrings()
	{
		String[] months = new java.text.DateFormatSymbols().getMonths();
		int lastIndex = months.length - 1;

		if ((months[lastIndex] == null) || (months[lastIndex].length() <= 0))
		{ 
			//last item empty
			String[] monthStrings = new String[lastIndex];
			System.arraycopy(months, 0, monthStrings, 0, lastIndex);
			return monthStrings;
		}
		else
		{ 
			//last item not empty
			return months;
		}
	}

   /**
	* <p>Checks for leap year</p>
	* @param year year to check
	* @return Returns true iff param is a leap year
	*/
	public static boolean isLeapYear(int year) {
		if (year % 4 != 0) {
			return false;
		}
		if (year % 400 == 0) {
			return true;
		}
		return (year % 100 != 0);
	}

	/**
	 * <p>Checks for weekday</p>
	 * @param cal Calendar to check
	 * @return Returns true iff Monday <= param <= Friday
	 */
	public static boolean isWeekday(Calendar cal)
	{
		int dow = cal.get(Calendar.DAY_OF_WEEK);

		return ((dow >= Calendar.MONDAY) && (dow <= Calendar.FRIDAY));
	}

	/**
	 * <p>Calculates number of days in param month/year</p>
	 * @param year  year to use in calculation
	 * @param month month to use in calculation
	 * @return Returns number of days in param month of param year
	 */
	public static int getDaysInMonth(int month, int year)
	{
		int m;
		if ((month == Calendar.FEBRUARY) && isLeapYear(year))
		{
			m = 29;
		}
		else
		{
			 m = days_in_month[month];
		}
		return m;
	}

	/**
	 * <p>Returns string representation of date in format mm/dd/yyyy</p>
	 * @param d Date for which to return string
	 * @return string representation of this date
	 */
	static public String getDateString(Date d)
	{
		if (d == null)
		{
			return "mm/dd/yyyy";
		}
		else
		{
			String dateString = null;
			try {
				SimpleDateFormat formatter = new SimpleDateFormat ("MM/dd/yyyy");
				dateString = formatter.format(d);
			}
			catch (Exception ex)
			{
				return "mm/dd/yyyy";
			}
			return (dateString);
		}
	}

	/**
	 * <p>Returns string representation of date in parameter format</p>
	 * @param d Date for which to return string
	 * @param format format of date string in SimpleDateFormat style
	 * @return string representation of this date
	 */
	static public String getDateString(Date d, String format)
	{
		if (d == null)
		{
			return format;
		}
		else
		{
			String dateString = null;
			try {
				SimpleDateFormat formatter = new SimpleDateFormat (format);
				dateString = formatter.format(d);
			}
			catch (Exception ex)
			{
				return format;
			}
			return (dateString);
		}
	}

	/**
	 * <p>Returns string representation of time of day in format hh:mm a</p>
	 * @param d Date for which to return string
	 * @return string representation of this time
	 */
	static public String getTimeString(Date d)
	{
		if (d == null)
		{
			return "hh:mm";
		}
		else
		{
			String dateString = null;
			try {
				SimpleDateFormat formatter = new SimpleDateFormat ("h:mm a");
				dateString = formatter.format(d);
			}
			catch (Exception ex)
			{
				return "hh:mm";
			}
			return (dateString);
		}
	}

	/**
	 * <p>Returns string representation of time of cal in format mm/dd/yyyy at hh:mm a</p>
	 * @param cal Calendar for which to return string
	 * @return string representation of this calendar
	 */
	static public String getDateTimeString(Calendar cal)
	{
		return getDateTimeString(cal.getTime());
	}

	/**
	 * <p>Returns string representation of time of date in format mm/dd/yyyy at hh:mm a</p>
	 * @param d Date for which to return string
	 * @return string representation of this Date
	 */
	static public String getDateTimeString(Date d)
	{
		if (d == null)
		{
			return "mm/dd/yyyy at hh:mm";
		}
		else
		{
			String dateString = null;
			try {
				SimpleDateFormat formatter = new SimpleDateFormat ("MM/dd/yyyy 'at' h:mm a");
				dateString = formatter.format(d);
			}
			catch (Exception ex)
			{
				return "mm/dd/yyyy at hh:mm";
			}
			return (dateString);
		}
	}

	/**
	 * <p>Returns string representation of a given month in the year</p>
	 * @param d Date for which to return string
	 * @return string representation of this Date
	 */
	static public String getMonthString(int month)
	{
		Calendar cal = new GregorianCalendar(2002, month, 1);
		SimpleDateFormat formatter = new SimpleDateFormat ("MMMM");
		return(formatter.format(cal.getTime()));
	}

	/**
	 * <p>Converts string to a date</p>
	 * @param s Date string in format MM/dd/yy
	 * @return date representation of this string
	 */
	static public Date parseDateString(String s)
			throws ParseException
	{
		return parseDateString(s, "MM/dd/yy");
	}

	/**
	 * <p>Converts string to a date</p>
	 * @param s Date string in format MM/dd/yy
	 * @return date representation of this string
	 */
	static public Date parseMySQLDateString(String s)
			throws ParseException
	{
		return parseDateString(s, "yyyy-MM-dd");
	}

	/**
	 * <p>Converts string to a date</p>
	 * @param s Date string in format MM/dd/yy
	 * @return date representation of this string
	 */
	static public Date parseDateString(String s, String format)
			throws ParseException
	{
		Date d = null;

		if ((s != null) && (s.length() >= 6))
		{
			try {
				SimpleDateFormat formatter = new SimpleDateFormat (format);
				d = formatter.parse(s);
			}
			catch (ParseException ignore) {}
		}
		return (d);
	}

	/**
	 * <p>Converts string to a date</p>
	 * @param s Date string in format MM/dd/yy at h:mm a
	 * @return date representation of this string
	 */
	static public Date parseDateTimeString(String s)
			throws ParseException
	{
		Date d = null;

		if ((s != null) && (s.length() >= 6))
		{
			try {
				SimpleDateFormat formatter = new SimpleDateFormat ("MM/dd/yyyy 'at' h:mm a");
				d = formatter.parse(s);
			}
			catch (ParseException ignore) {}
		}
		return (d);
	}

	/**
	 * <p>Converts a date to a standard java Calendar</p>
	 * @param date Date to convert
	 * @return calendar for that date
	 */
	static public Calendar getCalendar(Date date)
	{
		Calendar rcal = Calendar.getInstance();
		rcal.setTime(date);
		return rcal;
	}

	/**
	 * <p>Removes time portion of a date</p>
	 * @param date Date to convert
	 * @return Date representing only day portion of Date
	 */
	static public Date getDateDatePart(Date date)
	{
		ELSCalendar cal = new ELSCalendar(date);
		return cal.getTime();
	}
}