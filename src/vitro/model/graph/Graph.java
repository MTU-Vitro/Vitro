package vitro.model.graph;

import vitro.util.*;
import vitro.model.*;
import java.util.*;

public class Graph extends Model {
	
	public final Set<Edge>   edges = new ObservableSet<Edge>();
	public final List<Node>  nodes = new ObservableList<Node>();

	protected final Graph model;
	
	private final Map<Actor, Node> locations = new HashMap<Actor, Node>();
	private final Map<Integer, Node> lists   = new HashMap<Integer, Node>();
	private final CollectionObserver<Actor> actorObserver = new ActorObserver();
	private final CollectionObserver<Node>  nodeObserver  = new NodeObserver();
	private final CollectionObserver<Edge>  edgeObserver  = new EdgeObserver();
	
	public Graph() {
		super(new ObservableSet<Actor>());
		((ObservableSet<Actor>)actors).addObserver(actorObserver);
		((ObservableList<Node>)nodes).addObserver(nodeObserver);
		((ObservableSet<Edge>)edges).addObserver(edgeObserver);
		model = this;
	}
	
	public Node createNode() {
		Node ret = new Node();
		nodes.add(ret);
		lists.put(System.identityHashCode(ret.actors), ret);
		return ret;
	}
	
	public Edge createEdge(Node a, Node b) {
		if (!nodes.contains(a) || !nodes.contains(b)) {
			throw new IllegalArgumentException("Edge does not connect two valid Nodes in this Graph.");
		}
		Edge ret = new Edge(a, b);
		edges.add(ret);
		a.edges.add(ret);
		return ret;
	}
	
	public Node getLocation(Actor a) {
		return locations.get(a);
	}
	
	protected List<Edge> path(Node start, Node destination) {
		List<Edge> ret = new ArrayList<Edge>();
		return path(
			ret,
			new HashSet<Node>(),
			start,
			destination
		) ? ret : null;
	}
	
	// this implementation is recursive for simplicity.
	// in the future, this should probably be rewritten
	// iteratively to avoid stack overflows on large graphs.
	// also this greedily returns any path, not the best path. Do better.
	private boolean path(List<Edge> path, Set<Node> visited, Node a, Node b) {
		if (a == b)              { return true; }
		if (visited.contains(a)) { return false; }
		visited.add(a);
		for(Edge e : a.edges) {
			if (path(path, visited, e.end, b)) {
				path.add(0,e);
				return true;
			}
		}
		return false;
	}
	
	protected Set<Node> reachable(Node start) {
		List<Node> frontier = new ArrayList<Node>();
		 Set<Node> visited  = new   HashSet<Node>();
		frontier.add(start);
		visited.add(start);
		while(frontier.size() > 0) {
			Node v = frontier.remove(frontier.size()-1);
			for(Edge e : v.edges) {
				if (!visited.contains(e.end)) {
					frontier.add(e.end);
				}
				visited.add(e.end);
			}
		}
		return visited;
	}

	protected Set<Node> reachable(Node root, int depth) {
		Set<Node> ret = new HashSet<Node>();
		reachable(ret, root, depth);
		return ret;
	}

	private void reachable(Set<Node> visited, Node root, int depth) {
		if (visited.contains(root)) { return; }
		visited.add(root);
		if (depth == 0) { return; }
		for(Edge e : root.edges) {
			reachable(visited, e.end, depth-1);
		}
	}
	
	public class Edge {
		public final Node start;
		public final Node end;
		
		private Edge(Node a, Node b) {
			start = a;
			end   = b;
		}

		public String toString() {
			return String.format("('%s'-->'%s')", start, end);
		}
	}
	
	public class Node {
		public final Set<Edge>   edges;
		public final Set<Actor> actors;

		protected final Set<Edge> internalEdges;
		protected final Set<Actor> internalActors;

		private Node() {
			edges = new ObservableSet<Edge>();
			actors = new ObservableSet<Actor>();
			internalEdges  = ((ObservableSet<Edge>)edges).store();
			internalActors = ((ObservableSet<Actor>)actors).store();
			((ObservableSet<Actor>)actors).addObserver(actorObserver);
			((ObservableSet<Edge>)edges).addObserver(edgeObserver);
			lists.put(System.identityHashCode(actors), this);
		}

		private Node(Set<Edge> edges, Set<Actor> actors) {
			this.edges  = Collections.unmodifiableSet(edges);
			this.actors = Collections.unmodifiableSet(actors);
			this.internalEdges  = edges;
			this.internalActors = actors;
		}

		private Graph model() {
			return model;
		}
		
		public List<Edge> path(Node destination) {
			return model.path(this, destination);
		}

		public List<Edge> path(Actor goal) {
			return path(model.getLocation(goal));
		}
		
		public List<Node> pathNodes(Node destination) {
			List<Edge> path = path(destination);
			if (path == null) { return null; }
			List<Node> ret = new ArrayList<Node>();
			for(Edge e : path) {
				ret.add(e.start);
			}
			ret.add(destination);
			return ret;
		}
		
		public Set<Node> reachable() {
			return model.reachable(this);
		}

		public Set<Node> reachable(int depth) {
			return model.reachable(this, depth);
		}
				
		public Set<Actor> reachableActors() {
			Set<Actor> ret = new HashSet<Actor>();
			for(Node v : reachable()) {
				ret.addAll(v.actors);
			}
			return ret;
		}

		public Set<Actor> reachableActors(int depth) {
			Set<Actor> ret = new HashSet<Actor>();
			for(Node v : reachable(depth)) {
				ret.addAll(v.actors);
			}
			return ret;
		}
	}

	public class VisibleNode extends Node {
		protected final Node node;
		protected final int depth;

		public VisibleNode(Node node, int depth) {
			super(
				new HashSet<Edge>(),
				new HashSet<Actor>()
			);
			this.node  = node;
			this.depth = depth;

			if (depth > 0) {
				for(Edge e : node.edges) {
					internalEdges.add(new VisibleEdge(this, e.end, depth));
				}
				internalActors.addAll(node.actors);
			}
		}
	}

	public class VisibleEdge extends Edge {
		protected final int depth;

		private VisibleEdge(VisibleNode a, Node b, int depth) {
			super(a, new VisibleNode(b, depth - 1));
			this.depth = depth;
		}
	}

	private class NodeObserver implements CollectionObserver<Node> {
		// there is only one collection of nodes to monitor,
		// so we don't need to disambiguate between senders.

		public void added(ObservableCollection sender, Node n) {
			// Since Nodes are intimately connected to a Graph,
			// we can't just transplant a node from elsewheresville.
			if (n.model() != model) {
				throw new IllegalArgumentException("Node belongs to a different Graph.");
			}
		}

		public void removed(ObservableCollection sender, Node n) {
			// remove out edges:
			n.edges.clear();
			// remove in edges:
			Set<Edge> incident = new HashSet<Edge>();
			for(Edge e : edges) {
				if (e.end == n) { incident.add(e); }
			}
			edges.removeAll(incident);
		}
	}

	private class EdgeObserver implements CollectionObserver<Edge> {
		public void added(ObservableCollection sender, Edge e) {
			// edges cannot be added 'raw' if they do not
			// connect to valid nodes in this graph on both sides.
			if (!nodes.contains(e.start) || !nodes.contains(e.end)) {
				throw new IllegalArgumentException("Edge does not connect two valid Nodes in this Graph.");
			}
			else if (sender == edges) {
				((ObservableSet<Edge>)e.start.edges).store().add(e);
			}
			else {
				((ObservableSet<Edge>)edges).store().add(e);
			}
		}

		public void removed(ObservableCollection sender, Edge e) {
			if (sender == edges) {
				e.start.edges.remove(e);
			}
			else {
				edges.remove(e);
			}
		}
	}
	
	private class ActorObserver implements CollectionObserver<Actor> {		
		public void added(ObservableCollection sender, Actor e) {
			if (sender == actors) {
				// actors added 'raw' do not have a location.
				// if we wanted a default, we'd do it here:
				// locations.put(e, vertices.get(0));
			}
			else {
				// actors should only exist in a single
				// location at a given time:
				Node currentLocation = getLocation(e);
				if (currentLocation != null) {
					currentLocation.actors.remove(e);
				}
				locations.put(e, lists.get(System.identityHashCode(sender)));
				actors.add(e);
			}
		}
		
		public void removed(ObservableCollection sender, Actor e) {
			if (sender == actors) {
				locations.get(e).actors.remove(e);
				locations.remove(e);
			}
			else {
				actors.remove(e);
			}
		}
	}
}