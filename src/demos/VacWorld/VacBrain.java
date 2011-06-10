package demos;

import vitro.util.*;
import vitro.model.*;
import vitro.model.graph.*;
import vitro.controller.*;
import java.util.*;
import static vitro.util.Groups.*;

public class VacBrain implements Agent<VacWorld.Scrubby>, Annotated {

	private Actor goalDirt = null;
	private Set<Edge> visited = new HashSet<Edge>();

	public Action choose(VacWorld.Scrubby actor, Set<Action> options) {

		// if there's stuff to clean, clean it!
		DestroyAction clean = actor.destroy(VacWorld.Dirt.class, options);
		if (clean != null) { return clean; }

		// otherwise, find stuff to clean!
		goalDirt = firstOfType(VacWorld.Dirt.class, actor.location().reachableActors());
		MoveAction move = actor.moveToward(goalDirt, options);
		if (move != null) {
			visited.add(move.edge);
			return move;
		}

		// life without cleaning is meaningless.
		return null;
	}

	public Set<Annotation> annotations() {
		Set<Annotation> ret = new HashSet<Annotation>();
		if (goalDirt != null) { ret.add(new ActorAnnotation(goalDirt, "Goal Dirt")); }
		for(Edge edge : visited) {
			ret.add(new EdgeAnnotation(edge, "Visited Edge"));
		}
		return ret;
	}
	
}