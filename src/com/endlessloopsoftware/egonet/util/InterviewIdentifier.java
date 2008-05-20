/**
 * <p>Title: Egocentric Networks Client Program</p>
 * <p>Description: Subject Interview Client</p>
 * <p>Copyright: Copyright (c) 2002 - 2004 </p>
 * <p>Company: Endless Loop Software</p>
 * @author Peter Schoaff
 *
 * $Id: InterviewIdentifier.java,v 1.1 2004/04/01 15:12:28 admin Exp $
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
