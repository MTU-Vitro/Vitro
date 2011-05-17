package vitro.model.graph;

import vitro.model.*;

public class MoveAction extends GraphAction {

	public final Graph.Edge edge;
	public final Actor actor;

	public MoveAction(Graph model, Graph.Edge edge, Actor actor) {
		super(model);
		this.edge = edge;
		this.actor = actor;
	}

	public void apply() {
		edge.end.actors.add(actor);
	}

	public void undo() {
		edge.start.actors.add(actor);
	}

	@Override
	public int hashCode() {
		return model.hashCode() ^
		        edge.hashCode() ^
		       actor.hashCode();
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof MoveAction)) { return false; }
		MoveAction other = (MoveAction)o;
		return (other.model == this.model) &&
		       (other.edge  == this.edge ) &&
		       (other.actor == this.actor);
	}

	@Override
	public String toString() {
		return String.format("Move actor '%s' from '%s' to '%s'.", actor, edge.start, edge.end);
	}
}