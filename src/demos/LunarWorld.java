package demos;

import vitro.*;
import vitro.plane.*;
import java.util.*;


public class LunarWorld extends Plane {

	public final LunarLander lander = new LunarLander(this);
	public final Gravitron   planet = new Gravitron(this, new Vector2(0.0, 6.0));
	
	public LunarWorld() {
		actors.add(planet);
	}
	
	public boolean done() {
		return false;
	}
	
	protected boolean collides(Actor actor0, Actor actor1) {
		// We know its only one or the other
		
		if(actor0 instanceof LunarLander) { 
			((LunarLander)actor0).isDead = true;
			((LunarLander)actor0).velocity = Vector2.ZERO;
		}
		if(actor1 instanceof LunarLander) { 
			((LunarLander)actor1).isDead = true;
			((LunarLander)actor1).velocity = Vector2.ZERO;
		}
		
		return true;
	}
	
	
	/**
	 *
	 **/
	public class PhysicsActor extends PlaneActor {
		public final double mass;
		
		public Vector2 velocity = Vector2.ZERO;
		
		public PhysicsActor(Plane model, double mass) {
			super(model);
			this.mass = mass;
		}
		
		public PhysicsActor(Plane model, double mass, Vector2 initial) {
			this(model, mass);
			this.velocity = initial;
		}
	}
	
	/**
	 *
	 **/
	public class ForceAction extends PlaneAction {
		public final Vector2      force;
		public final PhysicsActor actor;
		
		public final Vector2 initVelocity;
		public final Vector2 endVelocity;
		
		public final MoveAction moveAction;
		
		public ForceAction(Plane model, Vector2 force, PhysicsActor actor) {
			super(model);
			
			this.force = force;
			this.actor = actor;

			this.initVelocity = actor.velocity;
			this.endVelocity  = actor.velocity.add(force.mul(1 / actor.mass).mul(0.1));

			Position newPos = model.positions.get(actor).translate(endVelocity.mul(0.1));
			this.moveAction = new MoveAction(model, newPos, actor);
		}
		
		public void apply() {
			actor.velocity = endVelocity;
			moveAction.apply();
		}
		
		public void undo() {
			actor.velocity = initVelocity;
			moveAction.undo();
		}
		
		@Override
		public int hashCode() {
			return    force.hashCode() ^
			          actor.hashCode() ^
			   initVelocity.hashCode() ^
			    endVelocity.hashCode() ^
			     moveAction.hashCode();
		}
	
		@Override
		public boolean equals(Object o) {
			if(!(o instanceof ForceAction)) { return false; }
			ForceAction other = (ForceAction)o;
			return (other.model == this.model)               &&
			       (other.force.equals(force))               &&
			       (other.actor.equals(actor))               &&
			       (other.initVelocity.equals(initVelocity)) &&
			       (other.endVelocity.equals(endVelocity))   &&
			       (other.moveAction.equals(moveAction));
		}
	}
	
	public class MultiAction implements Action {
		public final Set<Action> actions;
		
		public MultiAction(Set<Action> actions) {
			this.actions = actions;
		}
		
		public void apply() {
			for(Action action : actions) { action.apply(); }
		}
		
		public void undo() {
			for(Action action : actions) { action.undo();  }
		}
		
		@Override
		public int hashCode() {
			return actions.hashCode();
		}
	
		@Override
		public boolean equals(Object o) {
			if(!(o instanceof MultiAction)) { return false; }
			MultiAction other = (MultiAction)o;
			return other.actions.equals(actions);
		}
		
		@Override
		public String toString() {
			return actions.toString();
		}
	}
	
	public class Gravitron extends PlaneActor implements Collidable {
		public final Vector2 force;
	
		public Gravitron(Plane model, Vector2 force) {
			super(model);
			this.force = force;
		}
		
		public Set<Action> actions() {
			Set<Action> ret = super.actions();
			
			if(!lander.isDead) {
				ret.add(new ForceAction(model, force, lander));
			}
			
			return ret;
		}
		
		public AlignedBox bound() {
			return new AlignedBox(-10000, 460, 10000, 479);
		}
	}
	
	public class ThrusterAction extends PlaneAction {
		public final boolean thrusterLeft;
		public final boolean thrusterRight;
		public final boolean thrusterMain;
		public final int     fuelCost;
		
		public final LunarLander lander;
		public final ForceAction forceAction;
		
		public ThrusterAction(Plane model, boolean left, boolean right, boolean main, LunarLander lander) {
			super(model);
			this.thrusterLeft  = left;
			this.thrusterRight = right;
			this.thrusterMain  = main;
			
			Vector2 force = Vector2.ZERO;
			int fuelDiff = 0;
			if(thrusterLeft) { 
				force = force.add(new Vector2( 5.0,   0.0)); 
				fuelDiff++;
			}
			if(thrusterRight) { 
				force = force.add(new Vector2(-5.0,   0.0)); 
				fuelDiff++;
			}
			if(thrusterMain) { 
				force = force.add(new Vector2( 0.0, -10.0));
				fuelDiff++;
			}
			
			this.fuelCost    = fuelDiff;
			this.lander      = lander;
			this.forceAction = new ForceAction(model, force, lander);
		}
		
		public void apply() {
			forceAction.apply();
			lander.fuel -= fuelCost;
		}
		
		public void undo() {
			forceAction.undo();
			lander.fuel += fuelCost;
		}
	}
	
	public class LunarLander extends PhysicsActor implements Collidable {
		public boolean isDead = false;
		public int     fuel   = 200;
	
		public LunarLander(Plane model) {
			super(model, 1.0);
		}
		
		public Set<Action> actions() {
			Set<Action> ret = new HashSet<Action>();
			
			/*
			if(model.positions.get(this).y >= 445) {
				isDead = true;
				velocity = Vector2.ZERO;
			}
			*/
			if(!isDead) {
				ret.add(new ThrusterAction(model, false, false, false, this));
				if(fuel > 0) {
					ret.add(new ThrusterAction(model, true , false, false, this));
					ret.add(new ThrusterAction(model, false, true , false, this));
					ret.add(new ThrusterAction(model, false, false, true , this));
				}
				if(fuel > 1) {
					ret.add(new ThrusterAction(model, true , true , false, this));
					ret.add(new ThrusterAction(model, true , false, true , this));
					ret.add(new ThrusterAction(model, false, true , true , this));
				}
				if(fuel > 2) {
					ret.add(new ThrusterAction(model, true , true , true , this));
				}
			}
			
			return ret;
		}
		
		public AlignedBox bound() {
			Position p = model.positions.get(this);
			return new AlignedBox(p.x - 19, p.y - 26, p.x + 19, p.y + 15);
		}
	}
}
