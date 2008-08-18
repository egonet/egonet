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
