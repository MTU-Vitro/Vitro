package assign.search;

import vitro.*;
import vitro.grid.*;
import vitro.util.*;
import demos.robots.*;
import java.util.*;
import java.awt.Color;


/**
*
**/
public class PathingAgent implements Agent<Robots.BLU>, Annotated {
	
	/**
	* 
	**/
	public enum PathType { 
		BREADTH, DEPTH, UNIFORM_COST, ASTAR_EUCLIDIAN, ASTAR_MANHATTAN; 
	}
	
	/**
	*
	**/
	public final PathType type;
	
	/**
	*
	**/
	public List<Location> pathing    = null;
	
	/**
	*
	**/
	public Map<Location, Integer> expansions = null;
	
	/**
	* Construct the agent, specifying the type of path finding algorithm
	* which it should use.
	*
	* @param type the pathing algorithm to use
	**/
	public PathingAgent(PathType type) {
		this.type = type;
	}

	/**
	* Selects an action to perform for the given actor from the set of
	* options. Attempting to apply an action directly from this method,
	* or to return an action which is not in the applicable (not in the
	* given set of actions) is a breach of this methods contract and
	* will cause an error.
	*
	* @param  actor   the actor for which you are choosing the action.
	* @param  options the set of applicable actions.
	* @return the action to perform.
	**/
	@Override
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
			
			// Select a search method for use based on information
			// specified through the command line.
			Search<Location> method = null;
			switch(type) {
				case BREADTH         : method = new BreadthFirstSearch<Location>();
				                       break;
				case DEPTH           : method = new DepthFirstSearch<Location>();
				                       break;
				case UNIFORM_COST    : method = new UniformCostSearch<Location>();
				                       break;
				case ASTAR_EUCLIDIAN : method = new AStarSearch<Location>(
				                           new EuclidianHeuristic(goal)
				                       );
				                       break;
				case ASTAR_MANHATTAN : method = new AStarSearch<Location>(
				                           new ManhattanHeuristic(goal)
				                       );
				                       break;
				default: throw new Error("Unrecognized search type!");
			}
			
			// Here we verify we recieved a path. This agent
			// is guaranteed to be in a configuration in which
			// a path is possible.
			pathing = method.search(domain);
			
			if(pathing == null) { 
				throw new Error("No path recieved!");
			}
			
			// For the sake of testing and instrumentation, we
			// use the DomainTracker class to track the number
			// of expansions used by the search method.
			expansions = ((DomainTracker<Location>)domain).expandOrder();
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
	
	/**
	* Returns a set of annotations for this agent.
	*
	* @return the set of annotations to request.
	**/
	@Override
	public final Set<Annotation> annotations() {
		Set<Annotation> ret = new HashSet<Annotation>();
		
		// Here we assemble a grid annotation, representing the
		// expansion order of the searching mechanism. The
		// GridAnnotation constructs a gradient between two colors
		// based on the ordering and overlays the data on top of
		// the view.
		/*
		ret.add(new GridAnnotation(
			expansions,
			new Color(0.90f, 0.09f, 0.58f, 0.5f),
			new Color(0.03f, 0.08f, 0.50f, 0.5f)
		));
		*/
		ret.add(new GridAnnotation(
			expansions,
			new Color(1f, 0f, 0f, 0.5f),
			new Color(0f, 1f, 0f, 0.5f),
			new Color(0f, 0f, 1f, 0.5f)
		));

		return ret;
	}
	
	/**
	*
	**/
	@Override
	public String toString() { return "Awesome Pathing Robot"; }
}
