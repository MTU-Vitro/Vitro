package vitro.grid;

import vitro.*;
import java.util.*;

public class DestroyAction extends GridAction {

	public final Map<Actor, Location> actors;

	public DestroyAction(Grid model, Actor... targets) {
		super(model);
		Map<Actor, Location> actorMap = new HashMap<Actor, Location>();
		for(Actor a : targets) {
			actorMap.put(a, model.locations.get(a));
		}
		actors = Collections.unmodifiableMap(actorMap);
	}

	public void apply() {
		for(Map.Entry<Actor, Location> e : actors.entrySet()) {
			if (!e.getValue().equals(model.locations.get(e.getKey()))) {
				throw new Error(String.format("Precondition for DestroyAction '%s' not satisfied.", this));
			}
			model.actors.remove(e.getKey());
		}
	}

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