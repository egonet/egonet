package org.egonet.test.graph;

import static org.junit.Assert.*;

import java.util.Set;

import org.junit.*;
import junit.framework.JUnit4TestAdapter;

import org.egonet.graph.IndexedSetOfSets;

import com.google.common.collect.Sets;

public class IndexedSetOfSetsTest {

	private Set<String> item1() {
		Set<String> item = Sets.newHashSet();
		item.add("a1");
		item.add("b1");
		item.add("c1");
		return item;
	}

	private Set<String> item2() {
		Set<String> item = Sets.newHashSet();
		item.add("a2");
		item.add("b2");
		item.add("c2");
		return item;
	}

	private Set<String> item3() {
		Set<String> item = Sets.newHashSet();
		item.add("a1");
		item.add("b1");
		item.add("c2");
		return item;
	}
	
	private IndexedSetOfSets<String> emptySet() {
		IndexedSetOfSets<String> set = new IndexedSetOfSets<String>();
		return set;
	}
	
	private IndexedSetOfSets<String> set1() {
		IndexedSetOfSets<String> set = new IndexedSetOfSets<String>();
		set.add(item1());
		return set;
	}
	
	private IndexedSetOfSets<String> set2() {
		IndexedSetOfSets<String> set = new IndexedSetOfSets<String>();
		set.add(item2());
		return set;
	}
	
	private IndexedSetOfSets<String> set12() {
		IndexedSetOfSets<String> set = new IndexedSetOfSets<String>();
		set.add(item1());
		set.add(item2());
		return set;
	}
	
	private IndexedSetOfSets<String> set21() {
		IndexedSetOfSets<String> set = new IndexedSetOfSets<String>();
		set.add(item2());
		set.add(item1());
		return set;
	}
	
	private IndexedSetOfSets<String> set13() {
		IndexedSetOfSets<String> set = new IndexedSetOfSets<String>();
		set.add(item1());
		set.add(item3());
		return set;
	}
	
	private IndexedSetOfSets<String> set123() {
		IndexedSetOfSets<String> set = new IndexedSetOfSets<String>();
		set.add(item1());
		set.add(item2());
		set.add(item3());
		return set;
	}
	
	@Test
	public void testAdd() {
		assertFalse("Adding an item to a set changes the set.", 
				emptySet().equals(set1()));
		assertFalse("Adding another item to a set changes the set.", 
				set1().equals(set12()));
		assertEquals("Order of adding does not matter.", 
				set12(), set21());
	}
	
	@Test
	public void testContains() {
		assertTrue("A set contains the item placed in it.", 
				set1().contains(item1()));
		assertTrue("A larger set contains the first item placed in it.", 
				set12().contains(item1()));
		assertTrue("A larger set contains the second item placed in it.", 
				set12().contains(item2()));
		assertFalse("A set does not contain an item that was not placed in it.", 
				set1().contains(item2()));
	}
	
	@Test
	public void testRemove() {
		IndexedSetOfSets<String> set12minus2 = set12();
		set12minus2.remove(item2());
		assertEquals("Remove is the inverse of add.",
				set12minus2,set1());
		
		IndexedSetOfSets<String> set12minus1 = set12();
		set12minus1.remove(item1());
		assertEquals("Remove is the inverse of add.",
				set12minus1,set2());
		
		IndexedSetOfSets<String> set12minus1and2 = set12();
		set12minus1and2.remove(item1());
		set12minus1and2.remove(item2());
		assertEquals("Remove is the inverse of add.",
				set12minus1and2,emptySet());
	}
	
	@Test
	public void testFindByIndex() {
		assertEquals("findByIndex returns empty set when used on empty set.",
				emptySet(),emptySet().findByIndex("a1"));
		assertEquals("findByIndex returns empty set when no relevant sets available.",
				emptySet(),set13().findByIndex("a2"));
		assertEquals("findByIndex finds the matching set.",
				set1(),set12().findByIndex("a1"));
		assertEquals("findByIndex can find more than one result.",
				set13(),set123().findByIndex("b1"));
	}

	public static junit.framework.Test suite() {
      return new JUnit4TestAdapter(IndexedSetOfSetsTest.class);
	}
}
