package com.endlessloopsoftware.elsutils;

import java.awt.Frame;

import javax.swing.UIManager;

/**
 * Endless Loop Software Utilities
 * Copyright (c) 2003, Endless Loop Software, Inc.
 *
 *  @author     $Author: schoaff $
 *  @date      	$Date: 2006-03-09 09:42:46 -0500 (Thu, 09 Mar 2006) $
 *  @version    $Id: TestProgress.java 2 2006-03-09 14:42:46Z schoaff $
 *
 */

class TestProgress extends ELSProgressBar
{
	public TestProgress(Frame frame, String title)
	{
		super(frame, title, null);
	}

	public Object construct()
	{
		for (int i = 0; i < 100; i++)
		{
			for (int j = 0; j < 10000000; j++)
			{
				;
			}

			setTitle(Integer.toString(i));
			setMessage(Integer.toString(i));
			// setValue(i);
		}

		close();

		return null;
	}

	public void finished()
	{
//		close();
	}

	public static void main(String[] args)
	{
		final ELSProgressBar fred = new TestProgress(null, "The Title");

		try
		{
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		fred.open();

		/*
		while (fred.getR)
		{
			try
			{
				Thread.sleep(100);
			}
			catch (InterruptedException ex)
			{
				break;
			}
		}
		*/

		System.exit(1);
	}
}

