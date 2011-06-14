package vitro.grid;

import vitro.*;

public class CreateAction extends GridAction {

	public final Location location;
	public final Actor actor;

	public CreateAction(Grid model, Location location, Actor actor) {
		super(model);
		this.location = location;
		this.actor    = actor;
	}

	public void apply() {
		if (model.actors.contains(actor)) {
			throw new Error(String.format("Precondition for CreateAction '%s' not satisfied.", this));
		}
		model.locations.put(actor, location);
	}

	public void undo() {
		if (!location.equals(model.locations.get(actor))) {
			throw new Error(String.format("Postcondition for CreateAction '%s' not satisfied.", this));
		}
		model.actors.remove(actor);
	}

	@Override
	public int hashCode() {
		return model.hashCode() ^
		       location.hashCode() ^
		       actor.hashCode();
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof CreateAction)) { return false; }
		CreateAction other = (CreateAction)o;
		return (other.model == model) &&
		       (other.location.equals(location)) &&
		       (other.actor.equals(actor));
	}

	@Override
	public String toString() {
		return String.format("Create actor '%s' at location '%s'.", actor, location);
	}

}