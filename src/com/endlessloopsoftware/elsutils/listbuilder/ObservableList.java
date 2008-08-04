/*
 * Created on Dec 9, 2003
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package com.endlessloopsoftware.elsutils.listbuilder;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Observable;

public class ObservableList
    extends Observable
{
    private java.util.List list = new ArrayList();

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
    public void add(Object o)
    {
        list.add(o);
        notifyObservers();
    }

    /**
     *
     *
     * @return returns
     */
    public Iterator iterator()
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
        list = new ArrayList();
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
