package demos.search;

import vitro.*;
import vitro.grid.*;
import vitro.util.*;
import demos.robots.*;
import java.util.*;
import java.awt.Color;


/**
*
**/
public class SokobanAgentBLU implements Agent<Robots.BLU>, Annotated {

	private List<SokobanStateBLU> states    = null;
	private List<Location>        pathBLU   = null;
	private List<Location>        pathBlock = null;

	private Map<Location, Integer> expansions1 = new HashMap<Location, Integer>();
	private List<Location>         expansions2 = new ArrayList<Location>();

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
		// Here we assume that we will always see the same actor.

		// On the first call, our state will not be initialized.
		if(states == null) {
			// We assume : 1 blue, 1 block, 2 targets.
			Location      bluLocation   = actor.location();
			Location      blockLocation = Groups.first(actor.blocks());
			Set<Location> targets       = actor.targets();
			
			// Consider the combinations for solving the puzzle:
			// BLU + Target 0, Block + Target 1;
			// BLU + Target 1, Block + Target 0.
			for(Location target : targets) {
				List<Location> goals = new ArrayList<Location>(targets);
				goals.remove(target);
				
				// First, distribute the two targets between blue and the block.
				Location bluTarget   = Groups.first(goals);
				Location blockTarget = target;
				
				// Second, path the block, making sure that you only expand 
				// in "pushable" directions.
				Search<SokobanStateBLU> method = new UniformCostSearch<SokobanStateBLU>(
					new CostFunction<SokobanStateBLU>() {
						public double value(SokobanStateBLU state) {
							return state.cost;
						}
					}
				);
				Domain<SokobanStateBLU> domain = new SokobanDomainBLU(
					actor,
					blockLocation,
					new SokobanStateBLU(0.0, bluLocation, blockLocation),
					blockTarget
				);
				states = method.search(domain);
				
				if(states == null) { continue; }
				
				// Third, path BLU from his final location pushing the block
				// to his target.
				Search<Location> bluMethod = new BreadthFirstSearch<Location>();
				Domain<Location> bluDomain = new DomainBLU(actor, actor.location(), bluTarget);
				List<Location> path = bluMethod.search(bluDomain);
				
				if(path == null) { continue; }
				
				// Here we have passed our checks and have found a valid
				// solution!
				break;
			}
			
			// Check to see if no solution has been found.
			if(states == null) { throw new Error("No Solution Found!"); }
			
			// Setup block path, as it is known at this time.
			pathBlock = new ArrayList<Location>();
			for(SokobanStateBLU state : states) {
				pathBlock.add(state.blockLocation);
			}
		}

		// If BLU already has a path, we should attend to that

		// Iterate through the block pushing states, pushing the block or
		// pathing BLU as needed.
		if(states.size() > 1) {
			SokobanStateBLU current = states.get(1);

			// If we have arrived at our intermediate destination, 
			// we can now push!
			if((actor.location()).equals(current.bluLocation)) {
				pathBLU = null;
				for(Action action : Groups.ofType(Robots.PushAction.class, options)) {
					Robots.PushAction push = (Robots.PushAction)action;
					if(push.pusher == actor && push.pushedTo.equals(current.blockLocation)) {
						states.remove(0);
						return push;
					}
				}
			}

			// Otherwise we path to our intermediate destination and follow!
			Search<Location> method = new BreadthFirstSearch<Location>();
			Domain<Location> domain = new DomainBLU(actor, actor.location(), current.bluLocation);
			List<Location> path = method.search(domain);
			
			if(pathBLU == null) { pathBLU = new ArrayList<Location>(path); }
			
			path.remove(0);
			Location next = path.remove(0);
			
			Action action = actor.move(next, options);
			return action;
		}
		else {
			// Path blu to his own target
			Set<Location> targetSet = actor.targets();
			targetSet.remove(Groups.first(actor.blocks()));
			Location target = Groups.first(targetSet);

			Search<Location> method = new BreadthFirstSearch<Location>();
			Domain<Location> domain = new DomainBLU(actor, actor.location(), target);
			List<Location> path = method.search(domain);
			
			if(pathBLU == null) { pathBLU = new ArrayList<Location>(path); }
			
			path.remove(0);
			Location next = path.remove(0);
			
			return actor.move(next, options);
		}
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
		
		ret.add(new GridAnnotation(pathBlock, new Color(0.90f, 0.09f, 0.08f, 0.2f)));
		if(pathBLU != null) {
			ret.add(new GridAnnotation(pathBLU, new Color(0.00f, 0.09f, 0.58f, 0.5f)));
		}
		

		return ret;
	}
}

