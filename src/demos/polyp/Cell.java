package demos.polyp;

import vitro.*;
import vitro.plane.*;
import java.util.*;

public class Cell extends PhysicsActor {//{implements Collidable {


	public Cell(Plane model) {
		super(model, 1.0);
	}
	
	public Set<Action> actions() {
		List<Action> composite = new LinkedList<Action>();
		composite.add(new SetVelocityAction(this, model.positions.get(this).displace(new Position(320, 240)).normalize().mul(5.0)));
		composite.add(new VelocityAction(model, this));
		
		Set<Action> ret = new HashSet<Action>();
		ret.add(new CompositeAction(composite));
		return ret;
	}
	
	public Action collision(Collidable obstacle) {
		if(obstacle instanceof Cell) {		// what else might it be?
			Cell other = (Cell)obstacle;
			
			//Vector2 aVel = new Vector2(Math.abs(velocity.x), Math.abs(velocity.y));
		}
	
		return new SetVelocityAction(this, Vector2.ZERO);
	}
	
	public Bound bound() {
		Position p = model.positions.get(this);
		return new Circle(p.x, p.y, 10);
	}
	
	
	public class SetVelocityAction implements Action {
		public final PhysicsActor actor;
		public final Vector2      newVelocity;
		
		private Vector2 oldVelocity = null;
		
		public SetVelocityAction(PhysicsActor actor, Vector2 newVelocity) {
			this.actor       = actor;
			this.newVelocity = newVelocity;
		}
		
		public void apply() {
			if(oldVelocity == null) { oldVelocity = actor.velocity; }
			actor.velocity = newVelocity;
		}
		
		public void undo() {
			actor.velocity = oldVelocity;
		}
	}
}
