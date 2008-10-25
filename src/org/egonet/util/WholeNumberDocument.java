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
import java.awt.Toolkit;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

/****
 * Class for text fields which only want to contain a whole number
 * Extends PlainDocument
 * Only overrides the insertString method
 */
public class WholeNumberDocument extends PlainDocument
{
	protected int 	maxLength = Integer.MAX_VALUE;
	
	/**
	 * 
	 */
	public WholeNumberDocument()
	{
		super();
	}

	/**
	 * 
	 */
	public WholeNumberDocument(int maxLength)
	{
		super();
		this.maxLength = maxLength;
	}

	/**
	 * Only allows integers to be inserted in the string
	 * If addition is string calls parent method, otherwise intercepts and disallows
	 *
	 * @param offs offset of addition
	 * @param str string added
	 * @param a Attribute Set
	 */
	public void insertString(int offs, String str, AttributeSet a)
			throws BadLocationException
	{
		char[]	source 		= str.toCharArray();
		char[]	result 		= new char[source.length];
		int		maxLength	= this.maxLength - offs;	
		int 	j 			= 0;

		for (int i = 0; i < Math.min(result.length, maxLength); i++)
		{
			if (Character.isDigit(source[i]))
			{
				result[j++] = source[i];
			}
			else
			{
				Toolkit.getDefaultToolkit().beep();
			}
		}
		
		super.insertString(offs, new String(result, 0, j), a);
	}
}
