package com.endlessloopsoftware.elsutils;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;

/**
 * Endless Loop Software Utilities
 * Copyright (c) 2003, Endless Loop Software, Inc.
 *
 *  @author $Author: schoaff $
 *  @date $Date: 2006-03-09 09:42:46 -0500 (Thu, 09 Mar 2006) $
 *  @version $Id: ELSList.java 2 2006-03-09 14:42:46Z schoaff $
 *
 */

public class ELSList
      extends ArrayList
{
   public ListIterator listIterator()
   {
      return (new ELSListIterator(this));
   }
}

class ELSListIterator
      implements ListIterator
{
   private int  lastreturn = -1;
   private int  index      = -1;
   private List l;

   ELSListIterator(List l)
   {
      this.l = l;
   }

   public boolean hasNext()
   {
      return (index < (l.size() - 1));
   }

   public Object next()
   {
      if (index >= (l.size() - 1))
      {
         throw (new NoSuchElementException());
      }

      index++;
      lastreturn = index;
      return (l.get(index));
   }

   public void remove()
   {
      if ((lastreturn >= 0) && (lastreturn < l.size()))
      {
         l.remove(lastreturn);
      }

      if (index > (l.size() - 1))
      {
         index = l.size() - 1;
      }
   }

   public int previousIndex()
   {
      if (index == -1)
         return (index);
      else
         return (index - 1);
   }

   public int nextIndex()
   {
      return (index + 1);
   }

   public void add(Object o)
   {
      throw (new UnsupportedOperationException());
   }

   public boolean hasPrevious()
   {
      return (index > 0);
   }

   public void set(Object o)
   {
      l.set(index, o);
   }

   public Object previous()
   {
      if (index <= 0)
      {
         throw (new NoSuchElementException());
      }

      index--;
      lastreturn = index;
      return (l.get(index));
   }
}