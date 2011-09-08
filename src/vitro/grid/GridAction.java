package vitro.grid;

import vitro.*;

/**
* A GridAction is a more restrictive type
* of Action that applies specifically to a Grid Model.
*
* @author John Earnest
**/
public abstract class GridAction implements Action {

	/**
	* The Grid to which this Action is applied.
	**/
	protected final Grid model;

	/**
	* Construct a GridAction.
	*
	* @param model the Grid to which this Action is applied.
	**/
	protected GridAction(Grid model) {
		this.model = model;
	}
}