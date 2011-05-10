public class CreateNodeAction extends GraphAction {

	private final Graph.Node n;

	public CreateNodeAction(Graph model) {
		super(model);
		n = model.createNode();
		model.nodes.remove(n);
	}

	public void apply() {
		model.nodes.add(n);
	}

	public void undo() {
		model.nodes.remove(n);
	}

	@Override
	public int hashCode() {
		return model.hashCode() ^
		           n.hashCode();
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof CreateNodeAction)) { return false; }
		CreateNodeAction other = (CreateNodeAction)o;
		return (other.model == this.model) &&
		       (other.n     == this.n    );
	}

}