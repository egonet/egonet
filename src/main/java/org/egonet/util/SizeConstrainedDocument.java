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
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

/****
 * Class for text fields which only want to contain a whole number
 * Extends PlainDocument
 * Only overrides the insertString method
 */
public class SizeConstrainedDocument extends PlainDocument
{
	int 	maxLength = 0;
	
	/**
	 * 
	 */
	public SizeConstrainedDocument(int maxLength)
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
		int		maxLength	= this.maxLength - offs;	
		int 	j 			= Math.min(source.length, maxLength);
		
		super.insertString(offs, new String(source, 0, j), a);
	}
}
