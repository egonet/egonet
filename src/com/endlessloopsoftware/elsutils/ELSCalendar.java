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
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;



public class ELSCalendar
		extends GregorianCalendar
		implements Comparable<Calendar>
{
	final private static byte[] QUARTERS = {0, 0, 0, 1, 1, 1, 2, 2, 2, 3, 3, 3};
	final private static byte[] QUARTER_STARTS = {Calendar.JANUARY, Calendar.APRIL, Calendar.JULY, Calendar.OCTOBER};
	final private static byte[] QUARTER_ENDS = {Calendar.MARCH, Calendar.JUNE, Calendar.SEPTEMBER, Calendar.DECEMBER};

	/**
	 * <p>Instantiates an ELSCalendar representing the current date</p>
	 * @return ELSCalendar for current date
	 */
	public ELSCalendar()
	{
		this(Calendar.getInstance());
	}

	/**
	 * <p>Instantiates an ELSCalendar for date represented by Date param</p>
	 * @param date Date to which to initialize ELSCalendar
	 * @return ELSCalendar for param date
	 */
	public ELSCalendar(Date date)
	{
		super();

		Calendar cal = Calendar.getInstance();
		cal.setTime(date);

		this.clear();
		this.set(Calendar.YEAR, cal.get(Calendar.YEAR));
		this.set(Calendar.MONTH, cal.get(Calendar.MONTH));
		this.set(Calendar.DATE, cal.get(Calendar.DATE));
	}

   /**
    * <p>Instantiates an ELSCalendar a given year/month/day triplet</p>
    * @param   year
    * @param   month
    * @param   date
    * @return  ELSCalendar for param date
    */
   public ELSCalendar(int year, int month, int date)
   {
      super();

      this.set(Calendar.YEAR, year);
      this.set(Calendar.MONTH, month);
      this.set(Calendar.DATE, date);
   }

	/**
	 * <p>Instantiates an ELSCalendar for date represented by Calendar param</p>
	 * @param calendar date to which to initialize ELSCalendar
	 * @return ELSCalendar for param date
	 */
	public ELSCalendar(Calendar calendar)
	{
		this(calendar.getTime());
	}

	/**
	 * <p>Instantiates an ELSCalendar representing the current date</p>
	 * @return ELSCalendar for current date
	 */
	public static ELSCalendar now()
	{
		return new ELSCalendar();
	}

	/**
	 * <p>Sets date to that of another ELSCalendar</p>
	 * @param ELSCalendar for desired date
	 */
	public void setTime(ELSCalendar cal)
	{
		this.clear();
		this.set(Calendar.YEAR, cal.get(Calendar.YEAR));
		this.set(Calendar.MONTH, cal.get(Calendar.MONTH));
		this.set(Calendar.DATE, cal.get(Calendar.DATE));
	}

	/**
	 * <p>Sets date to that of another ELSCalendar</p>
	 * @param ELSCalendar for desired date
	 */
	public static ELSCalendar mySQLCalendar(String dateString)
		throws ParseException
	{
		if ("0000-00-00".equals(dateString)) 	return null;
		else 									         return new ELSCalendar(DateUtils.parseMySQLDateString(dateString));
	}

	/**
	 * <p>Compares this to another date.</p>
	 * @return -1 if this before, 0 if equal to, and 1 if after param Calendar
	 */
	public int compareTo(Calendar that)
	{
		if (this.before(that))
		{
			return -1;
		}
		else if (this.after(that))
		{
			return 1;
		}
		else
		{
			return 0;
		}
	}

	/**
	 * <p>Routine which finds the Date matching the previous day matching
	 * the param Day of Week. </p>
	 * @param dow Day of Week to find
	 * @param checkToday consider today as a possible match
	 * @return ELSCalendar of previous day matching param Day of Week
	 */
	public ELSCalendar getPriorWeekdayInstance(int dow, boolean checkToday)
	{
		int thisdow = this.get(Calendar.DAY_OF_WEEK);
		ELSCalendar cal = (ELSCalendar) this.clone();

		if (thisdow > dow)
		{
			cal.add(Calendar.DATE, dow - thisdow);
		}
		else if (thisdow < dow)
		{
			cal.add(Calendar.DATE, dow - thisdow - 7);
		}
		else
		{
			cal.add(Calendar.DATE, checkToday ? 0 : -7);
		}

		return cal;
	}

	/**
	 * <p>Routine which determines if this is a given day of week</p>
	 * @param dow Day of Week to check
	 * @return true iff this is param dow
	 */
	public boolean isWeekdayInstance(int dow)
	{
		return (this.get(Calendar.DAY_OF_WEEK) == dow);
	}

	/**
	 * <p>Routine which finds the Date matching the next day matching
	 * the param Day of Week. </p>
	 * @param dow Day of Week to find
	 * @param checkToday consider today as a possible match
	 * @return ELSCalendar of previous day matching param Day of Week
	 */
	public ELSCalendar getNextWeekdayInstance(int dow, boolean checkToday)
	{
		int thisdow = this.get(Calendar.DAY_OF_WEEK);
		ELSCalendar cal = (ELSCalendar) this.clone();

		/* Calculate start and end of work week */
		if (thisdow > dow)
		{
			cal.add(Calendar.DATE, 7 - (thisdow - dow));
		}
		else if (thisdow < dow)
		{
			cal.add(Calendar.DATE, dow - thisdow);
		}
		else
		{
			cal.add(Calendar.DATE, checkToday ? 0 : 7);
		}

		return cal;
	}

	/**
	 * <p>Routine which finds the Calendar for the first day of this quarter </p>
	 * @return ELSCalendar of first day of the this quarter
	 */
	public ELSCalendar getQuarterStart()
	{
		ELSCalendar cal = (ELSCalendar) this.clone();
		cal.set(Calendar.DATE, 1);
		cal.set(Calendar.MONTH, QUARTER_STARTS[QUARTERS[this.get(Calendar.MONTH)]]);

		return cal;
	}

	/**
	 * <p>Routine which finds the Calendar for the last day of this quarter </p>
	 * @return ELSCalendar of last day of this quarter
	 */
	public ELSCalendar getQuarterEnd()
	{
		ELSCalendar cal = (ELSCalendar) this.clone();
		cal.set(Calendar.MONTH, QUARTER_ENDS[QUARTERS[this.get(Calendar.MONTH)]]);
		cal.set(Calendar.DATE, cal.getActualMaximum(Calendar.DATE));

		return cal;
	}

	/**
	 * <p>Routine which finds the Calendar for the first day of the prior quarter </p>
	 * @return ELSCalendar of first day of the prior quarter
	 */
	public ELSCalendar getPriorQuarterStart()
	{
		ELSCalendar cal = (ELSCalendar) this.clone();
		cal.add(Calendar.MONTH, -3);
		return cal.getQuarterStart();
	}

	/**
	 * <p>Routine which finds the Calendar for the last day of the prior quarter </p>
	 * @return ELSCalendar of last day of the prior quarter
	 */
	public ELSCalendar getPriorQuarterEnd()
	{
		ELSCalendar cal = (ELSCalendar) this.clone();
		cal.add(Calendar.MONTH, -3);
		return cal.getQuarterEnd();
	}

	/**
	 * <p>Routine which finds the Calendar for the first day of this quarter </p>
	 * @return ELSCalendar of first day of the this quarter
	 */
	public ELSCalendar getYearStart()
	{
		ELSCalendar cal = (ELSCalendar) this.clone();
		cal.set(Calendar.DATE, 1);
		cal.set(Calendar.MONTH, Calendar.JANUARY);

		return cal;
	}

	/**
	 * <p>Routine which finds the Calendar for the last day of this quarter </p>
	 * @return ELSCalendar of last day of this quarter
	 */
	public ELSCalendar getYearEnd()
	{
		ELSCalendar cal = (ELSCalendar) this.clone();
		cal.set(Calendar.MONTH, Calendar.DECEMBER);
		cal.set(Calendar.DATE, cal.getActualMaximum(Calendar.DATE));

		return cal;
	}

	/**
	 * <p>Routine which finds the Calendar for the first day of the prior quarter </p>
	 * @return ELSCalendar of first day of the prior quarter
	 */
	public ELSCalendar getPriorYearStart()
	{
		ELSCalendar cal = (ELSCalendar) this.clone();
		cal.add(Calendar.MONTH, -12);
		return cal.getYearStart();
	}

	/**
	 * <p>Routine which finds the Calendar for the last day of the prior quarter </p>
	 * @return ELSCalendar of last day of the prior quarter
	 */
	public ELSCalendar getPriorYearEnd()
	{
		ELSCalendar cal = (ELSCalendar) this.clone();
		cal.add(Calendar.MONTH, -12);
		return cal.getYearEnd();
	}

	/**
	 * <p>Checks if start <= this <= end</p>
	 * @param start first day of period to match
	 * @param end last day of period to match
	 * @return true iff start <= now <= end
	 */
	public boolean isInPeriod(ELSCalendar start, ELSCalendar end)
	{
		return ((!start.after(this)) && (!this.after(end)));
	}

	/**
	 * <p>Returns the interval between two dates as an ELS Calendar. The
	 * year field indicates the years between the dates, etc.. The order of
	 * dates is irrelevant</p>
	 * @param date1 one end of interval
	 * @param date2 the other end of the interval
	 * @return ELSCalendar of interval
	 */
	public static Interval getInterval(ELSCalendar date1, ELSCalendar date2)
	{
		Interval interval = new Interval();

		/* Assure date2 follows date1 */
		if (date1.after(date2))
		{
			ELSCalendar date3 = date1;
			date1 = date2;
			date2 = date3;
		}

		int year1 = date1.get(Calendar.YEAR);
		int year2 = date2.get(Calendar.YEAR);
		int month1 = date1.get(Calendar.MONTH);
		int month2 = date2.get(Calendar.MONTH);
		int day1 = date1.get(Calendar.DATE);
		int day2 = date2.get(Calendar.DATE);

		interval.setYears(year2 - year1);
		if ((month1 > month2) ||
			((month1 == month2) && (day1 > day2)))
		{
			interval.setYears(interval.getYears() - 1);
		}

		interval.setMonths(interval.getYears() * 12);
		if (month2 >= month1)
		{
			interval.setMonths(interval.getMonths() + (month2 - month1));
		}
		else
		{
			interval.setMonths(interval.getMonths() + (12 - (month1 - month2)));
		}

		long deltamillis = date2.getTimeInMillis() - date1.getTimeInMillis();
		interval.setDays((int) ELSMath.ceilingQuotient(deltamillis, 1000 * 60 * 60 * 24));

		return interval;
	}

	/**
	 * <p>Returns java.sql.Date for midnight of date represented by this ELSCalendar</p>
	 * @return sql date for midnight of this date
	 */
	public java.sql.Date toSqlDate()
	{
		return new java.sql.Date(this.getTimeInMillis());
	}

	/**
	 * <p>Returns java.sql.Date for midnight of date represented by param Calendar</p>
	 * @param cal Calendar to convert to an sqlDate
	 * @return sql date for midnight of param date
	 */
	public static java.sql.Date parseSqlDate(Calendar cal)
	{
		return new ELSCalendar(cal).toSqlDate();
	}

	/**
	 * <p>Returns java.sql.Date for midnight of date represented by param Date</p>
	 * @param date java.util.Date to convert to an sqlDate
	 * @return sql date for midnight of param date
	 */
	public static java.sql.Date parseSqlDate(java.util.Date date)
	{
		return new ELSCalendar(date).toSqlDate();
	}

	/**
	 * <p>Returns string representation of date in format mm/dd/yyyy</p>
	 * @return string representation of this date
	 */
	public String toString()
	{
		return DateUtils.getDateString(this.getTime());
	}

	/**
	 * <p>Returns string representation of date in parameter format</p>
	 * @param format String format in SimpleDateFormat style
	 * @return string representation of this date
	 */
	public String toString(String format)
	{
		return DateUtils.getDateString(this.getTime(), format);
	}

	/**
	 * Returns a java.sql.Timestamp for the current time, useful when timestamping stuff
	 * @return timestamp for now
	 */
	public static java.sql.Timestamp timestamp()
	{
		return (new java.sql.Timestamp(new java.util.Date().getTime()));
	}

	public static void main(String[] args)
	{
		System.out.println(new ELSCalendar());
		System.out.println(new ELSCalendar(Calendar.getInstance()));
		System.out.println(new ELSCalendar(Calendar.getInstance().getTime()));

		ELSCalendar lastSunday = new ELSCalendar().getPriorWeekdayInstance(Calendar.SUNDAY, false);
		ELSCalendar lastMonday = new ELSCalendar().getPriorWeekdayInstance(Calendar.MONDAY, false);
		ELSCalendar nextSunday = new ELSCalendar().getNextWeekdayInstance(Calendar.SUNDAY, true);
		ELSCalendar nextMonday = new ELSCalendar().getNextWeekdayInstance(Calendar.MONDAY, true);
		System.out.println(lastSunday);
		System.out.println(lastMonday);
		System.out.println(lastSunday.isWeekdayInstance(Calendar.SUNDAY) + " --> Expects true");
		System.out.println(lastSunday.isWeekdayInstance(Calendar.MONDAY) + " --> Expects false");
		System.out.println(ELSCalendar.getInterval(lastSunday, nextSunday));
		System.out.println(ELSCalendar.getInterval(nextMonday, lastMonday));

		System.out.println(lastMonday.isInPeriod(lastSunday, nextSunday) + " --> Expects true");
		System.out.println(lastMonday.isInPeriod(lastMonday, nextMonday) + " --> Expects true");
		System.out.println(nextMonday.isInPeriod(lastSunday, nextSunday) + " --> Expects false");

		System.out.println("Year/Quarter Calculations");
		ELSCalendar now = ELSCalendar.now();
		System.out.println(now.getPriorYearStart());
		System.out.println(now.getPriorYearEnd());
		System.out.println(now.getPriorQuarterStart());
		System.out.println(now.getPriorQuarterEnd());
		System.out.println(now.getYearStart());
		System.out.println(now.getQuarterStart());
		System.out.println(now.getQuarterEnd());
		System.out.println(now.getYearEnd());
	}
}

/**
 * $Log: ELSCalendar.java,v $
 * Revision 1.1.1.1  2005/10/23 16:21:25  schoaff
 * Checking from IntelliJ IDEA
 *
 * Revision 1.1.1.1  2005/03/23 13:22:21  schoaff
 * New CVS Repository
 *
 * Revision 1.4  2003/12/03 18:08:23  admin
 * Adding Gif Encoders
 *
 * Revision 1.3  2003/09/16 15:26:32  admin
 * Improved DateChooser and PhoneBean
 *
 * Revision 1.2  2003/09/13 18:44:42  admin
 * Adding Header
 *
 */

