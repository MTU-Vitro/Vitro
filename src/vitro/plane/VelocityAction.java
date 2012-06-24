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

	public void apply() {
		if(!applied) {
			applied = true;

			prevPosition = model.positions.get(actor);
			nextPosition = prevPosition;

			Vector2 velocity = actor.velocity;
			if(actor instanceof Collidable) {
				SortedMap<Double, Collidable> intersections = Collision.collision(model, (Collidable)actor, velocity);
				for(Map.Entry<Double, Collidable> entry : intersections.entrySet()) {
					

					Action response = ((Collidable)actor).collisionAction(entry.getValue());
					if(response != null) { responses.add(response); }

					Vector2 partial = velocity.mul(1 - entry.getKey());
					Vector2 reflect = ((Collidable)actor).collisionVector(entry.getValue(), partial);
					if(!partial.equals(reflect)) {
						nextPosition = nextPosition.translate(velocity.mul(entry.getKey()));
						// perform the reflection or whatever.
						velocity = reflect;
						break;
					}
				}
				nextPosition = nextPosition.translate(velocity);
			}
		}

		model.positions.put(actor, nextPosition);
		for(Action response : responses) { response.apply(); }
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
