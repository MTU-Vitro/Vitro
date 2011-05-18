package vitro.model.graph;

import vitro.util.*;
import vitro.model.*;
import java.util.*;

public class EdgeWrapper extends Edge {

	private final Edge edge;

	public EdgeWrapper(Edge edge) {
		super(new NodeWrapper(edge.start), new NodeWrapper(edge.end));
		this.edge = edge;
	}

	public boolean equals(Object o) {
		return edge.equals(o);
	}

	public int hashCode() {
		return edge.hashCode();
	}
}