package vitro.plane;

import vitro.*;

public class CreateAction extends PlaneAction {

	public final Frame frame;
	public final Actor actor;
	
	public CreateAction(Plane model, Frame frame, Actor actor) {
		super(model);
		this.frame = frame;
		this.actor = actor;
	}
	
	public void apply() {
		if(model.actors.contains(actor)) {
			throw new Error(String.format("Precondition for CreateAction '%s' not satisfied.", this));
		}
		model.frames.put(actor, frame);
	}

	public void undo() {
		if(!frame.equals(model.frames.get(actor))) {
			throw new Error(String.format("Postcondition for CreateAction '%s' not satisfied.", this));
		}
		model.frames.remove(actor);
	}
	
	@Override
	public int hashCode() {
		return model.hashCode() ^
		       actor.hashCode() ^
		       frame.hashCode();
	}
	
	@Override
	public boolean equals(Object o) {
		if(!(o instanceof CreateAction)) { return false; }
		CreateAction other = (CreateAction)o;
		return (other.model == model) &&
		       (other.actor.equals(actor)) &&
		       (other.frame.equals(frame));
	}
	
	@Override
	public String toString() {
		return String.format("Create actor '%s' with frame '%s'.", actor, frame);
	}
}
