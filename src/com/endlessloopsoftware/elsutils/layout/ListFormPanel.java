/**
 * Endless Loop Software Utilities
 * Copyright (c) 2003, Endless Loop Software, Inc.
 *
 *  @author     $Author: schoaff $
 *  @date      	$Date: 2006-03-09 09:42:46 -0500 (Thu, 09 Mar 2006) $
 *  @version    $Id: ListFormPanel.java 2 2006-03-09 14:42:46Z schoaff $
 *
 */

package com.endlessloopsoftware.elsutils.layout;

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
