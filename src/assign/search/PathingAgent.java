package assign.search;

import vitro.*;
import vitro.grid.*;
import vitro.util.*;
import demos.robots.*;
import java.util.*;
import java.awt.Color;


public class PathingAgent implements Agent<Robots.BLU>, Annotated {
	
	public enum PathType { 
		BREADTH, DEPTH, ASTAR_EUCLIDIAN, ASTAR_MANHATTAN; 
	}
	
	public final PathType type;
	
	public List<Location>         pathing    = null;
	public Map<Location, Integer> expansions = null;
	
	public PathingAgent(PathType type) {
		this.type = type;
	}

	public final Action choose(Robots.BLU actor, Set<Action> options) {
		// Here we assume that we will always see the same
		// actor. This will be true of many models, including
		// this one, but not always.

		// On the first call, our state will not be initialized
		// so we initialize it here once and use it on subsequent
		// calls.
		if(pathing == null) {
			// This agent is responsible for pathing our BLU actor from
			// its initial location to a goal state. We grab these locations
			// and create a Domain, which your pathing code will use to
			// perform the search.
			Location initial = actor.location();
			Location goal    = Groups.any(actor.targets());
			
			if(goal == null) { throw new Error("No goal state obtained!"); }
			
			Domain<Location> domain = new DomainBLU(actor, initial, goal);
			domain = new DomainTracker<Location>(domain);
			
			//
			Search<Location> method = null;
			switch(type) {
				case BREADTH         : method = new BreadthFirstSearch<Location>();
				                       break;
				case DEPTH           : method = new DepthFirstSearch<Location>();
				                       break;
				case ASTAR_EUCLIDIAN : //method = new AStarSearch<Location>(new EuclidianHeuristic(domain.goal()));
				                       break;
				case ASTAR_MANHATTAN : //method = new AStarSearch<Location>(new ManhattanHeuristic(domain.goal()));
				                       break;
				default: throw new Error("Unrecognized search type!");
			}
			
			pathing = method.search(domain);
			
			// Here we verify we recieved a path. This agent
			// is guaranteed to be in a configuration in which
			// a path is possible.
			if(pathing == null) { 
				throw new Error("No path recieved!");
			}
			
			pathing.remove(0);
		}
		
		

		// If we have reached the end of our path then there
		// is nothing we can do and so we just wait.
		if(pathing.isEmpty()) {
			return null;
		}
		
		// Finally we have verified post-conditions and can
		// make our move. We obtain the next location in the
		// path and we obtain the action available which will
		// move us there;
		Location next = pathing.remove(0);
		return actor.move(next, options);
	}
	
	public final Set<Annotation> annotations() {
		Set<Annotation> ret = new HashSet<Annotation>();
		// You have a null pointer exception in GridAnnotation!
		// at vitro.grid.GridAnnotation.calculateScaling(GridAnnotation.java:49)
		//ret.add(new GridAnnotation(expansions, Color.WHITE, Color.GRAY));
		return ret;
	}
}
