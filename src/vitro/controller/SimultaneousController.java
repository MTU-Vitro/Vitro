package vitro.controller;

import vitro.util.*;
import vitro.model.*;
import java.util.*;

public class SimultaneousController extends Controller {

	public SimultaneousController(Model model) {
		super(model);
	}

	public List<Action> nextRound() {
		List<Action> actions = new ArrayList<Action>();
		for(Actor a : model.actors) {
			Action action = getAction(a);
			if (action != null) {
				actions.add(action);
			}
		}
		for(Action a : actions) {
			a.apply();
		}
		return actions;
	}
}