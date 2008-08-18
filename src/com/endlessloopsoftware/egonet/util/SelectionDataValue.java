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


/**
 * Value object for SelectionEJB.
 *
 * @xdoclet-generated at ${TODAY}
 * @copyright 2004 Endless Loop Software
 *  Inc.
 */
public class SelectionDataValue
   extends java.lang.Object
   implements java.io.Serializable, Comparable
{
   private String id;
   private boolean idHasBeenSet = false;
   private java.lang.String text;
   private boolean textHasBeenSet = false;
   private int value;
   private boolean valueHasBeenSet = false;
   private int index;
   private boolean indexHasBeenSet = false;
   private boolean adjacent;
   private boolean adjacentHasBeenSet = false;

   private com.endlessloopsoftware.egonet.interfaces.SelectionEJBPK pk;

   public SelectionDataValue()
   {
	  pk = new com.endlessloopsoftware.egonet.interfaces.SelectionEJBPK();
   }

   public SelectionDataValue( String id,java.lang.String text,int value,int index,boolean adjacent )
   {
	  this.id = id;
	  idHasBeenSet = true;
	  this.text = text;
	  textHasBeenSet = true;
	  this.value = value;
	  valueHasBeenSet = true;
	  this.index = index;
	  indexHasBeenSet = true;
	  this.adjacent = adjacent;
	  adjacentHasBeenSet = true;
	  pk = new com.endlessloopsoftware.egonet.interfaces.SelectionEJBPK(this.getId());
   }

   //TODO Cloneable is better than this !
   public SelectionDataValue( SelectionDataValue otherValue )
   {
	  this.id = otherValue.id;
	  idHasBeenSet = true;
	  this.text = otherValue.text;
	  textHasBeenSet = true;
	  this.value = otherValue.value;
	  valueHasBeenSet = true;
	  this.index = otherValue.index;
	  indexHasBeenSet = true;
	  this.adjacent = otherValue.adjacent;
	  adjacentHasBeenSet = true;

	  pk = new com.endlessloopsoftware.egonet.interfaces.SelectionEJBPK(this.getId());
   }

   public com.endlessloopsoftware.egonet.interfaces.SelectionEJBPK getPrimaryKey()
   {
	  return pk;
   }

   public void setPrimaryKey( com.endlessloopsoftware.egonet.interfaces.SelectionEJBPK pk )
   {
      // it's also nice to update PK object - just in case
      // somebody would ask for it later...
      this.pk = pk;
	  setId( pk.id );
   }

   public String getId()
   {
	  return this.id;
   }

   public void setId( String id )
   {
	  this.id = id;
	  idHasBeenSet = true;

		 pk.setId(id);
   }

   public boolean idHasBeenSet(){
	  return idHasBeenSet;
   }
   public java.lang.String getText()
   {
	  return this.text;
   }

   public void setText( java.lang.String text )
   {
	  this.text = text;
	  textHasBeenSet = true;

   }

   public boolean textHasBeenSet(){
	  return textHasBeenSet;
   }
   public int getValue()
   {
	  return this.value;
   }

   public void setValue( int value )
   {
	  this.value = value;
	  valueHasBeenSet = true;

   }

   public boolean valueHasBeenSet(){
	  return valueHasBeenSet;
   }
   public int getIndex()
   {
	  return this.index;
   }

   public void setIndex( int index )
   {
	  this.index = index;
	  indexHasBeenSet = true;

   }

   public boolean indexHasBeenSet(){
	  return indexHasBeenSet;
   }
   public boolean getAdjacent()
   {
	  return this.adjacent;
   }

   public void setAdjacent( boolean adjacent )
   {
	  this.adjacent = adjacent;
	  adjacentHasBeenSet = true;

   }

   public boolean adjacentHasBeenSet(){
	  return adjacentHasBeenSet;
   }

   public String toString()
   {
	  StringBuffer str = new StringBuffer("{");

	  str.append("id=" + getId() + " " + "text=" + getText() + " " + "value=" + getValue() + " " + "index=" + getIndex() + " " + "adjacent=" + getAdjacent());
	  str.append('}');

	  return(str.toString());
   }

   /**
    * A Value Object has an identity if the attributes making its Primary Key have all been set. An object without identity is never equal to any other object.
    *
    * @return true if this instance has an identity.
    */
   protected boolean hasIdentity()
   {
	  boolean ret = true;
	  ret = ret && idHasBeenSet;
	  return ret;
   }

   public boolean equals(Object other)
   {
      if (this == other)
         return true;
	  if ( ! hasIdentity() ) return false;
	  if (other instanceof SelectionDataValue)
	  {
		 SelectionDataValue that = (SelectionDataValue) other;
		 if ( ! that.hasIdentity() ) return false;
		 boolean lEquals = true;
		 if( this.id == null )
		 {
			lEquals = lEquals && ( that.id == null );
		 }
		 else
		 {
			lEquals = lEquals && this.id.equals( that.id );
		 }

		 lEquals = lEquals && isIdentical(that);

		 return lEquals;
	  }
	  else
	  {
		 return false;
	  }
   }

   public boolean isIdentical(Object other)
   {
	  if (other instanceof SelectionDataValue)
	  {
		 SelectionDataValue that = (SelectionDataValue) other;
		 boolean lEquals = true;
		 if( this.text == null )
		 {
			lEquals = lEquals && ( that.text == null );
		 }
		 else
		 {
			lEquals = lEquals && this.text.equals( that.text );
		 }
		 lEquals = lEquals && this.value == that.value;
		 lEquals = lEquals && this.index == that.index;
		 lEquals = lEquals && this.adjacent == that.adjacent;

		 return lEquals;
	  }
	  else
	  {
		 return false;
	  }
   }

   public int hashCode(){
	  int result = 17;
      result = 37*result + ((this.id != null) ? this.id.hashCode() : 0);

      result = 37*result + ((this.text != null) ? this.text.hashCode() : 0);

      result = 37*result + (int) value;

      result = 37*result + (int) index;

      result = 37*result + (adjacent ? 0 : 1);

	  return result;
   }

	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(Object arg0) 
	{
		SelectionDataValue that = (SelectionDataValue) arg0;
		int rval = 0;
		
		if (this.indexHasBeenSet() && that.indexHasBeenSet())
		{
			rval = this.getIndex() - that.getIndex();
		}
		
		if (this.valueHasBeenSet() && that.valueHasBeenSet())
		{
			rval = that.getValue() - this.getValue();
		}
		
		return rval;
	}

}
