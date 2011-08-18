package demos.lunar;

import vitro.*;
import vitro.plane.*;
import vitro.util.*;
import java.util.*;

public class LunarBrain implements Agent<Lander.Navigation> {
	public final boolean idiot;
	
	public LunarBrain() {
		this.idiot = true;
	}
	
	public LunarBrain(boolean idiot) {
		this.idiot = idiot;
	}

	public Action choose(Lander.Navigation actor, Set<Action> options) {
		TriggerAction action = (TriggerAction)Groups.firstOfType(TriggerAction.class, options);
		if(action == null) { return null; }


		if(idiot) {
			boolean lFired = false;
			boolean rFired = false;

			if(actor.velocity().x <  5 && actor.position().x < actor.target().x - 30) {
				action.setLeftTrigger(true);
				lFired = true;
			}
		
			if(actor.velocity().x > -5 && actor.position().x > actor.target().x + 30) {
				action.setRightTrigger(true);
				rFired = true;
			}
		
			if(Math.abs(actor.velocity().y) > Math.max(Math.abs(actor.position().y - actor.target().y) / 40, 3)) {
				action.setMainTrigger(true);
			}
		}
		else {
			boolean lFired = false;
			boolean rFired = false;

			if(actor.velocity().x <  5 && actor.position().x < actor.target().x - 50) {
				action.setLeftTrigger(true);
				lFired = true;
			}
			else if(actor.velocity().x < 1 && Math.abs(actor.position().x - actor.target().x) < 15) {
				action.setLeftTrigger(true);
				lFired = true;
			}
		
			if(actor.velocity().x > -5 && actor.position().x > actor.target().x + 50) {
				action.setRightTrigger(true);
				rFired = true;
			}
			else if(actor.velocity().x > -1 && Math.abs(actor.position().x - actor.target().x) < 15) {
				action.setRightTrigger(true);
				rFired = true;
			}
		
			if(lFired && rFired) {
				action.setLeftTrigger(false);
				action.setRightTrigger(false);
			}
		
			if(Math.abs(actor.velocity().y) > Math.max(Math.abs(actor.position().y - actor.target().y) / 20, 3)) {
				action.setMainTrigger(true);
			}
		}
		return action;
	}

}
