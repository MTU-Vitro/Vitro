package vitro.model.graph;

import vitro.model.*;

public class CreateAction extends GraphAction {

	public final Node node;
	public final Actor actor;

	public CreateAction(Graph model, Node node, Actor actor) {
		super(model);
		this.node = new NodeWrapper(node);
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

	@Override
	public String toString() {
		return String.format("Create actor '%s' at location '%s'.", actor, node);
	}
}