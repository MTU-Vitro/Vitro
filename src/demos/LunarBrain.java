package demos;

import demos.lunar.*;
import vitro.*;
import vitro.plane.*;
import vitro.util.*;
import java.util.*;

public class LunarBrain implements Agent<Lander.Navigation> {

	public Action choose(Lander.Navigation actor, Set<Action> options) {
		TriggerAction action = (TriggerAction)Groups.firstOfType(TriggerAction.class, options);
		if(action == null) { return null; }

		if(actor.velocity().norm() > 2) {
			action.setMainTrigger(true);
		}
		return action;
	}

}
