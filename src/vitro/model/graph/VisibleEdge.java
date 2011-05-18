package vitro.model.graph;

import vitro.model.*;
import java.util.*;


public class VisibleEdge extends Edge {
	protected final int depth;

	public VisibleEdge(Graph model, VisibleNode a, Node b, int depth) {
		super(a, new VisibleNode(model, b, depth - 1));
		this.depth = depth;
	}
}