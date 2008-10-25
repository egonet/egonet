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

import java.text.ParseException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import javax.swing.text.DefaultFormatter;

/**
 * A regular expression based implementation of <code>AbstractFormatter</code>.
 */
public class RegexFormatter extends DefaultFormatter
{
	private Pattern pattern;
	private Matcher matcher;

	public RegexFormatter()
	{
		super();
	}

	/**
	 * Creates a regular expression based <code>AbstractFormatter</code>.
	 * <code>pattern</code> specifies the regular expression that will
	 * be used to determine if a value is legal.
	 */
	public RegexFormatter(String pattern) throws PatternSyntaxException
	{
		this();
		setPattern(Pattern.compile(pattern));
	}

	/**
	 * Creates a regular expression based <code>AbstractFormatter</code>.
	 * <code>pattern</code> specifies the regular expression that will
	 * be used to determine if a value is legal.
	 */
	public RegexFormatter(Pattern pattern)
	{
		this();
		setPattern(pattern);
	}

	/**
	 * Sets the pattern that will be used to determine if a value is
	 * legal.
	 */
	public void setPattern(Pattern pattern)
	{
		this.pattern = pattern;
	}

	/**
	 * Returns the <code>Pattern</code> used to determine if a value is
	 * legal.
	 */
	public Pattern getPattern()
	{
		return pattern;
	}

	/**
	 * Sets the <code>Matcher</code> used in the most recent test
	 * if a value is legal.
	 */
	protected void setMatcher(Matcher matcher)
	{
		this.matcher = matcher;
	}

	/**
	 * Returns the <code>Matcher</code> from the most test.
	 */
	protected Matcher getMatcher()
	{
		return matcher;
	}

	/**
	 * Parses <code>text</code> returning an arbitrary Object. Some
	 * formatters may return null.
	 * <p>
	 * If a <code>Pattern</code> has been specified and the text
	 * completely matches the regular expression this will invoke
	 * <code>setMatcher</code>.
	 *
	 * @throws ParseException if there is an error in the conversion
	 * @param text String to convert
	 * @return Object representation of text
	 */
	public Object stringToValue(String text) throws ParseException
	{
		Pattern pattern = getPattern();

		if (pattern != null)
		{
			Matcher matcher = pattern.matcher(text);

			if (matcher.matches())
			{
				setMatcher(matcher);
				return super.stringToValue(text);
			}
			throw new ParseException("Pattern did not match", 0);
		}
		return text;
	}
}
