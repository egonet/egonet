package com.endlessloopsoftware.elsutils.files;

/**
 * Endless Loop Software Utilities
 * Copyright (c) 2003, Endless Loop Software, Inc.
 *
 *  @author     $Author: schoaff $
 *  @date      	$Date: 2006-03-09 09:42:46 -0500 (Thu, 09 Mar 2006) $
 *  @version    $Id: FileCreateException.java 2 2006-03-09 14:42:46Z schoaff $
 *
 */

public class FileCreateException extends Exception
{
	public FileCreateException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}
	public FileCreateException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}
	public FileCreateException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}
	public boolean report;
	public FileCreateException() {}
	public FileCreateException(boolean report)
	{
		this.report = report;
	}
}