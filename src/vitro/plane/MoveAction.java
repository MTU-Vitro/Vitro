package vitro.plane;

import vitro.*;
import vitro.util.*;
import static vitro.util.Groups.*;
import java.util.*;

public class MoveAction extends PlaneAction {

	public final Position start;
	public final Position end;
	public final Actor    actor;

	private boolean collision = false;
	
	public MoveAction(Plane model, Position destination, Actor actor) {
		super(model);
		this.start = model.positions.get(actor);
		this.end   = this.start.translate(collision(actor, start.displace(destination)));
		this.actor = actor;
	}
	
	private Vector2 collision(Actor actor, Vector2 moveVector) {
		if(actor instanceof Collidable) {
			Set<Collidable> collidableActors = new HashSet<Collidable>();
			for(Actor a : model.actors) {
				if(a instanceof Collidable) {
					collidableActors.add((Collidable)a);
				}
			}
			// shouldn't have to forward the model on to the Collision class
			Vector2 newMoveVector = Collision.earliestCollision(model, collidableActors, (Collidable)actor, moveVector);
			if(!newMoveVector.equals(moveVector)) { collision = true; System.out.println("COLLISION"); }
			return newMoveVector;
		}
		return moveVector;
	}

	public boolean collision() { return collision; }

	public void apply() {
		if(!start.equals(model.positions.get(actor))) {
			throw new Error(String.format("Precondition for MoveAction '%s' not satisfied.", this));
		}
		model.positions.put(actor, end);
	}
	
	public void undo() {
		if(!end.equals(model.positions.get(actor))) {
			throw new Error(String.format("Postcondition for MoveAction '%s' not satisfied.", this));
		}
		model.positions.put(actor, start);
	}
	
	@Override
	public int hashCode() {
		return model.hashCode() ^
		       start.hashCode() ^
		         end.hashCode();
	}
	
	@Override
	public boolean equals(Object o) {
		if(!(o instanceof MoveAction)) { return false; }
		MoveAction other = (MoveAction)o;
		return (other.model == this.model) &&
		       (other.start.equals(start)) &&
		       (other.actor.equals(actor)) &&
		       (other.actor.equals(end));
	}

	@Override
	public String toString() {
		return String.format("Move actor '%s' from '%s' to '%s'.", actor, start, end);
	}
}
