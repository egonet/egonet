package org.egonet.gui.menus;

import java.util.List;

/**
 * This class provides a context for what menus this MDI frame can display,
 * primarily for metadata that is needed to build initial JDesktopPane (the
 * non-document window). Once the parent frame menus are built, state changes
 * will be handled by the methods provided by each document window.
 * 
 * @author Martin
 * 
 */
public abstract class MenuContext {

	public abstract String getSourceName();
	public abstract List<MenuGroup> getMenuGroups();
}
