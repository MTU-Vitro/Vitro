package demos;

import vitro.*;
import vitro.plane.*;
import java.util.*;


public class LunarWorld extends Plane {

	public final LunarLander lander = new LunarLander(this);
	public final Gravitron   planet = new Gravitron(this, new Vector2(0.0, 3.0));
	public final Target      target = new Target(this);
	
	public LunarWorld() {
		actors.add(planet);
	}
	
	public static boolean results = false;
	public boolean done() {
		if(lander.isDead && !results) { 
			System.out.format("Simulation Complete. You have failed!%n");
			results = true;
		}
		if(lander.landed && !results) {
			System.out.format("Simulation Complete. You have succeeded! Your score is: %f%n",
				Math.abs(model.positions.get(lander).x - model.positions.get(target).x) < 20 ? 100.0 : 10.0);
			results = true;
		}
		
		if(lander.isDead || lander.landed) { return true; }
		return false;
	}
	
	public boolean collides(Collidable actor0, Collidable actor1) {
		// We know its only one or the other

		if(lander == actor0 || lander == actor1) {
			//System.out.println("Collision Detected");
			if(lander.velocity.norm() > 10) {
				//System.out.println("The Lander has Crashed!");
				lander.isDead = true;
			}
			else {
				//System.out.println("The Lander has Landed!");
				lander.landed = true;
			}
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
			if(moveAction.collision()) {
				actor.velocity = Vector2.ZERO;
			}
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
			
			if(!lander.isDead && !lander.landed) {
				ret.add(new ForceAction(model, force, lander));
			}
			
			return ret;
		}
		
		public AlignedBox bound() {
			return new AlignedBox(-10000, 460, 10000, 479);
		}
	}
	
	public class Target extends PlaneActor implements Collidable {
	
		public Target(Plane model) {
			super(model);
			model.positions.put(this, new Position(425, 470));
		}
	
		public AlignedBox bound() {
			Position p = model.positions.get(this);
			return new AlignedBox(p.x - 25, p.y - 10, p.x + 25, p.y + 10);
		}
	}
	
	public class LunarLander extends PhysicsActor implements Collidable {
		public final Thruster lThruster = new Thruster(new Vector2( 5.0,   0.0), 1);
		public final Thruster rThruster = new Thruster(new Vector2(-5.0,   0.0), 1);
		public final Thruster mThruster = new Thruster(new Vector2( 0.0, -10.0), 1);
		
		public boolean isDead = false;
		public boolean landed = false;
		
		public int     fuel   = 100;
	
		public LunarLander(Plane model) {
			super(model, 1.0);
		}
		
		public Set<Action> actions() {
			Set<Action> ret = new HashSet<Action>();
			
			if(!isDead && !landed) {
				//if(velocity.x < 4) ret.add(new ThrusterAction(model, true , false, false, this));
				//if(velocity.y > 5) ret.add(new ThrusterAction(model, false, false, true , this));
			
				
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
		
		public class Thruster {
			private static final double timeDiff = 0.1;
			private double time = 0.0;
			
			public final Vector2 maxThrust;
			public final int     fuelCost;
			
			public Thruster(Vector2 maxThrust, int fuelCost) {
				this.maxThrust = maxThrust;
				this.fuelCost  = fuelCost;
			}
			
			public Vector2 fire(boolean fired) {
				if(!fired || fuel < fuelCost) { time = 0.0; return Vector2.ZERO; }
				
				time += timeDiff;
				return maxThrust.mul(Math.atan(10 * time + 1) / (Math.PI / 2.0));
			}
		}
	}
	
	public class ThrusterAction extends PlaneAction {
		public final boolean lThrusterFired;
		public final boolean rThrusterFired;
		public final boolean mThrusterFired;
		public final int     fuelCost;
		
		public final LunarLander lander;
		public final ForceAction forceAction;
		
		public ThrusterAction(Plane model, boolean lThrust, boolean rThrust, boolean mThrust, LunarLander lander) {
			super(model);
			
			this.lThrusterFired = lThrust;
			this.rThrusterFired = rThrust;
			this.mThrusterFired = mThrust;
			
			int     fuelDiff = 0;
			Vector2 force    = Vector2.ZERO;
			
			if(lThrust) { 
				force = force.add(lander.lThruster.fire(lThrust));
				fuelDiff += lander.lThruster.fuelCost;
			}
			if(rThrust) {
				force = force.add(lander.rThruster.fire(rThrust));
				fuelDiff += lander.rThruster.fuelCost;
			}
			if(mThrust) { 
				force = force.add(lander.mThruster.fire(mThrust));
				fuelDiff += lander.mThruster.fuelCost;
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
}
