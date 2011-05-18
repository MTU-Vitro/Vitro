package vitro.model.graph;

import vitro.model.*;
import java.util.*;

public class DestroyNodeAction extends GraphAction {

	public final Node node;
	public final Set<Edge> edges;

	public DestroyNodeAction(Graph model, Node node) {
		super(model);
		this.node = new NodeWrapper(node);

		Set<Edge> edgeSet = new HashSet<Edge>();
		for(Edge e : node.edges) {
			edgeSet.add(new EdgeWrapper(e));
		}
		for(Edge e : model.edges) {
			if (e.end == node) { edgeSet.add(new EdgeWrapper(e)); }
		}
		edges = Collections.unmodifiableSet(edgeSet);
	}

	public void apply() {
		model.nodes.remove(node);
	}

	public void undo() {
		model.nodes.add(node);
		model.edges.addAll(edges);
	}

	@Override
	public int hashCode() {
		return model.hashCode() ^
		        node.hashCode() ^
		       edges.hashCode();
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof DestroyNodeAction)) { return false; }
		DestroyNodeAction other = (DestroyNodeAction)o;
		return (other.model == this.model) &&
		       (other.node  == this.node ) &&
		       (other.edges.equals(this.edges));
	}

	@Override
	public String toString() {
		return String.format("Destroy node '%s'.", node);
	}

}