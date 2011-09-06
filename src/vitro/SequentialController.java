package vitro;

import vitro.util.*;
import java.util.*;

/**
* A Controller implementation in which each Agent
* has the opportunity to choose an Action and then
* it is immediately applied. Thus, subsequent
* Agents will observe a different Model state
* within the same round. Agents may move in
* a completely arbitrary order.
*
* @author John Earnest
**/
public class SequentialController extends Controller {

	/**
	* Create a new SequentialController to drive
	* a specified Model.
	*
	* @param model the Model this Controller will drive.
	**/
	public SequentialController(Model model) {
		super(model);
	}

	public List<Action> nextRound() {
		List<Action> actions = new ArrayList<Action>();
		for(Actor a : actors()) {
			Action action = getAction(a);
			if (action != null) {
				action.apply();
				actions.add(action);
			}
		}
		return actions;
	}
}