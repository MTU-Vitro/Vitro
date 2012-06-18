package tests;

import vitro.util.*;
import java.util.*;

import org.junit.Test;
import static org.junit.Assert.*;

public class ObservableMapTester {
	
	@Test
	public void testAdd() {
		ObservableMap<String, Integer> map = new ObservableMap<String, Integer>();
		TestObserver obs = new TestObserver();
		map.addObserver(obs);

		map.put("hello", new Integer(12));

		assertTrue(obs.added);
		assertFalse(obs.removed);
		assertEquals("hello",         obs.entry.getKey());
		assertEquals(new Integer(12), obs.entry.getValue());
		assertEquals(map,             obs.sender);

		assertEquals(System.identityHashCode(map), System.identityHashCode(obs.sender));
	}

	@Test
	public void testRemove() {
		ObservableMap<String, Integer> map = new ObservableMap<String, Integer>();

		map.put("world", new Integer(37));

		TestObserver obs = new TestObserver();
		map.addObserver(obs);

		map.remove("world");

		assertTrue(obs.removed);
		assertFalse(obs.added);
		assertEquals("world",         obs.entry.getKey());
		assertEquals(new Integer(37), obs.entry.getValue());
		assertEquals(map,             obs.sender);

		assertEquals(System.identityHashCode(map), System.identityHashCode(obs.sender));
	}

	private static class TestObserver implements CollectionObserver<Map.Entry<String, Integer>> {
		public ObservableCollection sender = null;
		public Map.Entry<String, Integer> entry = null;
		public boolean added = false;
		public boolean removed = false;

		public void added(ObservableCollection sender, Map.Entry<String, Integer> entry) {
			this.sender = sender;
			this.entry = entry;
			added = true;
			removed = false;
		}

		public void removed(ObservableCollection sender, Map.Entry<String, Integer> entry) {
			this.sender = sender;
			this.entry = entry;
			added = false;
			removed = true;
		}
	}
}