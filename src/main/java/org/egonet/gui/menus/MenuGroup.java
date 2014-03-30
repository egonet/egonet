package org.egonet.gui.menus;

import net.sf.functionalj.tuple.Pair;

/**
 * A group of related menu items. Normally, the union of all MenuGroup items in
 * an MDI parent frame will yield a larger, common set of MenuGroups like "File"
 * or "Edit" menus. It may be combined from many individual file and edit menu
 * options. These will be separated by a menu separator.
 * 
 * @author Martin
 * 
 */
public abstract class MenuGroup {

	public enum CommonGroups {
		FILE("File"),
		EDIT("Edit"),
		WINDOW("Window"),
		ABOUT("About");
		
		public final String name;
		private CommonGroups(String name) {
			this.name = name;
		}
	}
	
	public abstract Pair<Integer,MenuElement> getMenuElements();
	public abstract String getGroupSignifier();
}
