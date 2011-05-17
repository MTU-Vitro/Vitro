import vitro.util.*;
import vitro.model.*;
import vitro.model.graph.*;
import vitro.controller.*;
import java.util.*;

public class VacBrain implements Agent<VacWorld.Scrubby> {

	private final Queue<Graph.Edge> path = new LinkedList<Graph.Edge>();

	public Action choose(VacWorld.Scrubby actor, Set<Action> options) {
		
		// if there's stuff to clean, clean it!
		Action clean = Groups.firstOfType(DestroyAction.class, options);
		if (clean != null) { return clean; }

		// follow a path I've decided to walk:
		if (path.size() > 0) { return walkPath(actor, options); }

		// otherwise, find stuff to clean!
		Actor dirt = Groups.firstOfType(VacWorld.Dirt.class, actor.location().reachableActors());
		path.addAll(actor.location().path(dirt));

		return walkPath(actor, options);
	}

	private MoveAction walkPath(VacWorld.Scrubby actor, Set<Action> options) {
		MoveAction move = actor.move(path.remove(), options);
		if (move == null) { throw new Error("I can't find a path- someone *LIED* to me!!!"); }
		return move;
	}
}