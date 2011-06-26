package demos;

import vitro.*;
import vitro.plane.*;
import java.util.*;


public class BoidWorld extends Plane {

	public BoidWorld(double width, double height) {
		super(width, height);
	}
	
	public boolean done() {
		return false;
	}
	
	
	public class Boid extends PlaneActor {
		
		public Boid(Plane model) {
			super(model);
		}
		
		public Set<Boid> flock() {
			Set<Boid> ret = new HashSet<Boid>();
			for(Actor actor : model.frames.keySet()) {
				if(actor instanceof Boid) {
					ret.add((Boid)actor);
				}
			}
			return ret;
		}
		
		// here we just set it to what it should be... no agent needed!
		public Set<Action> actions() {
			Set<Action> ret = new HashSet<Action>(); // Forget anything else... I'm a boid!
			// here we compute the new location (position + heading)
//			Location newLocation = location();
//			ret.add(new MoveAction(model, newLocation, this);
			return ret;
		}
	}
}
