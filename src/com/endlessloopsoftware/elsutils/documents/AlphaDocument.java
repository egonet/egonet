package com.endlessloopsoftware.elsutils.documents;

/**
 * Endless Loop Software Utilities
 * Copyright (c) 2003, Endless Loop Software, Inc.
 *
 *  @author     $Author: schoaff $
 *  @date      	$Date: 2006-03-09 09:42:46 -0500 (Thu, 09 Mar 2006) $
 *  @version    $Id: AlphaDocument.java 2 2006-03-09 14:42:46Z schoaff $
 *
 */
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

/****
 * Class for text fields which only want to contain a whole number
 * Extends PlainDocument
 * Only overrides the insertString method
 */
public class AlphaDocument extends PlainDocument
{
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
		char[]	source 	= str.toCharArray();
		char[]	result 	= new char[source.length];
		int 	j 		= 0;

		for (int i = 0; i < result.length; i++)
		{
			if (Character.isJavaIdentifierPart(source[i]))
			{
				result[j++] = source[i];
			}
		}

		super.insertString(offs, new String(result, 0, j), a);
	}
}
