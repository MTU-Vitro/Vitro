package vitro.grid;

import vitro.*;

/**
* A MoveAction encapsulates the process of
* Moving an Actor from one Location to another.
*
* @author John Earnest
**/
public class MoveAction extends GridAction {

	/**
	* The initial Location of the Actor.
	**/
	public final Location start;
	/**
	* The final Location of the Actor.
	**/
	public final Location end;
	/**
	* The Actor that is moved.
	**/
	public final Actor actor;

	/**
	* Create a new MoveAction.
	*
	* @param model the Grid within which to move an Actor.
	* @param destination the Location to which the Actor will be moved.
	* @param actor the Actor to move.
	**/
	public MoveAction(Grid model, Location destination, Actor actor) {
		super(model);
		this.start = model.locations.get(actor);
		this.end   = destination;
		this.actor = actor;
	}

	/**
	* Apply this Action.
	* The Actor must be at the Location it was in when this Action was created.
	**/
	public void apply() {
		if (!start.equals(model.locations.get(actor))) {
			throw new Error(String.format("Precondition for MoveAction '%s' not satisfied.", this));
		}
		model.locations.put(actor, end);
	}

	/**
	* Roll back this Action.
	* The Actor must be at the Location where this Action left it.
	**/
	public void undo() {
		if (!end.equals(model.locations.get(actor))) {
			throw new Error(String.format("Postcondition for MoveAction '%s' not satisfied.", this));
		}
		model.locations.put(actor, start);
	}

	@Override
	public int hashCode() {
		return model.hashCode() ^
		       start.hashCode() ^
		         end.hashCode();
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof MoveAction)) { return false; }
		MoveAction other = (MoveAction)o;
		return (other.model == this.model) &&
		       (other.start.equals(start)) &&
		       (other.actor.equals(actor)) &&
		       (other.end.equals(end));
	}

	@Override
	public String toString() {
		return String.format("Move actor '%s' from %s to %s.", actor, start, end);
	}
}
