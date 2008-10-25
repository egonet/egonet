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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Observable;

public class ObservableList<T>
    extends Observable
{
    private java.util.List<T> list = new ArrayList<T>();

    /****
     * Notifies observers that a field in the study has changed
     */
    public void notifyObservers()
    {
        setChanged();
        super.notifyObservers(this);
    }

    /**
     *
     *
     * @param o param
     */
    public void add(T o)
    {
        list.add(o);
        notifyObservers();
    }

    /**
     *
     *
     * @return returns
     */
    public Iterator<T> iterator()
    {
        return list.iterator();
    }

    /**
     *
     *
     * @param i param
     */
    public void remove(int i)
    {
        list.remove(i);
        notifyObservers();
    }

    /**
     *
     */
    public void removeAll()
    {
        list = new ArrayList<T>();
        notifyObservers();
    }

    /**
     *
     *
     * @return returns
     */
    public int size()
    {
        return (list.size());
    }

    /**
     *
     *
     * @param a param
     *
     * @return returns
     */
    public Object[] toArray(Object[] a)
    {
        return (list.toArray(a));
    }
}
