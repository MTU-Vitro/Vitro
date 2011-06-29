package vitro.plane;

import vitro.*;

public class MoveAction extends PlaneAction {

	public final Position start;
	public final Position end;
	public final Actor    actor;
	
	public MoveAction(Plane model, Position destination, Actor actor) {
		super(model);
		this.start = model.positions.get(actor);
		this.end   = destination;
		this.actor = actor;
	}
	
	public void apply() {
		if(!start.equals(model.positions.get(actor))) {
			throw new Error(String.format("Precondition for MoveAction '%s' not satisfied.", this));
		}
		model.positions.put(actor, end);
	}
	
	public void undo() {
		if(!end.equals(model.positions.get(actor))) {
			throw new Error(String.format("Postcondition for MoveAction '%s' not satisfied.", this));
		}
		model.positions.put(actor, start);
	}
	
	@Override
	public int hashCode() {
		return model.hashCode() ^
		       start.hashCode() ^
		         end.hashCode();
	}
	
	@Override
	public boolean equals(Object o) {
		if(!(o instanceof MoveAction)) { return false; }
		MoveAction other = (MoveAction)o;
		return (other.model == this.model) &&
		       (other.start.equals(start)) &&
		       (other.actor.equals(actor)) &&
		       (other.actor.equals(end));
	}

	@Override
	public String toString() {
		return String.format("Move actor '%s' from '%s' to '%s'.", actor, start, end);
	}
}
