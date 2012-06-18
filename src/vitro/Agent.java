package vitro;

import java.io.Serializable;
import java.util.*;

/**
* An Agent makes decisions for one or more Actors.
* The Controller provides agents with an actor to control
* and a set of possible actions it could take.
* The Agent can be thought of as a 'filter' for actions,
* extracting the single action that should take place
* during an actor's turn.
*
* @author John Earnest
**/
public interface Agent<A extends Actor> extends Serializable {

	/**
	* Select an action from the available options.
	*
	* @param actor the Actor for whom a decision is being made.
	* @param options the available Actions for the Actor.
	* @return the Action this Actor will take.
	**/
	public Action choose(A actor, Set<Action> options);

}