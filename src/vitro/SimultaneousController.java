package vitro;

import vitro.util.*;
import java.util.*;

public class SimultaneousController extends Controller {

	public SimultaneousController(Model model) {
		super(model);
	}

	public List<Action> nextRound() {
		List<Action> actions = new ArrayList<Action>();
		for(Actor a : actors()) {
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