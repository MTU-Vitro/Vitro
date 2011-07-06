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
				//System.out.println(prevPosition.translate(collision.intercept));
				//System.out.println(">> " + collision.intercept);
				nextPosition = prevPosition.translate(collision.intercept);
				//System.out.println(">> " + nextPosition);
			}
			else {
				nextPosition = prevPosition.translate(actor.velocity);
			}
		}

		if(!model.positions.get(actor).equals(prevPosition)) {
			throw new Error();
		}
		model.positions.put(actor, nextPosition);
		if(actor instanceof Collidable) {
			//System.out.println("++ " + ((AlignedBox)(((Collidable)actor).bound())).point0.y);
		}
		if(response != null) { response.apply(); }
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
