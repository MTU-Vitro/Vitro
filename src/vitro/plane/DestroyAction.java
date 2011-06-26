package vitro.plane;

import vitro.*;
import java.util.*;

public class DestroyAction extends PlaneAction {

	public final Map<Actor, Frame> actors;

	public DestroyAction(Plane model, Actor... targets) {
		super(model);
		Map<Actor, Frame> actorMap = new HashMap<Actor, Frame>();
		for(Actor a : targets) {
			actorMap.put(a, model.frames.get(a));
		}
		actors = Collections.unmodifiableMap(actorMap);
	}

	public void apply() {
		for(Map.Entry<Actor, Frame> e : actors.entrySet()) {
			if (!e.getValue().equals(model.frames.get(e.getKey()))) {
				throw new Error(String.format("Precondition for DestroyAction '%s' not satisfied.", this));
			}
			model.actors.remove(e.getKey());
		}
	}

	public void undo() {
		for(Map.Entry<Actor, Frame> e : actors.entrySet()) {
			if (model.actors.contains(e.getKey())) {
				throw new Error(String.format("Postcondition for DestroyAction '%s' not satisfied.", this));
			}
			model.frames.put(e.getKey(), e.getValue());
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
