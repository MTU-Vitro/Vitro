package demos;

import vitro.*;
import vitro.plane.*;
import java.util.*;


public class BoidWorld extends Plane {

	public Boid createBoid() { return new Boid(this); }

	public BoidWorld(double width, double height) {
		super(width, height);
	}
	
	public boolean done() {
		return false;
	}
	
	public class Boid extends PlaneActor {

		private double angle = 0.0;

		public Boid(Plane model) {
			super(model);
		}
		
		public Set<Boid> flock() {
			Set<Boid> ret = new HashSet<Boid>();
			for(Actor actor : model.positions.keySet()) {
				if(actor instanceof Boid && positions.get(actor) != null) {
					ret.add((Boid)actor);
				}
			}
			return ret;
		}

		// here we just set it to what it should be... no agent needed!
		public Set<Action> actions() {
			Set<Action> ret = new HashSet<Action>(); // Forget anything else... I'm a boid!

			// only continue if I am in the world
			Position myPos = positions.get(this);
			if(myPos == null) {
				return ret;
			}

			// calculate!
			
			Vector2 centerMass = Vector2.ZERO;
			Vector2 centerHead = Vector2.ZERO;
			Vector2 repulsion  = Vector2.ZERO;

			for(Boid boid : flock()) {
				Position theirPos = positions.get(boid);

				centerMass = centerMass.add(Position.ZERO.displace(theirPos).mul(1.0 / flock().size()));
				centerHead = centerHead.add(new Vector2(Math.cos(boid.angle), Math.sin(boid.angle)).mul(1.0 / flock().size()));

				Vector2 repulse = theirPos.displace(myPos);
				if(0.25 > repulse.normSq() && repulse.normSq() > 0) {
					repulsion = repulsion.add(repulse.normalize().mul(1.0 / repulse.normSq()));
				}
			}
			//repulsion  = repulsion.mul(1.0 / flock().size());

			Vector2 heading = Vector2.ZERO;
			heading = heading.add(myPos.displace(new Position(centerMass)).normalize());
			heading = heading.add(centerHead.normalize().mul(5.0));
			heading = heading.add(repulsion);

			angle = Math.atan2(heading.y, heading.x);

			Position newPos = myPos.translate(heading.normalize().mul(0.1));
			newPos = new Position(Math.min(Math.max(newPos.x, 0.0), width), Math.min(Math.max(newPos.y, 0.0), height));
			ret.add(new MoveAction(model, newPos, this));
			
			
			//ret.add(new MoveAction(model, myPos.translate(0.1, 0.0), this));
			
			return ret;
		}
	}
}