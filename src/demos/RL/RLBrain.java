package demos.RL;

import vitro.*;
import vitro.grid.*;
import vitro.util.*;
import java.util.*;
import java.awt.Color;
import static vitro.util.Groups.*;

public class RLBrain implements Agent<RLActor>, Persistent, Annotated {

	private Map<Location, Map<Action, Double>> qtable;
	private       double alpha;
	private final double gamma;
	
	private State previous;
	
	/**
	*
	**/
	public RLBrain() {
		qtable = new HashMap<Location, Map<Action, Double>>();
		alpha  = 1.0;
		gamma  = 0.9;
		
		previous = null;
	}
	
	/**
	*
	**/
	public Action choose(RLActor actor, Set<Action> options) {
		generate(actor, options);
		update  (actor);
		
		if(Math.random() < 0.9) {
			previous = new State(
				actor.location(), 
				decide(actor, options), 
				actor.reward
			);
		}
		else {
			previous = new State(
				actor.location(), 
				any(options), 
				actor.reward
			);
		}
		return previous.action;
	}
	
	//
	private void generate(RLActor actor, Set<Action> options) {
		if(!qtable.keySet().contains(actor.location())) {
			Map<Action, Double> actions = new HashMap<Action, Double>();
			qtable.put(actor.location(), actions);
			
			for(Action action : options) {
				actions.put(action, 1. / options.size());
			}
		}
	}
	
	//
	private void update(RLActor actor) {
		if(previous != null) {
			double reward = actor.reward - previous.reward;
			double oldq   = qtable.get(previous.location).get(previous.action);
			double maxq   = Collections.max(qtable.get(actor.location()).values());
			double newq   = oldq + alpha * (reward + gamma * maxq - oldq);
			
			qtable.get(previous.location).put(previous.action, newq);
			alpha = alpha * 0.99;
		}
	}
	
	//
	private Action decide(RLActor actor, Set<Action> options) {
		Map<Action, Double> actions = qtable.get(actor.location());
		Action best = first(options);
		
		for(Action action : options) {
			if(actions.get(best) < actions.get(action)) {
				best = action;
			}
		}
		
		return best;
	}
	
	public Set<Annotation> annotations() {
		Set<Annotation> annotations = new HashSet<Annotation>();
	
		//
		Map<Location, Double> visited = new HashMap<Location, Double>();
		for(Location location : qtable.keySet()) {
			Map<Action, Double> actions = qtable.get(location);
			Action best = first(actions.keySet());
			
			for(Action action : actions.keySet()) {
				if(actions.get(best) < actions.get(action)) {
					best = action;
				}
			}
			
			if(best instanceof RLMove) {
				RLMove move = (RLMove)best;
				
				Location src = move.start;
				Location dst = move.end;
				if (src.x     == dst.x && src.y - 1 == dst.y) { visited.put(location, 0.00); }
				if (src.x + 1 == dst.x && src.y     == dst.y) { visited.put(location, 0.25); }
				if (src.x     == dst.x && src.y + 1 == dst.y) { visited.put(location, 0.50); }
				if (src.x - 1 == dst.x && src.y     == dst.y) { visited.put(location, 0.75); }
			}
		}
		
		annotations.add(new VectorAnnotation(visited));
		//return Collections.singleton((Annotation)new GridLabelAnnotation(visited));
		return annotations;
	}
	
	private static class State {
		public final Location location;
		public final Action   action;
		public final double   reward;
		
		public State(Location location, Action action, double reward) {
			this.location = location;
			this.action   = action;
			this.reward   = reward;
		}
	}

	public Object freeze(Model m) {
		//System.out.println("Final Reward: " + ((RLModel)m).actor().reward);
		return new Thermos(qtable, alpha);
	}

	public void thaw(Object o) {
		Thermos thermos = (Thermos)o;
		qtable = thermos.qtable;
		alpha  = thermos.alpha;
	}
	
	private static class Thermos {
		public final Map<Location, Map<Action, Double>> qtable;
		public final double alpha;
	
		public Thermos(Map<Location, Map<Action, Double>> qtable, double alpha) {
			this.qtable = qtable;
			this.alpha  = alpha;
		}
	}
}