package vitro.model.graph;

import vitro.model.*;
import java.util.*;

public class DestroyAction extends GraphAction {

	public final Map<Actor, Node> actors;

	public DestroyAction(Graph model, Actor... targets) {
		super(model);
		Map<Actor, Node> actorMap = new HashMap<Actor, Node>();
		for(Actor a : targets) {
			actorMap.put(a, model.getLocation(a));
		}
		actors = Collections.unmodifiableMap(actorMap);
	}

	public void apply() {
		for(Map.Entry<Actor, Node> e : actors.entrySet()) {
			e.getValue().actors.remove(e.getKey());
		}
	}

	public void undo() {
		for(Map.Entry<Actor, Node> e : actors.entrySet()) {
			e.getValue().actors.add(e.getKey());
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