package com.endlessloopsoftware.elsutils.files;

/**
 * Endless Loop Software Utilities
 * Copyright (c) 2003, Endless Loop Software, Inc.
 *
 *  @author     $Author: schoaff $
 *  @date      	$Date: 2006-03-09 09:42:46 -0500 (Thu, 09 Mar 2006) $
 *  @version    $Id: DirList.java 2 2006-03-09 14:42:46Z schoaff $
 *
 */

import java.io.File;
import java.io.FilenameFilter;
import java.util.Arrays;

/****
 * Manages list of question files in the base library directory
 */
public class DirList
{
	public static String[] getDirList(final File path, final String suffix)
	{
		String[] list;

		if (suffix == null)
		{
			list = path.list();
		}
		else
		{
			list = path.list(new FilenameFilter() {
				public boolean accept(File dir, String n)
				{
					String f = new File(n).getName();
					return (f.toLowerCase().endsWith(suffix.toLowerCase()));
				}
			});

			if (list == null)
			{
				list = new String[0];
			}
		}

		Arrays.sort(list);

		return (list);
	}

	public static File getLibraryDirectory()
	{
		return new File("./lib/");
	}
}
