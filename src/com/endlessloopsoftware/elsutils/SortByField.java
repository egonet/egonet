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

import java.lang.reflect.Method;
import java.util.Comparator;

 /**
 * <p>Description: Implements a comparator to be passed to sortable classes. It takes a class and a method name
 * for a method which fetches a comparable field and implements compare for that field.</p>
 */
public class SortByField implements Comparator
{
	private final Class[]  argtypelist = new Class[0];
	private final Object[] arglist = new Object[0];
	Method                 method;

	public SortByField(Class sortClass, String methodName) throws NoSuchMethodException
	{
			this.method = sortClass.getMethod(methodName, argtypelist);
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