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
