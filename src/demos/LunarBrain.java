package demos;

import vitro.*;
import vitro.plane.*;
import vitro.util.*;
import java.util.*;

public class LunarBrain implements Agent<LunarWorld.LunarLander> {

	public Action choose(LunarWorld.LunarLander actor, Set<Action> options) {
		LunarWorld model = actor.model();
		
		Position landerPos = model.positions.get(model.lander);
		Position targetPos = model.positions.get(model.target);
		
		boolean lThrust = false;
		boolean rThrust = false;
		boolean mThrust = false;
		
		double distTarget = Math.abs(landerPos.x - targetPos.x);
		
		if(model.lander.velocity.y > 5) { mThrust = true; }
		if(landerPos.x < targetPos.x) {
			if(distTarget > 50 && model.lander.velocity.x < 5) {
				lThrust = true;
			}
			if(distTarget < 10 && model.lander.velocity.x > 0) {
				rThrust = true;
			}
		}
		
		for(Action action : options) {
			if(action instanceof LunarWorld.ThrusterAction) {
				LunarWorld.ThrusterAction thrust = (LunarWorld.ThrusterAction)action;
				if(thrust.lThrusterFired == lThrust &&
				   thrust.rThrusterFired == rThrust &&
				   thrust.mThrusterFired == mThrust ) {
					return action;
				}
 			}
		}
		
		return null;
	}
}
