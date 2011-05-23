package vitro.model.graph;

import vitro.model.*;

public class DestroyEdgeAction extends GraphAction {

	public final Edge edge;

	public DestroyEdgeAction(Graph model, Edge edge) {
		super(model);
		this.edge = edge;
	}

	public void apply() {
		model.edges.remove(edge);
	}

	public void undo() {
		model.edges.add(edge);
	}

	@Override
	public int hashCode() {
		return model.hashCode() ^
		        edge.hashCode();
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof DestroyEdgeAction)) { return false; }
		DestroyEdgeAction other = (DestroyEdgeAction)o;
		return (other.model == this.model) &&
		       (other.edge  == this.edge );
	}

	@Override
	public String toString() {
		return String.format("Destroy edge from '%s' to '%s'.", edge.start, edge.end);
	}

}