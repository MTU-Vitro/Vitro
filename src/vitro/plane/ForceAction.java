package vitro.plane;

import vitro.*;
import vitro.plane.*;

/**
*
**/
public class ForceAction implements Action {
	public final Vector2      force;
	private Vector2      prevVelocity = null;
	private Vector2      nextVelocity = null;
	public final PhysicsActor actor;

	public ForceAction(PhysicsActor actor, Vector2 force) {
		this.force        = force;
		this.actor        = actor;
	}

	public void apply() {
		if(nextVelocity == null) {
			this.prevVelocity = actor.velocity;
			this.nextVelocity = actor.velocity.add(force.mul(1.0 / actor.mass));
		}
		actor.velocity = nextVelocity;
	}

	public void undo() {
		actor.velocity = prevVelocity;
	}

	@Override
	public int hashCode() {
		return    force.hashCode() ^
		          actor.hashCode();
	}

	@Override
	public boolean equals(Object o) {
		if(!(o instanceof ForceAction)) { return false; }
		ForceAction other = (ForceAction)o;
		return (other.force.equals(force))               &&
		       (other.actor.equals(actor));
	}
}
