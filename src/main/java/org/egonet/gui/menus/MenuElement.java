package org.egonet.gui.menus;

import javax.swing.JMenuItem;

import net.sf.functionalj.tuple.Pair;

/**
 * Represents a menu element, which is basically a JMenuItem and a weight.
 * @author Martin
 *
 */
public abstract class MenuElement {
	public abstract Pair<Integer,JMenuItem> getMenuItem();
}
