package demos.lunar;

import vitro.*;
import vitro.plane.*;
import java.util.*;

/**
*
**/
public class Lander extends PhysicsActor implements Collidable {
	public final Navigation navigation = new Navigation();

	public final Thruster mThruster;
	public final Thruster lThruster;
	public final Thruster rThruster;

	public State state = State.IN_FLIGHT;
	public int   fuel  = 1000;

	/**
	*
	**/
	public Lander(Plane model) {
		super(model, 1.0);
		mThruster = new Thruster(new Vector2( 0.0,  3.0), 1);
		lThruster = new Thruster(new Vector2( 1.0,  0.0), 1);
		rThruster = new Thruster(new Vector2(-1.0,  0.0), 1);
	}

	public Lander lander() {
		return this;
	}

	/**
	*
	**/
	public Set<Action> actions() {
		Set<Action> ret = new HashSet<Action>();

		if(state == State.IN_FLIGHT) {
			List<Action> composite = new LinkedList<Action>();
			composite.add(new ThrusterAction(this, mThruster));
			composite.add(new ThrusterAction(this, lThruster));
			composite.add(new ThrusterAction(this, rThruster));
			composite.add(new VelocityAction(model, this));
			ret.add(new CompositeAction(composite));
		}

		return ret;
	}

	/**
	*
	**/
	public Bound bound() {
		Position p = model.positions.get(this);
		return new AlignedBox(p.x - 19, p.y - 15, p.x + 19, p.y + 24);
	}

	/**
	*
	**/	
	public Vector2 collisionVector(Collidable obstacle, Vector2 remaining) {
		if(obstacle instanceof Lander) { return remaining; }
		if(obstacle instanceof LandingPad || obstacle instanceof Gravitron) { return Vector2.ZERO; }
		return Vector2.ZERO;
	}
	
	/**
	*
	**/
	public Action  collisionAction(Collidable obstacle) {
		if(obstacle instanceof Lander) { return null; }
		
		List<Action> composite = new LinkedList<Action>();
		if(obstacle instanceof LandingPad && velocity.norm() < 10 && ((LandingPad)obstacle).correct(this)) {
			composite.add(new ChangeStateAction(State.LANDED));
		}
		else {
			composite.add(new ChangeStateAction(State.CRASHED));
		}
		composite.add(new ForceAction(this, velocity.mul(-mass)));
		return new CompositeAction(composite);
	}

	/**
	*
	**/
	public enum State { IN_FLIGHT, CRASHED, LANDED };

	/**
	*
	**/
	public class ChangeStateAction implements Action {
		public final State oldState;
		public final State newState;

		public ChangeStateAction(State newState) {
			this.oldState = state;
			this.newState = newState;
		}

		public void apply() {
			state = newState;
		}

		public void undo() {
			state = oldState;
		}
	}

	/**
	*
	**/
	public class Thruster {
		public final Vector2 maxThrust;
		public final int fuelCost;

		public boolean triggered = false;
		public boolean activated = false;

		/**
		*
		**/
		public Thruster(Vector2 maxThrust, int fuelCost) {
			this.maxThrust = maxThrust;
			this.fuelCost  = fuelCost;
		}
	}

	/**
	*
	**/
	public class Navigation extends Actor {
		public Vector2 velocity() {
			return velocity;
		}
		
		public Vector2 position() {
			return position();
		}

		public Set<Action> actions() {
			Set<Action> ret = new HashSet<Action>();
			if(state == State.IN_FLIGHT) { ret.add(new TriggerAction(lander())); }
			return ret;
		}
	}
}
