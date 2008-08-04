package com.endlessloopsoftware.elsutils.layout;

import java.awt.*;
import javax.swing.*;
/**
 * Endless Loop Software Utilities
 * Copyright (c) 2003, Endless Loop Software, Inc.
 *
 *  @author     $Author: schoaff $
 *  @date      	$Date: 2006-03-09 09:42:46 -0500 (Thu, 09 Mar 2006) $
 *  @version    $Id: ArrowIcon.java 2 2006-03-09 14:42:46Z schoaff $
 *
 */
public class ArrowIcon implements Icon, SwingConstants
{
	private int width = 9;
	private int height = 18;

	private int[] xPoints = new int[4];
	private int[] yPoints = new int[4];

	public ArrowIcon(int direction)
	{
		if (direction == SwingConstants.LEFT)
		{
			xPoints[0] = width;
			yPoints[0] = -1;
			xPoints[1] = width;
			yPoints[1] = height;
			xPoints[2] = 0;
			yPoints[2] = height / 2;
			xPoints[3] = 0;
			yPoints[3] = height / 2 - 1;
		}
		else /* direction == TRAILING */ {
			xPoints[0] = 0;
			yPoints[0] = -1;
			xPoints[1] = 0;
			yPoints[1] = height;
			xPoints[2] = width;
			yPoints[2] = height / 2;
			xPoints[3] = width;
			yPoints[3] = height / 2 - 1;
		}
	}

	public int getIconHeight()
	{
		return height;
	}

	public int getIconWidth()
	{
		return width;
	}

	public void paintIcon(Component c, Graphics g, int x, int y)
	{
		int length = xPoints.length;
		int adjustedXPoints[] = new int[length];
		int adjustedYPoints[] = new int[length];

		for (int i = 0; i < length; i++)
		{
			adjustedXPoints[i] = xPoints[i] + x;
			adjustedYPoints[i] = yPoints[i] + y;
		}

		if (c.isEnabled())
		{
			g.setColor(c.getForeground());
		}
		else
		{
			g.setColor(Color.gray);
		}

		g.fillPolygon(adjustedXPoints, adjustedYPoints, length);
	}
}