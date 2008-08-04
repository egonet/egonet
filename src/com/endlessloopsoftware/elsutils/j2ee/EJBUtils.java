package com.endlessloopsoftware.elsutils.j2ee;

import java.security.SecureRandom;

/**
 * Endless Loop Software Utilities
 * Copyright (c) 2003, Endless Loop Software, Inc.
 *
 *  @author     $Author: schoaff $
 *  @version    $Id: EJBUtils.java 3 2006-03-09 15:13:47Z schoaff $
 *
 */
public class EJBUtils
{
	// initialise the secure random instance
	private static final SecureRandom seeder = new SecureRandom();

	public EJBUtils()
	{
	}

	/**
	 * A 24 byte GUID generator (Globally Unique ID). These artificial keys SHOULD <strong>NOT </strong> be seen by the user,
	 * not even touched by the DBA but with very rare exceptions, just manipulated by the database and the programs.
	 *
	 * Usage: Add an id field (type java.lang.String) to your EJB, and add setId(XXXUtil.generateGUID(this)); to the ejbCreate method.
	 */
	public static final String generateGUID(Object target)
	{
		long timeNow      = System.currentTimeMillis();
		int timeLow       = (int) timeNow & 0xFFFFFFFF;
		int node          = seeder.nextInt();

		StringBuffer guid = new StringBuffer(24);
		guid.append(hexFormat(timeLow, 8));
		guid.append(hexFormat(System.identityHashCode(target), 8));
		guid.append(hexFormat(node, 8));
		return guid.toString();
	}

	private static int getInt(byte bytes[])
	{
		int i = 0;
		int j = 24;
		for (int k = 0; j >= 0; k++)
		{
			int l = bytes[k] & 0xff;
			i += l << j;
			j -= 8;
		}
		return i;
	}

	private static String hexFormat(int i, int j)
	{
		String s = Integer.toHexString(i);
		return padHex(s, j) + s;
	}

	private static String padHex(String s, int i)
	{
		StringBuffer tmpBuffer = new StringBuffer();
		if (s.length() < i)
		{
			for (int j = 0; j < i - s.length(); j++)
			{
				tmpBuffer.append('0');
			}
		}
		return tmpBuffer.toString();
	}
}