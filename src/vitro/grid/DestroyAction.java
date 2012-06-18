package vitro.grid;

import vitro.*;
import java.util.*;

/**
* A DestroyAction encapsulates the process of
* removing one or more Actors from a Grid.
*
* @author John Earnest
**/
public class DestroyAction extends GridAction {

	/**
	* A mapping from the destroyed Actors to their original Locations.
	**/
	public final Map<Actor, Location> actors;

	/**
	* Create a new DestroyAction.
	*
	* @param model the Grid from which to remove Actors.
	* @param targets one or more Actors to remove.
	**/
	public DestroyAction(Grid model, Actor... targets) {
		super(model);
		Map<Actor, Location> actorMap = new HashMap<Actor, Location>();
		for(Actor a : targets) {
			actorMap.put(a, model.locations.get(a));
		}
		actors = Collections.unmodifiableMap(actorMap);
	}

	/**
	* Apply this Action.
	* Every target Actor must be at the Location it was in when this Action was created.
	**/
	public void apply() {
		for(Map.Entry<Actor, Location> e : actors.entrySet()) {
			if (!e.getValue().equals(model.locations.get(e.getKey()))) {
				throw new Error(String.format("Precondition for DestroyAction '%s' not satisfied.", this));
			}
			model.actors.remove(e.getKey());
		}
	}

	/**
	* Roll back this Action.
	* None of the actors removed by this Action can exist in the Grid.
	**/
	public void undo() {
		for(Map.Entry<Actor, Location> e : actors.entrySet()) {
			if (model.actors.contains(e.getKey())) {
				throw new Error(String.format("Postcondition for DestroyAction '%s' not satisfied.", this));
			}
			model.locations.put(e.getKey(), e.getValue());
		}
	}

	@Override
	public int hashCode() {
		return actors.hashCode() ^
		        model.hashCode();
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof DestroyAction)) { return false; }
		DestroyAction other = (DestroyAction)o;
		return (other.actors.equals(actors)) &&
		       (other.model == this.model  );
	}

	@Override
	public String toString() {
		return String.format("Destroy actors: %s", actors.keySet());
	}
}
