package vitro;

import vitro.util.*;
import java.util.*;

/**
* A Controller implementation in which every
* Agent has the opportunity to select an Action
* before any are applied. Thus, subsequent
* Agents will all observe the same state within
* a given round.
*
* @author John Earnest
**/
public class SimultaneousController extends Controller {

	/**
	* Create a new SimultaneousController to drive
	* a specified Model.
	*
	* @param model the Model this Controller will drive.
	**/
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