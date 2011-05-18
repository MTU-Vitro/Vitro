package vitro.model.graph;

import vitro.model.*;
import java.util.*;


public class VisibleEdge extends Edge {
	private Edge original;
	protected final int depth;

	public VisibleEdge(Graph model, Edge original, VisibleNode a, int depth) {
		super(a, new VisibleNode(model, original.end, depth - 1));
		this.original = original;
		this.depth = depth;
	}

	public boolean equals(Object o) {
		return original.equals(o);
	}

	public int hashCode() {
		return original.hashCode();
	}
}