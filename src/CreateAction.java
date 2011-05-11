public class CreateAction extends GraphAction {

	protected final Graph.Node node;
	protected final Actor actor;

	public CreateAction(Graph model, Graph.Node node, Actor actor) {
		super(model);
		this.node = node;
		this.actor = actor;
	}

	public void apply() {
		node.actors.add(actor);
	}

	public void undo() {
		node.actors.remove(actor);
	}

	@Override
	public int hashCode() {
		return model.hashCode() ^
		        node.hashCode() ^
		       actor.hashCode();
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof CreateAction)) { return false; }
		CreateAction other = (CreateAction)o;
		return (other.model == this.model) &&
		       (other.node  == this.node ) &&
		       (other.actor == this.actor);
	}
}