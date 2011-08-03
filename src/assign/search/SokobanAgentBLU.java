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

	private List<SokobanStateBLU> blockPath = null;
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
		if(blockPath == null) {
			// We assume : 1 blu, 1 block, 2 targets
			Location       robot   = actor.location();
			Location       block   = Groups.first(actor.blocks());
			Set<Location> targets = actor.targets();
			
			for(Location target : targets) {
				List<Location> goals = new ArrayList<Location>(targets);
				goals.remove(target);
				
				// First, distribute the two targets between blu and the block.
				Location robotTarget = Groups.first(goals);
				Location blockTarget = target;
				
				// Second, path the block, making sure that you only expand
				// in "pushable" directions.
				Domain<SokobanStateBLU> domain = new SokobanDomainBLU(actor, new SokobanStateBLU(robot, block), new SokobanStateBLU(robot, blockTarget));
				
				Search<SokobanStateBLU> method = new BreadthFirstSearch<SokobanStateBLU>();
				blockPath = method.search(domain);
				if(blockPath == null) { break; }
			}
			
			if(blockPath == null) {
				throw new Error("No path recieved!");
			}
			
			expansions = new HashMap<Location, Integer>();
			int count = 0;
			for(SokobanStateBLU state : blockPath) {
				expansions.put(state.blockLocation, count);
				count++;
			}
			
			expansions2 = new HashMap<Location, Integer>();
			count = 0;
			for(SokobanStateBLU state : blockPath) {
				expansions2.put(state.bluLocation, count);
				count++;
			}
			System.out.println(expansions2);
		}
		
		return null;
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
		ret.add(new GridAnnotation(
			expansions,
			new Color(0.90f, 0.09f, 0.08f, 0.5f),
			new Color(0.53f, 0.08f, 0.00f, 0.5f)
		));
		
		
		ret.add(new GridAnnotation(
			expansions2,
			new Color(0.00f, 0.09f, 0.58f, 0.5f),
			new Color(0.03f, 0.08f, 0.58f, 0.5f)
		));
		
		/*
		ret.add(new GridAnnotation(
			blockPath,
			new Color(0.80f, 0.00f, 0.80f, 0.5f)
		));
		*/

		return ret;
	}
}
