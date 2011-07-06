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
	public Action collision(Collidable obstacle) {
		State newState = State.CRASHED;
		if(obstacle instanceof LandingPad) {
			if(this.velocity.norm() < 10 && ((LandingPad)obstacle).correct(this)) {
				newState = State.LANDED;
			}
		}

		List<Action> composite = new LinkedList<Action>();
		composite.add(new ForceAction(this, this.velocity.mul(-this.mass)));
		composite.add(new ChangeStateAction(newState));
		return new CompositeAction(composite);
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

		public Set<Action> actions() {
			Set<Action> ret = new HashSet<Action>();
			if(state == State.IN_FLIGHT) { ret.add(new TriggerAction(lander())); }
			return ret;
		}
	}
}
