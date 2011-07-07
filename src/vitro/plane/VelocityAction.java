package vitro.plane;

import vitro.*;
import vitro.util.*;
import java.util.*;

public class VelocityAction extends PlaneAction {
	public final PhysicsActor actor;

	private boolean  applied       = false;
	private Position prevPosition  = null;
	private Position nextPosition  = null;
	private List<Action> responses = new LinkedList<Action>();
	private Action   chained       = null;

	public VelocityAction(Plane model, PhysicsActor actor) {
		super(model);
		this.actor = actor;
	}

	public void apply(Vector2 v, int depth) {
		if(v.normSq() < 0.001 || depth > 10) { return; }
		
		if(actor instanceof Collidable) {
			Collision collision = Collision.collision(model, (Collidable)actor, v);
			if(collision.intercepted != null) {
				nextPosition = model.positions.get(actor).translate(collision.intercept);

				Action response = ((Collidable)actor).collisionAction(collision.intercepted);
				if(response != null) {
					responses.add(response);
					response.apply();
				}
				
				Vector2 vel = ((Collidable)actor).collisionVector(collision.intercepted, (v).sub(collision.intercept));
				model.positions.put(actor, nextPosition);
				apply(vel, depth + 1);
			}
		}
	}

	public void apply() {
		if(applied) {
			for(int x = responses.size() - 1; x >= 0; x++) { responses.get(x).apply(); }
		}
		
		if(!applied) {
			applied = true;
			
			prevPosition = model.positions.get(actor);
			nextPosition = prevPosition.translate(actor.velocity);
			
			apply(actor.velocity, 0);
		}

//		if(!model.positions.get(actor).equals(prevPosition)) {
//			throw new Error();
//		}
		model.positions.put(actor, nextPosition);
	}

	public void undo() {
		if(!model.positions.get(actor).equals(nextPosition)) {
			throw new Error();
		}
		model.positions.put(actor, prevPosition);
		for(int x = responses.size() - 1; x >= 0; x++) { responses.get(x).undo(); }
	}

	@Override
	public int hashCode() {
		return model.hashCode() ^
		       actor.hashCode();
	}

	@Override
	public boolean equals(Object o) {
		if(!(o instanceof VelocityAction)) { return false; }
		VelocityAction other = (VelocityAction)o;
		return (other.model == this.model) &&
		       (other.actor.equals(actor));
	}

	@Override
	public String toString() {
		return String.format("Move actor with velocity '%s' from '%s' to '%s'.", this, actor.velocity, prevPosition, nextPosition);
	}
}
