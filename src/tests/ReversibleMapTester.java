package tests;

import vitro.util.*;
import java.util.*;

import org.junit.Test;
import static org.junit.Assert.*;

public class ReversibleMapTester {
	
	@Test
	public void testAdd() {
		ReversibleMap<String, Integer>  forwards = new ReversibleMap<String, Integer>();
		ReversibleMap<Integer, String> backwards = forwards.reverse();

		forwards.put("foo", new Integer(31));

		assertTrue(backwards.containsKey(new Integer(31)));
		assertEquals("foo", backwards.get(new Integer(31)));
	}
	
	@Test
	public void testRemove() {
		ReversibleMap<String, Integer>  forwards = new ReversibleMap<String, Integer>();
		ReversibleMap<Integer, String> backwards = forwards.reverse();

		forwards.put("foo", new Integer(31));

		assertTrue(backwards.containsKey(new Integer(31)));
		assertEquals("foo", backwards.get(new Integer(31)));

		backwards.remove(new Integer(31));

		assertFalse(forwards.containsKey("foo"));
	}

	@Test
	public void testUpdate() {
		ReversibleMap<String, Integer>  forwards = new ReversibleMap<String, Integer>();
		ReversibleMap<Integer, String> backwards = forwards.reverse();

		forwards.put("foo", new Integer(31));

		backwards.put(new Integer(31), "bar");

		assertFalse(forwards.containsKey("foo"));
		assertTrue(forwards.containsKey("bar"));
		assertEquals(new Integer(31), forwards.get("bar"));
	}
}