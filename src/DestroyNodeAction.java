import java.util.*;

public class DestroyNodeAction extends GraphAction {

	protected final Graph.Node node;
	protected final Set<Graph.Edge> edges = new HashSet<Graph.Edge>();

	public DestroyNodeAction(Graph model, Graph.Node node) {
		super(model);
		this.node = node;

		edges.addAll(node.edges);
		for(Graph.Edge e : model.edges) {
			if (e.end == node) { edges.add(e); }
		}
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

}