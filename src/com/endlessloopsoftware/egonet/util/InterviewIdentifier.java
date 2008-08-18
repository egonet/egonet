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
package com.endlessloopsoftware.egonet.util;

import java.io.Serializable;

public class InterviewIdentifier
   implements Serializable, Comparable
{
   private final Long   _id;
   private final String _firstName;
   private final String _lastName;
   
   /*******
    * Constructor for immutable object
    * @param id
    * @param first
    * @param last
    */
   public InterviewIdentifier(Long id, String first, String last)
   {
      _id          = id;
      _firstName   = first;
      _lastName    = last;
   }
   
   public String toString()
   {
      return (_lastName + ", " + _firstName);
   }
   
   /**
    * @return Returns the _firstName.
    */
   public String getFirstName()
   {
      return _firstName;
   }
   
   /**
    * @return Returns the _id.
    */
   public Long getId()
   {
      return _id;
   }
   
   /**
    * @return Returns the _lastName.
    */
   public String getLastName()
   {
      return _lastName;
   }

   /* (non-Javadoc)
    * @see java.lang.Comparable#compareTo(java.lang.Object)
    */
   public int compareTo(Object o)
   {
      InterviewIdentifier that = (InterviewIdentifier) o;
      
      int rval = this.getLastName().compareTo(that.getLastName());
      
      if (rval == 0)
         rval = this.getFirstName().compareTo(that.getFirstName());
      
      if (rval == 0)
         rval = this.getId().compareTo(that.getId());
      
      return rval;
   }
}
