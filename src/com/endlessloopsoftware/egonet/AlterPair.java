/**
 * <p>Title: Egocentric Networks Client Program</p>
 * <p>Description: Subject Interview Client</p>
 * <p>Copyright: Copyright (c) 2002 - 2004 </p>
 * <p>Company: Endless Loop Software</p>
 * @author Peter Schoaff
 *
 * $Id: AlterPair.java,v 1.3 2004/05/17 00:05:24 admin Exp $
 */

package com.endlessloopsoftware.egonet;

import java.io.Serializable;


/**
 * @author admin
 *
 *	@serial	primaryAlter
 *	@serial	secondaryAlter
 */
public final class AlterPair
	implements Serializable, Comparable
{
	private static final long serialVersionUID = 7526472295622776147L;
	
	private final int		_secondaryAlter;
	private final int		_primaryAlter;

	public AlterPair()
	{
		_primaryAlter 		= Shared.NO_ALTER;
		_secondaryAlter	   = Shared.NO_ALTER;
	}
	
	public AlterPair(int primary)
	{
		_primaryAlter 		= primary;
		_secondaryAlter	   = Shared.NO_ALTER;
	}
	
	public AlterPair(int primary, int secondary)
	{
		_primaryAlter 		= primary;
		_secondaryAlter	   = secondary;
	}
	
	/**
	 * @return Returns the _primaryAlter.
	 */
	public int getPrimaryAlter()
	{
		return _primaryAlter;
	}
	/**
	 * @return Returns the _secondaryAlter.
	 */
	public int getSecondaryAlter()
	{
		return _secondaryAlter;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() 
	{
		StringBuffer str = new StringBuffer();
		str.append("AlterPair [");
		str.append(" Primary: " 			+ _primaryAlter);
		str.append("; Secondary: "	 		+ _secondaryAlter);
		str.append(" ]");
		
		return str.toString();
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object obj)
	{
		AlterPair that = (AlterPair) obj;
		
		return ( (this.getPrimaryAlter() == that.getPrimaryAlter()) &&
					(this.getSecondaryAlter() == that.getSecondaryAlter()));
	}
	
	public int hashCode()
	{
		int result = 17;
		result = 37*result + this.getPrimaryAlter();
		result = 37*result + this.getSecondaryAlter();		
		
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(Object o)
	{
		AlterPair that = (AlterPair) o;
		
		int rval = this._primaryAlter - that._primaryAlter;
		
		if (rval == 0)
			rval = this._secondaryAlter - that._secondaryAlter;
		
		return rval;
	}
	
	public int[] toArray()
	{
		int[] alterArray;
		
		if (getPrimaryAlter() == Shared.NO_ALTER)
		{
			alterArray = new int[0];
		}
		else if (getSecondaryAlter() == Shared.NO_ALTER)
		{
			alterArray = new int[] {getPrimaryAlter()};
		}
		else
		{
			alterArray = new int[] {getPrimaryAlter(), getSecondaryAlter()};
		}
		
		return alterArray;
	}
}
