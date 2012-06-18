package vitro;
import java.io.Serializable;

/**
* Actions encapsulate atomic mutations of a Model.
* Once created, the behavior of an Action should
* be deterministic. That is, if an action's outcome
* is decided randomly, this decision should be made
* and stored in the constructor.
*
* @author John Earnest
**/
public interface Action extends Serializable {

	/**
	* Apply this Action's change to a supplied Model or Models.
	**/
	public void apply();

	/**
	* Revert any changes made during apply().
	**/
	public void undo();
}