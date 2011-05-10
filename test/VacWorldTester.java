import java.util.*;
import static junit.framework.Assert.*;

public class VacWorldTester {

	@Test
	private static void testClean() {
		VacWorld w = new VacWorld();
		Graph.Node a = w.createNode();

		a.actors.add(w.createScrubby());

		assertTrue(w.clean());

		a.actors.add(w.createDirt());

		assertFalse(w.clean());
	}

	@Test
	private static void testScrubbyActions() {
		VacWorld w = new VacWorld();
		Graph.Node n1 = w.createNode();
		Graph.Node n2 = w.createNode();
		Graph.Node n3 = w.createNode();
		Graph.Edge e1 = w.createEdge(n1, n2);
		Graph.Edge e2 = w.createEdge(n1, n3);

		VacWorld.Scrubby scrubby = w.createScrubby();
		n1.actors.add(scrubby);

		Set<Action> a = scrubby.actions();
		assertTrue(a.contains(new MoveAction(w, e1, scrubby)));
		assertTrue(a.contains(new MoveAction(w, e2, scrubby)));
		assertTrue(a.size() == 2);

		VacWorld.Dirt dirt = w.createDirt();
		n1.actors.add(dirt);

		Set<Action> b = scrubby.actions();
		assertTrue(b.contains(new MoveAction(w, e1, scrubby)));
		assertTrue(b.contains(new MoveAction(w, e2, scrubby)));
		assertTrue(b.contains(new DestroyAction(w, dirt)));
		assertTrue(b.size() == 3);
	}

	@Test
	public static void testScrubbyMove() {
		VacWorld w = new VacWorld();
		Graph.Node n1 = w.createNode();
		Graph.Node n2 = w.createNode();
		Graph.Edge e1 = w.createEdge(n1, n2);
		
		VacWorld.Scrubby scrubby = w.createScrubby();
		n1.actors.add(scrubby);
		
		assertTrue(n1.actors.contains(scrubby));

		MoveAction move = new MoveAction(w, e1, scrubby);
		
		assertTrue(scrubby.actions().contains(move));

		move.apply();

		assertTrue(w.getLocation(scrubby) == n2);

		move.undo();

		assertTrue(w.getLocation(scrubby) == n1);
	}

	@Test
	public static void testScrubbyClean() {
		VacWorld w = new VacWorld();
		Graph.Node n1 = w.createNode();
		
		VacWorld.Scrubby scrubby = w.createScrubby();
		n1.actors.add(scrubby);

		VacWorld.Dirt dirt = w.createDirt();
		n1.actors.add(dirt);

		DestroyAction clean = new DestroyAction(w, dirt);
		
		assertTrue(scrubby.actions().contains(clean));

		clean.apply();

		assertTrue(w.clean());

		clean.undo();

		assertFalse(w.clean());
	}
}