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
