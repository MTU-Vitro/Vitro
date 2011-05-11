public class CreateNodeAction extends GraphAction {

	protected final Graph.Node node;

	public CreateNodeAction(Graph model) {
		super(model);
		node = model.createNode();
		model.nodes.remove(node);
	}

	public void apply() {
		model.nodes.add(node);
	}

	public void undo() {
		model.nodes.remove(node);
	}

	@Override
	public int hashCode() {
		return model.hashCode() ^
		        node.hashCode();
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof CreateNodeAction)) { return false; }
		CreateNodeAction other = (CreateNodeAction)o;
		return (other.model == this.model) &&
		       (other.node  == this.node );
	}

	@Override
	public String toString() {
		return String.format("Create node '%s'.", node);
	}

}