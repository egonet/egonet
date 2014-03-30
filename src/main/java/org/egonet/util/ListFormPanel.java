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

import java.awt.Component;
import java.awt.Dimension;

import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;

import com.jgoodies.forms.layout.FormLayout;


public abstract class ListFormPanel
    extends JComponent
{
    /**
     * Creates a new ListFormPanel object.
     */
    public void buildPanel()
    {
        this.setLayout(new FormLayout("d:g", "d:g"));
        this.add(buildSplitPane());
    }

    /**
     * Builds and answers the split panel.
     */
    private JComponent buildSplitPane()
    {
        JComponent side = buildSideBar();
        JComponent main = buildMainPanel();

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, side, main);

        splitPane.setOneTouchExpandable(false);
        splitPane.setDividerLocation(0.33);

        //		  Provide minimum sizes for the two components in the split pane
        Dimension minimumSize = new Dimension(100, 100);
        side.setMinimumSize(minimumSize);
        main.setMinimumSize(minimumSize);

        return splitPane;
    }

    /**
     * Builds and answers the side bar.
     */
    public abstract JComponent buildSideBar();

    /**
     * Builds and answers the main panel.
     */
    public abstract JComponent buildMainPanel();

    /**
     * Creates and answers a <code>JScrollpane</code> that has no border.
     */
    public static JScrollPane createStrippedScrollPane(Component c)
    {
        JScrollPane scrollPane = new JScrollPane(c);
        scrollPane.setBorder(null);

        return scrollPane;
    }
}
