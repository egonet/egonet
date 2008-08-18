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

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;


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