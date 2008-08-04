package com.endlessloopsoftware.elsutils;

import java.lang.reflect.Method;
import java.util.Comparator;

/**
 * Endless Loop Software Utilities
 * Copyright (c) 2003, Endless Loop Software, Inc.
 *
 *  @author     $Author: schoaff $
 *  @date      	$Date: 2006-03-09 09:42:46 -0500 (Thu, 09 Mar 2006) $
 *  @version    $Id: SortByField.java 2 2006-03-09 14:42:46Z schoaff $
 *
 * <p>Description: Implements a comparator to be passed to sortable classes. It takes a class and a method name
 * for a method which fetches a comparable field and implements compare for that field.</p>
 */
public class SortByField implements Comparator
{
	private final Class[]  argtypelist = new Class[0];
	private final Object[] arglist = new Object[0];
	Method                 method;

	public SortByField(Class sortClass, String methodName)
	{
		try
		{
			this.method = sortClass.getMethod(methodName, argtypelist);
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
		}
	}

	public int compare(Object a, Object b)
	{
		Comparable acomp = null;
		Comparable bcomp = null;

		try
		{
			acomp = (Comparable) method.invoke(a, arglist);
			bcomp = (Comparable) method.invoke(b, arglist);
		}
		catch (Exception ex)
		{
			throw new ClassCastException(ex.getMessage());
		}

		return (acomp.compareTo(bcomp));
	}
}