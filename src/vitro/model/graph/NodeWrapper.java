package vitro.model.graph;

import vitro.util.*;
import vitro.model.*;
import java.util.*;

public class NodeWrapper extends Node {

	private final Node node;

	public NodeWrapper(Node node) {
		super(new HashSet<Edge>(), new HashSet<Actor>(), false, node.model);
		this.node = node;
	}

	public Graph model() {
		throw new UnsupportedOperationException();
	}
	public List<Edge> path(Node destination) {
		throw new UnsupportedOperationException();
	}
	public List<Edge> path(Actor goal) {
		throw new UnsupportedOperationException();
	}
	public List<Node> pathNodes(Node destination) {
		throw new UnsupportedOperationException();
	}
	public Set<Node> reachable() {
		throw new UnsupportedOperationException();
	}
	public Set<Node> reachable(int depth) {
		throw new UnsupportedOperationException();
	}
	public Set<Actor> reachableActors() {
		throw new UnsupportedOperationException();
	}
	public Set<Actor> reachableActors(int depth) {
		throw new UnsupportedOperationException();
	}

	public boolean equals(Object o) {
		return node.equals(o);
	}

	public int hashCode() {
		return node.hashCode();
	}
}