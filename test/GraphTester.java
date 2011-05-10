import java.util.*;

public class GraphTester {
	
	private static boolean testBuild() {
		Graph g = new Graph();
		Graph.Node v1 = g.createNode();
		Graph.Node v2 = g.createNode();
		Graph.Node v3 = g.createNode();
		
		Graph.Edge e1 = g.createEdge(v1,v2);
		Graph.Edge e2 = g.createEdge(v2,v3);
		
		assert g.nodes.contains(v1);
		assert g.nodes.contains(v2);
		assert g.nodes.contains(v3);
		assert g.nodes.size() == 3;
		
		assert g.edges.contains(e1);
		assert g.edges.contains(e2);
		assert g.edges.size() == 2;
		
		assert v1.edges.contains(e1);
		assert v1.edges.size() == 1;
		assert v2.edges.contains(e2);
		assert v2.edges.size() == 1;

		boolean check1 = false;
		try { g.createEdge(v1, null); }
		catch(IllegalArgumentException e) { check1 = true; }
		assert check1;

		boolean check2 = false;
		Graph.Node other = new Graph().createNode();
		try { g.createEdge(v1, other); }
		catch(IllegalArgumentException e) { check2 = true; }
		assert check2;

		return true;
	}
	
	private static boolean testAddActor() {
		Graph g = new Graph();
		Graph.Node v1 = g.createNode();
		Graph.Node v2 = g.createNode();
		Graph.Node v3 = g.createNode();
		
		Actor a = new Actor();
		Actor b = new Actor();
		
		assert !(g.actors.contains(a));
		g.actors.add(a);
		assert g.actors.contains(a);		
		assert g.getLocation(a) == null;
		
		assert !(g.actors.contains(b));
		assert !(v2.actors.contains(b));
		v2.actors.add(b);
		assert g.actors.contains(b);
		assert v2.actors.contains(b);
		assert g.getLocation(b) == v2;
		
		return true;
	}
	
	private static boolean testRemoveActor() {
		Graph g = new Graph();
		Graph.Node v1 = g.createNode();
		Graph.Node v2 = g.createNode();
		Graph.Node v3 = g.createNode();
		
		Actor a = new Actor();
		
		v2.actors.add(a);
		assert g.actors.contains(a);
		assert v2.actors.contains(a);
		
		v2.actors.remove(a);
		assert !(g.actors.contains(a));
		assert !(v2.actors.contains(a));
		
		return true;
	}
	
	private static boolean testMoveActor() {
		Graph g = new Graph();
		Graph.Node v1 = g.createNode();
		Graph.Node v2 = g.createNode();
		Graph.Node v3 = g.createNode();
		
		Actor a = new Actor();
		
		v2.actors.add(a);
		assert g.actors.contains(a);
		assert v2.actors.contains(a);
		
		v1.actors.add(a);
		assert g.actors.contains(a);
		assert v1.actors.contains(a);
		assert !(v2.actors.contains(a));

		g.actors.add(a);
		assert g.actors.size() == 1;
		
		return true;
	}
	
	public static boolean testReachable() {
		Graph g = new Graph();
		Graph.Node v1 = g.createNode();
		Graph.Node v2 = g.createNode();
		Graph.Node v3 = g.createNode();
		Graph.Node v4 = g.createNode();
		Graph.Node v5 = g.createNode();
		Graph.Node v6 = g.createNode();
		Graph.Node v7 = g.createNode();
		
		g.createEdge(v2, v3);
		g.createEdge(v3, v2);
		
		g.createEdge(v4, v5);
		g.createEdge(v5, v6);
		g.createEdge(v6, v7);
		
		// isolated vertex
		Set<Graph.Node> a = v1.reachable();
		assert a.equals(new HashSet<Graph.Node>(Arrays.asList(v1))) : a;
		
		// cyclic vertices
		Set<Graph.Node> b = v2.reachable();
		assert b.equals(new HashSet<Graph.Node>(Arrays.asList(v2,v3))) : b;
		
		// chained vertices
		Set<Graph.Node> c = v4.reachable();
		assert c.equals(new HashSet<Graph.Node>(Arrays.asList(v4,v5,v6,v7))) : c;
		
		return true;
	}
	
	public static boolean testReachableActors() {
		Graph g = new Graph();
		Graph.Node v1 = g.createNode();
		Graph.Node v2 = g.createNode();
		Graph.Node v3 = g.createNode();
		Graph.Node v4 = g.createNode();
		
		g.createEdge(v2, v3);
		g.createEdge(v3, v4);
		
		Actor a1 = new Actor();
		Actor a2 = new Actor();
		
		v1.actors.add(a1);
		v4.actors.add(a2);
		
		Set<Actor> a = v1.reachableActors();
		assert a.size() == 1;
		assert a.contains(a1);
		
		Set<Actor> b = v2.reachableActors();
		assert b.size() == 1;
		assert b.contains(a2);
		
		return true;
	}
	
	public static boolean testPath() {
		Graph g = new Graph();
		Graph.Node v1 = g.createNode();
		Graph.Node v2 = g.createNode();
		Graph.Node v3 = g.createNode();
		Graph.Node v4 = g.createNode();
		Graph.Node v5 = g.createNode();
		
		Graph.Edge e1 = g.createEdge(v1, v2);
		Graph.Edge e2 = g.createEdge(v1, v3);
		Graph.Edge e3 = g.createEdge(v3, v4);
		Graph.Edge e4 = g.createEdge(v3, v2);
		Graph.Edge e5 = g.createEdge(v4, v5);
		
		List<Graph.Edge> a = v1.path(v5);
		assert a.equals(Arrays.asList(e2, e3, e5)) : a;
		
		List<Graph.Edge> b = v1.path(v1);
		assert b.equals(new ArrayList<Graph.Edge>()) : b;
		
		List<Graph.Node> c = v1.pathNodes(v1);
		assert c.equals(Arrays.asList(v1)) : c;
		
		List<Graph.Edge> d = v5.path(v1);
		assert d == null : d;
				
		List<Graph.Node> e = v1.pathNodes(v5);
		assert e.equals(Arrays.asList(v1, v3, v4, v5)) : e;
		
		return true;
	}

	public static boolean testRemoveNodes() {
		Graph g = new Graph();
		Graph.Node v1 = g.createNode();
		Graph.Node v2 = g.createNode();
		Graph.Node v3 = g.createNode();
		
		Graph.Edge e1 = g.createEdge(v1, v2);
		Graph.Edge e2 = g.createEdge(v2, v3);
		Graph.Edge e3 = g.createEdge(v3, v1);

		assert g.edges.size() == 3;

		g.nodes.remove(v1);

		assert g.edges.contains(e2);
		assert g.edges.size() == 1;

		return true;
	}
	
	public static boolean testRemoveEdges() {
		Graph g = new Graph();
		Graph.Node v1 = g.createNode();
		Graph.Node v2 = g.createNode();
		Graph.Node v3 = g.createNode();
		
		Graph.Edge e1 = g.createEdge(v1, v2);
		Graph.Edge e2 = g.createEdge(v2, v3);
		Graph.Edge e3 = g.createEdge(v3, v1);

		assert g.edges.size() == 3;

		v2.edges.remove(e2);

		assert g.edges.contains(e1);
		assert g.edges.contains(e3);
		assert g.edges.size() == 2;

		g.edges.remove(e1);
		assert v1.edges.size() == 0 : v1.edges;

		return true;
	}

	public static boolean testAddEdges() {
		Graph g = new Graph();
		Graph.Node v1 = g.createNode();
		Graph.Node v2 = g.createNode();
		Graph.Edge e1 = g.createEdge(v1, v2);

		g.edges.remove(e1);

		assert g.edges.size() == 0;

		g.edges.add(e1);

		assert v1.edges.contains(e1);
		assert v1.edges.size() == 1;

		g.nodes.remove(v2);

		assert g.edges.size() == 0;

		// we can't add an edge that
		// does not point to valid nodes:
		boolean except = false;
		try { g.edges.add(e1); }
		catch(IllegalArgumentException e) { except = true; }
		assert except;

		return true;
	}

	public static boolean testAddNodes() {
		Graph g1 = new Graph();
		Graph.Node v1 = g1.createNode();
		Graph.Node v2 = g1.createNode();
		
		g1.nodes.remove(v1);
		assert g1.nodes.size() == 1;

		g1.nodes.add(v1);
		assert g1.nodes.size() == 2;
		g1.createEdge(v1, v2);

		Graph g2 = new Graph();
		Graph.Node alien = g2.createNode();
		boolean check1 = false;
		try { g1.nodes.add(alien); }
		catch(IllegalArgumentException e) { check1 = true; }
		assert check1;

		return true;
	}

	public static void main(String[] args) {
	
		assert testBuild();
		assert testAddActor();
		assert testRemoveActor();
		assert testMoveActor();
	
		assert testReachable();
		assert testReachableActors();
		assert testPath();

		assert testRemoveNodes();
		assert testRemoveEdges();
		assert testAddEdges();
		assert testAddNodes();
	
		System.out.println("\nAll tests passed!\n");
	}

}