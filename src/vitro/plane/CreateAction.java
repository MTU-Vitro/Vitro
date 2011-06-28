package vitro.plane;

import vitro.*;

public class CreateAction extends PlaneAction {

	public final Position position;
	public final Actor    actor;
	
	public CreateAction(Plane model, Position position, Actor actor) {
		super(model);
		this.position = position;
		this.actor    = actor;
	}
	
	public void apply() {
		if(model.actors.contains(actor)) {
			throw new Error(String.format("Precondition for CreateAction '%s' not satisfied.", this));
		}
		model.positions.put(actor, position);
	}

	public void undo() {
		if(!position.equals(model.positions.get(actor))) {
			throw new Error(String.format("Postcondition for CreateAction '%s' not satisfied.", this));
		}
		model.positions.remove(actor);
	}
	
	@Override
	public int hashCode() {
		return    model.hashCode() ^
		          actor.hashCode() ^
		       position.hashCode();
	}
	
	@Override
	public boolean equals(Object o) {
		if(!(o instanceof CreateAction)) { return false; }
		CreateAction other = (CreateAction)o;
		return (other.model == model)          &&
		       (other.actor.equals(actor))     &&
		       (other.position.equals(position));
	}
	
	@Override
	public String toString() {
		return String.format("Create actor '%s' with frame '%s'.", actor, position);
	}
}
