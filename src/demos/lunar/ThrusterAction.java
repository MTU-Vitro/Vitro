package demos.lunar;

import vitro.*;
import vitro.plane.*;

/**
*
**/
public class ThrusterAction implements Action {
	public final Lander          lander;
	public final Lander.Thruster thruster;

	private boolean     applied        = false;
	private boolean     prevActivated  = false;
	private boolean     nextActivated  = false;
	private ForceAction forceAction    = null;

	/**
	*
	**/
	public ThrusterAction(Lander lander, Lander.Thruster thruster) {
		this.lander   = lander;
		this.thruster = thruster;
	}

	/**
	*
	**/
	public void apply() {
		if(!applied) {
			applied = true;
			prevActivated = thruster.activated;
			nextActivated = thruster.triggered;

			if(nextActivated && lander.fuel >= thruster.fuelCost) {
				forceAction = new ForceAction(lander, thruster.maxThrust);
			}
		}

		thruster.triggered = false;
		thruster.activated = false;

		if(forceAction != null) {
			thruster.activated = nextActivated;
			forceAction.apply();
			lander.fuel -= thruster.fuelCost;
		}
	}

	/**
	*
	**/
	public void undo() {
		thruster.triggered = nextActivated;
		thruster.activated = prevActivated;

		if(forceAction != null) {
			forceAction.undo();
			lander.fuel += thruster.fuelCost;
		}
	}
}
