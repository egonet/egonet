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
package org.egonet.util.listbuilder;

import java.util.Arrays;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;

public class ListBuilderPresets
{
	public static Map<String,Selection[]> getPresets()
	{
		Map<String,Selection[]> ret = new HashMap<String,Selection[]>();
		ret.put("Yes/No", Arrays.copyOf(YES_NO, YES_NO.length));
		ret.put("Gender", Arrays.copyOf(GENDER, GENDER.length));
		ret.put("States", Arrays.copyOf(STATES, STATES.length));
		ret.put("Kin", Arrays.copyOf(KIN, KIN.length));

		return ret;
	}
	
	public static final Selection[] YES_NO =
	{ new Selection("Yes", 1, 0, false), new Selection("No", 0, 1, false), };

	public static final Selection[] GENDER =
	{ new Selection("Male", 1, 0, false), new Selection("Female", 0, 1, false), };

	public static final Selection[] STATES =
	{ new Selection("Alabama", 49, 0, false), new Selection("Alaska", 48, 1, false),
			new Selection("Arizona", 47, 2, false), new Selection("Arkansas", 46, 3, false),
			new Selection("California", 45, 4, false), new Selection("Colorado", 44, 5, false),
			new Selection("Connecticut", 43, 6, false), new Selection("Delaware", 42, 7, false),
			new Selection("Florida", 41, 8, false), new Selection("Georgia", 40, 9, false),
			new Selection("Hawaii", 39, 10, false), new Selection("Idaho", 38, 11, false),
			new Selection("Illinois", 37, 12, false), new Selection("Indiana", 36, 13, false),
			new Selection("Iowa", 35, 14, false), new Selection("Kansas", 34, 15, false),
			new Selection("Kentucky", 33, 16, false), new Selection("Louisiana", 32, 17, false),
			new Selection("Maine", 31, 18, false), new Selection("Maryland", 30, 19, false),
			new Selection("Massachusetts", 29, 20, false), new Selection("Michigan", 28, 21, false),
			new Selection("Minnesota", 27, 22, false), new Selection("Mississippi", 26, 23, false),
			new Selection("Missouri", 25, 24, false), new Selection("Montana", 24, 25, false),
			new Selection("Nebraska", 23, 26, false), new Selection("Nevada", 22, 27, false),
			new Selection("New Hampshire", 21, 28, false), new Selection("New Jersey", 20, 29, false),
			new Selection("New Mexico", 19, 30, false), new Selection("New York", 18, 31, false),
			new Selection("North Carolina", 17, 32, false),
			new Selection("North Dakota", 16, 33, false), new Selection("Ohio", 15, 34, false),
			new Selection("Oklahoma", 14, 35, false), new Selection("Oregon", 13, 36, false),
			new Selection("Pennsylvania", 12, 37, false),
			new Selection("Rhode Island", 11, 38, false),
			new Selection("South Carolina", 10, 39, false),
			new Selection("South Dakota", 9, 40, false), new Selection("Tennessee", 8, 41, false),
			new Selection("Texas", 7, 42, false), new Selection("Utah", 6, 43, false),
			new Selection("Vermont", 5, 44, false), new Selection("Virginia", 4, 45, false),
			new Selection("Washington", 3, 46, false), new Selection("West Virginia", 2, 47, false),
			new Selection("Wisconsin", 1, 48, false), new Selection("Wyoming", 0, 49, false), };
	
	public static final Selection[] KIN = getKinTerms();
	
	private static Selection[] getKinTerms() {
		ArrayList<Selection> kinTerms = new ArrayList<Selection>();
		String[] kinStrings = { // This list also appears in org.egonet.util.ListBuilder.presetLists.
     	   "Grand Mother", "Grand Father", "Mother", "Father", "Grand Son",
     	   "Grand Daughter", "Son", "Daughter", "Sister", "Brother", "Aunt",
     	   "Uncle", "Niece", "Nephew", "Cousing", "Mother-in-Law",
     	   "Father-in-Law", "Sister-in-Law", "Brother-in-Law",
     	   "Step Son", "Step Daughter", "Half-Brother", "Half-Sister"
        };
		for(Integer i = 0; i < kinStrings.length; i++) {
			kinTerms.add(new Selection(kinStrings[i],kinStrings.length-1-i,i,false)); // Following states example
		}
		return kinTerms.toArray(new Selection[]{});
	}
}
