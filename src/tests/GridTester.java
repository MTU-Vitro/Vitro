package tests;

import vitro.*;
import vitro.grid.*;
import java.util.*;

import org.junit.Test;
import static org.junit.Assert.*;

public class GridTester {
	
	@Test
	public void testRawActors() {
		Grid g = new Grid(10, 10);
		Actor a = new Actor();
		g.actors.add(a);
		g.actors.remove(a);
	}

	@Test
	public void testSingleActorAt() {
		Grid g = new Grid(10, 10);
		Actor a = new Actor();
		Location loc = new Location(g, 1, 1);

		assertTrue(g.actorAt(loc) == null);
		g.put(a, 1, 1);
		assertTrue(g.actorAt(loc) == a);
		g.actors.remove(a);
		assertTrue(g.actorAt(loc) == null);
	}

	@Test
	public void testMultipleActorAt() {
		Grid g = new Grid(10, 10);
		Actor a = new Actor();
		Actor b = new Actor();
		Location loc = new Location(g, 1, 1);

		g.put(a, 1, 1);
		g.put(b, 1, 1);
		assertTrue(g.actorAt(loc) == a);
		g.locations.remove(a);
		assertTrue(g.actorAt(loc) == b);
		g.locations.remove(b);
		assertTrue(g.actorAt(loc) == null);
	}
}
