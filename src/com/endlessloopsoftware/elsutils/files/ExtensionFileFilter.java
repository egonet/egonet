package com.endlessloopsoftware.elsutils.files;

/**
 * Endless Loop Software Utilities
 * Copyright (c) 2003, Endless Loop Software, Inc.
 *
 *  @author     $Author: schoaff $
 *  @date      	$Date: 2006-03-09 09:42:46 -0500 (Thu, 09 Mar 2006) $
 *  @version    $Id: ExtensionFileFilter.java 2 2006-03-09 14:42:46Z schoaff $
 *
 */

import java.io.File;

import javax.swing.filechooser.FileFilter;

/****
 * Filter used by file choosers to focus on question files
 */
public class ExtensionFileFilter
		extends FileFilter
{
	private	String 		description;
	private	String[]	extensions;

	/**
	 * Instantiates filter with a single file suffix
	 * @param	description	name of file filter
	 * @param	extension	single extension of files to accept
	 */
	public ExtensionFileFilter(String description, String extension)
	{
		this(description, new String[] {extension});
	}

	/**
	 * Instantiates filter with multiple file suffixes
	 * @param	description	name of file filter
	 * @param	extensions	list of extensions of files to accept
	 */
	public ExtensionFileFilter(String description, String[] extensions)
	{
		if (description == null)
		{
			this.description = extensions[0] + "{" + extensions.length + "}";
		}
		else
		{
			this.description = description;
		}

		// Copy array and convert to lower case
		this.extensions = (String[]) extensions.clone();
		toLower(this.extensions);
	}

	private void toLower(String array[])
	{
		for (int i = 0, n = array.length; i < n; i++)
		{
			if (array[i].indexOf(".") == 0)
			{
				array[i] = array[i].toLowerCase();
			}
			else
			{
				array[i] = "." + array[i].toLowerCase();
			}
		}
	}

	/**
	 * Accepts all directories. Accepts files with suffixes in list
	 * @param	f file handle to check
	 * @return	true iff directory or matching suffix
	 */
	public boolean accept(File f)
	{
		if (f.isDirectory())
		{
			return true;
		}
		else
		{
			String path = f.getAbsolutePath().toLowerCase();
			for (int i = 0, n = extensions.length; i < n; i++)
			{
				if (path.endsWith(extensions[i]))
				{
					return true;
				}
			}

			return false;
		}
	}

	/**
	 * Appends the correct suffix to the file created by the user
	 * removes any incorrect suffixes
	 * @param	f file handle to check
	 * @return	new correct file
	 */
	public File getCorrectFileName(File f)
	{
		if (accept(f))
		{
			return(f);
		}
		else
		{
			String path = f.getParent();
			String filename = f.getName();

			if ((filename.lastIndexOf(".") != -1) &&
				((filename.length() - filename.lastIndexOf(".")) <= 4))
			{
				filename = filename.substring(0, filename.lastIndexOf("."));
			}

			File newFile = new File(path, filename.concat(extensions[0]));

			return newFile;
		}
	}

	/**
	 * Returns description of file filter
	 * @return	description	description string
	 */
	public String getDescription()
	{
		return description;
	}
}
