import java.util.*;

public class VacWorldTester {

	private static void testClean() {
		VacWorld w = new VacWorld();
		Graph.Node a = w.createNode();

		a.actors.add(w.createScrubby());

		assert w.clean();

		a.actors.add(w.createDirt());

		assert !(w.clean());
	}

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
		assert a.contains(new MoveAction(w, e1, scrubby)) : a;
		assert a.contains(new MoveAction(w, e2, scrubby)) : a;
		assert a.size() == 2;

		VacWorld.Dirt dirt = w.createDirt();
		n1.actors.add(dirt);

		Set<Action> b = scrubby.actions();
		assert b.contains(new MoveAction(w, e1, scrubby)) : b;
		assert b.contains(new MoveAction(w, e2, scrubby)) : b;
		assert b.contains(new DestroyAction(w, dirt)) : b;
		assert b.size() == 3;
	}

	public static void testScrubbyMove() {
		VacWorld w = new VacWorld();
		Graph.Node n1 = w.createNode();
		Graph.Node n2 = w.createNode();
		Graph.Edge e1 = w.createEdge(n1, n2);
		
		VacWorld.Scrubby scrubby = w.createScrubby();
		n1.actors.add(scrubby);
		
		assert n1.actors.contains(scrubby);

		MoveAction move = new MoveAction(w, e1, scrubby);
		
		assert scrubby.actions().contains(move);

		move.apply();

		assert w.getLocation(scrubby) == n2;

		move.undo();

		assert w.getLocation(scrubby) == n1;
	}

	public static void testScrubbyClean() {
		VacWorld w = new VacWorld();
		Graph.Node n1 = w.createNode();
		
		VacWorld.Scrubby scrubby = w.createScrubby();
		n1.actors.add(scrubby);

		VacWorld.Dirt dirt = w.createDirt();
		n1.actors.add(dirt);

		DestroyAction clean = new DestroyAction(w, dirt);
		
		assert scrubby.actions().contains(clean);

		clean.apply();

		assert w.clean();

		clean.undo();

		assert !(w.clean());
	}

	public static void main(String[] args) {
		testClean();
		testScrubbyActions();
		testScrubbyMove();
		testScrubbyClean();

		System.out.println("\nAll tests successful!\n");
	}
}