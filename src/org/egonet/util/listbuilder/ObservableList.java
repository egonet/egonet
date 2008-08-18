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
package org.egonet.util.listbuilder;

import java.util.Arrays;
import java.util.Observable;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Collections;

public class ObservableList<T> extends Observable
{

	private List<T> list;
	
	public ObservableList()
	{
		list = Collections.synchronizedList(new ArrayList<T>());
	}
	
	public ObservableList(T [] array)
	{
		list = Collections.synchronizedList(new ArrayList<T>(Arrays.asList(array)));
	}

	public void add(T o)
	{
		list.add(o);
		super.setChanged(); super.notifyObservers(this);
	}
	
	public void remove(Object o)
	{
		list.remove(o);
		super.setChanged(); super.notifyObservers(this);
	}
	
	public void addAll(T [] o)
	{
		list.addAll(java.util.Arrays.asList(o));
		super.setChanged(); super.notifyObservers(this);
	}
	
	public Iterator iterator()
	{
		return list.iterator();
	}
	
	public T remove(int i)
	{
		System.out.println("Removing element " + i);
		T obj = list.remove(i);
		super.setChanged(); super.notifyObservers(this);
		
		return obj;
	}
	
	public void removeAll()
	{
		list.clear();
		super.setChanged(); super.notifyObservers(this);
	}
	
	public int size()
	{
		return list.size();
	}
	
	public Object[] toArray()
	{
		return list.toArray();
	}
	
	public T get(int index)
	{
		return list.get(index);
	}
}
