package demos.vacuum;

import vitro.*;
import vitro.graph.*;
import vitro.util.*;
import java.util.*;
import static vitro.util.Groups.*;

public class VacBrain implements Agent<VacWorld.Scrubby>, Annotated {

	private Actor goalDirt = null;
	private Set<Edge> visited = new HashSet<Edge>();
	private Set<Edge> notated = new HashSet<Edge>();
	
	public Action choose(VacWorld.Scrubby actor, Set<Action> options) {

		// if there's stuff to clean, clean it!
		DestroyAction clean = actor.destroy(VacWorld.Dirt.class, options);
		//System.out.println(clean);
		if (clean != null) {
			visited.addAll(notated);
			notated.clear();
			//System.out.println();
			return clean;
		}

		// otherwise, find stuff to clean!
		goalDirt = firstOfType(VacWorld.Dirt.class, actor.location().reachableActors());
		MoveAction move = actor.moveToward(goalDirt, options);
		
		//System.out.println(goalDirt);
		//System.out.println(move);
		
		if (move != null) {
			//visited.add(move.edge);
			notated.add(move.edge);
			//System.out.println();
			return move;
		}

		// life without cleaning is meaningless.
		return null;
	}

	public Set<Annotation> annotations() {
		Set<Annotation> ret = new HashSet<Annotation>();
		
		if (goalDirt != null) { ret.add(new ActorAnnotation(goalDirt, "Goal Dirt")); }
		for(Edge edge : notated) {
			ret.add(new EdgeAnnotation(edge, "Current Path"));
		}
		
		
		//if (goalDirt != null) { ret.add(new ActorAnnotation(goalDirt, "Goal Dirt")); }
		for(Edge edge : visited) {
			ret.add(new EdgeAnnotation(edge, "Visited Edge"));
		}

		//ret.add(new DataAnnotation(new HashSet<Edge>(visited), "Visited Edges"));
		
		
		return ret;
	}
	
}
