package vitro.graph;

import vitro.*;
import vitro.util.*;
import java.util.*;

public abstract class Node {

	public final Set<Edge>  edges;
	public final Set<Actor> actors;

	protected final Set<Edge>  internalEdges;
	protected final Set<Actor> internalActors;
	protected final Graph model;

	public Node(Set<Edge> edges, Set<Actor> actors, boolean modifiable, Graph model) {
		this.model = model;
		if (modifiable) {
			this.edges  = edges;
			this.actors = actors;
		}
		else {
			this.edges  = Collections.unmodifiableSet(edges);
			this.actors = Collections.unmodifiableSet(actors);
		}
		if (edges instanceof ObservableSet) {
			internalEdges = ((ObservableSet<Edge>)edges).store();
		}
		else {
			internalEdges = edges;
		}
		if (actors instanceof ObservableSet) {
			internalActors = ((ObservableSet<Actor>)actors).store();
		}
		else {
			internalActors = actors;
		}
	}

	public Node(Set<Edge> edges, Set<Actor> actors, Graph model) {
		this(edges, actors, true, model);
	}

	public Graph model() {
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