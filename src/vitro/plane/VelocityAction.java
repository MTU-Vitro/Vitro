package vitro.plane;

import vitro.*;

public class VelocityAction extends PlaneAction {
	public final PhysicsActor actor;

	private boolean  applied      = false;
	private Position prevPosition = null;
	private Position nextPosition = null;
	private Action   response     = null;

	public VelocityAction(Plane model, PhysicsActor actor) {
		super(model);
		this.actor = actor;
	}

	public void apply() {
		if(!applied) {
			applied = true;
			
			prevPosition = model.positions.get(actor);
			if(actor instanceof Collidable) {
				Collision collision = Collision.collision(model, (Collidable)actor, actor.velocity);
				if(collision.intercepted != null) {
					response = ((Collidable)actor).collision(collision.intercepted);
				}
				nextPosition = prevPosition.translate(collision.intercept);
			}
			else {
				nextPosition = prevPosition.translate(actor.velocity);
			}
		}

		if(!model.positions.get(actor).equals(prevPosition)) {
			throw new Error();
		}
		if(response != null) { response.apply(); }
		model.positions.put(actor, nextPosition);
	}

	public void undo() {
		if(!model.positions.get(actor).equals(nextPosition)) {
			throw new Error();
		}
		model.positions.put(actor, prevPosition);
		if(response != null) { response.undo(); }
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
