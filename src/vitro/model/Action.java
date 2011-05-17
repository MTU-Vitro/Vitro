package vitro.model;

/**
* Actions encapsulate atomic mutations of a Model.
* Once created, the behavior of an Action should
* be deterministic. That is, if an action's outcome
* is decided randomly, this decision should be made
* and stored in the constructor.
*
* @author John Earnest
**/
public interface Action {

	/**
	* Apply this Action's change to a supplied Model or Models.
	**/
	public void apply();

	/**
	* Revert any changes made during apply().
	**/
	public void undo();
}