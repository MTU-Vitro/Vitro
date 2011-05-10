public class DestroyNodeAction extends GraphAction {

	private final Graph.Node n;

	public DestroyNodeAction(Graph model, Graph.Node n) {
		super(model);
		this.n = n;
	}

	public void apply() {
		model.nodes.remove(n);
	}

	public void undo() {
		model.nodes.add(n);
	}

	@Override
	public int hashCode() {
		return model.hashCode() ^
		           n.hashCode();
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof DestroyNodeAction)) { return false; }
		DestroyNodeAction other = (DestroyNodeAction)o;
		return (other.model == this.model) &&
		       (other.n     == this.n    );
	}

}