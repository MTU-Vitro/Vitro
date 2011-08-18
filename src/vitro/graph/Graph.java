package vitro.graph;

import vitro.*;
import vitro.util.*;
import java.util.*;

public class Graph extends Model {
	
	public final Set<Edge>   edges = new ObservableSet<Edge>();
	public final List<Node>  nodes = new ObservableList<Node>();

	protected final Graph model;
	
	private final Map<Position, Node> positions = new HashMap<Position, Node>();
	private final Map<   Actor, Node> locations = new HashMap<   Actor, Node>();
	private final Map< Integer, Node> lists     = new HashMap< Integer, Node>();
	private final CollectionObserver<Actor> actorObserver = new ActorObserver();
	private final CollectionObserver<Node>   nodeObserver = new NodeObserver();
	private final CollectionObserver<Edge>   edgeObserver = new EdgeObserver();
	
	public Graph() {
		super(new ObservableSet<Actor>());
		((ObservableSet<Actor>)actors).addObserver(actorObserver);
		((ObservableList<Node>)nodes).addObserver(nodeObserver);
		((ObservableSet<Edge>)edges).addObserver(edgeObserver);
		model = this;
	}
	
	public Node createNode() {
		Node ret = new GraphNode(this);
		nodes.add(ret);
		return ret;
	}
	
	public Edge createEdge(Node a, Node b) {
		if (!nodes.contains(a) || !nodes.contains(b)) {
			throw new IllegalArgumentException("Edge does not connect two valid Nodes in this Graph.");
		}
		Edge ret = new GraphEdge(a, b);
		edges.add(ret);
		return ret;
	}
	
	public Node getLocation(Actor a) {
		return locations.get(a);
	}

	public Node getNode(Position position) {
		return positions.get(position);
	}
	
	protected List<Edge> path(Node start, Node destination) {
//		List<Edge> ret = new ArrayList<Edge>();
//		return path(
//			ret,
//			new HashSet<Node>(),
//			start,
//			destination
//		) ? ret : null;

		return pathBFS(start, destination);
	}
	
	private List<Edge> pathBFS(Node start, Node destination) {
		Queue<Node> frontier = new LinkedList<Node>();
		frontier.add(start);

		Set<Node> visited = new HashSet<Node>();
		visited.add(start);

		Map<Node, Edge> tracks = new HashMap<Node, Edge>();
		tracks.put(start, null);

		while(!frontier.isEmpty()) {
			Node node = frontier.poll();

			if(node == destination) {
				List<Edge> path = new ArrayList<Edge>();
				
				Edge edge = tracks.get(node);
				while(edge != null) {
					path.add(0, edge);
					edge = tracks.get(edge.start);
				}

				return path;
			}

			for(Edge edge : node.edges) {
				if(!visited.contains(edge.end)) {
					frontier.add(edge.end);
					visited.add(edge.end);
					tracks.put(edge.end, edge);
				}
			}
		}

		return null;
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
	
	private static class GraphEdge extends Edge {
		private GraphEdge(Node a, Node b) {
			super(a, b);
		}
	}

	private class GraphNode extends Node {
		private GraphNode(Graph model) {
			super(new ObservableSet<Edge>(), new ObservableSet<Actor>(), model);
			((ObservableSet<Actor>)actors).addObserver(actorObserver);
			((ObservableSet<Edge>) edges ).addObserver(edgeObserver);
			lists.put(System.identityHashCode(actors), this);
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
			lists.put(System.identityHashCode(n.actors), n);
			positions.put(new Position(n), n);
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
			positions.remove(new Position(n));
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
				// if we wanted a default, we'd do it here.
			}
			else {
				// actors should only exist in a single
				// location at a given time:
				Node currentLocation = getLocation(e);
				if (currentLocation != null && currentLocation.actors != sender) {
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
