package vitro.model.graph;

import vitro.model.*;
import java.util.*;

public class VisibleNode extends Node {
	protected final Node node;
	protected final int depth;

	public VisibleNode(Graph model, Node node, int depth) {
		super(
			new HashSet<Edge>(),
			new HashSet<Actor>(),
			false,
			model
		);
		this.node  = node;
		this.depth = depth;

		if (depth > 0) {
			for(Edge e : node.edges) {
				internalEdges.add(new VisibleEdge(model, e, this, depth));
			}
			internalActors.addAll(node.actors);
		}
	}

	public List<Edge> path(Node destination) {
		if (!reachable(depth).contains(destination)) { return null; }
		return super.path(destination);
	}

	public List<Edge> path(Actor goal) {
		if (!reachableActors(depth).contains(goal)) { return null; }
		return super.path(goal);
	}

	public Set<Node> reachable()          { return reachable(depth); }
	public Set<Node> reachable(int depth) { return reachable(Math.min(depth, this.depth)); }

	public boolean equals(Object o) {
		return node.equals(o);
	}

	public int hashCode() {
		return node.hashCode(o);
	}
}