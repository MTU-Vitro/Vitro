package tests;

import vitro.util.*;
import java.util.*;

import org.junit.Test;
import static org.junit.Assert.*;

public class GroupsTester {
	
	private class A           {}
	private class B extends A {}
	private class C extends A {}
	
	@Test
	public void testFirstOfType() {
		Collection<A> collection = new HashSet<A>();
		collection.add(new B());
		collection.add(new B());
		
		assertTrue(Groups.firstOfType(C.class, collection) == null);
		assertTrue(collection.contains(Groups.firstOfType(A.class, collection)));
		assertTrue(collection.contains(Groups.firstOfType(B.class, collection)));
	}
	
	@Test
	public void testOfType() {
		Collection<A> collection = new HashSet<A>();
		collection.add(new A());
		collection.add(new B());
		collection.add(new B());
		collection.add(new A());
		collection.add(new B());
		collection.add(new A());
		collection.add(new A());
		collection.add(new C());
		
		assertTrue(Groups.ofType(A.class, collection).size() == 8);
		assertTrue(Groups.ofType(B.class, collection).size() == 3);
		assertTrue(Groups.ofType(C.class, collection).size() == 1);
	}
	
	@Test
	public void testContainsType() {
		Collection<A> collection = new HashSet<A>();
		collection.add(new B());
		collection.add(new B());

		assertTrue(Groups.containsType(A.class, collection));
		assertTrue(Groups.containsType(B.class, collection));
		assertFalse(Groups.containsType(C.class, collection));
	}
	
	@Test
	public void testAny() {
		Collection<String> collection = new HashSet<String>();
		
		assertTrue(Groups.any(collection) == null);
		
		collection.add("Hello World");
		assertTrue(Groups.any(collection).equals("Hello World"));
		
		collection.add("Goodbye World");
		assertTrue(collection.contains(Groups.any(collection)));
	}
	
	@Test
	public void testFirstEmpty() {
		Collection<String> collection = new HashSet<String>();
		
		assertTrue(Groups.first(collection) == null);
		
		collection.add("Hello World");
		assertTrue(Groups.first(collection).equals("Hello World"));
		
		collection.add("Goodbye World");
		assertTrue(collection.contains(Groups.first(collection)));
	}
}
