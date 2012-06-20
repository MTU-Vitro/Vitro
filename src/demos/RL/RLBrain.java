package demos.RL;

import vitro.*;
import vitro.grid.*;
import vitro.util.*;
import java.util.*;
import java.awt.Color;
import static vitro.util.Groups.*;

public class RLBrain implements Agent<RLActor>, Persistent, Annotated {
	
	public Action choose(RLActor actor, Set<Action> options) {
		visited.put(actor.location(), (int)(Math.random() * 4) * .25);
		return any(options);
	}

	private Map<Location, Double> visited = new HashMap<Location, Double>();

	public Set<Annotation> annotations() {
		return Collections.singleton((Annotation)new VectorAnnotation(visited));
		//return Collections.singleton((Annotation)new GridLabelAnnotation(visited));
	}

	//private int counter = 0;

	public Object freeze(Model m) {
		//System.out.println("Frozen: " + (counter + 1));
		//return counter + 1;
		System.out.println("Final Reward: " + ((RLModel)m).actor().reward);
		return visited;
	}

	public void thaw(Object o) {
		//System.out.println("Thawed: " + o);
		//counter = (Integer)o;
		visited = (Map<Location, Double>)o;
	}
}