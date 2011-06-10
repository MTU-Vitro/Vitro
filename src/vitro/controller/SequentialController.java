package vitro;

import vitro.util.*;
import java.util.*;

public class SequentialController extends Controller {

	public SequentialController(Model model) {
		super(model);
	}

	public List<Action> nextRound() {
		List<Actor> actors = new ArrayList<Actor>(model.actors);
		List<Action> actions = new ArrayList<Action>();

		for(Actor a : actors) {
			Action action = getAction(a);
			if (action != null) {
				action.apply();
				actions.add(action);
			}
		}
		return actions;
	}
}