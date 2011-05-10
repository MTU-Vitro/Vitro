public class DestroyEdgeAction extends GraphAction {

	private final Graph.Edge e;

	public DestroyEdgeAction(Graph model, Graph.Edge e) {
		super(model);
		this.e = e;
	}

	public void apply() {
		model.edges.remove(e);
	}

	public void undo() {
		model.edges.add(e);
	}

	@Override
	public int hashCode() {
		return model.hashCode() ^
		           e.hashCode();
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof DestroyEdgeAction)) { return false; }
		DestroyEdgeAction other = (DestroyEdgeAction)o;
		return (other.model == this.model) &&
		       (other.e     == this.e    );
	}

}