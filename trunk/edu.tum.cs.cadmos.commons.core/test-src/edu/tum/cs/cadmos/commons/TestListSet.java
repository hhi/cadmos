package edu.tum.cs.cadmos.commons;

import static java.util.Arrays.asList;
import static junit.framework.Assert.assertSame;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import edu.tum.cs.cadmos.commons.core.AbstractIdentifiable;
import edu.tum.cs.cadmos.commons.core.ListSet;

public class TestListSet {

	private static class Element extends AbstractIdentifiable {

		public Element(String id) {
			super(id);
		}

	}

	@Test
	public void testHashCode() {
		final ListSet<Element> ls = new ListSet<>();
		final List<Element> list = new ArrayList<>();
		for (int i = 0; i < 10; i++) {
			ls.add(new Element(String.valueOf(i)));
			list.add(new Element(String.valueOf(i)));
		}
		assertEquals(list.hashCode(), ls.hashCode());
	}

	@Test
	public void testListSet() {
		final ListSet<Element> ls = new ListSet<>();
		assertTrue(ls.isEmpty());
	}

	@Test
	public void testListSetCollectionOfE() {
		final List<Element> list = new ArrayList<>();
		for (int i = 0; i < 10; i++) {
			list.add(new Element(String.valueOf(i)));
		}
		final ListSet<Element> ls = new ListSet<>(list);
		for (int i = 0; i < 10; i++) {
			assertTrue(ls.get(i).getId().equals(String.valueOf(i)));
		}
	}

	@Test
	public void testIterator() {
		final ListSet<Element> ls = new ListSet<>();
		for (int i = 0; i < 10; i++) {
			ls.add(new Element(String.valueOf(i)));
		}
		int i = 0;
		for (final Element e : ls) {
			assertEquals(String.valueOf(i++), e.getId());
		}
	}

	@Test
	public void testAdd() {
		final ListSet<Element> ls = new ListSet<>();
		ls.add(new Element("x"));
		assertEquals(1, ls.size());
		assertEquals("x", ls.getFirst().getId());
	}

	@Test
	public void testAddAll() {
		final ListSet<Element> ls = new ListSet<>();
		ls.addAll(asList(new Element("x"), new Element("y")));
		assertEquals(2, ls.size());
		assertEquals("x", ls.getFirst().getId());
		assertEquals("y", ls.getLast().getId());
	}

	@Test
	public void testContainsE() {
		final ListSet<Element> ls = new ListSet<>();
		ls.add(new Element("x"));
		assertTrue(ls.contains(new Element("x")));
	}

	@Test
	public void testContainsString() {
		final ListSet<Element> ls = new ListSet<>();
		ls.add(new Element("x"));
		assertTrue(ls.contains("x"));
	}

	@Test
	public void testGetE() {
		final ListSet<Element> ls = new ListSet<>();
		final Element x = new Element("x");
		ls.add(x);
		assertEquals(x, ls.get(new Element("x")));
	}

	@Test
	public void testGetString() {
		final ListSet<Element> ls = new ListSet<>();
		final Element x = new Element("x");
		ls.add(x);
		assertEquals(x, ls.get("x"));
	}

	@Test
	public void testGetInt() {
		final ListSet<Element> ls = new ListSet<>();
		final Element x = new Element("x");
		ls.add(x);
		assertEquals(x, ls.get(0));
	}

	@Test
	public void testGetFirstGetLast() {
		final ListSet<Element> ls = new ListSet<>();
		final Element x = new Element("x");
		final Element y = new Element("y");
		ls.addAll(asList(x, y));
		assertSame(x, ls.getFirst());
		assertSame(y, ls.getLast());
	}

}
