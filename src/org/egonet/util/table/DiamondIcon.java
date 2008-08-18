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
package org.egonet.util.table;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Polygon;

import javax.swing.Icon;

/**
 * Icon to display on color cell of table
 * @author sonam
 *
 */
class DiamondIcon implements Icon {
	  private Color color;

	  private boolean selected;

	  private int width;

	  private int height;

	  private Polygon poly;

	  private static final int DEFAULT_WIDTH = 10;

	  private static final int DEFAULT_HEIGHT = 10;

	  public DiamondIcon(Color color) {
	    this(color, true, DEFAULT_WIDTH, DEFAULT_HEIGHT);
	  }

	  public DiamondIcon(Color color, boolean selected) {
	    this(color, selected, DEFAULT_WIDTH, DEFAULT_HEIGHT);
	  }

	  public DiamondIcon(Color color, boolean selected, int width, int height) {
	    this.color = color;
	    this.selected = selected;
	    this.width = width;
	    this.height = height;
	    initPolygon();
	  }

	  private void initPolygon() {
	    poly = new Polygon();
	    int halfWidth = width / 2;
	    int halfHeight = height / 2;
	    poly.addPoint(0, halfHeight);
	    poly.addPoint(halfWidth, 0);
	    poly.addPoint(width, halfHeight);
	    poly.addPoint(halfWidth, height);
	  }

	  public int getIconHeight() {
	    return height;
	  }

	  public int getIconWidth() {
	    return width;
	  }

	  public void paintIcon(Component c, Graphics g, int x, int y) {
	    g.setColor(color);
	    g.translate(x, y);
	    if (selected) {
	      g.fillPolygon(poly);
	    } else {
	      g.drawPolygon(poly);
	    }
	    g.translate(-x, -y);
	  }
	}

