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

import java.awt.Frame;

import javax.swing.UIManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


class TestProgress extends ELSProgressBar
{
	final private static Logger logger = LoggerFactory.getLogger(ELSProgressBar.class);
	
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
		catch (Exception ex)
		{
			logger.error(ex.toString());
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

