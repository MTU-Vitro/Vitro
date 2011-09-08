package vitro.grid;
import vitro.*;

/**
* A CreateAction encapsulates the process of
* spawning a new Actor at a specified location.
*
* Note that if two CreateActions are created with references
* to the same "spawned" Actor and then both applied,
* goofy things can happen.
*
* @author John Earnest
**/
public class CreateAction extends GridAction {

	/**
	* The Location at which to spawn an Actor.
	**/
	public final Location location;

	/**
	* The Actor that will be spawned.
	**/
	public final Actor actor;

	/**
	* Create a new CreateAction.
	*
	* @param model the Grid in which to spawn an Actor.
	* @param location the location at which to spawn the Actor.
	* @param actor the Actor to spawn.
	**/
	public CreateAction(Grid model, Location location, Actor actor) {
		super(model);
		this.location = location;
		this.actor    = actor;
	}

	/**
	* Apply this Action.
	* The Actor cannot already exist in the Grid.
	**/
	public void apply() {
		if (model.actors.contains(actor)) {
			throw new Error(String.format("Precondition for CreateAction '%s' not satisfied.", this));
		}
		model.locations.put(actor, location);
	}

	/**
	* Roll back this Action.
	* The Actor must already exist in the Grid.
	**/
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