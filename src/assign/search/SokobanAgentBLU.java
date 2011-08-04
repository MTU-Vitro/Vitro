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
public class SokobanAgentBLU implements Agent<Robots.BLU>, Annotated {

	private List<SokobanStateBLU> states = null;
	private Map<Location, Integer> expansions = null;
	private Map<Location, Integer> expansions2 = null;

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
			// We assume : 1 blu, 1 block, 2 targets
			Location       robot   = actor.location();
			Location       block   = Groups.first(actor.blocks());
			Set<Location> targets = actor.targets();

			// Consider the combinations for solving the puzzle:
			// BLU + Target 0, Block + Target 1;
			// BLU + Target 1, Block + Target 0.
			for(Location target : targets) {
				System.out.println("Trying target : " + target);

				List<Location> goals = new ArrayList<Location>(targets);
				goals.remove(target);

				// First, distribute the two targets between blu and the block.
				Location robotTarget = Groups.first(goals);
				Location blockTarget = target;

				// Second, path the block, making sure that you only expand
				// in "pushable" directions.
				Search<SokobanStateBLU> method = new BreadthFirstSearch<SokobanStateBLU>();
				Domain<SokobanStateBLU> domain = new SokobanDomainBLU(
					actor,
					new SokobanStateBLU(robot, block),
					blockTarget
				);

				states = method.search(domain);
				if(states != null) { break; }
			}

			// No solution found.
			if(states == null) { throw new Error("No path recieved!"); }

			expansions = new HashMap<Location, Integer>();
			int count = 0;
			for(SokobanStateBLU state : states) {
				expansions.put(state.blockLocation, count);
				count++;
			}
		}

		if(states.size() == 0) { return null; }

		SokobanStateBLU current = states.get(1);
		System.out.println(current);

		// We have arrived at our intermediate destination, so we now push!
		if((actor.location()).equals(current.bluLocation)) {
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
		System.out.println(current.bluLocation);

		Location next = method.search(domain).get(1);
		Action action = actor.move(next, options);
		return action;
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
			new Color(0.90f, 0.09f, 0.08f, 0.2f),
			new Color(0.53f, 0.08f, 0.00f, 0.2f)
		));
		*/

		/*
		ret.add(new GridAnnotation(
			path,
			new Color(0.00f, 0.09f, 0.58f, 0.5f)
		));
		*/

		return ret;
	}
}
